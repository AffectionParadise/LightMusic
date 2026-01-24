package net.doge.util.ui.quantizer;

import net.doge.entity.core.color.HSL;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.*;

public class MMCQ {
    private static final int MAX_ITERATIONS = 100;
    private static final double RGB_DISTANCE = 0.5;
    private static final int MAX_IMG_WIDTH = 256;

    private static final int COLOR_ALPHA = 0;
    private static final int COLOR_RED = 1;
    private static final int COLOR_GREEN = 2;
    private static final int COLOR_BLUE = 3;

    private int[] pixels;
    private int maxColor;
    private double fraction;
    private int sigBits;
    private int rShift;
    private int width;
    private int height;
    private Map<Integer, Long> histo = new HashMap<>();

    /**
     * @param img      Image data [[A, R, G, B], ...]
     * @param maxColor Between [2, 256]
     */
    public MMCQ(BufferedImage img, int maxColor) {
        this(img, maxColor, 0.85d, 5);
    }

    /**
     * @param img      Image data [[A, R, G, B], ...]
     * @param maxColor Between [2, 256]
     * @param fraction Between [0.3, 0.9]
     * @param sigBits  5 or 6
     */
    public MMCQ(BufferedImage img, int maxColor, double fraction, int sigBits) {
        if (maxColor < 2 || maxColor > 256) throw new IllegalArgumentException("maxColor should between [2, 256]!");
        this.maxColor = maxColor;
        if (fraction < 0.3 || fraction > 0.9) throw new IllegalArgumentException("fraction should between [0.3, 0.9]!");
        this.fraction = fraction;
        if (sigBits < 5 || sigBits > 6) throw new IllegalArgumentException("sigbits should between [5, 6]!");
        this.sigBits = sigBits;
        rShift = 8 - this.sigBits;

        if (img.getWidth() > MAX_IMG_WIDTH) img = ImageUtil.width(img, MAX_IMG_WIDTH);
        width = img.getWidth();
        height = img.getHeight();
        pixels = ImageUtil.toPixels(img, 0, 0, width, height);

        initPixHisto();
    }

    private void initPixHisto() {
        for (int rgb : pixels) {
            int alpha = ColorUtil.alpha(rgb);
            if (alpha < 128) continue;
            int red = ColorUtil.red(rgb) >> rShift;
            int green = ColorUtil.green(rgb) >> rShift;
            int blue = ColorUtil.blue(rgb) >> rShift;
            int colorIndex = getColorIndexWithRgb(red, green, blue);
            long count = histo.getOrDefault(colorIndex, 0L);
            histo.put(colorIndex, count + 1);
        }
    }

    private int getColorIndexWithRgb(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    private VBox createVBox() {
        int rMax = getMax(COLOR_RED) >> rShift;
        int rMin = getMin(COLOR_RED) >> rShift;
        int gMax = getMax(COLOR_GREEN) >> rShift;
        int gMin = getMin(COLOR_GREEN) >> rShift;
        int bMax = getMax(COLOR_BLUE) >> rShift;
        int bMin = getMin(COLOR_BLUE) >> rShift;

        return new VBox(rMin, rMax, gMin, gMax, bMin, bMax, 1 << rShift, histo);
    }

    private int getMax(int which) {
        int max = 0;
        for (int rgb : pixels) {
            int value = getRgbPart(rgb, which);
            if (max < value) max = value;
        }
        return max;
    }

    private int getMin(int which) {
        int min = Integer.MAX_VALUE;
        for (int rgb : pixels) {
            int value = getRgbPart(rgb, which);
            if (min > value) min = value;
        }
        return min;
    }

    private VBox[] medianCutApply(VBox vBox) {
        long nPixs = 0;

        switch (vBox.axis) {
            case COLOR_RED: // Red axis is largest
                for (int r = vBox.r1; r <= vBox.r2; r++) {
                    for (int g = vBox.g1; g <= vBox.g2; g++) {
                        for (int b = vBox.b1; b <= vBox.b2; b++) {
                            long count = vBox.histo.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.pixelNum / 2) {
                        int left = r - vBox.r1;
                        int right = vBox.r2 - r;
                        int r2 = (left >= right) ? Math.max(vBox.r1, r - 1 - left / 2) : Math.min(vBox.r2 - 1, r + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2, vBox.multiple, vBox.histo);
                        VBox vBox2 = new VBox(r2 + 1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2, vBox.multiple, vBox.histo);
                        if (isSimilarRgb(vBox1.getAvgRgb(), vBox2.getAvgRgb())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }

            case COLOR_GREEN: // Green axis is largest
                for (int g = vBox.g1; g <= vBox.g2; g++) {
                    for (int b = vBox.b1; b <= vBox.b2; b++) {
                        for (int r = vBox.r1; r <= vBox.r2; r++) {
                            long count = vBox.histo.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.pixelNum / 2) {
                        int left = g - vBox.g1;
                        int right = vBox.g2 - g;
                        int g2 = (left >= right) ? Math.max(vBox.g1, g - 1 - left / 2) : Math.min(vBox.g2 - 1, g + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, vBox.r2, vBox.g1, g2, vBox.b1, vBox.b2, vBox.multiple, vBox.histo);
                        VBox vBox2 = new VBox(vBox.r1, vBox.r2, g2 + 1, vBox.g2, vBox.b1, vBox.b2, vBox.multiple, vBox.histo);
                        if (isSimilarRgb(vBox1.getAvgRgb(), vBox2.getAvgRgb())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }

            case COLOR_BLUE: // Blue axis is largest
                for (int b = vBox.b1; b <= vBox.b2; b++) {
                    for (int r = vBox.r1; r <= vBox.r2; r++) {
                        for (int g = vBox.g1; g <= vBox.g2; g++) {
                            long count = vBox.histo.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.pixelNum / 2) {
                        int left = b - vBox.b1;
                        int right = vBox.b2 - b;
                        int b2 = (left >= right) ? Math.max(vBox.b1, b - 1 - left / 2) : Math.min(vBox.b2 - 1, b + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, b2, vBox.multiple, vBox.histo);
                        VBox vBox2 = new VBox(vBox.r1, vBox.r2, vBox.g1, vBox.g2, b2 + 1, vBox.b2, vBox.multiple, vBox.histo);
                        if (isSimilarRgb(vBox1.getAvgRgb(), vBox2.getAvgRgb())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }
        }
        return new VBox[]{vBox, null};
    }

    private void iterCut(int maxColor, PriorityQueue<VBox> boxQueue) {
        int nColors = 1;
        int nIters = 0;
        List<VBox> store = new ArrayList<>();
        while (true) {
            if (nColors >= maxColor || boxQueue.isEmpty()) break;
            VBox vBox = boxQueue.poll();
            if (vBox.pixelNum == 0) continue;
            VBox[] vBoxes = medianCutApply(vBox);
            if (vBoxes[0] == vBox || vBoxes[0].pixelNum == vBox.pixelNum) {
                store.add(vBoxes[0]);
                continue;
            }
            boxQueue.offer(vBoxes[0]);
            //if (vBoxes[1] != null) {
            nColors++;
            boxQueue.offer(vBoxes[1]);
            //}
            if (++nIters >= MAX_ITERATIONS) break;
        }
        boxQueue.addAll(store);
    }

    public List<ThemeColor> quantize() {
        if (width * height < maxColor)
            throw new IllegalArgumentException("Image({" + width + "}x{" + height + "}) too small to be quantized");

        VBox oriVBox = createVBox();
        PriorityQueue<VBox> pOneQueue = new PriorityQueue<>(maxColor);
        pOneQueue.offer(oriVBox);
        int popColors = (int) (maxColor * fraction);
        iterCut(popColors, pOneQueue);

        PriorityQueue<VBox> boxQueue = new PriorityQueue<>(maxColor, (vBox1, vBox2) -> Long.compare(vBox2.pixelNum * vBox2.volume, vBox1.pixelNum * vBox1.volume));

        boxQueue.addAll(pOneQueue);
        pOneQueue.clear();

        iterCut(maxColor - popColors + 1, boxQueue);

        pOneQueue.addAll(boxQueue);
        boxQueue.clear();

        PriorityQueue<ThemeColor> themeColors = new PriorityQueue<>(maxColor);

        while (!pOneQueue.isEmpty()) {
            VBox vBox = pOneQueue.poll();
            double proportion = (double) vBox.pixelNum / oriVBox.pixelNum;
            if (proportion < 0.01) continue;
            ThemeColor themeColor = new ThemeColor(vBox.getAvgRgb(), proportion);
            themeColors.offer(themeColor);
        }

        return new ArrayList<>(themeColors);
    }

    private int getRgbPart(int rgb, int which) {
        switch (which) {
            case COLOR_ALPHA:
                return ColorUtil.alpha(rgb);
            case COLOR_RED:
                return ColorUtil.red(rgb);
            case COLOR_GREEN:
                return ColorUtil.green(rgb);
            case COLOR_BLUE:
                return ColorUtil.blue(rgb);
            default:
                throw new IllegalArgumentException("parameter which must be COLOR_ALPHA/COLOR_RED/COLOR_GREEN/COLOR_BLUE !");
        }
    }

    private boolean isSimilarRgb(int rgb1, int rgb2) {
        return ColorUtil.distance(rgb1, rgb2) < RGB_DISTANCE;
    }

    /**
     * The color space is divided up into a set of 3D rectangular regions (called `vboxes`)
     */
    private class VBox implements Comparable<VBox> {
        private final int r1;
        private final int r2;
        private final int g1;
        private final int g2;
        private final int b1;
        private final int b2;
        private final Map<Integer, Long> histo;
        private final long pixelNum;
        private final long volume;
        private final int axis;
        private final int multiple;
        private int avgRgb = -1;

        private VBox(int r1, int r2, int g1, int g2, int b1, int b2, int multiple, Map<Integer, Long> histo) {
            this.r1 = r1;
            this.r2 = r2;
            this.g1 = g1;
            this.g2 = g2;
            this.b1 = b1;
            this.b2 = b2;
            this.multiple = multiple;
            this.histo = histo;
            pixelNum = population();
            final int rl = Math.abs(r2 - r1) + 1, gl = Math.abs(g2 - g1) + 1, bl = Math.abs(b2 - b1) + 1;
            volume = rl * gl * bl;
            final int max = Math.max(Math.max(rl, gl), bl);
            if (max == rl) axis = COLOR_RED;
            else if (max == gl) axis = COLOR_GREEN;
            else axis = COLOR_BLUE;
        }

        private long population() {
            long sum = 0;
            for (int r = r1; r <= r2; r++) {
                for (int g = g1; g <= g2; g++) {
                    for (int b = b1; b <= b2; b++) {
                        long count = histo.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                        sum += count;
                    }
                }
            }
            return sum;
        }

        private int getAvgRgb() {
            if (avgRgb == -1) {
                long total = 0, rSum = 0, gSum = 0, bSum = 0;

                for (int r = r1; r <= r2; r++) {
                    for (int g = g1; g <= g2; g++) {
                        for (int b = b1; b <= b2; b++) {
                            long count = histo.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            if (count == 0) continue;
                            total += count;
                            rSum += count * (r + 0.5) * multiple;
                            gSum += count * (g + 0.5) * multiple;
                            bSum += count * (b + 0.5) * multiple;
                        }
                    }
                }

                int r, g, b;
                if (total == 0) {
                    r = (r1 + r2 + 1) * multiple / 2;
                    g = (g1 + g2 + 1) * multiple / 2;
                    b = (b2 + b2 + 1) * multiple / 2;
                } else {
                    r = (int) (rSum / total);
                    g = (int) (gSum / total);
                    b = (int) (bSum / total);
                }
                avgRgb = ColorUtil.merge(r, g, b);
            }

            return avgRgb;
        }

        @Override
        public int compareTo(VBox o) {
            return Long.compare(o.pixelNum, pixelNum);
        }
    }

    public class ThemeColor implements Comparable<ThemeColor> {
//        private static final float MIN_CONTRAST_TITLE_TEXT = 3.0f;
//        private static final float MIN_CONTRAST_BODY_TEXT = 4.5f;

        private final int rgb;
        private final double proportion;
        private final double priority;

//        private boolean textRgbGenerated;
//        private int titleRgb;
//        private int bodyRgb;

        private ThemeColor(int rgb, double proportion) {
            this.rgb = rgb;
            this.proportion = proportion;
            // (...) / 3d * (3 / 2d)
            double distance = ColorUtil.hslDistance(ColorUtil.rgbValToHsl(rgb), new HSL(180, 50, 50));
            priority = proportion * (1 - distance);
        }

        @Override
        public int compareTo(ThemeColor themeColor) {
            return Double.compare(themeColor.priority, priority);
        }

        public int getRgb() {
            return rgb;
        }

        public double getProportion() {
            return proportion;
        }

//        public int getBodyRgb() {
//            ensureTextColorsGenerated();
//            return bodyRgb;
//        }
//
//        public int getTitleRgb() {
//            ensureTextColorsGenerated();
//            return titleRgb;
//        }

//        private void ensureTextColorsGenerated() {
//            if (!textRgbGenerated) {
//                // First check white, as most colors will be dark
//                final int lightBodyAlpha = ColorUtil.calculateMinimumAlpha(Colors.WHITE.getRGB(), rgb, MIN_CONTRAST_BODY_TEXT);
//                final int lightTitleAlpha = ColorUtil.calculateMinimumAlpha(Colors.WHITE.getRGB(), rgb, MIN_CONTRAST_TITLE_TEXT);
//
//                if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
//                    // If we found valid light values, use them and return
//                    bodyRgb = ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightBodyAlpha);
//                    titleRgb = ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightTitleAlpha);
//                    textRgbGenerated = true;
//                    return;
//                }
//
//                final int darkBodyAlpha = ColorUtil.calculateMinimumAlpha(Colors.BLACK.getRGB(), rgb, MIN_CONTRAST_BODY_TEXT);
//                final int darkTitleAlpha = ColorUtil.calculateMinimumAlpha(Colors.BLACK.getRGB(), rgb, MIN_CONTRAST_TITLE_TEXT);
//
//                if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
//                    // If we found valid dark values, use them and return
//                    bodyRgb = ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkBodyAlpha);
//                    titleRgb = ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkTitleAlpha);
//                    textRgbGenerated = true;
//                    return;
//                }
//
//                // If we reach here then we can not find title and body values which use the same
//                // lightness, we need to use mismatched values
//                bodyRgb = lightBodyAlpha != -1 ? ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightBodyAlpha)
//                        : ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkBodyAlpha);
//                titleRgb = lightTitleAlpha != -1 ? ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightTitleAlpha)
//                        : ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkTitleAlpha);
//                textRgbGenerated = true;
//            }
//        }
    }
}
