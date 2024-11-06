package ru.artem.alaverdyan;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import javassist.*;
import ru.artem.alaverdyan.injections.AfterCall;
import ru.artem.alaverdyan.injections.DirectInject;
import ru.artem.alaverdyan.injections.Inject;
import ru.artem.alaverdyan.utilities.EConsole;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.FileWriter;
import java.nio.file.Paths;


public class PlusicInjector {
    public static Map<String, ArrayList<Class<?>>> mixins;
    public static ArrayList<RegClazz> newClazzez;

    public static String Version = "1.0.1.d";

    static String outputDir = ".plusicapi/output_classes";  // Директория для извлеченных классов
    static String mainDir = ".plusicapi"; // Путь к папке PlusicAPI
    static String modifiedJarPath = ".plusicapi/modifiedfile.jar"; // путь к модифицированному JAR файлу
    static String infoFilePath = ".plusicapi/info.txt"; // путь к файлу с информацией о версии
    static String jarPath;

    public static void main(String[] args) {

        String[] logo = new String[5];
        logo[0] = " ▄▄▄·▄▄▌  ▄• ▄▌.▄▄ · ▀   ▄▄·      ▄▄▄·  ▄▄▄·▀  ";
        logo[1] = "▐█ ▄███•  █•██▌▐█ ▀. ██ ▐█ ▌•    ▐█ ▀█ ▐█ ▄███ ";
        logo[2] = " ██▀·██•  █▌▐█▌▄▀▀▀█▄▐█·██ ▄▄    ▄█▀▀█  ██▀·▐█·";
        logo[3] = "▐█•·•▐█▌▐▌▐█▄█▌▐█▄•▐█▐█▌▐███▌    ▐█ •▐▌▐█•·•▐█▌";
        logo[4] = ".▀   .▀▀▀  ▀▀▀  ▀▀▀▀ ▀▀▀·▀▀▀      ▀  ▀ .▀   ▀▀▀";


        if (args.length >= 2 && args[0].equals("-game")) {
            File file = new File(args[1]);
            if (!file.exists()) {
                EConsole.write(EConsole.RED_BG, EConsole.BLACK, "[ERROR] File not found: " + args[1]);
                EConsole.write(EConsole.RED_BG, EConsole.BLACK, "[ERROR] Exiting...");
                return;
            }
            jarPath = args[1];
        } else {
            // Поиск файла "aoh3" в корне текущей директории
            jarPath = findFile("aoh3");
            if (jarPath == null) {
                EConsole.write(EConsole.RED_BG, EConsole.BLACK, "[ERROR] Game not found: \"aoh3\"");
                EConsole.write(EConsole.RED_BG, EConsole.BLACK, "[ERROR] Exiting...");
                return;
            }
        }


        try {
            EConsole.write(EConsole.RED_BG, EConsole.RED + EConsole.BOLD, PlusicAPI.repeatChar('#', logo[0].length()));
            for (String s : logo) {
                EConsole.write(EConsole.RED, s);
            }

            EConsole.write(EConsole.RED_BG, EConsole.RED + EConsole.BOLD, PlusicAPI.repeatChar('#', logo[0].length()));
            EConsole.write(EConsole.RED, "[INFO] Version: " + Version);
            EConsole.write(EConsole.RED, "[INFO] By Artem Alaverdyan aka Mega4oSS");
            EConsole.write("");
            File dir = new File(mainDir);
            if (!dir.exists() && !dir.mkdirs()) {
                System.out.println("Error create folder: " + mainDir);
                return;
            }
            File info = new File(infoFilePath);
            boolean created = false;
            try {
                created = info.createNewFile();
            } catch (IOException e) {
                System.err.println("Error create file: " + infoFilePath);
                e.printStackTrace();
            }

            if (created) {
                try {
                    List<String> lines = Files.readAllLines(Paths.get(infoFilePath));
                    if (lines.isEmpty()) {
                        EConsole.write(EConsole.RED, "[PlusicInjector] The version does not match.");
                        try (FileWriter writer = new FileWriter(infoFilePath)) {
                            writer.write("Version: " + Version);
                        } catch (IOException e) {
                            System.err.println("Error write file: " + infoFilePath);
                            e.printStackTrace();
                        }
                        EConsole.write(EConsole.MAGENTA, "[PlusicInjector] Updating files...");
                        updateFiles();
                    }
                    String fileContent = lines.get(0).trim();

                    if (fileContent.startsWith("Version: ") && fileContent.substring(9).equals(Version)) {
                        EConsole.write(EConsole.MAGENTA, "[PlusicInjector] The version is match");
                    } else {
                        EConsole.write(EConsole.RED, "[PlusicInjector] The version does not match.");
                        try (FileWriter writer = new FileWriter(infoFilePath)) {
                            writer.write("Version: " + Version);
                        } catch (IOException e) {
                            System.err.println("Error write file: " + infoFilePath);
                            e.printStackTrace();
                        }
                        EConsole.write(EConsole.MAGENTA, "[PlusicInjector] Updating files...");
                        updateFiles();
                    }
                } catch (IOException | IndexOutOfBoundsException e) {
                    EConsole.write(EConsole.YELLOW, "[PlusicInjector] Error updating files: " + e.getMessage());
                }
            }


            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Starting game");
            runJar(modifiedJarPath);
        } catch (Exception e) {
            EConsole.writeStacktrace(e.getCause(), e.getLocalizedMessage(), e.getStackTrace());
        }

    }

    private static void updateFiles() {
        try {
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Extracting: " + jarPath);
            extractJar(jarPath, outputDir);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Extracted to: " + outputDir);

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Pre-Initialization Plusic API");
            PlusicAPI.preInit();
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Plusic API Pre-Initialized");

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Processing mixins");
            mixins = MixinProcessor.processMixins(PlusicAPI.mixins);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Mixins proceeded");

            newClazzez = new ArrayList<>();
            newClazzez.add(new RegClazz(PlusicAPI.class, null));
            newClazzez.add(new RegClazz(EConsole.class, null));
            newClazzez.add(new RegClazz(PlusicMod.class, null));
            newClazzez.add(new RegClazz(RegClazz.class, null));
            newClazzez.addAll(PlusicAPI.clazzez);
            newClazzez.add(new RegClazz(ClickAnimationPAPIT.class, null));
            newClazzez.add(new RegClazz(PlusicAPIText.class, null));
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Registration log4j library...");
            extractLib(outputDir);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Registration classes...");
            saveClassesToFiles(newClazzez, outputDir);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Classes registered");

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Injections mixins");
            modifyClasses(outputDir, jarPath);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Mixins injected");

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Recompiling game");
            createJar(outputDir, modifiedJarPath);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Recompiled " + outputDir + ";" + modifiedJarPath);
            deleteDir(new File(outputDir));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void extractLib(String outputDir) throws IOException {
        // Создание директории для извлечения классов
        boolean created = new File(outputDir).mkdirs();
        ProcessBuilder pb = new ProcessBuilder("jar", "xf", PlusicInjector.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "), "ch", "logback.xml", "org");
        pb.directory(new File(outputDir));
        pb.inheritIO();
        if (created || new File(outputDir).exists()) {
            Process process = pb.start();
            try {
                process.waitFor(); // Подождите завершения процесса
            } catch (InterruptedException e) {
                EConsole.write(EConsole.RED, e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        } else {
            EConsole.write(EConsole.RED, "[ERROR] Output directory not created, check permissions or idk.");
        }
    }

    // Метод для сохранения скомпилированных классов в файлы
    private static void saveClassesToFiles(ArrayList<RegClazz> classList, String outputFolder) {
        createFolder(outputFolder);

        for (RegClazz clazz : classList) {
            try {
                EConsole.write(EConsole.YELLOW + EConsole.BOLD + "[PlusicInjector] Registration class: " + EConsole.RESET + EConsole.YELLOW + clazz.getClazz().getName() + ":" + clazz.getClassPath() + EConsole.RESET);
                ClassPool pool = ClassPool.getDefault();
                if (!(clazz.getClassPath() == null)) pool.insertClassPath(clazz.getClassPath());
                CtClass ctClass = pool.get(clazz.getClazz().getName());
                ctClass.writeFile(outputFolder);
                EConsole.write(EConsole.GREEN + EConsole.BOLD + "[PlusicInjector] Class successfully registered: " + EConsole.RESET + EConsole.GREEN + clazz.getClazz().getSimpleName() + EConsole.RESET);
            } catch (NotFoundException | IOException | CannotCompileException e) {
                EConsole.write(EConsole.RED, e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }
    }

    // Метод для создания папки
    private static void createFolder(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                EConsole.write(EConsole.BLUE + EConsole.BOLD + "[PlusicInjector] Folder created: " + folderName + EConsole.RESET);
            } else {
                EConsole.write(EConsole.CYAN + EConsole.BOLD + "[PlusicInjector] Failed to create folder: " + folderName + EConsole.RESET);
            }
        }
    }

    private static void extractJar(String jarPath, String outputDir) throws IOException {
        // Создание директории для извлечения классов
        boolean created = new File(outputDir).mkdirs();
        ProcessBuilder pb = new ProcessBuilder("jar", "xf", jarPath);
        pb.directory(new File(outputDir));
        pb.inheritIO();
        if (created) {
            Process process = pb.start();
            try {
                process.waitFor(); // Подождите завершения процесса
            } catch (InterruptedException e) {
                EConsole.write(EConsole.RED, e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        } else {
            EConsole.write(EConsole.RED, "[ERROR] Output directory not created, check permissions or idk.");
        }
    }

    private static void modifyClasses(String outputDir, String jarPath) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(jarPath);
        // Проходим по всем классам и модифицируем их
        //noinspection resource
        Files.walk(new File(outputDir).toPath())
                .filter(path -> path.toString().endsWith(".class") && path.toString().contains("output_classes\\aoc\\kingdoms\\"))
                .forEach(path -> {
                    try {
                        CtClass cc = pool.makeClass(Files.newInputStream(path));
                        if (cc.isFrozen()) {
                            cc.defrost(); // если класс заморожен
                        }
                        if (cc.getSimpleName().equals("MainMenu")) {
                            try {
                                CtConstructor body = cc.getDeclaredConstructor(new CtClass[]{});
                                String code = "menuElements.add(new ru.artem.alaverdyan.PlusicAPIText(this));";
                                body.insertAt(510, code);
                            } catch (NotFoundException | CannotCompileException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (cc.getSimpleName().equals("DesktopLauncher")) {
                            try {
                                CtMethod body = cc.getDeclaredMethod("main");
                                String code = "ru.artem.alaverdyan.PlusicAPI.preInit();";
                                body.insertAt(16, code);
                            } catch (NotFoundException | CannotCompileException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (inject(cc)) {
                            cc.writeFile(outputDir);
                            EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin] Registered new modified game class" + EConsole.RESET);
                        } else if (cc.getSimpleName().equals("MainMenu") || cc.getSimpleName().equals("DesktopLauncher")) {
                            cc.writeFile(outputDir);
                        }
                    } catch (Exception e) {
                        EConsole.write(EConsole.RED, e.getLocalizedMessage());
                    }
                });
    }

    private static boolean inject(CtClass cc) {
        boolean moded = false;
        if (mixins.containsKey(cc.getName())) {
            EConsole.write("");
            EConsole.write(EConsole.MAGENTA_BG + EConsole.BLACK + "[Mixin] Starting injection " + cc.getName() + EConsole.RESET);
            ArrayList<Class<?>> mixins = PlusicInjector.mixins.get(cc.getName());
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(mixins.get(0)));
            try {
                Paranamer paranamer = new BytecodeReadingParanamer();
                EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin] Mixins count: " + EConsole.RESET + EConsole.MAGENTA + mixins.size() + EConsole.RESET);
                for (int i = 0; i < mixins.size(); i++) {
                    Class<?> mixinClass = mixins.get(i);
                    EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin] Mixin " + EConsole.RESET + EConsole.MAGENTA + mixinClass.getSimpleName() + EConsole.RESET);
                    //Class<?> targetClass = mixin.value()[0];
                    for (int f = 0; f < mixinClass.getMethods().length; f++) {
                        if (mixinClass.getMethods()[f].isAnnotationPresent(Inject.class)) {
                            Inject inject = mixinClass.getMethods()[f].getAnnotation(Inject.class);
                            CtClass ctClassM = pool.get(mixinClass.getName());

                            CtMethod methodM = ctClassM.getDeclaredMethod(mixinClass.getMethods()[f].getName());

                            EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Injection " + EConsole.RESET + EConsole.MAGENTA + mixinClass.getSimpleName() + "." + methodM.getName() + "()" + EConsole.RESET);
                            String[] parameterNames = paranamer.lookupParameterNames(mixinClass.getMethods()[f]);
                            String afterMC = "";
                            if (inject.afterCall() == AfterCall.CONTINUE) {
                                afterMC = "continue;";
                            } else if (inject.afterCall() == AfterCall.BREAK) {
                                afterMC = "break;";
                            } else if (inject.afterCall() == AfterCall.RETURN) {
                                afterMC = "return;";
                            }
                            if (!inject.method()[0].equals(cc.getSimpleName())) {

                                CtClass[] parameters = new CtClass[inject.constructorParameters().length];
                                CtMethod method;
                                if (parameters.length > 0) {
                                    for (int icp = 0; icp < inject.constructorParameters().length; icp++) {
                                        parameters[icp] = pool.get(inject.constructorParameters()[icp].getName());
                                    }
                                    method = cc.getDeclaredMethod(inject.method()[0], parameters);
                                } else {
                                    method = cc.getDeclaredMethod(inject.method()[0]);
                                }
                                CtMethod newMethod = new CtMethod(methodM, cc, null);
                                String methodName = mixinClass.getMethods()[f].getName() + i;
                                newMethod.setName(methodName);
                                cc.addMethod(newMethod);
                                String returnVariable = "";
                                if(!inject.returnTo().to().isEmpty()) {
                                    returnVariable = inject.returnTo().to() + " = ";
                                }
                                switch (inject.at()[0].value()) {
                                    case "AFTER":
                                        method.insertAfter(returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                    case "BEFORE":
                                        method.insertBefore(returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                    case "BY":
                                        method.insertAt(inject.at()[0].by(), returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                }
                                EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Injected method " + EConsole.RESET + EConsole.MAGENTA + returnVariable + methodName + "(" + joinParameterNames(parameterNames) + "); " + EConsole.RESET + EConsole.MAGENTA + EConsole.BOLD + "to " + EConsole.RESET + EConsole.MAGENTA + cc.getSimpleName() + "." + method.getName() + "()" + EConsole.RESET);
                                EConsole.write("");
                            } else {
                                EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Injection " + mixinClass.getSimpleName() + "." + methodM.getName() + "()" + EConsole.RESET);

                                CtClass[] parameters = new CtClass[inject.constructorParameters().length];
                                CtConstructor method;
                                if (parameters.length > 0) {
                                    for (int icp = 0; icp < inject.constructorParameters().length; icp++) {
                                        parameters[icp] = pool.get(inject.constructorParameters()[icp].getName());
                                    }
                                    method = cc.getDeclaredConstructor(parameters);
                                } else {
                                    method = cc.getDeclaredConstructor(new CtClass[0]);
                                }
                                CtMethod newMethod = new CtMethod(methodM, cc, null);
                                String methodName = mixinClass.getMethods()[f].getName() + i;
                                newMethod.setName(methodName);
                                cc.addMethod(newMethod);

                                String returnVariable = "";
                                if(!inject.returnTo().to().isEmpty()) {
                                    returnVariable = inject.returnTo().to() + " = ";
                                }
                                switch (inject.at()[0].value()) {
                                    case "AFTER":
                                        method.insertAfter(returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                    case "BEFORE":
                                        method.insertBefore(returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                    case "BY":
                                        method.insertAt(inject.at()[0].by(), returnVariable + methodName + "(" + joinParameterNames(parameterNames) + ");" + afterMC);
                                        break;
                                }
                                EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Injected method " + EConsole.RESET + EConsole.MAGENTA + returnVariable + methodName + "(" + joinParameterNames(parameterNames) + "); " + EConsole.RESET + EConsole.MAGENTA + EConsole.BOLD + "to " + EConsole.RESET + EConsole.MAGENTA + cc.getSimpleName() + "()" + EConsole.RESET);
                                EConsole.write("");
                            }
                            moded = true;

                        }
                        if (mixinClass.getMethods()[f].isAnnotationPresent(DirectInject.class)) {
                            EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Direct injection: " + EConsole.RESET + EConsole.MAGENTA + mixinClass.getSimpleName() + EConsole.RESET);
                            if (mixinClass.getMethods()[f].getParameterCount() == 1) {
                                mixinClass.getMethods()[f].invoke(null, cc);
                                EConsole.write(EConsole.MAGENTA + EConsole.BOLD + "[Mixin|Injection] Directly injected: " + EConsole.RESET + EConsole.MAGENTA + mixinClass.getSimpleName() + "." + mixinClass.getMethods()[f].getName() + "(" + cc.getSimpleName().toLowerCase(Locale.ROOT) + ")" + EConsole.RESET);
                            } else {
                                EConsole.write(EConsole.RED + EConsole.BOLD + "[Mixin|Injection] Direct Injection doesn't have \"CtClass\" parameter: " + EConsole.RESET + EConsole.RED + mixinClass.getSimpleName() + "." + mixinClass.getMethods()[f].getName() + "(" + cc.getSimpleName().toLowerCase(Locale.ROOT) + ")" + EConsole.RESET);
                            }
                            moded = true;
                        }
                    }
                }

            } catch (NotFoundException | IllegalAccessException | InvocationTargetException |
                     CannotCompileException e) {
                EConsole.writeStacktrace(e.getCause(), e.getLocalizedMessage(), e.getStackTrace());
                throw new RuntimeException(e);
            }

            //как же сложно на сонное ебало кодить

            EConsole.write(EConsole.MAGENTA_BG + EConsole.BLACK + PlusicAPI.repeatChar('#', ("[Mixin] Starting injection " + cc.getName()).length()) + EConsole.RESET);
        }
        return moded;
    }

    public static String joinParameterNames(String[] parameterNames) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parameterNames.length; i++) {
            result.append(parameterNames[i]);
            if (i < parameterNames.length - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    private static void createJar(String outputDir, String modifiedJarPath) throws IOException {
        File manifestFile = new File(outputDir, "MANIFEST.MF");
        try (BufferedWriter writer = Files.newBufferedWriter(manifestFile.toPath())) {
            writer.write("Manifest-Version: 1.0");
            writer.newLine();
            writer.write("Main-Class: aoc.kingdoms.lukasz.jakowski.desktop.DesktopLauncher");
            writer.newLine();
            writer.newLine(); // Обязательно добавить пустую строку в конце
        }

        // Создание JAR с указанием манифеста
        ProcessBuilder pb = new ProcessBuilder("jar", "cfm", modifiedJarPath, manifestFile.getAbsolutePath(), "-C", outputDir, ".");
        pb.inheritIO();
        Process process = pb.start();
        try {
            process.waitFor(); // Подождите завершения процесса
        } catch (InterruptedException e) {
            EConsole.writeStacktrace(e.getCause(), e.getLocalizedMessage(), e.getStackTrace());
        }
    }

    private static void runJar(String modifiedJarPath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", modifiedJarPath);
        pb.inheritIO();
        pb.start();
    }

    public static String findFile(String baseName) {
        File exeFile = new File(baseName + ".exe");
        File jarFile = new File(baseName + ".jar");

        if (exeFile.exists()) {
            return exeFile.getAbsolutePath();
        } else if (jarFile.exists()) {
            return jarFile.getAbsolutePath();
        }

        return null;
    }
}