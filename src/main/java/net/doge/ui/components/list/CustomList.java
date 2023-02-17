package net.doge.ui.components.list;

import javax.swing.*;

public class CustomList<E> extends JList<E> {

    public CustomList() {
        setOpaque(false);
    }

    @Override
    public void setModel(ListModel<E> model) {
        if (getModel() == model) return;
        super.setModel(model);
    }
}
