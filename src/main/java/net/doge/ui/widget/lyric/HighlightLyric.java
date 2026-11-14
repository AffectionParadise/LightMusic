package net.doge.ui.widget.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.constant.core.ui.Colors;
import net.doge.constant.core.ui.Fonts;
import net.doge.entity.core.lyric.Statement;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.collection.ListUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.math.MathUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

@Data
public class HighlightLyric {
    // 尺寸
    private int width;
    private int height;
    // 歌词
    private double time;
    private String lyric;
    private String plainLyric;
    // 字体
    private Font labelFont;
    // 是否为桌面歌词
    private boolean isDesktopLyric;
    // 颜色
    private Color c1;
    private Color c2;
    // 比率
    private double ratio;
    // 宽度限制
    private int widthThreshold;
    // 阴影水平偏移
    private int shadowHOffset;
    // 是否逐字
    private boolean isByWord;
    // 渐隐宽度
    private int fadeWidth;

    // 输出的歌词图像
    private BufferedImage img;
    // 走过的歌词图像
    private BufferedImage imgL;
    // 歌词的光辉效果图像
    private BufferedImage imgGlow;
    // 未走过的歌词图像
    private BufferedImage imgR;
    private ImageIcon imgIcon;

    private FontMetrics[] metricsArray;

    // 最小上浮动画时间
    private final long minDropDuration = 100;
    // 最大简单上浮动画时间
    private final long maxSimpleDropDuration = 1000;
    // 起始/经过/结束位移
    private final int startDrop = ScaleUtil.scale(3);
    private final double interpolarX = 0.5;
    private final int topDrop = ScaleUtil.scale(-2);
    private final int destDrop = ScaleUtil.scale(0);
    // 每个字(也有可能是一段)的下坠高度
    public List<Integer> wordDropList = new LinkedList<>();
    public List<Integer> wordDropOriginList = new LinkedList<>();
    public List<Integer> wordDropGapList = new LinkedList<>();

    // 光辉效果出现要求的字(段)最小持续时间
    private final long glowOccurrenceDemand = 1000;
    // 光辉效果透明度变化函数参数(在 x 点达到最大，然后在 y 点开始减小)
    private final double glowX = 0.3;
    private final double glowY = 0.8;
    // 每个字(也有可能是一段)的光辉透明度
    public List<Float> wordGlowOpacityList = new LinkedList<>();

    // 每个字(也有可能是一段)的宽度
    private List<Integer> wordWidthList = new LinkedList<>();
    // 字(也有可能是一段)宽度前缀和
    private List<Integer> wordWidthPrefixSumExcludedList = new LinkedList<>();
    // 每个字(也有可能是一段)相对当行的起始时间
    private List<Long> wordStartList = new LinkedList<>();
    // 每个字(也有可能是一段)的持续时间
    private List<Long> wordDurationList = new LinkedList<>();

    /**
     * @param label          显示字体的标签
     * @param c1             颜色1(走过的颜色)
     * @param c2             颜色2(未走过的颜色)
     * @param ratio          颜色1所占全部的比值
     * @param isDesktopLyric 是否是桌面歌词
     * @param widthThreshold 文字最大宽度
     * @param glowRequired   是否生成光辉效果文字(此项防止歌词文字字体动画导致的性能损耗)
     */
    public HighlightLyric(JLabel label, Statement stmt, Color c1, Color c2, double ratio, boolean isDesktopLyric, int widthThreshold, boolean glowRequired) {
        if (stmt.isEmpty()) return;

        this.c1 = c1;
        this.c2 = c2;
        this.isDesktopLyric = isDesktopLyric;
        this.widthThreshold = widthThreshold;
        this.shadowHOffset = ScaleUtil.scale(isDesktopLyric ? 3 : 0);

        this.time = stmt.getTime();
        this.lyric = stmt.getLyric();
        this.plainLyric = stmt.getPlainLyric();

        // 是否为逐字歌词
        if (RegexUtil.contains(LyricPattern.PAIR, lyric)) isByWord = true;

        // 获取字符串的宽（显示在屏幕上所占的像素 px）
        labelFont = label.getFont();
        float fontSize = labelFont.getSize();

        // 初始化 Metrics
        List<Font> fonts = isDesktopLyric ? Fonts.TYPES_HUGE : Fonts.TYPES_BIG;
        metricsArray = new FontMetrics[fonts.size()];
        for (int i = 0, len = metricsArray.length; i < len; i++)
            metricsArray[i] = label.getFontMetrics(fonts.get(i).deriveFont(fontSize));

        // 初始化每段时间数据，非逐字歌词需要等待 EndTime 传入后再更新
        if (isByWord) {
            List<String> startList = RegexUtil.findAllGroup1(LyricPattern.START, lyric);
            for (String startStr : startList) wordStartList.add(Long.parseLong(startStr));
            List<String> durationList = RegexUtil.findAllGroup1(LyricPattern.DURATION, lyric);
            for (String durationStr : durationList) wordDurationList.add(Long.parseLong(durationStr));
        }
        initWordWidthList(lyric);

        // 计算宽度
        for (int i = 0, len = plainLyric.length(); i < len; i++) {
            int codePoint = plainLyric.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);

            for (int j = 0, l = metricsArray.length; j < l; j++) {
                if (!fonts.get(j).canDisplay(codePoint)) continue;
                width += metricsArray[j].stringWidth(str);
                i += chars.length - 1;
                break;
            }
        }
        // 桌面歌词阴影显示不完全解决
        width += 2 * shadowHOffset;
        height = label.getFontMetrics(labelFont).getHeight();
        height += fontSize;
        // 渐隐宽度根据字体大小决定
        fadeWidth = height / 2;

        // 部分字体显示不出来，留白
        width = Math.max(1, width);

        // 构造一个具有指定尺寸及类型为预定义图像类型之一的 BufferedImage
        imgL = ImageUtil.createTransparentImage(width, height);
        imgR = ImageUtil.createTransparentImage(width, height);

        // 通过 BufferedImage 创建 Graphics2D 对象
        Graphics2D g1 = GraphicsUtil.setup(imgL.createGraphics());
        Graphics2D g2 = GraphicsUtil.setup(imgR.createGraphics());

        int dy = height - (int) fontSize;
        if (!isDesktopLyric) dy += ScaleUtil.scale(5);

        g1.setColor(c1);
        g2.setColor(ColorUtil.deriveAlpha(c2, 0.45f));

        // 画文字
        int widthDrawn = shadowHOffset;
        for (int i = 0, len = plainLyric.length(); i < len; i++) {
            int codePoint = plainLyric.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);

            for (int j = 0, l = metricsArray.length; j < l; j++) {
                Font font = fonts.get(j);
                if (!font.canDisplay(codePoint)) continue;
                Font nf = font.deriveFont(fontSize);
                g1.setFont(nf);
                g2.setFont(nf);
                g1.drawString(str, widthDrawn, dy);
                g2.drawString(str, widthDrawn, dy);
                int strWidth = metricsArray[j].stringWidth(str);
                widthDrawn += strWidth;
                i += chars.length - 1;
                break;
            }
        }
        // 桌面歌词文字阴影
        if (isDesktopLyric) imgL = ImageUtil.shadow(imgL, c1);
        // 初始化光辉效果的文字
        if (glowRequired) imgGlow = ImageUtil.glowShadow(imgL, c1);

        g1.dispose();
        g2.dispose();

        // 按照比例清除相关的像素点
        if (ratio <= 1 && ratio >= 0) setRatio(ratio);
    }

    private void initWordWidthList(String lyric) {
        // 非逐字歌词直接分割成单个字符
        String[] sp = ArrayUtil.removeFirstEmpty(isByWord ? lyric.split(LyricPattern.PAIR, -1) : lyric.split(""));
        // 计算每段的宽度
        int sum = 0;
        List<Font> fonts = isDesktopLyric ? Fonts.TYPES_HUGE : Fonts.TYPES_BIG;
        for (String partStr : sp) {
            // 计算宽度前缀和
            wordWidthPrefixSumExcludedList.add(sum);
            int w = 0;
            for (int i = 0, len = partStr.length(); i < len; i++) {
                int codePoint = partStr.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                for (int j = 0, l = metricsArray.length; j < l; j++) {
                    if (!fonts.get(j).canDisplay(codePoint)) continue;
                    w += metricsArray[j].stringWidth(str);
                    i += chars.length - 1;
                    break;
                }
            }
            sum += w;
            wordWidthList.add(w);
            // 初始化每段文字下坠高度
            wordDropOriginList.add(startDrop);
            wordDropList.add(startDrop);
            wordDropGapList.add(0);
            // 初始化每段文字光辉透明度
            wordGlowOpacityList.add(0f);
        }
    }

    /**
     * 根据歌词时间计算比率
     *
     * @param currTime      当前播放时间
     * @param lineStartTime 当行歌词起始时间
     * @return
     */
    public double computeRatio(double currTime, double lineStartTime) {
        long currTimeMs = (long) (currTime * 1000), lineStartTimeMs = (long) (lineStartTime * 1000);
        long lineCurrTimeMs = currTimeMs - lineStartTimeMs;

        int wordStartIndex = ListUtil.binarySearchLowBound(wordStartList, lineCurrTimeMs);
        if (wordStartIndex < 0) return 0;
        long wordStart = wordStartList.get(wordStartIndex);
        // 防止单字比率超出
        double wordRatio = Math.min(1, (double) (lineCurrTimeMs - wordStart) / wordDurationList.get(wordStartIndex));
        // 部分情况有 NAN 值
        if (Double.isNaN(wordRatio)) wordRatio = 1;
        double currWidth = wordWidthPrefixSumExcludedList.get(wordStartIndex) + wordWidthList.get(wordStartIndex) * wordRatio;
        int totalWidth = width - 2 * shadowHOffset;
        // 防止整句比率超出
        double ratio = Math.min(1, currWidth / totalWidth);
        return ratio;
    }

    // 更新普通歌词特效参数
    public void updateNormalWordEffects(double currTime, double lineStartTime, double lineEndTime) {
        // 如果每段起始/持续时间未初始化(针对非逐字歌词)
        if (wordStartList.isEmpty()) {
            long lineStartTimeMs = (long) (lineStartTime * 1000), lineEndTimeMs = (long) (lineEndTime * 1000);
            long lineDurationMs = lineEndTimeMs - lineStartTimeMs;

            int totalWidth = width - 2 * shadowHOffset;
            for (int i = 0, len = wordWidthList.size(); i < len; i++) {
                int wordWidth = wordWidthList.get(i);
                long wordStart = lineDurationMs * wordWidthPrefixSumExcludedList.get(i) / totalWidth;
                long wordDuration = lineDurationMs * wordWidth / totalWidth;
                wordStartList.add(wordStart);
                wordDurationList.add(wordDuration);
            }
        }
        updateWordEffects(currTime, lineStartTime);
    }

    // 更新逐字歌词特效参数
    public void updateWordEffects(double currTime, double lineStartTime) {
        int currTimeMs = (int) (currTime * 1000), lineStartTimeMs = (int) (lineStartTime * 1000);
        int lineCurrTimeMs = currTimeMs - lineStartTimeMs;
        for (int i = 0, len = wordStartList.size(); i < len; i++) {
            long wordStart = wordStartList.get(i);
            long wordDuration = wordDurationList.get(i);
            // Drop 动画进度，控制在 0-1 之间
            long wordElapsed = lineCurrTimeMs - wordStart;
            double progress = MathUtil.clamp((double) wordElapsed / Math.max(minDropDuration, wordDuration), 0, 1);
            // 超出最大简单动画时间，使用曲线动画
            int wordDropOrigin = computeWordDrop(progress, wordDuration >= maxSimpleDropDuration);
            wordDropOriginList.set(i, wordDropOrigin);
            wordDropGapList.set(i, Math.abs(wordDropOrigin - wordDropList.get(i)));
            // 更新光辉透明度
            float glowOpacity = wordDuration < glowOccurrenceDemand ? 0f : computeGlowOpacity(progress);
            wordGlowOpacityList.set(i, glowOpacity);
        }
    }

    // 根据进度计算 Drop
    private int computeWordDrop(double progress, boolean useCurve) {
        if (useCurve)
            return (int) curveWordDrop(progress, startDrop, interpolarX, topDrop, destDrop);
        // 线性匀速
        return (int) (startDrop + (destDrop - startDrop) * progress);
    }

    // 下坠高度计算曲线：过 (0, a) 和 (1, b)，在中间插入 (t1, c) 的曲线，计算 t 处的值
    private double curveWordDrop(double t, double a, double t1, double c, double b) {
        // 使用正弦函数实现平滑的缓入缓出
        if (t <= t1) {
            // 第一段：正弦缓入
            double u = t / t1;
            double sinFactor = Math.sin(u * Math.PI / 2);
            return a + (c - a) * sinFactor;
        } else {
            // 第二段：正弦缓出
            double v = (t - t1) / (1 - t1);
            double sinFactor = Math.sin((1 - v) * Math.PI / 2);
            return b + (c - b) * sinFactor;
        }
    }

    // 根据进度计算光辉透明度
    private float computeGlowOpacity(double progress) {
        return (float) curveGlowOpacity(progress, glowX, glowY);
    }

    // 光辉透明度计算函数：过 (0, 0) 和 (1, 0)，在中间插入 (x, 1) 和 (y, 1) 的曲线，计算 t 处的值
    private double curveGlowOpacity(double t, double x, double y) {
        // 上升段：从 (0,0) 到 (x,1)，斜率为 1/x
        if (t <= x) return t / x;
            // 水平段：恒为 1
        else if (t <= y) return 1;
            // 下降段：从 (y,1) 到 (1,0)，斜率为 -1/(1-y)
        else return (1 - t) / (1 - y);
    }

    // 画 imgL 作为左侧，带特效
    private void paintImgLWithEffects(Graphics2D g2d, int t) {
        for (int i = 0, len = wordDropList.size(); i < len; i++) {
            int wordDrop = wordDropList.get(i);
            int wordWidth = wordWidthList.get(i);
            int prefix = wordWidthPrefixSumExcludedList.get(i);
            int dx1 = shadowHOffset + prefix, dy1 = wordDrop, dx2 = Math.min(shadowHOffset + prefix + wordWidth, t + fadeWidth), dy2 = height + wordDrop;
            if (dx1 > t + fadeWidth) break;
            int sx1 = dx1, sy1 = 0, sx2 = dx2, sy2 = height;
            g2d.drawImage(imgL, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            // 绘制光辉效果
            float glowOpacity = wordGlowOpacityList.get(i);
            GraphicsUtil.srcOver(g2d, glowOpacity);
            g2d.drawImage(imgGlow, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            GraphicsUtil.srcOver(g2d);
        }
    }

    // 画 ImgR 作为右侧，带特效
    private void paintImgRWithEffects(Graphics2D g2d, int t) {
        for (int i = wordDropList.size() - 1; i >= 0; i--) {
            int wordDrop = wordDropList.get(i);
            int wordWidth = wordWidthList.get(i);
            int prefix = wordWidthPrefixSumExcludedList.get(i);
            int dx1 = Math.max(t, shadowHOffset + prefix), dy1 = wordDrop, dx2 = shadowHOffset + prefix + wordWidth, dy2 = height + wordDrop;
            if (dx2 < t) break;
            int sx1 = dx1, sy1 = 0, sx2 = dx2, sy2 = height;
            g2d.drawImage(imgR, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
    }

    public void setRatio(double ratio) {
        if (width == 0 || height == 0) return;
        int pw = width - 2 * shadowHOffset, t = (int) Math.round(shadowHOffset + pw * ratio);
        // 由于单例清除图像时会闪烁，因此需要不断创建新的图像，尽管存在性能开销
        img = ImageUtil.createTransparentImage(width, height);
        Graphics2D g2d = GraphicsUtil.setup(img.createGraphics());
        if (ratio > 0) {
            // 将 img 的左半部分用 imgL 的左半部分替换
            paintImgLWithEffects(g2d, t);
//            g2d.drawImage(imgL, shadowHOffset, 0, t + fadeWidth, height, shadowHOffset, 0, t + fadeWidth, height, null);
            // 创建渐变覆盖层（使用黑色到透明的渐变，然后使用 DST_OUT）
            GradientPaint fadeOverlay = new GradientPaint(t, 0, Colors.TRANSPARENT, t + fadeWidth, 0, Colors.BLACK, false);
            // 使用 DST_OUT：目标在源外（移除黑色覆盖的部分）
            g2d.setComposite(AlphaComposite.DstOut);
            g2d.setPaint(fadeOverlay);
            g2d.fillRect(t, 0, fadeWidth, height);
            // 背景覆盖前景的模式
            g2d.setComposite(AlphaComposite.DstOver);
        }
        // 将 img 的右半部分用 imgR 的右半部分替换
        paintImgRWithEffects(g2d, t);
//        g2d.drawImage(imgR, t, 0, width - shadowHOffset, height, t, 0, width - shadowHOffset, height, null);
        g2d.dispose();

        cropImg();
        makeIcon();

        this.ratio = ratio;
    }

    // 裁剪图片使之宽度不超过阈值
    private void cropImg() {
        if (width <= widthThreshold) return;
        int pw = (int) Math.round(width * ratio);
        if (pw <= widthThreshold / 2) img = ImageUtil.cut(img, 0, 0, widthThreshold, height);
        else if (width - pw > widthThreshold / 2)
            img = ImageUtil.cut(img, pw - widthThreshold / 2, 0, widthThreshold, height);
        else img = ImageUtil.cut(img, width - widthThreshold, 0, widthThreshold, height);
    }

    private void makeIcon() {
        if (imgIcon == null) imgIcon = new ImageIcon(img);
        imgIcon.setImage(img);
    }
}