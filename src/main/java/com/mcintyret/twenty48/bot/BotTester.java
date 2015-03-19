package com.mcintyret.twenty48.bot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.mcintyret.twenty48.core.Driver;

public class BotTester {

    private static final int TESTS = 1000;

    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService EXEC = Executors.newFixedThreadPool(THREADS);

    public static void main(String[] args) throws InterruptedException {
        testBot();
    }

    private static void testBot() throws InterruptedException {
        MoveStrategy moveStrategy = new TopCornerMoveStrategy(10, 1);

        AtomicLong totalScore = new AtomicLong();
        AtomicInteger doneCount = new AtomicInteger();

        CountDownLatch done = new CountDownLatch(TESTS);

        for (int i = 0; i < TESTS; i++) {
            EXEC.execute(() -> {
                Driver driver = new Driver();
                Bot bot = new Bot(driver, moveStrategy);
                driver.start();

                bot.run();

                totalScore.addAndGet(driver.getGrid().getScore());

                int doneNow = doneCount.incrementAndGet();
                if (doneNow % 100 == 0) {
                    System.out.println(doneNow + "...");
                }
                done.countDown();
            });
        }

        System.out.println("Waiting...");
        done.await();

        System.out.println("Average score: " + totalScore.get() / TESTS);

    }
}
