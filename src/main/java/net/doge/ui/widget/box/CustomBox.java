package net.doge.ui.widget.box;

import lombok.Getter;
import net.doge.ui.widget.base.OpacitySupported;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义 Box
 * @Date 2020/12/13
 */
public class CustomBox extends Box implements OpacitySupported {
    @Getter
    private float opacity = 1f;

    public CustomBox(int axis) {
        super(axis);
    }

    public static CustomBox createHorizontalBox() {
        return new CustomBox(BoxLayout.X_AXIS);
    }

    public static CustomBox createVerticalBox() {
        return new CustomBox(BoxLayout.Y_AXIS);
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    @Override
    protected void paintChildren(Graphics g) {
        GraphicsUtil.srcOver(g, opacity);
        super.paintChildren(g);
    }
}
