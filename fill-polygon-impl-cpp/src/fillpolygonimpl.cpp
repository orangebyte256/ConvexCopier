#include "fillpolygonimpl.h"

#include <vector>
#include <thread>

FillPolygonImpl::FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth) {
    this->imagePixels = imagePixels;
    this->patternPixels = patternPixels;
    this->imageWidth = imageWidth;
    this->patternWidth = patternWidth;
}

 std::set<int> FillPolygonImpl::calcCrossingPoints(const std::unordered_set<Line> &crossingSet, int y) {
    std::set<int> crossPoints;
    for (const Line& elem: crossingSet) {
        int x = elem.getXByY(y);
        if (crossPoints.find(x) != crossPoints.end()) {
            crossPoints.erase(x);
        } else {
            crossPoints.insert(x);
        }
    }
    assert (crossPoints.size() % 2 == 0);
    return crossPoints;
}

void FillPolygonImpl::updateCrossingSet(std::unordered_set<Line> &crossingSet,
                                               const UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                               const UnorderedMapOfLinesT &linesPerHorizonBottomPoint, int y) {
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
        updateCrossingSet(crossingSet, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint, y);
        std::set<int> crossPoints = calcCrossingPoints(crossingSet, y);
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

void FillPolygonImpl::fillLinesPerHorizonMaps(int coordsSize, int* coordsArray,
                                              UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                              UnorderedMapOfLinesT &linesPerHorizonBottomPoint) {
    assert (coordsSize % 2 == 0);

    int pointsCount = coordsSize / 2;
    for (int i = 0; i < pointsCount; i++) {
        Point cur(coordsArray + i * 2);
        Point next(coordsArray + ((i + 1) % pointsCount) * 2);
        Line line = Line(cur, next);
        linesPerHorizonUpperPoint[std::max(cur.y, next.y)].push_back(line);
        linesPerHorizonBottomPoint[std::min(cur.y, next.y)].push_back(line);
    }
}

void FillPolygonImpl::setupMaxAndMin(int coordsSize, int* coordsArray, int &maxY, int &minY) {
    assert (coordsSize % 2 == 0);

    minY = INT_MAX;
    maxY = INT_MIN;
    int pointsCount = coordsSize / 2;
    for (int i = 0; i < pointsCount; i++) {
        Point cur(coordsArray + i * 2);
        minY = std::min(minY, cur.y);
        maxY = std::max(maxY, cur.y);
    }
}

void FillPolygonImpl::fillPolygon(int coordsSize, int* coordsArray, int parallelism, int anchorX, int anchorY) {
    UnorderedMapOfLinesT linesPerHorizonUpperPoint;
    UnorderedMapOfLinesT linesPerHorizonBottomPoint;
    std::unordered_set<Line> crossingSet;

    int lastY;
    fillLinesPerHorizonMaps(coordsSize, coordsArray, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint);
    setupMaxAndMin(coordsSize, coordsArray, curY, lastY);

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