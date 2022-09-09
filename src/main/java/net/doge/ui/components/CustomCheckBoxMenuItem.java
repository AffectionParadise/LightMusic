package net.doge.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description 菜单项自定义 UI
 * @Date 2020/12/13
 */
public class CustomCheckBoxMenuItem extends JCheckBoxMenuItem {
    private Color foreColor;

    public CustomCheckBoxMenuItem(String text) {
        super(text);
        createBorder();
    }

    public CustomCheckBoxMenuItem(String text, boolean selected) {
        super(text, selected);
        createBorder();
    }

    private void createBorder() {
        setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 0));
    }

    public CustomCheckBoxMenuItem(Color foreColor) {
        this.foreColor = foreColor;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        Rectangle rect = getVisibleRect();
//        Graphics2D g2d = (Graphics2D) g;
//        // 避免锯齿
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setColor(Color.red);
//        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
//    }
}
