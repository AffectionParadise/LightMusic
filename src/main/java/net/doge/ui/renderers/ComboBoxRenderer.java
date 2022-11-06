package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.ui.components.CustomLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description 下拉框渲染器
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboBoxRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;

    public ComboBoxRenderer(Color foreColor) {
        this.foreColor = foreColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomLabel label = new CustomLabel();

        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setDrawBg(isSelected);
        label.setText((String) value);
        label.setFont(customFont);
        label.setForeground(foreColor);

        return label;
    }
}
