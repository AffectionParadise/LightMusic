package net.doge.util.ui;

import java.awt.*;

/**
 * @author Doge
 * @description 缩放工具类
 * @date 2020/12/15
 */
public class ScaleUtil {
    public static final float SCALE = Toolkit.getDefaultToolkit().getScreenResolution() / 96f;

    /**
     * 缩放一个值
     *
     * @param value
     * @return
     */
    public static int scale(int value) {
        if (value > Integer.MAX_VALUE / SCALE) return value;
        return (int) (value * SCALE);
    }

    public static double scale(double value) {
        if (value > Double.MAX_VALUE / SCALE) return value;
        return value * SCALE;
    }
}