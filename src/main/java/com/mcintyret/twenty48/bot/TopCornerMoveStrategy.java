package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;

public class TopCornerMoveStrategy extends MonteCarloMoveStrategy {

    public TopCornerMoveStrategy(int tries, int depth) {
        super(tries, depth);
    }

    @Override
    protected int scoreGrid(Grid grid) {
        int score = 0;
        for (int r = 1; r <= grid.getRows(); r++) {
            for (int c = 1; c <= grid.getCols(); c++) {
                int val = grid.getNumber(r - 1, c - 1);
                if (val > 0) {
                    val *= (grid.getRows() * r) + c; // positional multiplier
                }
                score += val;
            }
        }
        return score;
    }

    @Override
    protected int getFailScore(Grid testGrid) {
        return 1;
    }
}
