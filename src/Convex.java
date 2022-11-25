import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Convex implements Serializable {
    final List<Point> points;
    final private Point enclosingMinPoint;
    final private Point enclosingMaxPoint;

    public Convex(Point... points) {
        this(Arrays.asList(points));
    }

    public Convex(List<Point> points) {
        this.points = points;
        enclosingMinPoint = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        enclosingMaxPoint = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        points.forEach(point -> {
            enclosingMinPoint.x = Math.min(enclosingMinPoint.x, point.x);
            enclosingMinPoint.y = Math.min(enclosingMinPoint.y, point.y);
            enclosingMaxPoint.x = Math.max(enclosingMaxPoint.x, point.x);
            enclosingMaxPoint.y = Math.max(enclosingMaxPoint.y, point.y);
        });
    }

    public Point enclosingMaxPoint() {
        return new Point(enclosingMaxPoint);
    }

    public Point enclosingMinPoint() {
        return new Point(enclosingMinPoint);
    }
    public ArrayList<Line> getLines() {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point cur = points.get(i);
            Point next = points.get((i + 1) % points.size());
            lines.add(new Line(cur, next));
        }
        return lines;
    }

    public void export(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.err.println("Export of file with vertex was failed");
            throw new RuntimeException(e);
        }
    }

    static Convex importConvex(String path) {
        try {
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(file);
            Convex res = (Convex) in.readObject();
            in.close();
            file.close();
            return res;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Import of file with vertex was failed");
            throw new RuntimeException(e);
        }
    }
}
