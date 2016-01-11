package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.MoveDirection;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.Point;
import com.mcintyret.twenty48.core.ValuedPoint;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import static com.mcintyret.twenty48.Utils.sleepUninterruptibly;
import static com.mcintyret.twenty48.ui.GridColors.getCellColor;
import static com.mcintyret.twenty48.ui.GridColors.getFontColor;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GridPanel extends JPanel implements GameListener {

    private static final int FRAMES_PER_SECOND = 35;

    private static final float BEVEL_PROPORTION = 5.8F;

    private static final float INITIAL_NEW_BLOCK_SCALE = 0.2F;

    private volatile long framesPerMove;

    private volatile long sleepMillisPerFrame;

    private final Map<FloatPoint, ScaledValue> cells = Collections.synchronizedMap(new HashMap<>());

    private final Driver driver;

    private final Semaphore semaphore = new Semaphore(0);
    private final String UPDATE_THREAD_NAME = "GUI update thread";
    private final ExecutorService updateExec = Executors.newSingleThreadExecutor(r -> new Thread(r, UPDATE_THREAD_NAME));

    public GridPanel(Driver driver) {
        this.driver = driver;
        driver.addGameListener(this);

        registerKeystroke("left", KeyEvent.VK_LEFT, MoveDirection.LEFT);
        registerKeystroke("right", KeyEvent.VK_RIGHT, MoveDirection.RIGHT);
        registerKeystroke("up", KeyEvent.VK_UP, MoveDirection.UP);
        registerKeystroke("down", KeyEvent.VK_DOWN, MoveDirection.DOWN);
    }

    public void setMoveTimeMillis(long moveTimeMillis) {
        updateExec.submit(() -> {
            this.framesPerMove = Math.max(1L, (long) (FRAMES_PER_SECOND * (moveTimeMillis / 1000D)));
            this.sleepMillisPerFrame = moveTimeMillis / framesPerMove;
        });
    }

    @Override
    public void paint(Graphics g) {
        int rows = driver.getRows();
        int cols = driver.getCols();

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
        Map<FloatPoint, ScaledValue> cells;
        synchronized (this.cells) {
            // take a copy to avoid CMEs
            cells = new HashMap<>(this.cells);
        }

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

    @Override
    public void onMove(List<Movement> movements, List<ValuedPoint> added, boolean gameOver) {
        handleMoves(movements, added, gameOver);
    }

    @Override
    public void onStart(List<ValuedPoint> added) {
        handleMoves(emptyList(), added, false);
    }

    private boolean isUpdateThread() {
        return Thread.currentThread().getName().equals(UPDATE_THREAD_NAME);
    }


    private void handleMoves(List<Movement> movements, List<ValuedPoint> added, boolean gameOver) {
        if (isUpdateThread()) {
            doHandleMoves(movements, added, gameOver);
        } else {
            updateExec.submit(() -> {
                try {
                    doHandleMoves(movements, added, gameOver);
                } finally {
                    semaphore.release();
                }
            });
            semaphore.acquireUninterruptibly();
        }
    }

    private void doHandleMoves(List<Movement> movements, List<ValuedPoint> added, boolean gameOver) {
        List<FloatPoint> combined = new ArrayList<>();
        List<MovementInfo> movementInfos = movements.stream().map(MovementInfo::new).collect(toList());

        for (int i = 0; i < framesPerMove; i++) {
            for (MovementInfo movementInfo : movementInfos) {
                ScaledValue val = cells.remove(movementInfo.getLastPoint());

                FloatPoint next = movementInfo.getNextPoint();
                ScaledValue existing = cells.put(next, val);
                if (existing != null) {
                    // If two blocks have moved to the same cell they must have merged, and therefore have the same value
                    if (existing.value != val.value) {
                        throw new AssertionError("Illegal combination: " + val + " and " + existing);
                    }
                    ScaledValue newVal = new ScaledValue(val.value << 1, INITIAL_NEW_BLOCK_SCALE);
                    cells.put(next, newVal);
                    combined.add(next);
                }
            }
            updateGrid();
            sleepUninterruptibly(sleepMillisPerFrame);
        }

        List<FloatPoint> addedFps = addNewPoints(added);

        animateAddedAndCombined(combined, addedFps);

        if (gameOver) {
            JOptionPane.showInternalMessageDialog(GUI.FRAME.getContentPane(), "You Lose!", "Oops", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerKeystroke(String name, int keyEvent, MoveDirection moveDirection) {
        getInputMap().put(KeyStroke.getKeyStroke(keyEvent, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateExec.submit(() -> driver.move(moveDirection));
            }
        });
    }

    private void animateAddedAndCombined(List<FloatPoint> combined, List<FloatPoint> added) {
        if (!added.isEmpty() || !combined.isEmpty()) {
            SizeChangeInfo addedSci = new SizeChangeInfo(INITIAL_NEW_BLOCK_SCALE, 1.0F);
            SizeChangeInfo combinedSci = new SizeChangeInfo(INITIAL_NEW_BLOCK_SCALE, 1.2F); // TODO: needs to be dynamic, or at least based on bezel?

            for (int i = 0; i < framesPerMove; i++) {
                combined.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> combinedSci.nextScaledValue(sv)));
                added.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> addedSci.nextScaledValue(sv)));

                updateGrid();
                sleepUninterruptibly(sleepMillisPerFrame);
            }

            // finally set all the combined back to normal size
            combined.forEach(fp -> cells.computeIfPresent(fp, (p, sv) -> new ScaledValue(sv.value)));
            updateGrid();
        }
    }

    private List<FloatPoint> addNewPoints(List<ValuedPoint> points) {
        if (points.isEmpty()) {
            return emptyList();
        }

        List<FloatPoint> floatPoints = new ArrayList<>(points.size());
        for (ValuedPoint p : points) {
            FloatPoint fp = new FloatPoint(p.p);
            if (cells.put(fp, new ScaledValue(p.val, INITIAL_NEW_BLOCK_SCALE)) != null) {
                throw new AssertionError("Added to non-empty cell: " + p);
            }
            floatPoints.add(fp);
        }
        updateGrid();
        return floatPoints;
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

    private final class MovementInfo {
        protected Supplier<FloatPoint> nextPointSupplier;

        protected FloatPoint lastPoint;

        private MovementInfo(Movement movement) {
            lastPoint = new FloatPoint(movement.getFrom());
            if (movement.getFrom().x != movement.getTo().x) {
                float d = Math.abs(movement.getFrom().x - movement.getTo().x);
                float perFrame = d / framesPerMove;
                if (movement.getFrom().x > movement.getTo().x) {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x - perFrame, lastPoint.y);
                } else {
                    nextPointSupplier = () -> new FloatPoint(lastPoint.x + perFrame, lastPoint.y);
                }
            } else {
                float d = Math.abs(movement.getFrom().y - movement.getTo().y);
                float perFrame = d / framesPerMove;
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

    private class SizeChangeInfo {

        private final float increment;

        protected SizeChangeInfo(float startScale, float endScale) {
            this.increment = (endScale - startScale) / framesPerMove;
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
