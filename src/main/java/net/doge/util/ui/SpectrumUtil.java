package net.doge.util.ui;

import net.doge.constant.core.ui.spectrum.SpectrumConstants;
import net.doge.entity.core.player.MusicPlayer;

/**
 * @author Doge
 * @description
 * @date 2020/12/9
 */
public class SpectrumUtil {
    private static final int ABS_THRESHOLD = Math.abs(SpectrumConstants.THRESHOLD);
    private static final int NUM_BANDS = SpectrumConstants.NUM_BANDS;

    /**
     * 处理单个 magnitude 数据，magnitude 在 THRESHOLD 到 0 之间
     * 将其处理成 0 到 BAR_MAX_HEIGHT 表示柱状图高度的数
     */
    public static double magnitudeToHeight(double magnitude) {
        return (magnitude + ABS_THRESHOLD) / ABS_THRESHOLD * ScaleUtil.scale(SpectrumConstants.barMaxHeight);
    }

    /**
     * 处理平铺数据并更新到与 UI 绑定的数组
     *
     * @param magnitudes
     * @param player
     */
    public static void handleMagnitudes(float[] magnitudes, MusicPlayer player) {
        int barNum = SpectrumConstants.barNum, nFactor = barNum - 30;
        double avg = 0;
        for (int i = 0; i < NUM_BANDS; i++) {
            int mult = i / barNum;
            int n = mult % 2 == 0 ? i - barNum * mult : barNum - (i - barNum * mult);
            int spectrum = n > nFactor ? 0 : (int) SpectrumUtil.magnitudeToHeight(magnitudes[n + 20]);
            avg += spectrum * 1.2;
        }
        avg = avg / NUM_BANDS * 1.4 / barNum + 0.42;

        double[] specs = player.specs;
        double[] specsOrigin = player.specsOrigin;
        double[] specsGap = player.specsGap;
        int barMaxHeight = ScaleUtil.scale(SpectrumConstants.barMaxHeight);
        for (int i = 0; i < barNum; i++) {
            double h = Math.min(barMaxHeight, SpectrumUtil.magnitudeToHeight(magnitudes[i]) * avg);
            specsOrigin[i] = h;
            specsGap[i] = Math.abs(specsOrigin[i] - specs[i]);
        }
    }
}
