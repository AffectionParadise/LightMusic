package net.doge.ui.widget.menu;

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
        setBorder(BorderFactory.createEmptyBorder(8, 5,8,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (isSelected()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
