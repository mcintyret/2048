package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.Grid;

public class BotTest {

    private static final int RUNS = 10000;

    private static final MoveStrategy MOVE_STRATEGY = new SimpleBottomCornerStrategy();

    public static void main(String args[]) {
        long totalScore = 0;
        int minScore = Integer.MAX_VALUE, maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < RUNS; i++) {
            if (i % 100 == 0) {
                System.out.println(i);
            }
            int score = runTest(MOVE_STRATEGY);
            totalScore += score;
            minScore = Math.min(minScore, score);
            maxScore = Math.max(maxScore, score);
        }

        System.out.println("Min: " + minScore + ", max: " + maxScore + ", avg: " + totalScore / RUNS);

    }

    private static int runTest(MoveStrategy moveStrategy) {
        Grid grid = new Grid();
        Driver driver = new Driver(grid);
        Bot bot = new Bot(driver, moveStrategy);

        driver.start();

        bot.run();


        return grid.getScore();

    }
}
