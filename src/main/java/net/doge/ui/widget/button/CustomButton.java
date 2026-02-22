package net.doge.ui.widget.button;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.listener.CustomButtonMouseAdapter;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomButton extends BaseButton implements ExtendedOpacitySupported {
    private boolean drawBg;
    private boolean drawBgIncreasing;
    private Timer drawBgTimer;
    private float bgAlpha;
    private final float destBgAlpha = 0.2f;
    @Getter
    private float extendedOpacity = 1f;

    private static Color maskColor;
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
        addMouseListener(new CustomButtonMouseAdapter(this));

        drawBgTimer = new Timer(2, e -> {
            if (drawBgIncreasing) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(0f, bgAlpha - 0.005f);
            if (bgAlpha >= destBgAlpha) drawBgTimer.stop();
            else if (bgAlpha <= 0f) {
                drawBg = false;
                drawBgTimer.stop();
            }
            repaint();
        });
    }

    public void transitionDrawBg(boolean drawBgIncreasing) {
        this.drawBg = true;
        this.drawBgIncreasing = drawBgIncreasing;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (!b) transitionDrawBg(false);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        // 更新遮罩颜色
        if (maskColor != null && maskColor.equals(fg)) return;
        maskImg = ImageUtil.dye(maskImg, fg);
        maskColor = fg;
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
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            int w = getWidth(), h = getHeight();
            GraphicsUtil.srcOver(g2d, extendedOpacity * bgAlpha);
            if (w - h >= ScaleUtil.scale(10)) {
                g2d.setColor(getForeground());
                int arc = ScaleUtil.scale(10);
                g2d.fillRoundRect(0, 0, w, h, arc, arc);
            } else {
                BufferedImage img = ImageUtil.width(maskImg, w);
                g2d.drawImage(img, 0, 0, w, h, null);
            }
        }
        GraphicsUtil.srcOver(g, extendedOpacity);
        super.paintComponent(g);
    }
}
