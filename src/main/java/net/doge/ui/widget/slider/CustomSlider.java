package net.doge.ui.widget.slider;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomSlider extends JSlider implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    public CustomSlider() {
        init();
    }

    private void init() {
        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }
}
