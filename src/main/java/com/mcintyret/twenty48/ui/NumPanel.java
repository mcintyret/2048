package com.mcintyret.twenty48.ui;

import javax.swing.*;
import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class NumPanel extends JPanel {

    private static final Color BEVEL_COLOR = Color.GRAY;

    private static final double BEVEL_PROPORTION = 1D / 20D;

    private static final Font FONT = new JLabel().getFont();

    private final String text;

    private final Color color;

    private final double[] fillProportionAnimations;

    private int fillIndex = 0;

    public NumPanel(int val, double... fillProportionAnimations) {
        this.color = GridColors.getColor(val);
        this.text = val == 0 ? "" : Integer.toString(val);
        this.fillProportionAnimations = fillProportionAnimations;
    }

    @Override
    public void paint(Graphics g) {
        // do the bevel
        paintBevel(g);

        // do the fill
        paintFill(g);

        // do the text
        paintText(g);
    }

    private void paintText(Graphics g) {
        g.setColor(Color.BLACK);
        Font font = new Font(FONT.getName(), Font.PLAIN, getHeight() / 3);
        int width = getFontMetrics(font).stringWidth(text);
        int height = getFontMetrics(font).getAscent();

        g.setFont(font);
        g.drawString(text, (getWidth() - width) / 2, (getHeight() + height) / 2);
    }

    private void paintFill(Graphics g) {
        double fillProportion = nextFillProportion();
        if (fillProportion < 1.0) {
            paintFill(g, 1.0, Color.LIGHT_GRAY);
        }
        paintFill(g, fillProportion, color);
    }

    private void paintFill(Graphics g, double fillProportion, Color color) {
        g.setColor(color);
        int bevelWidth = (int) (BEVEL_PROPORTION * getWidth());
        int bevelHeight = (int) (BEVEL_PROPORTION * getHeight());

        int rectWidth = (int) (fillProportion * getWidth() - (2 * bevelWidth));
        int rectHeight = (int) (fillProportion * getHeight() - (2 * bevelHeight));

        int startX = (getWidth() - rectWidth) / 2;
        int startY = (getHeight() - rectHeight) / 2;

        g.fillRoundRect(startX, startY, rectWidth, rectHeight, 10, 10);

    }

    private void paintBevel(Graphics g) {
        g.setColor(BEVEL_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public boolean animationRunning() {
        return fillIndex < fillProportionAnimations.length;
    }

    private double nextFillProportion() {
        if (fillIndex == fillProportionAnimations.length) {
            return 1.0D;
        } else {
            return fillProportionAnimations[fillIndex++];
        }
    }
}
