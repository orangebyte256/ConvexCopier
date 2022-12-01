#ifndef FILL_POLYGON_IMPL
#define FILL_POLYGON_IMPL

#include "point.h"
#include "line.h"

#include <unordered_map>
#include <unordered_set>
#include <set>
#include <mutex>

typedef std::unordered_map<int, std::vector<Line> > UnorderedMapOfLinesT;

class FillPolygonImpl {
    int *imagePixels, *patternPixels;
    int imageWidth, patternWidth;
    std::mutex mutex;
    int curY;

    static void updateCrossingSet(std::unordered_set<Line> &crossingSet, const UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                  const UnorderedMapOfLinesT &linesPerHorizonBottomPoint, int y);
    static std::set<int> calcCrossingPoints(const std::unordered_set<Line> &crossingSet, int y);
    static int calcOffsetInImage(int x, int y, int width);
    static void setupMaxAndMin(int coordsSize, int *coordsArray, int &maxY, int &minY);
    static void fillLinesPerHorizonMaps(int coordsSize, int *coordsArray, UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                 UnorderedMapOfLinesT &linesPerHorizonBottomPoint);
    void fillPolygonWorker(std::unordered_set<Line> &crossingSet, const UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                           const UnorderedMapOfLinesT &linesPerHorizonBottomPoint, const Point &anchor, int lastY);
public:
    FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth);
    void fillPolygon(int coordsSize, int *coordsArray, int parallelism, int anchorX, int anchorY);
};


#endif //FILL_POLYGON_IMPL
