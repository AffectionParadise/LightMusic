package net.doge.ui.widget.list.renderer.core;

import lombok.Data;
import net.doge.constant.core.ui.Fonts;
import net.doge.constant.core.ui.lyric.LyricAlignment;
import net.doge.entity.core.lyric.Statement;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.list.renderer.base.StableCellHeightProvider;
import net.doge.ui.widget.list.ui.LyricListUI;
import net.doge.ui.widget.lyric.HighlightLyric;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Doge
 * @description
 * @date 2020/12/7
 */
@Data
public class LyricListRenderer extends CustomListCellRenderer implements StableCellHeightProvider {
    private final Font normalLyricTargetFont = Fonts.NORMAL_SUPER;
    private final Font highlightLyricTargetFont = Fonts.NORMAL_BIG;
    private Font shrinkFont = normalLyricTargetFont;
    private Font highlightFont = highlightLyricTargetFont;
    // 字体大小动画数组
    private Font[] fontAnimation;
    private int hIndex;
    private int sIndex;
    // 走过的歌词颜色
    private Color highlightColor;
    // 未走的歌词颜色
    private Color bgColor;
    // 高亮文字
    private HighlightLyric hl;
    // 比例
    private double ratio;
    // 高亮行
    private int row = -1;
    // 上一个高亮行
    private int lRow = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel lyricLabel = new CustomLabel();
    private CustomLabel subLyricLabel = new CustomLabel();

    // 高亮最大不透明度
    private final float highlightMaxOpacity = 1f;
    // 普通歌词最大最小不透明度
    public final float normalMaxOpacity = 0.4f;
    public final float normalMinOpacity = 0.1f;
    private Timer fontTimer;

    private final int LYRIC_GAP = ScaleUtil.scale(10);
    private final int HORIZONTAL_INSET = ScaleUtil.scale(90);
    private final int VERTICAL_INSET = ScaleUtil.scale(10);
    private final Border BORDER = new EmptyBorder(VERTICAL_INSET, 0, VERTICAL_INSET, HORIZONTAL_INSET);

    public LyricListRenderer() {
        createFontAnimation();
        fontTimer = new Timer(10, e -> {
            // 高亮行字体增大
            highlightFont = fontAnimation[hIndex++];
            // 经过行字体减小
            shrinkFont = fontAnimation[sIndex--];
            if (sIndex < 0) fontTimer.stop();
        });
        init();
    }

    private void createFontAnimation() {
        int hs = highlightFont.getSize(), ss = shrinkFont.getSize(), l = hs - ss + 1;
        fontAnimation = new Font[l];
        for (int i = 0; i < l; i++) fontAnimation[i] = shrinkFont.deriveFont((float) (ss++));
    }

    public void setRow(int row) {
        this.ratio = 0;
        lRow = this.row;
        this.row = row;

        highlightFont = fontAnimation[hIndex = 0];
        shrinkFont = fontAnimation[sIndex = fontAnimation.length - 1];
        if (!fontTimer.isRunning()) fontTimer.start();
    }

    private void init() {
        outerPanel.setLayout(new GridBagLayout());
        outerPanel.setBorder(BORDER);

        // 主歌词与可见的子歌词作为一组居中，共享单元格预留的垂直空间
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        outerPanel.add(lyricLabel, constraints);

        constraints.gridy = 1;
        constraints.insets = new Insets(LYRIC_GAP, 0, 0, 0);
        outerPanel.add(subLyricLabel, constraints);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        int maxWidth = list.getVisibleRect().width - HORIZONTAL_INSET;
        if (maxWidth <= 0) return outerPanel;
        list.setFixedCellWidth(maxWidth);

        Statement statement = (Statement) value;
        double time = statement.getTime();
        String plainLyric = statement.getPlainLyric();

        // 歌词标签
        int alignment = LyricAlignment.VALUES[LyricAlignment.lyricAlignmentIndex];
        lyricLabel.setHorizontalAlignment(alignment);
        lyricLabel.setForeground(bgColor);
        if (index == row) lyricLabel.setOpacity(highlightMaxOpacity);
        else if (index < row) lyricLabel.setOpacity(normalMinOpacity);
        else lyricLabel.setOpacity(normalMaxOpacity);

        // 高亮的行的样式
        if (index == row) {
            lyricLabel.setFont(highlightFont);
            if (hl == null
                    || hl.getWidthThreshold() != maxWidth
                    || !hl.getLabelFont().equals(highlightFont)
                    || hl.getTime() != time
                    || !hl.getPlainLyric().equals(plainLyric)
                    || !hl.getC1().equals(highlightColor)
                    || !hl.getC2().equals(bgColor))
                hl = new HighlightLyric(lyricLabel, statement, highlightColor, bgColor, ratio, false, maxWidth, !fontTimer.isRunning());
            else hl.setRatio(ratio);
            lyricLabel.setIcon(hl.getImgIcon());
            lyricLabel.setText("");
        }
        // 其他行的样式
        else {
            lyricLabel.setFont(index == lRow ? shrinkFont : normalLyricTargetFont);
            // 对长歌词手动添加 <br> 换行，由于排版问题取消自动换行
            lyricLabel.setText(HtmlUtil.textToHtmlNoWrap(HtmlUtil.wrapLineByWidth(plainLyric, maxWidth, normalLyricTargetFont.getSize())));
            lyricLabel.setIcon(null);
        }

        // 子歌词标签
        boolean hasSubLyric = statement.hasSubLyric();
        subLyricLabel.setVisible(hasSubLyric);
        if (hasSubLyric) {
            String subLyric = statement.getSubLyric();
            subLyricLabel.setFont(normalLyricTargetFont);
            subLyricLabel.setHorizontalAlignment(alignment);
            subLyricLabel.setForeground(bgColor);
            if (index == row) subLyricLabel.setOpacity(highlightMaxOpacity);
            else if (index < row) subLyricLabel.setOpacity(normalMinOpacity);
            else subLyricLabel.setOpacity(normalMaxOpacity);
            subLyricLabel.setText(HtmlUtil.textToHtmlNoWrap(HtmlUtil.wrapLineByWidth(subLyric, maxWidth, normalLyricTargetFont.getSize())));
        } else {
            subLyricLabel.setText("");
        }

        // 设置 list 对应行的高度
        ((LyricListUI) list.getUI()).setCellHeight(index, outerPanel.getPreferredSize().height);

        return outerPanel;
    }

    // 返回稳定的 Cell 高度，用于适应字体大小动画
    @Override
    public int getStableCellHeight(JList<?> list, Object value, int index, int width) {
        Statement statement = (Statement) value;
        // LyricList 传入的 width 已经扣除了 List 的 Insets
        Insets insets = outerPanel.getInsets();
        int contentWidth = Math.max(1, width - insets.left - insets.right);
        // 主歌词区域需要容纳换行文本和高亮图像，并按动画结束时的最大字号预留高度
        int textHeight = measureLyricHeight(statement.getPlainLyric(), highlightLyricTargetFont, contentWidth);
        int imageHeight = HighlightLyric.getImageHeight(lyricLabel.getFontMetrics(highlightLyricTargetFont));
        int lyricHeight = Math.max(textHeight, imageHeight);
        // 子歌词高度
        int subLyricHeight = 0;
        int gap = 0;
        if (statement.hasSubLyric()) {
            subLyricHeight = measureLyricHeight(statement.getSubLyric(), normalLyricTargetFont, contentWidth);
            gap = LYRIC_GAP;
        }
        int height = insets.top + lyricHeight + gap + subLyricHeight + insets.bottom;
        return height;
    }

    private int measureLyricHeight(String text, Font font, int width) {
        JLabel probe = new JLabel();
        probe.setFont(font);
        probe.setText(HtmlUtil.textToHtmlNoWrap(HtmlUtil.wrapLineByWidth(text, width, font.getSize())));
        return probe.getPreferredSize().height;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
