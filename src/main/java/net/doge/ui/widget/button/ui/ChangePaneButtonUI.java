package net.doge.ui.widget.button.ui;

import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 为 ChangePaneButton 设定的自定义 UI，图标添加遮罩
 * @Date 2020/12/13
 */
public class ChangePaneButtonUI extends BasicButtonUI {
    private boolean drawMask;
    private boolean drawMaskIncreasing;
    protected Timer drawMaskTimer;
    protected float maskAlpha;
    protected final float destMaskAlpha = 0.3f;
    public static BufferedImage frameImg = LMIconManager.getImage("control.frame");

    public ChangePaneButtonUI(MainFrame f) {
        frameImg = ImageUtil.dye(frameImg, f.currUIStyle.getIconColor());

        drawMaskTimer = new Timer(2, e -> {
            if (drawMaskIncreasing) maskAlpha = Math.min(destMaskAlpha, maskAlpha + 0.005f);
            else maskAlpha = Math.max(0f, maskAlpha - 0.005f);
            if (maskAlpha >= destMaskAlpha) drawMaskTimer.stop();
            else if (maskAlpha <= 0f) {
                drawMask = false;
                drawMaskTimer.stop();
            }
            f.changePaneButton.repaint();
        });
    }

    public void transitionDrawMask(boolean drawMaskIncreasing) {
        this.drawMask = true;
        this.drawMaskIncreasing = drawMaskIncreasing;
        if (drawMaskTimer.isRunning()) return;
        drawMaskTimer.start();
    }

    @Override
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        super.paintIcon(g, c, iconRect);
        // 画遮罩
        if (drawMask) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(Colors.BLACK);
            GraphicsUtil.srcOver(g2d, maskAlpha);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height, arc, arc);
            // 画框图
            GraphicsUtil.srcOver(g2d, Math.min(1f, maskAlpha * 3));
            g2d.drawImage(frameImg, iconRect.x, iconRect.y, null);
            GraphicsUtil.srcOver(g2d);
        }
    }
}
