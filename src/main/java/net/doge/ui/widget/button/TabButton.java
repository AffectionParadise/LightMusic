package net.doge.ui.widget.button;

import lombok.Getter;
import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.tooltip.CustomToolTip;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

public class TabButton extends BaseButton {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    private float bgAlpha;
    private final float destBgAlpha = 0.2f;

    @Getter
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
        init();
    }

    private void init() {
        drawBgTimer = new Timer(2, e -> {
            if (drawBgIncreasing) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(0, bgAlpha - 0.005f);
            if (bgAlpha >= destBgAlpha) drawBgTimer.stop();
            else if (bgAlpha <= 0) {
                drawBg = false;
                drawBgTimer.stop();
            }
            repaint();
        });
    }

    public void transitionDrawBg(boolean drawBgIncreasing) {
        this.drawBg = true;
        this.drawBgIncreasing = drawBgIncreasing;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip toolTip = new CustomToolTip(this);
        toolTip.setVisible(false);
        return toolTip;
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (!b) transitionDrawBg(false);
    }

    public void setActive(boolean active) {
        this.active = active;
        transitionDrawBg(active);
    }

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, active ? 0.1f : bgAlpha);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d);
        }
        super.paintComponent(g);
    }
}
