package com.mcintyret.twenty48;

import com.mcintyret.twenty48.bot.Bot;
import com.mcintyret.twenty48.bot.MoveStrategy;
import com.mcintyret.twenty48.core.Driver;
import com.mcintyret.twenty48.ui.GUI;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class Runner {

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, InterruptedException {
        List<Class<? extends MoveStrategy>> strategies = new ArrayList<>();
        new FastClasspathScanner().matchClassesImplementing(MoveStrategy.class, strategies::add).scan();

        if (strategies.isEmpty()) {
            runInteractiveGame();
            return;
        }

        System.out.println("Pick strategy");
        ListIterator<Class<? extends MoveStrategy>> it = strategies.listIterator();
        formatChoice(0, "Interactive");
        while (it.hasNext()) {
            formatChoice(it.nextIndex() + 1, it.next());
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            int choice = scanner.nextInt();
            if (choice < 0 || choice > strategies.size()) {
                System.out.println("Invalid choice: " + choice);
                continue;
            }
            if (choice == 0) {
                runInteractiveGame();
                return;
            } else {
                Class<? extends MoveStrategy> strategy = strategies.get(choice - 1);
                System.out.println(strategy + " chosen");
                formatChoice(0, "GUI");
                formatChoice(1, "Power");

                while (true) {
                    int mode = scanner.nextInt();
                    if (mode != 0 && mode != 1) {
                        System.out.println("Invalid choice. Choose GUI or Power mode");
                        continue;
                    }
                    if (mode == 0) {
                        runStrategyWithGui(strategy);
                    } else {
                        PowerRunner.runInPowerMode(strategy);
                    }
                    return;
                }
            }
        }
    }


    private static void runStrategyWithGui(Class<? extends MoveStrategy> strategyClass) throws IllegalAccessException, InstantiationException {
        MoveStrategy strategy = strategyClass.newInstance();
        Driver driver = new Driver();
        Bot bot = new Bot(driver, strategy);
        GUI.attachGui(driver);
        driver.start();
        bot.run();
    }

    private static void formatChoice(int index, Object value) {
        System.out.print(String.format("[%d]\t%s%n", index, value));
    }

    private static void runInteractiveGame() {
        Driver driver = new Driver();
        GUI.attachGui(driver);
        driver.start();
    }
}
