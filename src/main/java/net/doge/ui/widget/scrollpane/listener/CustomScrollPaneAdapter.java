package net.doge.ui.widget.scrollpane.listener;

import net.doge.ui.widget.scrollpane.CustomScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 滚动条的监听器
 * @Date 2021/1/10
 */
public class CustomScrollPaneAdapter extends MouseAdapter {
    private CustomScrollPane sp;

    public CustomScrollPaneAdapter(CustomScrollPane sp) {
        this.sp = sp;

        sp.getVBar().addMouseListener(this);
        sp.getViewportView().addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        sp.setVBarActive(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        sp.setVBarActive(false);
    }
}
