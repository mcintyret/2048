package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.MoveDirection;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

import java.util.List;

public class Bot implements Runnable, GameListener {

    private final Driver driver;

    private final MoveStrategy moveStrategy;

    public Bot(Driver driver, MoveStrategy moveStrategy) {
        this.driver = driver;
        this.moveStrategy = moveStrategy;
        driver.addGameListener(this);
    }

    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            MoveDirection moveDirection = moveStrategy.calculateMove(driver.getGrid());

            driver.move(moveDirection);
        }
    }

    @Override
    public void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean gameOver) {
        running = !gameOver;
    }
}
