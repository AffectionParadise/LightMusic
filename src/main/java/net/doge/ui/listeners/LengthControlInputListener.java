package net.doge.ui.listeners;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author yzx
 * @Description 控制文本框只能输入 0-255 数字的监听器
 * @Date 2021/1/10
 */
public class LengthControlInputListener extends KeyAdapter {
    private int length;
    private JTextField tf;

    public LengthControlInputListener(JTextField tf, int length) {
        this.tf = tf;
        this.length = length;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (tf.getText().length() + 1 <= length) return;
        e.consume();
    }
}
