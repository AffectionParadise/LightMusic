package net.doge.util.ui;

import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.popup.CustomComboPopup;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Doge
 * @description Swing 组件工具类
 * @date 2020/12/15
 */
public class SwingUtil {
    /**
     * 设置组件树透明度
     *
     * @param component
     * @param opacity
     */
    public static void setTreeExtendedOpacity(Component component, float opacity) {
        Queue<Component> queue = new LinkedList<>();
        queue.offer(component);
        while (!queue.isEmpty()) {
            Component comp = queue.poll();
            if (comp == null) continue;
            // 设置组件透明度
            if (comp instanceof ExtendedOpacitySupported) {
                ((ExtendedOpacitySupported) comp).setExtendedOpacity(opacity);
            }
            // 遍历容器子组件
            if (comp instanceof Container) {
                Container container = (Container) comp;
                for (Component com : container.getComponents()) queue.offer(com);
                // JList 视图需要单独处理 CellRenderer 中的组件
                if (container instanceof JList) {
                    JList list = (JList) comp;
                    ListCellRenderer renderer = list.getCellRenderer();
                    if (renderer instanceof CustomListCellRenderer) {
                        Component com = ((CustomListCellRenderer) renderer).getRootComponent();
                        queue.offer(com);
                    }
                }
                // CustomComboBox 视图需要单独处理 popup 中的组件
                else if (container instanceof CustomComboBox) {
                    CustomComboBox comboBox = (CustomComboBox) comp;
                    CustomComboPopup popup = comboBox.getPopup();
                    queue.offer(popup);
                }
            }
        }
    }

    /**
     * 获取 BorderLayout 容器的某个位置上的组件
     *
     * @param component
     * @param constraints
     */
    public static Component getBorderLayoutComponent(JComponent component, Object constraints) {
        LayoutManager layout = component.getLayout();
        if (layout instanceof BorderLayout) {
            BorderLayout borderLayout = (BorderLayout) layout;
            return borderLayout.getLayoutComponent(constraints);
        }
        return null;
    }
}