#include "fillpolygonimpl.h"

#include <vector>
#include <thread>

FillPolygonImpl::FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth) {
    this->imagePixels = imagePixels;
    this->patternPixels = patternPixels;
    this->imageWidth = imageWidth;
    this->patternWidth = patternWidth;
}

 std::vector<int> FillPolygonImpl::calcCrossingPoints(const std::unordered_set<Line> &crossingSet, int y) {
    std::vector<int> crossPoints(crossingSet.size());
    int i = 0;
    for (const Line& elem: crossingSet) {
        int x = elem.getXByY(y);
        crossPoints[i++] = x;
    }
    std::sort(crossPoints.begin(), crossPoints.end());
    return crossPoints;
}

int FillPolygonImpl::calcOffsetInImage(int x, int y, int width) {
    return y * width + x;
}

void FillPolygonImpl::fillPolygonWorker(std::unordered_set<Line> &crossingSet, const UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                        const UnorderedMapOfLinesT &linesPerHorizonBottomPoint, const Point &anchor, int lastY) {
    while (true) {
        mutex.lock();
        int y = curY--;
        if (y <= lastY) {
            mutex.unlock();
            return;
        }
        if (linesPerHorizonUpperPoint.find(y) != linesPerHorizonUpperPoint.end()) {
            for (const Line &elem: linesPerHorizonUpperPoint.at(y)) {
                crossingSet.insert(elem);
            }
        }
        if (linesPerHorizonBottomPoint.find(y) != linesPerHorizonBottomPoint.end()) {
            for (const Line &elem: linesPerHorizonBottomPoint.at(y)) {
                crossingSet.erase(elem);
            }
        }
        std::vector<int> crossPoints = calcCrossingPoints(crossingSet, y);
        assert (crossPoints.size() % 2 == 0);
        mutex.unlock();

        auto orderedPoints = crossPoints.begin();
        while (orderedPoints != crossPoints.end()) {
            int left = *orderedPoints++;
            int right = *orderedPoints++;
            int length = right - left;
            if (length > 0) {
                std::memcpy(imagePixels + calcOffsetInImage(left + anchor.x, y + anchor.y, imageWidth),
                            patternPixels + calcOffsetInImage(left, y, patternWidth), length * sizeof(int));
            }
        }
    }
}

void FillPolygonImpl::fillLinesPerHorizonMaps(const std::vector<Point> &points,
                                              UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                              UnorderedMapOfLinesT &linesPerHorizonBottomPoint) {
    for (size_t i = 0; i < points.size(); i++) {
        Point cur = points[i];
        Point next = points[(i + 1) % points.size()];
        Line line = Line(cur, next);
        linesPerHorizonUpperPoint[std::max(cur.y, next.y)].push_back(line);
        linesPerHorizonBottomPoint[std::min(cur.y, next.y)].push_back(line);
    }
}

void FillPolygonImpl::setupMaxAndMin(const std::vector<Point> &points, int &maxY, int &minY) {
    maxY = INT_MIN;
    minY = INT_MAX;
    for (const auto &cur : points) {
        minY = std::min(minY, cur.y);
        maxY = std::max(maxY, cur.y);
    }
}

std::vector<Point> FillPolygonImpl::bareArrayToVecPoints(int coordsSize, const int *coordsArray) {
    assert (coordsSize % 2 == 0);

    int pointsCount = coordsSize / 2;
    std::vector<Point> res;
    for (int i = 0; i < pointsCount; i++) {
        res.emplace_back(coordsArray + i * 2);
    }
    return res;
}

void FillPolygonImpl::fillPolygon(int coordsSize, int* coordsArray, int parallelism, int anchorX, int anchorY) {
    UnorderedMapOfLinesT linesPerHorizonUpperPoint;
    UnorderedMapOfLinesT linesPerHorizonBottomPoint;
    std::unordered_set<Line> crossingSet;

    const std::vector<Point> points = bareArrayToVecPoints(coordsSize, coordsArray);
    int lastY;
    fillLinesPerHorizonMaps(points, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint);
    setupMaxAndMin(points, curY, lastY);

    std::vector<std::thread> threads(parallelism);
    Point anchor(anchorX, anchorY);
    for (int i = 0; i < parallelism; i++) {
        threads[i] = std::thread(&FillPolygonImpl::fillPolygonWorker, this, std::ref(crossingSet), linesPerHorizonUpperPoint,
                                 linesPerHorizonBottomPoint, anchor, lastY);
    }
    for (int i = 0; i < parallelism; i++) {
        threads[i].join();
    }
}
