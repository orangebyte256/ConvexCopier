package com.orangebyte256.convexcopier.common;

import java.util.Optional;

public class Line {
    private final Point first, second;
    // Ax + By + C = 0
    private final double A, B, C;
    private static final double FUZZ_FACTOR = 0.0001;

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

    protected Boolean isPointInsideSector(Point point) {
        return (Math.min(this.first.x, this.second.x) <= point.x && point.x <= Math.max(this.first.x, this.second.x) &&
                Math.min(this.first.y, this.second.y) <= point.y && point.y <= Math.max(this.first.y, this.second.y));
    }

    protected Boolean pointOnLine(Point p) {
        return Math.abs(A * p.x + B * p.y + C) < FUZZ_FACTOR;
    }

    public Optional<Point> findCrossPoint(Line other) {
        if (pointOnLine(other.first) && pointOnLine(other.second)) {
            int fits = 0;
            fits += this.isPointInsideSector(other.first) ? 1 : 0;
            fits += this.isPointInsideSector(other.second) ? 1 : 0;
            fits += other.isPointInsideSector(this.first) ? 1 : 0;
            fits += other.isPointInsideSector(this.second) ? 1 : 0;
            if (fits > 2 || fits == 0) {
                return Optional.empty();
            }
            if (this.first.equals(other.first) || this.first.equals(other.second) ||
                    this.second.equals(other.first) || this.second.equals(other.second)) {
                if (this.isPointInsideSector(other.first)) {
                    return Optional.of(other.getFirst());
                }
                if (this.isPointInsideSector(other.second)) {
                    return Optional.of(other.getSecond());
                }
            }
            return Optional.empty();
        }
        int x = (int)((this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B));
        int y = (int)((this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B));
        Point result = new Point(x, y);
        if (this.isPointInsideSector(result) && other.isPointInsideSector(result)) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public Optional<Integer> getYByX(int x) {
        if (B == 0.0) {
            return Optional.empty();
        }

        Point point = new Point(x, (int)(-(C + A * x) / B));
        if (!isPointInsideSector(point)) {
            return Optional.empty();
        }
        return Optional.of(point.y);
    }

    public Optional<Integer> getXByY(int y) {
        if (A == 0.0) {
            return Optional.empty();
        }

        Point point = new Point((int)(-(C + B * y) / A), y);
        if (!isPointInsideSector(point)) {
            return Optional.empty();
        }
        return Optional.of(point.x);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Line other)) {
            return false;
        }

        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
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
