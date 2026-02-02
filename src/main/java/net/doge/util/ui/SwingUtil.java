package net.doge.util.ui;

import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @Author Doge
 * @Description Swing 组件工具类
 * @Date 2020/12/15
 */
public class SwingUtil {
    /**
     * 设置组件树透明度
     *
     * @param component
     * @param opacity
     */
    public static void setTreeExtendedOpacity(Component component, float opacity) {
        LinkedList<Component> queue = new LinkedList<>();
        queue.offer(component);
        while (!queue.isEmpty()) {
            Component comp = queue.poll();
            // 设置组件透明度
            if (comp instanceof ExtendedOpacitySupported) {
                ((ExtendedOpacitySupported) comp).setExtendedOpacity(opacity);
            }
            // 遍历容器子组件
            if (comp instanceof Container) {
                Container container = (Container) comp;
                // JList 视图需要单独处理
                if (container instanceof JList) {
                    JList list = (JList) comp;
                    ListCellRenderer renderer = list.getCellRenderer();
                    if (renderer instanceof CustomListCellRenderer) {
                        container = (Container) ((CustomListCellRenderer) list.getCellRenderer()).getRootComponent();
                    }
                }
                if (container == null) continue;
                for (Component com : container.getComponents()) queue.offer(com);
            }
        }
    }
}