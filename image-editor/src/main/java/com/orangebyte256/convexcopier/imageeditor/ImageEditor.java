package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Polygon;
import com.orangebyte256.convexcopier.common.Utils;
import com.orangebyte256.convexcopier.common.Point;
import com.orangebyte256.convexcopier.fillpolygonimpl.FillPolygonImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import java.util.function.Consumer;

import static com.orangebyte256.convexcopier.common.Utils.runWithTimeMeasurement;

public class ImageEditor {
    static {
        System.loadLibrary("fill_polygon_impl_cpp");
    }

    private final BufferedImage image;

    public ImageEditor(BufferedImage image) {
        this.image = image;
    }

    protected void fillPolygonBFS(Polygon polygon, BufferedImage pattern, Point inside, Point patternAncor) {
        assert polygon.fitsToImage(image, new Point(0, 0));
        assert polygon.fitsToImage(pattern, patternAncor);

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

        polygon.getLines().forEach(line -> {
            Point first = line.getFirst(), second = line.getSecond();
            int width = Math.abs(first.x - second.x);
            int height = Math.abs(first.y - second.y);
            if (width > height) {
                int left = Math.min(first.x, second.x);
                int right = Math.max(first.x, second.x);
                for (int x = left; x <= right; x++) {
                    int y = line.getYByX(x).get();
                    markVisited.accept(new Point(x, y));
                }
            } else {
                int bottom = Math.min(first.y, second.y);
                int up = Math.max(first.y, second.y);
                for (int y = bottom; y <= up; y++) {
                    int x = line.getXByY(y).get();
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
            image.setRGB(x, y, pattern.getRGB(x + patternAncor.x, y + patternAncor.y));
            visited[y][x] = 1;
            queue.add(new Point(x + 1, y));
            queue.add(new Point(x - 1, y));
            queue.add(new Point(x, y + 1));
            queue.add(new Point(x, y - 1));
        }
    }

    public native void fillPolygonJNI(int[] imagePixels, int imageWidth, int[] convex, int[] patternPixels,
                                      int patternWidth, int parallelism, int anchorX, int anchorY);

    public void fillPolygon(Polygon polygon, BufferedImage pattern, int parallelism, boolean useJNI, Point anchor) {
        assert polygon.fitsToImage(image, anchor);
        assert polygon.fitsToImage(pattern, new Point(0, 0));

        int[] imagePixels = ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
        int[] patternPixels = ((DataBufferInt)(pattern.getRaster().getDataBuffer())).getData();

        if (useJNI) {
            fillPolygonJNI(imagePixels, image.getWidth(), polygon.pointsToPlainArray(), patternPixels,
                    pattern.getWidth(), parallelism, anchor.x, anchor.y);
        } else {
            FillPolygonImpl javaImpl = new FillPolygonImpl(imagePixels, image.getWidth(), patternPixels, pattern.getWidth());
            javaImpl.fillPolygon(polygon, parallelism, anchor);
        }
    }

    protected BufferedImage getImage() {
        return image;
    }

    private static void printHelpMessage() {
        System.out.println("Incorrect command. You can use:");
        System.out.println("For creation polygon: -create pattern_image outfile_for_coordinates");
        System.out.println("For making copy: -copy source_image pattern_image coordinates_file ancorX ancorY [jni]");
    }

    private static void runPointCreator(String pattern, String coordsPath) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage image = Utils.importImage(pattern);
        if (image.getWidth() >= screenSize.getWidth() || image.getHeight() >= screenSize.getHeight()) {
            JOptionPane.showMessageDialog(null, "Image too big, choose another one");
            System.exit(0);
        }
        new PointCreator(image, coordsPath);
    }

    public static void main(String[] args) {
        System.out.println("Start Convex Copier");

        if (args.length == 0) {
            printHelpMessage();
            return;
        }

        switch (args[0]) {
            case "-create" -> {
                if (args.length != 3) {
                    printHelpMessage();
                    return;
                }
                runPointCreator(args[1], args[2]);
            }
            case "-copy" -> {
                if (args.length != 6 && !(args.length == 7 && args[6].equals("jni"))) {
                    printHelpMessage();
                    return;
                }
                ImageEditor imageEditor = new ImageEditor(Utils.importImage(args[1]));
                BufferedImage pattern = Utils.importImage(args[2]);
                Polygon polygon = Polygon.importConvex(args[3]);
                Point ancor = new Point(Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                boolean isJNI = args.length == 7;
                if (!polygon.fitsToImage(pattern, new Point(0, 0))) {
                    System.err.println("Error: Polygon's points outside pattern image");
                    return;
                }
                if (!polygon.fitsToImage(imageEditor.getImage(), ancor)) {
                    System.err.println("Error: Polygon's points with ancor outside source image");
                    return;
                }
                runWithTimeMeasurement(() -> imageEditor.fillPolygon(polygon, pattern, 4, isJNI, ancor), "Original");
                Utils.exportImage("result", imageEditor.image);
            }
            default -> printHelpMessage();
        }
    }
}
