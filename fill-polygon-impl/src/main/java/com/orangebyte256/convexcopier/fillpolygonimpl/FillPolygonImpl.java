package com.orangebyte256.convexcopier.fillpolygonimpl;

import com.orangebyte256.convexcopier.common.Convex;
import com.orangebyte256.convexcopier.common.Line;
import com.orangebyte256.convexcopier.common.Point;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class FillPolygonImpl {
    public static void fillPolygon(BufferedImage image, Convex convex, BufferedImage pattern, int parallelism) {
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
        AtomicInteger yIter = new AtomicInteger(enclosingMaxPoint.y + 1);
        Runnable runnable = () -> {
            while (true) {
                final List<Integer> crossPoints;
                final int y;
                synchronized (yIter) {
                    y = yIter.addAndGet(-1);
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
                    int[] pixels = pattern.getRGB(left, y, length, 1, null, 0, length);
                    image.setRGB(left, y, length, 1, pixels, 0, 0);
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
}