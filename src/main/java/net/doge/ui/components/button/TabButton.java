package net.doge.ui.components.button;

import javax.swing.*;
import java.awt.*;

public class TabButton extends CustomButton {
    private boolean active;

    public TabButton() {
        super();
    }

    public TabButton(String text) {
        super(text);
    }

    public TabButton(Icon icon) {
        super(icon);
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
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, active ? 0.1f : 0.2f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        super.paintComponent(g);
    }
}
