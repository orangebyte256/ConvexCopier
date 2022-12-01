#include "Line.h"

Line::Line(Point first, Point second) {
    this->first = first;
    this->second = second;

    A = first.y - second.y;
    B = second.x - first.x;
    C = first.x * second.y - second.x * first.y;
}

int Line::getXByY(int y) const {
    assert (A != 0);

    return (int)(-(C + B * y) / A);
}

bool operator==(const Line& lhs, const Line& rhs)
{
    return lhs.first == rhs.first && lhs.second == rhs.second;
}