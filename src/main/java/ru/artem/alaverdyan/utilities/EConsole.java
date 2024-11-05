package ru.artem.alaverdyan.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fusesource.jansi.Ansi.*;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class EConsole {

    private static final Logger LOGGER = LoggerFactory.getLogger(EConsole.class);


    private static final Map<String, Runnable> colorCodes = new HashMap<>();


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


    public static void initializeColorCodes() {
        // Цвета переднего плана
        colorCodes.put("\u001B[30m", () -> Ansi.ansi().fg(Ansi.Color.BLACK));
        colorCodes.put("\u001B[31m", () -> Ansi.ansi().fg(Ansi.Color.RED));
        colorCodes.put("\u001B[32m", () -> Ansi.ansi().fg(Ansi.Color.GREEN));
        colorCodes.put("\u001B[33m", () -> Ansi.ansi().fg(Ansi.Color.YELLOW));
        colorCodes.put("\u001B[34m", () -> Ansi.ansi().fg(Ansi.Color.BLUE));
        colorCodes.put("\u001B[35m", () -> Ansi.ansi().fg(Ansi.Color.MAGENTA));
        colorCodes.put("\u001B[36m", () -> Ansi.ansi().fg(Ansi.Color.CYAN));
        colorCodes.put("\u001B[37m", () -> Ansi.ansi().fg(Ansi.Color.WHITE));

        // Цвета заднего плана
        colorCodes.put("\u001B[40m", () -> Ansi.ansi().bg(Ansi.Color.BLACK));
        colorCodes.put("\u001B[41m", () -> Ansi.ansi().bg(Ansi.Color.RED));
        colorCodes.put("\u001B[42m", () -> Ansi.ansi().bg(Ansi.Color.GREEN));
        colorCodes.put("\u001B[43m", () -> Ansi.ansi().bg(Ansi.Color.YELLOW));
        colorCodes.put("\u001B[44m", () -> Ansi.ansi().bg(Ansi.Color.BLUE));
        colorCodes.put("\u001B[45m", () -> Ansi.ansi().bg(Ansi.Color.MAGENTA));
        colorCodes.put("\u001B[46m", () -> Ansi.ansi().bg(Ansi.Color.CYAN));
        colorCodes.put("\u001B[47m", () -> Ansi.ansi().bg(Ansi.Color.WHITE));

        colorCodes.put("\u001B[0m", () -> Ansi.ansi().reset());
        colorCodes.put("\u001B[1m", () -> Ansi.ansi().bold());
    }

    public static String convert(String input) {
        Ansi ansiOutput = Ansi.ansi();
        int startIndex = 0;

        while (startIndex < input.length()) {
            int endIndex = input.indexOf('\u001B', startIndex);

            if (endIndex == -1) {
                ansiOutput.a(input.substring(startIndex)).reset();
                break;
            }

            ansiOutput.a(input.substring(startIndex, endIndex));

            int nextSemicolon = input.indexOf('m', endIndex + 2);
            if (nextSemicolon != -1) {
                String code = input.substring(endIndex, nextSemicolon + 1);

                Runnable converter = colorCodes.get(code);
                if (converter != null) {
                    converter.run();
                } else {
                    ansiOutput.a(code);
                }
            }

            startIndex = nextSemicolon + 1;
        }

        return ansiOutput.toString();
    }

    public static void write(String text) {
        AnsiConsole.systemInstall();
        System.out.println(text);
        AnsiConsole.systemUninstall();

    }

    public static void write(String color, String text) {
        AnsiConsole.systemInstall();

        System.out.println(convert(color + text + RESET) );

        AnsiConsole.systemUninstall();

    }

    public static void write(ArrayList<String> color, ArrayList<String> text) {
        AnsiConsole.systemInstall();


        for (int i = 0; i < text.size(); i++) {
            System.out.println(convert(color.get(i) + text.get(i).replaceAll("%cR", RESET) + RESET));
        }

        AnsiConsole.systemUninstall();

    }

    public static void write(ArrayList<String> bgColor, ArrayList<String> color, ArrayList<String> text) {
        AnsiConsole.systemInstall();


        for (int i = 0; i < text.size(); i++) {
            System.out.println(convert(bgColor.get(i) + color.get(i) + text.get(i).replaceAll("%cR", RESET) + RESET));
        }

        AnsiConsole.systemUninstall();

    }

    public static void write(String bgColor, String color, String text) {
        AnsiConsole.systemInstall();


        System.out.println(convert(bgColor + color + text + RESET));

        AnsiConsole.systemUninstall();

    }

    public static void write(ArrayList<String> color, String text) {
        AnsiConsole.systemInstall();

        for (int i = 0; i < color.size(); i++) {
            System.out.println(convert(text.replaceAll("%c" + i, color.get(i)).replaceAll("%cR", RESET) + RESET));
        }
        AnsiConsole.systemUninstall();

    }

    public static void write(ArrayList<String> bgColor, ArrayList<String> color, String text) {

        AnsiConsole.systemInstall();

        for (int i = 0; i < color.size(); i++) {
            System.out.println(convert(text.replaceAll("%c" + i, color.get(i)).replaceAll("%bgC" + i, bgColor.get(i)).replaceAll("%cR", RESET) + RESET));
        }

        AnsiConsole.systemUninstall();

    }


    public static void writeGradient(String text, ArrayList<String> colors) {
        AnsiConsole.systemInstall();


        for (int i = 0; i < text.length(); i++) {
            // Вычисляем индекс цвета, используя модуль для плавного перехода
            String color = colors.get(i % colors.size());
            System.out.print(convert(color + text.charAt(i)));
        }
        System.out.println(RESET); // Сбрасываем цвет после завершения строки

        AnsiConsole.systemUninstall();

    }

    public static void writeStacktrace(Throwable cause, String localizedMsg, StackTraceElement[] stackTrace) {
        AnsiConsole.systemInstall();


        System.out.println(convert(RED_BG + BLACK + localizedMsg + RESET));
        System.out.println(convert(RED_BG + BLACK + cause + RESET));
        for (int i = 0; i < stackTrace.length; i++) {
            System.out.println(convert(RED_BG + BLACK + stackTrace[i].toString() + RESET));
        }
        AnsiConsole.systemUninstall();

    }
}

