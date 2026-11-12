package net.doge.util.core.img;

import cn.hutool.core.img.ImgUtil;
import com.jhlabs.image.*;
import com.luciad.imageio.webp.WebPReadParam;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constant.core.os.Format;
import net.doge.constant.core.ui.Colors;
import net.doge.constant.core.ui.image.BlurConstants;
import net.doge.util.core.RandomUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.img.helper.FluidBackground;
import net.doge.util.core.log.LogUtil;
import net.doge.util.core.math.MathUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Doge
 * @description
 * @date 2020/12/9
 */
public class ImageUtil {
    // 高斯模糊过滤器
    private static final GaussianFilter gaussianFilter = new GaussianFilter();
    // 分析布朗运动过滤器
    private static final FBMFilter fbmFilter = new FBMFilter();
    // 曝光过滤器
    private static final ExposureFilter exposureFilter = new ExposureFilter();
    // 饱和度过滤器
    private static final SaturationFilter saturationFilter = new SaturationFilter();
    // 阴影过滤器
    private static final ShadowFilter shadowFilter = new ShadowFilter();
    // 光辉阴影过滤器
    private static final ShadowFilter glowShadowFilter = new ShadowFilter();
    // 边框阴影过滤器
    private static final ShadowFilter borderShadowFilter = new ShadowFilter();
    // 阴影厚度
    private static final int SHADOW_THICKNESS = 30;

    static {
        fbmFilter.setLacunarity(0.35f);
        fbmFilter.setH(5f);
        fbmFilter.setBasisType(FBMFilter.RIDGED);

        shadowFilter.setRadius(20f);
        shadowFilter.setDistance(0f);
        shadowFilter.setOpacity(0.65f);

        glowShadowFilter.setRadius(10f);
        glowShadowFilter.setDistance(0f);
        glowShadowFilter.setOpacity(1f);
        glowShadowFilter.setShadowOnly(true);

        borderShadowFilter.setRadius(SHADOW_THICKNESS);
        borderShadowFilter.setDistance(0f);
        borderShadowFilter.setOpacity(0.65f);
    }

    /**
     * 从文件路径读取图片
     *
     * @param source 图片路径
     * @return
     */
    public static BufferedImage read(String source) {
        try {
            return Thumbnails.of(source).scale(1).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 从 File 读取图片
     *
     * @param f 图片文件
     * @return
     */
    public static BufferedImage read(File f) {
        try {
            return Thumbnails.of(f).scale(1).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 从流读取图片
     *
     * @param in 图片输入流
     * @return
     */
    public static BufferedImage read(InputStream in) {
        if (in == null) return null;
        try (InputStream _in = in) {
            return Thumbnails.of(_in).scale(1).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 从 url 读取 Webp 图像
     *
     * @param imgUrl 图片 url
     * @return
     */
    public static BufferedImage readWebp(String imgUrl) {
        if (StringUtil.isEmpty(imgUrl)) return null;
        try (InputStream in = getStream(imgUrl)) {
            if (in == null) return null;
            // Obtain a WebP ImageReader instance
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);
            // Configure the input on the ImageReader
            // 读取网络流用 MemoryCacheImageInputStream
            reader.setInput(new MemoryCacheImageInputStream(in));
            // Decode the image
            return reader.read(0, readParam);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 url 读取图片
     *
     * @param imgUrl 图片 url
     * @return
     */
    public static BufferedImage readFromUrl(String imgUrl) {
        if (StringUtil.isEmpty(imgUrl)) return null;
        if (imgUrl.endsWith(Format.WEBP)) return readWebp(imgUrl);
        return read(getStream(imgUrl));
    }

    /**
     * 从 url 获取图片流
     *
     * @param imgUrl 图片 url
     * @return
     */
    public static InputStream getStream(String imgUrl) {
        if (StringUtil.isEmpty(imgUrl)) return null;
        try {
            return HttpRequest.get(imgUrl)
                    .timeout(20)
                    .executeAsStream();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 导出为图片文件
     *
     * @param imgUrl 图像 url
     * @param dest   导出文件路径
     * @return
     */
    public static void toFile(String imgUrl, String dest) {
        toFile(imgUrl, new File(dest));
    }

    /**
     * 导出为图片文件
     *
     * @param imgUrl     图像 url
     * @param outputFile 导出文件
     * @return
     */
    public static void toFile(String imgUrl, File outputFile) {
        toFile(readFromUrl(imgUrl), outputFile);
    }

    /**
     * 导出为图片文件
     *
     * @param img  图像
     * @param dest 导出文件路径
     * @return
     */
    public static void toFile(BufferedImage img, String dest) {
        try {
            Thumbnails.of(img).scale(1).toFile(dest);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 导出为图片文件
     *
     * @param img        图像
     * @param outputFile 导出文件
     * @return
     */
    public static void toFile(BufferedImage img, File outputFile) {
        try {
            Thumbnails.of(img).scale(1).toFile(outputFile);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 将图片转为 bytes
     *
     * @param img 图片
     * @return
     */
    public static byte[] toBytes(BufferedImage img) {
        return ImgUtil.toBytes(img, ImgUtil.IMAGE_TYPE_PNG);
    }

    /**
     * 将 Base64 转为图片
     *
     * @param base64
     * @return
     */
    public static BufferedImage toImage(String base64) {
        try {
            return ImgUtil.toImage(base64);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 将图片转为一维像素数组
     *
     * @param img
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static int[] toPixels(BufferedImage img, int x, int y, int w, int h) {
        int[] pixels = new int[w * h];
        for (int idx = 0, i = x; i < h; i++)
            for (int j = y; j < w; j++)
                pixels[idx++] = img.getRGB(i, j);
        return pixels;
    }

    /**
     * 创建指定宽高的透明图片
     *
     * @param w
     * @param h
     * @return
     */
    public static BufferedImage createTransparentImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * 从指定 BufferedImage 创建透明图片
     *
     * @return
     */
    public static BufferedImage createTransparentImage(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage outputImg = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return outputImg;
    }

    /**
     * 消去图片透明度，换成黑底
     *
     * @param img
     * @return
     */
    public static BufferedImage eraseTransparency(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage outputImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return outputImg;
    }

    /**
     * 给图标着色，保留透明部分
     *
     * @param icon
     * @param color
     * @return
     */
    public static ImageIcon dye(ImageIcon icon, Color color) {
        if (icon == null) return null;
        return new ImageIcon(dye(icon.getImage(), color));
    }

    /**
     * 给图像着色，保留透明部分
     *
     * @param img
     * @return
     */
    public static BufferedImage dye(Image img, Color color) {
        int w = img.getWidth(null), h = img.getHeight(null);
        BufferedImage outputImg = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop);
//        final float diff = 15;
//        final float[] fractions = {0.333f, 0.667f, 1};
//        final Color[] colors = {ColorUtil.hsvDiffPick(color, -diff), color, ColorUtil.hsvDiffPick(color, diff)};
//        LinearGradientPaint lgp = new LinearGradientPaint(0, 0, w, h, fractions, colors);
//        g2d.setPaint(lgp);
        g2d.setColor(color);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        return outputImg;
    }

    /**
     * 返回纯色指定宽高的矩形图片
     *
     * @param w
     * @param h
     * @param color
     * @return
     */
    public static BufferedImage dyeRect(int w, int h, Color color) {
        if (color == null) return null;
        BufferedImage img = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        g2d.setColor(color);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        return img;
    }

    /**
     * 返回纯色指定宽高的圆角矩形图片
     *
     * @param w
     * @param h
     * @param color
     * @return
     */
    public static BufferedImage dyeRoundRect(int w, int h, Color color) {
        BufferedImage img = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        g2d.setColor(color);
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(0, 0, w, h, arc, arc);
        g2d.dispose();
        return img;
    }

    /**
     * 返回纯色指定宽度的圆形图片
     *
     * @param w
     * @param color
     * @return
     */
    public static BufferedImage dyeCircle(int w, Color color) {
        BufferedImage img = createTransparentImage(w, w);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        g2d.setColor(color);
        g2d.fillOval(0, 0, w, w);
        g2d.dispose();
        return img;
    }

    /**
     * 为图像设置圆角边框，保留透明度
     *
     * @param img
     * @param arc
     * @return
     */
    public static BufferedImage radius(BufferedImage img, double arc) {
        return radius(img, (int) (img.getWidth() * arc));
    }

    /**
     * 为图像设置圆角边框，保留透明度
     *
     * @param img
     * @param radius
     * @return
     */
    public static BufferedImage radius(BufferedImage img, int radius) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage outputImg = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.fillRoundRect(0, 0, w, h, radius, radius);
        g2d.setComposite(AlphaComposite.SrcIn);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return outputImg;
    }

    /**
     * 改变图像质量
     *
     * @param img
     * @param q
     * @return
     */
    public static BufferedImage quality(BufferedImage img, double q) {
        try {
            return Thumbnails.of(img).scale(1).outputQuality(q).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param img
     * @param width
     * @return
     */
    public static BufferedImage width(BufferedImage img, int width) {
        if (img == null) return null;
        try {
            return Thumbnails.of(img).width(width).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 等比例设置图片高度，返回新的 BufferedImage
     *
     * @param img
     * @param height
     * @return
     */
    public static BufferedImage height(BufferedImage img, int height) {
        try {
            return Thumbnails.of(img).height(height).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 设置图片宽度和高度，返回新的 BufferedImage
     *
     * @param img
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage forceSize(BufferedImage img, int width, int height) {
        try {
            return Thumbnails.of(img).forceSize(width, height).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 将宽高不相等的图片剪成正方形，保留中间部分
     *
     * @param img
     * @return
     */
    public static BufferedImage cropCenter(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        if (w < h) return region(img, 0, (h - w) / 2, w, w);
        else if (w > h) return region(img, (w - h) / 2, 0, h, h);
        return img;
    }

    /**
     * 裁剪图片
     *
     * @param img
     * @param x   左上角 x
     * @param y   左上角 y
     * @param w   宽
     * @param h   高
     * @return
     */
    public static BufferedImage region(BufferedImage img, int x, int y, int w, int h) {
        try {
            return Thumbnails.of(img).scale(1).sourceRegion(x, y, w, h).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 改变图像缩放比例
     *
     * @param img
     * @param scale
     * @return
     */
    public static BufferedImage scale(BufferedImage img, double scale) {
        try {
            return Thumbnails.of(img).scale(scale).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 旋转图像
     *
     * @param img
     * @param angle
     * @return
     */
    public static BufferedImage rotate(BufferedImage img, double angle) {
        try {
            return Thumbnails.of(img).scale(1).rotate(angle).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 为图片添加光辉效果
     *
     * @param img
     * @return
     */
    public static BufferedImage glowShadow(BufferedImage img, Color color) {
        if (img == null) return null;
        glowShadowFilter.setShadowColor(color.getRGB());
        return glowShadowFilter.filter(img, null);
    }

    /**
     * 为图片添加阴影，指定颜色
     *
     * @param img
     * @param color
     * @return
     */
    public static BufferedImage shadow(BufferedImage img, Color color) {
        if (img == null) return null;
        shadowFilter.setShadowColor(color.getRGB());
        return shadowFilter.filter(img, null);
    }

    /**
     * 为图片添加边框阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage borderShadow(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage outputImg = createTransparentImage(w + 2 * SHADOW_THICKNESS, h + 2 * SHADOW_THICKNESS);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.drawImage(img, SHADOW_THICKNESS, SHADOW_THICKNESS, null);
        g2d.dispose();
        return width(borderShadowFilter.filter(outputImg, null), w);
    }

    /**
     * 获取图像平均亮度
     *
     * @param img
     * @return
     */
    public static double computeAverageLuminance(BufferedImage img) {
        if (img == null) return 0;
        int w = img.getWidth(), h = img.getHeight();
        List<Float> dots = new LinkedList<>();
        double t = 0;
        for (float i = 0; i < 1; i += 0.05f) dots.add(i);
        for (float dw : dots) {
            for (float dh : dots) {
                int rgb = img.getRGB((int) (w * dw), (int) (h * dh));
                t += ColorUtil.luminance(rgb);
            }
        }
        int s = dots.size();
        t /= s * s;
        return t;
    }

    /**
     * 根据指定面板尺寸生成流体图
     *
     * @param img
     * @return
     */
    public static BufferedImage fluid(BufferedImage img, int canvasWidth, int canvasHeight) {
        if (img == null) return null;
        FluidBackground fluidBackground = FluidBackground.from(img);
        double scale = Math.min(1.0, 256.0 / Math.max(canvasWidth, canvasHeight));
        int rw = Math.max(1, (int) Math.round(canvasWidth * scale));
        int rh = Math.max(1, (int) Math.round(canvasHeight * scale));
        double ts = RandomUtil.randomDouble(Double.MAX_VALUE);
        BufferedImage outputImg = fluidBackground.render(rw, rh, ts);
        return outputImg;
    }

    /**
     * 对图像进行高斯模糊处理
     *
     * @param img
     * @return
     */
    public static BufferedImage gaussianBlur(BufferedImage img) {
        if (img == null) return null;
        gaussianFilter.setRadius(Math.max(1, img.getWidth() * BlurConstants.GAUSSIAN_FACTOR[BlurConstants.gsFactorIndex]));
        return gaussianFilter.filter(img, null);
    }

    /**
     * 对图像进行暗化处理
     *
     * @param img
     * @return
     */
    public static BufferedImage darker(BufferedImage img) {
        if (img == null) return null;
        double l = computeAverageLuminance(img);
        float exp = 0.3f, param = BlurConstants.DARKER_FACTOR[BlurConstants.darkerFactorIndex];
        if (l > 0.8) exp += param - 0.05f;
        else if (l > 0.5) exp += param;
        else if (l > 0.4) exp += param + 0.1f;
        else if (l > 0.3) exp += param + 0.15f;
        else if (l > 0.2) exp += param + 0.2f;
        else if (l > 0.1) exp += param + 0.25f;
        else if (l > 0.05) exp += param + 0.3f;
        else exp += param + 0.6f;
        // 自适应曝光度
        exposureFilter.setExposure(exp);
        return exposureFilter.filter(img, null);
    }

    /**
     * 计算图片平均饱和度
     *
     * @param img
     * @return
     */
    public static float computeAverageSaturation(BufferedImage img) {
        if (img == null) return 0f;
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pixels = new int[w * h];
        img.getRGB(0, 0, w, h, pixels, 0, w);
        float totalS = 0;
        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF, g = (pixel >> 8) & 0xFF, b = pixel & 0xFF;
            // 转 HSV
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            totalS += hsv[1];  // Saturation
        }
        return totalS / (w * h);  // 范围 0~1
    }

    /**
     * 根据平滑的函数计算 SaturationFilter 的 amount 值
     *
     * @param avgSaturation
     * @return
     */
    private static float computeDynamicAmountSmooth(float avgSaturation) {
        float minAmount = 1.0f;
        float maxAmount = 2.5f;
        // 归一化“灰度程度”：avgSaturation=0 时 ratio=1；=1 时 ratio=0
        float ratio = 1.0f - avgSaturation;
        // 使用 0.7 次幂：让低饱和度区域获得极大的增幅补偿
        float factor = (float) Math.pow(ratio, 0.7);
        // 计算最终 amount
        float amount = minAmount + (maxAmount - minAmount) * factor;
        // 严格限幅
        amount = MathUtil.clamp(amount, minAmount, maxAmount);
        return amount;
    }

    /**
     * 提升图像色彩活力度
     *
     * @param img
     * @return
     */
    public static BufferedImage vibrant(BufferedImage img) {
        if (img == null) return null;
        float avgSat = computeAverageSaturation(img);
        float amount = computeDynamicAmountSmooth(avgSat);
        saturationFilter.setAmount(amount);
        return saturationFilter.filter(img, null);
    }

    /**
     * 获取图片均值颜色
     *
     * @param img
     * @return
     */
    public static Color getAvgColor(BufferedImage img) {
        return getAvgColor(img, false);
    }

    /**
     * 获取图片均值颜色，调整为最佳颜色
     *
     * @param img
     * @return
     */
    public static Color getBestAvgColor(BufferedImage img) {
        return getAvgColor(img, true);
    }

    /**
     * 获取图片均值颜色，带透明度
     *
     * @param img
     * @return
     */
    public static Color getAvgColor(BufferedImage img, boolean best) {
        int w = img.getWidth(), h = img.getHeight();
        List<Float> dots = new LinkedList<>();
        for (float i = 0; i < 1; i += 0.05f) dots.add(i);
        int R = 0, G = 0, B = 0, s = dots.size();
        for (float dw : dots) {
            for (float dh : dots) {
                int rgbVal = img.getRGB((int) (w * dw), (int) (h * dh));
                Color color = new Color(rgbVal);
                R += color.getRed();
                G += color.getGreen();
                B += color.getBlue();
            }
        }
        int cn = s * s;
        return best ? ColorUtil.makeBestColor(ColorUtil.merge(R / cn, G / cn, B / cn))
                : new Color(R / cn, G / cn, B / cn);
    }

    /**
     * 提取图像主色调，并生成指定宽高的分形布朗运动图像
     *
     * @return
     */
    public static BufferedImage fbmImage(BufferedImage img, int w, int h) {
        List<Color> palette = ColorUtil.extractPalette(img);
        int ca = palette.get(0).getRGB();
        int cb = (palette.size() > 1 ? palette.get(1) : Colors.THEME).getRGB();
        fbmFilter.setAngle((float) Math.toRadians(RandomUtil.randomInt(360)));
        fbmFilter.setColormap(new LinearColormap(cb, ca));
        return fbmFilter.filter(createTransparentImage(w, h), null);
    }

    /**
     * 图像提取主色调，并生成指定宽高的线性渐变图像
     *
     * @return
     */
    public static BufferedImage gradientImage(BufferedImage img, int w, int h) {
        List<Color> pair = ColorUtil.extractPairVisualAppeal(img);
        Color ca = pair.get(0), cb = pair.get(1);
        return linearGradient(w, h, ca, cb);
    }

    /**
     * 生成两种颜色的渐变图像
     *
     * @return
     */
    public static BufferedImage linearGradient(int w, int h, Color c1, Color c2) {
        BufferedImage img = createTransparentImage(w, h);
        GradientFilter gf = new GradientFilter(new Point(0, 0), new Point(w, h), c1.getRGB(), c2.getRGB(), false, GradientFilter.LINEAR, GradientFilter.INT_LINEAR);
//        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
//        GradientPaint gp = new GradientPaint(0, 0, c1, w, 0, c2);
//        LinearGradientPaint lgp = new LinearGradientPaint(0, 0, w, h, new float[]{0, 0.5f, 1}, new Color[]{c1, c2, c3});
//        g2d.setPaint(lgp);
//        g2d.fillRect(0, 0, w, h);
//        g2d.dispose();
        return gf.filter(img, null);
    }
}
