package ru.artem.alaverdyan.utilities;

import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(EConsole.class);


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

    private static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0001;
    private static final int DISABLE_NEWLINE_AUTO_RETURN = 0x0008;

    public static void enableAnsi() {
        int STD_OUTPUT_HANDLE = -11;
        WinNT.HANDLE hOut = Kernel32.INSTANCE.GetStdHandle(STD_OUTPUT_HANDLE);

        int mode = ENABLE_VIRTUAL_TERMINAL_PROCESSING | DISABLE_NEWLINE_AUTO_RETURN;

        try {
            Kernel32.INSTANCE.SetConsoleMode(hOut, mode);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static void writeStacktrace(Throwable cause, String localizedMsg, StackTraceElement[] stackTrace) {
        System.out.println(RED_BG + BLACK + localizedMsg + RESET);
        System.out.println(RED_BG + BLACK + cause + RESET);
        for (int i = 0; i < stackTrace.length; i++) {
            System.out.println(RED_BG + BLACK + stackTrace[i].toString() + RESET);
        }
    }
}

