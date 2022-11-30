package com.orangebyte256.convexcopier.fillpolygonimpl;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class FillPolygonImpl {
    static {
        System.loadLibrary("fill_polygon_impl_cpp");
    }

    private static int calcOffset(int x, int y, int width) {
        return y * width + x;
    }

    public static void fillPolygonJava(int[] imagePixels, int imageWidth, Convex convex, int[] patternPixels,
                                   int patternWidth, int parallelism, int ancorX, int ancorY) {
        HashMap<Integer, ArrayList<Line>> linesPerHorizonUpperPoint = new HashMap<>();
        HashMap<Integer, ArrayList<Line>> linesPerHorizonBottomPoint = new HashMap<>();
        convex.getLines().forEach(line -> {
            BiConsumer<HashMap<Integer, ArrayList<Line>>, Integer> addValueToMap = (map, y) -> {
                map.putIfAbsent(y, new ArrayList<>());
                map.get(y).add(line);
            };
            Point first = line.getFirst(), second = line.getSecond();
            addValueToMap.accept(linesPerHorizonUpperPoint, Math.max(first.y, second.y));
            addValueToMap.accept(linesPerHorizonBottomPoint, Math.min(first.y, second.y));
        });

        Point enclosingMinPoint = convex.enclosingMinPoint();
        Point enclosingMaxPoint = convex.enclosingMaxPoint();
        HashSet<Line> crossingSet = new HashSet<>();
        AtomicInteger yIter = new AtomicInteger(enclosingMaxPoint.y);
        Runnable runnable = () -> {
            while (true) {
                final List<Integer> crossPoints;
                final int y;
                synchronized (yIter) {
                    y = yIter.getAndDecrement();
                    if (y <= enclosingMinPoint.y) {
                        return;
                    }

                    if (linesPerHorizonUpperPoint.containsKey(y)) {
                        crossingSet.addAll(linesPerHorizonUpperPoint.get(y));
                    }
                    if (linesPerHorizonBottomPoint.containsKey(y)) {
                        crossingSet.removeAll(linesPerHorizonBottomPoint.get(y));
                    }

                    crossPoints = crossingSet.stream().map(l -> l.getXByY(y).get()).sorted().toList();
                    assert (crossPoints.size() % 2) == 0;
                }
                Iterator<Integer> iter = crossPoints.iterator();
                while (iter.hasNext()) {
                    int left = iter.next();
                    int right = iter.next();
                    int length = right - left;
                    if (length > 0) {
                        System.arraycopy(patternPixels, calcOffset(left, y, patternWidth), imagePixels,
                                calcOffset(left + ancorX, y + ancorY, imageWidth), length);
                    }
                }
            }
        };
        try {
            Thread[] threads = new Thread[parallelism];
            for (int i = 0; i < parallelism; i++) {
                threads[i] = new Thread(runnable);
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public native void fillPolygonJNI(int[] imagePixels, int imageWidth, int[] convex, int[] patternPixels,
                                        int patternWidth, int parallelism, int ancorX, int ancorY);

}