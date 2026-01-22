package net.doge.ui.widget.scrollpane;

import lombok.Getter;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;

public class CustomScrollPane extends JScrollPane {
    @Getter
    private final int thickness = 10;

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

        // 滚轮滚动动画
        wheelScrollingTimer = new Timer(0, e -> {
            int vValue = getVBarValue();
            if (vValue == scrollingTo) wheelScrollingTimer.stop();

            int gap = scrollingTo - scrollingFrom, piece = Math.max(1, Math.abs(gap) / 20);
            setVValue(gap > 0 ? Math.min(scrollingTo, vValue + piece) : Math.max(scrollingTo, vValue - piece));

            if (getVBarValue() == vValue) wheelScrollingTimer.stop();
        });
        setWheelScrollingEnabled(false);
        addMouseWheelListener(e -> {
            // 单位滚动长度
            final int scrollAmount = 200;
            scrollingFrom = getVBarValue();
            scrollingTo = e.getWheelRotation() > 0 ? Math.min(getVBarMax(), scrollingFrom + scrollAmount) : Math.max(getVBarMin(), scrollingFrom - scrollAmount);
            if (scrollingFrom == scrollingTo || wheelScrollingTimer.isRunning()) return;
            wheelScrollingTimer.start();
        });

        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setPreferredSize(new Dimension(0, thickness));

        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(thickness, 0));

        // 默认有黑边，不绘制边框
        setBorder(BorderFactory.createEmptyBorder());
    }

    public boolean setVValue(int value) {
        if (value < getVBarMin() || value > getVBarMax()) return false;
        verticalScrollBar.setValue(value);
        // 修复某些情况滚动条不显示并且无法滚动
        if (value == 0 && !verticalScrollBar.isShowing()) {
            setVisible(false);
            setVisible(true);
        }
        return true;
    }

    public int getVBarValue() {
        return verticalScrollBar.getValue();
    }

    public int getVBarMax() {
        return verticalScrollBar.getMaximum();
    }

    public int getVBarMin() {
        return verticalScrollBar.getMinimum();
    }

    public void setHBarUI(ScrollBarUI ui) {
        horizontalScrollBar.setUI(ui);
    }

    public ScrollBarUI getHBarUI() {
        return horizontalScrollBar.getUI();
    }

    public void setVBarUI(ScrollBarUI ui) {
        verticalScrollBar.setUI(ui);
    }

    public ScrollBarUI getVBarUI() {
        return verticalScrollBar.getUI();
    }

    @Override
    protected JViewport createViewport() {
        return new CustomViewport();
    }

    public Component getViewportView() {
        return viewport.getView();
    }
}
