package net.doge.ui.widget.button.tooltip;

import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CustomToolTip extends JToolTip {
    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 5;

    public CustomToolTip(JComponent comp) {
        // 阴影边框
        Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), border));

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
        g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, 4, 4);

        // 画边框阴影
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
            g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 4, 4);
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
