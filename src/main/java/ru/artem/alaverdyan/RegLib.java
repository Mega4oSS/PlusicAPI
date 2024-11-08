package ru.artem.alaverdyan;

import static ru.artem.alaverdyan.PlusicAPI.libs;

public class RegLib {
    PlusicMod mod;
    String libPath;

    public RegLib(String libPath, PlusicMod mod) {
        this.mod = mod;
        this.libPath = libPath;
    }

    public PlusicMod getMod() {
        return mod;
    }
}
