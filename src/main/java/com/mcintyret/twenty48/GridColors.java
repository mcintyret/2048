package com.mcintyret.twenty48;

import java.awt.*;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GridColors {

    private static final Color[] COLORS = {Color.LIGHT_GRAY, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            Color.RED, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK};

    static Color getColor(int val) {
        int res = 0;
        if (val > 0) {
            res += log2nlz(val);
        }
        return COLORS[res];
    }

    private static int log2nlz(int bits) {
        return 31 - Integer.numberOfLeadingZeros(bits);
    }

}
