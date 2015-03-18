package com.mcintyret.twenty48.bot;

import java.util.Random;

import com.mcintyret.twenty48.core.Grid;

abstract class MonteCarloMoveStrategy implements MoveStrategy {

    private static final Random RNG = new Random();

    private static Move randomMove() {
        return Move.values()[RNG.nextInt(Move.values().length)];
    }

    private final int tries;

    private final int depth;

    public MonteCarloMoveStrategy(int tries, int depth) {
        this.tries = tries;
        this.depth = depth;
    }

    @Override
    public Move calculateMove(final Grid grid) {

        Move bestMove = null;
        int bestScore = -1;
        for (int i = 0; i < tries; i++) {

            Grid testGrid = grid.copy();
            Move first = null;
            int score = 0;

            for (int j = 0; j < depth; j++) {
                Move move = randomMove();
                if (first == null) {
                    first = move;
                }

                move.apply(testGrid);

                if (!testGrid.hasAvailableMoves()) {
                    score = getFailScore(testGrid);
                    break;
                }
            }

            if (score == 0) {
                // Didn't die
                score = scoreGrid(grid);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = first;
            }
        }
        return bestMove;
    }

    protected int getFailScore(Grid testGrid) {
        return testGrid.getScore();
    }

    protected abstract int scoreGrid(Grid grid);
}
