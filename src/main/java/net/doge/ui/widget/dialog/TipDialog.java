package net.doge.ui.widget.dialog;

import lombok.Setter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.dialog.base.AbstractShadowDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.util.core.StringUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 自定义淡入淡出式对话框
 * @date 2021/1/5
 */
public class TipDialog extends AbstractShadowDialog {
    private final Font font = Fonts.NORMAL_MEDIUM;
    @Setter
    private int ms;
    private boolean closing;

    private String message = "";
    private CustomLabel messageLabel = new CustomLabel(message);

    private Timer opacityTimer;
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
        setSize(new Dimension(sw + ScaleUtil.scale(60) + 2 * pixels, sh + ScaleUtil.scale(40) + 2 * pixels));
    }

    public void updateView(boolean resetLocation) {
        updateSize();
        updateBlur();
        if (resetLocation) setLocationRelativeTo(null);
    }

    private void initView() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        messageLabel.setForeground(textColor);
        messageLabel.setFont(font);
        globalPanel.add(messageLabel, BorderLayout.CENTER);

        initTimer();
    }

    private void initTimer() {
        opacityTimer = new Timer(2, e -> {
            // 渐隐效果
            float opacity = getOpacity();
            if (closing) opacity = Math.max(0f, opacity - 0.02f);
            else opacity = Math.min(1f, opacity + 0.02f);
            setOpacity(opacity);
            if (closing && opacity <= 0f || !closing && opacity >= 1f) {
                opacityTimer.stop();
                if (closing) close();
                else if (ms > 0) closeTimer.start();
            }
        });
        // 停留时间
        closeTimer = new Timer(ms, ev -> {
            transitionClose();
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
        opacityTimer.start();
    }

    public void transitionClose() {
        closing = true;
        opacityTimer.start();
    }
}