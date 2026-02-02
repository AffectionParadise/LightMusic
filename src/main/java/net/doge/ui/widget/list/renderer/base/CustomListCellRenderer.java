package net.doge.ui.widget.list.renderer.base;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义 list 单元格
 */
public abstract class CustomListCellRenderer extends DefaultListCellRenderer {
    // 获取根组件
    public abstract Component getRootComponent();
}
