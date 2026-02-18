package net.doge.ui.widget.textfield.listener;

import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.core.StringUtil;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author Doge
 * @description 文本框占位文本
 * @date 2020/12/21
 */
public class TextFieldHintListener implements FocusListener {
    private String hintText;
    private CustomTextField tf;

    public TextFieldHintListener(CustomTextField tf, String hintText) {
        this.tf = tf;
        this.hintText = hintText;
    }

    @Override
    public void focusGained(FocusEvent e) {
        // 获取焦点时，清空提示内容
        if (tf.isHintHolding()) tf.setText("");
    }

    @Override
    public void focusLost(FocusEvent e) {
        // 失去焦点时，没有输入内容，显示提示内容
        String text = tf.getText();
        if (StringUtil.notEmpty(text)) return;
        toHintHoldingStatus();
    }

    public void toHintHoldingStatus() {
        tf.setHintHolding(true);
        tf.setText(hintText, false);
    }
}