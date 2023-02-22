package net.doge.ui.components.combobox;

import net.doge.constants.Fonts;
import net.doge.ui.components.button.CustomButton;
import net.doge.ui.componentui.ComboBoxUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomComboBox<T> extends JComboBox<T> {
    private boolean entered;
    protected Timer drawBgTimer;
    protected final float startAlpha = 0.15f;
    protected final float destAlpha = 0.3f;
    protected float alpha = startAlpha;

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

        drawBgTimer = new Timer(1, e -> {
            if (entered) alpha = Math.min(destAlpha, alpha + 0.002f);
            else alpha = Math.max(startAlpha, alpha - 0.002f);
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

    public boolean isEntered() {
        return entered;
    }

    public float getAlpha() {
        return alpha;
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
