package com.mcintyret.twenty48.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Driver {

    private static final int INITIAL_BLOCKS = 2;

    private static final Random RNG = new Random();

    private final Grid grid;

    private final List<GameListener> listeners = new ArrayList<>();

    private int moveCount = 0;

    private boolean started = false;

    public Driver(GameListener... initialListeners) {
        this(new Grid(), initialListeners);
    }

    public Driver(Grid grid, GameListener... initialListeners) {
        this.grid = grid;
        Collections.addAll(listeners, initialListeners);
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("Already started");
        }
        started = true;

        List<ValuedPoint> newBLocks = addNewBlocks(INITIAL_BLOCKS);
        listeners.forEach(l -> l.onStart(newBLocks));
    }

    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    public void move(MoveDirection direction) {
        move(direction, true);
    }

    public void move(MoveDirection direction, boolean addNewBlocks) {
        List<Movement> movements = grid.move(direction);

        if (movements.isEmpty()) {
            return; // Nothing interesting happened, don't need to do anything
        }

        List<ValuedPoint> added = addNewBlocks ? addNewBlocks(1) : Collections.emptyList();

        moveCount++;

        boolean gameOver = !grid.hasAvailableMoves();

        listeners.forEach(l -> l.onMove(movements, added, gameOver));
    }

    private List<ValuedPoint> addNewBlocks(int n) {
        List<Point> freePoints = newPointList();

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (grid.getNumber(r, c) == 0) {
                    freePoints.add(new Point(r, c));
                }
            }
        }

        if (!freePoints.isEmpty()) {
            Collections.shuffle(freePoints, RNG);
            int toAdd = Math.min(n, freePoints.size());

            List<ValuedPoint> added = new ArrayList<>(toAdd);

            for (int i = 0; i < toAdd; i++) {
                Point p = freePoints.get(i);
                int val = RNG.nextInt(8) == 0 ? 4 : 2;
                grid.setNumber(p.x, p.y, val);
                added.add(new ValuedPoint(p, val));
            }

            return added;
        }

        return Collections.emptyList();
    }

    private List<Point> newPointList() {
        return new ArrayList<>(grid.getCols() * grid.getRows());
    }

    public Grid getGrid() {
        return grid;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getRows() {
        return grid.getRows();
    }

    public int getCols() {
        return grid.getCols();
    }
}
