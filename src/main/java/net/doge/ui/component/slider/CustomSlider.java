package net.doge.ui.component.slider;

import javax.swing.*;
import java.awt.*;

public class CustomSlider extends JSlider {
    public CustomSlider() {
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
