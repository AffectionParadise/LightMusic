package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

public class CustomScrollPane extends JScrollPane {

    public CustomScrollPane() {
        super();
        init();
    }

    public CustomScrollPane(JComponent comp) {
        super(comp);
        init();
    }

    private void init() {
        setOpaque(false);
        getViewport().setOpaque(false);
        getHorizontalScrollBar().setOpaque(false);
        getVerticalScrollBar().setOpaque(false);
        getVerticalScrollBar().setUnitIncrement(30);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
