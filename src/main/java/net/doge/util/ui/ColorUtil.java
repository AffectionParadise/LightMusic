package net.doge.util.ui;

import net.doge.constant.ui.BlurConstants;
import net.doge.constant.ui.Colors;
import net.doge.model.color.HSL;
import net.doge.model.color.HSV;
import net.doge.model.color.Palette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

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
     * 合并 RGB 三个值(0-255)
     *
     * @param
     * @return
     */
    public static int merge(int r, int g, int b) {
        return r << 16 | g << 8 | b;
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
        int R = (rgb >> 16) & 0xFF, G = (rgb >> 8) & 0xFF, B = rgb & 0xFF;
        float R_1 = (float) R / 255, G_1 = (float) G / 255, B_1 = (float) B / 255;
        float max = Math.max(R_1, Math.max(G_1, B_1)), min = Math.min(R_1, Math.min(G_1, B_1));
        float diff = max - min;
        float hue = 0;
        if (diff == 0) hue = 0;
        else {
            if (max == R_1) hue = (((G_1 - B_1) / diff) % 6) * 60;
            else if (max == G_1) hue = (((B_1 - R_1) / diff) + 2) * 60;
            else if (max == B_1) hue = (((R_1 - G_1) / diff) + 4) * 60;
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

    /**
     * Color 转 RGB 数组
     *
     * @param color
     * @return
     */
    public static int[] colorToRgbArray(Color color) {
        if (color == null) return null;
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    /**
     * RGB 数组转 Color
     *
     * @param a
     * @return
     */
    public static Color rgbArrayToColor(int[] a) {
        if (a == null) return null;
        return new Color(a[0], a[1], a[2]);
    }

    /**
     * 获取颜色亮度
     *
     * @param rgb
     * @return
     */
    public static double lightness(int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return Math.pow(Math.pow(r / 255.0f, 2.2f) + Math.pow(g / 170.0f, 2.2f) + Math.pow(b / 425.0f, 2.2f), 1 / 2.2f) * 0.547373141f;
//        return colorToHsl(r, g, b).l / 100;
    }

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

    /**
     * 判断色块是否已经在调色板
     *
     * @param palette
     * @param swatch
     * @return
     */
    private static boolean isSwatchSelected(Palette palette, Color swatch) {
        return swatch.equals(palette.vibrant)
                || swatch.equals(palette.lightVibrant)
                || swatch.equals(palette.darkVibrant)
                || swatch.equals(palette.muted)
                || swatch.equals(palette.lightMuted)
                || swatch.equals(palette.darkMuted);
    }

    /**
     * 根据参数返回色块
     *
     * @return
     */
    public static Color swatch(List<Color> colors, ColorThiefUtil.CMap colorMap, Palette palette,
                               float lTarget, float lMin, float lMax,
                               float sTarget, float sMin, float sMax) {
        Color res = null;
        float maxWeight = 0, maxPopulation = 0;
        int size = colors.size();
        int[] pop = new int[size];
        // 找到最大的颜色分布
        for (int i = 0; i < size; i++) {
            pop[i] = colorMap.vboxes.get(i).count(false);
            maxPopulation = Math.max(maxPopulation, pop[i]);
        }
        final float sWeight = 3f, lWeight = 6.5f, pWeight = 0.5f, weightSum = sWeight + lWeight + pWeight;
        for (int i = 0; i < size; i++) {
            Color color = colors.get(i);
            HSL hsl = colorToHsl(color);
            if (hsl.s >= sMin && hsl.s <= sMax && hsl.l >= lMin && hsl.l <= lMax && !isSwatchSelected(palette, color)) {
                // 根据饱和度、亮度、颜色分布范围对该颜色求一个加权平均值
                float weight = invertDiff(hsl.s, sTarget) * sWeight + invertDiff(hsl.l, lTarget) * lWeight + pop[i] / maxPopulation * pWeight;
                weight /= weightSum;
                if (res == null || weight > maxWeight) {
                    res = color;
                    maxWeight = weight;
                }
            }
        }
        return res;
    }

    private static final float targetDarkLuma = 26, maxDarkLuma = 45, minLightLuma = 55, targetLightLuma = 74, minNormalLuma = 30,
            targetNormalLuma = 50, maxNormalLuma = 70, targetMutesSaturation = 30, maxMutesSaturation = 40, targetVibrantSaturation = 100, minVibrantSaturation = 35;

    /**
     * 根据图片和颜色数量生成 Palette，并找到最佳色块返回
     *
     * @param img
     * @param colorCount
     * @return
     */
    public static Color getBestSwatch(BufferedImage img, int colorCount) {
        ColorThiefUtil.CMap colorMap = ColorThiefUtil.getColorMap(img, colorCount);
        List<Color> colors = colorMap.palette();
        if (colors.isEmpty()) colors.add(Colors.GRAY);

        Palette palette = new Palette();
        palette.vibrant = swatch(colors, colorMap, palette, targetNormalLuma, minNormalLuma, maxNormalLuma, targetVibrantSaturation, minVibrantSaturation, 100);
        palette.lightVibrant = swatch(colors, colorMap, palette, targetLightLuma, minLightLuma, 100, targetVibrantSaturation, minVibrantSaturation, 100);
        palette.darkVibrant = swatch(colors, colorMap, palette, targetDarkLuma, 0, maxDarkLuma, targetVibrantSaturation, minVibrantSaturation, 100);
        palette.muted = swatch(colors, colorMap, palette, targetNormalLuma, minNormalLuma, maxNormalLuma, targetMutesSaturation, 0, maxMutesSaturation);
        palette.lightMuted = swatch(colors, colorMap, palette, targetLightLuma, minLightLuma, 100, targetMutesSaturation, 0, maxMutesSaturation);
        palette.darkMuted = swatch(colors, colorMap, palette, targetDarkLuma, 0, maxDarkLuma, targetMutesSaturation, 0, maxMutesSaturation);

        optimizePalette(palette);

        switch (BlurConstants.gradientColorStyleIndex) {
            case BlurConstants.VIBRANT:
            default:
                return palette.vibrant;
            case BlurConstants.LIGHT_VIBRANT:
                return palette.lightVibrant;
            case BlurConstants.DARK_VIBRANT:
                return palette.darkVibrant;
            case BlurConstants.MUTED:
                return palette.muted;
            case BlurConstants.LIGHT_MUTED:
                return palette.lightMuted;
            case BlurConstants.DARK_MUTED:
                return palette.darkMuted;
        }
//        return findBestSwatch(colors.get(0));
    }

//    /**
//     * 找到最佳色块
//     *
//     * @param swatch
//     * @return
//     */
//    private static Color findBestSwatch(Color swatch) {
//        HSL hsl = ColorUtil.colorToHsl(swatch);
//        if (hsl.s > 40) hsl.s = 40;
//        if (hsl.l < 40) hsl.l = 40;
//        else if (hsl.l > 60) hsl.l = 60;
//        swatch = ColorUtil.hslToColor(hsl);
//        return swatch;
//    }

    /**
     * 优化 Palette
     *
     * @param palette
     * @return
     */
    private static void optimizePalette(Palette palette) {
        HSL hsl;
        if (palette.vibrant == null && palette.darkVibrant == null && palette.lightVibrant == null) {
            if (palette.darkVibrant == null && palette.darkMuted != null) {
                hsl = colorToHsl(palette.darkMuted);
                hsl.l = targetDarkLuma;
                palette.darkVibrant = hslToColor(hsl);
            }
            if (palette.lightVibrant == null && palette.lightMuted != null) {
                hsl = colorToHsl(palette.lightMuted);
                hsl.l = targetDarkLuma;
                palette.darkVibrant = hslToColor(hsl);
            }
        }
        if (palette.vibrant == null && palette.darkVibrant != null) {
            hsl = colorToHsl(palette.darkVibrant);
            hsl.l = targetNormalLuma;
            palette.vibrant = hslToColor(hsl);
        } else if (palette.vibrant == null && palette.lightVibrant != null) {
            hsl = colorToHsl(palette.lightVibrant);
            hsl.l = targetNormalLuma;
            palette.vibrant = hslToColor(hsl);
        }
        if (palette.darkVibrant == null && palette.vibrant != null) {
            hsl = colorToHsl(palette.vibrant);
            hsl.l = targetDarkLuma;
            palette.darkVibrant = hslToColor(hsl);
        }
        if (palette.lightVibrant == null && palette.vibrant != null) {
            hsl = colorToHsl(palette.vibrant);
            hsl.l = targetLightLuma;
            palette.lightVibrant = hslToColor(hsl);
        }
        if (palette.muted == null && palette.vibrant != null) {
            hsl = colorToHsl(palette.vibrant);
            hsl.l = targetMutesSaturation;
            palette.muted = hslToColor(hsl);
        }
        if (palette.darkMuted == null && palette.darkVibrant != null) {
            hsl = colorToHsl(palette.darkVibrant);
            hsl.l = targetMutesSaturation;
            palette.darkMuted = hslToColor(hsl);
        }
        if (palette.lightMuted == null && palette.lightVibrant != null) {
            hsl = colorToHsl(palette.lightVibrant);
            hsl.l = targetMutesSaturation;
            palette.lightMuted = hslToColor(hsl);
        }
    }

    private static float invertDiff(float v1, float v2) {
        return 1 - Math.abs((v1 - v2) / 100);
    }
}
