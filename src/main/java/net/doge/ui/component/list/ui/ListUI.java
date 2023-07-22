package net.doge.ui.component.list.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 列表自定义 UI
 * @Date 2020/12/13
 */
public class ListUI extends BasicListUI {
    private int highlightIndex;
    private ListModel emptyModel = new DefaultListModel();

    public ListUI(int highlightIndex) {
        this.highlightIndex = highlightIndex;
    }

    public void setCellHeight(int index, int h) {
        if (index >= cellHeights.length) return;
        cellHeights[index] = h;
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
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, row != highlightIndex ? 0.7f : 1));
            super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
