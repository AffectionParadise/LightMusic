package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Colors;
import net.doge.models.MusicPlayer;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
    private Font font;
    private Color foreColor;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomLabel label = new CustomLabel();

        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setDrawBg(isSelected);
        label.setText((String) value);
        label.setFont(font);
        label.setForeground(foreColor);

        return label;
    }
}
