package com.mcintyret.twenty48;

import javax.swing.*;
import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class NumPanel extends JPanel {

    private static final Color BEVEL_COLOR = Color.GRAY;

    private static final double BEVEL_PROPORTION = 1D / 12D;

    private static final Font FONT = new JLabel().getFont();

    private final String text;

    private final Color color;

    public NumPanel(int val) {
        this.color = GridColors.getColor(val);
        this.text = val == 0 ? "" : Integer.toString(val);
    }

    @Override
    public void paint(Graphics g) {
        // do the bevel
        g.setColor(BEVEL_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        // do the fill
        g.setColor(color);
        int bevelWidth = (int) (BEVEL_PROPORTION * getWidth());
        int bevelHeight = (int) (BEVEL_PROPORTION * getHeight());
        g.fillRoundRect(bevelWidth, bevelHeight, getWidth() - (2 * bevelWidth), getHeight() - (2 * bevelHeight), 10, 10);

        // do the text
        g.setColor(Color.BLACK);
        Font font = new Font(FONT.getName(), Font.PLAIN, getHeight() / 3);
        int width = getFontMetrics(font).stringWidth(text);

        g.setFont(font);
        // TODO: tweak so the text is centred.
        g.drawString(text, (getWidth() - width) / 2, getHeight() /2);
    }
}
