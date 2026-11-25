package net.doge.ui.widget.combobox.renderer;

import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.renderer.base.CustomComboBoxRenderer;
import net.doge.ui.widget.label.CustomLabel;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Doge
 * @description 下拉框渲染器
 * @date 2020/12/7
 */
public class StringComboBoxRenderer extends CustomComboBoxRenderer {
    // 支持的 index 高亮显示
    private boolean opacitySensitive;
    private Set<Integer> indicesSupported;

    public StringComboBoxRenderer(CustomComboBox<String> comboBox, int[] indicesSupported) {
        super(comboBox);
        applyIndicesSupported(indicesSupported);
    }

    // 应用支持的 index
    public void applyIndicesSupported(int[] indicesSupported) {
        if (indicesSupported != null) {
            this.indicesSupported = new HashSet<>();
            for (int index : indicesSupported) this.indicesSupported.add(index);
            opacitySensitive = true;
        } else {
            opacitySensitive = false;
            this.indicesSupported = null;
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomLabel label = getLabel(index);
        String text = (String) value;

        label.setDrawBg(isSelected);
        label.setText(text);
        // combobox 当前值 index < 0
        label.setOpacity(!opacitySensitive || indicesSupported.contains(index) || index < 0 ? 1f : 0.5f);

        return label;
    }
}
