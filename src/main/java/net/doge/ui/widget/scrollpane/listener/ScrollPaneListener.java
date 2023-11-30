package net.doge.ui.widget.scrollpane.listener;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 滚动条的监听器
 * @Date 2021/1/10
 */
public class ScrollPaneListener extends MouseAdapter {
    private CustomScrollPane sp;
    private MainFrame f;

    public ScrollPaneListener(CustomScrollPane sp, MainFrame f) {
        this.sp = sp;
        this.f = f;
        sp.getVerticalScrollBar().addMouseListener(this);
        sp.getViewport().getView().addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        ScrollBarUI ui = (ScrollBarUI) sp.getVUI();
        ui.setActive(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ScrollBarUI ui = (ScrollBarUI) sp.getVUI();
        ui.setActive(false);
    }
}
