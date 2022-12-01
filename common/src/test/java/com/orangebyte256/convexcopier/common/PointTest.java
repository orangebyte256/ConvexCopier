package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void testEquals() {
        assertEquals(new Point(1,1), new Point(1,1));
        assertNotEquals(new Point(1,2), new Point(1,1));
    }

    @Test
    void importPoint() {
        assertEquals(new Point(1,1), Point.importPoint("1 1"));
        assertNotEquals(new Point(1,1), Point.importPoint("1 2"));
    }
}