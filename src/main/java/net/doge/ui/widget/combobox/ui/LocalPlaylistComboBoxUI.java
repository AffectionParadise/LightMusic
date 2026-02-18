package net.doge.ui.widget.combobox.ui;

import net.doge.entity.service.LocalPlaylist;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.renderer.LocalPlaylistComboBoxRenderer;
import net.doge.ui.widget.combobox.ui.base.CustomComboBoxUI;

/**
 * @author Doge
 * @description 下拉框元素标签自定义 UI
 * @date 2020/12/13
 */
public class LocalPlaylistComboBoxUI extends CustomComboBoxUI {
    public LocalPlaylistComboBoxUI(CustomComboBox<LocalPlaylist> comboBox, MainFrame f) {
        this(comboBox, f, 170);
    }

    public LocalPlaylistComboBoxUI(CustomComboBox<LocalPlaylist> comboBox, MainFrame f, int width) {
        super(comboBox, f, width);
        // 下拉列表渲染
        comboBox.setRenderer(new LocalPlaylistComboBoxRenderer());
        comboBox.setMaximumSize(new HDDimension(width, 30));
    }
}
