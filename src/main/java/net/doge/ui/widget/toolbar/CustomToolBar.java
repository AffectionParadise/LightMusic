package net.doge.ui.widget.toolbar;

import javax.swing.*;

public class CustomToolBar extends JToolBar {
    public CustomToolBar() {
        init();
    }

    private void init() {
        setOpaque(false);
        setFloatable(false);
        setBorder(null);
    }
}
