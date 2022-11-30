#include "FillPolygonImpl.h"

#include <set>
#include <vector>
#include <thread>

FillPolygonImpl::FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth) {
    this->imagePixels = imagePixels;
    this->patternPixels = patternPixels;
    this->imageWidth = imageWidth;
    this->patternWidth = patternWidth;
}

std::set<int> FillPolygonImpl::calcCrossingPoints(int y) const {
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

int FillPolygonImpl::calcOffsetInImage(int x, int y, int width) const {
    return y * width + x;
}

void FillPolygonImpl::fillPolygonWorker(int ancorX, int ancorY) {
    while (true) {
        mutex.lock();
        int y = curY--;
        if (y <= lastY) {
            mutex.unlock();
            return;
        }
        if (linesPerHorizonUpperPoint.find(y) != linesPerHorizonUpperPoint.end()) {
            for (const Line &elem: linesPerHorizonUpperPoint[y]) {
                crossingSet.insert(elem);
            }
        }
        if (linesPerHorizonBottomPoint.find(y) != linesPerHorizonBottomPoint.end()) {
            for (const Line &elem: linesPerHorizonBottomPoint[y]) {
                crossingSet.erase(elem);
            }
        }
        std::set<int> crossPoints = calcCrossingPoints(y);
        mutex.unlock();

        auto orderedPoints = crossPoints.begin();
        while (orderedPoints != crossPoints.end()) {
            int left = *orderedPoints++;
            int right = *orderedPoints++;
            int length = right - left;
            if (length > 0) {
                std::memcpy(imagePixels + calcOffsetInImage(left + ancorX, y + ancorY, imageWidth),
                            patternPixels + calcOffsetInImage(left, y, patternWidth), length * sizeof(int));
            }
        }
    }
}

void FillPolygonImpl::fillPolygon(int coordsSize, int* coordsArray, int parallelism, int ancorX, int ancorY) {
    int minY = INT_MAX, maxY = INT_MIN;
    int pointsCount = coordsSize / 2;
    for (int i = 0; i < pointsCount; i++) {
        Point cur(coordsArray + i * 2);
        Point next(coordsArray + ((i + 1) % pointsCount) * 2);
        minY = std::min(minY, cur.y);
        maxY = std::max(maxY, cur.y);
        Point upper = cur;
        Point bottom = next;
        if (upper.y < bottom.y) {
            std::swap(upper, bottom);
        }
        Line line = Line(upper, bottom);
        linesPerHorizonUpperPoint[upper.y].push_back(line);
        linesPerHorizonBottomPoint[bottom.y].push_back(line);
    }
    curY = maxY;
    lastY = minY;

    std::thread threads[parallelism];
    for (int i = 0; i < parallelism; i++) {
        threads[i] = std::thread(&FillPolygonImpl::fillPolygonWorker, this, ancorX, ancorY);
    }
    for (int i = 0; i < parallelism; i++) {
        threads[i].join();
    }
}