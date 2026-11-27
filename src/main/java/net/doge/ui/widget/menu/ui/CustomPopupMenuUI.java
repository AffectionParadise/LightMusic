package net.doge.ui.widget.menu.ui;

import net.doge.constant.core.ui.Colors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;

public class CustomPopupMenuUI extends BasicPopupMenuUI {
    @Override
    public Popup getPopup(JPopupMenu menu, int x, int y) {
        Popup popup = super.getPopup(menu, x, y);
        // PopupFactory 已挂载菜单，此时弹出窗口还未显示
        Window window = SwingUtilities.getWindowAncestor(menu);
        if (window instanceof JWindow && !window.isVisible()) {
            // 若弹窗继承了父窗口的透明背景，AWT 需要一次从不透明到透明的切换才会同步内容层
            if (!window.isBackgroundSet() && !window.isOpaque()) window.setBackground(Colors.BLACK);
            window.setBackground(Colors.TRANSPARENT);
        }
        return popup;
    }
}
