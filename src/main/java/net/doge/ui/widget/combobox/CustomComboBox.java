package net.doge.ui.widget.combobox;

import lombok.Getter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.combobox.ui.base.ComboBoxUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomComboBox<T> extends JComboBox<T> {
    private boolean entered;
    protected Timer drawBgTimer;
    protected final float startAlpha = 0.15f;
    protected final float destAlpha = 0.3f;
    @Getter
    protected float alpha = startAlpha;

    public CustomComboBox() {
        setOpaque(false);
        setFocusable(false);
        setLightWeightPopupEnabled(false);
        setMaximumRowCount(15);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setEntered(true);
                showPopup();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setEntered(false);
                Point p = e.getPoint();
                if (getBounds().contains(p) || getPopup().getBounds().contains(p) || getArrowButton().getBounds().contains(p))
                    return;
                hidePopup();
            }
        });

        drawBgTimer = new Timer(2, e -> {
            if (entered) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(startAlpha, alpha - 0.005f);
            if (alpha <= startAlpha || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setEntered(boolean entered) {
        if (this.entered == entered) return;
        this.entered = entered;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    public CustomButton getArrowButton() {
        return ((ComboBoxUI) getUI()).getArrowButton();
    }

    public CustomComboPopup getPopup() {
        return ((ComboBoxUI) getUI()).getPopup();
    }

    @Override
    public void showPopup() {
        if(isPopupVisible()) return;
        super.showPopup();
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
