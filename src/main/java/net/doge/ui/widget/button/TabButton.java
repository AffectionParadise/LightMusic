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
    private Timer drawBgTimer;
    private float alpha;
    private final float destAlpha = 0.2f;

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
            if (drawBg) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(0, alpha - 0.005f);
            if (alpha <= 0 || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setDrawBg(boolean drawBg) {
        if (this.drawBg == drawBg) return;
        this.drawBg = drawBg;
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
        if (!b) setDrawBg(false);
    }

    public void setActive(boolean active) {
        this.active = active;
        setDrawBg(active);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            // 画背景
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, active ? 0.1f : alpha);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d);
        }
        super.paintComponent(g);
    }
}
