package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.MoveDirection;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for MoveStrategy implementations that want to decide on a move by randomly simulating potential moves
 * and scoring them (according to the implementation class' strategy), using the highest-scoring move
 */
public abstract class MonteCarloMoveStrategy implements MoveStrategy {

    private static final Random RNG = new Random();

    private static MoveDirection randomMove() {
        return MoveDirection.values()[RNG.nextInt(MoveDirection.values().length)];
    }

    private final int tries;

    private final int depth;

    public MonteCarloMoveStrategy(int tries, int depth) {
        this.tries = tries;
        this.depth = depth;
    }

    @Override
    public MoveDirection calculateMove(final Grid grid) {

        MoveDirection bestMoveDirection = null;
        int bestScore = -1;
        for (int i = 0; i < tries; i++) {

            AtomicBoolean gameOver = new AtomicBoolean();
            Driver driver = new Driver(grid.copy(), new GameListener() {
                @Override
                public void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean isGameOver) {
                    gameOver.set(isGameOver);
                }
            });
            MoveDirection first = null;
            int score = 0;

            for (int j = 0; j < depth; j++) {
                MoveDirection moveDirection = randomMove();
                if (first == null) {
                    first = moveDirection;
                }

                driver.move(moveDirection);

                if (gameOver.get()) {
                    score = getFailScore(driver.getGrid());
                    break;
                }
            }

            if (score == 0) {
                // Didn't die
                score = scoreGrid(driver.getGrid());
            }

            if (score > bestScore) {
                bestScore = score;
                bestMoveDirection = first;
            }
        }
        return bestMoveDirection;
    }

    protected int getFailScore(Grid testGrid) {
        return testGrid.getScore();
    }

    protected abstract int scoreGrid(Grid grid);
}
