package com.mcintyret.twenty48;

import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.mcintyret.twenty48.core.Grid;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class Utils {

    private static final Random RNG = new Random();

    public static Point randomPoint(int xMax, int yMax) {
        return new Point(RNG.nextInt(xMax), RNG.nextInt(yMax));
    }

    public static int getNumberOfFreeSpaces(Grid grid) {
        int num = 0;
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getNumber(r, c) == 0) {
                    num++;
                }
            }
        }
        return num;
    }

    public static Collection<Point> getNRandomPoints(int xMax, int yMax, int n) {
        Set<Point> set = new HashSet<>();
        while (set.size() < n) {
            set.add(randomPoint(xMax, yMax));
        }
        return set;
    }

    public static void sleepUninterruptibly(long millis) {
        long targetTime = System.currentTimeMillis() + millis;
        long toSleep;
        while ((toSleep = targetTime - System.currentTimeMillis()) > 0) {

            try {
                Thread.sleep(toSleep);
                break;
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

}
