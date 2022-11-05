package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

public class CustomSlider extends JSlider {

    public CustomSlider() {
        super();
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
