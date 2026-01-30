package net.doge.ui.widget.menu;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenuItem extends JMenuItem {
    private boolean drawBg;
    private Timer drawBgTimer;
    private float alpha;
    private final float destAlpha = 0.1f;
    private static final Border BORDER = BorderFactory.createEmptyBorder(6, 0, 6, -10);

    public CustomMenuItem() {
        this(null);
    }

    public CustomMenuItem(String text) {
        super(text);
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(Fonts.NORMAL);
        setIconTextGap(10);
        setBorder(BORDER);
        initResponse();
    }

    private void initResponse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setDrawBg(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setDrawBg(false);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setDrawBg(false);
            }
        });

        addMouseWheelListener(e -> setDrawBg(false));

        drawBgTimer = new Timer(2, e -> {
            if (drawBg) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(0, alpha - 0.005f);
            if (alpha <= 0 || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    private void setDrawBg(boolean drawBg) {
        if (this.drawBg == drawBg) return;
        this.drawBg = drawBg;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        // 画背景
        g2d.setColor(getForeground());
        GraphicsUtil.srcOver(g2d, alpha);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        GraphicsUtil.srcOver(g2d);

        super.paintComponent(g);
    }
}
