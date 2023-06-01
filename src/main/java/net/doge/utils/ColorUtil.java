package net.doge.utils;

import net.doge.constants.Colors;
import net.doge.models.color.HSL;
import net.doge.models.color.HSV;
import net.doge.models.color.Palette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @Author yzx
 * @Description 颜色工具类
 * @Date 2020/12/15
 */
public class ColorUtil {
    /**
     * Color 转为 RGB 字符串，格式 xxx,xxx,xxx
     *
     * @param color
     * @return
     */
    public static String colorToRGBString(Color color) {
        if (color == null) return "";
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    /**
     * RGB 字符串转为 Color，格式 xxx,xxx,xxx
     *
     * @param rgbStr
     * @return
     */
    public static Color RGBStringToColor(String rgbStr) {
        if (StringUtil.isEmpty(rgbStr)) return null;
        String[] split = rgbStr.split(",");
        return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    /**
     * 十六进制字符串转为 Color
     *
     * @param hex
     * @return
     */
    public static Color hexToColor(String hex) {
        if (StringUtil.isEmpty(hex)) return null;
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
    public static String toHex(Color color) {
        if (color == null) return null;
        return cn.hutool.core.img.ColorUtil.toHex(color);
    }

    /**
     * 合并 RGB 三个值
     *
     * @param
     * @return
     */
    public static int merge(int r, int g, int b) {
        return r << 16 | g << 8 | b;
    }

    /**
     * 合并 RGB 三个值
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
    public static HSV intColorToHsv(int rgb) {
        int R = (rgb >> 16) & 0xFF, G = (rgb >> 8) & 0xFF, B = rgb & 0xFF;
        float R_1 = R / 255f, G_1 = G / 255f, B_1 = B / 255f;
        float max = Math.max(R_1, Math.max(G_1, B_1)), min = Math.min(R_1, Math.min(G_1, B_1));
        float C_max = max, C_min = min;
        float diff = C_max - C_min;
        float hue = 0f;
        if (diff == 0f) hue = 0f;
        else {
            if (C_max == R_1) {
                hue = (((G_1 - B_1) / diff) % 6) * 60f;
            }
            if (C_max == G_1) {
                hue = (((B_1 - R_1) / diff) + 2f) * 60f;
            }
            if (C_max == B_1) {
                hue = (((R_1 - G_1) / diff) + 4f) * 60f;
            }
        }
        if (hue < 0) hue += 360;
        float saturation;
        if (C_max == 0f) saturation = 0f;
        else saturation = diff / C_max;
        float value = C_max;
        return new HSV(hue, saturation * 100, value * 100);
    }

    /**
     * Color 转 HSV
     *
     * @param
     * @return
     */
    public static HSV colorToHsv(Color color) {
        return intColorToHsv(color.getRGB());
    }

//    public static void main(String[] args) {
//        System.out.println(colorToHsv(new Color(255, 0, 34)));
//        System.out.println(hsvToColor(352, 100, 100));
//    }

    /**
     * HSV 转 int
     *
     * @param
     * @return
     */
    public static int hsvToIntColor(float h, float s, float v) {
        s /= 100;
        v /= 100;
        float f, p, q, t;
        if (s == 0) return merge(v, v, v);
        h /= 60;
        int i = (int) h;
        f = h - i;
        p = v * (1 - s);
        q = v * (1 - s * f);
        t = v * (1 - s * (1 - f));
        switch (i) {
            case 0:
                return merge(v, t, p);
            case 1:
                return merge(q, v, p);
            case 2:
                return merge(p, v, t);
            case 3:
                return merge(p, q, v);
            case 4:
                return merge(t, p, v);
            default:
                return merge(v, p, q);
        }
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
     * HSV 转 Color
     *
     * @param
     * @return
     */
    public static Color hsvToColor(float h, float s, float v) {
        return new Color(hsvToIntColor(h, s, v));
    }

//    /**
//     * HSV 转 Color
//     *
//     * @param
//     * @return
//     */
//    public static Color hsvToColor(HSV hsv) {
//        return new Color(hsvToIntColor(hsv.h, hsv.s, hsv.v));
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
     * 更亮的颜色
     *
     * @param color
     * @return
     */
    public static Color brighter(Color color) {
        return brighter(color, 0.15f);
    }

    /**
     * 更亮的颜色，带因子
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
     * 更亮的颜色，带因子
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
     * 更暗的颜色
     *
     * @param color
     * @return
     */
    public static Color darker(Color color) {
        return darker(color, 0.15f);
    }

    /**
     * 更暗的颜色，带因子
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
     * 更暗的颜色，带因子
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
//    public static Color darkMuted(Color color, double sMin, double sMax, double lMin, double lMax) {
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
        double d = hsl.h + deg;
        hsl.h = d >= 0 ? d % 360 : 360 + d;
        return hslToColor(hsl);
    }

    /**
     * Color 转 int[]
     *
     * @param color
     * @return
     */
    public static int[] colorToIntArray(Color color) {
        if (color == null) return null;
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    /**
     * int[] 转 Color
     *
     * @param a
     * @return
     */
    public static Color intArrayToColor(int[] a) {
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
//        return intColorToHsv(rgb).v / 100;
    }

    /**
     * Color 转 HSL
     *
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static HSL colorToHsl(int red, int green, int blue) {
        double b, delta, g, max, min, r;

        double hue, saturation, luminosity;
        /*
         * Convert RGB to HSL colorspace.
         */
        r = (double) red / 255;
        g = (double) green / 255;
        b = (double) blue / 255;
        max = Math.max(r, Math.max(g, b));
        min = Math.min(r, Math.min(g, b));

        hue = 0.0;
        saturation = 0.0;
        luminosity = (min + max) / 2.0;
        delta = max - min;
        if (delta == 0.0) {
            return new HSL(hue * 360, saturation * 100, luminosity * 100);
        }
        saturation = delta / ((luminosity <= 0.5) ? (min + max) : (2.0 - max - min));
        if (r == max)
            hue = (g == min ? 5.0 + (max - b) / delta : 1.0 - (max - g) / delta);
        else if (g == max)
            hue = (b == min ? 1.0 + (max - r) / delta : 3.0 - (max - b) / delta);
        else
            hue = (r == min ? 3.0 + (max - g) / delta : 5.0 - (max - r) / delta);
        hue /= 6.0;

        return new HSL(hue * 360, saturation * 100, luminosity * 100);
    }

    /**
     * Color 转 HSL
     *
     * @param color
     * @return
     */
    public static HSL colorToHsl(Color color) {
        return colorToHsl(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * HSL 转 Color
     *
     * @param hsl
     * @return
     */
    public static Color hslToColor(HSL hsl) {
        return hslToColor(hsl.h, hsl.s, hsl.l);
    }

    /**
     * HSL 转 Color
     *
     * @param h
     * @param s
     * @param l
     * @return
     */
    public static Color hslToColor(double h, double s, double l) {
        double hue = h / 360, saturation = s / 100, luminosity = l / 100;
        // int red, green, blue;
        double b, g, r, v, x, y, z;

        /*
         * Convert HSL to RGB colorspace.
         */
        v = (luminosity <= 0.5) ? (luminosity * (1.0 + saturation))
                : (luminosity + saturation - luminosity * saturation);
        if (saturation == 0.0) {
            return new Color((int) (255 * luminosity + 0.5), (int) (255 * luminosity + 0.5), (int) (255 * luminosity + 0.5));
        }
        y = 2.0 * luminosity - v;
        x = y + (v - y) * (6.0 * hue - Math.floor(6.0 * hue));
        z = v - (v - y) * (6.0 * hue - Math.floor(6.0 * hue));
        switch ((int) (6.0 * hue)) {
            case 0:
                r = v;
                g = x;
                b = y;
                break;
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
            default:
                r = v;
                g = x;
                b = y;
                break;
        }
        return new Color((int) (255 * r + 0.5), (int) (255 * g + 0.5), (int) (255 * b + 0.5));
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
                               double lTarget, double lMin, double lMax,
                               double sTarget, double sMin, double sMax) {
        Color res = null;
        double maxWeight = 0, maxPopulation = 0;
        int size = colors.size();
        int[] pop = new int[size];
        // 找到最大的颜色分布
        for (int i = 0; i < size; i++) {
            pop[i] = colorMap.vboxes.get(i).count(false);
            maxPopulation = Math.max(maxPopulation, pop[i]);
        }
        final double sWeight = 3, lWeight = 6.5, pWeight = 0.5, weightSum = sWeight + lWeight + pWeight;
        for (int i = 0; i < size; i++) {
            Color color = colors.get(i);
            HSL hsl = colorToHsl(color);
            if (hsl.s >= sMin && hsl.s <= sMax && hsl.l >= lMin && hsl.l <= lMax && !isSwatchSelected(palette, color)) {
                // 根据饱和度、亮度、颜色分布范围对该颜色求一个加权平均值
                double weight = invertDiff(hsl.s, sTarget) * sWeight + invertDiff(hsl.l, lTarget) * lWeight + pop[i] / maxPopulation * pWeight;
                weight /= weightSum;
                if (res == null || weight > maxWeight) {
                    res = color;
                    maxWeight = weight;
                }
            }
        }
        return res;
    }

    private static final double targetDarkLuma = 26, maxDarkLuma = 45, minLightLuma = 55, targetLightLuma = 74, minNormalLuma = 30,
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

        return findBestSwatch(palette, colors.get(0));
    }

    /**
     * 找到最佳色块
     *
     * @param palette
     * @param avg
     * @return
     */
    private static Color findBestSwatch(Palette palette, Color avg) {
        List<Color> swatches = Arrays.asList(palette.muted, palette.darkMuted);
        swatches.sort(Comparator.comparingDouble(c -> distance(c, avg)));
        return swatches.get(0);
    }

    /**
     * 两种颜色的距离
     *
     * @param c1
     * @param c2
     * @return
     */
    private static double distance(Color c1, Color c2) {
        return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2));
    }

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
            hsl.s = targetMutesSaturation;
            palette.muted = hslToColor(hsl);
        }
        if (palette.darkMuted == null && palette.darkVibrant != null) {
            hsl = colorToHsl(palette.darkVibrant);
            hsl.s = targetMutesSaturation;
            palette.darkMuted = hslToColor(hsl);
        }
        if (palette.lightMuted == null && palette.lightVibrant != null) {
            hsl = colorToHsl(palette.lightVibrant);
            hsl.s = targetMutesSaturation;
            palette.lightMuted = hslToColor(hsl);
        }
    }

    /**
     * 混合多种颜色
     *
     * @param colors
     * @return
     */
    public static Color mix(Color... colors) {
        int rn = 0, gn = 0, bn = 0, s = colors.length;
        for (Color color : colors) {
            rn += color.getRed();
            gn += color.getGreen();
            bn += color.getBlue();
        }
        return new Color(rn / s, gn / s, bn / s);
    }

    private static double invertDiff(double v1, double v2) {
        return 1 - Math.abs((v1 - v2) / 100);
    }
}
