package net.doge.ui.widget.button.base;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.button.tooltip.CustomToolTip;

import javax.swing.*;
import java.awt.*;

public abstract class BaseButton extends JButton {
    public BaseButton() {
        this(null, null);
    }

    public BaseButton(String text) {
        this(text, null);
    }

    public BaseButton(Icon icon) {
        this(null, icon);
    }

    public BaseButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip toolTip = new CustomToolTip(this);
        toolTip.setVisible(false);
        return toolTip;
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
