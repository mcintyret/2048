package com.mcintyret.twenty48.bot;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.MoveDirection;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

public class Bot implements Runnable, GameListener {

    private final Driver driver;

    private final MoveStrategy moveStrategy;

    public Bot(Driver driver, MoveStrategy moveStrategy) {
        this.driver = driver;
        this.moveStrategy = moveStrategy;
        driver.addGameListener(this);
    }

    private volatile boolean running = true;

    private final CountDownLatch startLatch = new CountDownLatch(1);

    @Override
    public void run() {
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }

        while (running) {
            MoveDirection moveDirection = moveStrategy.calculateMove(driver.getGrid());

            driver.move(moveDirection);

            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean gameOver) {
        running = !gameOver;
    }

    @Override
    public void onStart(List<ValuedPoint> initialBlocks) {
        startLatch.countDown();
    }
}
