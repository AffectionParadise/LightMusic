package net.doge.ui.componentui.slider;

import javafx.scene.media.MediaPlayer;
import net.doge.model.player.MusicPlayer;
import net.doge.ui.MainFrame;
import net.doge.ui.component.dialog.TipDialog;
import net.doge.util.ui.ColorUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 播放时间条自定义 UI
 * @Date 2020/12/13
 */
public class SliderUI extends BasicSliderUI {
    private Color thumbColor;
    private Color trackColor;
    private Color trackBgColor;
    private boolean isTimeBar;
    private MainFrame f;
    private MusicPlayer player;
    private MediaPlayer mp;
    private TipDialog dialog;
    private TipDialog lrcDialog;

//    private int bufferedGap;
//    private int currBufferedWidth;
//    private int originBufferedWidth;
//    private Timer bufferTimer;

    private boolean drawThumb;

//    private final BufferedImage hxhImg = ImageUtil.read(SimplePath.ICON_PATH + "hxh.png");

    // 音频播放器
    public SliderUI(JSlider slider, Color thumbColor, Color trackColor, MainFrame f, MusicPlayer player, boolean isTimeBar) {
        super(slider);
        this.thumbColor = thumbColor;
//        trackColor = Colors.HXH;
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtil.darker(trackColor);
        this.f = f;
        this.player = player;
        dialog = new TipDialog(f, 0);
        if (isTimeBar) lrcDialog = new TipDialog(f, 0);
        this.isTimeBar = isTimeBar;

//        initTimer();
    }

    // 视频播放器
    public SliderUI(JSlider slider, Color thumbColor, Color trackColor, MainFrame f, MediaPlayer mp, boolean isTimeBar) {
        super(slider);
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
        this.trackBgColor = ColorUtil.darker(trackColor);
        this.f = f;
        this.mp = mp;
        dialog = new TipDialog(f, 0);
        dialog.setAlwaysOnTop(true);
        this.isTimeBar = isTimeBar;

//        initTimer();
    }

//    // 初始化动画 Timer
//    private void initTimer() {
//        bufferTimer = new Timer(0, e -> {
//            if (currBufferedWidth < originBufferedWidth)
//                currBufferedWidth = Math.min(currBufferedWidth + bufferedGap, originBufferedWidth);
//            else if (currBufferedWidth > originBufferedWidth)
//                currBufferedWidth = Math.max(currBufferedWidth - bufferedGap, originBufferedWidth);
//            else bufferTimer.stop();
//            slider.repaint();
//        });
//    }

//    // 更新缓冲长度
//    public void updateBufferedWidth() {
//        originBufferedWidth = mp == null ? (int) (player.getBufferedSeconds() / player.getDurationSeconds() * trackRect.width + 0.5)
//                : (int) (mp.getBufferProgressTime() == null ? 0 : mp.getBufferProgressTime().toSeconds() / mp.getMedia().getDuration().toSeconds() * trackRect.width + 0.5);
//        bufferedGap = 1;
//        if (!bufferTimer.isRunning()) bufferTimer.start();
//    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {
        if (!drawThumb) return;
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(thumbColor);
        g2d.fillOval(thumbRect.x, thumbRect.y + 4, 12, 12);
//        g2d.drawImage(hxhImg, thumbRect.x, thumbRect.y, null);
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
        g2d.setColor(trackBgColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, isTimeBar ? 0.1f : 0.3f));
        int thx = Math.max(thumbRect.x, trackRect.x), thy = trackRect.y + (drawThumb ? 7 : 8), height = trackRect.height - (drawThumb ? 14 : 16),
                arc = drawThumb ? 6 : 4;
        // 画未填充部分
        g2d.fillRoundRect(thx, thy, trackRect.width - thx + trackRect.x, height, arc, arc);
        // 在时间条画出缓冲完成的部分
        if (isTimeBar) {
            int w = mp == null ? (int) (player.getBufferedSeconds() / player.getDurationSeconds() * trackRect.width + 0.5)
                    : (int) (mp.getBufferProgressTime() == null ? 0 : mp.getBufferProgressTime().toSeconds() / mp.getMedia().getDuration().toSeconds() * trackRect.width + 0.5);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2d.fillRoundRect(
                    thx,
                    thy,
                    // 缓冲长度没超过已填充长度时可以不画；缓冲长度超出轨道长度时取轨道长度
                    Math.min(Math.max(w - thx + trackRect.x, 0), trackRect.width - thx + trackRect.x),
                    height, arc, arc
            );
        }
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
        return new TrackListener() {
            private void update(MouseEvent e, boolean showDialog) {
                slider.setValueIsAdjusting(true);
                slider.setValue((int) ((double) (e.getX() - trackRect.x) / trackRect.width * slider.getMaximum()));
                if (!showDialog) return;
                if (isTimeBar) {
                    boolean isMusic = mp == null;
                    String dStr = isMusic ? player.getDurationString() : TimeUtil.format(mp.getMedia().getDuration().toSeconds());
                    double dSec = isMusic ? player.getDurationSeconds() : mp.getMedia().getDuration().toSeconds();
                    double cSec = (double) slider.getValue() / slider.getMaximum() * dSec;
                    String lrc = isMusic ? f.getTimeLrc(cSec) : "";
                    dialog.setMessage(String.format("%s / %s", TimeUtil.format(cSec), dStr));
                    if (lrcDialog != null) {
                        lrcDialog.setMessage(StringUtil.textToHtml(lrc));
                        lrcDialog.updateSize();
                        // 将把手坐标转为屏幕上的坐标，确定歌词对话框位置
                        Point p = new Point(thumbRect.x, thumbRect.y);
                        SwingUtilities.convertPointToScreen(p, slider);
                        lrcDialog.setLocation(p.x - lrcDialog.getWidth() / 2 + thumbRect.width / 2, p.y - lrcDialog.getHeight() - 5);
                    }
                } else dialog.setMessage("音量：" + slider.getValue());
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
