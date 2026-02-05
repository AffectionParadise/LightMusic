package net.doge.ui.widget.button.base;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.tooltip.CustomToolTip;

import javax.swing.*;
import java.awt.*;

public class BaseButton extends JButton {
    private static final HDEmptyBorder BORDER = new HDEmptyBorder(5, 5, 5, 5);

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
        setBorder(BORDER);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip toolTip = new CustomToolTip();
        toolTip.setVisible(false);
        return toolTip;
    }
}
