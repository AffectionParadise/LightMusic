package net.doge.ui.widget.toolbar;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomToolBar extends JToolBar implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    public CustomToolBar() {
        setOpaque(false);
        setFloatable(false);
        setBorder(null);
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

    @Override
    protected void paintComponent(Graphics g) {
        GraphicsUtil.srcOver(g, extendedOpacity);
        super.paintComponent(g);
    }
}
