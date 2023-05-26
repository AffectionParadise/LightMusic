package net.doge.utils;

import cn.hutool.http.HttpRequest;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.GradientFilter;
import com.jhlabs.image.ShadowFilter;
import com.luciad.imageio.webp.WebPReadParam;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurConstants;
import net.doge.constants.Colors;
import net.doge.constants.Format;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/9
 */
public class ImageUtil {
    // 毛玻璃(高斯模糊)过滤器
    private static final GaussianFilter gaussianFilter = new GaussianFilter();
    // 对比度过滤器
    private static final ContrastFilter contrastFilter = new ContrastFilter();
    // 阴影过滤器
    private static final ShadowFilter shadowFilter = new ShadowFilter();
    // 边框阴影过滤器
    private static final ShadowFilter borderShadowFilter = new ShadowFilter();
    private static final int thickness = 30;

    static {
        shadowFilter.setRadius(10);
        shadowFilter.setDistance(0);
        shadowFilter.setOpacity(0.65f);

        borderShadowFilter.setRadius(thickness);
        borderShadowFilter.setDistance(0);
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
        } catch (IOException e) {
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
        try {
            // Obtain a WebP ImageReader instance
            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();

            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);

            // Configure the input on the ImageReader
            reader.setInput(
                    // 读取网络流用 MemoryCacheImageInputStream
                    new MemoryCacheImageInputStream(getImgStream(imgUrl))
            );

            // Decode the image
            return reader.read(0, readParam);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从 URL 读取图片
     *
     * @param url 图片 url
     * @return
     */
    public static BufferedImage read(URL url) {
        try {
            return Thumbnails.of(getImgStream(url.toString())).scale(1).asBufferedImage();
        } catch (IOException e) {
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
        } catch (IOException e) {
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
        try {
            return Thumbnails.of(in).scale(1).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 根据图片 url 获取图片流
     *
     * @param imgUrl 图像 url
     * @return
     */
    public static InputStream getImgStream(String imgUrl) {
        return HttpRequest.get(imgUrl)
                .setFollowRedirects(true)
                .setReadTimeout(20000)
                .execute()
                .bodyStream();
    }

    /**
     * 导出为图片文件
     *
     * @param imgUrl 图像 url
     * @param dest   导出文件路径
     * @return
     */
    public static void toFile(String imgUrl, String dest) {
        try {
            Thumbnails.of(getImgStream(imgUrl)).scale(1).toFile(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出为图片文件
     *
     * @param imgUrl     图像 url
     * @param outputFile 导出文件
     * @return
     */
    public static void toFile(String imgUrl, File outputFile) {
        try {
            Thumbnails.of(getImgStream(imgUrl)).scale(1).toFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
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
        if (img == null) return;
        try {
            Thumbnails.of(img).scale(1).toFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建透明图片
     */
    public static BufferedImage createTranslucentImage(int w, int h) {
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        // 获取透明的 BufferedImage
        BufferedImage translucentImg = g.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        g.dispose();
        return translucentImg;
    }

    /**
     * Image 转为 BuffedImage
     *
     * @param image
     * @return
     */
    public static BufferedImage imageToBufferedImage(Image image) {
        if (image instanceof BufferedImage) return (BufferedImage) image;
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = false;
        BufferedImage bufferedImage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) transparency = Transparency.BITMASK;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bufferedImage = gc.createCompatibleImage(image.getWidth(null), image
                    .getHeight(null), transparency);
        } catch (HeadlessException e) {
        }
        if (bufferedImage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) type = BufferedImage.TYPE_INT_ARGB;
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bufferedImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 消去图片透明度，换成黑底
     *
     * @param img
     * @return
     */
    public static BufferedImage eraseTranslucency(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return bufferedImage;
    }

    /**
     * 给 ImageIcon 着色，保留透明部分
     *
     * @param icon
     * @param color
     * @return
     */
    public static ImageIcon dye(ImageIcon icon, Color color) {
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
        BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
//        final float diff = 15;
//        final float[] fractions = {0.333f, 0.667f, 1};
//        final Color[] colors = {ColorUtil.hsvDiffPick(color, -diff), color, ColorUtil.hsvDiffPick(color, diff)};
//        LinearGradientPaint lgp = new LinearGradientPaint(0, 0, w, h, fractions, colors);
//        g.setPaint(lgp);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return dyed;
    }

//    /**
//     * 生成关于某个颜色的调色板 ImageIcon
//     *
//     * @param h
//     * @return
//     */
//    public static ImageIcon palette(float h, int width) {
//        final int height = 100;
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2d = img.createGraphics();
//        for (int i = 0; i < height; i++) {
//            GradientPaint paint = new GradientPaint(0, i, ColorUtils.hsvToColor(h, 0, height - i),
//                    width - 1, i, ColorUtils.hsvToColor(h, 100, height - i));
//            g2d.setPaint(paint);
//            g2d.drawLine(0, i, width - 1, i);
//        }
//        g2d.dispose();
//        return new ImageIcon(img);
//    }

    /**
     * 返回纯色指定宽高的矩形 BuffedImage
     *
     * @param width
     * @param height
     * @param color
     * @return
     */
    public static BufferedImage dyeRect(int width, int height, Color color) {
        if (color == null) return null;
        BufferedImage translucentImage = createTranslucentImage(width, height);
        Graphics2D g = translucentImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return translucentImage;
    }

    /**
     * 返回纯色指定宽高的圆角矩形 ImageIcon
     *
     * @param width
     * @param height
     * @param color
     * @return
     */
    public static ImageIcon dyeRoundRect(int width, int height, Color color) {
        BufferedImage translucentImage = createTranslucentImage(width, height);
        Graphics2D g = translucentImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillRoundRect(0, 0, width, height, 10, 10);
        g.dispose();
        return new ImageIcon(translucentImage);
    }

    /**
     * 返回纯色指定宽度的圆形 ImageIcon
     *
     * @param width
     * @param color
     * @return
     */
    public static ImageIcon dyeCircle(int width, Color color) {
        BufferedImage translucentImage = createTranslucentImage(width, width);
        Graphics2D g = translucentImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(0, 0, width, width);
        g.dispose();
        return new ImageIcon(translucentImage);
    }

    /**
     * 对 BufferedImage 进行毛玻璃化(高斯模糊)处理，用于专辑背景
     *
     * @param img
     * @return
     */
    public static BufferedImage doBlur(BufferedImage img) {
        if (img == null) return null;
        gaussianFilter.setRadius(Math.max(1, img.getWidth() / BlurConstants.gaussianFactor[BlurConstants.gsFactorIndex]));
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
        double ln = getLightness(img);
        float bn, param = BlurConstants.darkerFactor[BlurConstants.darkerFactorIndex];
        if (ln > 0.6f) bn = param;
        else if (ln > 0.3f) bn = param + 0.1f;
        else if (ln > 0.25f) bn = param + 0.18f;
        else if (ln > 0.2f) bn = param + 0.26f;
        else if (ln > 0.1f) bn = param + 0.35f;
        else bn = param + 0.95f;
        // 自适应亮度
        contrastFilter.setBrightness(bn);
//        System.out.println(lightness + " " + bn);
        return contrastFilter.filter(img, null);
    }

    /**
     * 获取 BufferedImage 亮度
     *
     * @param img
     * @return
     */
    public static double getLightness(BufferedImage img) {
        if (img == null) return 0;
        double t = 0;
        int w = img.getWidth(), h = img.getHeight();
        List<Float> dots = new LinkedList<>();
        for (float i = 0.05f; i < 1; i += 0.05f) dots.add(i);
        for (float dw : dots) {
            for (float dh : dots) {
                int rgb = img.getRGB((int) (w * dw), (int) (h * dh));
                t += ColorUtil.lightness(rgb);
            }
        }
        int s = dots.size();
        t /= s * s;
        return t;
    }

    /**
     * BufferedImage 设置为圆角边框，保留透明度
     *
     * @param image
     * @param arc
     * @return
     */
    public static BufferedImage setRadius(BufferedImage image, double arc) {
        if (image == null) return null;
        return setRadius(image, (int) (image.getWidth() * arc));
    }

    /**
     * BufferedImage 设置为圆角边框，保留透明度
     *
     * @param image
     * @param radius
     * @return
     */
    public static BufferedImage setRadius(BufferedImage image, int radius) {
        if (image == null) return null;
        int width = image.getWidth(), height = image.getHeight();
        BufferedImage outputImage = createTranslucentImage(width, height);
        Graphics2D g = outputImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, width, height, radius, radius);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return outputImage;
    }

    /**
     * 改变图像质量
     *
     * @param img
     * @param q
     * @return
     */
    public static BufferedImage quality(BufferedImage img, float q) {
        if (img == null) return null;
        try {
            return Thumbnails.of(img).scale(1f).outputQuality(q).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param img
     * @param width
     * @return
     * @throws IOException
     */
    public static BufferedImage width(BufferedImage img, int width) {
        if (img == null) return null;
        try {
            return Thumbnails.of(img).width(width).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param imgUrl
     * @param width
     * @return
     * @throws IOException
     */
    public static BufferedImage width(String imgUrl, int width) {
        try {
            BufferedImage img = null;
            // 先处理 webp 图像
            if (imgUrl.endsWith(Format.WEBP)) img = readWebp(imgUrl);
            if (img == null) return Thumbnails.of(getImgStream(imgUrl)).width(width).asBufferedImage();
            return Thumbnails.of(img).width(width).asBufferedImage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 等比例设置图片高度，返回新的 BufferedImage
     *
     * @param img
     * @param height
     * @return
     * @throws IOException
     */
    public static BufferedImage height(BufferedImage img, int height) {
        try {
            return Thumbnails.of(img).height(height).asBufferedImage();
        } catch (IOException e) {
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
     * @throws IOException
     */
    public static BufferedImage forceSize(BufferedImage img, int width, int height) {
        try {
            return Thumbnails.of(img).forceSize(width, height).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将宽高不相等的图片剪成正方形，保留中间部分
     *
     * @param img
     * @return
     * @throws IOException
     */
    public static BufferedImage cropCenter(BufferedImage img) {
        if (img == null) return null;
        int w = img.getWidth(), h = img.getHeight();
        try {
            if (w < h)
                return Thumbnails.of(img).scale(1f).sourceRegion(0, (h - w) / 2, w, w).asBufferedImage();
            else if (w > h)
                return Thumbnails.of(img).scale(1f).sourceRegion((w - h) / 2, 0, h, h).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
        return img;
    }

    /**
     * 获取图片均值颜色
     *
     * @param img
     * @return
     */
    public static Color getAvgRGB(BufferedImage img) {
        return getAvgRGB(img, 1f);
    }

    /**
     * 获取图片均值颜色，带透明度
     *
     * @param img
     * @return
     */
    public static Color getAvgRGB(BufferedImage img, float alpha) {
        int w = img.getWidth(), h = img.getHeight();
        List<Float> dots = new LinkedList<>();
        for (float i = 0.05f; i < 1; i += 0.05f) dots.add(i);
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
        return new Color(R / cn, G / cn, B / cn, (int) (255 * alpha));
    }

    /**
     * 图像转为线性渐变
     *
     * @return
     */
    public static BufferedImage toGradient(BufferedImage img) {
        List<Color> colors = ColorThiefUtil.getPalette(img, 5);
        if (colors.isEmpty()) colors.add(Colors.LIGHT_GRAY);
        Color ca = colors.get(0), cb = colors.get(colors.size() > 1 ? 1 : 0);
        return linearGradient(img.getWidth(), img.getHeight(), ca, cb);
    }

    /**
     * 生成两种颜色的渐变图像
     *
     * @return
     */
    public static BufferedImage linearGradient(int w, int h, Color c1, Color c2) {
        BufferedImage img = createTranslucentImage(w, h);
        GradientFilter gf = new GradientFilter(new Point(0, 0), new Point(w, h), c1.getRGB(), c2.getRGB(), false, GradientFilter.LINEAR, GradientFilter.INT_LINEAR);
//        Graphics2D g = img.createGraphics();
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        GradientPaint gp = new GradientPaint(0, 0, c1, w, 0, c2);
//        g.setPaint(gp);
//        g.fillRect(0, 0, w, h);
//        g.dispose();
        return gf.filter(img, null);
    }

    /**
     * 图片添加阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage shadow(BufferedImage img) {
        return shadowFilter.filter(img, null);
    }

    /**
     * 图片添加边框阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage borderShadow(BufferedImage img) {
        int ow = img.getWidth(), oh = img.getHeight();
        BufferedImage newImg = createTranslucentImage(ow + 2 * thickness, oh + 2 * thickness);
        Graphics2D g = newImg.createGraphics();
        g.drawImage(img, thickness, thickness, null);
        g.dispose();
        newImg = borderShadowFilter.filter(newImg, null);
        newImg = width(newImg, ow);
        return newImg;
    }

    /**
     * 改变图片比例
     *
     * @param img
     * @param scale
     * @return
     */
    public static BufferedImage scale(BufferedImage img, float scale) {
        if (img == null) return null;
        try {
            return Thumbnails.of(img).scale(scale).asBufferedImage();
        } catch (IOException e) {
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
        if (img == null) return null;
        try {
            return Thumbnails.of(img).scale(1f).rotate(angle).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }
}
