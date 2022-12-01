#ifndef LINE
#define LINE

#include "Point.h"

#include <cassert>
#include <functional>

struct Line {
	Point first, second;
	double A, B, C;

public:
	Line(Point first, Point second);
	int getXByY(int y) const;
};

bool operator==(const Line& lhs, const Line& rhs);

template<>
struct std::hash<Line> {
    size_t operator()(const Line &line) const {
        return std::hash<Point>()(line.first) ^ std::hash<Point>()(line.second);
    }
};

#endif