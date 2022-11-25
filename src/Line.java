import java.io.Serializable;

class Point implements Serializable {
    public int x, y;

    Point (int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }
}


public class Line {
    Point first, second;
    // Ax + By = C
    double A, B, C;
    // TODO override hash
    // TODO think more
    public Line (Point first, Point second) {
        this.first = first;
        this.second = second;
        A = first.y - second.y;
        B = second.x - first.x;
        C = first.x * second.y - second.x * first.y;
    }
    public Point findCross(Line other) {
        double x = (this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B);
        double y = (this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B);
        return new Point((int)(x), (int)(y));
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
