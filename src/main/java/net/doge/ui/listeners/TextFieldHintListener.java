package net.doge.ui.listeners;

/**
 * @Author yzx
 * @Description swing 单行文本框默认文本
 * @Date 2020/12/21
 */

import lombok.Data;
import net.doge.ui.components.CustomTextField;
import net.doge.utils.ColorUtils;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Data
public class TextFieldHintListener implements FocusListener {
    private String hintText;
    private CustomTextField tf;
    private Color placeholderColor;
    private Color inputColor;

    public TextFieldHintListener(CustomTextField tf, String hintText, Color inputColor) {
        this.tf = tf;
        this.hintText = hintText;
        this.inputColor = inputColor;
        this.placeholderColor = ColorUtils.darker(inputColor);
        tf.setText(hintText);
        tf.setForeground(placeholderColor);
    }

    @Override
    public void focusGained(FocusEvent e) {
        // 获取焦点时，清空提示内容
        String temp = tf.getText();
        if (temp.equals(hintText)) {
            tf.setOccupied(true);
            tf.setText("");
        }
        tf.setForeground(inputColor);
    }

    @Override
    public void focusLost(FocusEvent e) {
        // 失去焦点时，没有输入内容，显示提示内容
        String temp = tf.getText();
        if (temp.isEmpty()) {
            tf.setOccupied(false);
            tf.setForeground(placeholderColor);
            tf.setText(hintText);
        }
    }
}