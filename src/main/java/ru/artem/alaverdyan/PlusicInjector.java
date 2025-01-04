package ru.artem.alaverdyan;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import javassist.*;
import ru.artem.alaverdyan.injections.AfterCall;
import ru.artem.alaverdyan.injections.DirectInject;
import ru.artem.alaverdyan.injections.Inject;
import ru.artem.alaverdyan.utilities.EConsole;


import java.io.*;

import java.nio.file.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import java.util.Locale;
import java.util.Map;


public class PlusicInjector {
    public static Map<String, ArrayList<Class<?>>> mixins;
    public static ArrayList<RegClazz> newClazzez;

    public static String Version = "1.0.3.d";


    static final String OUTPUT_DIR = ".plusicapi/output_classes";  // Директория для извлеченных классов
    static String mainDir = ".plusicapi"; // Путь к папке PlusicAPI
    static String modifiedJarPath = ".plusicapi/modifiedfile.jar"; // путь к модифицированному JAR файлу
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
            jarPath = findFile("game");
            if (jarPath == null) {
                EConsole.write(EConsole.RED_BG, EConsole.BLACK, "[ERROR] Game not found: \"game\"");
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

            updateFiles();

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Starting game");
            runJar(modifiedJarPath);
        } catch (Exception e) {
            EConsole.writeStacktrace(e.getCause(), e.getLocalizedMessage(), e.getStackTrace());
        }

    }

    private static void updateVC(ArrayList<PlusicMod> mods) {
        File file = new File(mainDir, "info.vc");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < mods.size(); i++) {
                writer.write(mods.get(i).getName() + ";" + mods.get(i).getVersion());
                if (i < mods.size()) {
                    writer.newLine(); // Переход на новую строку
                }
            }
            System.out.println("VC Updated: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("VC error: " + e.getMessage());
        }
    }

    public static boolean compareVC(ArrayList<PlusicMod> mods) {
        File file = new File(mainDir, "info.vc");
        ArrayList<String> modsVC = new ArrayList<>();
        ArrayList<String> modsVCA = new ArrayList<>();

        if(!file.exists()) {
            return true;
        }
        for (int i = 0; i < mods.size(); i++) {
            modsVCA.add(mods.get(i).getName() + ";" + mods.get(i).getVersion());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Читаем строку за строкой и добавляем в список
            while ((line = reader.readLine()) != null) {
                modsVC.add(line);
            }
        } catch (IOException e) {
            System.err.println("VC Comparing Error: " + e.getMessage());
        }
        System.out.println(modsVCA);
        System.out.println(modsVC);

        if(modsVCA.equals(modsVC)) {
            System.out.println("Compared!");
            return false;
        } else return true;
    }

    private static void updateFiles() {
        try {
            PlusicAPI.preInit();
            if (!compareVC(PlusicAPI.mods)) {
                return;
            }
            updateVC(PlusicAPI.mods);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Plusic API Pre-Initialized");

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Extracting: " + jarPath);

            extractJar(jarPath, OUTPUT_DIR);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Extracted to: " + OUTPUT_DIR);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Pre-Initialization Plusic API");
            initLibs();
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Processing mixins");
            mixins = MixinProcessor.processMixins(PlusicAPI.mixins);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Mixins proceeded");

            newClazzez = new ArrayList<>();
            newClazzez.add(new RegClazz(PlusicAPI.class, null));
            newClazzez.add(new RegClazz(EConsole.class, null));
            newClazzez.add(new RegClazz(PlusicMod.class, null));
            newClazzez.add(new RegClazz(RegClazz.class, null));
            newClazzez.add(new RegClazz(RegLib.class, null));
            newClazzez.addAll(PlusicAPI.clazzez);
            newClazzez.add(new RegClazz(ClickAnimationPAPIT.class, null));
            newClazzez.add(new RegClazz(PlusicAPIText.class, null));
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Registration log4j library...");
            extractLib(OUTPUT_DIR);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Registration classes...");
            saveClassesToFiles(newClazzez, OUTPUT_DIR);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Classes registered");

            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Injections mixins");
            modifyClasses(OUTPUT_DIR, jarPath);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Mixins injected");
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Recompiling game");
            createJar(OUTPUT_DIR, modifiedJarPath);
            EConsole.write(EConsole.WHITE_BG, EConsole.BLACK, "[PlusicInjector] Recompiled " + OUTPUT_DIR + ";" + modifiedJarPath);
            deleteDir(new File(OUTPUT_DIR));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void initLibs() throws IOException {
        for(RegLib lib : PlusicAPI.libs ) {
            boolean created = new File(OUTPUT_DIR).mkdirs();
            ProcessBuilder pb = new ProcessBuilder("jar", "xf", PlusicAPI.modPaths.get(PlusicAPI.mods.indexOf(lib.getMod())), lib.libPath);
            EConsole.write(PlusicAPI.modPaths.get(PlusicAPI.mods.indexOf(lib.getMod())) + " LIB: " + lib.libPath);
            pb.directory(new File(OUTPUT_DIR));
            pb.inheritIO();
            if (created || new File(OUTPUT_DIR).exists()) {
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



    private static double calculateCurrentPercentage(int progress) {
        return progress + 1;
    }


    private static void extractJar(String jarPath, String outputDir) throws IOException, InterruptedException {
        // Создание директории для извлечения классов
        boolean created = new File(outputDir).mkdirs();
        ProcessBuilder pb = new ProcessBuilder("jar", "xf", jarPath);
        pb.directory(new File(outputDir));
        pb.inheritIO();
        if (created) {
            Process process = pb.start();

            int progress = 0;

            EConsole.write(" ");

            while (process.isAlive()) {
                TimeUnit.MILLISECONDS.sleep(300); // Ожидание перед следующим обновлением
                progress++;
                double currentPercent = calculateCurrentPercentage(progress); // Метод расчета процента выполнения
                EConsole.printProgressBar((int)(currentPercent)); // Обновляем прогресс-бар
            }

            try {
                process.waitFor(); // Подождите завершения процесса
                Thread.sleep(750);
                EConsole.printProgressBar(100);
                System.out.println(" ");
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
                .filter(path -> path.toString().endsWith(".class") && path.toString().contains("output_classes\\aoh\\kingdoms\\history\\"))
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
                        }else if (cc.getSimpleName().equals("DesktopLauncher")) {
                            try {
                                CtMethod body = cc.getDeclaredMethod("main");
                                String code = "ru.artem.alaverdyan.PlusicAPI.preInit();";
                                body.insertAt(16, code);
                            } catch (NotFoundException | CannotCompileException e) {
                                throw new RuntimeException(e);
                            }
                        } else if(cc.getSimpleName().equals("AA_Game")) {
                            try {
                                CtMethod body = cc.getDeclaredMethod("create");
                                String code = "ru.artem.alaverdyan.PlusicAPI.init();";
                                body.insertAfter(code);
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
            writer.write("Main-Class: aoh.kingdoms.history.mainGame.desktop.DesktopLauncher");
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
        ProcessBuilder pb = new ProcessBuilder("java", "-Xms2048M","-Xmx2048M","-XX:+UseG1GC","-XX:+ParallelRefProcEnabled","-XX:MaxGCPauseMillis=200","-XX:+UnlockExperimentalVMOptions","-XX:+DisableExplicitGC","-XX:+AlwaysPreTouch", "-XX:G1NewSizePercent=30","-XX:G1MaxNewSizePercent=40","-XX:G1HeapRegionSize=8M","-XX:G1ReservePercent=20","-XX:G1HeapWastePercent=5","-XX:G1MixedGCCountTarget=4","-XX:InitiatingHeapOccupancyPercent=15","-XX:G1MixedGCLiveThresholdPercent=90","-XX:G1RSetUpdatingPauseTimePercent=5","-XX:SurvivorRatio=32","-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1", "-jar", modifiedJarPath);
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