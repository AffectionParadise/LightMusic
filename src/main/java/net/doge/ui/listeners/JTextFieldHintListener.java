package net.doge.ui.listeners;

/**
 * @Author yzx
 * @Description swing 单行文本框默认文本
 * @Date 2020/12/21
 */

import lombok.Data;
import net.doge.ui.components.CustomTextField;
import net.doge.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Data
public class JTextFieldHintListener implements FocusListener {
    private String hintText;
    private CustomTextField tf;
    private Color placeholderColor;
    private Color inputColor;

    public JTextFieldHintListener(CustomTextField tf, String hintText, Color inputColor) {
        this.tf = tf;
        this.hintText = hintText;
        this.inputColor = inputColor;
        this.placeholderColor = ColorUtils.darker(inputColor);
        tf.setText(hintText);  // 默认直接显示
        tf.setForeground(placeholderColor);
    }

    @Override
    public void focusGained(FocusEvent e) {
        // 获取焦点时，清空提示内容
        String temp = tf.getText();
        if (temp.equals(hintText)) {
            tf.setText("");
            tf.setOccupied(true);
        }
        tf.setForeground(inputColor);
    }

    @Override
    public void focusLost(FocusEvent e) {
        // 失去焦点时，没有输入内容，显示提示内容
        String temp = tf.getText();
        if (temp.equals("")) {
            tf.setForeground(placeholderColor);
            tf.setText(hintText);
            tf.setOccupied(false);
        }
    }
}