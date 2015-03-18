package com.mcintyret.twenty48.bot;

import com.mcintyret.twenty48.core.Grid;

enum Move {
    UP {
        @Override
        void apply(Grid grid) {
            grid.moveUp();
        }
    },
    DOWN {
        @Override
        void apply(Grid grid) {
            grid.moveDown();
        }
    },
    LEFT {
        @Override
        void apply(Grid grid) {
            grid.moveLeft();
        }
    },
    RIGHT {
        @Override
        void apply(Grid grid) {
            grid.moveRight();
        }
    };

    abstract void apply(Grid grid);

}
