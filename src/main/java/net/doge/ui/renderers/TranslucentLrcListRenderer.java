package net.doge.ui.renderers;

import lombok.Data;
import net.doge.models.Statement;
import net.doge.ui.components.StringTwoColor;
import net.doge.ui.componentui.LabelUI;
import net.doge.ui.componentui.ListUI;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class TranslucentLrcListRenderer extends DefaultListCellRenderer {
    private Font defaultFont;
    private Font highlightFont;
    // 走过的歌词颜色
    private Color foregroundColor;
    // 未走的歌词颜色
    private Color backgroundColor;
    // 高亮文字
    private StringTwoColor stc;
    // 比例
    private double ratio;
    private int row;

    private int thresholdWidth = 700;

    private LabelUI highlightLabelUI;
    private LabelUI normalLabelUI;

    public TranslucentLrcListRenderer() {
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
        Statement statement = (Statement) value;
        String lyric = statement.toString();

        label.setUI(index != row ? normalLabelUI : highlightLabelUI);
        label.setForeground(backgroundColor);
        // 所有标签透明
        label.setOpaque(false);
        label.setText(StringUtils.textToHtml(StringUtils.wrapLineByWidth(lyric, thresholdWidth)));

        // 高亮的行的样式
        if (index == row) {
            label.setFont(highlightFont);
            if (stc == null || !stc.getText().equals(lyric) || !stc.getC1().equals(foregroundColor) || !stc.getC2().equals(backgroundColor))
                stc = new StringTwoColor(this, lyric, foregroundColor, backgroundColor, ratio, false, thresholdWidth);
            else stc.setRatio(ratio);
            label.setIcon(stc.getImageIcon());
            label.setText("");
        }
        // 其他行的样式
        else {
            label.setFont(defaultFont);
        }
        // 设置 list 对应行的高度
        ((ListUI) list.getUI()).setCellHeight(index, getPreferredSize().height);

        return label;
    }
}
