package net.doge.ui.widget.list.ui;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Doge
 * @description 弹簧列表自定义 UI
 * @date 2020/12/13
 */
public class LyricListUI extends BasicListUI {
    public void setCellHeight(int index, int h) {
        if (cellHeights == null) return;
        if (index >= cellHeights.length) return;
        cellHeights[index] = h;
    }

    @Override
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 左键拖动由外部处理，避免默认选区拖动再次滚动视口
                if (SwingUtilities.isLeftMouseButton(e)) return;
                super.mouseDragged(e);
            }
        };
    }

    @Override
    protected void updateLayoutState() {
        boolean cont = true;
        while (cont) {
            try {
                super.updateLayoutState();
                cont = false;
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer, ListModel dataModel, ListSelectionModel selModel, int leadIndex) {
        try {
            super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
