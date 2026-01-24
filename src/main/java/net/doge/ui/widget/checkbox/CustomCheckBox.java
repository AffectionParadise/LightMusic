package net.doge.ui.widget.checkbox;

import net.doge.constant.core.ui.core.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomCheckBox extends JCheckBox {
    public CustomCheckBox() {
        this(null);
    }

    public CustomCheckBox(String text) {
        super(text);
        init();
    }

    private void init() {
        setOpaque(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setIconTextGap(10);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
