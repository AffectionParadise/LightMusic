package net.doge.util.ui;

import net.doge.constant.ui.SpectrumConstants;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/9
 */
public class SpectrumUtil {
    private static final int p = Math.abs(SpectrumConstants.THRESHOLD);

    /**
     * 处理单个 magnitude 数据，magnitude 在 THRESHOLD 到 0 之间
     * 将其处理成 0 到 BAR_MAX_HEIGHT 表示柱状图高度的数
     */
    public static double handleMagnitude(double magnitude) {
        return (magnitude + p) / p * SpectrumConstants.BAR_MAX_HEIGHT;
    }
}
