package net.doge.ui.componentui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 滚动条自定义 UI
 * @Date 2020/12/13
 */
public class ScrollBarUI extends BasicScrollBarUI {
    private boolean active;
    private Color thumbColor;

    public ScrollBarUI(Color thumbColor) {
        this.thumbColor = thumbColor;
        this.active = true;
    }

    public ScrollBarUI(Color thumbColor, boolean active) {
        this.thumbColor = thumbColor;
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
        scrollbar.repaint();
    }

    // 创建空按钮去掉滚动条上的按钮
    protected JButton createZeroButton() {
        JButton button = new JButton("");
        Dimension zeroDim = new Dimension(0,0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
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
        // 不要调用父类的方法，会画一个实心的滚动条，不好看
//        super.paintThumb(g, c, thumbBounds);
        if(active) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(thumbColor);
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 透明滚动条
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2d.fillRoundRect(
                    (int) thumbBounds.getX(),
                    (int) thumbBounds.getY(),
                    (int) thumbBounds.getWidth(),
                    (int) thumbBounds.getHeight(),
                    10,
                    10
            );
        }
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
     * 设置滚动条宽度
     * @param c
     * @return
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        JScrollBar scrollBar = (JScrollBar) c;
        if(scrollBar.getOrientation() == HORIZONTAL) {
            scrollBar.setPreferredSize(new Dimension(0, 10));
        } else {
            scrollBar.setPreferredSize(new Dimension(10, 0));
        }
        return super.getPreferredSize(c);
    }
}
