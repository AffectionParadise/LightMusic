package net.doge.ui.components.list;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;

public class CustomScrollPane extends JScrollPane {
    private Timer wheelScrollingTimer;
    private int scrollingFrom;
    private int scrollingTo;

    public CustomScrollPane() {
        super();
        init();
    }

    public CustomScrollPane(Component comp) {
        super(comp);
        init();
    }

    private void init() {
        setOpaque(false);
        viewport.setOpaque(false);

        // 滚轮滚动动画
        wheelScrollingTimer = new Timer(1, e -> {
            int vValue = getVValue();
            if (vValue == scrollingTo) wheelScrollingTimer.stop();

            int gap = scrollingTo - scrollingFrom, piece = Math.max(1, Math.abs(gap) / 10);
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

        final int thickness = 10;
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setPreferredSize(new Dimension(0, thickness));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(thickness, 0));
        // 滚动条不显示时也要占位
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    public void setVValue(int value) {
        if (value < getVMin() || value > getVMax()) return;
        verticalScrollBar.setValue(value);
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

    public void setVUI(ScrollBarUI ui) {
        verticalScrollBar.setUI(ui);
    }

    public ScrollBarUI getVUI() {
        return verticalScrollBar.getUI();
    }
}
