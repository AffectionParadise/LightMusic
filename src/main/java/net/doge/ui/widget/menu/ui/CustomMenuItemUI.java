package net.doge.ui.widget.menu.ui;

import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleStorage;

import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

/**
 * @author Doge
 * @description 菜单项元素标签自定义 UI
 * @date 2020/12/13
 */
public class CustomMenuItemUI extends BasicMenuItemUI {
    public CustomMenuItemUI() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        selectionForeground = textColor;
        selectionBackground = Colors.TRANSPARENT;
    }
}
