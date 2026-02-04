package net.doge.ui.widget.button;

import lombok.Getter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.tooltip.CustomToolTip;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class TabButton extends BaseButton implements ExtendedOpacitySupported {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    private float bgAlpha;
    private final float destBgAlpha = 0.2f;
    @Getter
    private float extendedOpacity = 1f;

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
        setFont(Fonts.NORMAL_TITLE2);
        setIconTextGap(ScaleUtil.scale(15));

        drawBgTimer = new Timer(2, e -> {
            if (drawBgIncreasing) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(0f, bgAlpha - 0.005f);
            if (bgAlpha >= destBgAlpha) drawBgTimer.stop();
            else if (bgAlpha <= 0f) {
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
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, extendedOpacity * (active ? 0.1f : bgAlpha));
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d, extendedOpacity);
        }
        super.paintComponent(g);
    }
}
