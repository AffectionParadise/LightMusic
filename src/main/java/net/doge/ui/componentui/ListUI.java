package net.doge.ui.componentui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 列表自定义 UI
 * @Date 2020/12/13
 */
public class ListUI extends BasicListUI {
    private int highlightIndex;

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
            } catch (ArrayIndexOutOfBoundsException e) {

            }
        }
    }

    @Override
    protected void paintCell(Graphics g,
                             int row,
                             Rectangle rowBounds,
                             ListCellRenderer cellRenderer,
                             ListModel dataModel,
                             ListSelectionModel selModel,
                             int leadIndex) {
        Graphics2D g2d = (Graphics2D) g;
        if (row != highlightIndex) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        } else {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        try {
            super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
