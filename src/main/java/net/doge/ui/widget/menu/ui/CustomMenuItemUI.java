package net.doge.ui.widget.menu.ui;

import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleStorage;

import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 菜单项元素标签自定义 UI
 * @Date 2020/12/13
 */
public class CustomMenuItemUI extends BasicMenuItemUI {
    public CustomMenuItemUI() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        selectionForeground = textColor;
        selectionBackground = Colors.TRANSPARENT;
    }
}
