package com.orangebyte256.convexcopier.imageeditor;

import com.orangebyte256.convexcopier.common.Polygon;
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

    private static Boolean isJNI = false;
    private static Boolean needMeasurement = false;

    private void compareFillPolygon(Point inside, int width, int height, Point ancor, Integer... list) {
        compareFillPolygon(inside, width, height, ancor, new Polygon(integersToPoints(list)));
    }

    private void compareFillPolygon(Point inside, int width, int height, Point ancor, String path) {
        compareFillPolygon(inside, width, height, ancor, Polygon.importConvex(path));
    }

    private void compareFillPolygon(Point inside, int width, int height, Point ancor, Polygon polygon) {
        ImageEditor imageEditorOriginal = new ImageEditor(Utils.createColorImage(Color.RED.getRGB(), width, height));
        ImageEditor imageEditorOnPattern = new ImageEditor(Utils.createColorImage(Color.GREEN.getRGB(), width, height));
        BufferedImage pattern = Utils.createColorImage(Color.GREEN.getRGB(), width, height);
        BiConsumer<Runnable, String> consumer = needMeasurement ? Utils::runWithTimeMeasurement : (a, b) -> a.run();
        if (needMeasurement) {
            consumer.accept(() -> imageEditorOriginal.fillPolygon(polygon, pattern, PARALLELISM, isJNI, ancor), "Original with parallelism");
            consumer.accept(() -> imageEditorOriginal.fillPolygon(polygon, pattern, 1, isJNI, ancor), "Original");
        } else {
            consumer.accept(() -> imageEditorOriginal.fillPolygon(polygon, pattern, PARALLELISM, isJNI, ancor), "Original");
        }
        consumer.accept(() -> imageEditorOnPattern.fillPolygonBFS(polygon, imageEditorOriginal.getImage(), inside, ancor), "BFS");
        int diff = Utils.compareImage(imageEditorOnPattern.getImage(), pattern);
        assertEquals(0, diff);
    }

    void basicTestFillPolygon(Point ancor) {
        needMeasurement = false;
        compareFillPolygon(new Point(250, 250), WIDTH, HEIGHT, ancor, 0, 0,  500, 0,  500, 500,  0, 500);
        compareFillPolygon(new Point(250, 250), WIDTH, HEIGHT, ancor, 250, 0,  0, 250,  250, 500,  500, 250);
        compareFillPolygon(new Point(640, 340), WIDTH, HEIGHT, ancor, "test0.ser");
        compareFillPolygon(new Point(350, 260), WIDTH, HEIGHT, ancor, "test1.ser");
        compareFillPolygon(new Point(350, 260), WIDTH, HEIGHT, ancor, "test2.ser");
    }

    @Test
    @DisplayName("Fill polygon on Java implementation")
    void basicFillPolygonJava() {
        isJNI = false;
        basicTestFillPolygon(new Point(0, 0));
    }

    @Test
    @DisplayName("Fill polygon on C++ implementation")
    void basicFillPolygonCpp() {
        isJNI = true;
        basicTestFillPolygon(new Point(0, 0));
    }

    @Test
    @DisplayName("Fill polygon on Java implementation with ancor")
    void basicFillPolygonJavaAncor() {
        isJNI = false;
        basicTestFillPolygon(new Point(123, 67));
    }

    @Test
    @DisplayName("Fill polygon on C++ implementation with ancor")
    void basicFillPolygonCppAncor() {
        isJNI = true;
        basicTestFillPolygon(new Point(123, 67));
    }

    private Polygon createComplexConvex() {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 1; i < 500; i++) {
            points.add(new Point(i * 20, (i % 2) * 4990));
        }
        for (int i = 500 - 1; i > 0; i--) {
            points.add(new Point(i * 20, Math.min(9999, (i % 2) * 5000 + 5010)));
        }
        return new Polygon(points);
    }

    void complexTestFillPolygon() {
        compareFillPolygon(new Point(505, 5000), BIG_WIDTH, BIG_HEIGHT, new Point(0,0), createComplexConvex());
        compareFillPolygon(new Point(5000, 5000), BIG_WIDTH, BIG_HEIGHT, new Point(0,0), 0, 0,  9999, 0,  9999, 9999,  0, 9999);
    }

    @Test
    @DisplayName("Fill polygon big data on Java implementation")
    void fillPolygonBigDataJava() {
        needMeasurement = false;
        isJNI = false;
        complexTestFillPolygon();
    }

    @Test
    @DisplayName("Fill polygon big data on C++ implementation")
    void fillPolygonBigDataCpp() {
        needMeasurement = false;
        isJNI = true;
        complexTestFillPolygon();
    }

    @Test
    @DisplayName("Fill polygon big data with measurement on Java implementation")
    void fillPolygonBigDataWithMeasurementJava() {
        needMeasurement = true;
        isJNI = false;
        complexTestFillPolygon();
    }

    @Test
    @DisplayName("Fill polygon big data with measurement on C++ implementation")
    void fillPolygonBigDataWithMeasurementCpp() {
        needMeasurement = true;
        isJNI = true;
        complexTestFillPolygon();
    }
}