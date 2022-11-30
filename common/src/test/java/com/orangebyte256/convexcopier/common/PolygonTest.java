package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolygonTest {

    @Test
    void isPointsFits() {
        assertTrue(Polygon.isPointsFits(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 1000,0)));
        assertFalse(Polygon.isPointsFits(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 1000,0, 1000,1000)));
        assertFalse(Polygon.isPointsFits(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000, 2000,2000)));
        assertFalse(Polygon.isPointsFits(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 500,1000)));
        assertTrue(Polygon.isPointsFits(Utils.integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000)));
    }
}