package net.doge.ui.listeners;

import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author yzx
 * @Description 控制文本框只能输入 0-255 数字的监听器
 * @Date 2021/1/10
 */
public class ColorControlInputListener extends KeyAdapter {
    private final String KEY = "0123456789" + (char) 8;
    private JTextField tf;

    public ColorControlInputListener(JTextField tf) {
        this.tf = tf;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char ch = e.getKeyChar();
        if (KEY.indexOf(ch) < 0) {
            e.consume();    // 如果不是数字则取消
            return;
        }
        String text = tf.getText() + ch;
        try {
            int i = Integer.parseInt(text);
            if (text.length() <= 3 && i >= 0 && i <= 255) return;
            e.consume();
        } catch (NumberFormatException ex) {

        }
    }
}
