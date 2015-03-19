package com.mcintyret.twenty48.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.core.GameListener;
import com.mcintyret.twenty48.core.Movement;
import com.mcintyret.twenty48.core.ValuedPoint;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GamePanel extends JPanel implements GameListener {

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
