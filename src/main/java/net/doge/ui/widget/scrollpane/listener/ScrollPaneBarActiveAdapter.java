package net.doge.ui.widget.scrollpane.listener;

import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Doge
 * @description 滚动条的监听器
 * @date 2021/1/10
 */
public class ScrollPaneBarActiveAdapter extends MouseAdapter {
    private CustomScrollPane sp;

    public ScrollPaneBarActiveAdapter(CustomScrollPane sp) {
        this.sp = sp;

        sp.getVBar().addMouseListener(this);
        CustomPanel panel = (CustomPanel) sp.getViewportView();
        panel.addMouseListener(this);
        // Panel 中的某些 Label 添加了自身的鼠标事件会导致父容器无法接收事件，因此单独给 Label 添加
        for (Component comp : panel.getComponents()) comp.addMouseListener(this);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        sp.setVBarActive(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // 将内部小组件的相对坐标转为 ScrollPane 整体坐标
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), sp);
        if (sp.isVBarAdjusting() || sp.contains(p)) return;
        sp.setVBarActive(false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // 将内部小组件的相对坐标转为 ScrollPane 整体坐标
        Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), sp);
        if (sp.contains(p)) return;
        sp.setVBarActive(false);
    }
}
