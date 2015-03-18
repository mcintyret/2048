package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;

public class Bot implements Runnable {

    private final Grid grid;

    private final MoveStrategy moveStrategy;

    public Bot(Grid grid) {
        this.grid = grid;
//        this.moveStrategy = new FreeSpacesMoveStrategy(1000, 10);
        this.moveStrategy = new TopCornerMoveStrategy(10, 1);
    }

    @Override
    public void run() {
        while (grid.hasAvailableMoves()) {
            Move move = moveStrategy.calculateMove(grid);
            System.out.println("Moving " + move);

            move.apply(grid);

            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
