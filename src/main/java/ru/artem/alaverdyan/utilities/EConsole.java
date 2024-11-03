package ru.artem.alaverdyan.utilities;

import java.util.ArrayList;

public class EConsole {
    // ANSI-коды для цветов и атрибутов
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // Фоновый цвет
    public static final String BLACK_BG = "\u001B[40m";
    public static final String RED_BG = "\u001B[41m";
    public static final String GREEN_BG = "\u001B[42m";
    public static final String YELLOW_BG = "\u001B[43m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String MAGENTA_BG = "\u001B[45m";
    public static final String CYAN_BG = "\u001B[46m";
    public static final String WHITE_BG = "\u001B[47m";

    // Жирный текст
    public static final String BOLD = "\u001B[1m";

    public static void write(String text) {
        System.out.println(text);
    }

    public static void write(String color, String text) {
        System.out.println(color + text + RESET);
    }

    public static void write(ArrayList<String> color, ArrayList<String> text) {
        for (int i = 0; i < text.size(); i++) {
            System.out.println(color.get(i) + text.get(i).replaceAll("%cR", RESET) + RESET);
        }
    }

    public static void write(ArrayList<String> bgColor, ArrayList<String> color, ArrayList<String> text) {
        for (int i = 0; i < text.size(); i++) {
            System.out.println(bgColor.get(i) + color.get(i) + text.get(i).replaceAll("%cR", RESET) + RESET);
        }
    }

    public static void write(String bgColor, String color, String text) {
        System.out.println(bgColor + color + text + RESET);
    }

    public static void write(ArrayList<String> color, String text) {
        for (int i = 0; i < color.size(); i++) {
            System.out.println(text.replaceAll("%c" + i, color.get(i)).replaceAll("%cR", RESET) + RESET);
        }
    }

    public static void write(ArrayList<String> bgColor, ArrayList<String> color, String text) {
        for (int i = 0; i < color.size(); i++) {
            System.out.println(text.replaceAll("%c" + i, color.get(i)).replaceAll("%bgC" + i, bgColor.get(i)).replaceAll("%cR", RESET) + RESET);
        }
    }


    public static void writeGradient(String text, ArrayList<String> colors) {
        for (int i = 0; i < text.length(); i++) {
            // Вычисляем индекс цвета, используя модуль для плавного перехода
            String color = colors.get(i % colors.size());
            System.out.print(color + text.charAt(i));
        }
        System.out.println(RESET); // Сбрасываем цвет после завершения строки
    }

    public static void writeStacktrace(StackTraceElement[] stackTrace) {
        for (int i = 0; i < stackTrace.length; i++) {
            System.out.println(RED_BG + BLACK + stackTrace[i].toString() + RESET);
        }
    }
}

