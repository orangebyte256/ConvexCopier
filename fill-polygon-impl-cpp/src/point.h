#ifndef POINT
#define POINT

#include <functional>

struct Point {
	const int x, y;

    Point() : x(0), y(0) {}
    Point(int _x, int _y) : x(_x), y(_y) {}
    explicit Point(const int *addr) : x(*addr), y(*(addr + 1)) {}
};

bool operator==(const Point& lhs, const Point& rhs);

template<>
struct std::hash<Point> {
    size_t operator()(const Point &point) const {
        return std::hash<int>()(point.x) ^ std::hash<int>()(point.y);
    }
};

#endif