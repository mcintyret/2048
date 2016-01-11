package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.bot.MoveStrategy;
import com.mcintyret.twenty48.bot.TopCornerMoveStrategy;
import com.mcintyret.twenty48.core.Driver;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.EventQueue;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GUI {

    private static final int BLOCK_WIDTH = 50;

    private static final int GAME_SIZE = 4;

    private static final int GAME_WIDTH = BLOCK_WIDTH * GAME_SIZE;

    static final JFrame FRAME = new JFrame("2048");


    public static void main(String[] args) {

        FRAME.setSize(new Dimension(GAME_WIDTH, GAME_WIDTH));

        FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MoveStrategy moveStrategy = new TopCornerMoveStrategy(10, 2);

        Driver driver = new Driver();

        FRAME.getContentPane().add(new GamePanel(driver));

        EventQueue.invokeLater(() -> {
            FRAME.setLocationRelativeTo(null);
            FRAME.setVisible(true);
        });

//        Bot bot = new Bot(driver, moveStrategy);
        driver.start();
//        new Thread(bot).start();

//        bot.run();
    }

}
