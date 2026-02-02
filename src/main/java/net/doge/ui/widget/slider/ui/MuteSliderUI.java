package net.doge.ui.widget.slider.ui;

import net.doge.ui.widget.slider.CustomSlider;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

/**
 * @Author Doge
 * @Description 静默的进度条自定义 UI
 * @Date 2020/12/13
 */
public class MuteSliderUI extends BasicSliderUI {
    private Color trackColor;
    private Color trackBgColor;

    // 休息模式，只画一个透明的满条
    private boolean rest;

    public MuteSliderUI(JSlider slider, Color trackColor) {
        super(slider);
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtil.darker(trackColor);
    }

    public void setRest(boolean rest) {
        this.rest = rest;
        slider.repaint();
    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {

    }

    /**
     * 自定义滑道
     *
     * @param g
     */
    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        CustomSlider sli = (CustomSlider) slider;
        float extendedOpacity = sli.getExtendedOpacity();
        g2d.setColor(trackBgColor);
        int thx = Math.max(thumbRect.x, trackRect.x), thy = trackRect.y + ScaleUtil.scale(5), height = trackRect.height - ScaleUtil.scale(10), arc = ScaleUtil.scale(6);
        // 画未填充部分
        if (!rest) {
            GraphicsUtil.srcOver(g2d, extendedOpacity * 0.2f);
            g2d.fillRoundRect(thx, thy, trackRect.width - thx + trackRect.x, height, arc, arc);
        }
        // 画已填充部分
        g2d.setColor(trackColor);
        GraphicsUtil.srcOver(g2d, extendedOpacity * (rest ? 0.5f : 1f));
        g2d.fillRoundRect(trackRect.x, thy, thumbRect.x - trackRect.x + thumbRect.width / 2, height, arc, arc);
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return null;
    }
}
