package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.Utils;
import com.mcintyret.twenty48.core.Grid;

public class FreeSpacesMoveStrategy extends MonteCarloMoveStrategy {

    public FreeSpacesMoveStrategy(int tries, int depth) {
        super(tries, depth);
    }

    @Override
    protected int scoreGrid(Grid grid) {
        return grid.getScore() * (1 + Utils.getNumberOfFreeSpaces(grid));
    }
}
