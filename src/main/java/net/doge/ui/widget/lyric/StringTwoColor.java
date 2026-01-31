package net.doge.ui.widget.lyric;

import lombok.Data;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.entity.core.lyric.Statement;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

@Data
public class StringTwoColor {
    private int width;
    private int height;
    private String lyric;
    private String plainLyric;
    private Font labelFont;
    private boolean isDesktopLyric;
    private Color c1;
    private Color c2;
    private double ratio;
    private int widthThreshold;
    // 阴影水平偏移
    private int shadowHOffset;
    // 是否逐字
    private boolean isByWord;
    // 渐隐宽度
    private int fadeWidth;

    private BufferedImage buffImg;
    private BufferedImage buffImg1;
    private BufferedImage buffImg2;
    private ImageIcon imgIcon;

    private FontMetrics[] metricsBig = new FontMetrics[Fonts.TYPES_BIG.size()];
    private FontMetrics[] metricsHuge = new FontMetrics[Fonts.TYPES_HUGE.size()];

    // 每个字(也有可能是一段)的宽度
    private List<Integer> wordWidthList = new LinkedList<>();
    // 每个字(也有可能是一段)相对当行的起始时间
    private List<Integer> wordStartList = new LinkedList<>();
    // 每个字(也有可能是一段)的持续时间
    private List<Integer> wordDurationList = new LinkedList<>();

    /**
     * @param label          显示字体的标签
     * @param c1             颜色1(走过的颜色)
     * @param c2             颜色2(未走过的颜色)
     * @param ratio          颜色1所占全部的比值
     * @param isDesktopLyric 是否是桌面歌词
     * @param widthThreshold 文字最大宽度
     */
    public StringTwoColor(JLabel label, Statement stmt, Color c1, Color c2, double ratio, boolean isDesktopLyric, int widthThreshold) {
        if (stmt.isEmpty()) return;

        this.lyric = stmt.getLyric();
        this.plainLyric = stmt.getPlainLyric();

        if (RegexUtil.contains(LyricPattern.PAIR, lyric)) isByWord = true;

        this.c1 = c1;
        this.c2 = c2;
        this.isDesktopLyric = isDesktopLyric;
        this.widthThreshold = widthThreshold;
        this.shadowHOffset = ScaleUtil.scale(isDesktopLyric ? 3 : 0);

        // 获取字符串的宽（显示在屏幕上所占的像素 px）
        labelFont = label.getFont();
        float fontSize = labelFont.getSize();

        FontMetrics metrics = label.getFontMetrics(labelFont);
        for (int i = 0, len = metricsBig.length; i < len; i++)
            metricsBig[i] = label.getFontMetrics(Fonts.TYPES_BIG.get(i).deriveFont(fontSize));
        for (int i = 0, len = metricsHuge.length; i < len; i++)
            metricsHuge[i] = label.getFontMetrics(Fonts.TYPES_HUGE.get(i).deriveFont(fontSize));

        if (isByWord) {
            List<String> startList = RegexUtil.findAllGroup1(LyricPattern.START, lyric);
            for (String startStr : startList) wordStartList.add(Integer.parseInt(startStr));
            List<String> durationList = RegexUtil.findAllGroup1(LyricPattern.DURATION, lyric);
            for (String durationStr : durationList) wordDurationList.add(Integer.parseInt(durationStr));
            initWordWidthList(lyric);
        }

        // 计算宽度
        for (int i = 0, len = plainLyric.length(); i < len; i++) {
            int codePoint = plainLyric.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);

            if (!isDesktopLyric) {
                for (int j = 0, l = metricsBig.length; j < l; j++) {
                    if (!Fonts.TYPES_BIG.get(j).canDisplay(codePoint)) continue;
                    width += metricsBig[j].stringWidth(str);
                    i += chars.length - 1;
                    break;
                }
            } else {
                for (int j = 0, l = metricsHuge.length; j < l; j++) {
                    if (!Fonts.TYPES_HUGE.get(j).canDisplay(codePoint)) continue;
                    width += metricsHuge[j].stringWidth(str);
                    i += chars.length - 1;
                    break;
                }
            }
        }
        // 桌面歌词阴影显示不完全解决
        width += 2 * shadowHOffset;
        height = metrics.getHeight();
        height += fontSize;
        // 渐隐宽度根据字体大小决定
        fadeWidth = height / 2;

        // 部分字体显示不出来，留白
        width = Math.max(1, width);

        // 构造一个具有指定尺寸及类型为预定义图像类型之一的 BufferedImage
        buffImg1 = ImageUtil.createTransparentImage(width, height);
        buffImg2 = ImageUtil.createTransparentImage(width, height);

        // 通过 BufferedImage 创建 Graphics2D 对象
        Graphics2D g1 = buffImg1.createGraphics();
        Graphics2D g2 = buffImg2.createGraphics();

        // 设置抗锯齿
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int dy = height - (int) fontSize;
        if (!isDesktopLyric) dy += ScaleUtil.scale(5);

        g1.setColor(c1);
        g2.setColor(ColorUtil.deriveAlphaColor(c2, 0.45f));

        // 画字符串
        if (!isDesktopLyric) {
            int widthDrawn = 0;
            for (int i = 0, len = plainLyric.length(); i < len; i++) {
                int codePoint = plainLyric.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                for (int j = 0, l = metricsBig.length; j < l; j++) {
                    Font font = Fonts.TYPES_BIG.get(j);
                    if (!font.canDisplay(codePoint)) continue;
                    Font nf = font.deriveFont(fontSize);
                    g1.setFont(nf);
                    g2.setFont(nf);
                    g1.drawString(str, widthDrawn, dy);
                    g2.drawString(str, widthDrawn, dy);
                    int strWidth = metricsBig[j].stringWidth(str);
                    widthDrawn += strWidth;
                    i += chars.length - 1;
                    break;
                }
            }
            // 文字阴影
//            buffImg1 = ImageUtil.shadow(buffImg1, c1);
        } else {
            int widthDrawn = shadowHOffset;
            for (int i = 0, len = plainLyric.length(); i < len; i++) {
                int codePoint = plainLyric.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                for (int j = 0, l = metricsHuge.length; j < l; j++) {
                    Font font = Fonts.TYPES_HUGE.get(j);
                    if (!font.canDisplay(codePoint)) continue;
//                        Shape shape = font.createGlyphVector(metricsHuge[j].getFontRenderContext(), str).getOutline();
//                        // 文字阴影
//                        g1.setColor(shadowColor);
//                        g2.setColor(shadowColor);
//                        g1.translate(shadowXOffset, shadowYOffset);
//                        g2.translate(shadowXOffset, shadowYOffset);
//                        g1.fill(shape);
//                        g2.fill(shape);
//                        g1.translate(-shadowXOffset, -shadowYOffset);
//                        g2.translate(-shadowXOffset, -shadowYOffset);
//                        // 文字本体
//                        g1.setColor(c1);
//                        g2.setColor(c2);
//                        g1.fill(shape);
//                        g2.fill(shape);
//                        // 文字描边
//                        g1.setColor(borderColor);
//                        g2.setColor(borderColor);
//                        g1.draw(shape);
//                        g2.draw(shape);
                    Font nf = font.deriveFont(fontSize);
                    g1.setFont(nf);
                    g2.setFont(nf);
                    g1.drawString(str, widthDrawn, dy);
                    g2.drawString(str, widthDrawn, dy);
                    int strWidth = metricsHuge[j].stringWidth(str);
                    widthDrawn += strWidth;
                    i += chars.length - 1;
                    break;
                }
            }

            // 文字阴影
            buffImg1 = ImageUtil.shadow(buffImg1, c1);
//            buffImg2 = ImageUtil.shadow(buffImg2, c2);
        }

        g1.dispose();
        g2.dispose();

        // 按照比例清除相关的像素点
        if (ratio <= 1 && ratio >= 0) setRatio(ratio);
    }

    private void initWordWidthList(String lyric) {
        String[] sp = ArrayUtil.removeFirstEmpty(lyric.split(LyricPattern.PAIR, -1));
        for (String partStr : sp) {
            // 计算每段的宽度
            int w = 0;
            for (int i = 0, len = partStr.length(); i < len; i++) {
                int codePoint = partStr.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                if (!isDesktopLyric) {
                    for (int j = 0, l = metricsBig.length; j < l; j++) {
                        if (!Fonts.TYPES_BIG.get(j).canDisplay(codePoint)) continue;
                        w += metricsBig[j].stringWidth(str);
                        i += chars.length - 1;
                        break;
                    }
                } else {
                    for (int j = 0, l = metricsHuge.length; j < l; j++) {
                        if (!Fonts.TYPES_HUGE.get(j).canDisplay(codePoint)) continue;
                        w += metricsHuge[j].stringWidth(str);
                        i += chars.length - 1;
                        break;
                    }
                }
            }
            wordWidthList.add(w);
        }
    }

    /**
     * 根据歌词时间计算比率
     *
     * @param currTime      当前播放时间
     * @param lineStartTime 当行歌词起始时间
     * @return
     */
    public double calcRatio(double currTime, double lineStartTime) {
        int currTimeMs = (int) (currTime * 1000);
        int lineStartTimeMs = (int) (lineStartTime * 1000);

        int lineCurrTimeMs = currTimeMs - lineStartTimeMs;

        int wordStartIndex = ListUtil.biSearchLeft(wordStartList, lineCurrTimeMs);
        if (wordStartIndex < 0) return 0;
//        wStartIndex = wordStartIndex;
        int wordStart = wordStartList.get(wordStartIndex);
        // 防止单字比率超出
        double wordRatio = Math.min(1, (double) (lineCurrTimeMs - wordStart) / wordDurationList.get(wordStartIndex));
        // 部分情况有 NAN 值
        if (wordRatio != wordRatio) wordRatio = 1;
//        wRatio = wordRatio;
        double currWidth = ListUtil.rangeSum(wordWidthList, 0, wordStartIndex) + wordWidthList.get(wordStartIndex) * wordRatio;
        int totalWidth = width - 2 * shadowHOffset;
        // 防止整句比率超出
        double ratio = Math.min(1, currWidth / totalWidth);
        return ratio;
    }

//    private double wRatio;
//    private int wStartIndex;
//
//    private int getWordOffsetY(int x1, int x2, double wRatio) {
//        double h = 10, x = x1 + (x2 - x1) * wRatio, c = (double) (x1 + x2) / 2;
//        if (x >= x1 && x <= c) return (int) (2 * h / (x2 - x1) * (x - x1));
//        else return (int) (2 * h / (x2 - x1) * (x2 - x));
//    }

    public void setRatio(double ratio) {
        if (width == 0 || height == 0) return;

        int pw = width - 2 * shadowHOffset, t = (int) (shadowHOffset + pw * ratio + 0.5);

        // 虽然不断创建新的图像存在性能开销，但是单例清除图像时会闪烁
        buffImg = ImageUtil.createTransparentImage(width, height);
        Graphics2D g2d = GraphicsUtil.setup(buffImg.createGraphics());

        if (ratio > 0) {
//            if (wStartIndex - 1 >= 0) {
//                int x1 = wordDurationList.get(wStartIndex - 1) + shadowHOffset, x2 = wordDurationList.get(wStartIndex) + shadowHOffset;
//                g2d.translate(0, -getWordOffsetY(x1, x2, wRatio));
//            }
            // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
            GraphicsUtil.srcOver(g2d);
            g2d.drawImage(buffImg1, shadowHOffset, 0, t + fadeWidth, height, shadowHOffset, 0, t + fadeWidth, height, null);

            // 创建渐变覆盖层（使用黑色到透明的渐变，然后使用 DST_OUT）
            GradientPaint fadeOverlay = new GradientPaint(t, 0, Colors.TRANSPARENT, t + fadeWidth, 0, Colors.BLACK, false);
            // 使用 DST_OUT：目标在源外（移除黑色覆盖的部分）
            g2d.setComposite(AlphaComposite.DstOut);
            g2d.setPaint(fadeOverlay);
            g2d.fillRect(t, 0, fadeWidth, height);

            // 背景覆盖前景的模式
            g2d.setComposite(AlphaComposite.DstOver);
        }
        // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
        g2d.drawImage(buffImg2, t, 0, width - shadowHOffset, height, t, 0, width - shadowHOffset, height, null);
        g2d.dispose();

        cropImg();
        makeIcon();

        this.ratio = ratio;
    }

    // 裁剪图片使之宽度不超过阈值
    private void cropImg() {
        if (width <= widthThreshold) return;
        int pw = (int) (width * ratio + 0.5);
        if (pw <= widthThreshold / 2) buffImg = ImageUtil.region(buffImg, 0, 0, widthThreshold, height);
        else if (width - pw > widthThreshold / 2)
            buffImg = ImageUtil.region(buffImg, pw - widthThreshold / 2, 0, widthThreshold, height);
        else buffImg = ImageUtil.region(buffImg, width - widthThreshold, 0, widthThreshold, height);
    }

    private void makeIcon() {
        if (imgIcon == null) imgIcon = new ImageIcon(buffImg);
        imgIcon.setImage(buffImg);
    }
}