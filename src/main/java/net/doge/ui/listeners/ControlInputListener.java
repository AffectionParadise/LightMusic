package net.doge.ui.listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author yzx
 * @Description 控制文本框只能输入数字的监听器
 * @Date 2021/1/10
 */
public class ControlInputListener extends KeyAdapter {
    private final String KEY = "0123456789" + (char) 8;

    @Override
    public void keyTyped(KeyEvent e) {
        if (KEY.indexOf(e.getKeyChar()) < 0) {
            e.consume();    // 如果不是数字则取消
        }
    }
}
