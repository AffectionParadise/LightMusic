package net.doge.ui.widget.menu.ui;

import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;

public class CustomPopupMenuUI extends BasicPopupMenuUI {
    @Override
    public Popup getPopup(JPopupMenu menu, int x, int y) {
        Popup popup = super.getPopup(menu, x, y);
        // PopupFactory 已挂载菜单，此时弹出窗口还未显示
        SwingUtil.makePopupWindowTransparent(menu);
        return popup;
    }
}
