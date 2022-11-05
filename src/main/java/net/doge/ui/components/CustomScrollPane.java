package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

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
        JScrollBar hs = getHorizontalScrollBar();
        hs.setOpaque(false);
        hs.setPreferredSize(new Dimension(0, thickness));
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollBar vs = getVerticalScrollBar();
        vs.setOpaque(false);
        vs.setPreferredSize(new Dimension(thickness, 0));
        vs.setUnitIncrement(30);
        // 滚动条不显示时也要占位
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
}
