package net.doge.ui.widget.button.listener;

import net.doge.ui.MainFrame;
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
    private ChangePaneButtonUI ui;
    private MainFrame f;

    public ChangePaneButtonMouseListener(ChangePaneButton b, ChangePaneButtonUI ui, MainFrame f) {
        this.b = b;
        this.ui = ui;
        this.f = f;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ui.setDrawMask(true);
        b.setUI(ui);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ui.setDrawMask(false);
        b.setUI(ui);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        b.setForeground(ColorUtil.darker(f.currUIStyle.getTextColor()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        b.setForeground(f.currUIStyle.getTextColor());
    }
}
