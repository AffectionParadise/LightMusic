package net.doge.ui.widget.box;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 自定义 Box
 * @date 2020/12/13
 */
public class CustomBox extends Box implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    public CustomBox(int axis) {
        super(axis);
    }

    public static CustomBox createHorizontalBox() {
        return new CustomBox(BoxLayout.X_AXIS);
    }

    public static CustomBox createVerticalBox() {
        return new CustomBox(BoxLayout.Y_AXIS);
    }

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
        GraphicsUtil.srcOver(g, extendedOpacity);
        super.paintComponent(g);
    }
}
