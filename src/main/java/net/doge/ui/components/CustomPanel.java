package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private boolean drawBg;

    public CustomPanel() {
        super();
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
        repaint();
    }


    @Override
    public void paint(Graphics g) {
        if(drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paint(g);
    }
}
