package com.mcintyret.twenty48;

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

    private int[][] grid;

    private final JPanel gridPanel = new JPanel();

    private final JPanel topPanel = new JPanel();

    private final JLabel scoreLabel = new JLabel();

    private int score = 0;

    public GridPanel() {
        reset();

        setLayout(new BorderLayout());
        add(gridPanel, BorderLayout.CENTER);
        gridPanel.setLayout(new GridLayout(grid.length, grid[0].length));

        add(topPanel, BorderLayout.NORTH);
        topPanel.add(scoreLabel);


        registerKeystroke("left", KeyEvent.VK_LEFT, this::shiftLeft, this::combineLeft);
        registerKeystroke("right", KeyEvent.VK_RIGHT, this::shiftRight, this::combineRight);
        registerKeystroke("up", KeyEvent.VK_UP, this::shiftUp, this::combineUp);
        registerKeystroke("down", KeyEvent.VK_DOWN, this::shiftDown, this::combineDown);
    }

    private void reset() {
        grid = new int[GUI.GAME_SIZE][GUI.GAME_SIZE];

        for (Point p : Utils.getNRandomPoints(GUI.GAME_SIZE, GUI.GAME_SIZE, 2)) {
            grid[p.x][p.y] = 2;
        }

        updateGrid();
        score = 0;
        updateScoreLabel();
    }

    private void registerKeystroke(String name, int keyEvent, Shifter shifter, Combiner combiner) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean changed = shifter.shift();
                changed |= combiner.combine();
                if (changed || shifter.shift()) {
                    afterMove();
                    updateScoreLabel();
                }
            }
        });
    }

    private boolean combineLeft() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length - 1; j++) {
                if (grid[i][j] != 0 && grid[i][j] == grid[i][j + 1]) {
                    changed = true;
                    grid[i][j] *= 2;
                    score += grid[i][j];
                    for (int k = j + 1; k < grid.length - 1; k++) {
                        grid[i][k] = grid[i][k + 1];
                    }
                    grid[i][grid[i].length - 1] = 0;
                }
            }
        }
        return changed;
    }

    private boolean combineRight() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            for (int j = grid[i].length - 1; j > 0; j--) {
                if (grid[i][j] != 0 && grid[i][j] == grid[i][j - 1]) {
                    changed = true;
                    grid[i][j] *= 2;
                    score += grid[i][j];
                    for (int k = j - 1; k > 0; k--) {
                        grid[i][k] = grid[i][k - 1];
                    }
                    grid[i][0] = 0;
                }
            }
        }
        return changed;
    }

    private boolean combineUp() {
        boolean changed = false;
        for (int j = 0; j < grid.length; j++) {
            for (int i = 0; i < grid.length - 1; i++) {
                if (grid[i][j] != 0 && grid[i][j] == grid[i + 1][j]) {
                    changed = true;
                    grid[i][j] *= 2;
                    score += grid[i][j];
                    for (int k = i + 1; k < grid.length - 1; k++) {
                        grid[k][j] = grid[k + 1][j];
                    }
                    grid[grid.length - 1][j] = 0;
                }
            }
        }
        return changed;
    }

    private boolean combineDown() {
        boolean changed = false;
        for (int j = 0; j < grid.length; j++) {
            for (int i = grid.length - 1; i > 0; i--) {
                if (grid[i][j] != 0 && grid[i][j] == grid[i - 1][j]) {
                    changed = true;
                    grid[i][j] *= 2;
                    score += grid[i][j];
                    for (int k = i - 1; k > 0; k--) {
                        grid[k][j] = grid[k - 1][j];
                    }
                    grid[0][j] = 0;
                }
            }
        }
        return changed;
    }

    private void afterMove() {
        addNewBlocks();
        updateGrid();
        checkAvailableMoves();
    }

    private void checkAvailableMoves() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    return;
                }
                if (i < grid.length - 1 && grid[i][j] == grid[i + 1][j]) {
                    return;
                }
                if (j < grid[i].length - 1 && grid[i][j] == grid[i][j + 1]) {
                    return;
                }
            }
        }
        System.out.println("NO MOVES!");

        JOptionPane.showInternalMessageDialog(GUI.FRAME.getContentPane(), "You Lose!", "Oops", JOptionPane.ERROR_MESSAGE);
        reset();

    }

    private void addNewBlocks() {
        int oneTwoOrThree = RNG.nextInt(3);

        if (oneTwoOrThree == 0) {
            return;
        }

        List<Point> free = newPointList();

        for (int i = 0; i < grid.length; i++) {
            if (grid[i][0] == 0) {
                free.add(new Point(i, 0));
            }
            if (grid[i][grid.length - 1] == 0) {
                free.add(new Point(i, grid.length - 1));
            }
        }

        for (int j = 1; j < grid.length - 1; j++) {
            if (grid[0][j] == 0) {
                free.add(new Point(0, j));
            }

            if (grid[grid.length - 1][j] == 0) {
                free.add(new Point(grid.length - 1, j));
            }
        }

        Collections.shuffle(free);

        Iterator<Point> it = free.iterator();

        setPoint(it.next());

        if (oneTwoOrThree == 2 && it.hasNext()) {
            setPoint(it.next());
        }
    }

    private void setPoint(Point point) {
        grid[point.x][point.y] = RNG.nextBoolean() ? 2 : 4;
    }

    private List<Point> newPointList() {
        int size = 2 * (grid.length + grid.length - 2);
        return new ArrayList<>(size);
    }

    private void updateGrid() {
        gridPanel.removeAll();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                gridPanel.add(new NumPanel(grid[i][j]));
            }
        }
        updateUI();
    }

    private boolean shiftLeft() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            int index = 0;
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > 0) {
                    if (index != j) {
                        grid[i][index] = grid[i][j];
                        changed = true;
                    }
                    index++;
                }
            }
            if (changed) {
                for (int j = index; j < grid[i].length; j++) {
                    grid[i][j] = 0;
                }
            }
        }
        return changed;
    }

    private boolean shiftRight() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            int index = grid[i].length - 1;
            for (int j = grid[i].length - 1; j >= 0; j--) {
                if (grid[i][j] > 0) {
                    if (index != j) {
                        grid[i][index] = grid[i][j];
                        changed = true;
                    }
                    index--;
                }
            }
            if (changed) {
                for (int j = index; j >= 0; j--) {
                    grid[i][j] = 0;
                }
            }
        }
        return changed;
    }

    private boolean shiftUp() {
        boolean changed = false;
        for (int j = 0; j < grid.length; j++) {
            int index = 0;
            for (int i = 0; i < grid.length; i++) {
                if (grid[i][j] > 0) {
                    if (index != i) {
                        grid[index][j] = grid[i][j];
                        changed = true;
                    }
                    index++;
                }
            }
            if (changed) {
                for (int i = index; i < grid.length; i++) {
                    grid[i][j] = 0;
                }
            }
        }
        return changed;
    }

    private boolean shiftDown() {
        boolean changed = false;
        for (int j = 0; j < grid.length; j++) {
            int index = grid.length - 1;
            for (int i = grid.length - 1; i >= 0; i--) {
                if (grid[i][j] > 0) {
                    if (index != i) {
                        grid[index][j] = grid[i][j];
                        changed = true;
                    }
                    index--;
                }
            }
            if (changed) {
                for (int i = index; i >= 0; i--) {
                    grid[i][j] = 0;
                }
            }
        }
        return changed;
    }

    interface Shifter {
        boolean shift();
    }

    interface Combiner {
        boolean combine();
    }

    private void printGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

}
