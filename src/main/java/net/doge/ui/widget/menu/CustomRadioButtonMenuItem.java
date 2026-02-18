package net.doge.ui.widget.menu;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 自定义单选菜单项
 * @date 2020/12/13
 */
public class CustomRadioButtonMenuItem extends CustomMenuItem {
    // 选定点图标
    private static ImageIcon dotIcon = LMIconManager.getIcon("menu.dot");

    public CustomRadioButtonMenuItem() {
        this(null);
    }

    public CustomRadioButtonMenuItem(String text) {
        super(text);
    }

    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        updateIconStyle();
    }

    public void updateIconStyle() {
        if (isSelected()) {
            Color iconColor = UIStyleStorage.currUIStyle.getIconColor();
            setIcon(ImageUtil.dye(dotIcon, iconColor));
        } else setIcon(null);
    }
}
