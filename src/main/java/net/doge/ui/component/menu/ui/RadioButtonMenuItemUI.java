package net.doge.ui.component.menu.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 单选菜单项元素标签自定义 UI
 * @Date 2020/12/13
 */
public class RadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {

    public RadioButtonMenuItemUI(Color foreColor) {
        selectionForeground = foreColor;
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
//        super.paintBackground(g, menuItem, bgColor);
    }
}
