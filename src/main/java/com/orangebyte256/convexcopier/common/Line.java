package com.orangebyte256.convexcopier.common;

import java.util.Optional;

public class Line {
    private final Point first, second;
    // Ax + By + C = 0
    private final double A, B, C;
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

    private Boolean isPointInsideLineBoxStrong(Point point) {
        return (Math.min(this.first.x, this.second.x) < point.x && point.x < Math.max(this.first.x, this.second.x) &&
                Math.min(this.first.y, this.second.y) < point.y && point.y < Math.max(this.first.y, this.second.y));
    }

    private Boolean pointOnLine(Point p) {
        // TODO change it
        return A * p.x + B * p.y + C == 0;
    }

    public Optional<Point> findCrossPoint(Line other) {
        if (pointOnLine(other.first) && pointOnLine(other.second)) {
            if (this.isPointInsideLineBoxStrong(other.first) || this.isPointInsideLineBoxStrong(other.second) ||
                    other.isPointInsideLineBoxStrong(this.first) || other.isPointInsideLineBoxStrong(this.second)) {
                return Optional.empty();
            }
            if (this.isPointInsideLineBox(other.first) && this.isPointInsideLineBox(other.second)) {
                return Optional.empty();
            }
            if (this.isPointInsideLineBox(other.first)) {
                return Optional.of(other.getFirst());
            }
            if (this.isPointInsideLineBox(other.second)) {
                return Optional.of(other.getSecond());
            }
            assert false; // Should not reach here
        }
        int x = (int)((this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B));
        int y = (int)((this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B));
        Point result = new Point(x, y);
        if (this.isPointInsideLineBox(result) && other.isPointInsideLineBox(result)) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public int getYByX(int x) {
        assert B != 0.0;
        return (int)(-(C + A * x) / B);
    }

    public int getXByY(int y) {
        assert A != 0.0;
        return (int)(-(C + B * y) / A);
    }

    // Methods for testing purpose
    protected double getA() {
        return A;
    }

    protected double getB() {
        return B;
    }

    protected double getC() {
        return C;
    }
}
