package net.doge.ui.widget.menu;

import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单分隔符自定义 UI
 * @Date 2020/12/13
 */
public class CustomSeparator extends JSeparator {
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        GraphicsUtil.srcOver(g2d, 0.1f);
        g2d.setColor(getForeground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
