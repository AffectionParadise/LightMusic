package net.doge.ui.widget.button.listener;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.widget.button.DialogButton;
import net.doge.util.ui.ColorUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class DialogButtonMouseAdapter extends MouseAdapter {
    private DialogButton b;

    public DialogButtonMouseAdapter(DialogButton b) {
        this.b = b;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!b.isEnabled()) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        b.setForeground(btc);
        b.transitionHighlightBg(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!b.isEnabled()) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        b.setForeground(textColor);
        b.transitionHighlightBg(false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color dtc = ColorUtil.darker(textColor);
        b.setHighlightBg(false);
        b.setForeground(dtc);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color btc = ColorUtil.brighter(textColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        b.setHighlightBg(c);
        b.setForeground(c ? btc : textColor);
    }
}
