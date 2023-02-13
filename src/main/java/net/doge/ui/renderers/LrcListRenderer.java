package net.doge.ui.renderers;

import lombok.Data;
import net.doge.constants.Fonts;
import net.doge.models.lyric.Statement;
import net.doge.ui.components.StringTwoColor;
import net.doge.ui.componentui.LabelUI;
import net.doge.ui.componentui.list.ListUI;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class LrcListRenderer extends DefaultListCellRenderer {
    private final Font defaultFont = Fonts.NORMAL;
    private final Font highlightFont = Fonts.NORMAL_BIG;
    // 走过的歌词颜色
    private Color highlightColor;
    // 未走的歌词颜色
    private Color bgColor;
    // 高亮文字
    private StringTwoColor stc;
    // 比例
    private double ratio;
    private int row;
    private int hoverIndex = -1;
//    private boolean drawBg;

    private LabelUI highlightLabelUI;
    private LabelUI normalLabelUI;

    public LrcListRenderer() {
        highlightLabelUI = new LabelUI(1);
        normalLabelUI = new LabelUI(0.5f);
    }

    public void setRow(int row) {
        this.ratio = 0;
        this.row = row;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JLabel label = (JLabel) component;

        final int maxWidth = list.getVisibleRect().width - 10;
        if (maxWidth <= 0) return label;

        Statement statement = (Statement) value;
        String lyric = statement.toString();

        // 标签
        label.setOpaque(false);
        label.setUI(index != row ? normalLabelUI : highlightLabelUI);
        label.setForeground(bgColor);

        // 高亮的行的样式
        if (index == row) {
            label.setFont(highlightFont);
            if (stc == null || stc.getWidthThreshold() != maxWidth
                    || !stc.getText().equals(lyric) || !stc.getC1().equals(highlightColor) || !stc.getC2().equals(bgColor))
                stc = new StringTwoColor(label, lyric, highlightColor, bgColor, ratio, false, maxWidth);
            else stc.setRatio(ratio);
            label.setIcon(stc.getImageIcon());
            label.setText("");
        }
        // 其他行的样式
        else {
            label.setFont(defaultFont);
            label.setText(StringUtil.textToHtml(StringUtil.wrapLineByWidth(lyric, maxWidth)));
            label.setIcon(null);
        }
        // 设置 list 对应行的高度
        ((ListUI) list.getUI()).setCellHeight(index, getPreferredSize().height);

        return label;

        // 有性能问题，停用
//        Statement statement = (Statement) value;
//        String lyric = statement.toString();
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
//            if (stc == null || stc.getWidthThreshold() != maxWidth ||
//                    !stc.getText().equals(lyric) || !stc.getC1().equals(highlightColor) || !stc.getC2().equals(bgColor))
//                stc = new StringTwoColor(lyricLabel, lyric, highlightColor, bgColor, ratio, false, maxWidth);
//            else stc.setRatio(ratio);
//            lyricLabel.setIcon(stc.getImageIcon());
//        }
//        // 其他行的样式
//        else {
//            lyricLabel.setFont(defaultFont);
//            lyricLabel.setText(StringUtils.textToHtml(StringUtils.wrapLineByWidth(lyric, maxWidth)));
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
