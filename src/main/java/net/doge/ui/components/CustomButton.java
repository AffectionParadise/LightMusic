package net.doge.ui.components;

import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    private boolean drawBg;

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
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip tooltip = new CustomToolTip(this);
        // 存在性能问题，暂时不用
        tooltip.setVisible(false);
        return tooltip;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            // 画背景
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
