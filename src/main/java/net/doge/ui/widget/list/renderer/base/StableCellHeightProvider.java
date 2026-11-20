package net.doge.ui.widget.list.renderer.base;

import javax.swing.*;

/**
 * 获取 CellRenderer 的稳定高度
 */
public interface StableCellHeightProvider {
    int getStableCellHeight(JList<?> list, Object value, int index, int width);
}
