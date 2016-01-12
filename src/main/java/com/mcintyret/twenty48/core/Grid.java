package com.mcintyret.twenty48.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the numbers currently on the game grid
 */
public class Grid {

    private static final int DEFAULT_SIZE = 4;

    private final int[][] numbers;

    private final int rows;

    private final int cols;

    private int score;

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

    List<Movement> move(MoveDirection direction) {
        switch (direction) {
            case LEFT:
                return move(Orientation.ZERO);
            case UP:
                return move(Orientation.NINETY);
            case RIGHT:
                return move(Orientation.ONE_EIGHTY);
            case DOWN:
                return move(Orientation.TWO_SEVENTY);
            default:
                throw new IllegalArgumentException();
        }
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
                            score += val;
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

    /**
     * Returns the number of rows in this grid
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in this grid
     */
    public int getCols() {
        return cols;
    }

    private int getVal(int i, int j) {
        return currentOrientation.get(numbers, rows, cols, i, j);
    }

    private int setVal(int val, int i, int j) {
        return currentOrientation.set(numbers, val, rows, cols, i, j);
    }

    boolean hasAvailableMoves() {
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

    /**
     * Returns the number at row i, column j.
     * Row and column indices are 0-based.
     * A value of 0 means no number is present.
     */
    public int getNumber(int i, int j) {
        return numbers[i][j];
    }

    void setNumber(int i, int j, int val) {
        numbers[i][j] = val;
    }

    /**
     * Returns a copy of this Grid. Useful if, for example, you wanted to run a simulation of possible moves without
     * changing the state of this Grid.
     */
    public Grid copy() {
        Grid newGrid = new Grid(rows, cols);
        newGrid.score = score;
        newGrid.currentOrientation = this.currentOrientation;

        for (int r = 0; r < rows; r++) {
            System.arraycopy(this.numbers[r], 0, newGrid.numbers[r], 0, cols);
        }
        return newGrid;
    }

    public int getScore() {
        return score;
    }

}
