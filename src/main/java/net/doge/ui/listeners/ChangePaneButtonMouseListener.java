package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
import net.doge.ui.componentui.ChangePaneButtonUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class ChangePaneButtonMouseListener extends MouseAdapter {
    private JButton b;
    private ChangePaneButtonUI ui;
    private PlayerFrame f;

    public ChangePaneButtonMouseListener(JButton b, ChangePaneButtonUI ui, PlayerFrame f) {
        this.b = b;
        this.ui = ui;
        this.f = f;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ui.setDrawMask(true);
        b.setUI(ui);
//        b.setForeground(f.getCurrUIStyle().getButtonColor().brighter());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ui.setDrawMask(false);
        b.setUI(ui);
//        b.setForeground(f.getCurrUIStyle().getButtonColor());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        b.setForeground(f.getCurrUIStyle().getButtonColor().darker());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        b.setForeground(f.getCurrUIStyle().getButtonColor());
    }


}
