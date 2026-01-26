package net.doge.ui.widget.scrollpane.viewport;

import lombok.Setter;
import net.doge.constant.core.ui.core.Colors;
import net.doge.util.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomViewport extends JViewport {
    @Setter
    private boolean edgeFaded;
    private BufferedImage maskImg;

    private final int FADE_HEIGHT = 200;

    public CustomViewport() {
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        // 边缘渐隐
        if (edgeFaded) {
            Graphics2D g2d = (Graphics2D) g;
            // 获取尺寸
            int w = getWidth(), h = getHeight();
            // 创建临时图像用于合成
            if (maskImg == null || w != maskImg.getWidth() || h != maskImg.getHeight())
                maskImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgG2d = maskImg.createGraphics();
            // 清除图像
            imgG2d.setComposite(AlphaComposite.Clear);
            imgG2d.fillRect(0, 0, w, h);
            // 绘制组件到临时图像
            super.paint(imgG2d);
            // 绘制渐隐遮罩
            imgG2d.setComposite(AlphaComposite.DstIn);
            Color white = Colors.WHITE, transparent = ColorUtil.deriveAlphaColor(white, 0);
            float ratio = (float) FADE_HEIGHT / h;
            LinearGradientPaint fade = new LinearGradientPaint(0, 0, 0, h, new float[]{0, ratio, 1 - ratio, 1},
                    new Color[]{transparent, white, white, transparent});
            imgG2d.setPaint(fade);
            imgG2d.fillRect(0, 0, w, h);
            imgG2d.dispose();
            // 绘制到屏幕
            g2d.drawImage(maskImg, 0, 0, null);
        } else {
            super.paint(g);
        }
    }
}
