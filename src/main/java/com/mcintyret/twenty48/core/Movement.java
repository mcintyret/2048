package com.mcintyret.twenty48.core;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class Movement {

    private final Point from;

    private final Point to;

    public Movement(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }
}
