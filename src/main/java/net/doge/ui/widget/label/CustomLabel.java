package net.doge.ui.widget.label;

import lombok.Setter;
import net.doge.constant.ui.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    @Setter
    private boolean drawBg;
    @Setter
    private Color bgColor;
    protected float alpha = 1f;
    private float destAlpha = 1f;
    private Timer alphaTimer;

    public CustomLabel() {
        this(null, null);
    }

    public CustomLabel(String text) {
        this(text, null);
    }

    public CustomLabel(Icon icon) {
        this(null, icon);
    }

    public CustomLabel(String text, Icon icon) {
        super(text, icon, CENTER);
        init();
    }

    private void init() {
        setFont(Fonts.NORMAL);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        alphaTimer = new Timer(0, e -> {
            if (alpha < destAlpha) alpha = Math.min(destAlpha, alpha + 0.005f);
            else if (alpha > destAlpha) alpha = Math.max(destAlpha, alpha - 0.005f);
            else alphaTimer.stop();
            repaint();
        });
    }

    public void setAlpha(float alpha) {
        this.destAlpha = alpha;
        if (alphaTimer.isRunning()) return;
        alphaTimer.start();
    }

    public void setInstantAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (drawBg) {
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(bgColor);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        super.paintComponent(g);
    }
}
