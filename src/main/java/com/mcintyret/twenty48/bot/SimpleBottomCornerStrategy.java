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

    private MoveDirection previousMove;

    int sameMoveCount = 0;

    @Override
    public MoveDirection calculateMove(Grid grid) {
        MoveDirection move = doCalculateMove(grid);
        if (move == previousMove) {
            sameMoveCount++;
            if (sameMoveCount > 100000) {
                System.out.println(move);
            }
        }
        return (previousMove = move);
    }

    private MoveDirection doCalculateMove(Grid grid) {
        int leftScore = (int) (calculateScoreChange(grid, MoveDirection.LEFT) * 1.5F);
        if (previousMove == MoveDirection.RIGHT) {
            // Very strong chance that we were forced to do this last move against our will. Just move back!
            if (leftScore >= 0) {
                return MoveDirection.LEFT;
            }
        }

        if (previousMove == MoveDirection.UP && grid.getNumber(grid.getRows() - 1, 0) == 0) {
            // If we just moved up and left a gap at the bottom left corner, move back down
            return MoveDirection.DOWN;
        }

        boolean firstColFull = isColFull(grid, 0);

        if (!firstColFull && leftScore >= 0) {
            // Always move left if it will help fill up the first column
            return MoveDirection.LEFT;
        }

        int downScore = (int) (calculateScoreChange(grid, MoveDirection.DOWN) * 1.8F);
        int upLeftScore = -1;
        int upScore = -10;

        // if it's safe to move up, our best bet might be to move up and the move left
        if (firstColFull) {
            upLeftScore = calculateScoreChange(grid, MoveDirection.UP, MoveDirection.LEFT);
            if (upLeftScore >= 0) {
                int downLeftScore = calculateScoreChange(grid, MoveDirection.DOWN, MoveDirection.LEFT);
                // Must be significantly better than going down then left
                if (upLeftScore < downLeftScore * 4) {
                    upLeftScore = -1;
                } else {
                    upLeftScore /= 2; // give it a penalty anyway
                }
            }
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
        });

        for (MoveDirection direction : directions) {
            driver.move(direction, false); // only want to take into account scores from existing blocks here
        }

        if (gameOverRef.get() || allMovements.isEmpty()) {
            return -1;
        }

        return copy.getScore() - grid.getScore();
    }
}
