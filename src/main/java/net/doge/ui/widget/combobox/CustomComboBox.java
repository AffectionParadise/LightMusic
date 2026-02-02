package net.doge.ui.widget.combobox;

import lombok.Getter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.combobox.popup.CustomComboPopup;
import net.doge.ui.widget.combobox.ui.base.CustomComboBoxUI;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomComboBox<T> extends JComboBox<T> implements ExtendedOpacitySupported {
    private boolean drawBgIncreasing;
    protected Timer drawBgTimer;
    protected final float startBgAlpha = 0.15f;
    protected final float destBgAlpha = 0.3f;
    @Getter
    protected float bgAlpha = startBgAlpha;
    @Getter
    private float extendedOpacity = 1f;

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
                transitionDrawBg(true);
                showPopup();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                transitionDrawBg(false);
                Point p = e.getPoint();
                if (getBounds().contains(p) || getPopup().getBounds().contains(p) || getArrowButton().getBounds().contains(p))
                    return;
                hidePopup();
            }
        });

        drawBgTimer = new Timer(2, e -> {
            if (drawBgIncreasing) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(startBgAlpha, bgAlpha - 0.005f);
            if (bgAlpha <= startBgAlpha || bgAlpha >= destBgAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void transitionDrawBg(boolean drawBgIncreasing) {
        this.drawBgIncreasing = drawBgIncreasing;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    public CustomButton getArrowButton() {
        return ((CustomComboBoxUI) getUI()).getArrowButton();
    }

    public CustomComboPopup getPopup() {
        return ((CustomComboBoxUI) getUI()).getPopup();
    }

    @Override
    public void showPopup() {
        if (isPopupVisible()) return;
        super.showPopup();
    }

    @Override
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
