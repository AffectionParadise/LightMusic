package net.doge.ui.core.inset;

import net.doge.util.ui.ScaleUtil;

import java.awt.*;

/**
 * 适配 DPI 的 Insets
 */
public class HDInsets extends Insets {
    public HDInsets(int top, int left, int bottom, int right) {
        super(ScaleUtil.scale(top), ScaleUtil.scale(left), ScaleUtil.scale(bottom), ScaleUtil.scale(right));
    }
}
