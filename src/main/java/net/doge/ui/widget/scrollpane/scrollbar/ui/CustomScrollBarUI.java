package net.doge.ui.widget.scrollpane.scrollbar.ui;

import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.scrollpane.scrollbar.CustomScrollBar;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 滚动条自定义 UI
 * @Date 2020/12/13
 */
public class CustomScrollBarUI extends BasicScrollBarUI {
    private boolean active;
    private boolean entered;
    private boolean adjusting;
    private Color thumbColor;

    public CustomScrollBarUI(Color thumbColor) {
        this(thumbColor, true);
    }

    public CustomScrollBarUI(Color thumbColor, boolean active) {
        this.thumbColor = thumbColor;
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
        scrollbar.repaint();
    }

    // 创建空按钮去掉滚动条上的按钮
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new HDDimension(0, 0));
        button.setVisible(false);
        return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    /**
     * 自定义把手
     *
     * @param g
     * @param c
     * @param thumbBounds
     */
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (!active) return;
        CustomScrollBar sb = (CustomScrollBar) scrollbar;
        Graphics2D g2d = GraphicsUtil.setup(g);
        g2d.setColor(thumbColor);
        // 透明滚动条
        GraphicsUtil.srcOver(g2d, sb.getExtendedOpacity() * (entered ? 0.6f : 0.3f));
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);
    }

    /**
     * 自定义滑道(不绘制)
     *
     * @param g
     * @param c
     * @param trackBounds
     */
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

    }

    /**
     * 创建自定义把手拖动策略
     *
     * @return
     */
    @Override
    protected TrackListener createTrackListener() {
        return new TrackListener() {
            @Override
            public void mouseEntered(MouseEvent e) {
                entered = true;
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!adjusting) entered = false;
                super.mouseExited(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                adjusting = true;
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!thumbRect.contains(e.getPoint())) entered = false;
                adjusting = false;
                super.mouseReleased(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                entered = thumbRect.contains(e.getPoint());
                super.mouseMoved(e);
            }
        };
    }
}
