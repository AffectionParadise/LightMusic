package net.doge.ui.widget.button.tooltip;

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

    public CustomToolTip(JComponent comp) {
        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        setComponent(comp);
        setOpaque(false);
        setFont(Fonts.NORMAL_TINY);
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
        g2d.setColor(Colors.LIGHT_GRAY);
        int arc = ScaleUtil.scale(4);
        g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, arc, arc);

        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlphaColor(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, getWidth() - (i * 2 + 1), getHeight() - (i * 2 + 1), arc, arc);
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
