package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GamePanel extends JPanel implements GameListener {

    private static final int BLOCK_WIDTH = 100;
    private static final int GAME_SIZE = 4;
    private static final int GAME_WIDTH = BLOCK_WIDTH * GAME_SIZE;

    private static final int SCORE_PANEL_HEIGHT = 25;

    private final JLabel scoreLabel = new JLabel();

    private final Driver driver;

    public GamePanel(Driver driver) {
        setLayout(new BorderLayout());
        GridPanel gridPanel = new GridPanel(driver);
        add(gridPanel, BorderLayout.CENTER);

        this.driver = driver;
        driver.addGameListener(this);

        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(scoreLabel);
        updateScoreLabel();
        topPanel.setPreferredSize(new Dimension(GAME_WIDTH, SCORE_PANEL_HEIGHT));

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_WIDTH + SCORE_PANEL_HEIGHT));
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + driver.getGrid().getScore());
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
