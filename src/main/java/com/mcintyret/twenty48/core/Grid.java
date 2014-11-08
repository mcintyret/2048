package com.mcintyret.twenty48.core;

import java.util.*;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class Grid {

    private static final int DEFAULT_SIZE = 4;

    private final int[][] numbers;

    private final CellState[][] states;

    private final List<OrientatedPoint> lastNewPoints = new ArrayList<>();

    private final List<OrientatedPoint> lastCombinedPoints = new ArrayList<>();

    private final int rows;

    private final int cols;

    private Orientation currentMoveOrientation;

    public Grid() {
        this(DEFAULT_SIZE);
    }

    public Grid(int sideLength) {
        this(sideLength, sideLength);
    }

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        numbers = new int[rows][cols];
        states = new CellState[rows][cols];

        for (int i = 0; i < rows; i++) {
            Arrays.fill(states[i], CellState.EMPTY);
        }
    }

    public void moveLeft() {
        clearSpecialPoints();
        move(Orientation.ZERO);
    }

    public void moveUp() {
        clearSpecialPoints();
        move(Orientation.NINETY);
    }

    public void moveRight() {
        clearSpecialPoints();
        move(Orientation.ONE_EIGHTY);
    }

    public void moveDown() {
        clearSpecialPoints();
        move(Orientation.TWO_SEVENTY);
    }

    public void continueMove() {
        move(currentMoveOrientation);
    }

    private void move(Orientation orientation) {
        System.out.println("Moving " + orientation);
        printGrid();
        currentMoveOrientation = orientation;

        boolean changed = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols - 1; j++) {
                CellState firstState = getState(orientation, i, j);
                if (firstState == CellState.EMPTY) {
                    // shift everything along by one
                    changed |= shift(orientation, i, j);
                    break;
                } else if (firstState == CellState.NORMAL && getState(orientation, i, j + 1) == CellState.NORMAL) {
                    int num = getNumber(orientation, i, j);
                    if (num == getNumber(orientation, i, j + 1)) {
                        changed = true;
                        setNumber(orientation, num << 1, i, j);
                        setState(orientation, CellState.COMBINED, i, j);

                        setNumber(orientation, 0, i, j + 1);
                        setState(orientation, CellState.EMPTY, i, j + 1);

                        lastCombinedPoints.add(new OrientatedPoint(i, j, orientation));

                        shift(orientation, i, j + 1);
                    }
                }
            }
        }

        if (!changed) {
            currentMoveOrientation = null;
        }
        printGrid();
    }

    private boolean shift(Orientation orientation, int i, int j) {
        System.out.println("Shifting");
        boolean changed = false;
        for (int k = j + 1; k < cols; k++) {
            CellState secondState = getState(orientation, i, k);
            if (secondState != CellState.EMPTY) {
                changed = true;
                setState(orientation, secondState, i, k - 1);
                setState(orientation, CellState.EMPTY, i, k);
                setNumber(orientation, getNumber(orientation, i, k), i, k - 1);
                setNumber(orientation, 0, i, k);
            }
        }
        printGrid();
        return changed;
    }

    public boolean moveInProgress() {
        return currentMoveOrientation != null;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private CellState getState(Orientation o, int i, int j) {
        return o.get(states, rows, cols, i, j);
    }

    private CellState setState(Orientation o, CellState state, int i, int j) {
        return o.set(states, state, rows, cols, i, j);
    }

    private int getNumber(Orientation o, int i, int j) {
        return o.get(numbers, rows, cols, i, j);
    }

    private int setNumber(Orientation o, int val, int i, int j) {
        return o.set(numbers, val, rows, cols, i, j);
    }

    public CellState getState(int i, int j) {
        return getState(Orientation.DEFAULT_ORIENTATION, i, j);
    }

    public int getNumber(int i, int j) {
        return getNumber(Orientation.DEFAULT_ORIENTATION, i, j);
    }

    // TODO: make the below stuff nicer

    private static final Random RNG = new Random();

    public void addNewBlocks(int n) {

        List<OrientatedPoint> free = newPointList();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i][0] == 0) {
                free.add(new OrientatedPoint(i, 0));
            }
            if (numbers[i][numbers.length - 1] == 0) {
                free.add(new OrientatedPoint(i, numbers.length - 1));
            }
        }

        for (int j = 1; j < numbers.length - 1; j++) {
            if (numbers[0][j] == 0) {
                free.add(new OrientatedPoint(0, j));
            }

            if (numbers[numbers.length - 1][j] == 0) {
                free.add(new OrientatedPoint(numbers.length - 1, j));
            }
        }

        Collections.shuffle(free);

        Iterator<OrientatedPoint> it = free.iterator();

        setNewPoint(it.next());

        while (it.hasNext() && --n > 0) {
            setNewPoint(it.next());
        }
    }

    private void setNewPoint(OrientatedPoint point) {
        setState(point.getOrientation(), CellState.NEW, point.getX(), point.getY());
        setNumber(point.getOrientation(), RNG.nextBoolean() ? 2 : 4, point.getX(), point.getY());
        lastNewPoints.add(point);
    }

    private void clearSpecialPoints() {
        clearSpecialPoints(lastNewPoints);
        clearSpecialPoints(lastCombinedPoints);
    }

    private void clearSpecialPoints(List<OrientatedPoint> points) {
        for (OrientatedPoint p : points) {
            setState(p.getOrientation(), CellState.NORMAL, p.getX(), p.getY());
        }
        points.clear();
    }

    private List<OrientatedPoint> newPointList() {
        int size = 2 * (rows + rows - 2); // TODO: ??
        return new ArrayList<>(size);
    }

    public boolean hasAvailableMoves() {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] == 0) {
                    return true;
                }
                if (i < numbers.length - 1 && numbers[i][j] == numbers[i + 1][j]) {
                    return true;
                }
                if (j < numbers[i].length - 1 && numbers[i][j] == numbers[i][j + 1]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printGrid() {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                System.out.print(numbers[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }
}
