package net.doge.ui.components;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    private boolean drawBg;
    private Color bgColor;

    public CustomLabel() {
        super();
        init();
    }

    public CustomLabel(String text) {
        super(text);
        init();
    }

    public CustomLabel(Icon icon) {
        super(icon);
        init();
    }

    public CustomLabel(String text, Icon icon) {
        super(text, icon, CENTER);
        init();
    }

    private void init() {
        setFont(Fonts.NORMAL);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(bgColor);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
