package net.doge.ui.widget.slider.ui;

import net.doge.ui.widget.dialog.ColorChooserDialog;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 颜色滑动条自定义 UI
 * @Date 2020/12/13
 */
public class ColorSliderUI extends BasicSliderUI {
    private ColorChooserDialog d;

    private final float[] ratios = {0, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f, 1};
    private final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.PINK, Color.RED};

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
        g2d.setColor(d.makeColorFromRgb(d.r, d.g, d.b));
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
        Color c1, c2;
        if (d.isRGB()) {
            c1 = slider == d.rSlider ? d.makeColorFromRgb(0, d.g, d.b) : slider == d.gSlider ? d.makeColorFromRgb(d.r, 0, d.b) : d.makeColorFromRgb(d.r, d.g, 0);
            c2 = slider == d.rSlider ? d.makeColorFromRgb(d.max1, d.g, d.b) : slider == d.gSlider ? d.makeColorFromRgb(d.r, d.max2, d.b) : d.makeColorFromRgb(d.r, d.g, d.max3);
        } else if (d.isHSV()) {
            c1 = slider == d.rSlider ? d.makeColorFromHsv(0, d.s, d.v) : slider == d.gSlider ? d.makeColorFromHsv(d.h, 0, d.v) : d.makeColorFromHsv(d.h, d.s, 0);
            c2 = slider == d.rSlider ? d.makeColorFromHsv(d.max1, d.s, d.v) : slider == d.gSlider ? d.makeColorFromHsv(d.h, d.max2, d.v) : d.makeColorFromHsv(d.h, d.s, d.max3);
        } else {
            c1 = slider == d.rSlider ? d.makeColorFromHsl(0, d.ns, d.nl) : slider == d.gSlider ? d.makeColorFromHsl(d.nh, 0, d.nl) : d.makeColorFromHsl(d.nh, d.ns, 0);
            c2 = slider == d.rSlider ? d.makeColorFromHsl(d.max1, d.ns, d.nl) : slider == d.gSlider ? d.makeColorFromHsl(d.nh, d.max2, d.nl) : d.makeColorFromHsl(d.nh, d.ns, d.max3);
        }
        if (!d.isRGB() && slider == d.rSlider) {
            LinearGradientPaint lgp = new LinearGradientPaint(trackRect.x, trackRect.y, trackRect.x + trackRect.width, trackRect.y, ratios, colors);
            g2d.setPaint(lgp);
        } else {
            GradientPaint paint = new GradientPaint(trackRect.x, trackRect.y, c1, trackRect.x + trackRect.width, trackRect.y, c2);
            g2d.setPaint(paint);
        }
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
