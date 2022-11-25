import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.BiConsumer;

public class ImageEditor {
    final private BufferedImage image;

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    public void fillPolygon(Convex convex, BufferedImage pattern) {
        int[][] visited = new int[image.getHeight()][image.getWidth()];
        convex.getLines().forEach(line -> {
            int width = Math.abs(line.first.x - line.second.x);
            int height = Math.abs(line.first.y - line.second.y);
            if (width > height) {
                int left = Math.min(line.first.x, line.second.x);
                int right = Math.max(line.first.x, line.second.x);
                for (double x = left; x <= right; x += 0.5) {
                    int y = line.getYByX(x);
                    visited[y][(int)x] = 1;
                }
            } else {
                int bottom = Math.min(line.first.y, line.second.y);
                int up = Math.max(line.first.y, line.second.y);
                for (double y = bottom; y <= up; y += 0.5) {
                    int x = line.getXByY(y);
                    visited[(int)y][x] = 1;
                }
            }
        });

        Point start = new Point((convex.enclosingMaxPoint().x + convex.enclosingMinPoint().x) / 2,
                (convex.enclosingMaxPoint().y + convex.enclosingMinPoint().y) / 2);
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
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
        System.out.println("1");
    }

    public void fillPolygon1(Convex convex, BufferedImage pattern) {
        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        convex.getLines().forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            addValueToMap.accept(linesPerHorizonUpperPoint, Math.max(line.first.y, line.second.y));
            addValueToMap.accept(linesPerHorizonBottomPoint, Math.min(line.first.y, line.second.y));
        });

        Point enclosingMinPoint = convex.enclosingMinPoint();
        Point enclosingMaxPoint = convex.enclosingMaxPoint();
        HashSet<Line> crossingSet = new HashSet<>();
        for (int y = enclosingMaxPoint.y; y >= enclosingMinPoint.y; y--) {
            Line horizon = new Line(new Point(enclosingMinPoint.x, y), new Point(enclosingMaxPoint.x, y));
            if (linesPerHorizonUpperPoint.containsKey(y)) {
                crossingSet.addAll(linesPerHorizonUpperPoint.get(y));
            }
            if (linesPerHorizonBottomPoint.containsKey(y)) {
                crossingSet.removeAll(linesPerHorizonBottomPoint.get(y));
            }
            List<Integer> crossPoints = crossingSet.stream().map(horizon::findCross).map(p -> p.x).sorted().toList();
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
}
