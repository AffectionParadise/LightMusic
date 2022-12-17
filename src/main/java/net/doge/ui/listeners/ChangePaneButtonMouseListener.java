package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.componentui.ChangePaneButtonUI;
import net.doge.utils.ColorUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class ChangePaneButtonMouseListener extends MouseAdapter {
    private CustomButton b;
    private ChangePaneButtonUI ui;
    private PlayerFrame f;

    public ChangePaneButtonMouseListener(CustomButton b, ChangePaneButtonUI ui, PlayerFrame f) {
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
        b.setForeground(ColorUtils.darker(f.currUIStyle.getTextColor()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        b.setForeground(f.currUIStyle.getTextColor());
    }
}
