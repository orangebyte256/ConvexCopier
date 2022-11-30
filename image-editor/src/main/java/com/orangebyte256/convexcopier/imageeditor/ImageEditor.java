package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Convex;
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
            image.setRGB(x, y, pattern.getRGB(x, y));
            visited[y][x] = 1;
            queue.add(new Point(x + 1, y));
            queue.add(new Point(x - 1, y));
            queue.add(new Point(x, y + 1));
            queue.add(new Point(x, y - 1));
        }
    }

    public void fillPolygon(Convex convex, BufferedImage pattern, int parallelism, boolean useJNI) {
        assert convexFits(convex);

        int[] imagePixels = ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
        int[] patternPixels = ((DataBufferInt)(pattern.getRaster().getDataBuffer())).getData();

        FillPolygonImpl fillPolygonImpl = new FillPolygonImpl();
        if (useJNI) {
            int[] arrayOfPoints = new int[convex.getPoints().size() * 2];
            for (int i = 0; i < convex.getPoints().size(); i++) {
                arrayOfPoints[i * 2] = convex.getPoints().get(i).x;
                arrayOfPoints[i * 2 + 1] = convex.getPoints().get(i).y;
            }
            fillPolygonImpl.fillPolygonJNI(imagePixels, image.getWidth(), arrayOfPoints, patternPixels, pattern.getWidth(), parallelism);
        } else {
            FillPolygonImpl.fillPolygonJava(imagePixels, image.getWidth(), convex, patternPixels, pattern.getWidth(), parallelism);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    private static void printHelpMessage() {
        System.out.println("Incorrect command. You can use:");
        System.out.println("For creation polygon: -create pattern_image outfile_for_coordinates");
        System.out.println("For making copy: -copy source_image pattern_image coordinates_file");
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
                if (args.length != 4) {
                    printHelpMessage();
                    return;
                }
                ImageEditor imageEditor = new ImageEditor(Utils.importImage(args[1]));
                runWithTimeMeasurement(() -> imageEditor.fillPolygon(Convex.importConvex(args[3]),
                        Utils.importImage(args[2]), 4, true), "Original");
                Utils.exportImage("result", imageEditor.image);
            }
            default -> printHelpMessage();

        }
        System.out.println("End");
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

}
