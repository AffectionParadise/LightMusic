package net.doge.ui.widget.list.renderer.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.label.CustomLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 下拉框渲染器
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComboBoxRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color textColor;
    private Color foreColor;
    private CustomLabel label = new CustomLabel();

    public ComboBoxRenderer(MainFrame f) {
        textColor = f.currUIStyle.getTextColor();
        foreColor = f.currUIStyle.getForeColor();

        init();
    }

    private void init() {
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setFont(customFont);
        label.setForeground(textColor);
        label.setBgColor(foreColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setDrawBg(isSelected);
        label.setText((String) value);
        return label;
    }
}
