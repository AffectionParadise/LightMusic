package net.doge.ui.widget.button.ui;

import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;

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
    private boolean drawMask = false;
    protected Timer drawMaskTimer;
    protected float alpha;
    protected final float destAlpha = 0.3f;
    public static BufferedImage frameImg = LMIconManager.getImage("control.frame");

    public ChangePaneButtonUI(MainFrame f) {
        frameImg = ImageUtil.dye(frameImg, f.currUIStyle.getIconColor());

        drawMaskTimer = new Timer(2, e -> {
            if (drawMask) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(0, alpha - 0.005f);
            if (alpha <= 0 || alpha >= destAlpha) drawMaskTimer.stop();
            f.changePaneButton.repaint();
        });
    }

    public void setDrawMask(boolean drawMask) {
        if (this.drawMask == drawMask) return;
        this.drawMask = drawMask;
        if (drawMaskTimer.isRunning()) return;
        drawMaskTimer.start();
    }

    @Override
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        super.paintIcon(g, c, iconRect);

        // 画遮罩
        Graphics2D g2d = GraphicsUtil.setup(g);
        GraphicsUtil.srcOver(g2d, alpha);
        g2d.setColor(Colors.BLACK);
        g2d.fillRoundRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height, 10, 10);

        // 画框图
        GraphicsUtil.srcOver(g2d, Math.min(1, alpha * 3));
        g2d.drawImage(frameImg, iconRect.x, iconRect.y, null);

        GraphicsUtil.srcOver(g2d);
    }
}
