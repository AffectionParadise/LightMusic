package net.doge.ui.widget.tabbedpane;

import lombok.Getter;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 自定义标签页面板
 * @Date 2020/12/13
 */
public class CustomTabbedPane extends JTabbedPane implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    public CustomTabbedPane(int tabPlacement) {
        super(tabPlacement, SCROLL_TAB_LAYOUT);
        init();
    }

    private void init() {
        setFocusable(false);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        super.setTabComponentAt(index, component);
        // 设置标签卡鼠标指针
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // 注册鼠标事件
        if (!(component instanceof CustomPanel)) return;
        CustomPanel panel = (CustomPanel) component;
        // 第一个 Tab 默认选中样式
        if (index == 0) {
            panel.setForeground(UIStyleStorage.currUIStyle.getSelectedColor());
            panel.setDrawBg(true);
        }
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (getSelectedIndex() == index) return;
                panel.setForeground(UIStyleStorage.currUIStyle.getForeColor());
                panel.transitionDrawBg(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (getSelectedIndex() == index) return;
                panel.transitionDrawBg(false);
            }

            // Panel 本身的鼠标事件会覆盖 TabbedPane 的，因此手动触发选择
            @Override
            public void mouseReleased(MouseEvent e) {
                if (getSelectedIndex() == index) return;
                setSelectedIndex(index);
            }
        });
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
