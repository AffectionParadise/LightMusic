package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 菜单项自定义 UI
 * @Date 2020/12/13
 */
public class CustomRadioButtonMenuItem extends JRadioButtonMenuItem {
    private boolean entered;

    public CustomRadioButtonMenuItem(String text) {
        super(text);
        createBorder();
        createListener();
    }

    public CustomRadioButtonMenuItem(String text, boolean selected) {
        super(text, selected);
        createBorder();
        createListener();
    }

    private void createBorder() {
        setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 0));
    }

    private void createListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                entered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                entered = false;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                entered = false;
                repaint();
            }
        });
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
