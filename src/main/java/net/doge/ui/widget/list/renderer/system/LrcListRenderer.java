package net.doge.ui.widget.list.renderer.system;

import lombok.Data;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.LyricAlignment;
import net.doge.model.lyric.Statement;
import net.doge.ui.widget.label.ui.LabelUI;
import net.doge.ui.widget.list.ui.ListUI;
import net.doge.ui.widget.lyric.StringTwoColor;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class LrcListRenderer extends DefaultListCellRenderer {
    private final Font defaultFont = Fonts.NORMAL_TITLE;
    private Font shrinkFont = defaultFont;
    private Font highlightFont = Fonts.NORMAL_BIG;
    // 字体大小动画数组
    private Font[] fontAnimation;
    private int hIndex;
    private int sIndex;
    // 走过的歌词颜色
    private Color highlightColor;
    // 未走的歌词颜色
    private Color bgColor;
    // 高亮文字
    private StringTwoColor stc;
    // 比例
    private double ratio;
    private int row;
    private int lRow;
//    private int hoverIndex = -1;

    private boolean drawBg;

//    private CustomPanel outerPanel = new CustomPanel();
//    private CustomLabel lyricLabel = new CustomLabel();

    public final int edgeCellNum = 3;
    private final float highlightAlpha = 1f;
    public final float normalMaxAlpha = 0.4f;
    public final float normalMinAlpha = 0.05f;
    private LabelUI labelUI = new LabelUI(normalMaxAlpha);
    private Timer fontTimer;
    private Map<Integer, Float> alphas = new HashMap<>();

    private final int SPACE = 90;
    private final int SPACE_UD = 25;
    private final Border[] BORDERS = {
            // 居左
            BorderFactory.createEmptyBorder(SPACE_UD, SPACE, SPACE_UD, 0),
            // 居中
            BorderFactory.createEmptyBorder(SPACE_UD, 0, SPACE_UD, 0),
            // 局右
            BorderFactory.createEmptyBorder(SPACE_UD, 0, SPACE_UD, SPACE)
    };

    public LrcListRenderer() {
        createFontAnimation();
        fontTimer = new Timer(10, e -> {
            // 高亮行字体增大
            highlightFont = fontAnimation[hIndex++];
            // 经过行字体减小
            shrinkFont = fontAnimation[sIndex--];
            if (sIndex < 0) fontTimer.stop();
        });
//        init();
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

//    private void init() {
//        outerPanel.setLayout(new GridLayout(1, 1));
//
//        outerPanel.add(lyricLabel);
//    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JLabel label = (JLabel) component;

        int maxWidth = list.getVisibleRect().width - SPACE;
        if (maxWidth <= 0) return label;
        list.setFixedCellWidth(maxWidth);

        Statement statement = (Statement) value;
        String plainLyric = statement.getPlainLyric();

        // 标签
        label.setOpaque(false);
        label.setBorder(BORDERS[LyricAlignment.lrcAlignmentIndex]);
        label.setHorizontalAlignment(LyricAlignment.VALUES[LyricAlignment.lrcAlignmentIndex]);
        label.setForeground(bgColor);
        label.setUI(labelUI);
        if (index == row) labelUI.setAlpha(highlightAlpha);
        else if (index < row) labelUI.setAlpha(normalMinAlpha);
        else labelUI.setAlpha(alphas.getOrDefault(index, normalMaxAlpha));

        // 高亮的行的样式
        if (index == row) {
            label.setFont(highlightFont);
            if (stc == null || stc.getWidthThreshold() != maxWidth || !stc.getLabelFont().equals(highlightFont)
                    || !stc.getPlainLyric().equals(plainLyric) || !stc.getC1().equals(highlightColor) || !stc.getC2().equals(bgColor))
                stc = new StringTwoColor(label, statement, highlightColor, bgColor, ratio, false, maxWidth);
            else stc.setRatio(ratio);
            label.setIcon(stc.getImgIcon());
            label.setText("");
//            labelUI.setDrawBg(index == hoverIndex);
        }
        // 其他行的样式
        else {
            label.setFont(index == lRow ? shrinkFont : defaultFont);
            // 对长歌词手动添加 <br> 换行，由于排版问题取消自动换行
            label.setText(StringUtil.textToHtmlNoWrap(StringUtil.wrapLineByWidth(plainLyric, maxWidth, defaultFont.getSize())));
//            labelUI.setDrawBg(index == hoverIndex && StringUtil.notEmpty(text.trim()));
            label.setIcon(null);
        }
        // 设置 list 对应行的高度
        ((ListUI) list.getUI()).setCellHeight(index, getPreferredSize().height);

        return label;

        // 有性能问题，停用
//        Statement statement = (Statement) value;
//        String plainLyric = statement.getPlainLyric();
//
//        lyricLabel.setBorder(BORDERS[LyricAlignment.lrcAlignmentIndex]);
//        lyricLabel.setHorizontalAlignment(LyricAlignment.VALUES[LyricAlignment.lrcAlignmentIndex]);
//        lyricLabel.setForeground(bgColor);
//        if (index == row) lyricLabel.setInstantAlpha(highlightAlpha);
//        else lyricLabel.setInstantAlpha(alphas.getOrDefault(index, normalMaxAlpha));
//
//        int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
//        if (maxWidth <= 0) return outerPanel;
//
//        // 高亮的行的样式
//        if (index == row) {
//            lyricLabel.setFont(highlightFont);
//            if (stc == null || stc.getWidthThreshold() != maxWidth || !stc.getLabelFont().equals(highlightFont)
//                    || !stc.getPlainLyric().equals(plainLyric) || !stc.getC1().equals(highlightColor) || !stc.getC2().equals(bgColor))
//                stc = new StringTwoColor(lyricLabel, statement, highlightColor, bgColor, ratio, false, maxWidth);
//            else stc.setRatio(ratio);
//            lyricLabel.setText("");
//            lyricLabel.setIcon(stc.getImgIcon());
//        }
//        // 其他行的样式
//        else {
//            lyricLabel.setFont(index == row - 2 ? shrinkFont : defaultFont);
//            lyricLabel.setText(StringUtil.textToHtmlWithSpace(StringUtil.wrapLineByWidth(plainLyric, maxWidth)));
//            lyricLabel.setIcon(null);
//        }
//
//        Dimension ps = lyricLabel.getPreferredSize();
//        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ps.height + 2, 10));
//        outerPanel.setPreferredSize(d);
//        list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//        // 调整 list 对应行的高度
//        ((ListUI) list.getUI()).setCellHeight(index, d.height);
//
//        outerPanel.setDrawBg(drawBg && isSelected);
//
//        return outerPanel;
    }
}
