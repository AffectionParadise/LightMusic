package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
import net.doge.ui.components.button.CustomButton;
import net.doge.ui.components.button.TabButton;
import net.doge.utils.ColorUtils;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class ButtonMouseListener extends MouseAdapter {
    private CustomButton b;
    private PlayerFrame f;

    public ButtonMouseListener(CustomButton b, PlayerFrame f) {
        this.b = b;
        this.f = f;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color btc = ColorUtils.brighter(textColor);
        Color bic = ColorUtils.brighter(iconColor);
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), bic));
        b.setForeground(btc);
        if (!(b instanceof TabButton)) b.setDrawBg(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), iconColor));
        b.setForeground(textColor);
        if (!(b instanceof TabButton)) b.setDrawBg(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color dtc = ColorUtils.darker(textColor);
        Color dic = ColorUtils.darker(iconColor);
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), dic));
        b.setForeground(dtc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Color iconColor = f.currUIStyle.getIconColor();
        Color textColor = f.currUIStyle.getTextColor();
        Color btc = ColorUtils.brighter(textColor);
        Color bic = ColorUtils.brighter(iconColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), bic));
        b.setForeground(c ? btc : textColor);
    }
}
