package ru.artem.alaverdyan;

import ru.artem.alaverdyan.utilities.EConsole;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.artem.alaverdyan.PlusicInjector.outputDir;

@SuppressWarnings("deprecation")
public class PlusicAPI {
    public static ArrayList<PlusicMod> mods;
    public static ArrayList<String> modPaths;
    public static ArrayList<Class<?>> mixins;
    public static ArrayList<RegClazz> clazzez;
    public static ArrayList<RegLib> libs = new ArrayList<>();



    public static void preInit() {
        EConsole.initializeColorCodes();
        mixins = new ArrayList<>();
        clazzez = new ArrayList<>();
        modPaths = new ArrayList<>();
        mods = new ArrayList<>();
        libs = new ArrayList<>();
        loadModsFromRoot();
        loadModsFromWorkshop();
        for (PlusicMod mod : mods) {
            EConsole.write(EConsole.GREEN + EConsole.BOLD + "[PlusicAPI] Pre-Initialization mod: " + EConsole.RESET + EConsole.GREEN + mod.getName() + ":" + mod.getVersion() + EConsole.RESET);
            mod.preInit();
        }
    }

    public static void registerLibrary(String libPath, PlusicMod mod) {
        RegLib lib = new RegLib(libPath, mod);
        libs.add(lib);
    }


    public static void registerClass(Class<?> clazz, PlusicMod mod) {
        clazzez.add(new RegClazz(clazz, modPaths.get(mods.indexOf(mod))));
    }

    public static void registerMixin(Class<?> mixin) {
        mixins.add(mixin);
    }

    public void init() {
        for (PlusicMod mod : mods) {
            EConsole.write(EConsole.GREEN + EConsole.BOLD + "[PlusicAPI] Initialization mod: " + EConsole.RESET + EConsole.GREEN + mod.getName() + ":" + mod.getVersion() + EConsole.RESET);
            mod.init();
        }
    }

    public static void loadModsFromRoot() {
        File modsDir = new File("mods");

        String[] directories = modsDir.list((current, name) -> new File(current, name).isDirectory());
        for (int d = 0; d < directories.length; d++) {
            EConsole.write(EConsole.BOLD + EConsole.YELLOW + "[ModLoader] Path to directory with mods: " + EConsole.RESET + EConsole.YELLOW + "mods" + File.separator + directories[d] + EConsole.RESET);
            EConsole.write("");
            File dir = new File("mods" + File.separator + directories[d]);
            List<File> lst = new ArrayList();
            File[] var2 = dir.listFiles();
            if (var2.length > 0) {
                int var3 = var2.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    File file = var2[var4];
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        lst.add(file);
                    }
                }

                for (int i = 0; i < lst.size(); ++i) {
                    File jar = new File("mods/" + ((File) lst.get(i)).getName());
                    String mainClass = "";

                    try {
                        EConsole.write(EConsole.YELLOW + EConsole.BOLD + "[ModLoader] Loading mod: " + EConsole.RESET + EConsole.YELLOW + jar.getAbsolutePath() + EConsole.RESET);
                        URL[] urls = new URL[]{new URL("jar:file:" + lst.get(i) + "!/")};
                        URLClassLoader cl = URLClassLoader.newInstance(urls);
                        if (cl.getResourceAsStream("modinfo.plus") == null) {
                            EConsole.write(EConsole.RED + EConsole.BOLD + "[ERROR] [Skipping] Error occurred while loading mod: " + EConsole.RESET + EConsole.RED + jar.getAbsolutePath() + EConsole.RESET);
                            continue;
                        }
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("modinfo.plus")));
                        String line = "";
                        String name = "Unknown";
                        String author = "Unknown";
                        String version = "1.0";

                        while ((line = bfr.readLine()) != null) {
                            if (line.split(": ")[0].equals("MainClass")) {
                                mainClass = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Name")) {
                                name = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Author")) {
                                author = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Version")) {
                                version = line.split(": ")[1];
                            }
                        }

                        PlusicMod mod = (PlusicMod) cl.loadClass(mainClass).newInstance();
                        mod.setName(name);
                        mod.setAuthor(author);
                        mod.setVersion(version);
                        mod.setRoot(lst.get(i).getParentFile().getAbsolutePath());
                        mods.add(mod);
                        modPaths.add(lst.get(i).getAbsolutePath());
                        EConsole.write("");
                        EConsole.write(EConsole.GREEN_BG + EConsole.BLACK + "[ModLoader] Successfully loaded modification: " + EConsole.RESET + EConsole.GREEN_BG + EConsole.GREEN + EConsole.BOLD + jar.getAbsolutePath() + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Modification Name: " + EConsole.RESET + EConsole.GREEN + name + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Author: " + EConsole.RESET + EConsole.GREEN + author + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Main class: " + EConsole.RESET + EConsole.GREEN + mainClass + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Version: " + EConsole.RESET + EConsole.GREEN + version + EConsole.RESET);
                        EConsole.write(EConsole.GREEN_BG + EConsole.BLACK + repeatChar('#', "[ModLoader] Successfully loaded modification: ".length()) + repeatChar('#', jar.getAbsolutePath().length()) + EConsole.RESET);
                    } catch (IOException | IllegalAccessException | InstantiationException |
                             ClassNotFoundException var13) {
                        throw new RuntimeException(var13);
                    }
                }
            }
        }
    }

    public static void loadModsFromWorkshop() {
        //System.out.println("o KURWA: " + Paths.get("").getParent().getParent().getFileName() + File.separator + "workshop" + File.separator + "content" + File.separator + "2772750");
        Path currentPath = Paths.get("").toAbsolutePath();
        // Проверка наличия родительских директорий
        Path twoDirectoriesBack;
        Path parentPath = currentPath.getParent();
        if (parentPath != null) {
            twoDirectoriesBack = parentPath.getParent();
            if (twoDirectoriesBack != null) {
            } else {
                EConsole.write(EConsole.YELLOW + "[PlusicAPI] Mods from workshop not loaded because.. Workshop folder doesn't found");
                return;
            }
        } else {
            EConsole.write(EConsole.YELLOW + "[PlusicAPI] Mods from workshop not loaded because.. Workshop folder doesn't found");
            return;
        }
        File modsDir = twoDirectoriesBack.resolve("workshop").resolve("content").resolve("2772750").toFile();
        if(!modsDir.exists()) {
            EConsole.write(EConsole.YELLOW + "[PlusicAPI] Mods from workshop not loaded because.. Workshop folder doesn't exists");
            return;
        }
        String[] directories = modsDir.list((current, name) -> new File(current, name).isDirectory());
        for (int d = 0; d < directories.length; d++) {
            EConsole.write(EConsole.BOLD + EConsole.YELLOW + "[ModLoader] Path to directory with mods: " + EConsole.RESET + EConsole.YELLOW + "mods" + File.separator + directories[d] + EConsole.RESET);
            EConsole.write("");
            File dir = new File(modsDir.getAbsoluteFile() + File.separator + directories[d]);
            List<File> lst = new ArrayList();
            File[] var2 = dir.listFiles();
            if (var2.length > 0) {
                int var3 = var2.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    File file = var2[var4];
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        lst.add(file);
                    }
                }

                for (int i = 0; i < lst.size(); ++i) {
                    File jar = new File("mods/" + ((File) lst.get(i)).getName());
                    String mainClass = "";

                    try {
                        EConsole.write(EConsole.YELLOW + EConsole.BOLD + "[ModLoader] Loading mod: " + EConsole.RESET + EConsole.YELLOW + jar.getAbsolutePath() + EConsole.RESET);
                        URL[] urls = new URL[]{new URL("jar:file:" + lst.get(i) + "!/")};
                        URLClassLoader cl = URLClassLoader.newInstance(urls);
                        if (cl.getResourceAsStream("modinfo.plus") == null) {
                            EConsole.write(EConsole.RED + EConsole.BOLD + "[ERROR] [Skipping] Error occurred while loading mod: " + EConsole.RESET + EConsole.RED + jar.getAbsolutePath() + EConsole.RESET);
                            continue;
                        }
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(cl.getResourceAsStream("modinfo.plus")));
                        String line = "";
                        String name = "Unknown";
                        String author = "Unknown";
                        String version = "1.0";

                        while ((line = bfr.readLine()) != null) {
                            if (line.split(": ")[0].equals("MainClass")) {
                                mainClass = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Name")) {
                                name = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Author")) {
                                author = line.split(": ")[1];
                            }

                            if (line.split(": ")[0].equals("Version")) {
                                version = line.split(": ")[1];
                            }
                        }

                        PlusicMod mod = (PlusicMod) cl.loadClass(mainClass).newInstance();
                        mod.setName(name);
                        mod.setAuthor(author);
                        mod.setVersion(version);
                        mod.setRoot(lst.get(i).getParentFile().getAbsolutePath());
                        mods.add(mod);
                        modPaths.add(lst.get(i).getAbsolutePath());
                        EConsole.write("");
                        EConsole.write(EConsole.GREEN_BG + EConsole.BLACK + "[ModLoader] Successfully loaded modification: " + EConsole.RESET + EConsole.GREEN_BG + EConsole.GREEN + EConsole.BOLD + jar.getAbsolutePath() + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Modification Name: " + EConsole.RESET + EConsole.GREEN + name + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Author: " + EConsole.RESET + EConsole.GREEN + author + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Main class: " + EConsole.RESET + EConsole.GREEN + mainClass + EConsole.RESET);
                        EConsole.write(EConsole.GREEN + EConsole.BOLD + "# Version: " + EConsole.RESET + EConsole.GREEN + version + EConsole.RESET);
                        EConsole.write(EConsole.GREEN_BG + EConsole.BLACK + repeatChar('#', "[ModLoader] Successfully loaded modification: ".length()) + repeatChar('#', jar.getAbsolutePath().length()) + EConsole.RESET);
                    } catch (IOException | IllegalAccessException | InstantiationException |
                             ClassNotFoundException var13) {
                        throw new RuntimeException(var13);
                    }
                }
            }
        }
    }

    static String repeatChar(char c, int length) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append(c);
        }
        return line.toString();
    }

    public static void created() {
        for (int i = 0; i < mods.size(); ++i) {
            ((PlusicMod) mods.get(i)).create();
        }

    }

    public static void dispose() {
        for (int i = 0; i < mods.size(); ++i) {
            ((PlusicMod) mods.get(i)).dispose();
        }

    }
}
