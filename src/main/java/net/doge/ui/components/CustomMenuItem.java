package net.doge.ui.components;

import net.doge.ui.componentui.MenuItemUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @Author yzx
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenuItem extends JMenuItem {
    private boolean entered;

    public CustomMenuItem(String text) {
        super(text);
        createBorder();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                enter();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exit();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                exit();
            }
        });

        addMouseWheelListener(e -> exit());
    }

    private void enter() {
        entered = true;
        repaint();
    }

    private void exit() {
        entered = false;
        repaint();
    }

    private void createBorder() {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (entered) {
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
