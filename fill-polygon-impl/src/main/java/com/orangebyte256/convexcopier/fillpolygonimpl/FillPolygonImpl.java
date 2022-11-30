package com.orangebyte256.convexcopier.fillpolygonimpl;

import com.orangebyte256.convexcopier.common.Polygon;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class FillPolygonImpl {
    private final HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
    private final HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
    private final HashSet<Line> crossingSet = new HashSet<>();
    final AtomicInteger curY;
    final int[] imagePixels, patternPixels;
    final int imageWidth, patternWidth;

    public FillPolygonImpl(int[] imagePixels, int imageWidth, int[] patternPixels, int patternWidth) {
        this.imagePixels = imagePixels;
        this.patternPixels = patternPixels;
        this.imageWidth = imageWidth;
        this.patternWidth = patternWidth;
        curY = new AtomicInteger();
    }

    private int calcOffset(int x, int y, int width) {
        return y * width + x;
    }

    private void fillPolygonWorker(int lastY, Point ancor) {
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

                crossPoints = crossingSet.stream().map(l -> l.getXByY(y).get()).sorted().toList();
            }
            assert (crossPoints.size() % 2) == 0;

            Iterator<Integer> iter = crossPoints.iterator();
            while (iter.hasNext()) {
                int left = iter.next();
                int right = iter.next();
                int length = right - left;
                if (length > 0) {
                    System.arraycopy(patternPixels, calcOffset(left, y, patternWidth), imagePixels,
                            calcOffset(left + ancor.x, y + ancor.y, imageWidth), length);
                }
            }
        }
    }

    public void fillPolygon(Polygon polygon, int parallelism, Point ancor) {
        linesPerHorizonUpperPoint.clear();
        linesPerHorizonBottomPoint.clear();
        crossingSet.clear();

        polygon.getLines().forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            Point first = line.getFirst(), second = line.getSecond();
            addValueToMap.accept(linesPerHorizonUpperPoint, Math.max(first.y, second.y));
            addValueToMap.accept(linesPerHorizonBottomPoint, Math.min(first.y, second.y));
        });

        Point enclosingMinPoint = polygon.enclosingMinPoint();
        Point enclosingMaxPoint = polygon.enclosingMaxPoint();
        curY.set(enclosingMaxPoint.y);
        Runnable worker = () -> fillPolygonWorker(enclosingMinPoint.y, ancor);

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