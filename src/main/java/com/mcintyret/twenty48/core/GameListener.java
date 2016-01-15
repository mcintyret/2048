package com.mcintyret.twenty48.core;

import java.util.List;

public interface GameListener {

    void onMove(List<Movement> movements, List<ValuedPoint> newPoints, boolean gameOver);

    default void onStart(List<ValuedPoint> initialBlocks) {
        // do nothing
    }
}
