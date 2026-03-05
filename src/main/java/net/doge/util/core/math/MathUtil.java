package net.doge.util.core.math;

/**
 * 数学工具类
 */
public class MathUtil {
    /**
     * 限制一个值在 [min, max] 范围
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
