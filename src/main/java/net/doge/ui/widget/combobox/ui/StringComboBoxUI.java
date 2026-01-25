package net.doge.ui.widget.combobox.ui;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.base.ComboBoxUI;
import net.doge.ui.widget.list.renderer.core.StringComboBoxRenderer;

import java.awt.*;

/**
 * @Author Doge
 * @Description 下拉框元素标签自定义 UI
 * @Date 2020/12/13
 */
public class StringComboBoxUI extends ComboBoxUI {

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f) {
        super(comboBox, f);
        // 下拉列表渲染
        comboBox.setRenderer(new StringComboBoxRenderer(f));
    }

    public StringComboBoxUI(CustomComboBox<String> comboBox, MainFrame f, int width) {
        this(comboBox, f);
        comboBox.setPreferredSize(new Dimension(width, 30));
    }
}
