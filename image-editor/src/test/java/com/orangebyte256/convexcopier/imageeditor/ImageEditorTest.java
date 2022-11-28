package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.Utils;
import com.orangebyte256.convexcopier.common.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import static com.orangebyte256.convexcopier.common.Utils.integersToPoints;
import static org.junit.jupiter.api.Assertions.*;

class ImageEditorTest {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final int BIG_WIDTH = 10000;
    private static final int BIG_HEIGHT = 10000;
    private static final int PARALLELISM = 4;

    private void compareFillPolygon(Point inside, int width, int height, Boolean needMeasurement, Integer... list) {
        compareFillPolygon(inside, width, height, needMeasurement, new Convex(integersToPoints(list)));
    }

    private void compareFillPolygon(Point inside, int width, int height, Boolean needMeasurement, String path) {
        compareFillPolygon(inside, width, height, needMeasurement, Convex.importConvex(path));
    }

    private void compareFillPolygon(Point inside, int width, int height, Boolean needMeasurement, Convex convex) {
        ImageEditor imageEditorOriginal = new ImageEditor(Utils.createColorImage(Color.RED.getRGB(), width, height));
        ImageEditor imageEditorOnPattern = new ImageEditor(Utils.createColorImage(Color.GREEN.getRGB(), width, height));
        BufferedImage pattern = Utils.createColorImage(Color.GREEN.getRGB(), width, height);
        BiConsumer<Runnable, String> consumer = needMeasurement ? Utils::runWithTimeMeasurement : (a, b) -> a.run();
        if (needMeasurement) {
            consumer.accept(() -> imageEditorOriginal.fillPolygon(convex, pattern, PARALLELISM), "Original with parallelism");
            consumer.accept(() -> imageEditorOriginal.fillPolygon(convex, pattern, 1), "Original");
        } else {
            consumer.accept(() -> imageEditorOriginal.fillPolygon(convex, pattern, PARALLELISM), "Original");
        }
        consumer.accept(() -> imageEditorOnPattern.fillPolygonBFS(convex, imageEditorOriginal.getImage(), inside), "BFS");
        int diff = Utils.compareImage(imageEditorOnPattern.getImage(), pattern);
        assertEquals(0, diff);
    }

    @Test
    @DisplayName("Fill polygon with pattern")
    void fillPolygon() {
        compareFillPolygon(new Point(250, 250), WIDTH, HEIGHT, false, 0,0, 500,0, 500,500, 0,500);
        compareFillPolygon(new Point(250, 250), WIDTH, HEIGHT, false, 250,0, 0,250, 250,500, 500,250);
        compareFillPolygon(new Point(640, 340), WIDTH, HEIGHT, false, "test0.ser");
        compareFillPolygon(new Point(350, 260), WIDTH, HEIGHT, false, "test1.ser");
        compareFillPolygon(new Point(350, 260), WIDTH, HEIGHT, false, "test2.ser");
    }

    private Convex createComplexConvex() {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 1; i < 500; i++) {
            points.add(new Point(i * 20, (i % 2) * 4990));
        }
        for (int i = 500 - 1; i > 0; i--) {
            points.add(new Point(i * 20, Math.min(9999, (i % 2) * 5000 + 5010)));
        }
        return new Convex(points);
    }

    @Test
    @DisplayName("Fill polygon with pattern on big data with measurement")
    void fillPolygonWIthTime() {
        compareFillPolygon(new Point(505, 5000), BIG_WIDTH, BIG_HEIGHT, true, createComplexConvex());
        compareFillPolygon(new Point(5000, 5000), BIG_WIDTH, BIG_HEIGHT, true, 0,0, 9999,0, 9999,9999, 0,9999);
    }

    @Test
    @DisplayName("Fill polygon with pattern on big data")
    void fillPolygonBigData() {
        compareFillPolygon(new Point(505, 5000), BIG_WIDTH, BIG_HEIGHT, false, createComplexConvex());
        compareFillPolygon(new Point(5000, 5000), BIG_WIDTH, BIG_HEIGHT, false, 0,0, 9999,0, 9999,9999, 0,9999);
    }
}