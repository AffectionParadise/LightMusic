package net.doge.ui.core.resource;

import net.doge.util.ui.ScaleUtil;

import javax.swing.plaf.InsetsUIResource;

/**
 * 适配 DPI 的 UI 资源
 */
public class HDInsetsUIResource extends InsetsUIResource {
    public HDInsetsUIResource(int top, int left, int bottom, int right) {
        super(ScaleUtil.scale(top), ScaleUtil.scale(left), ScaleUtil.scale(bottom), ScaleUtil.scale(right));
    }
}
