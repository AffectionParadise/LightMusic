package net.doge.utils;

import net.doge.models.HSV;

import java.awt.*;

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
     * Color 转 HSV
     *
     * @param
     * @return
     */
    public static HSV colorToHsv(Color color) {
        int R = color.getRed(), G = color.getGreen(), B = color.getBlue();
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

    /**
     * 通过 HSV 模型选取更亮或更暗的颜色
     *
     * @param color
     * @return
     */
    public static Color hsvDiffPick(Color color, float diff) {
        HSV hsv = colorToHsv(color);
        return hsvToColor((hsv.h + diff) % 360, hsv.s, hsv.v);
    }

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
     * 获取颜色亮度
     *
     * @param color
     * @return
     */
    public static double lightness(Color color) {
        return Math.pow(Math.pow(color.getRed() / 255.0f, 2.2f) + Math.pow(color.getGreen() / 170.0f, 2.2f) + Math.pow(color.getBlue() / 425.0f, 2.2f), 1 / 2.2f) * 0.547373141f;
    }

//    /**
//     * Color 转为十六进制字符串
//     *
//     * @param color
//     * @return
//     */
//    public static String colorToHexString(Color color) {
//        String r = Integer.toHexString(color.getRed());
//        r = r.length() < 2 ? ('0' + r) : r;
//        String g = Integer.toHexString(color.getGreen());
//        g = g.length() < 2 ? ('0' + g) : g;
//        String b = Integer.toHexString(color.getBlue());
//        b = b.length() < 2 ? ('0' + b) : b;
//        return '#' + r + g + b;
//    }
//
//    /**
//     * 十六进制字符串转为 Color
//     *
//     * @param str
//     * @return
//     */
//    public static Color hexStringToColor(String str) {
//        return new Color(
//                Integer.parseInt(str.substring(1, 3), 16),
//                Integer.parseInt(str.substring(3, 5), 16),
//                Integer.parseInt(str.substring(5), 16)
//        );
//    }

//    /**
//     * awt 的 Color 转为 JavaFx 的 Color
//     *
//     * @param color
//     * @return
//     */
//    public static javafx.scene.paint.Color javaFxColor(Color color) {
//        return javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), (double) color.getAlpha() / 255);
//    }

//    /**
//     * JavaFx 的 Color 转为 awt 的 Color
//     *
//     * @param color
//     * @return
//     */
//    public static Color awtColor(javafx.scene.paint.Color color) {
//        return new Color((int) (255 * color.getRed()), (int) (255 * color.getGreen()), (int) (255 * color.getBlue()), (int) (255 * color.getOpacity()));
//    }
}
