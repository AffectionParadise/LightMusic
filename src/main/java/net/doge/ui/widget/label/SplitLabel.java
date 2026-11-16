package net.doge.ui.widget.label;

import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

public class SplitLabel extends CustomLabel {
    // 分割线图标
    private ImageIcon splitIcon = LMIconManager.getIcon("title.split");

    public SplitLabel() {
        init();
    }

    private void init() {
        setIcon(splitIcon);
        // 最佳大小
        Dimension d = new Dimension(splitIcon.getIconWidth() + ScaleUtil.scale(10), splitIcon.getIconHeight() + ScaleUtil.scale(10));
        setPreferredSize(d);
    }
}
