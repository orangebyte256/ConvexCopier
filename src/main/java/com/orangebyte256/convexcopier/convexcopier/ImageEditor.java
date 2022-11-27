package com.orangebyte256.convexcopier.convexcopier;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.Utils;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.orangebyte256.convexcopier.common.Utils.runWithTimeMeasurement;

public class ImageEditor {
    final private BufferedImage image;

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    private Boolean convexFits(Convex convex) {
        return convex.enclosingMaxPoint().x < image.getWidth() && convex.enclosingMaxPoint().y < image.getHeight();
    }

    public void fillPolygonBFS(Convex convex, BufferedImage pattern, Point inside) {
        assert convexFits(convex);

        int[][] visited = new int[image.getHeight()][image.getWidth()];
        Consumer<Point> markVisited = ((p) -> {
            for (int y = p.y - 1; y <= p.y + 1; y++) {
                for (int x = p.x - 1; x <= p.x + 1; x++) {
                    if (0 <= x && x < image.getWidth() && 0 <= y && y < image.getHeight()) {
                        visited[y][x] = 1;
                    }
                }
            }
        });

        convex.getLines().forEach(line -> {
            Point first = line.getFirst(), second = line.getSecond();
            int width = Math.abs(first.x - second.x);
            int height = Math.abs(first.y - second.y);
            if (width > height) {
                int left = Math.min(first.x, second.x);
                int right = Math.max(first.x, second.x);
                for (int x = left; x <= right; x++) {
                    int y = line.getYByX(x);
                    markVisited.accept(new Point(x, y));
                }
            } else {
                int bottom = Math.min(first.y, second.y);
                int up = Math.max(first.y, second.y);
                for (int y = bottom; y <= up; y++) {
                    int x = line.getXByY(y);
                    markVisited.accept(new Point(x, y));
                }
            }
        });

        Queue<Point> queue = new LinkedList<>();
        queue.add(inside);
        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int x = point.x;
            int y = point.y;
            if (x == -1 || x == pattern.getWidth() || y == -1 || y == pattern.getHeight()) {
                continue;
            }
            if (visited[y][x] == 1) {
                continue;
            }
            image.setRGB(x, y, pattern.getRGB(x, y));
            visited[y][x] = 1;
            queue.add(new Point(x + 1, y));
            queue.add(new Point(x - 1, y));
            queue.add(new Point(x, y + 1));
            queue.add(new Point(x, y - 1));
        }
    }

    public void fillPolygon(Convex convex, BufferedImage pattern) {
        assert convexFits(convex);

        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        convex.getLines().forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            Point first = line.getFirst(), second = line.getSecond();
            addValueToMap.accept(linesPerHorizonUpperPoint, Math.max(first.y, second.y));
            addValueToMap.accept(linesPerHorizonBottomPoint, Math.min(first.y, second.y));
        });

        Point enclosingMinPoint = convex.enclosingMinPoint();
        Point enclosingMaxPoint = convex.enclosingMaxPoint();
        HashSet<Line> crossingSet = new HashSet<>();
        for (int y = enclosingMaxPoint.y; y >= enclosingMinPoint.y; y--) {
            final int yFinal = y;
            if (linesPerHorizonUpperPoint.containsKey(y)) {
                crossingSet.addAll(linesPerHorizonUpperPoint.get(y));
            }
            if (linesPerHorizonBottomPoint.containsKey(y)) {
                crossingSet.removeAll(linesPerHorizonBottomPoint.get(y));
            }
            List<Integer> crossPoints = crossingSet.stream().map(l -> l.getXByY(yFinal)).sorted().toList();
            assert (crossPoints.size() % 2) == 0;

            Iterator<Integer> iter = crossPoints.iterator();
            while (iter.hasNext()) {
                int left = iter.next();
                int right = iter.next();
                int length = right - left;
                int[] pixels = pattern.getRGB(left, y, length, 1, null, 0, length);
                image.setRGB(left, y, length, 1, pixels, 0, 0);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public static void main(String[] args) {
        System.out.println("Start Convex Copier");
        final String coordsPath = "convex.ser";
        final String patternPath = "penguins";
        final String imagePath = "green";
        ImageEditor imageEditor = new ImageEditor(Utils.importImage(imagePath));

        runWithTimeMeasurement(() ->
                imageEditor.fillPolygon(Convex.importConvex(coordsPath), Utils.importImage(patternPath)), "Original");

        Utils.exportImage("result", imageEditor.image);
        System.out.println("End");
    }

}
