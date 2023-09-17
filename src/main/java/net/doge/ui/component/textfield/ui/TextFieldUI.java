package net.doge.ui.component.textfield.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 文本框自定义 UI
 * @Date 2020/12/13
 */
public class TextFieldUI extends BasicTextFieldUI {
    private JTextField textField;
    private Color backgroundColor;

    public TextFieldUI(JTextField textField, Color backgroundColor) {
        this.textField = textField;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void paintBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, textField.getWidth(), textField.getHeight(), 25, 25);
    }
}
