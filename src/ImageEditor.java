import java.awt.image.BufferedImage;
import java.util.*;

public class ImageEditor {
    private BufferedImage image;

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    public void fillPolygon(Point[] coords, BufferedImage pattern) {
        HashMap<Integer, ArrayList<Line>> linesPerHorizonFirst = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonSecond = new HashMap<>();
        for (int i = 0; i < coords.length; i++) {
            Point cur = coords[i];
            Point next = coords[(i + 1) % coords.length];
            Line line = new Line(cur, next);

            linesPerHorizonFirst.putIfAbsent(line.first.y, new ArrayList<>());
            linesPerHorizonSecond.putIfAbsent(line.second.y, new ArrayList<>());
            linesPerHorizonFirst.get(line.first.y).add(line);
            linesPerHorizonSecond.get(line.second.y).add(line);
        }

        List<Point> coordsList = Arrays.asList(coords);
        int minX = coordsList.stream().map(p -> p.x).min(Integer::compare).get();
        int maxX = coordsList.stream().map(p -> p.x).max(Integer::compare).get();
        int minY = coordsList.stream().map(p -> p.y).min(Integer::compare).get();
        int maxY = coordsList.stream().map(p -> p.y).max(Integer::compare).get();

        HashSet<Line> crossingSet = new HashSet<>();
        for (int y = maxY; y >= minY; y--) {
            Line horizon = new Line(new Point(minX, y), new Point(maxX, y));
            if (linesPerHorizonFirst.containsKey(y)) {
                crossingSet.addAll(linesPerHorizonFirst.get(y));
            }
            if (linesPerHorizonSecond.containsKey(y)) {
                crossingSet.removeAll(linesPerHorizonSecond.get(y));
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
