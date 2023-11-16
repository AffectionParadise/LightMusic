package net.doge.ui.component.lyric;

import lombok.Data;
import lombok.Getter;
import net.doge.constant.ui.Fonts;
import net.doge.model.lyric.Statement;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.ui.ImageUtil;

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
    // 是否逐字
    @Getter
    private boolean isByWord;
    private BufferedImage buffImg;
    private BufferedImage buffImg1;
    private BufferedImage buffImg2;
    private ImageIcon imgIcon;

    private final int SHADOW_H_OFFSET = 3;

    private FontMetrics[] metricsBig = new FontMetrics[Fonts.TYPES_BIG.size()];
    private FontMetrics[] metricsHuge = new FontMetrics[Fonts.TYPES_HUGE.size()];

//    private int dropOffset = 3;
//    private List<CharBlock> blockList = new LinkedList<>();
//
//    @AllArgsConstructor
//    @ToString
//    private static class CharBlock {
//        public int start;
//        public int duration;
//    }
//
//    private CharBlock findKeyCharBlock(double t) {
//        int low = 0, high = blockList.size() - 1, mid;
//        while (low < high) {
//            mid = (low + high) / 2;
//            CharBlock cb = blockList.get(mid);
//            if (t < cb.start) high = mid - 1;
//            else if (t > cb.start) low = mid + 1;
//            else return cb;
//        }
//        CharBlock l = blockList.get(low);
//        return t < l.start ? low - 1 < 0 ? new CharBlock(0, 0) : blockList.get(low - 1) : l;
//    }

    //    public static void main(String[] args) {
//        blockList.add(new CharBlock(10, 20));
//        blockList.add(new CharBlock(20, 20));
//        blockList.add(new CharBlock(30, 20));
//        System.out.println(findKeyCharBlock(19));
//        System.out.println(findKeyCharBlock(20));
//        System.out.println(findKeyCharBlock(25));
//        System.out.println(findKeyCharBlock(30));
//        System.out.println(findKeyCharBlock(35));
//    }
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

        if (RegexUtil.contains("<\\d+,\\d+>", lyric)) isByWord = true;

        this.c1 = c1;
        this.c2 = c2;
        this.isDesktopLyric = isDesktopLyric;
        this.widthThreshold = widthThreshold;

        // 获取字符串的宽（显示在屏幕上所占的像素 px）
        labelFont = label.getFont();
        float fontSize = labelFont.getSize();

        FontMetrics metrics = label.getFontMetrics(labelFont);
        for (int i = 0, len = metricsBig.length; i < len; i++)
            metricsBig[i] = label.getFontMetrics(Fonts.TYPES_BIG.get(i).deriveFont(fontSize));
        for (int i = 0, len = metricsHuge.length; i < len; i++)
            metricsHuge[i] = label.getFontMetrics(Fonts.TYPES_HUGE.get(i).deriveFont(fontSize));

//        Color borderColor = ColorUtils.darker(ColorUtils.darker(c2));
//        int shadowXOffset = 2, shadowYOffset = 2;
//        Color shadowColor = Colors.BLACK;

        if (isByWord) {
            List<String> startList = RegexUtil.findAllGroup1("<(\\d+),\\d+>", lyric);
            for (String startStr : startList) wordStartList.add(Integer.parseInt(startStr));
            List<String> durationList = RegexUtil.findAllGroup1("<\\d+,(\\d+)>", lyric);
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
        if (isDesktopLyric) width += 2 * SHADOW_H_OFFSET;
        height = metrics.getHeight();
//        height += fontSize + dropOffset;
        height += fontSize;

        // 部分字体显示不出来，留白
        width = Math.max(1, width);

        // 构造一个具有指定尺寸及类型为预定义图像类型之一的 BufferedImage
        buffImg1 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        buffImg2 = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//        buffImg1 = new BufferedImage(width, height - dropOffset, BufferedImage.TYPE_4BYTE_ABGR);
//        buffImg2 = new BufferedImage(width, height - dropOffset, BufferedImage.TYPE_4BYTE_ABGR);

        // 通过 BufferedImage 创建 Graphics2D 对象
        Graphics2D g1 = buffImg1.createGraphics();
        Graphics2D g2 = buffImg2.createGraphics();

        // 设置抗锯齿
        g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int dy = height - (int) fontSize;
//        int dy = height - (int) fontSize - dropOffset;

        g1.setColor(c1);
        g2.setColor(c2);

//        // 设置颜色
//        if (!isDesktopLyric) {
//            g1.setColor(c1);
//            g2.setColor(c2);
//        } else {
//            g1.translate(0, dy);
//            g2.translate(0, dy);
//        }

        // 画字符串
        if (!isDesktopLyric) {
            int widthDrawn = 0;
            for (int i = 0, len = plainLyric.length(); i < len; i++) {
                int codePoint = plainLyric.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                for (int j = 0, l = metricsBig.length; j < l; j++) {
                    Font font = Fonts.TYPES_BIG.get(j);
                    if (font.canDisplay(codePoint)) {
                        Font nf = font.deriveFont(fontSize);
                        g1.setFont(nf);
                        g2.setFont(nf);
                        g1.drawString(str, widthDrawn, dy);
                        g2.drawString(str, widthDrawn, dy);
                        int strWidth = metricsBig[j].stringWidth(str);
//                        blockList.add(new CharBlock(widthDrawn, strWidth));
                        widthDrawn += strWidth;
                        i += chars.length - 1;
                        break;
                    }
                }
            }
        } else {
            int widthDrawn = SHADOW_H_OFFSET;
            for (int i = 0, len = plainLyric.length(); i < len; i++) {
                int codePoint = plainLyric.codePointAt(i);
                char[] chars = Character.toChars(codePoint);
                String str = new String(chars);

                for (int j = 0, l = metricsHuge.length; j < l; j++) {
                    Font font = Fonts.TYPES_HUGE.get(j);
                    if (font.canDisplay(codePoint)) {
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
//                        blockList.add(new CharBlock(widthDrawn, strWidth));
                        widthDrawn += strWidth;
//                        g1.translate(widthDrawn, 0);
//                        g2.translate(widthDrawn, 0);
                        i += chars.length - 1;
                        break;
                    }
                }
            }

            // 文字阴影
            buffImg1 = ImageUtil.shadow(buffImg1);
            buffImg2 = ImageUtil.shadow(buffImg2);
        }

        g1.dispose();
        g2.dispose();

        // 按照比例清除相关的像素点
        if (ratio <= 1 && ratio >= 0) setRatio(ratio);
    }

    private void initWordWidthList(String lyric) {
        String[] sp = ArrayUtil.removeFirstEmpty(lyric.split("<\\d+,\\d+>", -1));
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
     * @param currTime  当前播放时间
     * @param startTime 当行歌词起始时间
     * @return
     */
    public double calcRatio(double currTime, double startTime) {
        int currTimeMs = (int) (currTime * 1000);
        int startTimeMs = (int) (startTime * 1000);

        int lineCurrTimeMs = currTimeMs - startTimeMs;

        int wordStartIndex = ListUtil.biSearchLeft(wordStartList, lineCurrTimeMs);
        if (wordStartIndex < 0) return 0;
        int wordStart = wordStartList.get(wordStartIndex);
        // 防止单字比率超出
        double wordRatio = Math.min(1, (double) (lineCurrTimeMs - wordStart) / wordDurationList.get(wordStartIndex));
        double currWidth = ListUtil.rangeSum(wordWidthList, 0, wordStartIndex) + wordWidthList.get(wordStartIndex) * wordRatio;
        int totalWidth = isDesktopLyric ? width - 2 * SHADOW_H_OFFSET : width;
        // 防止整句比率超出
        double ratio = Math.min(1, currWidth / totalWidth);
        return ratio;
    }

    public void setRatio(double ratio) {
        if (width == 0 || height == 0) return;

        int t = (int) (width * ratio + 0.5);
//        CharBlock kcb = findKeyCharBlock(t);
//        int dOff = dropOffset - (int) (((double) t - kcb.start) / kcb.duration * dropOffset);
        if (width > widthThreshold || buffImg == null) {
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2d = buffImg.createGraphics();
//            g2d.drawImage(buffImg1, 0, 0, kcb.start, height - dropOffset, 0, 0, kcb.start, height - dropOffset, null);
//            g2d.drawImage(buffImg1, kcb.start, dOff, t, dOff + height - dropOffset, kcb.start, 0, t, height - dropOffset, null);
//            g2d.drawImage(buffImg2, t, dOff, kcb.start + kcb.duration, dOff + height - dropOffset, t, 0, kcb.start + kcb.duration, height - dropOffset, null);
//            g2d.drawImage(buffImg2, kcb.start + kcb.duration, dropOffset, width, height, kcb.start + kcb.duration, 0, width, height - dropOffset, null);
            // 将 buffImg 的左半部分用 buffImg1 的左半部分替换
            g2d.drawImage(buffImg1, 0, 0, t, height, 0, 0, t, height, null);
//            g2d.drawImage(buffImg1, 0, 0, t, height - dropOffset, 0, 0, t, height - dropOffset, null);
            // 将 buffImg 的右半部分用 buffImg2 的右半部分替换
            g2d.drawImage(buffImg2, t, 0, width, height, t, 0, width, height, null);
//            g2d.drawImage(buffImg2, t, 0, width, height - dropOffset, t, 0, width, height - dropOffset, null);
            g2d.dispose();
            cropImg();
            makeIcon();
        } else {
            int o = (int) (width * this.ratio + 0.5);
            Graphics2D g2d = buffImg.createGraphics();
            if (this.ratio < ratio) {
                // 将 buffImg 的需要更新的部分用 buffImg1 的对应部分替换
                clearRect(buffImg, o, 0, t - o, height);
                g2d.drawImage(buffImg1, o, 0, t, height, o, 0, t, height, null);
//                g2d.drawImage(buffImg1, o, 0, t, height - dropOffset, o, 0, t, height - dropOffset, null);
            } else if (this.ratio > ratio) {
                // 将 buffImg 的需要更新的部分用 buffImg2 的对应部分替换
                clearRect(buffImg, t, 0, o - t, height);
                g2d.drawImage(buffImg2, t, 0, o, height, t, 0, o, height, null);
//                g2d.drawImage(buffImg2, t, 0, o, height - dropOffset, t, 0, o, height - dropOffset, null);
            }
            g2d.dispose();
        }
        this.ratio = ratio;
    }

    // 清除图像区域为透明
    private void clearRect(BufferedImage img, int x, int y, int width, int height) {
        for (int i = x, w = x + width; i < w; i++) {
            for (int j = y, h = y + height; j < h; j++) {
                img.setRGB(i, j, 0);
            }
        }
    }

    // 裁剪图片使之宽度不超过阈值
    private void cropImg() {
        if (width <= widthThreshold) return;
        int foreWidth = (int) (width * ratio + 0.5);
        if (foreWidth <= widthThreshold / 2) buffImg = ImageUtil.region(buffImg, 0, 0, widthThreshold, height);
        else if (width - foreWidth > widthThreshold / 2)
            buffImg = ImageUtil.region(buffImg, foreWidth - widthThreshold / 2, 0, widthThreshold, height);
        else buffImg = ImageUtil.region(buffImg, width - widthThreshold, 0, widthThreshold, height);
    }

    private void makeIcon() {
        if (imgIcon == null) imgIcon = new ImageIcon(buffImg);
        imgIcon.setImage(buffImg);
    }
}