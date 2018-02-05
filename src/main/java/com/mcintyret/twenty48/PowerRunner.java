package com.mcintyret.twenty48;

import com.mcintyret.twenty48.bot.Bot;
import com.mcintyret.twenty48.bot.MoveStrategy;
import com.mcintyret.twenty48.core.Driver;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PowerRunner {

    private static final int NUMBER_OF_THREADS = 4;
    private static final int NUMBER_OF_GAMES = 10000;

    public static void runInPowerMode(Class<? extends MoveStrategy> moveStrategyClass) throws InterruptedException {
        ExecutorService exec = new ThreadPoolExecutor(
                NUMBER_OF_THREADS,
                NUMBER_OF_THREADS,
                Long.MAX_VALUE,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        Collection<Integer> scores = new ConcurrentLinkedQueue<>();
        AtomicInteger completed = new AtomicInteger();
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            exec.submit(() -> {
                Driver driver = new Driver();
                try {
                    Bot bot = new Bot(driver, moveStrategyClass.newInstance());
                    driver.start();
                    bot.run();
                    scores.add(driver.getMoveCount());
                    int completedNow = completed.incrementAndGet();
                    if (completedNow % 1000 == 0) {
                        System.out.println("Finished " + completedNow + " games");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            });
        }

        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        printStats(scores);
    }

    private static void printStats(Collection<Integer> scores) {
        if (scores.size() != NUMBER_OF_GAMES) {
            throw new AssertionError("Scores: " + scores.size() + ", Games: " + NUMBER_OF_GAMES);
        }
        long total = 0;
        int max = 0;
        for (int score : scores) {
            if (score > max) {
                max = score;
            }
            total += score;
        }

        System.out.println("Max: " + max);
        System.out.println("Avg: " + total / NUMBER_OF_GAMES);
    }
}
