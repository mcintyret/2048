package com.mcintyret.twenty48.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.mcintyret.twenty48.bot.Bot;
import com.mcintyret.twenty48.core.Grid;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GUI {

    static final int BLOCK_WIDTH = 50;

    static final int GAME_SIZE = 4;

    private static final int GAME_WIDTH = BLOCK_WIDTH * GAME_SIZE;

    static final JFrame FRAME = new JFrame("2048");


    public static void main(String[] args) {

        FRAME.setSize(new Dimension(GAME_WIDTH, GAME_WIDTH));

        FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Grid grid = new Grid();
        Bot bot = new Bot(grid);
        GamePanel gamePanel = new GamePanel();
        GridPanel gridPanel = new GridPanel(gamePanel, grid);

        FRAME.getContentPane().add(gamePanel);

        EventQueue.invokeLater(() -> FRAME.setVisible(true));
        bot.run();
    }

}
