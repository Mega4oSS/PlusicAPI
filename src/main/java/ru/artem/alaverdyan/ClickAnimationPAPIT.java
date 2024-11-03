package ru.artem.alaverdyan;

import aoc.kingdoms.lukasz.map.diplomacy.DiplomacyManager;
import aoc.kingdoms.lukasz.menu.ClickAnimation;
import com.badlogic.gdx.graphics.Color;

public class ClickAnimationPAPIT extends ClickAnimation {
    public ClickAnimationPAPIT(int iX, int iY, int iWMax, int iHMax) {
        super(iX, iY, iWMax, iHMax);
    }

    @Override
    public Color getColor() {
        return DiplomacyManager.COLOR_WAR;
    }
}
