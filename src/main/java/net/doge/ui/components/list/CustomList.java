package net.doge.ui.components.list;

import javax.swing.*;

public class CustomList<E> extends JList<E> {

    public CustomList() {
        setOpaque(false);
        // 横向滚动时自适应宽度
        setVisibleRowCount(0);

//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentShown(ComponentEvent e) {
//                // 单列和横向时调整单元格宽度
//                setFixedCellWidth(isVerticalOrientation() ? getVisibleRect().width - 10 : 200);
//            }
//        });
    }

    @Override
    public void setModel(ListModel<E> model) {
        if (getModel() == model) return;
        super.setModel(model);
    }

//    public boolean isVerticalOrientation() {
//        return getLayoutOrientation() == VERTICAL;
//    }

    @Override
    public void setFixedCellWidth(int width) {
        if (getFixedCellWidth() == width) return;
        super.setFixedCellWidth(width);
    }
}
