package net.doge.ui.widget.combobox.ui;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.renderer.StringComboBoxRenderer;
import net.doge.ui.widget.combobox.ui.base.CustomComboBoxUI;

/**
 * @Author Doge
 * @Description 下拉框元素标签自定义 UI
 * @Date 2020/12/13
 */
public class StringComboBoxUI extends CustomComboBoxUI {
    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f) {
        this(comboBox, f, 170);
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int width) {
        super(comboBox, f, width);
        // 下拉列表渲染
        comboBox.setRenderer(new StringComboBoxRenderer());
    }
}
