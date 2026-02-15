package net.doge.ui.widget.checkbox;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

public class CustomCheckBox extends JCheckBox {
    // 复选框图标
    public static ImageIcon uncheckedIcon = LMIconManager.getIcon("dialog.unchecked");
    public static ImageIcon checkedIcon = LMIconManager.getIcon("dialog.checked");

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
        setIconTextGap(ScaleUtil.scale(10));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void updateIconStyle() {
        Color iconColor = UIStyleStorage.currUIStyle.getIconColor();
        setIcon(ImageUtil.dye(uncheckedIcon, iconColor));
        setSelectedIcon(ImageUtil.dye(checkedIcon, iconColor));
    }
}
