package net.doge.ui.components;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomComboBox<T> extends JComboBox<T> {

    public CustomComboBox() {
        super();
        setOpaque(false);
        setFocusable(false);
        setLightWeightPopupEnabled(false);
        setFont(Fonts.NORMAL);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
