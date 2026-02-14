package net.doge.util.ui;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
import com.jhlabs.image.*;
import com.luciad.imageio.webp.WebPReadParam;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constant.core.os.Format;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.image.BlurConstants;
import net.doge.util.core.LogUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.http.HttpRequest;
import net.doge.util.ui.quantizer.MMCQ;

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
 * @Author Doge
 * @Description
 * @Date 2020/12/9
 */
public class ImageUtil {
    // 高斯模糊过滤器
    private static final GaussianFilter gaussianFilter = new GaussianFilter();
    // 旋转过滤器
    private static final TwirlFilter twirlFilter = new TwirlFilter();
    // 分析布朗运动过滤器
    private static final FBMFilter fbmFilter = new FBMFilter();
    // 对比度过滤器
    private static final ContrastFilter contrastFilter = new ContrastFilter();
    // 阴影过滤器
    private static final ShadowFilter shadowFilter = new ShadowFilter();
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
        try (InputStream input = in) {
            return Thumbnails.of(input).scale(1).asBufferedImage();
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
        try (InputStream in = getImgStream(imgUrl)) {
            // Obtain a WebP ImageReader instance
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();

            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);

            // Configure the input on the ImageReader
            reader.setInput(
                    // 读取网络流用 MemoryCacheImageInputStream
                    new MemoryCacheImageInputStream(in)
            );

            // Decode the image
            return reader.read(0, readParam);
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 从 url 读取图片
     *
     * @param imgUrl 图片 url
     * @return
     */
    public static BufferedImage readByUrl(String imgUrl) {
        if (StringUtil.isEmpty(imgUrl)) return null;
        if (imgUrl.endsWith(Format.WEBP)) return readWebp(imgUrl);
        return read(getImgStream(imgUrl));
    }

    /**
     * 根据图片 url 获取图片流
     *
     * @param imgUrl 图像 url
     * @return
     */
    public static InputStream getImgStream(String imgUrl) {
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
        try (InputStream in = getImgStream(imgUrl)) {
            Thumbnails.of(in).scale(1).toFile(outputFile);
        } catch (Exception e) {
            LogUtil.error(e);
        }
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
        for (int index = 0, i = x; i < w; i++)
            for (int j = y; j < h; j++)
                pixels[index++] = img.getRGB(i, j);
        return pixels;
    }

    /**
     * 创建透明图片
     */
    public static BufferedImage createTransparentImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Image 转为 BufferedImage
     *
     * @param img
     * @return
     */
    public static BufferedImage toBufferedImage(Image img) {
        return ImgUtil.toBufferedImage(img, ImgUtil.IMAGE_TYPE_PNG);
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
     * 给 ImageIcon 着色，保留透明部分
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
     * 给 Image 着色，保留透明部分
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
     * 返回纯色指定宽高的矩形 BufferedImage
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
     * 返回纯色指定宽高的圆角矩形 ImageIcon
     *
     * @param w
     * @param h
     * @param color
     * @return
     */
    public static ImageIcon dyeRoundRect(int w, int h, Color color) {
        BufferedImage img = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        g2d.setColor(color);
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(0, 0, w, h, arc, arc);
        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * 返回纯色指定宽度的圆形 ImageIcon
     *
     * @param w
     * @param color
     * @return
     */
    public static ImageIcon dyeCircle(int w, Color color) {
        BufferedImage img = createTransparentImage(w, w);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        g2d.setColor(color);
        g2d.fillOval(0, 0, w, w);
        g2d.dispose();
        return new ImageIcon(img);
    }

    /**
     * BufferedImage 设置为圆角边框，保留透明度
     *
     * @param img
     * @param arc
     * @return
     */
    public static BufferedImage radius(BufferedImage img, double arc) {
        return radius(img, (int) (img.getWidth() * arc));
    }

    /**
     * BufferedImage 设置为圆角边框，保留透明度
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
            return Thumbnails.of(img).scale(1f).outputQuality(q).asBufferedImage();
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
        try {
            return Thumbnails.of(img).width(width).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param imgUrl
     * @param width
     * @return
     */
    public static BufferedImage width(String imgUrl, int width) {
        try {
            BufferedImage img = null;
            // 先处理 webp 图像
            if (imgUrl.endsWith(Format.WEBP)) img = readWebp(imgUrl);
            if (img == null) return Thumbnails.of(getImgStream(imgUrl)).width(width).asBufferedImage();
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
            return Thumbnails.of(img).scale(1f).sourceRegion(x, y, w, h).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 改变图片比例
     *
     * @param img
     * @param scale
     * @return
     */
    public static BufferedImage scale(BufferedImage img, float scale) {
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
            return Thumbnails.of(img).scale(1f).rotate(angle).asBufferedImage();
        } catch (Exception e) {
            LogUtil.error(e);
            return null;
        }
    }

    /**
     * 图片添加阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage shadow(BufferedImage img) {
        return shadow(img, Colors.SHADOW);
    }

    /**
     * 图片添加阴影，带颜色
     *
     * @param img
     * @param color
     * @return
     */
    public static BufferedImage shadow(BufferedImage img, Color color) {
        shadowFilter.setShadowColor(color.getRGB());
        return shadowFilter.filter(img, null);
    }

    /**
     * 图片添加边框阴影
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
     * 获取 BufferedImage 亮度
     *
     * @param img
     * @return
     */
    public static double lightness(BufferedImage img) {
        if (img == null) return 0;
        int w = img.getWidth(), h = img.getHeight();
        List<Float> dots = new LinkedList<>();
        double t = 0;
        for (float i = 0; i < 1; i += 0.05f) dots.add(i);
        for (float dw : dots) {
            for (float dh : dots) {
                int rgb = img.getRGB((int) (w * dw), (int) (h * dh));
                t += ColorUtil.calculateLuminance(rgb);
            }
        }
        int s = dots.size();
        t /= s * s;
        return t;
    }

    /**
     * 生成流体图
     *
     * @param img
     * @return
     */
    public static BufferedImage fluidImage(BufferedImage img) {
        if (img == null) return null;
        twirlFilter.setAngle((float) Math.toRadians(RandomUtil.randomInt(10, 350)));
        twirlFilter.setRadius((float) img.getWidth() / 2);
        return twirlFilter.filter(img, null);
    }


    /**
     * 对 BufferedImage 进行高斯模糊处理，用于歌曲封面
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
     * 对 BufferedImage 进行暗化处理
     *
     * @param img
     * @return
     */
    public static BufferedImage darker(BufferedImage img) {
        if (img == null) return null;
        double ln = lightness(img);
        float bn, param = BlurConstants.DARKER_FACTOR[BlurConstants.darkerFactorIndex];
//        System.out.println(ln);
        if (ln > 0.8) bn = param - 0.05f;
        else if (ln > 0.5) bn = param;
        else if (ln > 0.4) bn = param + 0.1f;
        else if (ln > 0.3) bn = param + 0.15f;
        else if (ln > 0.2) bn = param + 0.2f;
        else if (ln > 0.1) bn = param + 0.25f;
        else if (ln > 0.05) bn = param + 0.3f;
        else bn = param + 0.6f;
        // 自适应亮度
        contrastFilter.setBrightness(bn);
        return contrastFilter.filter(img, null);
    }

    /**
     * 为 BufferedImage 添加遮罩
     *
     * @param img
     * @return
     */
    public static BufferedImage mask(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage outputImg = createTransparentImage(w, h);
        Graphics2D g2d = GraphicsUtil.setup(outputImg.createGraphics());
        g2d.drawImage(img, 0, 0, null);
        g2d.setColor(getBestAvgColor(img));
        GraphicsUtil.srcOver(g2d, 0.95f);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        return outputImg;
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
        List<MMCQ.ThemeColor> themeColors = ColorUtil.getThemeColors(img);
        int ca = ColorUtil.makeBestColor(themeColors.get(0).getRgb()).getRGB();
        int cb = ColorUtil.makeBestColor(themeColors.size() > 1 ? themeColors.get(1).getRgb() : Colors.THEME.getRGB()).getRGB();
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
        Color mc = ColorUtil.getBestColor(img);
        double l = ColorUtil.calculateLuminance(mc.getRGB());
        if (l >= 0.25) {
            Color ca = ColorUtil.rotate(mc, -10), cb = ColorUtil.rotate(ColorUtil.hslDarken(mc, 0.3f), 10);
            return linearGradient(w, h, ca, cb);
        } else {
            Color ca = ColorUtil.rotate(mc, 10), cb = ColorUtil.rotate(ColorUtil.hslLighten(mc, 0.1f), -10);
            return linearGradient(w, h, cb, ca);
        }
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
