package net.doge.ui.widget.combobox.renderer;

import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.entity.service.LocalPlaylist;
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
public class LocalPlaylistComboBoxRenderer extends CustomListCellRenderer {
    protected CustomLabel label = new CustomLabel();

    public LocalPlaylistComboBoxRenderer() {
        init();
    }

    private void init() {
        label.setBorder(new HDEmptyBorder(5, 0, 5, 0));
        UIStyle style = UIStyleStorage.currUIStyle;
        label.setForeground(style.getTextColor());
        label.setBgColor(style.getForeColor());
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        label.setDrawBg(isSelected);
        label.setText(((LocalPlaylist) value).getName());
        return label;
    }

    @Override
    public Component getRootComponent() {
        return label;
    }
}
