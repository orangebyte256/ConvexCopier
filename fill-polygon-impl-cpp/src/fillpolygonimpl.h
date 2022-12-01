#ifndef FILL_POLYGON_IMPL
#define FILL_POLYGON_IMPL

#include "point.h"
#include "line.h"

#include <unordered_map>
#include <unordered_set>
#include <set>
#include <mutex>

// gtest support
#define FRIEND_TEST(test_case_name, test_name)\
friend class test_case_name##_##test_name##_Test

typedef std::unordered_map<int, std::vector<Line> > UnorderedMapOfLinesT;

class FillPolygonImpl {
    int *imagePixels, *patternPixels;
    int imageWidth, patternWidth;
    std::mutex mutex;
    int curY;

    static std::set<int> calcCrossingPoints(const std::unordered_set<Line> &crossingSet, int y);
    static int calcOffsetInImage(int x, int y, int width);
    static void setupMaxAndMin(const std::vector<Point> &points, int &maxY, int &minY);
    static void fillLinesPerHorizonMaps(const std::vector<Point> &points, UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                                        UnorderedMapOfLinesT &linesPerHorizonBottomPoint);
    static std::vector<Point> bareArrayToVecPoints(int coordsSize, const int *coordsArray);
    void fillPolygonWorker(std::unordered_set<Line> &crossingSet, const UnorderedMapOfLinesT &linesPerHorizonUpperPoint,
                           const UnorderedMapOfLinesT &linesPerHorizonBottomPoint, const Point &anchor, int lastY);

    FRIEND_TEST(FillPolygon_testing, bareArrayToVecPoints);
    FRIEND_TEST(FillPolygon_testing, calcOffsetInImage);
    FRIEND_TEST(FillPolygon_testing, setupMaxAndMin);
    FRIEND_TEST(FillPolygon_testing, calcCrossingPoints);
    FRIEND_TEST(FillPolygon_testing, fillLinesPerHorizonMaps);
public:
    FillPolygonImpl(int *imagePixels, int imageWidth, int *patternPixels, int patternWidth);
    void fillPolygon(int coordsSize, int *coordsArray, int parallelism, int anchorX, int anchorY);
};

#endif //FILL_POLYGON_IMPL
