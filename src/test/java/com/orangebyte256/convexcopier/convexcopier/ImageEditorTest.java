package com.orangebyte256.convexcopier.convexcopier;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.ImageUtils;
import com.orangebyte256.convexcopier.common.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageEditorTest {
    final String imagePath = "green";
    final String patternPath = "penguins";
    BufferedImage pattern = ImageUtils.importImage(patternPath);
    ImageEditor imageEditorOriginal = null;
    ImageEditor imageEditorOnPattern = null;

    @BeforeEach
    void setup() {
        imageEditorOriginal = new ImageEditor(ImageUtils.importImage(imagePath));
        imageEditorOnPattern = new ImageEditor(ImageUtils.importImage(patternPath));
    }

    private void compareFillPolygon(List<Integer> list, List<Integer> inside) {
        assert list.size() % 2 == 0;

        Iterator<Integer> iter = list.iterator();
        ArrayList<Point> points = new ArrayList<>();
        while (iter.hasNext()) {
            points.add(new Point(iter.next(), iter.next()));
        }
        compareFillPolygon(new Convex(points), inside);
    }

    private void compareFillPolygon(String path, List<Integer> inside) {
        compareFillPolygon(Convex.importConvex(path), inside);
    }

    private void compareFillPolygon(Convex convex, List<Integer> inside) {
        assert inside.size() == 2;

        imageEditorOriginal.fillPolygon(convex, pattern);
        imageEditorOnPattern.fillPolygonBFS(convex, imageEditorOriginal.getImage(),
                new Point(inside.get(0), inside.get(1)));
        int diff = ImageUtils.compareImage(imageEditorOnPattern.getImage(), pattern);
        assertEquals(0, diff);
    }

    @Test
    @DisplayName("Fill polygon with pattern")
    void fillPolygon() {
        compareFillPolygon("test0.ser", List.of(640, 340));
        compareFillPolygon(List.of(0,0, 500,0, 500,500, 0,500), List.of(250, 250));
        compareFillPolygon(List.of(250,0, 0,250, 250,500, 500,250), List.of(250, 250));
        compareFillPolygon("test1.ser", List.of(350, 260));
        compareFillPolygon("test2.ser", List.of(350, 260));
    }
}