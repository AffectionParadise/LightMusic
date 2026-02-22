package net.doge.ui.widget.panel;

import lombok.Getter;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.panel.base.BasePanel;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends BasePanel implements ExtendedOpacitySupported {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    // 背景不透明度
    private float bgAlpha;
    private final float destBgAlpha = 0.1f;
    @Getter
    private float extendedOpacity = 1f;

    public CustomPanel() {
        this(new HDFlowLayout());
    }

    public CustomPanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    private void init() {
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

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
        this.bgAlpha = destBgAlpha;
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
            GraphicsUtil.srcOver(g2d, extendedOpacity * bgAlpha);
            int arc = ScaleUtil.scale(8);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }
    }
}
