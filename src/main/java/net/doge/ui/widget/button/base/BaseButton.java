package net.doge.ui.widget.button.base;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.tooltip.CustomToolTip;
import net.doge.util.ui.ImageUtil;

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

    // 根据主题色更新图标
    public void updateIconStyle() {
        Icon icon = getIcon();
        if (icon == null) return;
        setIcon(ImageUtil.dye((ImageIcon) icon, UIStyleStorage.currUIStyle.getIconColor()));
    }

    // 根据指定颜色更新图标
    public void updateIconColor(Color color) {
        Icon icon = getIcon();
        if (icon == null) return;
        setIcon(ImageUtil.dye((ImageIcon) icon, color));
    }

    // 应用主题色的图标
    public void setStyledIcon(ImageIcon icon) {
        setIcon(ImageUtil.dye(icon, UIStyleStorage.currUIStyle.getIconColor()));
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip toolTip = new CustomToolTip();
        toolTip.setVisible(false);
        return toolTip;
    }
}
