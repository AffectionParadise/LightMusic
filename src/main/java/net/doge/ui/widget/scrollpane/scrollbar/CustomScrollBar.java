package net.doge.ui.widget.scrollpane.scrollbar;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;

public class CustomScrollBar extends JScrollBar implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    public CustomScrollBar(int orientation) {
        super(orientation);
        init();
    }

    private void init() {
        setOpaque(false);
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
