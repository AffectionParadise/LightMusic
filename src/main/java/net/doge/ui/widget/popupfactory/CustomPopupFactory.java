package net.doge.ui.widget.popupfactory;

import net.doge.ui.widget.tooltip.CustomToolTip;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomPopupFactory extends PopupFactory {
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y) {
        Popup popup = super.getPopup(owner, contents, x, y);
        // 修复 ToolTip 偶尔出现的白色背景
        if (contents instanceof CustomToolTip) {
            // 中量级弹层的 AWT Panel 会绘制不透明背景，改用支持透明背景的窗口
            if (SwingUtilities.getAncestorOfClass(Panel.class, contents) != null) {
                popup.hide();
                popup = new Popup(owner, contents, x, y) {
                };
            }
            SwingUtil.makePopupWindowTransparent(contents);
        }
        return popup;
    }
}
