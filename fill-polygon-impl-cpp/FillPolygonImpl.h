#ifndef FILL_POLYGON_IMPL
#define FILL_POLYGON_IMPL

#include "Point.h"
#include "Line.h"

#include <unordered_map>
#include <unordered_set>
#include <set>
#include <mutex>

class FillPolygonImpl {
private:
    std::unordered_map<int, std::vector<Line> > linesPerHorizonUpperPoint;
    std::unordered_map<int, std::vector<Line> > linesPerHorizonBottomPoint;
    std::unordered_set<Line> crossingSet;
    int *imagePixels, *patternPixels;
    int imageWidth, patternWidth;
    std::mutex mutex;
    int curY, lastY;
    std::set<int> calcCrossingPoints(int y) const;
    void fillPolygonWorker(int ancorX, int ancorY);
    int calcOffsetInImage(int x, int y, int width) const;
public:
    FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth);
    void fillPolygon(int coordsSize, int *coordsArray, int parallelism, int ancorX, int ancorY);
};


#endif //FILL_POLYGON_IMPL
