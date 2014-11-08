package com.mcintyret.twenty48;

import com.mcintyret.twenty48.core.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GridPanel extends JPanel {

    private static final Random RNG = new Random();

    private Grid grid;

    private final JPanel gridPanel = new JPanel();

    private final JPanel topPanel = new JPanel();

    private final JLabel scoreLabel = new JLabel();

    private int score = 0;

    public GridPanel() {
        reset();

        setLayout(new BorderLayout());
        add(gridPanel, BorderLayout.CENTER);
        gridPanel.setLayout(new GridLayout(grid.getRows(), grid.getCols()));

        add(topPanel, BorderLayout.NORTH);
        topPanel.add(scoreLabel);


        registerKeystroke("left", KeyEvent.VK_LEFT, grid::moveLeft);
        registerKeystroke("right", KeyEvent.VK_RIGHT, grid::moveRight);
        registerKeystroke("up", KeyEvent.VK_UP, grid::moveUp);
        registerKeystroke("down", KeyEvent.VK_DOWN, grid::moveDown);
    }

    private void reset() {
        grid = new Grid();

        grid.addNewBlocks(2);

        updateGrid();
        score = 0;
        updateScoreLabel();
    }

    private void registerKeystroke(String name, int keyEvent, Runnable mover) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mover.run(); // This calls one of the grid::move* methods
                updateGrid();
                while (grid.moveInProgress()) {
                    grid.continueMove();
                    updateGrid();
                }
                afterMove();
            }
        });
    }

    private void afterMove() {
        int newBlocks = RNG.nextInt(3);
        if (newBlocks > 0) {
            grid.addNewBlocks(newBlocks);
        }
        updateGrid();
        checkAvailableMoves();
    }

    private void checkAvailableMoves() {
        if (!grid.hasAvailableMoves()) {
            System.out.println("NO MOVES!");

            JOptionPane.showInternalMessageDialog(GUI.FRAME.getContentPane(), "You Lose!", "Oops", JOptionPane.ERROR_MESSAGE);
            reset();
        }

    }



    private void updateGrid() {
        gridPanel.removeAll();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                gridPanel.add(new NumPanel(grid.getNumber(i, j)));
            }
        }
        updateUI();
    }



    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

}
