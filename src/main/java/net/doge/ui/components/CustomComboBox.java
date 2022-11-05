package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

public class CustomComboBox<T> extends JComboBox<T> {

    public CustomComboBox() {
        super();
        setOpaque(false);
        setFocusable(false);
        setLightWeightPopupEnabled(false);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
