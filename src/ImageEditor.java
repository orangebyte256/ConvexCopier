import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.BiConsumer;

public class ImageEditor {
    private BufferedImage image;

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    public void fillPolygon(Point[] coords, BufferedImage pattern) {
        ArrayList<Line> lines = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            Point cur = coords[i];
            Point next = coords[(i + 1) % coords.length];
            lines.add(new Line(cur, next));
        }

        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        lines.forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            addValueToMap.accept(linesPerHorizonUpperPoint, line.first.y);
            addValueToMap.accept(linesPerHorizonBottomPoint, line.second.y);
        });

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (Point point : coords) {
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
        }

        HashSet<Line> crossingSet = new HashSet<>();
        for (int y = maxY; y >= minY; y--) {
            Line horizon = new Line(new Point(minX, y), new Point(maxX, y));
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
                for (int x = left; x < right; x++) {
                    image.setRGB(x, y, pattern.getRGB(x, y));
                }
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}
