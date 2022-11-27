package com.orangebyte256.convexcopier.common;

public class Point {
    public int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Point other)) {
            return false;
        }

        return this.x == other.x && this.y == other.y;
    }
    @Override
    public String toString() {
        return x + " " + y;
    }

    public static Point importPoint(String s) {
        String[] parts = s.split(" ");
        assert parts.length == 2;

        return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}