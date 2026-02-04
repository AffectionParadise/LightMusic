package net.doge.ui.widget.menu;

import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomPopupMenu extends JPopupMenu {
    private MainFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    private final int pixels = ScaleUtil.scale(10);

    public CustomPopupMenu(MainFrame f) {
        this.f = f;

        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));

        setOpaque(false);
        setLightWeightPopupEnabled(false);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // 使 JPopupMenu 对应的 Window 透明！
            Window w = SwingUtilities.getWindowAncestor(this);
            w.setVisible(false);
            w.setBackground(Colors.BLACK);
            w.setBackground(Colors.TRANSPARENT);
            w.setVisible(true);
        }
        f.currPopup = b ? this : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg()));
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, arc, arc);

        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, getWidth() - (i * 2 + 1), getHeight() - (i * 2 + 1), arc, arc);
        }
    }

    @Override
    public void addSeparator() {
        add(new CustomSeparator());
    }
}
