//package net.doge.ui.components.list;
//
//import net.doge.ui.components.panel.CustomPanel;
//
//import javax.swing.*;
//import javax.swing.event.ListDataEvent;
//import javax.swing.event.ListDataListener;
//import java.util.LinkedList;
//
//public class GridList<E> extends CustomPanel {
//    private ListSelectionModel selectionModel;
//    private ListModel<E> dataModel;
//    private LinkedList<CustomPanel> panels;
//
//    public GridList() {
//        labels = new LinkedList<>();
//        dataModel.addListDataListener(new ListDataListener() {
//            @Override
//            public void intervalAdded(ListDataEvent e) {
//                updateLabels();
//            }
//
//            @Override
//            public void intervalRemoved(ListDataEvent e) {
//                updateLabels();
//            }
//
//            @Override
//            public void contentsChanged(ListDataEvent e) {
//
//            }
//        });
//    }
//
//    private void updateLabels() {
//        for (int i = 0, size = dataModel.getSize(); i < size; i++) {
//            E elem = dataModel.getElementAt(i);
//        }
//    }
//
//    public ListModel<E> getModel() {
//        return dataModel;
//    }
//
//    public void setModel(ListModel<E> model) {
//        if (model == null) throw new IllegalArgumentException("model must be non null");
//        dataModel = model;
//        clearSelection();
//    }
//
//    public void clearSelection() {
//        selectionModel.clearSelection();
//    }
//
//    public int getMinSelectionIndex() {
//        return selectionModel.getMinSelectionIndex();
//    }
//
//    public E getSelectedValue() {
//        int i = getMinSelectionIndex();
//        return (i == -1) ? null : getModel().getElementAt(i);
//    }
//}
