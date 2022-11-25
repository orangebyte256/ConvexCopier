package main.java.com.orangebyte256.convexcopier.convexcopier;

import main.java.com.orangebyte256.convexcopier.common.Convex;
import main.java.com.orangebyte256.convexcopier.common.ImageUtils;

import java.time.Duration;
import java.time.Instant;

public class Main {
    private static ImageEditor imageEditor = null;

    private static void setupImageEditor(String path) {
        imageEditor = new ImageEditor(ImageUtils.importImage(path));
    }

    public static void main(String[] args) {
        System.out.println("Start program");
        setupImageEditor("beer");

        Instant startTime = Instant.now();
//        imageEditor.fillPolygon(new main.java.com.orangebyte256.convexcopier.common.Convex(new main.java.com.orangebyte256.convexcopier.common.Point(0,0), new main.java.com.orangebyte256.convexcopier.common.Point(9000,0),
//                new main.java.com.orangebyte256.convexcopier.common.Point(9000,9000), new main.java.com.orangebyte256.convexcopier.common.Point(0,9000)), main.java.com.orangebyte256.convexcopier.common.ImageUtils.importImage("red"));
//        imageEditor.fillPolygon(new main.java.com.orangebyte256.convexcopier.common.Convex(new main.java.com.orangebyte256.convexcopier.common.Point(12,2), new main.java.com.orangebyte256.convexcopier.common.Point(21,12),
//                new main.java.com.orangebyte256.convexcopier.common.Point(12,21), new main.java.com.orangebyte256.convexcopier.common.Point(5,12)), main.java.com.orangebyte256.convexcopier.common.ImageUtils.importImage("red"));
        imageEditor.fillPolygon(Convex.importConvex("convex.ser"), ImageUtils.importImage("penguins"));
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");

        ImageUtils.exportImage("result", imageEditor.getImage());
        System.out.println("End");
    }
}