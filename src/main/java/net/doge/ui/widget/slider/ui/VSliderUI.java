package net.doge.ui.widget.slider.ui;

import net.doge.ui.core.dimension.HDDimension;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

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

    @Override
    protected Dimension getThumbSize() {
        return new HDDimension(20, 16);
    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        GraphicsUtil.srcOver(g2d);
        g2d.setColor(thumbColor);
        g2d.fillOval(thumbRect.x + (thumbRect.width - thumbRect.height) / 2 + 2, thumbRect.y, thumbRect.height, thumbRect.height);
    }

    /**
     * 自定义滑道
     *
     * @param g
     */
    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        // 画未填充部分
        g2d.setColor(trackColor);
        GraphicsUtil.srcOver(g2d, 0.3f);
        int arc = ScaleUtil.scale(6);
        g2d.fillRoundRect(trackRect.x + ScaleUtil.scale(9), trackRect.y,
                trackRect.width - ScaleUtil.scale(14), thumbRect.y - trackRect.y + thumbRect.height / 2, arc, arc);
        // 画已填充部分
        GraphicsUtil.srcOver(g2d);
        g2d.fillRoundRect(trackRect.x + ScaleUtil.scale(9), thumbRect.y + thumbRect.height / 2,
                trackRect.width - ScaleUtil.scale(14), trackRect.height - thumbRect.y + trackRect.y - thumbRect.height / 2, arc, arc);
    }

    @Override
    protected TrackListener createTrackListener(JSlider s) {
        return new TrackListener() {
            private void update(MouseEvent e) {
                int min = s.getMinimum(), max = s.getMaximum();
                s.setValue((int) (min + (double) (trackRect.height - e.getY() + trackRect.y) / trackRect.height * (max - min)));
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
