package net.doge.ui.components.button;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    protected boolean drawBg;

    public CustomButton() {
        super();
        init();
    }

    public CustomButton(String text) {
        super(text);
        init();
    }

    public CustomButton(Icon icon) {
        super(icon);
        init();
    }

    public CustomButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
        repaint();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip toolTip = new CustomToolTip(this);
        toolTip.setVisible(false);
        return toolTip;
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (!b) setDrawBg(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (drawBg && !(this instanceof TabButton)) {
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
