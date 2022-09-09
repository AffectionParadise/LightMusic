package net.doge.ui.componentui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 列表元素标签自定义 UI
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
        Rectangle rect = textField.getVisibleRect();
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(rect.x, rect.y, rect.width - 1, rect.height - 1, 25, 25);
    }
}
