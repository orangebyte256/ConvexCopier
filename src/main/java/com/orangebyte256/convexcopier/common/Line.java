package com.orangebyte256.convexcopier.common;

import java.util.Optional;

public class Line {
    private final Point first, second;
    // Ax + By + C = 0
    private final int A, B, C;
    // TODO override hash
    // TODO think more
    public Line (Point first, Point second) {
        assert first.x != second.x || first.y != second.y;

        this.first = first;
        this.second = second;
        A = first.y - second.y;
        B = second.x - first.x;
        C = first.x * second.y - second.x * first.y;
    }

    public Point getFirst() {
        return new Point(first);
    }

    public Point getSecond() {
        return new Point(second);
    }

    private Boolean isPointInsideLineBox(Point point) {
        return (Math.min(this.first.x, this.second.x) <= point.x && point.x <= Math.max(this.first.x, this.second.x) &&
                Math.min(this.first.y, this.second.y) <= point.y && point.y <= Math.max(this.first.y, this.second.y));
    }

    public Optional<Point> findCross(Line other) {
        int x = ((this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B));
        int y = ((this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B));
        Point result = new Point(x, y);
        if (this.isPointInsideLineBox(result) && other.isPointInsideLineBox(result)) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public int getYByX(int x) {
        assert B != 0.0;
        return (-(C + A * x) / B);
    }

    public int getXByY(int y) {
        assert A != 0.0;
        return (-(C + B * y) / A);
    }

    public int length() {
        return (int)Math.sqrt(Math.pow(first.x - second.x, 2.0) + Math.pow(first.y - second.y, 2.0));
    }

    // Methods for testing purpose
    protected int getA() {
        return A;
    }

    protected int getB() {
        return B;
    }

    protected int getC() {
        return C;
    }
}
