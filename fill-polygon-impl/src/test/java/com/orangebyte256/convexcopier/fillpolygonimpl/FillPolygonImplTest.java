package com.orangebyte256.convexcopier.fillpolygonimpl;

import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;
import com.orangebyte256.convexcopier.common.Polygon;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FillPolygonImplTest {

    @Test
    void calcOffset() {
        assertEquals(FillPolygonImpl.calcOffset(10, 10, 10), 110);
        assertEquals(FillPolygonImpl.calcOffset(0, 10, 1), 10);
    }

    private void compareListAsSet(List<Line> list1, List<Line> list2) {
        assertEquals(list1.size(), list2.size());
        assertEquals(new HashSet<>(list1), new HashSet<>(list2));
    }

    @Test
    void fillLinesPerHorizonMaps() {
        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        Polygon polygon = new Polygon(List.of(new Point(25,0), new Point(0,25), new Point(25,50), new Point(50,25)));
        FillPolygonImpl.fillLinesPerHorizonMaps(polygon, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint);
        compareListAsSet(linesPerHorizonUpperPoint.get(25),
                List.of(new Line(new Point(25,0), new Point(0,25)), new Line(new Point(50,25), new Point(25,0))));
        compareListAsSet(linesPerHorizonUpperPoint.get(50),
                List.of(new Line(new Point(0,25), new Point(25,50)), new Line(new Point(25,50), new Point(50,25))));
        compareListAsSet(linesPerHorizonBottomPoint.get(0),
                List.of(new Line(new Point(25,0), new Point(0,25)), new Line(new Point(50,25), new Point(25,0))));
        compareListAsSet(linesPerHorizonBottomPoint.get(25),
                List.of(new Line(new Point(0,25), new Point(25,50)), new Line(new Point(25,50), new Point(50,25))));
    }

    @Test
    void getCrossingPoint() {
        Line l1 = new Line(new Point(0,0), new Point(10, 10));
        Line l2 = new Line(new Point(10,10), new Point(20, 0));
        Line l3 = new Line(new Point(0,0), new Point(20, 10));
        HashSet<Line> crossingSet = new HashSet<>(List.of(l1, l2, l3));
        assertEquals(FillPolygonImpl.getCrossingPoint(crossingSet, 0), List.of(0, 0, 20));
        assertEquals(FillPolygonImpl.getCrossingPoint(crossingSet, 5), List.of(5, 10, 15));
        assertEquals(FillPolygonImpl.getCrossingPoint(crossingSet, 10), List.of(10, 10, 20));
    }
}