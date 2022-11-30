package com.orangebyte256.convexcopier.common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public static void exportImage(String path, BufferedImage image) {
        File outputfile = new File(path + ".jpg");
        try {
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            System.err.println("Some error happened while exporting");
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage importImage(String path) {
        File img = new File(path + ".jpg");
        try {
            BufferedImage in = ImageIO.read(img);
            BufferedImage result = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics2D g = result.createGraphics();
            g.drawImage(in, 0, 0, in.getWidth(), in.getHeight(), null);
            g.dispose();
            return result;
        } catch (IOException e) {
            System.err.println("Some error happened while importing");
            throw new RuntimeException(e);
        }
    }

    public static int compareImage(BufferedImage first, BufferedImage second) {
        if (first.getWidth() != second.getWidth() || first.getHeight() != second.getHeight()) {
            return -1;
        }
        int diff = 0;
        for (int y = 0; y < first.getHeight(); y++) {
            for (int x = 0; x < first.getWidth(); x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) {
                    diff++;
                }
            }
        }
        return diff;
    }

    public static BufferedImage createColorImage(int color, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[width];
        Arrays.fill(pixels, color);
        for (int y = 0; y < height; y++) {
            result.setRGB(0, y, width, 1, pixels, 0, 0);
        }
        return result;
    }

    public static void runWithTimeMeasurement(Runnable action, String info) {
        Instant startTime = Instant.now();
        action.run();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        System.out.println("Time taken for " + info + ": "+ timeElapsed.toMillis() +" milliseconds");
    }

    public static List<Point> integersToPoints(Integer... list) {
        assert list.length % 2 == 0;

        Iterator<Integer> iter = Arrays.stream(list).iterator();
        ArrayList<com.orangebyte256.convexcopier.common.Point> points = new ArrayList<>();
        while (iter.hasNext()) {
            points.add(new Point(iter.next(), iter.next()));
        }
        return points;
    }
}
