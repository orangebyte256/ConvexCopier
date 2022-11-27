package com.orangebyte256.convexcopier.convexcopier;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.Utils;
import com.orangebyte256.convexcopier.common.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.orangebyte256.convexcopier.common.Utils.runWithTimeMeasurement;
import static org.junit.jupiter.api.Assertions.*;

class ImageEditorTest {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private static final int BIG_WIDTH = 10000;
    private static final int BIG_HEIGHT = 10000;

    private void compareFillPolygon(List<Integer> list, List<Integer> inside, int width, int height) {
        assert list.size() % 2 == 0;

        Iterator<Integer> iter = list.iterator();
        ArrayList<Point> points = new ArrayList<>();
        while (iter.hasNext()) {
            points.add(new Point(iter.next(), iter.next()));
        }
        compareFillPolygon(new Convex(points), inside, width, height);
    }

    private void compareFillPolygon(String path, List<Integer> inside, int width, int height) {
        compareFillPolygon(Convex.importConvex(path), inside, width, height);
    }

    private void compareFillPolygon(Convex convex, List<Integer> inside, int width, int height) {
        assert inside.size() == 2;

        ImageEditor imageEditorOriginal = new ImageEditor(Utils.createColorImage(Color.RED.getRGB(), width, height));
        ImageEditor imageEditorOnPattern = new ImageEditor(Utils.createColorImage(Color.GREEN.getRGB(), width, height));
        BufferedImage pattern = Utils.createColorImage(Color.GREEN.getRGB(), width, height);

        runWithTimeMeasurement(() -> imageEditorOriginal.fillPolygon(convex, pattern), "Original");
        runWithTimeMeasurement(() -> imageEditorOriginal.fillPolygonBFS(convex, imageEditorOriginal.getImage(),
                new Point(inside.get(0), inside.get(1))), "BFS");
        int diff = Utils.compareImage(imageEditorOnPattern.getImage(), pattern);
        assertEquals(0, diff);
    }

    @Test
    @DisplayName("Fill polygon with pattern")
    void fillPolygon() {
        compareFillPolygon("test0.ser", List.of(640, 340), WIDTH, HEIGHT);
        compareFillPolygon(List.of(0,0, 500,0, 500,500, 0,500), List.of(250, 250), WIDTH, HEIGHT);
        compareFillPolygon(List.of(250,0, 0,250, 250,500, 500,250), List.of(250, 250), WIDTH, HEIGHT);
        compareFillPolygon("test1.ser", List.of(350, 260), WIDTH, HEIGHT);
        compareFillPolygon("test2.ser", List.of(350, 260), WIDTH, HEIGHT);
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
    @DisplayName("Fill polygon with pattern on big data")
    void fillPolygonWIthTime() {
        compareFillPolygon(createComplexConvex(), List.of(505, 5000), BIG_WIDTH, BIG_HEIGHT);
        compareFillPolygon(List.of(0,0, 9999,0, 9999,9999, 0,9999), List.of(5000, 5000), BIG_WIDTH, BIG_HEIGHT);
    }
}