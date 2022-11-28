package com.orangebyte256.convexcopier.common;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Convex {
    final List<Point> points;
    final private Point enclosingMinPoint;
    final private Point enclosingMaxPoint;
    final private ArrayList<Line> lines;

    public static Boolean isPointsFits(List<Point> points) {
        return pointsToLines(points).isPresent();
    }

    private static Optional<ArrayList<Line>> pointsToLines(List<Point> points) {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point cur = points.get(i);
            Point next = points.get((i + 1) % points.size());
            Line curLine = new Line(cur, next);
            for (int j = 0; j < lines.size() - 1; j++) {
                if (i == points.size() - 1 && j == 0) {
                    if (curLine.findCrossPoint(lines.get(j)).isEmpty()) {
                        return Optional.empty();
                    }
                } else if (j == points.size() - 1) {
                    if (curLine.findCrossPoint(lines.get(j)).isEmpty()) {
                        return Optional.empty();
                    }
                } else {
                    if (curLine.findCrossPoint(lines.get(j)).isPresent()) {
                        return Optional.empty();
                    }
                }
            }
            lines.add(curLine);
        }
        return Optional.of(lines);
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

        Optional<ArrayList<Line>> pointsToLines = pointsToLines(points);
        assert pointsToLines.isPresent();
        lines = pointsToLines.get();
    }

    public Point enclosingMaxPoint() {
        return new Point(enclosingMaxPoint);
    }

    public Point enclosingMinPoint() {
        return new Point(enclosingMinPoint);
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void export(String path) {
        try (PrintWriter out = new PrintWriter(path)) {
            String format = points.stream().map(Object::toString).collect(Collectors.joining(","));
            out.print(format);
            out.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Export of file with vertex was failed");
            throw new RuntimeException(e);
        }
    }

    public static Convex importConvex(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            String text = scanner.useDelimiter("\\A").next();
            Convex res = new Convex(Arrays.stream(text.split( "," )).map(Point::importPoint).
                    collect(Collectors.toList()));
            scanner.close();
            return res;
        } catch (FileNotFoundException e) {
            System.err.println("Import of file with vertex was failed");
            throw new RuntimeException(e);
        }
    }
}
