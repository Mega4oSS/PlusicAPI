package ru.artem.alaverdyan;
import aoh.kingdoms.history.mainGame.CFG;
import aoh.kingdoms.history.mainGame.Game_Calendar;
import aoh.kingdoms.history.menu.Colors;
import aoh.kingdoms.history.menu.Menu;
import aoh.kingdoms.history.menu.MenuManager;
import aoh.kingdoms.history.menu_element.menuElementHover.*;
import aoh.kingdoms.history.menu_element.textStatic.Text_Static;
import aoh.kingdoms.history.textures.Images;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlusicAPIText extends Text_Static {
    private final Menu menu;
    public PlusicAPIText(Menu menu) {
        super("PlusicAPI", CFG.PADDING * 3, CFG.GAME_HEIGHT - (CFG.TEXT_HEIGHT * 2) - CFG.PADDING * 4, CFG.FONT_REGULAR_SMALL);
        this.menu = menu;
    }

    public void actionElement() {
        MenuManager.addClickAnimation(new ClickAnimationPAPIT(this.getPosX() + this.getWidth() / 2 + menu.getMenuPosX(), this.getPosY() + this.getHeight() / 2 + menu.getMenuPosY(), this.getWidth(), this.getHeight()));
    }

    @Override
    public void buildElementHover() {
        List<MenuElement_HoverElement> nElements = new ArrayList<>();
        List<MenuElement_HoverElement_Type> nData = new ArrayList<>();
        nData.add(new MenuElement_HoverElement_Type_TextTitle_BG_Center("Programmer", CFG.FONT_BOLD, Colors.HOVER_LEFT));
        nElements.add(new MenuElement_HoverElement(nData));
        nData.clear();
        nData.add(new MenuElement_HoverElement_Type_Button_TextBonus("Artem Alaverdyan", "", Images.world, CFG.FONT_BOLD, CFG.FONT_BOLD, Colors.HOVER_GOLD, Colors.HOVER_GOLD));
        nElements.add(new MenuElement_HoverElement(nData));
        nData.clear();
        nData.add(new MenuElement_HoverElement_Type_Button_TextBonus("Where multiplayer!?", "", Game_Calendar.IMG_MANPOWER, CFG.FONT_BOLD, CFG.FONT_BOLD, Colors.HOVER_LEFT, Colors.HOVER_LEFT));
        nElements.add(new MenuElement_HoverElement(nData));
        nData.clear();
        String[] desc = new String[3];
        desc[0] = "Yo-ho laba didi, but we no pirates!";
        desc[1] = "Waiting money on the steam: second day";
        desc[2] = "Fucking JavaAssist, teach self how to compile lambda's";
        boolean lineAdded = false;
        for (String s : desc) {
            if (s != null && !lineAdded) {
                lineAdded = true;
                nData.add(new MenuElement_HoverElement_Type_Line());
                nElements.add(new MenuElement_HoverElement(nData));
                nData.clear();
            }

            nData.add(new MenuElement_HoverElement_Type_Text_Desc(Objects.requireNonNull(s), CFG.FONT_REGULAR_SMALL, Colors.HOVER_LEFT2));
            nElements.add(new MenuElement_HoverElement(nData));
            nData.clear();
        }
        this.menuElementHover = new MenuElement_Hover(nElements);
    }

    @Override
    protected Color getColor(boolean isActive) {
        return !this.getIsHovered() && !isActive ? Colors.HOVER_RIGHT3 : Colors.HOVER_LEFT;
    }
}