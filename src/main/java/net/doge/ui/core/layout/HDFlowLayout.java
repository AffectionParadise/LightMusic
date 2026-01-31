package net.doge.ui.core.layout;

import net.doge.util.ui.ScaleUtil;

import java.awt.*;

/**
 * 适配 DPI 的流式布局
 */
public class HDFlowLayout extends FlowLayout {
    public HDFlowLayout() {
        this(CENTER, 5, 5);
    }

    public HDFlowLayout(int align) {
        this(align, 5, 5);
    }

    public HDFlowLayout(int align, int hGap, int vGap) {
        super(align, ScaleUtil.scale(hGap), ScaleUtil.scale(vGap));
    }
}
