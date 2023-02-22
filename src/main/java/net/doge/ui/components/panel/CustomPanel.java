package net.doge.ui.components.panel;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private boolean drawBg;
    // 是否无动画画背景
    private boolean bluntDrawBg;
    private Timer drawBgTimer;
    private float alpha;
    private final float destAlpha = 0.1f;

    public CustomPanel() {
        init();
    }

    public CustomPanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    private void init() {
        setOpaque(false);

        drawBgTimer = new Timer(1, e -> {
            if (drawBg) alpha = Math.min(destAlpha, alpha + 0.002f);
            else alpha = Math.max(0, alpha - 0.002f);
            if (alpha <= 0 || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setBluntDrawBg(boolean bluntDrawBg) {
        this.bluntDrawBg = bluntDrawBg;
    }

    public void setDrawBg(boolean drawBg) {
        if (this.drawBg == drawBg) return;
        this.drawBg = drawBg;
        if (bluntDrawBg) {
            alpha = destAlpha;
            repaint();
        } else {
            if (drawBgTimer.isRunning()) return;
            drawBgTimer.start();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 画背景
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getForeground());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        super.paintComponent(g);
    }

    // 返回组件索引，找不到返回 -1
    public int getComponentIndex(Component comp) {
        Component[] components = getComponents();
        for (int i = 0, len = components.length; i < len; i++) {
            if (components[i] == comp) return i;
        }
        return -1;
    }
}
