package net.doge.ui.components;

import javax.swing.*;

public class CustomCheckBox extends JCheckBox {

    public CustomCheckBox() {
        super();
        init();
    }

    public CustomCheckBox(String text) {
        super(text);
        init();
    }

    private void init() {
        setOpaque(false);
        setFocusPainted(false);
    }
}
