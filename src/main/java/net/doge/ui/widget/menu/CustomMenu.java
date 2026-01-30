package net.doge.ui.widget.menu;

import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenu extends JMenu {
    public CustomMenu(String text) {
        super(text);
        createBorder();
    }

    private void createBorder() {
        setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (isSelected()) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, 0.1f);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            GraphicsUtil.srcOver(g2d);
        }

        super.paintComponent(g);
    }
}
