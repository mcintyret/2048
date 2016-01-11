package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.bot.MoveStrategy;
import com.mcintyret.twenty48.bot.TopCornerMoveStrategy;
import com.mcintyret.twenty48.core.Driver;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.EventQueue;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GUI {

    static final JFrame FRAME = new JFrame("2048");

    public static void main(String[] args) {

        FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MoveStrategy moveStrategy = new TopCornerMoveStrategy(10, 2);

        Driver driver = new Driver();

        GamePanel gamePanel = new GamePanel(driver);
        FRAME.getContentPane().add(gamePanel);

        EventQueue.invokeLater(() -> {
            FRAME.pack();
            FRAME.setSize(gamePanel.getSize());
            FRAME.setLocationRelativeTo(null);
            FRAME.setVisible(true);
        });

//        Bot bot = new Bot(driver, moveStrategy);
        driver.start();
//        new Thread(bot).start();

//        bot.run();
    }

}
