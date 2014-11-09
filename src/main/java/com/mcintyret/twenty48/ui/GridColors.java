package com.mcintyret.twenty48.ui;

import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GridColors {

//    private static final Color[] COLORS = {Color.LIGHT_GRAY, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
//            Color.RED, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK};

    static final Color EMPTY_CELL_COLOR = new Color(192, 179, 165);
    static final Color BEZEL_COLOR = new Color(172, 157, 143);

    private static final Color DARK_FONT_COLOR = new Color(100, 91, 83);

    private static final Color[] COLORS = {
        null,
        new Color(234, 222, 210), // 2
        new Color(232, 217, 189), // 4
        new Color(240, 160, 97), // 8
        new Color(229, 120, 67), // 16
        new Color(242, 103, 77), // 32
        new Color(243, 73, 37), // 64
        new Color(234, 196, 86), // 128
        new Color(234, 194, 66), // 256
        new Color(236, 189, 50), // 512
        new Color(233, 186, 28), // 1024
        new Color(231, 184, 0), // 2048
        new Color(82, 213, 129), // 4096
        new Color(37, 178, 82), // 8192
        Color.BLUE};


    static Color getCellColor(int val) {
        return COLORS[log2nlz(val)];
    }

    static Color getFontColor(int val) {
        return val >= 8 ? Color.WHITE : DARK_FONT_COLOR;
    }

    private static int log2nlz(int bits) {
        return 31 - Integer.numberOfLeadingZeros(bits);
    }

}
