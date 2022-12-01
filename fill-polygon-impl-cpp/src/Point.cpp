#include "Point.h"

Point::Point(int x, int y) {
    this->x = x;
    this->y = y;
}

Point::Point(const int *addr) {
    this->x = *addr;
    this->y = *(addr + 1);
}

Point::Point() {
    x = 0;
    y = 0;
}

bool operator==(const Point& lhs, const Point& rhs)
{
    return lhs.x == rhs.x && lhs.y == rhs.y;
}