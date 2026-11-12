package net.doge.util.ui.helper;

import net.doge.util.ui.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PaletteHelper {
    private static final int COLOR_COUNT = 6;

    private static List<Candidate> extractPaletteAsCandidate(BufferedImage img) {
        int sw = Math.min(64, img.getWidth());
        int sh = Math.min(64, img.getHeight());
        List<double[]> samples = new ArrayList<>();
        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                int px = (int) ((x + 0.5) * img.getWidth() / sw);
                int py = (int) ((y + 0.5) * img.getHeight() / sh);
                int argb = img.getRGB(px, py);
                int alpha = (argb >>> 24) & 255;
                if (alpha < 24) {
                    continue;
                }
                int r = (argb >> 16) & 255, g = (argb >> 8) & 255, b = argb & 255;
                double chroma = (Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b))) / 255.0;
                samples.add(new double[]{r, g, b, alpha / 255.0 * (0.35 + chroma * 1.65)});
            }
        }
        if (samples.isEmpty()) {
            return Collections.singletonList(new Candidate(new double[]{42, 45, 55, 1}));
        }
        // 确定性的最远点初始化可避免重新加载时出现随机闪烁
        List<double[]> centers = new ArrayList<>();
        double[] first = samples.get(0);
        for (double[] sample : samples) {
            if (sample[3] > first[3]) first = sample;
        }
        centers.add(first.clone());
        while (centers.size() < COLOR_COUNT) {
            double best = 0;
            double[] candidate = null;
            for (double[] sample : samples) {
                double distance = Double.MAX_VALUE;
                for (double[] center : centers) {
                    distance = Math.min(distance, colorDistance(sample, center));
                }
                double score = distance * sample[3];
                if (score > best) {
                    best = score;
                    candidate = sample;
                }
            }
            if (candidate == null || best < 180) break;
            centers.add(candidate.clone());
        }
        for (int iteration = 0; iteration < 10; iteration++) {
            int size = centers.size();
            double[][] sums = new double[size][4];
            for (double[] sample : samples) {
                int nearest = 0;
                double distance = Double.MAX_VALUE;
                for (int i = 0; i < size; i++) {
                    double d = colorDistance(sample, centers.get(i));
                    if (d < distance) {
                        distance = d;
                        nearest = i;
                    }
                }
                double[] sum = sums[nearest];
                for (int channel = 0; channel < 3; channel++) {
                    sum[channel] += sample[channel] * sample[3];
                }
                sum[3] += sample[3];
            }
            for (int i = 0; i < size; i++) {
                if (sums[i][3] > 0) {
                    double[] center = centers.get(i);
                    for (int channel = 0; channel < 3; channel++) {
                        center[channel] = sums[i][channel] / sums[i][3];
                    }
                    center[3] = sums[i][3];
                } else {
                    centers.get(i)[3] = 0;
                }
            }
        }
        centers.sort((a, b) -> Double.compare(b[3], a[3]));
        List<Candidate> result = new ArrayList<>();
        for (double[] center : centers) {
            if (center[3] > 0) result.add(new Candidate(center));
        }
        return result;
    }

    // 提取主色调 Palette
    public static List<Color> extractPalette(BufferedImage img) {
        List<Candidate> candidates = extractPaletteAsCandidate(img);
        List<Color> palette = new ArrayList<>();
        for (Candidate candidate : candidates) palette.add(candidate.color);
        return palette;
    }

    // 选取一对视觉美观的颜色
    public static List<Color> extractPairVisualAppeal(BufferedImage img) {
        List<Candidate> candidates = extractPaletteAsCandidate(img);
        if (candidates.size() == 1) {
            Color color = candidates.get(0).color;
            return makeColorListWithPriority(color, color);
        }
        double total = 0;
        for (Candidate candidate : candidates) {
            total += candidate.weight;
        }
        List<Candidate> dominant = new ArrayList<>();
        boolean hasColor = false;
        for (Candidate candidate : candidates) {
            // 微小的点缀色不应取代真正的主色调
            if (candidate.weight / total >= 0.02) {
                dominant.add(candidate);
                hasColor |= candidate.hsb[1] >= 0.15f;
            }
        }
        // 在最多 8 个聚类的情况下，至少有一个聚类必然覆盖 2%
        if (dominant.size() == 1) {
            return makeColorListWithPriority(dominant.get(0).color, dominant.get(0).color);
        }
        Candidate first = dominant.get(0), second = dominant.get(1);
        double bestScore = -1;
        for (int i = 0; i < dominant.size(); i++) {
            for (int j = i + 1; j < dominant.size(); j++) {
                Candidate a = dominant.get(i), b = dominant.get(j);
                double areaA = a.weight / total, areaB = b.weight / total;
                double coverage = (areaA + areaB) / 2 + Math.sqrt(areaA * areaB);
                double separation = Math.sqrt(colorDistance(a.rgb, b.rgb) / (9 * 255.0 * 255.0));
                double distinctness = 0.2 + 0.8 * Math.min(1, separation / 0.32);
                double hueDistance = Math.abs(a.hsb[0] - b.hsb[0]);
                hueDistance = Math.min(hueDistance, 1 - hueDistance);
                // Gently favor neighboring hues; neutrals can accompany any hue.
                double harmony = Math.min(a.hsb[1], b.hsb[1]) < 0.12 ? 1
                        : 0.65 + 0.35 * Math.max(0, 1 - Math.abs(hueDistance - 0.14) / 0.36);
                double quality = Math.sqrt(colorQuality(a, hasColor) * colorQuality(b, hasColor));
                double score = coverage * distinctness * harmony * quality;
                if (score > bestScore) {
                    bestScore = score;
                    first = a;
                    second = b;
                }
            }
        }
        return makeColorListWithPriority(first.color, second.color);
    }

    private static List<Color> makeColorListWithPriority(Color first, Color second) {
        return Collections.unmodifiableList(ColorUtil.luminance(first) <= ColorUtil.luminance(second)
                ? Arrays.asList(first, second) : Arrays.asList(second, first));
    }

    private static double colorQuality(Candidate candidate, boolean hasColor) {
        double saturation = Math.max(0, 1 - Math.abs(candidate.hsb[1] - 0.60) / 0.75);
        double lightness = Math.max(0, 1 - Math.abs(ColorUtil.luminance(candidate.color) - 0.50) / 0.50);
        double quality = 0.2 + 0.4 * saturation + 0.4 * lightness;
        // De-emphasize white borders and black text when substantial color is present.
        return hasColor && candidate.hsb[1] < 0.08f ? quality * 0.55 : quality;
    }

    private static class Candidate {
        public double[] rgb;
        public Color color;
        public float[] hsb;
        public double weight;

        Candidate(double[] center) {
            rgb = center;
            color = new Color((int) Math.round(center[0]), (int) Math.round(center[1]),
                    (int) Math.round(center[2]));
            hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            weight = center[3];
        }
    }

    private static double colorDistance(double[] a, double[] b) {
        double r = a[0] - b[0], g = a[1] - b[1], bl = a[2] - b[2];
        return 2 * r * r + 4 * g * g + 3 * bl * bl;
    }
}
