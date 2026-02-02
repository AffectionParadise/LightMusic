package net.doge.ui.widget.list.ui;

import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 列表自定义 UI
 * @Date 2020/12/13
 */
public class CustomListUI extends BasicListUI {
    private int highlightIndex;

    public CustomListUI(int highlightIndex) {
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
            Graphics2D g2d = GraphicsUtil.setup(g);
            GraphicsUtil.srcOver(g2d, row != highlightIndex ? 0.7f : 1f);
            super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
