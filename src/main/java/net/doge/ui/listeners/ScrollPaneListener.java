package net.doge.ui.listeners;

import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomScrollPane;
import net.doge.ui.componentui.ScrollBarUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 滚动条的监听器
 * @Date 2021/1/10
 */
public class ScrollPaneListener extends MouseAdapter {
    private CustomScrollPane sp;
    private PlayerFrame f;

    public ScrollPaneListener(CustomScrollPane sp, PlayerFrame f) {
        this.sp = sp;
        this.f = f;
        sp.getVerticalScrollBar().addMouseListener(this);
        sp.getViewport().getView().addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ScrollBarUI ui = (ScrollBarUI) sp.getVUI();
        ui.setActive(true);
        sp.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ScrollBarUI ui = (ScrollBarUI) sp.getVUI();
        ui.setActive(false);
        sp.repaint();
    }
}
