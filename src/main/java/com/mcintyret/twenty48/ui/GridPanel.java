package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.Point;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GridPanel extends JPanel {

    private static final int BEVEL_PROPORTION = 20;

    private static final Font FONT = new JLabel().getFont();

    private int rows = 4;

    private int cols = 4;

    private final Map<Point, Integer> cells = new HashMap<>();

    private final GamePanel gamePanel;

    public GridPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        reset();
    }

    @Override
    public void paint(Graphics g) {
        // fill with bevel color
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Calculate the proportions
        float bevelsAcross = (BEVEL_PROPORTION * cols) + cols + 1;
        float bevelWidth = getWidth() / bevelsAcross;
        float cellWidth = BEVEL_PROPORTION * bevelWidth;

        float bevelsDown = (BEVEL_PROPORTION * rows) + rows + 1;
        float bevelHeight = getHeight() / bevelsDown;
        float cellHeight = BEVEL_PROPORTION * bevelHeight;

        // Draw empty cells
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < rows; i++) {
            float startY = bevelHeight + (i * (cellHeight + bevelHeight));
            for (int j = 0; j < cols; j++) {
                float startX = bevelWidth + (j * (cellWidth + bevelWidth));
                g.fillRoundRect((int) startX, (int) startY, (int) cellWidth, (int) cellHeight, 10, 10);
            }
        }

        // Draw filled Cells
        Font font = new Font(FONT.getName(), Font.PLAIN, (int) (cellHeight / 3));
        for (Map.Entry<Point, Integer> entry : cells.entrySet()) {
            g.setColor(GridColors.getColor(entry.getValue()));

            float startY = bevelHeight + (entry.getKey().x * (cellHeight + bevelHeight));
            float startX = bevelWidth + (entry.getKey().y * (cellWidth + bevelWidth));

            g.fillRoundRect((int) startX, (int) startY, (int) cellWidth, (int) cellHeight, 10, 10);

            String text = Integer.toString(entry.getValue());
            g.setColor(Color.BLACK);

            int textWidth = getFontMetrics(font).stringWidth(text);
            int textHeight = getFontMetrics(font).getAscent();

            g.setFont(font);
            g.drawString(text, (int) (startX + (cellWidth - textWidth) / 2), (int) (startY + (cellHeight + textHeight) / 2));
        }
    }

    private static final Random RNG = new Random();
    private static final int INITIAL_BLOCKS = 2;

    private static final int FRAMES_PER_SECOND = 35;

    private static final long SLEEP_MILLIS_PER_FRAME = TimeUnit.SECONDS.toMillis(1) / FRAMES_PER_SECOND;

    private Grid grid;

    private final ExecutorService updateExec = Executors.newSingleThreadExecutor();


    private void reset() {
        grid = new Grid();
        cells.clear();

        registerKeystroke("left", KeyEvent.VK_LEFT, grid::moveLeft);
        registerKeystroke("right", KeyEvent.VK_RIGHT, grid::moveRight);
        registerKeystroke("up", KeyEvent.VK_UP, grid::moveUp);
        registerKeystroke("down", KeyEvent.VK_DOWN, grid::moveDown);

        addNewBlocks(INITIAL_BLOCKS);

        updateGrid();
        gamePanel.reset();
    }

    private void registerKeystroke(String name, int keyEvent, Supplier<java.util.List<Movement>> mover) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExec.execute(() -> {
                    List<Movement> movements = mover.get(); // This calls one of the grid::move* methods

                    for (Movement movement : movements) {
                        int val = cells.remove(movement.getFrom());

                        Integer existing = cells.put(movement.getTo(), val);
                        if (existing != null) {
                            if (existing != val) {
                                throw new AssertionError("Illegal combination: " + val + " and " + existing);
                            }
                            int newVal = val << 1;
                            gamePanel.incrementScore(newVal);
                            cells.put(movement.getTo(), newVal);
                        }
                    }

                    updateGrid();

                    if (!movements.isEmpty()) {
                        addNewBlocksAfterMove();
                    }
                    checkAvailableMoves();
                    gamePanel.onMoveEnd();
                });
            }
        });
    }


    private void addNewBlocksAfterMove() {
        int newBlocks = RNG.nextInt(3);
        if (newBlocks > 0) {
            addNewBlocks(newBlocks);
        }
    }

    private void addNewBlocks(int n) {
        List<Point> points = grid.addNewBlocks(n);
        for (Point point : points) {
            if (cells.put(point, grid.getNumber(point.x, point.y)) != null) {
                throw new AssertionError("Added to non-empty cell:  " + point);
            }
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
        revalidate();
        repaint();
    }

}
