package net.doge.ui.widget.tabbedpane;

import lombok.Getter;
import net.doge.ui.widget.base.OpacitySupported;
import net.doge.util.ui.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义标签页面板
 * @Date 2020/12/13
 */
public class CustomTabbedPane extends JTabbedPane implements OpacitySupported {
    @Getter
    private float opacity = 1f;

    public CustomTabbedPane(int tabPlacement) {
        super(tabPlacement, SCROLL_TAB_LAYOUT);
        setFocusable(false);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        super.setTabComponentAt(index, component);
        // 设置标签卡鼠标指针
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
