package net.doge.ui.widget.slider.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 垂直滑动条自定义 UI
 * @Date 2020/12/13
 */
public class VSliderUI extends BasicSliderUI {
    private Color thumbColor;
    private Color trackColor;

    public VSliderUI(JSlider slider, Color thumbColor, Color trackColor) {
        super(slider);
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
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
        g2d.setColor(thumbColor);
        g2d.fillOval(thumbRect.x + 4, thumbRect.y - 2, thumbRect.width - 4, thumbRect.width - 4);
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
        g2d.setColor(trackColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.fillRoundRect(
                trackRect.x + 9,
                trackRect.y,
                trackRect.width - 14,
                thumbRect.y - trackRect.y + thumbRect.height / 2, 6, 6
        );
        // 画已填充部分
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.fillRoundRect(
                trackRect.x + 9,
                thumbRect.y + thumbRect.height / 2,
                trackRect.width - 14,
                trackRect.height - thumbRect.y + trackRect.y - thumbRect.height / 2, 6, 6
        );
    }

    @Override
    protected TrackListener createTrackListener(JSlider s) {
        return new TrackListener() {
            private void update(MouseEvent e) {
                s.setValue((int) (s.getMinimum() + (double) (trackRect.height - e.getY() + trackRect.y) / trackRect.height * (s.getMaximum() - s.getMinimum())));
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
