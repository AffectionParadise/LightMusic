package net.doge.ui.component.button;

import net.doge.constant.ui.Fonts;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomButton extends JButton {
    protected boolean drawBg;
    protected Timer drawBgTimer;
    protected float alpha;
    protected final float destAlpha = 0.2f;

    private static BufferedImage maskImg = LMIconManager.getImage("mask");

    public CustomButton() {
        this(null, null);
    }

    public CustomButton(String text) {
        this(text, null);
    }

    public CustomButton(Icon icon) {
        this(null, icon);
    }

    public CustomButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        drawBgTimer = new Timer(2, e -> {
            if (drawBg) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(0, alpha - 0.005f);
            if (alpha <= 0 || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setDrawBg(boolean drawBg) {
        if (this.drawBg == drawBg) return;
        this.drawBg = drawBg;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
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
    public void setForeground(Color fg) {
        maskImg = ImageUtil.dye(maskImg, fg);
        super.setForeground(fg);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (!(this instanceof TabButton) && !(this instanceof ChangePaneButton)) {
            Graphics2D g2d = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            // 画背景
            BufferedImage img = ImageUtil.width(maskImg, w);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            if (w / h >= 2) {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getForeground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
