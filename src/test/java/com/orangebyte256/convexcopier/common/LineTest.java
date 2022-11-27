package com.orangebyte256.convexcopier.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LineTest {
    private static final double FUZZ_FACTOR = 0.0001;
    private void checkLineCoefficient(List<Integer> coords, List<Integer> answer) {
        Line cur = new Line(new Point(coords.get(0),coords.get(1)), new Point(coords.get(2),coords.get(3)));
        assertEquals(cur.getA(), (double)answer.get(0), FUZZ_FACTOR);
        assertEquals(cur.getB(), (double)answer.get(1), FUZZ_FACTOR);
        assertEquals(cur.getC(), (double)answer.get(2), FUZZ_FACTOR);
    }

    @Test
    @DisplayName("Correctness of coefficient calculation by two points")
    void Line() {
        checkLineCoefficient(List.of(0, 0, 1, 1), List.of(-1, 1, 0));
        checkLineCoefficient(List.of(0, 0, 5, 5), List.of(-5, 5, 0));
        checkLineCoefficient(List.of(1, 1, 0, 0), List.of(1, -1, 0));
        checkLineCoefficient(List.of(5, 5, 0, 0), List.of(5, -5, 0));

        checkLineCoefficient(List.of(2, 0, 2, 2), List.of(-2, 0, 4));
        checkLineCoefficient(List.of(0, 2, 2, 2), List.of(0, 2, -4));

        checkLineCoefficient(List.of(1, 5, 3, 9), List.of(-4, 2, -6));
        checkLineCoefficient(List.of(4, 6, 1, 1), List.of(5, -3, -2));
        checkLineCoefficient(List.of(1, 7, 7, 1), List.of(6, 6, -48));
    }

    private void checkLineCrossing(List<Integer> firstCoords, List<Integer> secondCoords, List<Integer> answer) {
        Line first = new Line(new Point(firstCoords.get(0),firstCoords.get(1)),
                new Point(firstCoords.get(2),firstCoords.get(3)));
        Line second = new Line(new Point(secondCoords.get(0),secondCoords.get(1)),
                new Point(secondCoords.get(2),secondCoords.get(3)));
        Optional<Point> cross = first.findCrossPoint(second);
        assertEquals(cross.isPresent(), answer != null);
        if (answer != null) {
            assert cross.isPresent();
            assertEquals(cross.get().x, answer.get(0));
            assertEquals(cross.get().y, answer.get(1));
        }
    }

    @Test
    @DisplayName("Crossing two lines")
    void findCross() {
        checkLineCrossing(List.of(0, 0, 50, 50), List.of(0, 50, 50, 0), List.of(25, 25));
        checkLineCrossing(List.of(0, 0, 0, 50), List.of(0, 25, 50, 25), List.of(0, 25));
        checkLineCrossing(List.of(0, 0, 25, 50), List.of(25, 50, 50, 0), List.of(25, 50));

        checkLineCrossing(List.of(0, 0, 50, 50), List.of(100, 150, 150, 100), null);
    }

    @Test
    void getYByX() {
    }

    @Test
    void getXByY() {
    }
}