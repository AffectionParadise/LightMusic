package net.doge.util.core.img.helper;

import net.doge.util.core.math.MathUtil;
import net.doge.util.ui.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.Objects;

/**
 * 基于分形布朗运动的确定性音乐艺术作品渲染器。
 *
 * <p>封面被缩减为一个小型调色板，随后将多个平滑的值噪声八度组合成
 * 一个 FBM 场。域扭曲将该场转变为大型、柔和的色彩流，而不是颗粒状的
 * 静态噪声。该渲染器是不可变的，且不会保留或修改源图像。</p>
 */
public class FbmHelper {
    private static final int MAX_FIELD_SIZE = 320;
    private static final int OCTAVES = 5;
    private static final int[] SRGB = new int[4097];

    static {
        for (int i = 0; i < SRGB.length; i++) {
            double value = i / 4096.0;
            SRGB[i] = (int) Math.round(255 * (value <= 0.0031308
                    ? 12.92 * value : 1.055 * Math.pow(value, 1 / 2.4) - 0.055));
        }
    }

    private final List<Color> palette;
    private final double[][] linearPalette;
    private final long seed;

    private FbmHelper(List<Color> colors, long seed) {
        this.palette = colors;
        linearPalette = new double[palette.size()][3];
        for (int i = 0; i < palette.size(); i++) {
            Color color = palette.get(i);
            linearPalette[i][0] = toLinear(color.getRed());
            linearPalette[i][1] = toLinear(color.getGreen());
            linearPalette[i][2] = toLinear(color.getBlue());
        }
        this.seed = seed;
    }

    // 创建一个确定性渲染器，其种子从艺术作品中派生
    public static FbmHelper from(BufferedImage artwork) {
        Objects.requireNonNull(artwork, "artwork");
        List<Color> colors = PaletteHelper.extractPalette(artwork);
        return new FbmHelper(colors, paletteSeed(colors));
    }

    // 创建一个动画/噪声种子由调用方控制的渲染器
    public static FbmHelper from(BufferedImage artwork, long seed) {
        Objects.requireNonNull(artwork, "artwork");
        return new FbmHelper(PaletteHelper.extractPalette(artwork), seed);
    }

    public List<Color> getPalette() {
        return palette;
    }

    public long getSeed() {
        return seed;
    }

    /**
     * 渲染平滑的 FBM 背景，时间以秒为单位；相同的输入会产生相同的像素
     * 计算开销较大的场在最长边最多以 320px 的尺寸求值，然后通过双三次插值放大
     */
    public BufferedImage render(int width, int height, double timeSeconds) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        if (Double.isNaN(timeSeconds) || Double.isInfinite(timeSeconds)) {
            throw new IllegalArgumentException("timeSeconds must be finite");
        }
        double scale = Math.min(1.0, MAX_FIELD_SIZE / (double) Math.max(width, height));
        int fieldWidth = Math.max(1, (int) Math.round(width * scale));
        int fieldHeight = Math.max(1, (int) Math.round(height * scale));
        BufferedImage field = renderField(fieldWidth, fieldHeight, timeSeconds);
        if (fieldWidth == width && fieldHeight == height) return field;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(field, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return result;
    }

    private BufferedImage renderField(int width, int height, double timeSeconds) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int y = 0; y < height; y++) {
            double ny = y / (double) Math.max(1, height - 1);
            for (int x = 0; x < width; x++) {
                double nx = x / (double) Math.max(1, width - 1);
                double warpX = fbm(nx * 1.45 + 4.7, ny * 1.45 - 2.3,
                        timeSeconds * 0.42, 0) * 2 - 1;
                double warpY = fbm(nx * 1.45 - 8.1, ny * 1.45 + 6.2,
                        timeSeconds * 0.37, 1) * 2 - 1;
                double flowX = nx + warpX * 0.22;
                double flowY = ny + warpY * 0.22;
                double broad = fbm(flowX * 2.15, flowY * 2.15,
                        timeSeconds * 0.55, 2);
                double detail = fbm(flowX * 4.3 + 3.1, flowY * 4.3 - 5.4,
                        timeSeconds * 0.78, 3);
                double ridge = 1 - Math.abs(detail * 2 - 1);
                double position = MathUtil.clamp(0.50 + (broad - 0.5) * 1.45
                        + (ridge - 0.5) * 0.22, 0, 1);

                double palettePosition = position * (palette.size() - 1);
                int left = Math.min(palette.size() - 1, (int) Math.floor(palettePosition));
                int right = Math.min(palette.size() - 1, left + 1);
                double blend = palettePosition - left;
                double red = lerp(linearPalette[left][0], linearPalette[right][0], blend);
                double green = lerp(linearPalette[left][1], linearPalette[right][1], blend);
                double blue = lerp(linearPalette[left][2], linearPalette[right][2], blend);

                // 克制的光场营造出丝滑的高光，而不会产生白色过曝亮斑
                double light = 0.78 + 0.15 * ridge + 0.06 * (1 - broad);
                double edge = (nx - 0.5) * (nx - 0.5) + (ny - 0.5) * (ny - 0.5);
                light *= 0.94 - edge * 0.12;
                pixels[y * width + x] = ColorUtil.merge(toSrgb(red * light),
                        toSrgb(green * light), toSrgb(blue * light));
            }
        }
        return image;
    }

    /**
     * 范围在 [-1, 1] 内的平滑值噪声，其中时间用作连续漂移
     */
    private double fbm(double x, double y, double time, int channel) {
        double sum = 0;
        double amplitude = 0.5;
        double frequency = 1;
        double normalization = 0;
        for (int octave = 0; octave < OCTAVES; octave++) {
            double driftX = time * (0.14 + octave * 0.035) * (channel % 2 == 0 ? 1 : -1);
            double driftY = time * (0.11 + octave * 0.027) * (channel % 2 == 0 ? -1 : 1);
            sum += amplitude * valueNoise(x * frequency + driftX,
                    y * frequency + driftY, channel * 31 + octave);
            normalization += amplitude;
            amplitude *= 0.5;
            frequency *= 2.03;
        }
        return MathUtil.clamp(0.5 + 0.5 * sum / normalization, 0, 1);
    }

    private double valueNoise(double x, double y, int channel) {
        int ix = fastFloor(x);
        int iy = fastFloor(y);
        double tx = smooth(x - ix);
        double ty = smooth(y - iy);
        double v00 = hashUnit(ix, iy, channel);
        double v10 = hashUnit(ix + 1, iy, channel);
        double v01 = hashUnit(ix, iy + 1, channel);
        double v11 = hashUnit(ix + 1, iy + 1, channel);
        double top = lerp(v00, v10, tx);
        double bottom = lerp(v01, v11, tx);
        return lerp(top, bottom, ty);
    }

    private double hashUnit(int x, int y, int channel) {
        long value = seed;
        value ^= 0x9E3779B97F4A7C15L * (x * 0x632BE59BD9B4E019L);
        value ^= 0xC2B2AE3D27D4EB4FL * (y * 0x85157AF5L + channel * 0x165667B1L);
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return ((value >>> 11) * 0x1.0p-53) * 2 - 1;
    }

    private static long paletteSeed(List<Color> colors) {
        long value = 0x4D595DF4D0F33173L;
        for (Color color : colors) {
            value = value * 0x9E3779B97F4A7C15L
                    + (color.getRGB() & 0x00FFFFFFL);
            value ^= value >>> 29;
        }
        return value;
    }

    private static int toSrgb(double linear) {
        return SRGB[MathUtil.clamp((int) Math.round(Math.max(0, Math.min(1, linear)) * 4096), 0, 4096)];
    }

    private static double toLinear(int channel) {
        double value = channel / 255.0;
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static int fastFloor(double value) {
        int integer = (int) value;
        return value < integer ? integer - 1 : integer;
    }

    private static double smooth(double value) {
        return value * value * (3 - 2 * value);
    }

    private static double lerp(double a, double b, double amount) {
        return a + (b - a) * amount;
    }
}
