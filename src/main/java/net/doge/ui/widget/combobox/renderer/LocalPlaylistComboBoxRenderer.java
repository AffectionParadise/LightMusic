package net.doge.ui.widget.combobox.renderer;

import net.doge.entity.service.LocalPlaylist;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.renderer.base.CustomComboBoxRenderer;
import net.doge.ui.widget.label.CustomLabel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 下拉框渲染器
 * @date 2020/12/7
 */
public class LocalPlaylistComboBoxRenderer extends CustomComboBoxRenderer {
    public LocalPlaylistComboBoxRenderer(CustomComboBox<LocalPlaylist> comboBox) {
        super(comboBox);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomLabel label = getLabel(index);
        LocalPlaylist playlist = (LocalPlaylist) value;

        label.setDrawBg(isSelected);
        label.setText(playlist.getName());

        return label;
    }
}
