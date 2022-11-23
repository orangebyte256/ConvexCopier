class Point {
    public int x, y;

    Point (int x, int y) {
        this.x = x;
        this.y = y;
    }
}


public class Line {
    Point first, second;
    double A, B, C;
    // TODO override hash
    // TODO think more
    public Line (Point first, Point second) {
        this.first = first.y > second.y ? first : second;
        this.second = first.y > second.y ? second : first;
        A = this.first.y - this.second.y;
        B = this.first.x - this.second.x;
        C = this.first.x * this.second.y - this.second.x * this.first.y;
    }

    public Point findCross(Line other) {
        double x = (this.B * other.C - other.B * this.C) / (this.A * other.B - other.A * this.B);
        double y = (this.C * other.A - other.C * this.A) / (this.A * other.B - other.A * this.B);
        return new Point((int)(x), (int)(y));
    }
}
