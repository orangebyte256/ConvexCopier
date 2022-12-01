package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void compareImage() {
        BufferedImage image = Utils.createColorImage(Color.RED.getRGB(), 10, 10);
        BufferedImage redImage = Utils.createColorImage(Color.RED.getRGB(), 10, 10);
        for (int y = 0; y < 10; y++) {
            image.setRGB(5, y, Color.GREEN.getRGB());
        }
        assertEquals(Utils.compareImage(image, redImage), 10);
        for (int x = 0; x < 10; x++) {
            image.setRGB(x, 5, Color.GREEN.getRGB());
        }
        assertEquals(Utils.compareImage(image, redImage), 19);
    }

    @Test
    void createColorImage() {
        BufferedImage image = Utils.createColorImage(Color.RED.getRGB(), 10, 10);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                assertEquals(image.getRGB(x, y), Color.RED.getRGB());
            }
        }
    }

    @Test
    void integersToPoints() {
        assertEquals(Utils.integersToPoints(0,0, 100,100, 50,50), List.of(new Point(0,0), new Point(100,100), new Point(50,50)));
    }
}