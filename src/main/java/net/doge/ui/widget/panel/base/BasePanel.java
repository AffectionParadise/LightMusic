package net.doge.ui.widget.panel.base;

import net.doge.ui.core.layout.HDFlowLayout;

import javax.swing.*;
import java.awt.*;

public class BasePanel extends JPanel {
    public BasePanel() {
        this(new HDFlowLayout());
    }

    public BasePanel(LayoutManager layoutManager) {
        super(layoutManager);
        init();
    }

    private void init() {
        setOpaque(false);
    }

    // 返回组件索引，找不到返回 -1
    public int getComponentIndex(Component comp) {
        Component[] components = getComponents();
        for (int i = 0, len = components.length; i < len; i++)
            if (components[i] == comp) return i;
        return -1;
    }
}
