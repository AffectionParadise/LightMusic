package net.doge.ui.widget.panel;

import lombok.Setter;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

public class CustomPanel extends JPanel {
    private boolean drawBg;
    // 是否无动画画背景
    @Setter
    private boolean instantDrawBg;
    private Timer drawBgTimer;
    // 背景不透明度
    private float bgAlpha;
    private final float destBgAlpha = 0.1f;

    public CustomPanel() {
        this(new FlowLayout());
    }

    public CustomPanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    private void init() {
        setOpaque(false);

        drawBgTimer = new Timer(2, e -> {
            if (drawBg) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(0, bgAlpha - 0.005f);
            if (bgAlpha <= 0 || bgAlpha >= destBgAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setDrawBg(boolean drawBg) {
        if (this.drawBg == drawBg) return;
        this.drawBg = drawBg;
        if (instantDrawBg) {
            bgAlpha = destBgAlpha;
            repaint();
        } else {
            if (drawBgTimer.isRunning()) return;
            drawBgTimer.start();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            // 画背景
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, bgAlpha);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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
