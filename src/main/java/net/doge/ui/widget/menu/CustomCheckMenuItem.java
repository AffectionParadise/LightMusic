package net.doge.ui.widget.menu;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 菜单项自定义 UI
 * @Date 2020/12/13
 */
public class CustomCheckMenuItem extends JCheckBoxMenuItem {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    private float bgAlpha;
    private final float destBgAlpha = 0.1f;

    public CustomCheckMenuItem(String text) {
        this(text, false);
    }

    public CustomCheckMenuItem(String text, boolean selected) {
        super(text, selected);
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(Fonts.NORMAL);
        createBorder();
        initResponse();
    }

    private void initResponse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                transitionDrawBg(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                transitionDrawBg(false);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                transitionDrawBg(false);
            }
        });

        addMouseWheelListener(e -> transitionDrawBg(false));

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

    private void createBorder() {
        setBorder(new HDEmptyBorder(4, 5, 4, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(getForeground());
            GraphicsUtil.srcOver(g2d, bgAlpha);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            GraphicsUtil.srcOver(g2d);
        }
        super.paintComponent(g);
    }
}
