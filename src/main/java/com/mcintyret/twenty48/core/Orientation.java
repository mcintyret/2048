package com.mcintyret.twenty48.core;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public enum Orientation {
    ZERO {
        @Override
        int transformI(int i, int j, int rows, int cols) {
            return i;
        }

        @Override
        int transformJ(int i, int j, int rows, int cols) {
            return j;
        }
    },
    NINETY {
        @Override
        int transformI(int i, int j, int rows, int cols) {
            return j;
        }

        @Override
        int transformJ(int i, int j, int rows, int cols) {
            return rows - (i + 1);
        }
    },
    ONE_EIGHTY {
        @Override
        int transformI(int i, int j, int rows, int cols) {
            return rows - (i + 1);
        }

        @Override
        int transformJ(int i, int j, int rows, int cols) {
            return cols - (j + 1);
        }
    },
    TWO_SEVENTY {
        @Override
        int transformI(int i, int j, int rows, int cols) {
            return cols - (j + 1);
        }

        @Override
        int transformJ(int i, int j, int rows, int cols) {
            return i;
        }
    };

    public static final Orientation DEFAULT_ORIENTATION = ZERO;
    
    abstract int transformI(int i, int j, int rows, int cols);
    
    abstract int transformJ(int i, int j, int rows, int cols);
    
    public <T> T get(T[][] a, int rows, int cols, int i, int j) {
        return a[transformI(i, j, rows, cols)][transformJ(i, j, rows, cols)];
    }
    
    public <T> T set(T[][] a, T val, int rows, int cols, int i, int j) {
        int newI = transformI(i, j, rows, cols);
        int newJ = transformJ(i, j, rows, cols);
        T curr = a[newI][newJ];
        a[newI][newJ] = val;
        return curr;
    }
    
    public int get(int[][] a, int rows, int cols, int i, int j) {
        return a[transformI(i, j, rows, cols)][transformJ(i, j, rows, cols)];
    }
    
    public int set(int[][] a, int val, int rows, int cols, int i, int j) {
        int newI = transformI(i, j, rows, cols);
        int newJ = transformJ(i, j, rows, cols);
        int curr = a[newI][newJ];
        a[newI][newJ] = val;
        return curr;
    }

}
