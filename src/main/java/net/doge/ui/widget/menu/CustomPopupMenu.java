package net.doge.ui.widget.menu;

import lombok.Getter;
import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义弹出菜单
 * @Date 2020/12/13
 */
public class CustomPopupMenu extends JPopupMenu implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;
    private Timer fadingInTimer;

    private MainFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    private final int pixels = ScaleUtil.scale(10);

    public CustomPopupMenu(MainFrame f) {
        this.f = f;
        init();
    }

    private void init() {
        setOpaque(false);
        setLightWeightPopupEnabled(false);
        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));

        fadingInTimer = new Timer(10, e -> {
            // 淡入
            float opacity = Math.min(1f, extendedOpacity + 0.05f);
            setTreeExtendedOpacity(opacity);
            if (opacity >= 1f) {
                // 淡入动画完成后恢复透明度
                setTreeExtendedOpacity(1f);
                fadingInTimer.stop();
            }
        });
    }

    // 淡入
    public void fadeIn() {
        setTreeExtendedOpacity(0f);
        if (fadingInTimer.isRunning()) return;
        fadingInTimer.start();
    }

    @Override
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        fadeIn();
        super.show(invoker, x, y);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // 使 JPopupMenu 对应的 Window 透明！
            Window w = SwingUtilities.getWindowAncestor(this);
            w.setVisible(false);
            w.setBackground(Colors.TRANSPARENT);
            fadeIn();
            w.setVisible(true);
        }
        f.currPopup = b ? this : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        int w = getWidth(), h = getHeight();

        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg()));
        GraphicsUtil.srcOver(g2d, extendedOpacity);
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(pixels, pixels, w - 2 * pixels, h - 2 * pixels, arc, arc);

        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, w - (i * 2 + 1), h - (i * 2 + 1), arc, arc);
        }
    }

    @Override
    public void addSeparator() {
        add(new CustomSeparator());
    }
}
