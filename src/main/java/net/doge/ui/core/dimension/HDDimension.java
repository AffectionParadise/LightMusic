package net.doge.ui.core.dimension;

import net.doge.util.ui.ScaleUtil;

import java.awt.*;

/**
 * 适配 DPI 的高清尺寸
 */
public class HDDimension extends Dimension {
    public HDDimension(int width, int height) {
        super(ScaleUtil.scale(width), ScaleUtil.scale(height));
    }
}
