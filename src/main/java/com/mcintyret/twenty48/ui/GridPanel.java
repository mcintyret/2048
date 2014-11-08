package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.Movement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GridPanel extends JPanel {

    private static final Random RNG = new Random();
    private static final int INITIAL_BLOCKS = 2;

    private static final int FRAMES_PER_SECOND = 35;

    private static final long SLEEP_MILLIS_PER_FRAME = TimeUnit.SECONDS.toMillis(1) / FRAMES_PER_SECOND;

    private Grid grid;

    private final ExecutorService updateExec = Executors.newSingleThreadExecutor();

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

        grid.addNewBlocks(INITIAL_BLOCKS);

        updateGrid();
        score = 0;
        updateScoreLabel();
    }

    private void registerKeystroke(String name, int keyEvent, Supplier<List<Movement>> mover) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExec.execute(() -> {
                    List<Movement>  movements = mover.get(); // This calls one of the grid::move* methods
                    updateGrid();

                    if (!movements.isEmpty()) {
                        addNewBlocksAfterMove();
                    }
                    checkAvailableMoves();
                });
            }
        });
    }


    private void addNewBlocksAfterMove() {
        int newBlocks = RNG.nextInt(3);
        if (newBlocks > 0) {
            grid.addNewBlocks(newBlocks);
        }
        updateGrid();
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
        revalidate();
        repaint();
        try {
            Thread.sleep(SLEEP_MILLIS_PER_FRAME);
        } catch (InterruptedException e) {

        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

}
