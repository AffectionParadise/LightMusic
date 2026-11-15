package net.doge.util.ui;

import net.doge.constant.core.ui.spectrum.SpectrumConstants;
import net.doge.entity.core.player.MusicPlayer;
import net.doge.util.ui.processor.VisualSpectrumProcessor;

/**
 * @author Doge
 * @description
 * @date 2020/12/9
 */
public class SpectrumUtil {
    // 频谱可视化处理类
    private static VisualSpectrumProcessor processor;

    /**
     * 处理频谱数据并同步到 MusicPlayer
     *
     * @param magnitudes
     * @param duration
     * @param player
     */
    public static void processMagnitudes(float[] magnitudes, double duration, MusicPlayer player) {
        int barNum = SpectrumConstants.barNum;
        int maxBarHeight = ScaleUtil.scale(SpectrumConstants.maxBarHeight);

        if (processor == null || barNum != processor.getBarCount() || maxBarHeight != processor.getMaxHeight())
            processor = new VisualSpectrumProcessor(barNum, maxBarHeight, 44100);
        double[] heights = processor.process(magnitudes, duration);

        // 将处理后的高度数组同步到 MusicPlayer
        double[] barHeights = player.barHeights;
        double[] barHeightsOrigin = player.barHeightsOrigin;
        double[] barHeightsGap = player.barHeightsGap;
        for (int i = 0; i < barNum; i++) {
            double h = heights[i];
            barHeightsOrigin[i] = h;
            barHeightsGap[i] = Math.abs(h - barHeights[i]);
        }
    }
}
