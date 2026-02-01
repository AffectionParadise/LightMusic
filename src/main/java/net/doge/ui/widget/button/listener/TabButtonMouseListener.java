package net.doge.ui.widget.button.listener;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.TabButton;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
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
    private MainFrame f;

    public TabButtonMouseListener(TabButton b, MainFrame f) {
        this.b = b;
        this.f = f;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!b.isEnabled()) return;
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        Color bic = ColorUtil.brighter(iconColor);
        if (b.getIcon() != null) b.setIcon(ImageUtil.dye((ImageIcon) b.getIcon(), bic));
        b.setForeground(btc);
        b.transitionDrawBg(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!b.isEnabled()) return;
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        if (b.getIcon() != null) b.setIcon(ImageUtil.dye((ImageIcon) b.getIcon(), iconColor));
        b.setForeground(textColor);
        b.transitionDrawBg(b.isActive());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color dtc = ColorUtil.darker(textColor);
        Color dic = ColorUtil.darker(iconColor);
        if (b.getIcon() != null) b.setIcon(ImageUtil.dye((ImageIcon) b.getIcon(), dic));
        b.setForeground(dtc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        Color bic = ColorUtil.brighter(iconColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        if (b.getIcon() != null) b.setIcon(ImageUtil.dye((ImageIcon) b.getIcon(), bic));
        b.setForeground(c ? btc : textColor);
    }
}
