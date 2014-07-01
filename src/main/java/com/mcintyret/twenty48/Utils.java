package com.mcintyret.twenty48;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class Utils {

    private static final Random RNG = new Random();

    public static Point randomPoint(int xMax, int yMax) {
        return new Point(RNG.nextInt(xMax), RNG.nextInt(yMax));
    }

    public static Collection<Point> getNRandomPoints(int xMax, int yMax, int n) {
        Set<Point> set = new HashSet<>();
        while (set.size() < n) {
            set.add(randomPoint(xMax, yMax));
        }
        return set;
    }

}
