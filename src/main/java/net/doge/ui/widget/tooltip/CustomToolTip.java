package net.doge.ui.widget.tooltip;

import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
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

    static {
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    }

    public CustomToolTip() {
        init();
    }

    private void init() {
        setOpaque(false);
        setFont(Fonts.NORMAL_TINY);
        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
    }

    // 使 JToolTip 背景透明
    @Override
    public void addNotify() {
//        super.addNotify();
        Component parent = getParent();
        if (parent instanceof JComponent) {
            JComponent jParent = (JComponent) parent;
            jParent.setOpaque(false);
        }
        Window w = SwingUtilities.getWindowAncestor(this);
        w.setBackground(Colors.TRANSPARENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        int w = getWidth(), h = getHeight();
        g2d.setColor(Colors.LIGHT_GRAY);
        int arc = ScaleUtil.scale(4);
        g2d.fillRoundRect(pixels, pixels, w - 2 * pixels, h - 2 * pixels, arc, arc);
        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, w - (i * 2 + 1), h - (i * 2 + 1), arc, arc);
        }
        super.paintComponent(g);
    }
}
