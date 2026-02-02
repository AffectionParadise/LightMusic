package net.doge.ui.widget.combobox.ui;

import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.base.CustomComboBoxUI;
import net.doge.ui.widget.list.renderer.core.StringComboBoxRenderer;

/**
 * @Author Doge
 * @Description 下拉框元素标签自定义 UI
 * @Date 2020/12/13
 */
public class StringComboBoxUI extends CustomComboBoxUI {

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f) {
        super(comboBox, f);
        // 下拉列表渲染
        comboBox.setRenderer(new StringComboBoxRenderer(f));
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int width) {
        this(comboBox, f);
        comboBox.setPreferredSize(new HDDimension(width, 30));
    }
}
