package net.doge.ui.widget.combobox.ui;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.renderer.StringComboBoxRenderer;
import net.doge.ui.widget.combobox.ui.base.CustomComboBoxUI;

/**
 * @author Doge
 * @description 下拉框元素标签自定义 UI
 * @date 2020/12/13
 */
public class StringComboBoxUI extends CustomComboBoxUI {
    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f) {
        this(comboBox, f, null);
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int width) {
        this(comboBox, f, width, null);
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int[] indicesSupported) {
        this(comboBox, f, 170, indicesSupported);
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int width, int[] indicesSupported) {
        super(comboBox, f, width);
        // 下拉列表渲染
        comboBox.setRenderer(new StringComboBoxRenderer(indicesSupported));
    }
}
