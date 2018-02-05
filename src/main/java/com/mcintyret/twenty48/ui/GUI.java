package com.mcintyret.twenty48.ui;

import com.mcintyret.twenty48.bot.Bot;
import com.mcintyret.twenty48.bot.MoveStrategy;
import com.mcintyret.twenty48.core.Driver;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: tommcintyre
 * Date: 6/29/14
 */
public class GUI {

    static final JFrame FRAME = new JFrame("2048");

    public static void main(String[] args) {

        Driver driver = new Driver();
        attachGui(driver);
        driver.start();

        if (args.length > 0) {
            MoveStrategy moveStrategy = instantiateMoveStrategy(args);

            Bot bot = new Bot(driver, moveStrategy);
            bot.run();
        }
    }

    public static void attachGui(Driver driver) {
        FRAME.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GamePanel gamePanel = new GamePanel(driver);
        FRAME.getContentPane().add(gamePanel);

        EventQueue.invokeLater(() -> {
            FRAME.pack();
            FRAME.setSize(gamePanel.getSize());
            FRAME.setLocationRelativeTo(null);
            FRAME.setVisible(true);
        });
    }

    private static void errorMessageAndExit(String message) {
        JOptionPane.showMessageDialog(GUI.FRAME.getContentPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(4);
    }

    private static MoveStrategy instantiateMoveStrategy(String[] moveStrategyClassAndArgs) {
        String moveStrategyClass = moveStrategyClassAndArgs[0];
        Class<MoveStrategy> clazz = null;
        try {
            clazz = (Class<MoveStrategy>) Class.forName(moveStrategyClass);

            if (!MoveStrategy.class.isAssignableFrom(clazz)) {
                errorMessageAndExit("Class '" + moveStrategyClass + "' does not implement " + MoveStrategy.class);
            }
        } catch (ClassNotFoundException e) {
            errorMessageAndExit("MoveStrategy implementation Class '" + moveStrategyClass + "' not on classpath");
        }

        int paramCount = moveStrategyClassAndArgs.length - 1;

        Constructor<MoveStrategy>[] ctors = (Constructor<MoveStrategy>[]) clazz.getConstructors();
        String[] args = paramCount == 0 ? new String[0] : Arrays.copyOfRange(moveStrategyClassAndArgs, 1, moveStrategyClassAndArgs.length);

        List<Constructor<MoveStrategy>> possibleCtors = new ArrayList<>();
        for (Constructor<MoveStrategy> ctor : ctors) {
            if (ctor.getParameterCount() == paramCount) {
                possibleCtors.add(ctor);
            }
        }

        if (possibleCtors.isEmpty()) {
            errorMessageAndExit("No Constructors for '" + moveStrategyClass + "' match given params of " + Arrays.toString(args));
        } else if (possibleCtors.size() > 1) {
            errorMessageAndExit("Not sure which Constructor to use for '" + moveStrategyClass + "', params: " + Arrays.toString(args));
        }
        Constructor<MoveStrategy> ctor = possibleCtors.get(0);
        Object[] argObjects = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            Object converted = null;
            try {
                converted = convert(args[i], ctor.getParameterTypes()[i]);
            } catch (RuntimeException e) {
                errorMessageAndExit("Error converting '" + moveStrategyClass + "' Constructor arg index " + i + ": " +
                    args[i] + " to type: " + ctor.getParameterTypes()[i] + ": " + e.getMessage());
            }
            argObjects[i] = converted;
        }

        try {
            return ctor.newInstance(argObjects);
        } catch (InstantiationException e) {
            errorMessageAndExit("Class '" + moveStrategyClass + "' cannot be instantiated");
        } catch (IllegalAccessException e) {
            errorMessageAndExit("Constructor on class '" + moveStrategyClass + "' is not accessible");
        } catch (InvocationTargetException e) {
            errorMessageAndExit("Constructor on class '" + moveStrategyClass + "' given args " + Arrays.toString(args) + " threw exception: " +
                e.getClass() + "(" + e.getMessage() + ")");
        }

        throw new AssertionError("Can't get here");
    }

    private static Object convert(String arg, Class<?> clazz) {
        if (clazz == String.class) {
            return arg;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return Integer.valueOf(arg);
        }
        if (clazz == long.class || clazz == Long.class) {
            return Long.valueOf(arg);
        }
        if (clazz == float.class || clazz == Float.class) {
            return Float.valueOf(arg);
        }
        if (clazz == double.class || clazz == Double.class) {
            return Double.valueOf(arg);
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return Byte.valueOf(arg);
        }
        if (clazz == short.class || clazz == Short.class) {
            return Short.valueOf(arg);
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return Boolean.valueOf(arg);
        }
        if (clazz == char.class || clazz == Character.class) {
            if (arg.length() != 1) {
                throw new IllegalArgumentException("String '" + arg + "' is not a single character");
            }
            return arg.charAt(0);
        }

        throw new IllegalArgumentException("Unsupported target class: " + clazz);
    }

}
