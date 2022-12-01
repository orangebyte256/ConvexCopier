#ifndef LINE
#define LINE

#include "point.h"

#include <cassert>
#include <functional>

struct Line {
	const Point first, second;
	const double A, B, C;

	Line(Point _first, Point _second) : first(_first), second(_second),
        A(first.y - second.y), B(second.x - first.x), C(first.x * second.y - second.x * first.y) {};
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