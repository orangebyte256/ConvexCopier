package com.orangebyte256.convexcopier.convexcopier;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.ImageUtils;
import com.orangebyte256.convexcopier.common.Point;
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
    final ImageEditor imageEditorFirst = new ImageEditor(ImageUtils.importImage(imagePath));
    final ImageEditor imageEditorSecond = new ImageEditor(ImageUtils.importImage(imagePath));

    private void compareFillPolygon(List<Integer> list) {
        assert list.size() % 2 == 0;

        Iterator<Integer> iter = list.iterator();
        ArrayList<Point> points = new ArrayList<>();
        while (iter.hasNext()) {
            points.add(new Point(iter.next(), iter.next()));
        }
        compareFillPolygon(new Convex(points));
    }

    private void compareFillPolygon(String path) {
        compareFillPolygon(Convex.importConvex(path));
    }

    private void compareFillPolygon(Convex convex) {
        imageEditorFirst.fillPolygon(convex, pattern);
        imageEditorSecond.fillPolygonBFS(convex, pattern);
        int diff = ImageUtils.compareImage(imageEditorFirst.getImage(), imageEditorSecond.getImage());
        assertNotEquals(diff, -1);
        assertTrue(diff < convex.calcPerimeter());
    }

    @Test
    @DisplayName("Fill polygon with pattern")
    void fillPolygon() {
        compareFillPolygon("test0.ser");
        compareFillPolygon(List.of(0,0, 500,0, 500,500, 0,500));
        compareFillPolygon(List.of(250,0, 0,250, 250,500, 500,250));
    }
}