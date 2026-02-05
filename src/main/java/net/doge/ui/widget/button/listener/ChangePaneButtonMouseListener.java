package net.doge.ui.widget.button.listener;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.widget.button.ChangePaneButton;
import net.doge.ui.widget.button.ui.ChangePaneButtonUI;
import net.doge.util.ui.ColorUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class ChangePaneButtonMouseListener extends MouseAdapter {
    private ChangePaneButton b;

    public ChangePaneButtonMouseListener(ChangePaneButton b) {
        this.b = b;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ChangePaneButtonUI ui = (ChangePaneButtonUI) b.getUI();
        ui.transitionDrawMask(true);
        b.setUI(ui);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ChangePaneButtonUI ui = (ChangePaneButtonUI) b.getUI();
        ui.transitionDrawMask(false);
        b.setUI(ui);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        b.setForeground(ColorUtil.darker(UIStyleStorage.currUIStyle.getTextColor()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        b.setForeground(UIStyleStorage.currUIStyle.getTextColor());
    }
}
