package net.doge.util.ui;

import net.doge.model.color.HSL;
import net.doge.model.color.HSV;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Doge
 * @Description 颜色工具类
 * @Date 2020/12/15
 */
public class ColorUtil {
    /**
     * 十六进制字符串转为 Color
     *
     * @param hex
     * @return
     */
    public static Color hexToColor(String hex) {
        try {
            return cn.hutool.core.img.ColorUtil.hexToColor(hex);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Color 转为十六进制字符串
     *
     * @param color
     * @return
     */
    public static String colorToHex(Color color) {
        if (color == null) return null;
        return cn.hutool.core.img.ColorUtil.toHex(color);
    }

    /**
     * 返回 RGB 颜色值中的 Alpha
     *
     * @param rgb
     * @return
     */
    public static int alpha(int rgb) {
        return rgb >> 24 & 0xFF;
    }

    /**
     * 返回 RGB 颜色值中的 Red
     *
     * @param rgb
     * @return
     */
    public static int red(int rgb) {
        return rgb >> 16 & 0xFF;
    }

    /**
     * 返回 RGB 颜色值中的 Green
     *
     * @param rgb
     * @return
     */
    public static int green(int rgb) {
        return rgb >> 8 & 0xFF;
    }

    /**
     * 返回 RGB 颜色值中的 Blue
     *
     * @param rgb
     * @return
     */
    public static int blue(int rgb) {
        return rgb & 0xFF;
    }

//    /**
//     * 设置 RGB 数值颜色透明度(0-255)
//     *
//     * @param color
//     * @param alpha
//     * @return
//     */
//    public static int setAlphaComponent(int rgb, int alpha) {
//        if (alpha >= 0 && alpha <= 255) return rgb & 16777215 | alpha << 24;
//        else throw new IllegalArgumentException("alpha must be between 0 and 255.");
//    }

    /**
     * 获取颜色亮度
     *
     * @param rgb
     * @return
     */
    public static double calculateLuminance(int rgb) {
        double red = red(rgb) / 255d;
        red = red < 0.03928 ? red / 12.92 : Math.pow((red + 0.055) / 1.055, 2.4);

        double green = green(rgb) / 255d;
        green = green < 0.03928 ? green / 12.92 : Math.pow((green + 0.055) / 1.055, 2.4);

        double blue = blue(rgb) / 255d;
        blue = blue < 0.03928 ? blue / 12.92 : Math.pow((blue + 0.055) / 1.055, 2.4);

        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

//    private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
//        return 0xFF - (((0xFF - backgroundAlpha) * (0xFF - foregroundAlpha)) / 0xFF);
//    }
//
//    private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a) {
//        if (a == 0) return 0;
//        return ((0xFF * fgC * fgA) + (bgC * bgA * (0xFF - fgA))) / (a * 0xFF);
//    }
//
//    /**
//     * Composite two potentially translucent colors over each other and returns the result.
//     */
//    public static int compositeColors(int foreground, int background) {
//        int bgAlpha = alpha(background), fgAlpha = alpha(foreground);
//        int a = compositeAlpha(fgAlpha, bgAlpha);
//
//        int r = compositeComponent(red(foreground), fgAlpha, red(background), bgAlpha, a);
//        int g = compositeComponent(green(foreground), fgAlpha, green(background), bgAlpha, a);
//        int b = compositeComponent(blue(foreground), fgAlpha, blue(background), bgAlpha, a);
//
//        return merge(a, r, g, b);
//    }
//
//    /**
//     * Returns the contrast ratio between {@code foreground} and {@code background}.
//     * {@code background} must be opaque.
//     * <p>
//     * Formula defined
//     * <a href="http://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef">here</a>.
//     */
//    public static double calculateContrast(int foreground, int background) {
//        if (alpha(background) != 255) {
//            throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(background));
//        }
//        if (alpha(foreground) < 255) {
//            // If the foreground is translucent, composite the foreground over the background
//            foreground = compositeColors(foreground, background);
//        }
//
//        final double luminance1 = calculateLuminance(foreground) + 0.05;
//        final double luminance2 = calculateLuminance(background) + 0.05;
//
//        // Now return the lighter luminance divided by the darker luminance
//        return Math.max(luminance1, luminance2) / Math.min(luminance1, luminance2);
//    }
//
//    /**
//     * Calculates the minimum alpha value which can be applied to {@code foreground} so that would
//     * have a contrast value of at least {@code minContrastRatio} when compared to
//     * {@code background}.
//     *
//     * @param foreground       the foreground color.
//     * @param background       the background color. Should be opaque.
//     * @param minContrastRatio the minimum contrast ratio.
//     * @return the alpha value in the range 0-255, or -1 if no value could be calculated.
//     */
//    public static int calculateMinimumAlpha(int foreground, int background, float minContrastRatio) {
//        if (alpha(background) != 255)
//            throw new IllegalArgumentException("background can not be translucent: #" + Integer.toHexString(background));
//
//        // First lets check that a fully opaque foreground has sufficient contrast
//        int testForeground = setAlphaComponent(foreground, 255);
//        double testRatio = calculateContrast(testForeground, background);
//        if (testRatio < minContrastRatio) {
//            // Fully opaque foreground does not have sufficient contrast, return error
//            return -1;
//        }
//
//        // Binary search to find a value with the minimum value which provides sufficient contrast
//        int numIterations = 0;
//        int minAlpha = 0;
//        int maxAlpha = 255;
//
//        while (numIterations <= 10 &&
//                (maxAlpha - minAlpha) > 1) {
//            final int testAlpha = (minAlpha + maxAlpha) / 2;
//
//            testForeground = setAlphaComponent(foreground, testAlpha);
//            testRatio = calculateContrast(testForeground, background);
//
//            if (testRatio < minContrastRatio) {
//                minAlpha = testAlpha;
//            } else {
//                maxAlpha = testAlpha;
//            }
//
//            numIterations++;
//        }
//
//        // Conservatively return the max of the range of possible alphas, which is known to pass.
//        return maxAlpha;
//    }

    /**
     * 合并 ARGB 四个值(0-255)
     *
     * @param
     * @return
     */
    public static int merge(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * 合并 RGB 三个值(0-255)
     *
     * @param
     * @return
     */
    public static int merge(int r, int g, int b) {
        return merge(255, r, g, b);
    }

    /**
     * 合并 RGB 三个值(0-1)
     *
     * @param
     * @return
     */
    public static int merge(float r, float g, float b) {
        return merge((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    /**
     * RGB 值转 HSV
     *
     * @param
     * @return
     */
    public static HSV rgbValToHsv(int rgb) {
        int r = red(rgb), g = green(rgb), b = blue(rgb);
        float r1 = (float) r / 255, g1 = (float) g / 255, b1 = (float) b / 255;
        float max = Math.max(r1, Math.max(g1, b1)), min = Math.min(r1, Math.min(g1, b1));
        float diff = max - min;
        float hue = 0;
        if (diff == 0) hue = 0;
        else {
            if (max == r1) hue = (((g1 - b1) / diff) % 6) * 60;
            else if (max == g1) hue = (((b1 - r1) / diff) + 2) * 60;
            else if (max == b1) hue = (((r1 - g1) / diff) + 4) * 60;
        }
        if (hue < 0) hue += 360;
        float s;
        if (max == 0) s = 0;
        else s = diff / max;
        return new HSV(hue, s * 100, max * 100);
    }

    /**
     * Color 转 HSV
     *
     * @param
     * @return
     */
    public static HSV colorToHsv(Color color) {
        return rgbValToHsv(color.getRGB());
    }

//    /**
//     * HSV 转 int
//     *
//     * @param
//     * @return
//     */
//    public static int hsvToIntColor(HSV hsv) {
//        return hsvToIntColor(hsv.h, hsv.s, hsv.v);
//    }

    /**
     * HSV 三个值转 Color
     *
     * @param
     * @return
     */
    public static Color hsvValToColor(float h, float s, float v) {
        s /= 100;
        v /= 100;
        float f, p, q, t;
        if (s == 0) return new Color(merge(v, v, v));
        h /= 60;
        int i = (int) h;
        f = h - i;
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));
        switch (i) {
            case 0:
                return new Color(merge(v, t, p));
            case 1:
                return new Color(merge(q, v, p));
            case 2:
                return new Color(merge(p, v, t));
            case 3:
                return new Color(merge(p, q, v));
            case 4:
                return new Color(merge(t, p, v));
            default:
                return new Color(merge(v, p, q));
        }
    }

//    /**
//     * HSV 转 Color
//     *
//     * @param
//     * @return
//     */
//    public static Color hsvToColor(HSV hsv) {
//        return hsvValToColor(hsv.h, hsv.s, hsv.v);
//    }

//    /**
//     * 通过 HSV 模型选取更亮或更暗的颜色
//     *
//     * @param color
//     * @return
//     */
//    public static Color hsvDiffPick(Color color, float diff) {
//        HSV hsv = colorToHsv(color);
//        return hsvToColor((hsv.h + diff) % 360, hsv.s, hsv.v);
//    }

    /**
     * 更亮的颜色(RGB 算法)
     *
     * @param color
     * @return
     */
    public static Color brighter(Color color) {
        return brighter(color, 0.15f);
    }

    /**
     * 更亮的颜色(RGB 算法)，带因子
     *
     * @param color
     * @return
     */
    public static Color brighter(Color color, float factor) {
        int red = (int) ((color.getRed() * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((color.getGreen() * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((color.getBlue() * (1 - factor) / 255 + factor) * 255);
        return new Color(red, green, blue, color.getAlpha());
    }

    /**
     * 更亮的颜色(HSL 算法)，带因子
     *
     * @param color
     * @return
     */
    public static Color hslLighten(Color color, float factor) {
        HSL hsl = colorToHsl(color);
        hsl.l *= 1 + factor;
        if (hsl.l < 0) hsl.l = 0;
        else if (hsl.l > 100) hsl.l = 100;
        return hslToColor(hsl);
    }

    /**
     * 更暗的颜色(RGB 算法)
     *
     * @param color
     * @return
     */
    public static Color darker(Color color) {
        return darker(color, 0.15f);
    }

    /**
     * 更暗的颜色(RGB 算法)，带因子
     *
     * @param color
     * @return
     */
    public static Color darker(Color color, float factor) {
        int red = (int) ((color.getRed() * (1 - factor)));
        int green = (int) ((color.getGreen() * (1 - factor)));
        int blue = (int) ((color.getBlue() * (1 - factor)));
        return new Color(red, green, blue, color.getAlpha());
    }

    /**
     * 更暗的颜色，带因子(HSL 算法)
     *
     * @param color
     * @return
     */
    public static Color hslDarken(Color color, float factor) {
        HSL hsl = colorToHsl(color);
        hsl.l *= 1 - factor;
        if (hsl.l < 0) hsl.l = 0;
        else if (hsl.l > 100) hsl.l = 100;
        return hslToColor(hsl);
    }

//    /**
//     * 调整颜色亮度使之成为较暗较柔和的颜色
//     *
//     * @param color
//     * @return
//     */
//    public static Color darkMuted(Color color, float sMin, float sMax, float lMin, float lMax) {
//        HSL hsl = colorToHsl(color);
//        if (hsl.s < sMin) hsl.s = sMin;
//        else if (hsl.s > sMax) hsl.s = sMax;
//        if (hsl.l < lMin) hsl.l = lMin;
//        else if (hsl.l > lMax) hsl.l = lMax;
//        return hslToColor(hsl);
//    }

    /**
     * 旋转颜色，改变颜色色相
     *
     * @param color
     * @return
     */
    public static Color rotate(Color color, float deg) {
        HSL hsl = colorToHsl(color);
        float d = hsl.h + deg;
        hsl.h = d >= 0 ? d % 360 : 360 + d;
        return hslToColor(hsl);
    }

//    /**
//     * Color 转 RGB 数组
//     *
//     * @param color
//     * @return
//     */
//    public static int[] colorToRgbArray(Color color) {
//        if (color == null) return null;
//        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
//    }
//
//    /**
//     * RGB 数组转 Color
//     *
//     * @param a
//     * @return
//     */
//    public static Color rgbArrayToColor(int[] a) {
//        if (a == null) return null;
//        return new Color(a[0], a[1], a[2]);
//    }

    /**
     * RGB 三个值转 HSL
     *
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static HSL rgbValToHsl(int red, int green, int blue) {
        float r, g, b, delta, max, min;
        float h, s, l;
        /*
         * Convert RGB to HSL colorspace.
         */
        r = (float) red / 255;
        g = (float) green / 255;
        b = (float) blue / 255;
        max = Math.max(r, Math.max(g, b));
        min = Math.min(r, Math.min(g, b));

        h = 0;
        s = 0;
        l = (min + max) / 2;
        delta = max - min;
        if (delta == 0) return new HSL(h * 360, s * 100, l * 100);
        s = delta / ((l <= 0.5f) ? (min + max) : (2 - max - min));
        if (r == max) h = g == min ? 5 + (max - b) / delta : 1 - (max - g) / delta;
        else if (g == max) h = b == min ? 1 + (max - r) / delta : 3 - (max - b) / delta;
        else h = r == min ? 3 + (max - g) / delta : 5 - (max - r) / delta;
        h /= 6;
        if (h == 1) h = 0;

        return new HSL(h * 360, s * 100, l * 100);
    }

    /**
     * Color 转 HSL
     *
     * @param color
     * @return
     */
    public static HSL colorToHsl(Color color) {
        return rgbValToHsl(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * HSL 三个值转 Color
     *
     * @param h
     * @param s
     * @param l
     * @return
     */
    public static Color hslValToColor(float h, float s, float l) {
        h = h / 360;
        s = s / 100;
        l = l / 100;
        float r, g, b, v, x, y, z;

        /*
         * Convert HSL to RGB colorspace.
         */
        v = l <= 0.5f ? l * (1 + s) : l + s - l * s;
        if (s == 0) return new Color((int) (255 * l + 0.5f), (int) (255 * l + 0.5f), (int) (255 * l + 0.5f));
        y = 2 * l - v;
        float v1 = (float) ((v - y) * (6 * h - Math.floor(6 * h)));
        x = y + v1;
        z = v - v1;
        switch ((int) (6 * h)) {
            case 1:
                r = z;
                g = v;
                b = y;
                break;
            case 2:
                r = y;
                g = v;
                b = x;
                break;
            case 3:
                r = y;
                g = z;
                b = v;
                break;
            case 4:
                r = x;
                g = y;
                b = v;
                break;
            case 5:
                r = v;
                g = y;
                b = z;
                break;
            case 0:
            default:
                r = v;
                g = x;
                b = y;
                break;
        }
        return new Color((int) (255 * r + 0.5f), (int) (255 * g + 0.5f), (int) (255 * b + 0.5f));
    }

    /**
     * HSL 转 Color
     *
     * @param hsl
     * @return
     */
    public static Color hslToColor(HSL hsl) {
        return hslValToColor(hsl.h, hsl.s, hsl.l);
    }

//    /**
//     * 判断色块是否已经在调色板
//     *
//     * @param palette
//     * @param swatch
//     * @return
//     */
//    private static boolean isSwatchSelected(Palette palette, Color swatch) {
//        return swatch.equals(palette.vibrant)
//                || swatch.equals(palette.lightVibrant)
//                || swatch.equals(palette.darkVibrant)
//                || swatch.equals(palette.muted)
//                || swatch.equals(palette.lightMuted)
//                || swatch.equals(palette.darkMuted);
//    }
//
//    /**
//     * 根据参数返回色块
//     *
//     * @return
//     */
//    public static Color swatch(List<Color> colors, List<MMCQ.ThemeColor> themeColors, Palette palette,
//                               float lTarget, float lMin, float lMax,
//                               float sTarget, float sMin, float sMax) {
//        Color res = null;
//        float maxWeight = 0, maxPopulation = 0;
//        int size = colors.size();
//        float[] pop = new float[size];
//        // 找到最大的颜色分布
//        for (int i = 0; i < size; i++) {
//            pop[i] = (float) themeColors.get(i).getProportion();
//            maxPopulation = Math.max(maxPopulation, pop[i]);
//        }
//        final float sWeight = 3f, lWeight = 6.5f, pWeight = 0.5f, weightSum = sWeight + lWeight + pWeight;
//        for (int i = 0; i < size; i++) {
//            Color color = colors.get(i);
//            HSL hsl = colorToHsl(color);
//            if (hsl.s >= sMin && hsl.s <= sMax && hsl.l >= lMin && hsl.l <= lMax && !isSwatchSelected(palette, color)) {
//                // 根据饱和度、亮度、颜色分布范围对该颜色求一个加权平均值
//                float weight = invertDiff(hsl.s, sTarget) * sWeight + invertDiff(hsl.l, lTarget) * lWeight + pop[i] / maxPopulation * pWeight;
//                weight /= weightSum;
//                if (res == null || weight > maxWeight) {
//                    res = color;
//                    maxWeight = weight;
//                }
//            }
//        }
//        return res;
//    }

//    private static final float targetDarkLuma = 26, maxDarkLuma = 45, minLightLuma = 55, targetLightLuma = 74, minNormalLuma = 30,
//            targetNormalLuma = 50, maxNormalLuma = 70, targetMutesSaturation = 30, maxMutesSaturation = 40, targetVibrantSaturation = 100, minVibrantSaturation = 35;

    /**
     * 根据图片和颜色数量生成 Palette，并找到最佳色块返回
     *
     * @param img
     * @param colorCount
     * @return
     */
    public static Color getBestSwatch(BufferedImage img, int colorCount) {
        MMCQ mmcq = new MMCQ(img, colorCount);
        List<MMCQ.ThemeColor> themeColors = mmcq.quantize();
        List<Color> colors = themeColors.stream().map(c -> new Color(c.getColor())).collect(Collectors.toList());
        Color defColor = colors.get(0);
//        ColorThiefUtil.CMap colorMap = ColorThiefUtil.getColorMap(img, colorCount);
//        List<Color> colors = colorMap.palette();
//        if (colors.isEmpty()) colors.add(Colors.GRAY);
//
//        Palette palette = new Palette();
//        palette.vibrant = swatch(colors, themeColors, palette, targetNormalLuma, minNormalLuma, maxNormalLuma, targetVibrantSaturation, minVibrantSaturation, 100);
//        palette.lightVibrant = swatch(colors, themeColors, palette, targetLightLuma, minLightLuma, 100, targetVibrantSaturation, minVibrantSaturation, 100);
//        palette.darkVibrant = swatch(colors, themeColors, palette, targetDarkLuma, 0, maxDarkLuma, targetVibrantSaturation, minVibrantSaturation, 100);
//        palette.muted = swatch(colors, themeColors, palette, targetNormalLuma, minNormalLuma, maxNormalLuma, targetMutesSaturation, 0, maxMutesSaturation);
//        palette.lightMuted = swatch(colors, themeColors, palette, targetLightLuma, minLightLuma, 100, targetMutesSaturation, 0, maxMutesSaturation);
//        palette.darkMuted = swatch(colors, themeColors, palette, targetDarkLuma, 0, maxDarkLuma, targetMutesSaturation, 0, maxMutesSaturation);
//
//        optimizePalette(palette);
//
//        switch (BlurConstants.gradientColorStyleIndex) {
//            case BlurConstants.VIBRANT:
//            default:
//                return palette.vibrant == null ? defColor : palette.vibrant;
//            case BlurConstants.LIGHT_VIBRANT:
//                return palette.lightVibrant == null ? defColor : palette.lightVibrant;
//            case BlurConstants.DARK_VIBRANT:
//                return palette.darkVibrant == null ? defColor : palette.darkVibrant;
//            case BlurConstants.MUTED:
//                return palette.muted == null ? defColor : palette.muted;
//            case BlurConstants.LIGHT_MUTED:
//                return palette.lightMuted == null ? defColor : palette.lightMuted;
//            case BlurConstants.DARK_MUTED:
//                return palette.darkMuted == null ? defColor : palette.darkMuted;
//        }
        return findBestSwatch(defColor);
    }

    /**
     * 找到最佳色块
     *
     * @param swatch
     * @return
     */
    private static Color findBestSwatch(Color swatch) {
        HSL hsl = ColorUtil.colorToHsl(swatch);
        final float maxS = 50, minL = 60, maxL = 80;
        if (hsl.s > maxS) hsl.s = maxS;
        if (hsl.l < minL) hsl.l = minL;
        else if (hsl.l > maxL) hsl.l = maxL;
        return ColorUtil.hslToColor(hsl);
    }

//    /**
//     * 优化 Palette
//     *
//     * @param palette
//     * @return
//     */
//    private static void optimizePalette(Palette palette) {
//        HSL hsl;
//        if (palette.vibrant == null && palette.darkVibrant == null && palette.lightVibrant == null) {
//            if (palette.darkVibrant == null && palette.darkMuted != null) {
//                hsl = colorToHsl(palette.darkMuted);
//                hsl.l = targetDarkLuma;
//                palette.darkVibrant = hslToColor(hsl);
//            }
//            if (palette.lightVibrant == null && palette.lightMuted != null) {
//                hsl = colorToHsl(palette.lightMuted);
//                hsl.l = targetDarkLuma;
//                palette.darkVibrant = hslToColor(hsl);
//            }
//        }
//        if (palette.vibrant == null && palette.darkVibrant != null) {
//            hsl = colorToHsl(palette.darkVibrant);
//            hsl.l = targetNormalLuma;
//            palette.vibrant = hslToColor(hsl);
//        } else if (palette.vibrant == null && palette.lightVibrant != null) {
//            hsl = colorToHsl(palette.lightVibrant);
//            hsl.l = targetNormalLuma;
//            palette.vibrant = hslToColor(hsl);
//        }
//        if (palette.darkVibrant == null && palette.vibrant != null) {
//            hsl = colorToHsl(palette.vibrant);
//            hsl.l = targetDarkLuma;
//            palette.darkVibrant = hslToColor(hsl);
//        }
//        if (palette.lightVibrant == null && palette.vibrant != null) {
//            hsl = colorToHsl(palette.vibrant);
//            hsl.l = targetLightLuma;
//            palette.lightVibrant = hslToColor(hsl);
//        }
//        if (palette.muted == null && palette.vibrant != null) {
//            hsl = colorToHsl(palette.vibrant);
//            hsl.l = targetMutesSaturation;
//            palette.muted = hslToColor(hsl);
//        }
//        if (palette.darkMuted == null && palette.darkVibrant != null) {
//            hsl = colorToHsl(palette.darkVibrant);
//            hsl.l = targetMutesSaturation;
//            palette.darkMuted = hslToColor(hsl);
//        }
//        if (palette.lightMuted == null && palette.lightVibrant != null) {
//            hsl = colorToHsl(palette.lightVibrant);
//            hsl.l = targetMutesSaturation;
//            palette.lightMuted = hslToColor(hsl);
//        }
//    }
//
//    private static float invertDiff(float v1, float v2) {
//        return 1 - Math.abs((v1 - v2) / 100);
//    }
}
