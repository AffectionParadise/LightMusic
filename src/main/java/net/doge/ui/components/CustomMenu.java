package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenu extends JMenu {

    public CustomMenu(String text) {
        super(text);
        createBorder();
    }

    private void createBorder() {
        setBorder(BorderFactory.createEmptyBorder(8, 5,8,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (isSelected()) {
            Rectangle rect = getVisibleRect();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
