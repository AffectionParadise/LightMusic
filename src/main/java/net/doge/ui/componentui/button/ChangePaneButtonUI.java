package net.doge.ui.componentui.button;

import net.doge.constant.ui.Colors;
import net.doge.constant.system.SimplePath;
import net.doge.ui.MainFrame;
import net.doge.util.ImageUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description 为 ChangePaneButton 设定的自定义 UI，图标添加遮罩
 * @Date 2020/12/13
 */
public class ChangePaneButtonUI extends BasicButtonUI {
    private boolean drawMask = false;
    protected Timer drawMaskTimer;
    protected float alpha;
    protected final float destAlpha = 0.3f;
    public static BufferedImage frameImg = ImageUtil.read(SimplePath.ICON_PATH + "frame.png");
    private MainFrame f;

    public ChangePaneButtonUI(MainFrame f) {
        this.f = f;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Colors.BLACK);
        g2d.fillRoundRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height, 10, 10);

        // 画框图
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1, alpha * 3)));
        g2d.drawImage(frameImg, iconRect.x, iconRect.y, null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
    }
}
