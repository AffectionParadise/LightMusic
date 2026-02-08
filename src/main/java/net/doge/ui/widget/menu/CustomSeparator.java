package net.doge.ui.widget.menu;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义菜单分隔符
 * @Date 2020/12/13
 */
public class CustomSeparator extends JSeparator implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    @Override
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        GraphicsUtil.srcOver(g2d, extendedOpacity * 0.1f);
        g2d.setColor(getForeground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
