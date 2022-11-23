class Point {
    public int x, y;

    Point (int x, int y) {
        this.x = x;
        this.y = y;
    }
}


public class Line {
    Point first, second;
    int A, B, C;

    // TODO think more
    public Line (Point first, Point second) {
        this.first = first.y > second.y ? first : second;
        this.second = first.y > second.y ? second : first;
        A = this.first.y - this.second.y;
        B = this.first.x - this.second.x;
        C = this.first.x * this.second.y - this.second.x * this.first.y;
    }

    public Point findCross(Line other) {
        double x = (double)(this.B * other.C - other.B * this.C) / (double)(this.A * other.B - other.A * this.B);
        double y = (double)(this.C * other.A - other.C * this.A) / (double)(this.A * other.B - other.A * this.B);
        return new Point((int)(x), (int)(y));
    }
}
