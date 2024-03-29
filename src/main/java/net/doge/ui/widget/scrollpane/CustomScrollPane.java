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
            int vValue = getVValue();
            if (vValue == scrollingTo) wheelScrollingTimer.stop();

            int gap = scrollingTo - scrollingFrom, piece = Math.max(1, Math.abs(gap) / 20);
            setVValue(gap > 0 ? Math.min(scrollingTo, vValue + piece) : Math.max(scrollingTo, vValue - piece));

            if (getVValue() == vValue) wheelScrollingTimer.stop();
        });
        setWheelScrollingEnabled(false);
        addMouseWheelListener(e -> {
            // 单位滚动长度
            final int scrollAmount = 200;
            scrollingFrom = getVValue();
            scrollingTo = e.getWheelRotation() > 0 ? Math.min(getVMax(), scrollingFrom + scrollAmount) : Math.max(getVMin(), scrollingFrom - scrollAmount);
            if (scrollingFrom == scrollingTo || wheelScrollingTimer.isRunning()) return;
            wheelScrollingTimer.start();
        });

        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setPreferredSize(new Dimension(0, thickness));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(thickness, 0));
        // 滚动条不显示时也要占位
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public boolean setVValue(int value) {
        if (value < getVMin() || value > getVMax()) return false;
        verticalScrollBar.setValue(value);
        return true;
    }

    public int getVValue() {
        return verticalScrollBar.getValue();
    }

    public int getVMax() {
        return verticalScrollBar.getMaximum();
    }

    public int getVMin() {
        return verticalScrollBar.getMinimum();
    }

    public void setHUI(ScrollBarUI ui) {
        horizontalScrollBar.setUI(ui);
    }

    public ScrollBarUI getHUI() {
        return horizontalScrollBar.getUI();
    }

    public void setVUI(ScrollBarUI ui) {
        verticalScrollBar.setUI(ui);
    }

    public ScrollBarUI getVUI() {
        return verticalScrollBar.getUI();
    }

    @Override
    protected JViewport createViewport() {
        return new CustomViewport();
    }
}
