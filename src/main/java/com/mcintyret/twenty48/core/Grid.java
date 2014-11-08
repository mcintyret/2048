package com.mcintyret.twenty48.core;

import java.util.*;
import java.util.List;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class Grid {

    private static final int DEFAULT_SIZE = 4;

    private final int[][] numbers;

    private final int rows;

    private final int cols;

    private Orientation currentOrientation;

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
    }

    public List<Movement> moveLeft() {
        return move(Orientation.ZERO);
    }

    public List<Movement> moveUp() {
        return move(Orientation.NINETY);
    }

    public List<Movement> moveRight() {
        return move(Orientation.ONE_EIGHTY);
    }

    public List<Movement> moveDown() {
        return move(Orientation.TWO_SEVENTY);
    }

    private List<Movement> move(Orientation orientation) {
        currentOrientation = orientation;
        List<Movement> moves = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            int lastMerge = -1;
            for (int j = 1; j < cols; j++) {
                int val = getVal(i, j);

                if (val > 0) {
                    boolean moved = false;
                    int end = 0;
                    for (int k = j - 1; k >= 0; k--) {
                        int val2 = getVal(i, k);
                        if (val == val2 && lastMerge != k) {
                            // Cool, we found a merge
                            moved = true;
                            val *= 2;
                            end = k;
                            lastMerge = k;
                            break;
                        } else if (val2 == 0) {
                            moved = true;
                        } else {
                            end = k + 1;
                            break;
                        }
                    }
                    if (moved) {
                        setVal(0, i, j);
                        setVal(val, i, end);
                        moves.add(new Movement(
                            new Point(
                                orientation.transformI(i, j, rows, cols),
                                orientation.transformJ(i, j, rows, cols)
                            ),
                            new Point(
                                orientation.transformI(i, end, rows, cols),
                                orientation.transformJ(i, end, rows, cols)
                            )
                        ));
                    }
                }
            }
        }

        return moves;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    private int getVal(int i, int j) {
        return currentOrientation.get(numbers, rows, cols, i, j);
    }

    private int setVal(int val, int i, int j) {
        return currentOrientation.set(numbers, val, rows, cols, i, j);
    }


    // TODO: make the below stuff nicer

    private static final Random RNG = new Random();

    public List<Point> addNewBlocks(int n) {

        List<Point> free = newPointList();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i][0] == 0) {
                free.add(new Point(i, 0));
            }
            if (numbers[i][numbers.length - 1] == 0) {
                free.add(new Point(i, numbers.length - 1));
            }
        }

        for (int j = 1; j < numbers.length - 1; j++) {
            if (numbers[0][j] == 0) {
                free.add(new Point(0, j));
            }

            if (numbers[numbers.length - 1][j] == 0) {
                free.add(new Point(numbers.length - 1, j));
            }
        }

        Collections.shuffle(free);

        Iterator<Point> it = free.iterator();
        List<Point> added = new ArrayList<>(n);

        while (it.hasNext() && n-- > 0) {
            Point p = it.next();
            added.add(p);
            numbers[p.x][p.y] = RNG.nextBoolean() ? 2 : 4;
        }

        return added;
    }

    private List<Point> newPointList() {
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

    public int getNumber(int i, int j) {
        return numbers[i][j];
    }
}
