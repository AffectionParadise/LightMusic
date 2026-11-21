package net.doge.entity.core.player.processor;

import lombok.Getter;
import net.doge.util.core.math.MathUtil;

import java.util.Arrays;

/**
 * 面向 JavaFX MediaPlayer 的视觉优先频谱处理器。
 * 输入：来自 AudioSpectrumListener 的 magnitudes[]，值以 dB 为单位，范围 [-80, 0]。
 * 输出：条形高度 [0, maxHeight]，默认 32 个条形。
 * <p>
 * 对同一个播放器按顺序调用 process(...)；切换歌曲/跳转时重置。
 * 使用频率补偿、缓慢的逐频段电平调整、谨慎的全局 AGC、
 * 瞬态脉冲、软拐点限幅器以及启动/释放平滑。
 */
public class VisualSpectrumProcessor {
    private static final double MIN_DB = -80.0;
    private static final double MEL_MIN_HZ = 55.0;
    private static final double MEL_MAX_HZ = 10000.0;
    private static final double ATTACK_SECONDS = 0.045;
    private static final double RELEASE_SECONDS = 0.200;
    private static final double LONG_TERM_SECONDS = 6.0;
    private static final double SOFT_CEILING = 0.996;
    private static final double SOFT_DRIVE = 2.90;
    private static final double SOFT_CONTRAST = 2.00;

    @Getter
    private final int barCount;
    @Getter
    private final double maxHeight;
    private final double sampleRate;
    private final double[] smoothed;
    private final double[] longTerm;
    private final double[] previousBands;

    private int inputBandCount = -1;
    private int[][] filterIndices;
    private double[][] filterWeights;
    private double[] inputLevels;

    private double globalGain = 1.0;
    // 缓慢的响度锚点：限制对安静片段的自动提升
    private double peakReference = 0.72;
    private double visualGain = 1.0;
    // 视觉强度
    private double intensity = 0.65;
    private double lastBass = 0.0;
    private double avgBassRise = 0.03;
    private double beatPulse = 0.0;
    private double beatCooldown = 10.0;
    private long lastTimestamp = 0;

    public VisualSpectrumProcessor(int barCount, double maxHeight, double sampleRate) {
        if (barCount < 2 || maxHeight <= 0 || sampleRate <= 0) {
            throw new IllegalArgumentException("Invalid spectrum settings");
        }
        this.barCount = barCount;
        this.maxHeight = maxHeight;
        this.sampleRate = sampleRate;
        this.smoothed = new double[barCount];
        this.longTerm = new double[barCount];
        this.previousBands = new double[barCount];
        Arrays.fill(longTerm, 0.28);
    }

    public VisualSpectrumProcessor() {
        this(32, 120, 44100);
    }

    /**
     * 推荐：传入频谱回调的持续时间（以秒为单位）
     * 对于 0.04 秒的频谱间隔，duration 通常接近 0.04
     */
    public synchronized double[] process(float[] magnitudes, double durationSeconds) {
        if (magnitudes == null || magnitudes.length == 0) {
            return new double[barCount];
        }
        final double dt = Double.isFinite(durationSeconds) && durationSeconds > 0
                ? Math.max(0.005, Math.min(0.100, durationSeconds)) : 0.04;

        int len = magnitudes.length;
        ensureFilters(len);
        for (int i = 0; i < len; i++) {
            // 保留 dB 余量：不要在 -15dB 处进行硬削波
            double level = MathUtil.clamp((magnitudes[i] - MIN_DB) / -MIN_DB, 0, 1);
            inputLevels[i] = Math.pow(level, 1.12);
        }

        // 将线性频率 bin 聚合为 Mel 刻度条形
        double[] band = new double[barCount];
        for (int b = 0; b < barCount; b++) {
            double energy = 0;
            for (int j = 0, l = filterIndices[b].length; j < l; j++) {
                energy += inputLevels[filterIndices[b][j]] * filterWeights[b][j];
            }
            band[b] = energy;
        }

        // 基于第 75% 的全局 AGC，有意放慢其上升速度
        double[] sorted = band.clone();
        Arrays.sort(sorted);
        double rank = (barCount - 1) * 0.75;
        int lo = (int) Math.floor(rank);
        int hi = Math.min(barCount - 1, lo + 1);
        double ref = sorted[lo] + (sorted[hi] - sorted[lo]) * (rank - lo);
        double wantedGain = MathUtil.clamp(0.50 / Math.max(0.22, ref), 0.76, 1.16);
        if (ref < 0.19) wantedGain = Math.min(1.0, wantedGain);
        double gainTau = wantedGain < globalGain ? 0.35 : 2.60;
        globalGain += (wantedGain - globalGain) * response(dt, gainTau);

        // 可选：细微的低频起音脉冲
        int bassBars = Math.min(5, barCount);
        double bass = 0;
        for (int i = 0; i < bassBars; i++) bass += band[i];
        bass /= bassBars;
        double rise = Math.max(0, bass - lastBass);
        avgBassRise = avgBassRise * 0.91 + rise * 0.09;
        beatCooldown += dt;
        if (bass > 0.30 && rise > Math.max(0.065, avgBassRise * 1.9)
                && beatCooldown > 0.18) {
            beatPulse = 1.0;
            beatCooldown = 0;
        }
        lastBass = bass;
        beatPulse *= Math.exp(-dt / 0.13);

        // 长期逐频段电平调整 + 静态高频补偿
        // 在选定共同的峰值增益之前，先计算每个频段的原始视觉信号
        double averageAlpha = response(dt, LONG_TERM_SECONDS);
        double[] signal = new double[barCount];
        double[] transients = new double[barCount];
        for (int b = 0; b < barCount; b++) {
            longTerm[b] += (band[b] - longTerm[b]) * averageAlpha;

            double position = b / (double) (barCount - 1);
            double tiltGain = 0.90 + 1.05 * Math.pow(position, 0.72);
            double leveling = MathUtil.clamp(
                    Math.sqrt(0.34 / Math.max(0.20, longTerm[b])),
                    0.85, 1.42);
            double compensated = band[b] * tiltGain * leveling * globalGain;

            double positiveFlux = Math.max(0, band[b] - previousBands[b]);
            previousBands[b] = band[b];
            transients[b] = positiveFlux;
            signal[b] = Math.max(0, compensated - 0.018)
                    * (1.0 + beatPulse * 0.065)
                    + positiveFlux * 0.15;
        }

        // 在数秒尺度上跟踪上频段能量，而不是每一帧的瞬时最大值。这样可以在歌曲级别上提供余量，而不会强迫安静帧填满整个图表
        double[] sortedSignal = signal.clone();
        Arrays.sort(sortedSignal);
        double p = (barCount - 1) * 0.95;
        int p0 = (int) Math.floor(p);
        int p1 = Math.min(barCount - 1, p0 + 1);
        double upperEnergy = sortedSignal[p0]
                + (sortedSignal[p1] - sortedSignal[p0]) * (p - p0);

        double anchorTau = upperEnergy > peakReference ? 0.45 : 3.5;
        peakReference += (upperEnergy - peakReference) * response(dt, anchorTau);
        double wantedVisualGain = MathUtil.clamp(
                0.79 / Math.max(0.52, peakReference), 0.95, 1.28);
        double visualGainTau = wantedVisualGain < visualGain ? 0.6 : 1.8;
        visualGain += (wantedVisualGain - visualGain)
                * response(dt, visualGainTau);

        // 保持对比度的软饱和
        // 旧映射：0.90 * tanh(1.30 * signal)  -> 条形会永久偏短
        // 该曲线可以接近可用高度的 99.6%，它在满量程处绝不会硬削波；非常高的条形仍会有轻微运动
        double[] heights = new double[barCount];
        for (int b = 0; b < barCount; b++) {
            double driven = SOFT_DRIVE * signal[b] * visualGain * intensity;
            double target = SOFT_CEILING * Math.pow(
                    Math.tanh(driven), SOFT_CONTRAST);

            // 将短瞬态提升至接近上限，不要抬高安静的频段
            double transientStrength = MathUtil.clamp(
                    (transients[b] - 0.04) / 0.21, 0, 1);
            target += (SOFT_CEILING - target) * 0.33 * transientStrength;

            double tau = target > smoothed[b] ? ATTACK_SECONDS : RELEASE_SECONDS;
            smoothed[b] += (target - smoothed[b]) * response(dt, tau);
            heights[b] = maxHeight * smoothed[b];
        }
        return heights;
    }

    /**
     * 当回调持续时间不可用时的替代方案
     */
    public synchronized double[] process(float[] magnitudes) {
        long now = System.nanoTime();
        double dt = lastTimestamp == 0 ? 0.04 : (now - lastTimestamp) / 1e9;
        lastTimestamp = now;
        return process(magnitudes, dt);
    }

    /**
     * 在跳转或切换曲目后重置，以防止状态残留
     */
    public synchronized void reset() {
        Arrays.fill(smoothed, 0);
        Arrays.fill(longTerm, 0.28);
        Arrays.fill(previousBands, 0);
        globalGain = 1.0;
        peakReference = 0.72;
        visualGain = 1.0;
        lastBass = 0;
        avgBassRise = 0.03;
        beatPulse = 0;
        beatCooldown = 10;
        lastTimestamp = 0;
    }

    private void ensureFilters(int count) {
        if (count == inputBandCount) return;
        inputBandCount = count;
        inputLevels = new double[count];
        filterIndices = new int[barCount][];
        filterWeights = new double[barCount][];

        double nyquist = sampleRate / 2.0;
        double maxHz = Math.min(MEL_MAX_HZ, nyquist * 0.95);
        double minHz = Math.min(MEL_MIN_HZ, maxHz * 0.25);
        double melMin = hzToMel(minHz);
        double melMax = hzToMel(maxHz);
        double binWidth = nyquist / count;

        for (int b = 0; b < barCount; b++) {
            double left = melToHz(melMin + (melMax - melMin) * b / barCount);
            double right = melToHz(melMin + (melMax - melMin) * (b + 1) / barCount);
            int first = Math.max(0, (int) Math.floor(left / binWidth));
            int last = Math.min(count - 1, (int) Math.ceil(right / binWidth) - 1);
            int length = Math.max(1, last - first + 1);
            filterIndices[b] = new int[length];
            filterWeights[b] = new double[length];

            double sum = 0;
            for (int j = 0; j < length; j++) {
                int i = first + j;
                double l = i * binWidth;
                double r = (i + 1) * binWidth;
                double overlap = Math.max(0, Math.min(right, r) - Math.max(left, l));
                filterIndices[b][j] = i;
                filterWeights[b][j] = overlap;
                sum += overlap;
            }
            if (sum <= 0) {
                // 极短的输入频谱：映射到最近的源频点
                int nearest = (int) MathUtil.clamp(Math.round((left + right) * 0.5 / binWidth), 0, count - 1);
                filterIndices[b] = new int[]{nearest};
                filterWeights[b] = new double[]{1};
            } else {
                for (int j = 0; j < length; j++) filterWeights[b][j] /= sum;
            }
        }
    }

    private static double response(double dt, double tau) {
        return 1.0 - Math.exp(-dt / tau);
    }

    private static double hzToMel(double hz) {
        return 2595.0 * Math.log10(1.0 + hz / 700.0);
    }

    private static double melToHz(double mel) {
        return 700.0 * (Math.pow(10.0, mel / 2595.0) - 1.0);
    }
}
