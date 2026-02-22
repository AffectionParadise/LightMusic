package net.doge.ui.widget.panel;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.base.BasePanel;
import net.doge.ui.widget.panel.listener.LoadingPanelMouseListener;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description loading 面板
 * @date 2020/12/21
 */
public class LoadingPanel extends BasePanel {
    private final Font textFont = Fonts.NORMAL_BIG;

    //    private RotatableIcon icon;
    private CustomLabel label = new CustomLabel();

    private boolean closing;
    private float bgAlpha;
    private final float destBgAlpha = 0.7f;
    private Timer showTimer;
    //    private Timer rotationTimer;

    private MainFrame f;

    public LoadingPanel(MainFrame f) {
        this.f = f;
        init();
    }

    private void init() {
        setLayout(new GridLayout(1, 1));

        label.setFont(textFont);
        label.setIconTextGap(ScaleUtil.scale(20));
        add(label);

        // 显示时屏蔽底层组件的响应
        addMouseListener(new LoadingPanelMouseListener());

        // 显示和隐藏动画
        showTimer = new Timer(3, e -> {
            // 渐现渐隐效果
            float opacity = label.getOpacity();
            if (closing) {
                bgAlpha = Math.max(0f, bgAlpha - 0.02f);
                label.setOpacity(Math.max(0f, opacity - 0.028f));
                if (bgAlpha <= 0f) {
                    setVisible(false);
                    showTimer.stop();
//                rotationTimer.stop();
                }
            } else {
                bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.02f);
                label.setOpacity(Math.min(1f, opacity + 0.028f));
                if (bgAlpha >= destBgAlpha) showTimer.stop();
            }
            repaint();
        });
//        // 图标旋转动画
//        rotationTimer = new Timer(60, e -> {
//            if (icon == null) return;
//            double angle = icon.getAngle();
//            icon.setAngle((angle + 1) % 360);
//            label.repaint();
//        });
    }

    public void start() {
        if (isShowing()) return;
//        if (icon != null) icon.setAngle(0);
        label.setOpacity(0);
        setVisible(true);
        closing = false;
        showTimer.start();
//        rotationTimer.start();
    }

    public void stop() {
        closing = true;
        if (showTimer.isRunning()) return;
        showTimer.start();
    }

    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (label != null) label.setForeground(fg);
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        int pw = getWidth(), ph = getHeight();
        // 画背景
        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg()));
        GraphicsUtil.srcOver(g2d, bgAlpha);
        g2d.fillRect(0, 0, pw, ph);
    }
}
