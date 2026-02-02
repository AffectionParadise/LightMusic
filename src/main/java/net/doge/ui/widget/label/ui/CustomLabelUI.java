package net.doge.ui.widget.label.ui;

import lombok.Setter;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 列表元素标签自定义 UI
 * @Date 2020/12/13
 */
@Setter
public class CustomLabelUI extends BasicLabelUI {
    private float opacity;
    private boolean drawBg;

    public CustomLabelUI(float opacity) {
        this.opacity = opacity;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        if (drawBg) {
            // 画背景
            g2d.setColor(c.getForeground());
            GraphicsUtil.srcOver(g2d, 0.1f);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);
        }
        GraphicsUtil.srcOver(g2d, opacity);
        super.paint(g, c);
    }
}
