package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.MoveDirection;

/**
 * Interface to define a strategy that determines what move a Bot will perform given the current state of the game.
 */
public interface MoveStrategy {

    /**
     * @param grid the current state of the grid
     * @return MoveDirection - UP, DOWN, LEFT or RIGHT
     */
    MoveDirection calculateMove(Grid grid);

}
