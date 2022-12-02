package net.doge.ui.components;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CustomScrollPane extends JScrollPane {

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
        getViewport().setOpaque(false);

        final int thickness = 10;
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setPreferredSize(new Dimension(0, thickness));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(thickness, 0));
        verticalScrollBar.setUnitIncrement(30);
        // 滚动条不显示时也要占位
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                JViewport vp = getViewport();
                if (vp.getComponentCount() == 0) return;
                vp.add(vp.getComponent(0));
                vp.add(vp.getComponent(0));
            }
        });
    }

    public void setVValue(int value) {
        verticalScrollBar.setValue(value);
    }

    public int getVValue() {
        return verticalScrollBar.getValue();
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

    public void setVUnitIncrement(int unitIncrement) {
        verticalScrollBar.setUnitIncrement(unitIncrement);
    }
}
