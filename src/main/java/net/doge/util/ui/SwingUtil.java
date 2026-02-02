package net.doge.util.ui;

import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;

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
        if (component instanceof ExtendedOpacitySupported) {
            ((ExtendedOpacitySupported) component).setExtendedOpacity(opacity);
        }
        // 遍历容器子组件
        if (component instanceof Container) {
            Container inner = (Container) component;
            // JList 视图需要单独处理
            if (inner instanceof JList) {
                JList list = (JList) component;
                ListCellRenderer renderer = list.getCellRenderer();
                if (renderer instanceof CustomListCellRenderer) {
                    inner = (Container) ((CustomListCellRenderer) list.getCellRenderer()).getRootComponent();
                }
            }
            if (inner == null) return;
            for (Component comp : inner.getComponents()) {
                setTreeExtendedOpacity(comp, opacity);
            }
        }
    }
}