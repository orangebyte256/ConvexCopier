package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolygonTest {

    @Test
    void isPointsFits() {
        assertTrue(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 1000,0)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,0, 1000,1000)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 0,2000)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,2000, 0,1000)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 1000,0, 1000,1000)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000, 2000,2000)));
        assertFalse(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 500,1000)));
        assertTrue(Polygon.arePointsWithoutIntersections(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000)));
    }

    @Test
    void enclosingPoints() {
        Polygon polygon = new Polygon(List.of(new Point(0,30), new Point(200,30), new Point(200,100), new Point(0,100)));
        assertEquals(polygon.enclosingMaxPoint(), new Point(200, 100));
        assertEquals(polygon.enclosingMinPoint(), new Point(0, 30));
    }

    @Test
    void pointsToPlainArray() {
        Polygon polygon = new Polygon(List.of(new Point(0,30), new Point(200,30), new Point(200,100), new Point(0,100)));
        assertArrayEquals(polygon.pointsToPlainArray(), new int[]{0, 30, 200, 30, 200, 100, 0, 100});
    }
}