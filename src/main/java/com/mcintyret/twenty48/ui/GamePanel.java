package com.mcintyret.twenty48.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GamePanel extends JPanel {

    private final JLabel scoreLabel = new JLabel();

    private int score = 0;

    public GamePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(scoreLabel);
    }

    public void incrementScore(int val) {
        score += val;
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    public void onMoveEnd() {
        updateScoreLabel();
    }

    public void reset() {
        score = 0;
        updateScoreLabel();
    }

}
