package net.doge.ui.widget.menu;

import net.doge.ui.MainFrame;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单分隔符自定义 UI
 * @Date 2020/12/13
 */
public class CustomSeparator extends JSeparator {
    private MainFrame f;

    public CustomSeparator(MainFrame f) {
        this.f = f;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        GraphicsUtil.srcOver(g2d, 0.1f);
        g2d.setColor(f.currUIStyle.getIconColor());
        g2d.drawLine(0, 0, getWidth(), 0);
    }
}
