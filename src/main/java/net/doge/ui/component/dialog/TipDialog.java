package net.doge.ui.component.dialog;

import net.doge.constant.ui.Colors;
import net.doge.constant.ui.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.dialog.factory.AbstractShadowDialog;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description 自定义淡入淡出式对话框
 * @Date 2021/1/5
 */
public class TipDialog extends AbstractShadowDialog {
    private TipDialog THIS = this;
    private Font font = Fonts.NORMAL_MEDIUM;
    private Color themeColor;
    private int ms;
    private boolean closing;

    private String message = "";
    private CustomLabel messageLabel = new CustomLabel(message);

    private Timer showtimer;
    private Timer closeTimer;

    public boolean notEmpty() {
        return StringUtil.notEmpty(StringUtil.removeHTMLLabel(message));
    }

    public void setMessage(String message) {
        this.message = message;
        messageLabel.setText(message);
        repaint();
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public TipDialog(MainFrame f, String message, int ms) {
        this(f);
        setMessage(message);
        this.ms = ms;
        initView();
    }

    public TipDialog(MainFrame f, int ms) {
        this(f);
        this.ms = ms;
        initView();
    }

    public TipDialog(MainFrame f, String message) {
        this(f);
        setMessage(message);
        this.ms = 1000;
        initView();
        // 视频播放界面的对话框需要置顶
        setAlwaysOnTop(true);
    }

    public TipDialog(MainFrame f) {
        super(f, false);
    }

    public void updateSize() {
        FontMetrics metrics = messageLabel.getFontMetrics(font);
        int sw = metrics.stringWidth(StringUtil.removeHTMLLabel(message)), sh = metrics.getHeight();
        setSize(new Dimension(sw + 60 + 2 * pixels, sh + 40 + 2 * pixels));
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
        globalPanel.setLayout(new BorderLayout());
        globalPanel.add(messageLabel, BorderLayout.CENTER);

        setContentPane(globalPanel);

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
}