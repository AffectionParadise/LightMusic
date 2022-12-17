package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 自定义淡入淡出式对话框
 * @Date 2021/1/5
 */
public class TipDialog extends JDialog {
    private TipDialog THIS = this;
    private Font font = Fonts.NORMAL_MEDIUM;
    private Color themeColor;
    private int ms;
    private boolean closing;

    private PlayerFrame f;
    private String message = "";
    private CustomLabel messageLabel = new CustomLabel(message);
    private UndergroundPanel mainPanel = new UndergroundPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    private Timer showtimer;
    private Timer closeTimer;

    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(StringUtils.removeHTMLLabel(message));
    }

    public void setMessage(String message) {
        this.message = message;
        messageLabel.setText(message);
        repaint();
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public TipDialog(PlayerFrame f, String message, int ms) {
        this(f);
        setMessage(message);
        this.ms = ms;
        initView();
    }

    public TipDialog(PlayerFrame f, int ms) {
        this(f);
        this.ms = ms;
        initView();
    }

    public TipDialog(PlayerFrame f, String message) {
        this(f);
        setMessage(message);
        this.ms = 1000;
        initView();
        // 视频播放界面的对话框需要置顶
        setAlwaysOnTop(true);
    }

    public TipDialog(PlayerFrame f) {
        super(f);
        this.f = f;
    }

    public void updateSize() {
        FontMetrics metrics = messageLabel.getFontMetrics(font);
        int sw = metrics.stringWidth(StringUtils.removeHTMLLabel(message)), sh = metrics.getHeight();
        setSize(new Dimension(sw + 60 + 2 * pixels, sh + 40 + 2 * pixels));
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.blurType != BlurType.OFF && f.player.loadedMusic()) {
            bufferedImage = f.player.getMusicInfo().getAlbumImage();
            if (bufferedImage == f.defaultAlbumImage) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG)
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.currUIStyle;
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    public void updateView(boolean resetLocation) {
        updateSize();
        updateBlur();
        if (resetLocation) setLocationRelativeTo(null);
    }

    private void initView() {
        // 设置主题色
        themeColor = f.currUIStyle.getTextColor();
        setUndecorated(true);
        // Dialog 背景透明
        setBackground(Colors.TRANSLUCENT);

        messageLabel.setForeground(themeColor);
        messageLabel.setFont(font);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(messageLabel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        initTimer();
    }

    private void initTimer() {
        showtimer = new Timer(2, e -> {
            // 渐隐效果
            float opacity = getOpacity();
            if (closing) opacity = Math.max(0, opacity - 0.02f);
            else opacity = Math.min(1, opacity + 0.02f);
            setOpacity(opacity);
            if (closing && opacity <= 0 || !closing && opacity >= 1) {
                showtimer.stop();
                if (closing) {
                    f.currDialogs.remove(THIS);
                    dispose();
                } else if (ms > 0) {
                    closeTimer.start();
                }
            }
        });
        // 停留时间
        closeTimer = new Timer(ms, ev -> {
            close();
            closeTimer.stop();
        });
    }

    public void showDialog() {
        showDialog(true);
    }

    public void showDialog(boolean resetLocation) {
        updateView(resetLocation);
        f.currDialogs.add(this);
        setOpacity(0);
        setVisible(true);
        closing = false;
        showtimer.start();
    }

    public void close() {
        closing = true;
        showtimer.start();
    }

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.player.loadedMusic();
            // 截取中间的一部分(有的图片是长方形)
            if (loadedMusic && f.blurType == BlurType.CV) bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            if (f.gsOn) bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            if (f.gsOn) bufferedImage = ImageUtils.doBlur(bufferedImage);
            if (f.darkerOn) bufferedImage = ImageUtils.darker(bufferedImage);
            // 放大至窗口大小
            bufferedImage = ImageUtils.width(bufferedImage, dw);
            if (dh > bufferedImage.getHeight())
                bufferedImage = ImageUtils.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (!loadedMusic || f.blurType == BlurType.CV || f.blurType == BlurType.OFF) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtils.forceSize(bufferedImage, dw, dh);
            }
            // 设置圆角
            bufferedImage = ImageUtils.setRadius(bufferedImage, 10);
            mainPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class UndergroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public UndergroundPanel() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }
//            g2d.setColor(ImageUtils.getAvgRGB(f.desktopLyricDialog.getBackgroundImage()));
//            g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, 10, 10);

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
        }
    }
}