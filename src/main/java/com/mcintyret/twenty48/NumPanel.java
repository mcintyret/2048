package com.mcintyret.twenty48;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class NumPanel extends JPanel {

    private static final Font FONT = new JLabel().getFont();

    private final String text;

    private final Color color;

    public NumPanel(int val) {
        this.color = GridColors.getColor(val);
        this.text = val == 0 ? "" : Integer.toString(val);
        setBorder(new LineBorder(Color.BLACK));
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);

        g.drawRect(0, 0, getWidth(), getHeight());

        Font font = new Font(FONT.getName(), Font.PLAIN, getHeight() / 3);

        int width = getFontMetrics(font).stringWidth(text);

        g.setFont(font);
        g.drawString(text, (getWidth() - width) / 2, getHeight() /2);
    }
}
