package net.doge.ui.widget.button;

import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

public class TabButton extends CustomButton {
    private boolean active;

    public TabButton() {
        this(null, null);
    }

    public TabButton(String text) {
        this(text, null);
    }

    public TabButton(Icon icon) {
        this(null, icon);
    }

    public TabButton(String text, Icon icon) {
        super(text, icon);
    }

    public void setActive(boolean active) {
        this.active = active;
        setDrawBg(active);
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            // 画背景
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, active ? 0.1f : alpha);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            GraphicsUtil.srcOver(g2d);
        }
        super.paintComponent(g);
    }
}
