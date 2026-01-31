package net.doge.ui.widget.label;

import lombok.Getter;
import lombok.Setter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    @Setter
    private boolean drawBg;
    @Setter
    private Color bgColor;
    @Getter
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
        Graphics2D g2d = GraphicsUtil.setup(g);

        if (drawBg) {
            // 画背景
            g2d.setColor(bgColor);
            GraphicsUtil.srcOver(g2d, 0.1f);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }

        GraphicsUtil.srcOver(g2d, alpha);

        super.paintComponent(g);
    }
}
