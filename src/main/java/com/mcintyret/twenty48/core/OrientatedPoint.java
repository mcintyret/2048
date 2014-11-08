package com.mcintyret.twenty48.core;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class OrientatedPoint {

    private final int x;

    private final int y;

    private final Orientation orientation;

    public OrientatedPoint(int x, int y, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    public OrientatedPoint(int x, int y) {
        this(x, y, Orientation.DEFAULT_ORIENTATION);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
