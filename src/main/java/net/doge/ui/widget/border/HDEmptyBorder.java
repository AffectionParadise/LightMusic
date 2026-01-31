package net.doge.ui.widget.border;

import net.doge.util.ui.ScaleUtil;

import javax.swing.border.EmptyBorder;

/**
 * 适配 DPI 的高清边框
 */
public class HDEmptyBorder extends EmptyBorder {
    public HDEmptyBorder() {
        this(0, 0, 0, 0);
    }

    public HDEmptyBorder(int top, int left, int bottom, int right) {
        super(ScaleUtil.scale(top), ScaleUtil.scale(left), ScaleUtil.scale(bottom), ScaleUtil.scale(right));
    }
}
