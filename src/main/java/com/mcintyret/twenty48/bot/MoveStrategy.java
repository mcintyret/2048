package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;

public interface MoveStrategy {

    Move calculateMove(Grid grid);

}
