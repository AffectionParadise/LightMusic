package net.doge.util.ui;

import net.doge.constant.ui.Colors;

import java.awt.image.BufferedImage;
import java.util.*;

public class MMCQ {
    private static final int MAX_ITERATIONS = 100;
    private static final float SCALE = 0.1f;

    private static final int COLOR_ALPHA = 0;
    private static final int COLOR_RED = 1;
    private static final int COLOR_GREEN = 2;
    private static final int COLOR_BLUE = 3;

    private int[] mPixelRGB;
    private int mMaxColor;
    private double mFraction;
    private int mSigbits;
    private int mRshift;
    private int mWidth;
    private int mHeight;
    private Map<Integer, Long> mPixHisto = new HashMap<>();


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
     * @param sigbits  5 or 6
     */
    public MMCQ(BufferedImage img, int maxColor, double fraction, int sigbits) {
        if (maxColor < 2 || maxColor > 256) throw new IllegalArgumentException("maxColor should between [2, 256]!");
        mMaxColor = maxColor;
        if (fraction < 0.3 || fraction > 0.9) throw new IllegalArgumentException("fraction should between [0.3, 0.9]!");
        mFraction = fraction;
        if (sigbits < 5 || sigbits > 6) throw new IllegalArgumentException("sigbits should between [5, 6]!");
        mSigbits = sigbits;
        mRshift = 8 - mSigbits;

        img = ImageUtil.scale(img, SCALE);
        mWidth = img.getWidth();
        mHeight = img.getHeight();
        mPixelRGB = new int[mWidth * mHeight];
        getPixels(img);

        initPixHisto();
    }

    private void getPixels(BufferedImage img) {
        int index = 0;
        for (int i = 0; i < mWidth; i++)
            for (int j = 0; j < mHeight; j++)
                mPixelRGB[index++] = img.getRGB(i, j);
    }

    private void initPixHisto() {
        for (int color : mPixelRGB) {
            int alpha = ColorUtil.alpha(color);
            if (alpha < 128) continue;
            int red = ColorUtil.red(color) >> mRshift;
            int green = ColorUtil.green(color) >> mRshift;
            int blue = ColorUtil.blue(color) >> mRshift;
            int colorIndex = getColorIndexWithRgb(red, green, blue);
            long count = mPixHisto.getOrDefault(colorIndex, 0L);
            mPixHisto.put(colorIndex, count + 1);
        }
    }

    public static int getColorIndexWithRgb(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    private VBox createVBox() {
        int rMax = getMax(COLOR_RED) >> mRshift;
        int rMin = getMin(COLOR_RED) >> mRshift;
        int gMax = getMax(COLOR_GREEN) >> mRshift;
        int gMin = getMin(COLOR_GREEN) >> mRshift;
        int bMax = getMax(COLOR_BLUE) >> mRshift;
        int bMin = getMin(COLOR_BLUE) >> mRshift;

        return new VBox(rMin, rMax, gMin, gMax, bMin, bMax, 1 << mRshift, mPixHisto);
    }

    private int getMax(int which) {
        int max = 0;
        for (int color : mPixelRGB) {
            int value = getColorPart(color, which);
            if (max < value) max = value;
        }
        return max;
    }

    private int getMin(int which) {
        int min = Integer.MAX_VALUE;
        for (int color : mPixelRGB) {
            int value = getColorPart(color, which);
            if (min > value) min = value;
        }
        return min;
    }

    private static VBox[] medianCutApply(VBox vBox) {
        long nPixs = 0;

        switch (vBox.mAxis) {
            case COLOR_RED: // Red axis is largest
                for (int r = vBox.r1; r <= vBox.r2; r++) {
                    for (int g = vBox.g1; g <= vBox.g2; g++) {
                        for (int b = vBox.b1; b <= vBox.b2; b++) {
                            long count = vBox.mHisto.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.mNumPixs / 2) {
                        int left = r - vBox.r1;
                        int right = vBox.r2 - r;
                        int r2 = (left >= right) ? Math.max(vBox.r1, r - 1 - left / 2) : Math.min(vBox.r2 - 1, r + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2, vBox.mMultiple, vBox.mHisto);
                        VBox vBox2 = new VBox(r2 + 1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2, vBox.mMultiple, vBox.mHisto);
                        if (isSimilarColor(vBox1.getAvgColor(), vBox2.getAvgColor())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }

            case COLOR_GREEN: // Green axis is largest
                for (int g = vBox.g1; g <= vBox.g2; g++) {
                    for (int b = vBox.b1; b <= vBox.b2; b++) {
                        for (int r = vBox.r1; r <= vBox.r2; r++) {
                            long count = vBox.mHisto.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.mNumPixs / 2) {
                        int left = g - vBox.g1;
                        int right = vBox.g2 - g;
                        int g2 = (left >= right) ? Math.max(vBox.g1, g - 1 - left / 2) : Math.min(vBox.g2 - 1, g + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, vBox.r2, vBox.g1, g2, vBox.b1, vBox.b2, vBox.mMultiple, vBox.mHisto);
                        VBox vBox2 = new VBox(vBox.r1, vBox.r2, g2 + 1, vBox.g2, vBox.b1, vBox.b2, vBox.mMultiple, vBox.mHisto);
                        if (isSimilarColor(vBox1.getAvgColor(), vBox2.getAvgColor())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }

            case COLOR_BLUE: // Blue axis is largest
                for (int b = vBox.b1; b <= vBox.b2; b++) {
                    for (int r = vBox.r1; r <= vBox.r2; r++) {
                        for (int g = vBox.g1; g <= vBox.g2; g++) {
                            long count = vBox.mHisto.getOrDefault(getColorIndexWithRgb(r, g, b), 0L);
                            nPixs += count;
                        }
                    }
                    if (nPixs >= vBox.mNumPixs / 2) {
                        int left = b - vBox.b1;
                        int right = vBox.b2 - b;
                        int b2 = (left >= right) ? Math.max(vBox.b1, b - 1 - left / 2) : Math.min(vBox.b2 - 1, b + right / 2);
                        VBox vBox1 = new VBox(vBox.r1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, b2, vBox.mMultiple, vBox.mHisto);
                        VBox vBox2 = new VBox(vBox.r1, vBox.r2, vBox.g1, vBox.g2, b2 + 1, vBox.b2, vBox.mMultiple, vBox.mHisto);
                        if (isSimilarColor(vBox1.getAvgColor(), vBox2.getAvgColor())) break;
                        else return new VBox[]{vBox1, vBox2};
                    }
                }
        }
        return new VBox[]{vBox, null};
    }

    private static void iterCut(int maxColor, PriorityQueue<VBox> boxQueue) {
        int nColors = 1;
        int nIters = 0;
        List<VBox> store = new ArrayList<>();
        while (true) {
            if (nColors >= maxColor || boxQueue.isEmpty()) break;
            VBox vBox = boxQueue.poll();
            if (vBox.mNumPixs == 0) continue;
            VBox[] vBoxes = medianCutApply(vBox);
            if (vBoxes[0] == vBox || vBoxes[0].mNumPixs == vBox.mNumPixs) {
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
        if (mWidth * mHeight < mMaxColor)
            throw new IllegalArgumentException("Image({" + mWidth + "}x{" + mHeight + "}) too small to be quantized");

        VBox oriVBox = createVBox();
        PriorityQueue<VBox> pOneQueue = new PriorityQueue<>(mMaxColor);
        pOneQueue.offer(oriVBox);
        int popColors = (int) (mMaxColor * mFraction);
        iterCut(popColors, pOneQueue);

        PriorityQueue<VBox> boxQueue = new PriorityQueue<>(mMaxColor, (o1, o2) -> {
            long priority1 = o1.getPriority() * o1.mVolume;
            long priority2 = o2.getPriority() * o2.mVolume;
            return Long.compare(priority1, priority2);
        });

        boxQueue.addAll(pOneQueue);
        pOneQueue.clear();

        iterCut(mMaxColor - popColors + 1, boxQueue);

        pOneQueue.addAll(boxQueue);
        boxQueue.clear();

        PriorityQueue<ThemeColor> themeColors = new PriorityQueue<>(mMaxColor);

        while (!pOneQueue.isEmpty()) {
            VBox vBox = pOneQueue.poll();
            double proportion = (double) vBox.mNumPixs / oriVBox.mNumPixs;
            if (proportion < 0.05) continue;
            ThemeColor themeColor = new ThemeColor(vBox.getAvgColor(), proportion);
            themeColors.offer(themeColor);
        }

        return new ArrayList<>(themeColors);
    }

    public static int getColorPart(int color, int which) {
        switch (which) {
            case COLOR_ALPHA:
                return ColorUtil.alpha(color);
            case COLOR_RED:
                return ColorUtil.red(color);
            case COLOR_GREEN:
                return ColorUtil.green(color);
            case COLOR_BLUE:
                return ColorUtil.blue(color);
            default:
                throw new IllegalArgumentException("parameter which must be COLOR_ALPHA/COLOR_RED/COLOR_GREEN/COLOR_BLUE !");
        }
    }

    private static final double COLOR_TOLERANCE = 0.5;

    public static boolean isSimilarColor(int color1, int color2) {
        return colorDistance(color1, color2) < COLOR_TOLERANCE;
    }

    public static double colorDistance(int color1, int color2) {
        int r1 = ColorUtil.red(color1), g1 = ColorUtil.green(color1), b1 = ColorUtil.blue(color1);
        int r2 = ColorUtil.red(color2), g2 = ColorUtil.green(color2), b2 = ColorUtil.blue(color2);
        double rd = (r1 - r2) / 255d, gd = (g1 - g2) / 255d, bd = (b1 - b2) / 255d;
        return Math.sqrt(rd * rd + gd * gd + bd * bd);
    }

//    public static double distanceToBGW(int color) {
//        int r = ColorUtil.red(color);
//        int g = ColorUtil.green(color);
//        int b = ColorUtil.blue(color);
//        double rg = (r - g) / 255d;
//        double gb = (g - b) / 255d;
//        double br = (b - r) / 255d;
//        return Math.sqrt((rg * rg + gb * gb + br * br) / 3d);
//    }

    /**
     * The color space is divided up into a set of 3D rectangular regions (called `vboxes`)
     */
    private static class VBox implements Comparable<VBox> {
        final int r1;
        final int r2;
        final int g1;
        final int g2;
        final int b1;
        final int b2;
        final Map<Integer, Long> mHisto;
        final long mNumPixs;
        final long mVolume;
        final int mAxis;
        final int mMultiple;
        private int mAvgColor = -1;

        VBox(int r1, int r2, int g1, int g2, int b1, int b2, int multiple, Map<Integer, Long> histo) {
            this.r1 = r1;
            this.r2 = r2;
            this.g1 = g1;
            this.g2 = g2;
            this.b1 = b1;
            this.b2 = b2;
            mMultiple = multiple;
            mHisto = histo;
            mNumPixs = population();
            final int rl = Math.abs(r2 - r1) + 1;
            final int gl = Math.abs(g2 - g1) + 1;
            final int bl = Math.abs(b2 - b1) + 1;
            mVolume = rl * gl * bl;
            final int max = Math.max(Math.max(rl, gl), bl);
            if (max == rl) {
                mAxis = COLOR_RED;
            } else if (max == gl) {
                mAxis = COLOR_GREEN;
            } else {
                mAxis = COLOR_BLUE;
            }
        }

        private long population() {
            long sum = 0;
            for (int r = r1; r <= r2; r++) {
                for (int g = g1; g <= g2; g++) {
                    for (int b = b1; b <= b2; b++) {
                        long count = mHisto.getOrDefault(MMCQ.getColorIndexWithRgb(r, g, b), 0L);
                        sum += count;
                    }
                }
            }
            return sum;
        }

        public int getAvgColor() {
            if (mAvgColor == -1) {
                long total = 0;
                long rSum = 0;
                long gSum = 0;
                long bSum = 0;

                for (int r = r1; r <= r2; r++) {
                    for (int g = g1; g <= g2; g++) {
                        for (int b = b1; b <= b2; b++) {
                            long count = mHisto.getOrDefault(MMCQ.getColorIndexWithRgb(r, g, b), 0L);
                            if (count == 0) continue;
                            total += count;
                            rSum += count * (r + 0.5) * mMultiple;
                            gSum += count * (g + 0.5) * mMultiple;
                            bSum += count * (b + 0.5) * mMultiple;
                        }
                    }
                }

                int r, g, b;
                if (total == 0) {
                    r = (r1 + r2 + 1) * mMultiple / 2;
                    g = (g1 + g2 + 1) * mMultiple / 2;
                    b = (b2 + b2 + 1) * mMultiple / 2;
                } else {
                    r = (int) (rSum / total);
                    g = (int) (gSum / total);
                    b = (int) (bSum / total);
                }
                mAvgColor = ColorUtil.merge(r, g, b);
            }

            return mAvgColor;
        }

        public long getPriority() {
            return -mNumPixs;
        }

        @Override
        public int compareTo(VBox o) {
            long priority = getPriority();
            long oPriority = o.getPriority();
            return Long.compare(priority, oPriority);
        }
    }

    public static class ThemeColor implements Comparable<ThemeColor> {
//        private static final float MIN_CONTRAST_TITLE_TEXT = 3.0f;
//        private static final float MIN_CONTRAST_BODY_TEXT = 4.5f;

        private final int mColor;
        private final double mProportion;
        private final double mPriority;

//        private boolean mGeneratedTextColors;
//        private int mTitleTextColor;
//        private int mBodyTextColor;

        private ThemeColor(int color, double proportion) {
            mColor = color;
            mProportion = proportion;
            // (...) / 3d * (3 / 2d)
            double distance = colorDistance(mColor, Colors.WHITE.getRGB());
            mPriority = mProportion * distance;
        }

        @Override
        public int compareTo(ThemeColor themeColor) {
            return Double.compare(themeColor.mPriority, mPriority);
        }

        public int getColor() {
            return mColor;
        }

        public double getProportion() {
            return mProportion;
        }

//        public int getBodyTextColor() {
//            ensureTextColorsGenerated();
//            return mBodyTextColor;
//        }
//
//        public int getTitleTextColor() {
//            ensureTextColorsGenerated();
//            return mTitleTextColor;
//        }

//        private void ensureTextColorsGenerated() {
//            if (!mGeneratedTextColors) {
//                // First check white, as most colors will be dark
//                final int lightBodyAlpha = ColorUtil.calculateMinimumAlpha(Colors.WHITE.getRGB(), mColor, MIN_CONTRAST_BODY_TEXT);
//                final int lightTitleAlpha = ColorUtil.calculateMinimumAlpha(Colors.WHITE.getRGB(), mColor, MIN_CONTRAST_TITLE_TEXT);
//
//                if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
//                    // If we found valid light values, use them and return
//                    mBodyTextColor = ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightBodyAlpha);
//                    mTitleTextColor = ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightTitleAlpha);
//                    mGeneratedTextColors = true;
//                    return;
//                }
//
//                final int darkBodyAlpha = ColorUtil.calculateMinimumAlpha(Colors.BLACK.getRGB(), mColor, MIN_CONTRAST_BODY_TEXT);
//                final int darkTitleAlpha = ColorUtil.calculateMinimumAlpha(Colors.BLACK.getRGB(), mColor, MIN_CONTRAST_TITLE_TEXT);
//
//                if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
//                    // If we found valid dark values, use them and return
//                    mBodyTextColor = ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkBodyAlpha);
//                    mTitleTextColor = ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkTitleAlpha);
//                    mGeneratedTextColors = true;
//                    return;
//                }
//
//                // If we reach here then we can not find title and body values which use the same
//                // lightness, we need to use mismatched values
//                mBodyTextColor = lightBodyAlpha != -1 ? ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightBodyAlpha)
//                        : ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkBodyAlpha);
//                mTitleTextColor = lightTitleAlpha != -1 ? ColorUtil.setAlphaComponent(Colors.WHITE.getRGB(), lightTitleAlpha)
//                        : ColorUtil.setAlphaComponent(Colors.BLACK.getRGB(), darkTitleAlpha);
//                mGeneratedTextColors = true;
//            }
//        }
    }
}
