package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.MoveDirection;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleBottomCornerStrategy implements MoveStrategy {

    @Override
    public MoveDirection calculateMove(Grid grid) {
        int downScore = calculateScoreChange(grid, MoveDirection.DOWN);
        int leftScore = calculateScoreChange(grid, MoveDirection.LEFT);
        int upLeftScore = -1;
        int upScore = -10;

        // if it's safe to move up, our best bet might be to move up and the move left
        if (isColFull(grid, 0)) {
            upLeftScore = calculateScoreChange(grid, MoveDirection.UP, MoveDirection.LEFT);
            upScore = calculateScoreChange(grid, MoveDirection.UP);
        }

        int best = Math.max(Math.max(upLeftScore, downScore), Math.max(leftScore, upScore));
        if (best >= 0) {
            if (best == downScore) {
                return MoveDirection.DOWN;
            } else if (best == leftScore) {
                return MoveDirection.LEFT;
            } else {
                return MoveDirection.UP;
            }
        }

        // None of the ideal moves seem available
        if (upScore == -10) {
            upScore = calculateScoreChange(grid, MoveDirection.UP);
        }

        // just move up if we can
        if (upScore >= 0) {
            return MoveDirection.UP;
        }

        // Fuck it, move right
        return MoveDirection.RIGHT;
    }

    private static boolean isRowFull(Grid grid, int row) {
        int prev = -1;
        for (int col = 0; col < grid.getCols(); col++) {
            int val = grid.getNumber(row, col);
            if (val == 0 || val == prev) {
                return false;
            }
            prev = val;
        }
        return true;
    }

    private static boolean isColFull(Grid grid, int col) {
        int prev = -1;
        for (int row = 0; row < grid.getRows(); row++) {
            int val = grid.getNumber(row, col);
            if (val == 0 || val == prev) {
                return false;
            }
            prev = val;
        }
        return true;
    }

    private int calculateScoreChange(Grid grid, MoveDirection... directions) {
        Grid copy = grid.copy();
        ArrayList<Movement> allMovements = new ArrayList<>();
        AtomicBoolean gameOverRef = new AtomicBoolean();
        Driver driver = new Driver(copy);
        driver.addGameListener(new GameListener() {
            @Override
            public void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean gameOver) {
                allMovements.addAll(movements);
                gameOverRef.set(gameOver);
            }

            @Override
            public void onStart(List<ValuedPoint> initialBlocks) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        for (MoveDirection direction : directions) {
            driver.move(direction);
        }

        if (gameOverRef.get() || allMovements.isEmpty()) {
            return -1;
        }

        return copy.getScore() - grid.getScore();
    }
}
