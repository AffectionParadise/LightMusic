package net.doge.utils;

import cn.hutool.http.HttpRequest;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.GaussianFilter;
import com.jhlabs.image.ShadowFilter;
import net.coobird.thumbnailator.Thumbnails;

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
public class ImageUtils {
    // 毛玻璃(高斯模糊)过滤器
    private static final GaussianFilter gaussianFilter = new GaussianFilter();
    // 对比度过滤器
    private static final ContrastFilter contrastFilter = new ContrastFilter();
    // 阴影过滤器
    private static final ShadowFilter shadowFilter = new ShadowFilter();
    // 边框阴影过滤器
    private static final ShadowFilter borderShadowFilter = new ShadowFilter();

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

//    /**
//     * 从 url 读取 Webp 图像
//     *
//     * @param url 图片 url
//     * @return
//     */
//    public static BufferedImage readWebp(String url) {
//        try {
//            // Obtain a WebP ImageReader instance
//            ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
//
//            // Configure decoding parameters
//            WebPReadParam readParam = new WebPReadParam();
//            readParam.setBypassFiltering(true);
//
//            // Configure the input on the ImageReader
//            reader.setInput(
//                    // 读取网络流用 MemoryCacheImageInputStream
//                    new MemoryCacheImageInputStream(
//                            HttpRequest.get(url)
//                            .setFollowRedirects(true)
//                            .setReadTimeout(20000)
//                            .execute()
//                            .bodyStream()
//                    )
//            );
//
//            // Decode the image
//            return reader.read(0, readParam);
//        } catch (IOException e) {
//            return null;
//        }
//    }

    /**
     * 从 URL 读取图片
     *
     * @param url 图片 url
     * @return
     */
    public static BufferedImage read(URL url) {
        try {
            return Thumbnails.of(
                    HttpRequest.get(url.toString())
                            .setFollowRedirects(true)
                            .setReadTimeout(20000)
                            .execute()
                            .bodyStream()
            ).scale(1).asBufferedImage();
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
     * 导出为图片文件
     *
     * @param imageUrl 图像 url
     * @param dest     导出文件路径
     * @return
     */
    public static void toFile(String imageUrl, String dest) {
        try {
            Thumbnails.of(
                    HttpRequest.get(imageUrl)
                            .setFollowRedirects(true)
                            .setReadTimeout(20000)
                            .execute()
                            .bodyStream()
            ).scale(1).toFile(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出为图片文件
     *
     * @param imageUrl   图像 url
     * @param outputFile 导出文件
     * @return
     */
    public static void toFile(String imageUrl, File outputFile) {
        try {
            Thumbnails.of(new URL(imageUrl)).scale(1).toFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出为图片文件
     *
     * @param image 图像
     * @param dest  导出文件路径
     * @return
     */
    public static void toFile(BufferedImage image, String dest) {
        try {
            Thumbnails.of(image).scale(1).toFile(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出为图片文件
     *
     * @param image      图像
     * @param outputFile 导出文件
     * @return
     */
    public static void toFile(BufferedImage image, File outputFile) {
        if (image == null) return;
        try {
            Thumbnails.of(image).scale(1).toFile(outputFile);
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
     * @param image
     * @return
     */
    public static BufferedImage eraseTranslucency(BufferedImage image) {
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, w, h, null);
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
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return new ImageIcon(dyed);
    }

    /**
     * 给 BufferedImage 着色，保留透明部分
     *
     * @param img
     * @return
     */
    public static BufferedImage dye(BufferedImage img, Color color) {
        int w = img.getWidth(), h = img.getHeight();
        BufferedImage dyed = createTranslucentImage(w, h);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
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
     * @param bufferedImage
     * @return
     */
    public static BufferedImage doBlur(BufferedImage bufferedImage) {
        gaussianFilter.setRadius(Math.max(1, bufferedImage.getWidth() / 16));
        return gaussianFilter.filter(bufferedImage, null);
    }

    /**
     * 对 BufferedImage 进行暗化处理
     *
     * @param bufferedImage
     * @return
     */
    public static BufferedImage darker(BufferedImage bufferedImage) {
        contrastFilter.setBrightness(0.65f);
        return contrastFilter.filter(bufferedImage, null);
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
        int width = image.getWidth(), height = image.getHeight(), cornerRadius = (int) (width * arc);
        BufferedImage outputImage = createTranslucentImage(width, height);
        Graphics2D g = outputImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return outputImage;
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
        int width = image.getWidth(), height = image.getHeight(), cornerRadius = radius;
        BufferedImage outputImage = createTranslucentImage(width, height);
        Graphics2D g = outputImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return outputImage;
    }

    /**
     * 改变图像质量
     *
     * @param image
     * @param q
     * @return
     */
    public static BufferedImage quality(BufferedImage image, float q) {
        if (image == null) return null;
        try {
            return Thumbnails.of(image).scale(1f).outputQuality(q).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param image
     * @param width
     * @return
     * @throws IOException
     */
    public static BufferedImage width(BufferedImage image, int width) {
        if (image == null) return null;
        try {
            return Thumbnails.of(image).width(width).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 等比例设置图片宽度，返回新的 BufferedImage
     *
     * @param imageUrl
     * @param width
     * @return
     * @throws IOException
     */
    public static BufferedImage width(String imageUrl, int width) {
        try {
            // 允许重定向请求图片
            return Thumbnails.of(
                    HttpRequest.get(imageUrl)
                            .setFollowRedirects(true)
                            .execute()
                            .bodyStream()
            ).width(width).asBufferedImage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 等比例设置图片高度，返回新的 BufferedImage
     *
     * @param image
     * @param height
     * @return
     * @throws IOException
     */
    public static BufferedImage height(BufferedImage image, int height) {
        try {
            return Thumbnails.of(image).height(height).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 设置图片宽度和高度，返回新的 BufferedImage
     *
     * @param image
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static BufferedImage forceSize(BufferedImage image, int width, int height) {
        try {
            return Thumbnails.of(image).forceSize(width, height).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将宽高不相等的图片剪成正方形，保留中间部分
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static BufferedImage cropCenter(BufferedImage image) {
        if (image == null) return null;
        int w = image.getWidth(), h = image.getHeight();
        try {
            if (w < h)
                return Thumbnails.of(image).scale(1f).sourceRegion(0, (h - w) / 2, w, w).asBufferedImage();
            else if (w > h)
                return Thumbnails.of(image).scale(1f).sourceRegion((w - h) / 2, 0, h, h).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
        return image;
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
        List<Color> colors = ColorThiefUtils.getPalette(img, 3);
        return linearGradient(img.getWidth(), img.getHeight(), colors.get(0), colors.get(colors.size() > 1 ? 1 : 0));
    }

    /**
     * 生成两种颜色的渐变图像
     *
     * @return
     */
    public static BufferedImage linearGradient(int w, int h, Color c1, Color c2) {
        BufferedImage img = createTranslucentImage(w, h);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return img;
    }

    /**
     * 图片添加阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage shadow(BufferedImage img) {
        shadowFilter.setRadius(10);
        shadowFilter.setDistance(-0.3f);
        shadowFilter.setOpacity(0.65f);
        return shadowFilter.filter(img, null);
    }

    /**
     * 图片添加边框阴影
     *
     * @param img
     * @return
     */
    public static BufferedImage borderShadow(BufferedImage img) {
        final int thickness = 30;
        int ow = img.getWidth(), oh = img.getHeight();
        BufferedImage newImg = createTranslucentImage(ow + 2 * thickness, oh + 2 * thickness);
        Graphics2D g = newImg.createGraphics();
        g.drawImage(img, thickness, thickness, null);
        g.dispose();
        borderShadowFilter.setRadius(thickness);
        borderShadowFilter.setDistance(-0.5f);
        borderShadowFilter.setOpacity(0.65f);
        newImg = borderShadowFilter.filter(newImg, null);
        newImg = width(newImg, ow);
        return newImg;
    }

    /**
     * 改变图片比例
     *
     * @param image
     * @param scale
     * @return
     */
    public static BufferedImage scale(BufferedImage image, float scale) {
        try {
            return Thumbnails.of(image).scale(scale).asBufferedImage();
        } catch (IOException e) {
            return null;
        }
    }
}
