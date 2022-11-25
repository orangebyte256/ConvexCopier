package main.java.com.orangebyte256.convexcopier.common;

import java.util.Optional;

public class Line {
    private final Point first, second;
    // Ax + By + C = 0
    private final double A, B, C;
    // TODO override hash
    // TODO think more
    public Line (Point first, Point second) {
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
        int x = (int)((this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B));
        int y = (int)((this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B));
        Point result = new Point(x, y);
        if (this.isPointInsideLineBox(result) && other.isPointInsideLineBox(result)) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public int getYByX(double x) {
        assert B != 0.0;
        return (int)(-(C + A * x) / B);
    }

    public int getXByY(double y) {
        assert A != 0.0;
        return (int)(-(C + B * y) / A);
    }
}
