package net.doge.ui.componentui;

import net.doge.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 静默的进度条自定义 UI
 * @Date 2020/12/13
 */
public class MuteSliderUI extends BasicSliderUI {
    private Color trackColor;
    private Color trackBgColor;

    public MuteSliderUI(JSlider slider, Color trackColor) {
        super(slider);
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtils.darker(trackColor);
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
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画未填充部分
        g2d.setColor(trackBgColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        int thx = Math.max(thumbRect.x, trackRect.x), thy = trackRect.y + 7, height = trackRect.height - 14, arc = 6;
        g2d.fillRoundRect(thx, thy, trackRect.width - thx + trackRect.x, height, arc, arc);
        // 画已填充部分
        g2d.setColor(trackColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.fillRoundRect(
                trackRect.x,
                thy,
                thumbRect.x - trackRect.x + thumbRect.width / 2,
                height, arc, arc
        );
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return null;
    }
}
