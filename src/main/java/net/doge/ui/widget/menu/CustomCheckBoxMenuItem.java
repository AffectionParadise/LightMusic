package net.doge.ui.widget.menu;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 自定义勾选菜单项
 * @date 2020/12/13
 */
public class CustomCheckBoxMenuItem extends CustomMenuItem {
    // 选定勾图标
    private static ImageIcon tickIcon = LMIconManager.getIcon("menu.tick");

    public CustomCheckBoxMenuItem() {
        this(null);
    }

    public CustomCheckBoxMenuItem(String text) {
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
            setIcon(ImageUtil.dye(tickIcon, iconColor));
        } else setIcon(null);
    }
}
