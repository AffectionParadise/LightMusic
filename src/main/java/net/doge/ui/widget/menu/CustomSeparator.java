package net.doge.ui.widget.menu;

import net.doge.ui.MainFrame;

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
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g2d.setColor(f.currUIStyle.getIconColor());
        g2d.drawLine(0, 0, getWidth(), 0);
    }
}
