package net.doge.ui.widget.scrollpane;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.scrollpane.scrollbar.CustomScrollBar;
import net.doge.ui.widget.scrollpane.scrollbar.ui.CustomScrollBarUI;
import net.doge.ui.widget.scrollpane.viewport.CustomViewport;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomScrollPane extends JScrollPane implements ExtendedOpacitySupported {
    @Getter
    private final int thickness = ScaleUtil.scale(10);
    // 单位滚动长度
    private final int scrollAmount = ScaleUtil.scale(200);
    @Getter
    private float extendedOpacity = 1f;

    private Timer wheelScrollingTimer;
    private int scrollingFrom;
    private int scrollingTo;

    public CustomScrollPane() {
        this(null);
    }

    public CustomScrollPane(Component comp) {
        super(comp);
        init();
    }

    private void init() {
        setOpaque(false);
        // 默认有黑边，不绘制边框
        setBorder(null);

        // 滚轮滚动动画
        wheelScrollingTimer = new Timer(0, e -> {
            int vValue = getVBarValue();
            if (vValue == scrollingTo) wheelScrollingTimer.stop();

            int gap = scrollingTo - scrollingFrom, piece = Math.max(1, Math.abs(gap) / ScaleUtil.scale(20));
            setVBarValue(gap > 0 ? Math.min(scrollingTo, vValue + piece) : Math.max(scrollingTo, vValue - piece));

            if (getVBarValue() == vValue) wheelScrollingTimer.stop();
        });
        setWheelScrollingEnabled(false);
        addMouseWheelListener(e -> {
            scrollingFrom = getVBarValue();
            scrollingTo = e.getWheelRotation() > 0 ? Math.min(getVBarMax(), scrollingFrom + scrollAmount) : Math.max(getVBarMin(), scrollingFrom - scrollAmount);
            if (scrollingFrom == scrollingTo || wheelScrollingTimer.isRunning()) return;
            wheelScrollingTimer.start();
        });

        horizontalScrollBar.setPreferredSize(new Dimension(0, thickness));
        horizontalScrollBar.setUI(new CustomScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(thickness, 0));
        verticalScrollBar.setUI(new CustomScrollBarUI());
    }

    public boolean setVBarValue(int value) {
        if (value < getVBarMin() || value > getVBarMax()) return false;
        verticalScrollBar.setValue(value);
        // 修复某些情况滚动条不显示并且无法滚动
        if (value == 0 && !verticalScrollBar.isShowing()) {
            revalidate();
        }
        return true;
    }

    public int getVBarValue() {
        return verticalScrollBar.getValue();
    }

    public CustomScrollBar getHBar() {
        return (CustomScrollBar) horizontalScrollBar;
    }

    public CustomScrollBar getVBar() {
        return (CustomScrollBar) verticalScrollBar;
    }

    public int getVBarMin() {
        return verticalScrollBar.getMinimum();
    }

    public int getVBarMax() {
        return verticalScrollBar.getMaximum();
    }

    public void setHBarActive(boolean active) {
        ((CustomScrollBar) horizontalScrollBar).setActive(active);
    }

    public void setVBarActive(boolean active) {
        ((CustomScrollBar) verticalScrollBar).setActive(active);
    }

    @Override
    protected JViewport createViewport() {
        return new CustomViewport();
    }

    public Component getViewportView() {
        return viewport.getView();
    }

    @Override
    public JScrollBar createHorizontalScrollBar() {
        return new CustomScrollBar(CustomScrollBar.HORIZONTAL);
    }

    @Override
    public JScrollBar createVerticalScrollBar() {
        return new CustomScrollBar(CustomScrollBar.VERTICAL);
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
}
