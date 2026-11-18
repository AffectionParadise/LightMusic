package net.doge.ui.widget.list;

import net.doge.ui.widget.list.transferhandler.ListItemTransferHandler;

import javax.swing.*;

public class CustomList<E> extends JList<E> {
    public CustomList() {
        init();
    }

    public CustomList(ListModel<E> model) {
        super(model);
        init();
    }

    private void init() {
        setOpaque(false);
        // 元素横向排布时自适应宽度
        setVisibleRowCount(0);
        // 拖放支持
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        setTransferHandler(new ListItemTransferHandler<>());
    }

    @Override
    public void setModel(ListModel<E> model) {
        if (getModel() == model) return;
        super.setModel(model);
    }

    @Override
    public void setFixedCellWidth(int width) {
        if (getFixedCellWidth() == width) return;
        super.setFixedCellWidth(width);
    }
}
