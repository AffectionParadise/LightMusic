//package net.doge.ui.components.list;
//
//import net.doge.ui.components.panel.CustomPanel;
//
//import javax.swing.*;
//import javax.swing.event.ListDataEvent;
//import javax.swing.event.ListDataListener;
//import javax.swing.event.ListSelectionEvent;
//import javax.swing.event.ListSelectionListener;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public abstract class GridList<E> extends CustomPanel {
//    private ListSelectionModel selectionModel;
//    private ListModel<E> dataModel;
//
//    private ListDataListener dataListener;
//    private ListSelectionListener selectionListener;
//
//    public GridList() {
//        setLayout(new GridLayout(5, 1));
//        initModel();
//    }
//
//    private void initModel() {
//        dataListener = new ListDataListener() {
//            @Override
//            public void intervalAdded(ListDataEvent e) {
//                add(buildInnerPanel(dataModel.getElementAt(e.getIndex0())));
//            }
//
//            @Override
//            public void intervalRemoved(ListDataEvent e) {
//                remove(e.getIndex0());
//            }
//
//            @Override
//            public void contentsChanged(ListDataEvent e) {
//
//            }
//        };
//        selectionModel = new DefaultListSelectionModel();
//        selectionListener = new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//
//            }
//        }
//    }
//
//    // 获取单元格
//    public abstract CustomPanel getInnerPanel(E e);
//
//    // 初始化单元格
//    private CustomPanel initInnerPanel(CustomPanel p) {
//        p.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
//            }
//        });
//    }
//
//    // 根据选中更新单元格背景
//    private void updateDrawBg(int index) {
//        Component[] components = getComponents();
//        for (int i = 0, len = components.length; i < len; i++) {
//            CustomPanel panel = (CustomPanel) components[i];
//            panel.setDrawBg(index == i);
//        }
//    }
//
//    public ListModel<E> getModel() {
//        return dataModel;
//    }
//
//    public void setModel(ListModel<E> model) {
//        if (model == null) throw new IllegalArgumentException("model must be non null");
//        if (dataModel == model) return;
//        if (dataModel != null) dataModel.removeListDataListener(dataListener);
//        dataModel = model;
//        dataModel.addListDataListener(dataListener);
//        clearSelection();
//    }
//
//    public void clearSelection() {
//        selectionModel.clearSelection();
//    }
//
//    public int getSelectionIndex() {
//        return selectionModel.getMinSelectionIndex();
//    }
//
//    public void setSelectedIndex(int index) {
//        selectionModel.setSelectionInterval(index, index);
//    }
//
//    public E getSelectedValue() {
//        int i = getSelectionIndex();
//        return (i == -1) ? null : getModel().getElementAt(i);
//    }
//}
