package com.mcintyret.twenty48.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: tommcintyre
 * Date: 11/8/14
 */
public class GridPanel2 extends JPanel {

    private static final int BEVEL_PROPORTION = 20;

    private int rows = 4;

    private int cols = 4;


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

    }
}
