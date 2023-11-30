package net.doge.ui.widget.menu;

import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomPopupMenu extends JPopupMenu {
    private MainFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    public CustomPopupMenu(MainFrame f) {
        this.f = f;

        // 阴影边框
        Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), border));

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
        Graphics2D g2d = (Graphics2D) g;

        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBackgroundImage()));
        g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, 10, 10);

        // 画边框阴影
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
            g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
//        super.paintBorder(g);
    }

    @Override
    public void addSeparator() {
        add(new CustomSeparator(f));
    }
}
