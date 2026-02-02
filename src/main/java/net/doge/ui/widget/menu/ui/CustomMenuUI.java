package net.doge.ui.widget.menu.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 列表元素标签自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenuUI extends BasicMenuUI {

    public CustomMenuUI(Color foreColor) {
        selectionForeground = foreColor;
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {

    }
}
