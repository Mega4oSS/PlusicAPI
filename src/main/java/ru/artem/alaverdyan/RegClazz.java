package ru.artem.alaverdyan;

public class RegClazz {
    private Class<?> clazz;
    private String classPath;

    public RegClazz(Class<?> clazz, String classPath) {
        this.clazz = clazz;
        this.classPath = classPath;
    }


    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
