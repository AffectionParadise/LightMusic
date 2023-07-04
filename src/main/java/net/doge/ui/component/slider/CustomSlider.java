package net.doge.ui.component.slider;

import javax.swing.*;
import java.awt.*;

public class CustomSlider extends JSlider {

    public CustomSlider() {
        super();
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
