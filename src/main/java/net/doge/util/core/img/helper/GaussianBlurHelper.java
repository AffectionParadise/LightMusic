package net.doge.util.core.img.helper;

import net.doge.util.ui.ColorUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * 用于 {@link BufferedImage} 的快速类高斯模糊。
 *
 * <p>该实现使用三个盒式滤波器。三个盒式滤波可以非常近似地拟合高斯核，
 * 而每个盒式滤波通过一维前缀和来计算，因此整个操作的复杂度为
 * O(宽度 × 高度)，与半径无关。这比直接进行二维高斯卷积要快得多，
 * 后者的代价为 O(宽度 × 高度 × 半径 × 半径)。</p>
 *
 * <p>像素在滤波前会转换为预乘 ARGB。这可以避免透明像素周围的 RGB 值
 * 产生暗色边缘。输入图像绝不会被修改。</p>
 */
public class GaussianBlurHelper {
    /**
     * 模糊图像并返回一个新图像。
     *
     * @param source 源图像；它绝不会被更改
     * @param radius 模糊半径，单位为像素；为零时返回一个副本
     * @return 一个模糊后的图像，具有相同的尺寸和 Alpha 能力
     * @throws NullPointerException     如果 {@code source} 为 null
     * @throws IllegalArgumentException 如果 {@code radius} 为负数
     */
    public static BufferedImage blur(BufferedImage source, int radius) {
        Objects.requireNonNull(source, "source");
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be >= 0");
        }
        int width = source.getWidth();
        int height = source.getHeight();
        boolean hasAlpha = source.getColorModel().hasAlpha();
        int[] sourcePixels = source.getRGB(0, 0, width, height, null, 0, width);

        //零半径调用仍然不会修改原图，并返回一个脱离原图的独立图像
        if (radius == 0 || width == 1 && height == 1) {
            return copy(sourcePixels, width, height, hasAlpha);
        }

        int[] current = toPremultiplied(sourcePixels);
        int[] scratch = new int[current.length];
        for (int boxRadius : boxRadii(radius)) {
            if (boxRadius == 0) {
                continue;
            }
            boxHorizontal(current, scratch, width, height, boxRadius);
            boxVertical(scratch, current, width, height, boxRadius);
        }

        int[] resultPixels = new int[current.length];
        for (int i = 0; i < current.length; i++) {
            resultPixels[i] = unpremultiply(current[i], hasAlpha);
        }
        return copy(resultPixels, width, height, hasAlpha);
    }

    /**
     * 将请求的半径拆分为三个几乎相等的盒式半径。三个盒式半径之和
     * 等于请求的半径，从而在使三次滤波接近高斯曲线的同时，
     * 保持视觉扩散范围直观
     */
    private static int[] boxRadii(int radius) {
        int base = radius / 3;
        int remainder = radius % 3;
        return new int[]{
                base + (remainder > 0 ? 1 : 0),
                base + (remainder > 1 ? 1 : 0),
                base
        };
    }

    /**
     * 采用复制边缘的水平盒式滤波，通过行前缀和进行计算
     */
    private static void boxHorizontal(int[] source, int[] target,
                                      int width, int height, int radius) {
        long[] alphaPrefix = new long[width + 1];
        long[] redPrefix = new long[width + 1];
        long[] greenPrefix = new long[width + 1];
        long[] bluePrefix = new long[width + 1];
        for (int y = 0; y < height; y++) {
            int row = y * width;
            alphaPrefix[0] = redPrefix[0] = greenPrefix[0] = bluePrefix[0] = 0;
            for (int x = 0; x < width; x++) {
                int argb = source[row + x];
                alphaPrefix[x + 1] = alphaPrefix[x] + ColorUtil.alpha(argb);
                redPrefix[x + 1] = redPrefix[x] + ColorUtil.red(argb);
                greenPrefix[x + 1] = greenPrefix[x] + ColorUtil.green(argb);
                bluePrefix[x + 1] = bluePrefix[x] + ColorUtil.blue(argb);
            }
            for (int x = 0; x < width; x++) {
                long from = (long) x - radius;
                long to = (long) x + radius;
                int left = from < 0 ? 0 : (int) from;
                int right = to >= width ? width - 1 : (int) to;
                long leftExtra = from < 0 ? -from : 0;
                long rightExtra = to >= width ? to - width + 1 : 0;
                long count = to - from + 1;
                int first = source[row];
                int last = source[row + width - 1];
                long alpha = sum(alphaPrefix, left, right)
                        + leftExtra * ColorUtil.alpha(first)
                        + rightExtra * ColorUtil.alpha(last);
                long red = sum(redPrefix, left, right)
                        + leftExtra * ColorUtil.red(first)
                        + rightExtra * ColorUtil.red(last);
                long green = sum(greenPrefix, left, right)
                        + leftExtra * ColorUtil.green(first)
                        + rightExtra * ColorUtil.green(last);
                long blue = sum(bluePrefix, left, right)
                        + leftExtra * ColorUtil.blue(first)
                        + rightExtra * ColorUtil.blue(last);
                target[row + x] = ColorUtil.merge(average(alpha, count), average(red, count),
                        average(green, count), average(blue, count));
            }
        }
    }

    /**
     * 采用复制边缘的垂直盒式滤波，通过列前缀和进行计算
     */
    private static void boxVertical(int[] source, int[] target,
                                    int width, int height, int radius) {
        long[] alphaPrefix = new long[height + 1];
        long[] redPrefix = new long[height + 1];
        long[] greenPrefix = new long[height + 1];
        long[] bluePrefix = new long[height + 1];
        for (int x = 0; x < width; x++) {
            alphaPrefix[0] = redPrefix[0] = greenPrefix[0] = bluePrefix[0] = 0;
            for (int y = 0; y < height; y++) {
                int argb = source[y * width + x];
                alphaPrefix[y + 1] = alphaPrefix[y] + ColorUtil.alpha(argb);
                redPrefix[y + 1] = redPrefix[y] + ColorUtil.red(argb);
                greenPrefix[y + 1] = greenPrefix[y] + ColorUtil.green(argb);
                bluePrefix[y + 1] = bluePrefix[y] + ColorUtil.blue(argb);
            }
            for (int y = 0; y < height; y++) {
                long from = (long) y - radius;
                long to = (long) y + radius;
                int top = from < 0 ? 0 : (int) from;
                int bottom = to >= height ? height - 1 : (int) to;
                long topExtra = from < 0 ? -from : 0;
                long bottomExtra = to >= height ? to - height + 1 : 0;
                long count = to - from + 1;
                int first = source[x];
                int last = source[(height - 1) * width + x];
                long alpha = sum(alphaPrefix, top, bottom)
                        + topExtra * ColorUtil.alpha(first)
                        + bottomExtra * ColorUtil.alpha(last);
                long red = sum(redPrefix, top, bottom)
                        + topExtra * ColorUtil.red(first)
                        + bottomExtra * ColorUtil.red(last);
                long green = sum(greenPrefix, top, bottom)
                        + topExtra * ColorUtil.green(first)
                        + bottomExtra * ColorUtil.green(last);
                long blue = sum(bluePrefix, top, bottom)
                        + topExtra * ColorUtil.blue(first)
                        + bottomExtra * ColorUtil.blue(last);
                target[y * width + x] = ColorUtil.merge(average(alpha, count), average(red, count),
                        average(green, count), average(blue, count));
            }
        }
    }

    private static long sum(long[] prefix, int left, int right) {
        return prefix[right + 1] - prefix[left];
    }

    private static int average(long value, long count) {
        return (int) ((value + count / 2) / count);
    }

    private static int[] toPremultiplied(int[] sourcePixels) {
        int[] pixels = new int[sourcePixels.length];
        for (int i = 0; i < sourcePixels.length; i++) {
            int argb = sourcePixels[i];
            int alpha = ColorUtil.alpha(argb);
            int red = ColorUtil.red(argb);
            int green = ColorUtil.green(argb);
            int blue = ColorUtil.blue(argb);
            red = (red * alpha + 127) / 255;
            green = (green * alpha + 127) / 255;
            blue = (blue * alpha + 127) / 255;
            pixels[i] = ColorUtil.merge(alpha, red, green, blue);
        }
        return pixels;
    }

    private static int unpremultiply(int premultiplied, boolean hasAlpha) {
        int alpha = ColorUtil.alpha(premultiplied);
        if (!hasAlpha || alpha == 255) return 0xff000000 | (premultiplied & 0x00ffffff);
        if (alpha == 0) return 0;
        int red = Math.min(255, (ColorUtil.red(premultiplied) * 255 + alpha / 2) / alpha);
        int green = Math.min(255, (ColorUtil.green(premultiplied) * 255 + alpha / 2) / alpha);
        int blue = Math.min(255, (ColorUtil.blue(premultiplied) * 255 + alpha / 2) / alpha);
        return ColorUtil.merge(alpha, red, green, blue);
    }

    private static BufferedImage copy(int[] pixels, int width, int height, boolean hasAlpha) {
        BufferedImage result = new BufferedImage(width, height,
                hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        result.setRGB(0, 0, width, height, pixels, 0, width);
        return result;
    }
}
