package net.doge.ui.widget.dialog;

import lombok.Setter;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.dialog.factory.AbstractShadowDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.util.common.HtmlUtil;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义淡入淡出式对话框
 * @Date 2021/1/5
 */
public class TipDialog extends AbstractShadowDialog {
    private TipDialog THIS = this;
    private Font font = Fonts.NORMAL_MEDIUM;
    private Color themeColor;
    @Setter
    private int ms;
    private boolean closing;

    private String message = "";
    private CustomLabel messageLabel = new CustomLabel(message);

    private Timer showTimer;
    private Timer closeTimer;

    public TipDialog(MainFrame f, int ms) {
        this(f, null, ms);
    }

    public TipDialog(MainFrame f, String message) {
        this(f, message, false);
    }

    public TipDialog(MainFrame f, String message, boolean onTop) {
        this(f, message, 1000, onTop);
    }

    public TipDialog(MainFrame f, String message, int ms) {
        this(f, message, ms, false);
    }

    public TipDialog(MainFrame f, String message, int ms, boolean onTop) {
        super(f, false);
        setMessage(message);
        this.ms = ms;
        initView();
        setAlwaysOnTop(onTop);
    }

    public boolean notEmpty() {
        return StringUtil.notEmpty(HtmlUtil.removeHtmlLabel(message));
    }

    public void setMessage(String message) {
        this.message = message;
        messageLabel.setText(message);
        repaint();
    }

    public void updateSize() {
        FontMetrics metrics = messageLabel.getFontMetrics(font);
        int sw = metrics.stringWidth(HtmlUtil.removeHtmlLabel(message)), sh = metrics.getHeight();
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
        setBackground(Colors.TRANSPARENT);

        messageLabel.setForeground(themeColor);
        messageLabel.setFont(font);
        globalPanel.setLayout(new BorderLayout());
        globalPanel.add(messageLabel, BorderLayout.CENTER);

        setContentPane(globalPanel);

        initTimer();
    }

    private void initTimer() {
        showTimer = new Timer(2, e -> {
            // 渐隐效果
            float opacity = getOpacity();
            if (closing) opacity = Math.max(0, opacity - 0.02f);
            else opacity = Math.min(1, opacity + 0.02f);
            setOpacity(opacity);
            if (closing && opacity <= 0 || !closing && opacity >= 1) {
                showTimer.stop();
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
        showTimer.start();
    }

    public void close() {
        closing = true;
        showTimer.start();
    }
}