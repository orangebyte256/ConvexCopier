#include "line.h"

int Line::getXByY(int y) const {
    assert (A != 0);

    return (int)(-(C + B * y) / A);
}

bool operator==(const Line& lhs, const Line& rhs) {
    return lhs.first == rhs.first && lhs.second == rhs.second;
}