package net.doge.ui.widget.button;

import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.tooltip.CustomToolTip;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomButton extends BaseButton {
    private boolean drawBg;
    private Timer drawBgTimer;
    private float alpha;
    private final float destAlpha = 0.2f;

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
        Graphics2D g2d = GraphicsUtil.setup(g);
        int w = getWidth(), h = getHeight();
        // 画背景
        GraphicsUtil.srcOver(g2d, alpha);
        if (w / h >= 2) {
            g2d.setColor(getForeground());
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, w, h, arc, arc);
        } else {
            BufferedImage img = ImageUtil.width(maskImg, w);
            g2d.drawImage(img, 0, 0, w, h, this);
        }
        GraphicsUtil.srcOver(g2d);
        super.paintComponent(g);
    }
}
