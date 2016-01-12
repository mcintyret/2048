package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GamePanel extends JPanel implements GameListener {

    private static final int DEFAULT_ANIMATION_TIME_MILLIS = 100;
    private static final int MIN_ANIMATION_TIME_MILLIS = 30;
    private static final int MAX_ANIMATION_TIME_MILLIS = 600;

    /*
    Since we want the slider to represent 'speed' but in reality it represents 'animation time', we need to invert
    the values
     */
    private static int invertSliderAnimationTime(int value) {
        return MIN_ANIMATION_TIME_MILLIS + MAX_ANIMATION_TIME_MILLIS - value;
    }

    private static final int BLOCK_WIDTH = 100;
    private static final int GAME_SIZE = 4;
    private static final int GAME_WIDTH = BLOCK_WIDTH * GAME_SIZE;

    private static final int TOP_PANEL_HEIGHT = 50;


    private final JLabel scoreLabel = new JLabel("", SwingConstants.CENTER);

    private final Driver driver;

    public GamePanel(Driver driver) {
        setLayout(new BorderLayout());
        GridPanel gridPanel = new GridPanel(driver);
        add(gridPanel, BorderLayout.CENTER);

        this.driver = driver;
        driver.addGameListener(this);

        JPanel topPanel = new JPanel(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(scoreLabel, BorderLayout.NORTH);
        updateScoreLabel();
        topPanel.setPreferredSize(new Dimension(GAME_WIDTH, TOP_PANEL_HEIGHT));

        JPanel animationSliderPanel = new JPanel(new BorderLayout());
        animationSliderPanel.add(new JLabel("Animation speed:"), BorderLayout.WEST);

        JSlider animationSlider = new JSlider(MIN_ANIMATION_TIME_MILLIS, MAX_ANIMATION_TIME_MILLIS, invertSliderAnimationTime(DEFAULT_ANIMATION_TIME_MILLIS));
        gridPanel.setMoveTimeMillis(DEFAULT_ANIMATION_TIME_MILLIS);
        animationSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                gridPanel.setMoveTimeMillis(invertSliderAnimationTime(source.getValue()));
            }
        });
        animationSlider.setFocusable(false); // Don't capture arrow keys (since they're how we play the game!)
        animationSliderPanel.add(animationSlider, BorderLayout.CENTER);
        topPanel.add(animationSliderPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_WIDTH + TOP_PANEL_HEIGHT));
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + driver.getGrid().getScore() + "    |    Moves: " + driver.getGrid().getMoves());
    }

    @Override
    public void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean gameOver) {
        updateScoreLabel();
    }

    @Override
    public void onStart(List<ValuedPoint> initialBlocks) {
        // Don't care
    }
}
