package net.doge.ui.componentui;

import net.doge.ui.components.dialog.ColorChooserDialog;
import net.doge.utils.ColorUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 颜色滑动条自定义 UI
 * @Date 2020/12/13
 */
public class ColorSliderUI extends BasicSliderUI {
    private ColorChooserDialog d;

    public ColorSliderUI(JSlider slider, ColorChooserDialog d) {
        super(slider);
        this.d = d;
    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(new Color(d.r, d.g, d.b));
        g2d.fillOval(thumbRect.x, thumbRect.y + 4, thumbRect.width, thumbRect.width);
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
        Color c1 = slider == d.rSlider ? new Color(0, d.g, d.b) : slider == d.gSlider ? new Color(d.r, 0, d.b) : new Color(d.r, d.g, 0);
        Color c2 = slider == d.rSlider ? new Color(255, d.g, d.b) : slider == d.gSlider ? new Color(d.r, 255, d.b) : new Color(d.r, d.g, 255);
        GradientPaint paint = new GradientPaint(trackRect.x, trackRect.y, c1, trackRect.x + trackRect.width, trackRect.y, c2);
        g2d.setPaint(paint);
        g2d.fillRoundRect(
                trackRect.x,
                trackRect.y + 8,
                trackRect.width,
                trackRect.height - 16, 4, 4
        );
    }

    @Override
    protected TrackListener createTrackListener(JSlider s) {
        return new TrackListener() {
            private void update(MouseEvent e) {
                slider.setValue((int) ((double) (e.getX() - trackRect.x) / trackRect.width * slider.getMaximum()));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                update(e);
            }

            // 拖动时重绘，避免滑块重叠绘制！
            @Override
            public void mouseDragged(MouseEvent e) {
                update(e);
            }
        };
    }
}
