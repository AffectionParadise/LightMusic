package net.doge.util.core.img.helper;

import net.doge.util.core.math.MathUtil;
import net.doge.util.ui.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.Objects;

/**
 * 一个不可变、线程安全的艺术作品调色板与流体背景渲染器
 * 仅使用 Java 8 / Java2D，源图像仅被采样，绝不会被修改或保留
 */
public class FluidHelper {
    private static final int MAX_FIELD_SIZE = 256;
    private static final int COLOR_COUNT = 6;
    private static final int[] SRGB = new int[4097];

    static {
        for (int i = 0; i < SRGB.length; i++) {
            double v = i / 4096.0;
            SRGB[i] = (int) Math.round(255 * (v <= 0.0031308
                    ? 12.92 * v : 1.055 * Math.pow(v, 1 / 2.4) - 0.055));
        }
    }

    private final List<Color> palette;
    private final double[][] linearColors;

    private FluidHelper(List<Color> palette) {
        this.palette = palette;
        linearColors = new double[COLOR_COUNT][3];
        for (int i = 0, s = palette.size(); i < COLOR_COUNT; i++) {
            Color color = palette.get(i % s);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            // 保留单色艺术作品，温和地提亮暗色调和浓郁色彩
            Color lifted = Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 1.08f),
                    Math.max(0.12f, Math.min(0.98f, hsb[2] * 1.08f)));
            linearColors[i][0] = toLinear(lifted.getRed());
            linearColors[i][1] = toLinear(lifted.getGreen());
            linearColors[i][2] = toLinear(lifted.getBlue());
        }
    }

    /**
     * 提取最多 COLOR_COUNT 种代表性颜色，忽略完全透明的像素
     */
    public static FluidHelper from(BufferedImage img) {
        Objects.requireNonNull(img, "img");
        return new FluidHelper(PaletteHelper.extractPalette(img));
    }

    /**
     * 代表性源颜色，按其加权面积排序
     */
    public List<Color> getPalette() {
        return palette;
    }

    /**
     * 在请求的尺寸和动画时间下渲染一个新的不透明背景
     * 相同的输入会产生相同的像素，时间以秒为单位；0 表示静止图像
     */
    public BufferedImage render(int width, int height, double timeSeconds) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("宽高必须为正整数");
        if (Double.isNaN(timeSeconds) || Double.isInfinite(timeSeconds))
            throw new IllegalArgumentException("时间必须在有限范围");
        double scale = Math.min(1.0, MAX_FIELD_SIZE / (double) Math.max(width, height));
        int fw = Math.max(1, (int) Math.round(width * scale));
        int fh = Math.max(1, (int) Math.round(height * scale));
        BufferedImage field = renderField(fw, fh, timeSeconds);
        if (fw == width && fh == height) return field;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(field, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return result;
    }

    private BufferedImage renderField(int width, int height, double t) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        double[] cx = new double[COLOR_COUNT];
        double[] cy = new double[COLOR_COUNT];
        double[] radiusX = new double[COLOR_COUNT];
        double[] radiusY = new double[COLOR_COUNT];
        for (int i = 0; i < COLOR_COUNT; i++) {
            double angle = i * Math.PI * 2 / COLOR_COUNT;
            cx[i] = 0.5 + 0.48 * Math.cos(angle) + 0.18 * Math.sin(t * 0.17 + i * 1.9);
            cy[i] = 0.5 + 0.48 * Math.sin(angle) + 0.18 * Math.cos(t * 0.13 + i * 1.5);
            radiusX[i] = 0.38 + 0.10 * Math.sin(t * 0.11 + i * 1.3);
            radiusY[i] = 0.44 + 0.12 * Math.cos(t * 0.12 + i * 1.7);
        }
        for (int y = 0; y < height; y++) {
            double ny = y / (double) Math.max(1, height - 1);
            for (int x = 0; x < width; x++) {
                double nx = x / (double) Math.max(1, width - 1);
                // 域扭曲将颜色场弯折成缓慢移动的丝带
                double wx = nx + 0.13 * Math.sin(ny * 5.0 + t * 0.19)
                        + 0.07 * Math.sin((nx + ny) * 4.0 - t * 0.13);
                double wy = ny + 0.13 * Math.sin(nx * 4.2 - t * 0.14)
                        + 0.07 * Math.cos((nx - ny) * 4.0 + t * 0.12);
                double red = 0, green = 0, blue = 0, sum = 0;
                for (int i = 0; i < COLOR_COUNT; i++) {
                    double dx = (wx - cx[i]) / radiusX[i];
                    double dy = (wy - cy[i]) / radiusY[i];
                    double weight = Math.exp(-2.2 * (dx * dx + dy * dy)) + 0.00001;
                    sum += weight;
                    red += linearColors[i][0] * weight;
                    green += linearColors[i][1] * weight;
                    blue += linearColors[i][2] * weight;
                }
                double edge = (nx - 0.5) * (nx - 0.5) + (ny - 0.5) * (ny - 0.5);
                double shade = 0.94 - edge * 0.22 - ny * 0.08;
                pixels[y * width + x] = ColorUtil.merge(toSrgb(red / sum * shade),
                        toSrgb(green / sum * shade), toSrgb(blue / sum * shade));
            }
        }
        // 两遍可分离的盒式滤波可柔化狭窄的颜色接缝，时间复杂度为 O(宽度 × 高度)
        blur(pixels, width, height, Math.max(1, Math.min(width, height) / 40));
        return image;
    }

    private static void blur(int[] pixels, int width, int height, int radius) {
        int[] temp = new int[pixels.length];
        for (int pass = 0; pass < 2; pass++) {
            blurHorizontal(pixels, temp, width, height, radius);
            // 在每一遍处理过程中进行转置，然后再转置回来
            blurHorizontal(temp, pixels, height, width, radius);
        }
    }

    private static void blurHorizontal(int[] source, int[] target, int width, int height, int radius) {
        int divisor = radius * 2 + 1;
        for (int y = 0; y < height; y++) {
            int r = 0, g = 0, b = 0;
            for (int offset = -radius; offset <= radius; offset++) {
                int rgb = source[y * width + MathUtil.clamp(offset, 0, width - 1)];
                r += ColorUtil.red(rgb);
                g += ColorUtil.green(rgb);
                b += ColorUtil.blue(rgb);
            }
            for (int x = 0; x < width; x++) {
                target[x * height + y] = ((r / divisor) << 16) | ((g / divisor) << 8) | (b / divisor);
                int left = source[y * width + MathUtil.clamp(x - radius, 0, width - 1)];
                int right = source[y * width + MathUtil.clamp(x + radius + 1, 0, width - 1)];
                r += ColorUtil.red(right) - ColorUtil.red(left);
                g += ColorUtil.green(right) - ColorUtil.green(left);
                b += ColorUtil.blue(right) - ColorUtil.blue(left);
            }
        }
    }

    private static double toLinear(int channel) {
        double v = channel / 255.0;
        return v <= 0.04045 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4);
    }

    private static int toSrgb(double channel) {
        return SRGB[MathUtil.clamp((int) Math.round(channel * 4096), 0, 4096)];
    }
}
