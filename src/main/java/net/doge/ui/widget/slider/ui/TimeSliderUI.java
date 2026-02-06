package net.doge.ui.widget.slider.ui;

import javafx.scene.media.MediaPlayer;
import net.doge.constant.core.lang.I18n;
import net.doge.entity.core.player.MusicPlayer;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.dialog.TipDialog;
import net.doge.util.core.DurationUtil;
import net.doge.util.core.HtmlUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 播放时间条自定义 UI
 * @Date 2020/12/13
 */
public class TimeSliderUI extends BasicSliderUI {
    private Color thumbColor;
    private Color trackColor;
    private Color trackBgColor;
    private boolean isTimeBar;
    private MainFrame f;
    private MusicPlayer player;
    private MediaPlayer mp;
    private TipDialog dialog;
    private TipDialog lrcDialog;

    private boolean drawThumb;

    // 音频播放器
    public TimeSliderUI(JSlider slider, Color thumbColor, Color trackColor, MainFrame f, MusicPlayer player, boolean isTimeBar) {
        super(slider);
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtil.darker(trackColor);
        this.f = f;
        this.player = player;
        dialog = new TipDialog(f, 0);
        if (isTimeBar) lrcDialog = new TipDialog(f, 0);
        this.isTimeBar = isTimeBar;
    }

    // 视频播放器
    public TimeSliderUI(JSlider slider, Color thumbColor, Color trackColor, MainFrame f, MediaPlayer mp, boolean isTimeBar) {
        super(slider);
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtil.darker(trackColor);
        this.f = f;
        this.mp = mp;
        dialog = new TipDialog(f, 0);
        dialog.setAlwaysOnTop(true);
        this.isTimeBar = isTimeBar;
    }

    @Override
    protected Dimension getThumbSize() {
        return new HDDimension(12, 20);
    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {
        if (!drawThumb) return;
        Graphics2D g2d = GraphicsUtil.setup(g);

        GraphicsUtil.srcOver(g2d);
        g2d.setColor(thumbColor);
        g2d.fillOval(thumbRect.x, thumbRect.y + (thumbRect.height - thumbRect.width) / 2, thumbRect.width, thumbRect.width);
    }

    /**
     * 自定义滑道
     *
     * @param g
     */
    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        g2d.setColor(trackBgColor);
        GraphicsUtil.srcOver(g2d, isTimeBar ? 0.1f : 0.3f);
        int thx = Math.max(thumbRect.x, trackRect.x), thy = trackRect.y + ScaleUtil.scale(drawThumb ? 7 : 8), height = trackRect.height - ScaleUtil.scale(drawThumb ? 14 : 16),
                arc = ScaleUtil.scale(drawThumb ? 6 : 4);
        // 画未填充部分
        g2d.fillRoundRect(thx, thy, trackRect.width - thx + trackRect.x, height, arc, arc);
        // 在时间条画出缓冲完成的部分
        if (isTimeBar) {
            int w = mp == null ? (int) (player.getBufferedSeconds() / player.getDurationSeconds() * trackRect.width + 0.5)
                    : (int) (mp.getBufferProgressTime() == null ? 0 : mp.getBufferProgressTime().toSeconds() / mp.getMedia().getDuration().toSeconds() * trackRect.width + 0.5);
            GraphicsUtil.srcOver(g2d, 0.2f);
            g2d.fillRoundRect(thx, thy,
                    // 缓冲长度没超过已填充长度时可以不画；缓冲长度超出轨道长度时取轨道长度
                    Math.min(Math.max(w - thx + trackRect.x, 0), trackRect.width - thx + trackRect.x), height, arc, arc);
        }
        // 画已填充部分
        g2d.setColor(trackColor);
        GraphicsUtil.srcOver(g2d);
        g2d.fillRoundRect(trackRect.x, thy,
                thumbRect.x - trackRect.x + thumbRect.width / 2, height, arc, arc);
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new TrackListener() {
            private void update(MouseEvent e, boolean showDialog) {
                slider.setValueIsAdjusting(true);
                slider.setValue((int) ((double) (e.getX() - trackRect.x) / trackRect.width * slider.getMaximum()));
                if (!showDialog) return;
                if (isTimeBar) {
                    boolean isMusic = mp == null;
                    String dStr = isMusic ? player.getDurationString() : DurationUtil.format(mp.getMedia().getDuration().toSeconds());
                    double dSec = isMusic ? player.getDurationSeconds() : mp.getMedia().getDuration().toSeconds();
                    double cSec = (double) slider.getValue() / slider.getMaximum() * dSec;
                    String lrc = isMusic ? f.getTimeLrc(cSec) : "";
                    dialog.setMessage(String.format("%s / %s", DurationUtil.format(cSec), dStr));
                    if (lrcDialog != null) {
                        lrcDialog.setMessage(HtmlUtil.textToHtml(lrc));
                        lrcDialog.updateSize();
                        // 将把手坐标转为屏幕上的坐标，确定歌词对话框位置
                        Point p = new Point(thumbRect.x, thumbRect.y);
                        SwingUtilities.convertPointToScreen(p, slider);
                        lrcDialog.setLocation(p.x - lrcDialog.getWidth() / 2 + thumbRect.width / 2, p.y - lrcDialog.getHeight() - 5);
                    }
                } else dialog.setMessage(I18n.getText("volume") + slider.getValue());
                if (!dialog.isShowing()) dialog.showDialog();
                if (lrcDialog != null && lrcDialog.notEmpty() && !lrcDialog.isShowing()) lrcDialog.showDialog(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                update(e, false);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                update(e, true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                slider.setValueIsAdjusting(false);
                // 鼠标在滑动条之外且松开时不画把手
                if (!trackRect.contains(e.getPoint())) {
                    drawThumb = false;
                    slider.repaint();
                }
                if (dialog.isShowing()) dialog.close();
                if (lrcDialog != null && lrcDialog.notEmpty() && lrcDialog.isShowing()) lrcDialog.close();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // 鼠标进入时画出把手
                drawThumb = true;
                slider.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 鼠标退出时且不在拖拽状态不画把手
                if (slider.getValueIsAdjusting()) return;
                drawThumb = false;
                slider.repaint();
            }
        };
    }
}
