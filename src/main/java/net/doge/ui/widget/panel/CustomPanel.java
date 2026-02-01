package net.doge.ui.widget.panel;

import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    // 背景不透明度
    private float bgAlpha;
    private final float destBgAlpha = 0.1f;

    public CustomPanel() {
        this(new HDFlowLayout());
    }

    public CustomPanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    private void init() {
        setOpaque(false);

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

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
        this.bgAlpha = destBgAlpha;
    }

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, bgAlpha);
            int arc = ScaleUtil.scale(8);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d);
        }
        super.paintComponent(g);
    }

    // 返回组件索引，找不到返回 -1
    public int getComponentIndex(Component comp) {
        Component[] components = getComponents();
        for (int i = 0, len = components.length; i < len; i++)
            if (components[i] == comp) return i;
        return -1;
    }
}
