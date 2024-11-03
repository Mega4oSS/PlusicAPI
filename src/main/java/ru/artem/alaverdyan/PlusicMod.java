package ru.artem.alaverdyan;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlusicMod {
    private String name;
    private String author;
    private String version;
    private String root;

    public PlusicMod() {
    }

    public void preInit() {
    }

    public void create() {
    }

    public void render(SpriteBatch batch) {
    }

    public void dispose() {
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    //public ArrayList<Mixin> getMixins() {
        //    return mixins;
        //}
    //
            //public void setMixins(ArrayList<Mixin> mixins) {
        //    this.mixins = mixins;
        //}
}
