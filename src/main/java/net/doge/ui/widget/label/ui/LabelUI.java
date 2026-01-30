package net.doge.ui.widget.label.ui;

import lombok.Setter;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 列表元素标签自定义 UI
 * @Date 2020/12/13
 */
@Setter
public class LabelUI extends BasicLabelUI {
    private float alpha;
    private boolean drawBg;

    public LabelUI(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        if (drawBg) {
            // 画背景
            g2d.setColor(c.getForeground());
            GraphicsUtil.srcOver(g2d, 0.1f);
            g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
        }
        GraphicsUtil.srcOver(g2d, alpha);
        super.paint(g, c);
    }
}
