#include <gtest/gtest.h>

#include "point.h"
#include "line.h"
#include "fillpolygonimpl.h"

#include <vector>
#include <set>

const double FUZZ_FACTOR = 0.0001;

TEST(Point_testing, BasicAssertions) {
    EXPECT_EQ(Point(1,1), Point(1,1));
}

TEST(Line_testing, BasicAssertions) {
    EXPECT_EQ(Line(Point(1,1), Point(2,2)), Line(Point(1,1), Point(2,2)));
    Line l1(Point(0,0), Point(1,1));
    EXPECT_NEAR(l1.A, -1.0, FUZZ_FACTOR);
    EXPECT_NEAR(l1.B, 1.0, FUZZ_FACTOR);
    EXPECT_NEAR(l1.C, 0.0, FUZZ_FACTOR);
    Line l2(Point(1,5), Point(3,9));
    EXPECT_NEAR(l2.A, -4.0, FUZZ_FACTOR);
    EXPECT_NEAR(l2.B, 2.0, FUZZ_FACTOR);
    EXPECT_NEAR(l2.C, -6.0, FUZZ_FACTOR);
}

TEST(FillPolygon_testing, bareArrayToVecPoints) {
    int test[6] = {0, 1,2,3,4,5};
    std::vector<Point> res{Point(0,1), Point(2,3), Point(4,5)};
    EXPECT_EQ(FillPolygonImpl::bareArrayToVecPoints(6, test), res);
}

TEST(FillPolygon_testing, calcOffsetInImage) {
    EXPECT_EQ(FillPolygonImpl::calcOffsetInImage(6, 7, 10), 76);
}

TEST(FillPolygon_testing, setupMaxAndMin) {
    std::vector<Point> points{Point(0,5), Point(20,5), Point(20,15), Point(0,15)};
    int min,max;
    FillPolygonImpl::setupMaxAndMin(points, max, min);
    EXPECT_EQ(max, 15);
    EXPECT_EQ(min, 5);
}

TEST(FillPolygon_testing, calcCrossingPoints) {
    std::unordered_set<Line> lines;
    lines.insert(Line(Point(0,0), Point(10,10)));
    lines.insert(Line(Point(10,10), Point(20,0)));
    lines.insert(Line(Point(0,0), Point(20,10)));

    std::vector<int> answer_0({0, 0, 20});
    std::vector<int> answer_5({5, 10, 15});
    std::vector<int> answer_10({10, 10, 20});

    EXPECT_EQ(FillPolygonImpl::calcCrossingPoints(lines, 0), answer_0);
    EXPECT_EQ(FillPolygonImpl::calcCrossingPoints(lines, 5), answer_5);
    EXPECT_EQ(FillPolygonImpl::calcCrossingPoints(lines, 10), answer_10);
}

TEST(FillPolygon_testing, fillLinesPerHorizonMaps) {
    std::vector<Point> points {Point(25,0), Point(0,25), Point(25,50), Point(50,25)};

    UnorderedMapOfLinesT linesPerHorizonUpperPoint;
    UnorderedMapOfLinesT linesPerHorizonBottomPoint;

    FillPolygonImpl::fillLinesPerHorizonMaps(points, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint);

    Line l1 = Line(Point(25,0), Point(0,25));
    Line l2 = Line(Point(0,25), Point(25,50));
    Line l3 = Line(Point(25,50), Point(50,25));
    Line l4 = Line(Point(50,25), Point(25,0));

    std::vector<Line> answer_25_upper({l1,l4});
    std::vector<Line> answer_50_upper({l2,l3});
    std::vector<Line> answer_25_bottom({l2,l3});
    std::vector<Line> answer_0_bottom({l1,l4});


    EXPECT_EQ(linesPerHorizonUpperPoint.at(25), answer_25_upper);
    EXPECT_EQ(linesPerHorizonUpperPoint.at(50), answer_50_upper);
    EXPECT_EQ(linesPerHorizonBottomPoint.at(25), answer_25_bottom);
    EXPECT_EQ(linesPerHorizonBottomPoint.at(0), answer_0_bottom);
}
