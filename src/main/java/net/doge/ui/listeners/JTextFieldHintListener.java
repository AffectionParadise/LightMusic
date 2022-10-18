package net.doge.ui.listeners;

/**
 * @Author yzx
 * @Description swing 单行文本框默认文本
 * @Date 2020/12/21
 */

import lombok.Data;
import net.doge.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Data
public class JTextFieldHintListener implements FocusListener {
    private String hintText;
    private JTextField textField;
    private Color placeholderColor;
    private Color inputColor;

    public JTextFieldHintListener(JTextField jTextField, String hintText, Color inputColor) {
        this.textField = jTextField;
        this.hintText = hintText;
        this.inputColor = inputColor;
        this.placeholderColor = ColorUtils.darker(inputColor);
        jTextField.setText(hintText);  // 默认直接显示
        jTextField.setForeground(placeholderColor);
    }

    @Override
    public void focusGained(FocusEvent e) {
        // 获取焦点时，清空提示内容
        String temp = textField.getText();
        if (temp.equals(hintText)) {
            textField.setText("");
        }
        textField.setForeground(inputColor);
    }

    @Override
    public void focusLost(FocusEvent e) {
        // 失去焦点时，没有输入内容，显示提示内容
        String temp = textField.getText();
        if (temp.equals("")) {
            textField.setForeground(placeholderColor);
            textField.setText(hintText);
        }
    }
}