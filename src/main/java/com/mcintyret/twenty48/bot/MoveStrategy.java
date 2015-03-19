package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.MoveDirection;

public interface MoveStrategy {

    MoveDirection calculateMove(Grid grid);

}
