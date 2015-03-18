package com.mcintyret.twenty48.ui;

import static com.mcintyret.twenty48.Utils.sleepUninterruptibly;
import static com.mcintyret.twenty48.ui.GridColors.getCellColor;
import static com.mcintyret.twenty48.ui.GridColors.getFontColor;
import static java.util.Collections.emptyList;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.mcintyret.twenty48.core.Grid;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.Point;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GridPanel extends JPanel {

    private static final long MOVE_TIME_MILLIS = 30;

    private static final int FRAMES_PER_SECOND = 35;

    private static final long FRAMES_PER_MOVE = (long) (FRAMES_PER_SECOND * (MOVE_TIME_MILLIS / 1000D));

    private static final float BEVEL_PROPORTION = 5.8F;

    private static final int INITIAL_BLOCKS = 2;

    private static final long SLEEP_MILLIS_PER_FRAME = MOVE_TIME_MILLIS / FRAMES_PER_MOVE;

    private static final float INITIAL_NEW_BLOCK_SCALE = 0.2F;

    private final Map<FloatPoint, ScaledValue> cells = new HashMap<>();

    private final GamePanel gamePanel;

    private Grid grid;

    private final ExecutorService updateExec = Executors.newSingleThreadExecutor();

    public GridPanel(GamePanel gamePanel, Grid grid) {
        this.gamePanel = gamePanel;
        gamePanel.add(this, BorderLayout.CENTER);
        setGrid(grid);
    }

    public GridPanel(GamePanel gamePanel) {
        this(gamePanel, new Grid());
    }

    @Override
    public void paint(Graphics g) {
        int rows = grid.getRows();
        int cols = grid.getCols();

        // fill with bezel color
        g.setColor(GridColors.BEZEL_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Calculate the proportions
        float bevelsAcross = (BEVEL_PROPORTION * cols) + cols + 1;
        float bevelWidth = getWidth() / bevelsAcross;
        float cellWidth = BEVEL_PROPORTION * bevelWidth;

        float bevelsDown = (BEVEL_PROPORTION * rows) + rows + 1;
        float bevelHeight = getHeight() / bevelsDown;
        float cellHeight = BEVEL_PROPORTION * bevelHeight;

        // Draw empty cells
        g.setColor(GridColors.EMPTY_CELL_COLOR);
        for (int i = 0; i < rows; i++) {
            float startY = bevelHeight + (i * (cellHeight + bevelHeight));
            for (int j = 0; j < cols; j++) {
                float startX = bevelWidth + (j * (cellWidth + bevelWidth));
                g.fillRoundRect((int) startX, (int) startY, (int) cellWidth, (int) cellHeight, 10, 10);
            }
        }

        // Draw filled Cells
        for (Map.Entry<FloatPoint, ScaledValue> entry : cells.entrySet()) {
            ScaledValue value = entry.getValue();

            float scaledCellHeight = cellHeight * value.scale;
            float scaledCellWidth = cellWidth * value.scale;

            g.setColor(getCellColor(value.value));

            FloatPoint point = entry.getKey();
            float startY = bevelHeight + (point.x * (cellHeight + bevelHeight)) + (cellHeight * (1 - value.scale)) / 2;
            float startX = bevelWidth + (point.y * (cellWidth + bevelWidth)) + (cellWidth * (1 - value.scale)) / 2;

            g.fillRoundRect((int) startX, (int) startY, (int) scaledCellWidth, (int) scaledCellHeight, 10, 10);

            Font font = new Font("Arial", Font.BOLD, (int) (scaledCellHeight / 2));
            g.setColor(getFontColor(value.value));
            String text = Integer.toString(value.value);

            int textWidth = getFontMetrics(font).stringWidth(text);
            int textHeight = getFontMetrics(font).getAscent();

            g.setFont(font);
            g.drawString(text, (int) (startX + (scaledCellWidth - textWidth) / 2), (int) (startY + (scaledCellHeight + textHeight) / 2));
        }
    }


    private void reset() {
        setGrid(new Grid());
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
        cells.clear();

        registerKeystroke("left", KeyEvent.VK_LEFT, grid::moveLeft);
        registerKeystroke("right", KeyEvent.VK_RIGHT, grid::moveRight);
        registerKeystroke("up", KeyEvent.VK_UP, grid::moveUp);
        registerKeystroke("down", KeyEvent.VK_DOWN, grid::moveDown);

        grid.setMoveListener((movements) -> {
            if (!movements.isEmpty()) {
                List<MovementInfo> movementInfos = new ArrayList<>(movements.size());
                movements.forEach(m -> movementInfos.add(new MovementInfo(m)));

                List<FloatPoint> combined = new ArrayList<>();

                for (int i = 0; i < FRAMES_PER_MOVE; i++) {
                    for (MovementInfo movementInfo : movementInfos) {
                        ScaledValue val = cells.remove(movementInfo.getLastPoint());

                        FloatPoint next = movementInfo.getNextPoint();
                        //                                System.out.println(next);
                        ScaledValue existing = cells.put(next, val);
                        if (existing != null) {
                            if (existing.value != val.value) {
                                throw new AssertionError("Illegal combination: " + val + " and " + existing);
                            }
                            ScaledValue newVal = new ScaledValue(val.value << 1, INITIAL_NEW_BLOCK_SCALE);
                            gamePanel.incrementScore(newVal.value);
                            cells.put(next, newVal);
                            combined.add(next);
                        }
                    }
                    updateGrid();
                    sleepUninterruptibly(SLEEP_MILLIS_PER_FRAME);
                }

                List<FloatPoint> added = addNewBlocksAfterMove();

                animateAddedAndCombined(combined, added);

                checkAvailableMoves();
                gamePanel.onMoveEnd();
            }
        });

        updateExec.execute(() -> {
            animateAddedAndCombined(emptyList(), addNewBlocks(INITIAL_BLOCKS));
            updateGrid();
        });

        gamePanel.reset();
    }

    private void registerKeystroke(String name, int keyEvent, Runnable mover) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExec.execute(mover::run);
            }
        });
    }

    private void animateAddedAndCombined(List<FloatPoint> combined, List<FloatPoint> added) {
        if (!added.isEmpty() || !combined.isEmpty()) {
            SizeChangeInfo addedSci = new SizeChangeInfo(INITIAL_NEW_BLOCK_SCALE, 1.0F);
            SizeChangeInfo combinedSci = new SizeChangeInfo(INITIAL_NEW_BLOCK_SCALE, 1.2F); // TODO: needs to be dynamic, or at least based on bezel?

            for (int i = 0; i < FRAMES_PER_MOVE; i++) {
                combined.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> combinedSci.nextScaledValue(sv)));
                added.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> addedSci.nextScaledValue(sv)));

                updateGrid();
                sleepUninterruptibly(SLEEP_MILLIS_PER_FRAME);
            }

            // finally set all the combined back to normal size
            combined.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> new ScaledValue(sv.value)));
            updateGrid();
        }
    }


    private List<FloatPoint> addNewBlocksAfterMove() {
        return addNewBlocks(1);
    }

    private List<FloatPoint> addNewBlocks(int n) {
        List<Point> points = grid.addNewBlocks(n);

        List<FloatPoint> floatPoints = new ArrayList<>(points.size());
        for (Point p : points) {
            FloatPoint fp = new FloatPoint(p);
            if (cells.put(new FloatPoint(p), new ScaledValue(grid.getNumber(p.x, p.y), INITIAL_NEW_BLOCK_SCALE)) != null) {
                throw new AssertionError("Added to non-empty cell: " + p);
            }
            floatPoints.add(fp);
        }
        updateGrid();
        return floatPoints;
    }

    private void checkAvailableMoves() {
        if (!grid.hasAvailableMoves()) {
            JOptionPane.showInternalMessageDialog(GUI.FRAME.getContentPane(), "You Lose!", "Oops", JOptionPane.ERROR_MESSAGE);
            reset();
        }
    }

    private static final class FloatPoint {
        private static final float EPSILON = 0.00001F;
        private final float x;
        private final float y;

        private FloatPoint(float x, float y) {
            if (Math.abs(x - Math.round(x)) < EPSILON) {
                x = Math.round(x);
            }
            if (Math.abs(y - Math.round(y)) < EPSILON) {
                y = Math.round(y);
            }

            this.x = x;
            this.y = y;
        }

        private FloatPoint(Point point) {
            this(point.x, point.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FloatPoint that = (FloatPoint) o;

            if (Float.compare(that.x, x) != 0) return false;
            if (Float.compare(that.y, y) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
            result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
            return result;
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    }


    private static final class MovementInfo {
        protected Supplier<FloatPoint> nextPointSupplier;

        protected FloatPoint lastPoint;

        private MovementInfo(Movement movement) {
            lastPoint = new FloatPoint(movement.getFrom());
            if (movement.getFrom().x != movement.getTo().x) {
                float d = Math.abs(movement.getFrom().x - movement.getTo().x);
                float perFrame = d / FRAMES_PER_MOVE;
                if (movement.getFrom().x > movement.getTo().x) {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x - perFrame, lastPoint.y);
                } else {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x + perFrame, lastPoint.y);
                }
            } else {
                float d = Math.abs(movement.getFrom().y - movement.getTo().y);
                float perFrame = d / FRAMES_PER_MOVE;
                if (movement.getFrom().y > movement.getTo().y) {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x, lastPoint.y - perFrame);
                } else {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x, lastPoint.y + perFrame);
                }
            }
        }

        protected FloatPoint getLastPoint() {
            return lastPoint;
        }

        protected FloatPoint getNextPoint() {
            FloatPoint next = nextPointSupplier.get();
            lastPoint = next;
            return next;
        }
    }

    private static class SizeChangeInfo {

        private final float increment;

        protected SizeChangeInfo(float startScale, float endScale) {
            this.increment = (endScale - startScale) / FRAMES_PER_MOVE;
        }

        public ScaledValue nextScaledValue(ScaledValue in) {
            return new ScaledValue(in.value, in.scale + increment);
        }
    }

    private void updateGrid() {
        revalidate();
        repaint();
    }

    private static class ScaledValue {

        private final int value;

        private final float scale;

        private ScaledValue(int value, float scale) {
            this.value = value;
            this.scale = scale;
        }

        private ScaledValue(int value) {
            this(value, 1.0F);
        }
    }
}
