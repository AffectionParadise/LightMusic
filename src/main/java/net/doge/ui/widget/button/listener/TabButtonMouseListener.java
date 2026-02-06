package net.doge.ui.widget.button.listener;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.widget.button.TabButton;
import net.doge.util.ui.ColorUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class TabButtonMouseListener extends MouseAdapter {
    private TabButton b;

    public TabButtonMouseListener(TabButton b) {
        this.b = b;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!b.isEnabled()) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color iconColor = style.getIconColor();
        Color textColor = style.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        Color bic = ColorUtil.brighter(iconColor);
        b.updateIconColor(bic);
        b.setForeground(btc);
        b.transitionDrawBg(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!b.isEnabled()) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        b.updateIconStyle();
        b.setForeground(textColor);
        b.transitionDrawBg(b.isActive());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color iconColor = style.getIconColor();
        Color textColor = style.getTextColor();
        Color dtc = ColorUtil.darker(textColor);
        Color dic = ColorUtil.darker(iconColor);
        b.updateIconColor(dic);
        b.setForeground(dtc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        UIStyle style = UIStyleStorage.currUIStyle;
        Color iconColor = style.getIconColor();
        Color textColor = style.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        Color bic = ColorUtil.brighter(iconColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        b.updateIconColor(c ? bic : iconColor);
        b.setForeground(c ? btc : textColor);
    }
}
