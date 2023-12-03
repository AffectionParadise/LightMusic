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

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class LrcListRenderer extends DefaultListCellRenderer {
    private final Font defaultFont = Fonts.NORMAL.deriveFont(18f);
    private Font shrinkFont = defaultFont;
    private Font highlightFont = Fonts.NORMAL_BIG;
    // 走过的歌词颜色
    private Color highlightColor;
    // 未走的歌词颜色
    private Color bgColor;
    // 高亮文字
    private StringTwoColor stc;
    // 比例
    private double ratio;
    private int row;
//    private int hoverIndex = -1;

//    private boolean drawBg;

    private LabelUI highlightLabelUI = new LabelUI(1);
    private LabelUI normalLabelUI = new LabelUI(0.4f);
    private Timer fontTimer;

    private final int SPACE = 90;
    private final Border[] BORDERS = {
            // 居左
            BorderFactory.createEmptyBorder(1, SPACE, 1, 0),
            // 居中
            BorderFactory.createEmptyBorder(1, 0, 1, 0),
            // 局右
            BorderFactory.createEmptyBorder(1, 0, 1, SPACE)
    };

    public LrcListRenderer() {
        fontTimer = new Timer(10, e -> {
            // 高亮行字体增大
            highlightFont = highlightFont.deriveFont(highlightFont.getSize() + 1f);

            // 经过行字体减小
            shrinkFont = shrinkFont.deriveFont(shrinkFont.getSize() - 1f);

            if (highlightFont.getSize() == Fonts.NORMAL_BIG.getSize()) fontTimer.stop();
        });
    }

    public void setRow(int row) {
        this.ratio = 0;
        this.row = row;
        highlightFont = defaultFont;
        shrinkFont = Fonts.NORMAL_BIG;
        if (!fontTimer.isRunning()) fontTimer.start();
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JLabel label = (JLabel) component;

        final int maxWidth = list.getVisibleRect().width - SPACE;
        if (maxWidth <= 0) return label;
        list.setFixedCellWidth(maxWidth);

        Statement statement = (Statement) value;
        String plainLyric = statement.getPlainLyric();

        // 标签
        label.setOpaque(false);
        label.setBorder(BORDERS[LyricAlignment.lrcAlignmentIndex]);
        label.setHorizontalAlignment(LyricAlignment.VALUES[LyricAlignment.lrcAlignmentIndex]);
        label.setForeground(bgColor);
        label.setUI(index != row ? normalLabelUI : highlightLabelUI);

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
            label.setFont(index == row - 2 ? shrinkFont : defaultFont);
            label.setText(StringUtil.textToHtml(StringUtil.wrapLineByWidth(plainLyric, maxWidth)));
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
//        CustomPanel outerPanel = new CustomPanel();
//        CustomLabel lyricLabel = new CustomLabel();
//
//        lyricLabel.setUI(index != row ? normalLabelUI : highlightLabelUI);
//        lyricLabel.setForeground(bgColor);
//
//        GridLayout layout = new GridLayout(1, 1);
//        layout.setHgap(15);
//        outerPanel.setLayout(layout);
//
//        outerPanel.add(lyricLabel);
//
//        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//
//        if (maxWidth <= 0) return outerPanel;
//
//        // 高亮的行的样式
//        if (index == row) {
//            lyricLabel.setFont(highlightFont);
//            if (stc == null || stc.getWidthThreshold() != maxWidth || !stc.getLabelFont().equals(highlightFont)
//                    || !stc.getPlainLyric().equals(plainLyric) || !stc.getC1().equals(highlightColor) || !stc.getC2().equals(bgColor))
//                stc = new StringTwoColor(lyricLabel, statement, highlightColor, bgColor, ratio, false, maxWidth);
//            else stc.setRatio(ratio);
//            lyricLabel.setIcon(stc.getImgIcon());
//        }
//        // 其他行的样式
//        else {
//            lyricLabel.setFont(index == row - 2 ? shrinkFont : defaultFont);
//            lyricLabel.setText(StringUtil.textToHtmlWithSpace(StringUtil.wrapLineByWidth(plainLyric, maxWidth)));
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
