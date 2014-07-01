package com.mcintyret.twenty48;

import javax.swing.*;
import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GUI {

    static final int BLOCK_WIDTH = 250;

    static final int GAME_SIZE = 4;

    private static final int GAME_WIDTH = BLOCK_WIDTH * GAME_SIZE;

    static final JFrame FRAME = new JFrame("2048");


    public static void main(String[] args) {

        FRAME.setSize(new Dimension(GAME_WIDTH, GAME_WIDTH));

        FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        FRAME.getContentPane().add(new GridPanel());

        EventQueue.invokeLater(() -> FRAME.setVisible(true));
    }

}
