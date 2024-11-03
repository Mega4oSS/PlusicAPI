package ru.artem.alaverdyan;

import ru.artem.alaverdyan.utilities.EConsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MixinProcessor {

    public static Map<String, ArrayList<Class<?>>> processMixins(List<Class<?>> mixinClasses) {
        Map<String, ArrayList<Class<?>>> mixinMap = new HashMap<>();//EConsole.RESET + EConsole.MAGENTA
        EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[PlusicInjector] Processing mixins." + EConsole.RESET);
        EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[PlusicInjector] Processing mixins.." + EConsole.RESET);
        EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[PlusicInjector] Processing mixins..." + EConsole.RESET);

        for (Class<?> clazz : mixinClasses) {
            // Получаем аннотацию Mixin
            Mixin mixin = clazz.getAnnotation(Mixin.class);
            if (mixin != null) {
                for (String path : mixin.value()) {
                    // Добавляем класс в HashMap по указанному пути
                    mixinMap.computeIfAbsent(path, k -> new ArrayList<>()).add(clazz);
                }
            }
        }

        EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[PlusicInjector] Successfully proceeded!" + EConsole.RESET);
        for (String s : mixinMap.keySet()) {
            EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin] " + s + " " + EConsole.RESET + EConsole.MAGENTA + inLine(mixinMap.get(s)) + EConsole.RESET);
        }
        return mixinMap;
    }

    public static String inLine(ArrayList<Class<?>> classes) {
        StringBuilder string = new StringBuilder("{ ");
        for (int i = 0; i < classes.size(); i++) {
            string.append(classes.get(i));
            if (i < classes.size() - 1) {
                string.append(", ");
            }
        }
        string.append(" }");
        return string.toString();
    }


}
