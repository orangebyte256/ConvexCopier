package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.Test;

import static com.orangebyte256.convexcopier.common.Utils.integersToPoints;
import static org.junit.jupiter.api.Assertions.*;

class ConvexTest {

    @Test
    void isPointsFits() {
        assertTrue(Convex.isPointsFits(integersToPoints(0,0, 0,1000, 1000,1000, 1000,0)));
        assertFalse(Convex.isPointsFits(integersToPoints(0,0, 0,1000, 1000,1000, 1000,0, 1000,1000)));
        assertFalse(Convex.isPointsFits(integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000, 2000,2000)));
        assertFalse(Convex.isPointsFits(integersToPoints(0,0, 0,1000, 1000,1000, 500,1000)));
        assertTrue(Convex.isPointsFits(integersToPoints(0,0, 0,1000, 1000,1000, 2000,1000)));
    }
}