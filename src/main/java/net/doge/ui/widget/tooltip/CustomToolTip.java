package net.doge.ui.widget.tooltip;

import net.doge.constant.core.ui.Colors;
import net.doge.constant.core.ui.Fonts;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomToolTip extends JToolTip {
    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    private final int pixels = ScaleUtil.scale(5);

    public CustomToolTip() {
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(Fonts.NORMAL_TINY);
        setForeground(Colors.DARK_GRAY);
        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        int w = getWidth(), h = getHeight();
        g2d.setColor(Colors.LIGHT_GRAY);
        GraphicsUtil.srcOver(g2d, 0.8f);
        int arc = ScaleUtil.scale(4);
        g2d.fillRoundRect(pixels, pixels, w - 2 * pixels, h - 2 * pixels, arc, arc);
        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, w - i * 2, h - i * 2, arc, arc);
        }
        GraphicsUtil.srcOver(g2d);
        super.paintComponent(g);
    }
}
