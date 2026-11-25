package net.doge.ui.widget.combobox.renderer.base;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import java.awt.*;

public abstract class CustomComboBoxRenderer extends CustomListCellRenderer {
    private CustomComboBox<?> comboBox;
    private CustomLabel popupLabel = createLabel();
    private CustomLabel currentValueLabel = createLabel();

    private static final HDEmptyBorder BORDER = new HDEmptyBorder(5, 0, 5, 0);

    public CustomComboBoxRenderer(CustomComboBox<?> comboBox) {
        this.comboBox = comboBox;
    }

    private CustomLabel createLabel() {
        CustomLabel label = new CustomLabel();
        label.setBorder(BORDER);
        UIStyle style = UIStyleStorage.currUIStyle;
        label.setForeground(style.getTextColor());
        label.setBgColor(style.getForeColor());
        return label;
    }

    protected CustomLabel getLabel(int index) {
        if (index >= 0) return popupLabel;
        // 当前值使用下拉框本身的透明度，独立于弹层动画
        float opacity = comboBox.getExtendedOpacity();
        if (currentValueLabel.getExtendedOpacity() != opacity) currentValueLabel.setExtendedOpacity(opacity);
        return currentValueLabel;
    }

    @Override
    public Component getRootComponent() {
        return popupLabel;
    }
}
