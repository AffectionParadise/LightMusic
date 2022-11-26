package net.doge.ui.components;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomComboBox<T> extends JComboBox<T> {

    private boolean entered;

    public CustomComboBox() {
        super();
        setOpaque(false);
        setFocusable(false);
        setLightWeightPopupEnabled(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setEntered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setEntered(false);
            }
        });
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
        repaint();
    }

    public boolean isEntered() {
        return entered;
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
