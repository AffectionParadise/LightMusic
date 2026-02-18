package net.doge.ui.widget.combobox.renderer;

import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 下拉框渲染器
 * @date 2020/12/7
 */
@Data
public class StringComboBoxRenderer extends CustomListCellRenderer {
    // 属性不能用 font，不然重复！
    protected Font customFont = Fonts.NORMAL;
    protected CustomLabel label = new CustomLabel();

    public StringComboBoxRenderer() {
        init();
    }

    private void init() {
        label.setBorder(new HDEmptyBorder(5, 0, 5, 0));
        label.setFont(customFont);
        UIStyle style = UIStyleStorage.currUIStyle;
        label.setForeground(style.getTextColor());
        label.setBgColor(style.getForeColor());
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setDrawBg(isSelected);
        label.setText((String) value);
        return label;
    }

    @Override
    public Component getRootComponent() {
        return label;
    }
}
