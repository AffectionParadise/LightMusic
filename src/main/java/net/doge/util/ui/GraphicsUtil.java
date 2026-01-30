package net.doge.util.ui;

import java.awt.*;

/**
 * @Author Doge
 * @Description 画笔工具类
 * @Date 2020/12/15
 */
public class GraphicsUtil {
    /**
     * 设置画笔渲染参数
     *
     * @param g
     * @return
     */
    public static Graphics2D setup(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 以下部分参数设置后会导致性能严重下降，谨慎使用！
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 清晰文字
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        // 清晰图像
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // 自然阴影
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        return g2d;
    }

    /**
     * 设置画笔不透明度
     *
     * @param g
     * @return
     */
    public static Graphics2D srcOver(Graphics g, float alpha) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        return g2d;
    }

    /**
     * 重置画笔不透明度
     *
     * @param g
     * @return
     */
    public static Graphics2D srcOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.SrcOver);
        return g2d;
    }
}