package net.doge.ui.core.dimension;

import net.doge.util.ui.ScaleUtil;

import java.awt.*;

/**
 * 垂直方向适配 DPI 的高清尺寸
 */
public class VerticalHDDimension extends Dimension {
    public VerticalHDDimension(int width, int height) {
        super(width, ScaleUtil.scale(height));
    }
}
