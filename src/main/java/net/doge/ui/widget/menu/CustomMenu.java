package net.doge.ui.widget.menu;

import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

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
        setBorder(new HDEmptyBorder(8, 5, 8, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (isSelected()) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, 0.1f);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d);
        }

        super.paintComponent(g);
    }
}
