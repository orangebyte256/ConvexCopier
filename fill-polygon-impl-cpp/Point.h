#ifndef POINT
#define POINT

#include <functional>

struct Point {
	int x, y;

    Point(int x, int y);
    Point(const int *addr);
    Point();
};

bool operator==(const Point& lhs, const Point& rhs);

template<>
struct std::hash<Point> {
    size_t operator()(const Point &point) const {
        return std::hash<int>()(point.x) ^ std::hash<int>()(point.y);
    }
};

#endif