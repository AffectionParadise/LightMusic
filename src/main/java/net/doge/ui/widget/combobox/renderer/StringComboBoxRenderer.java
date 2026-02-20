package net.doge.ui.widget.combobox.renderer;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Doge
 * @description 下拉框渲染器
 * @date 2020/12/7
 */
public class StringComboBoxRenderer extends CustomListCellRenderer {
    // 支持的 index 高亮显示
    private boolean opacitySensitive;
    private Set<Integer> indicesSupported;

    protected CustomLabel label = new CustomLabel();

    public StringComboBoxRenderer() {
        this(null);
    }

    public StringComboBoxRenderer(int[] indicesSupported) {
        applyIndicesSupported(indicesSupported);
        init();
    }

    private void init() {
        label.setBorder(new HDEmptyBorder(5, 0, 5, 0));
        UIStyle style = UIStyleStorage.currUIStyle;
        label.setForeground(style.getTextColor());
        label.setBgColor(style.getForeColor());
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
        label.setDrawBg(isSelected);
        label.setText((String) value);
        // combobox 当前值 index < 0
        label.setOpacity(!opacitySensitive || indicesSupported.contains(index) || index < 0 ? 1f : 0.5f);
        return label;
    }

    @Override
    public Component getRootComponent() {
        return label;
    }
}
