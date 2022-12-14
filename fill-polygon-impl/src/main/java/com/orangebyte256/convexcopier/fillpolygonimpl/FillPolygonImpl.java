package com.orangebyte256.convexcopier.fillpolygonimpl;

import com.orangebyte256.convexcopier.common.Polygon;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class FillPolygonImpl {
    private final AtomicInteger curY;
    private final int[] imagePixels, patternPixels;
    private final int imageWidth, patternWidth;

    public FillPolygonImpl(int[] imagePixels, int imageWidth, int[] patternPixels, int patternWidth) {
        this.imagePixels = imagePixels;
        this.patternPixels = patternPixels;
        this.imageWidth = imageWidth;
        this.patternWidth = patternWidth;
        curY = new AtomicInteger();
    }

    protected static int calcOffset(int x, int y, int width) {
        return y * width + x;
    }

    protected static List<Integer> getCrossingPoint(HashSet<Line> crossingSet, int y) {
        return crossingSet.stream().map(l -> l.getXByY(y).get()).sorted().toList();
    }

    private void fillPolygonWorker(HashSet<Line> crossingSet, HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint,
                       HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint, int lastY, Point anchor) {
        while (true) {
            final List<Integer> crossPoints;
            final int y;
            synchronized (curY) {
                y = curY.getAndDecrement();
                if (y <= lastY) {
                    return;
                }
                if (linesPerHorizonUpperPoint.containsKey(y)) {
                    crossingSet.addAll(linesPerHorizonUpperPoint.get(y));
                }
                if (linesPerHorizonBottomPoint.containsKey(y)) {
                    crossingSet.removeAll(linesPerHorizonBottomPoint.get(y));
                }
                crossPoints = getCrossingPoint(crossingSet, y);
                assert (crossPoints.size() % 2) == 0;
            }

            Iterator<Integer> iter = crossPoints.iterator();
            while (iter.hasNext()) {
                int left = iter.next();
                int right = iter.next();
                int length = right - left;
                if (length > 0) {
                    System.arraycopy(patternPixels, calcOffset(left, y, patternWidth), imagePixels,
                            calcOffset(left + anchor.x, y + anchor.y, imageWidth), length);
                }
            }
        }
    }

    // setup all maps which allows to support actual set for checking of crossing
    protected static void fillLinesPerHorizonMaps(Polygon polygon, HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint,
                                           HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint) {
        polygon.getLines().forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            Point first = line.getFirst(), second = line.getSecond();
            addValueToMap.accept(linesPerHorizonUpperPoint, Math.max(first.y, second.y));
            addValueToMap.accept(linesPerHorizonBottomPoint, Math.min(first.y, second.y));
        });
    }

    // Basic algorithm of filling polygon:
    // We are moving horizontal line between the most top and bottom point of polygon.
    // For each horizontal line we start moving from left to right and count each intersection.
    // If amount of intersection is odd we could start copying pixels from pattern until amount not became even
    // Improvement:
    // Instead of checking crossing each lines, we could check only lines which definitely have intersection.
    // For each horizontal line we support actual set for checking of crossing for this purpose.
    // Current version of algorithm supports multithreading where each thread processing another horizontal lines.
    public void fillPolygon(Polygon polygon, int parallelism, Point anchor) {
        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        HashSet<Line> crossingSet = new HashSet<>();

        fillLinesPerHorizonMaps(polygon, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint);
        Point enclosingMinPoint = polygon.enclosingMinPoint();
        Point enclosingMaxPoint = polygon.enclosingMaxPoint();
        curY.set(enclosingMaxPoint.y);

        Runnable worker = () -> fillPolygonWorker(crossingSet, linesPerHorizonUpperPoint, linesPerHorizonBottomPoint,
                enclosingMinPoint.y, anchor);
        try {
            Thread[] threads = new Thread[parallelism];
            for (int i = 0; i < parallelism; i++) {
                threads[i] = new Thread(worker);
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}