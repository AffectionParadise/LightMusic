package net.doge.ui.components;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 菜单项自定义 UI
 * @Date 2020/12/13
 */
public class CustomCheckMenuItem extends JCheckBoxMenuItem {
    private boolean entered;

    public CustomCheckMenuItem(String text) {
        super(text);
        init();
    }

    public CustomCheckMenuItem(String text, boolean selected) {
        super(text, selected);
        init();
    }

    private void init() {
        setFont(Fonts.NORMAL);
        createBorder();
        initResponse();
    }

    private void initResponse() {
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
        setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (entered) {
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
