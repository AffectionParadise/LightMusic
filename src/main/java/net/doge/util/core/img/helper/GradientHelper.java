package net.doge.util.core.img.helper;

import com.jhlabs.image.GradientFilter;
import net.doge.util.core.img.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

/**
 * 用于 {@link BufferedImage} 的线性渐变图像生成
 */
public class GradientHelper {
    /**
     * 返回一个提取原图一对视觉美观的颜色，并生成渐变的图像
     *
     * @param source 源图像；它绝不会被更改
     * @param width  新图像宽度
     * @param height 新图像高度
     */
    public static BufferedImage render(BufferedImage source, int width, int height) {
        Objects.requireNonNull(source, "source");
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("宽高必须为正整数");
        List<Color> pair = PaletteHelper.extractPairVisualAppeal(source);
        Color ca = pair.get(0), cb = pair.get(1);
        BufferedImage output = ImageUtil.createTransparentImage(width, height);
        GradientFilter gf = new GradientFilter(new Point(0, 0), new Point(width, height), ca.getRGB(), cb.getRGB(), false, GradientFilter.LINEAR, GradientFilter.INT_LINEAR);
//        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
//        GradientPaint gp = new GradientPaint(0, 0, c1, w, 0, c2);
//        LinearGradientPaint lgp = new LinearGradientPaint(0, 0, w, h, new float[]{0, 0.5f, 1}, new Color[]{c1, c2, c3});
//        g2d.setPaint(lgp);
//        g2d.fillRect(0, 0, w, h);
//        g2d.dispose();
        return gf.filter(output, null);
    }
}
