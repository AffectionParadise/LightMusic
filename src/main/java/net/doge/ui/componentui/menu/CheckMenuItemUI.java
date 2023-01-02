package net.doge.ui.componentui.menu;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 菜单项元素标签自定义 UI
 * @Date 2020/12/13
 */
public class CheckMenuItemUI extends BasicMenuItemUI {

    public CheckMenuItemUI(Color foreColor) {
        selectionForeground = foreColor;
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
//        super.paintBackground(g, menuItem, bgColor);
    }
}
