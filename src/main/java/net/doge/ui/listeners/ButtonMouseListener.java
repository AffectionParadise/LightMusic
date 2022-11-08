package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
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
    private JButton b;
    private PlayerFrame f;

    public ButtonMouseListener(JButton b, PlayerFrame f) {
        this.b = b;
        this.f = f;

        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        Color brighterColor = ColorUtils.brighter(buttonColor);
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), brighterColor));
        b.setForeground(brighterColor);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), buttonColor));
        b.setForeground(buttonColor);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Color buttonColor = f.getCurrUIStyle().getButtonColor();
            Color darkerColor = ColorUtils.darker(buttonColor);
            if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), darkerColor));
            b.setForeground(darkerColor);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        Color brighterColor = ColorUtils.brighter(buttonColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), brighterColor));
        b.setForeground(c ? brighterColor : buttonColor);
    }
}
