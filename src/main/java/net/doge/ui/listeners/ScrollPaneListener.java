package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
import net.doge.ui.componentui.ScrollBarUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 滚动条的监听器
 * @Date 2021/1/10
 */
public class ScrollPaneListener extends MouseAdapter {
    private JScrollPane js;
    private PlayerFrame f;

    public ScrollPaneListener(JScrollPane js, PlayerFrame f) {
        this.js = js;
        this.f = f;
        js.getVerticalScrollBar().addMouseListener(this);
        js.getViewport().getView().addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JScrollBar vs = js.getVerticalScrollBar();
        ScrollBarUI ui = (ScrollBarUI) vs.getUI();
        ui.setActive(true);
        vs.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JScrollBar vs = js.getVerticalScrollBar();
        ScrollBarUI ui = (ScrollBarUI) vs.getUI();
        ui.setActive(false);
        vs.repaint();
    }
}
