package net.doge.ui.widget.panel;

import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @Author Doge
 * @Description loading 面板
 * @Date 2020/12/21
 */
public class LoadingPanel extends CustomPanel implements MouseListener {
    private final Font textFont = Fonts.NORMAL_BIG;

    //    private RotatableIcon icon;
    private CustomLabel label = new CustomLabel();

    private boolean closing;
    private float bgAlpha;
    private Timer showTimer;
    //    private Timer rotationTimer;

    private MainFrame f;

    public LoadingPanel(MainFrame f) {
        this.f = f;

        setLayout(new GridLayout(1, 1));

        label.setFont(textFont);
        label.setIconTextGap(ScaleUtil.scale(20));
        add(label);

        // 显示时屏蔽底层组件的响应
        addMouseListener(this);

        // 显示和隐藏动画
        showTimer = new Timer(3, e -> {
            // 渐现渐隐效果
            float alpha = label.getOpacity();
            if (closing) {
                bgAlpha = Math.max(0, bgAlpha - 0.02f);
                label.setOpacity(Math.max(0, alpha - 0.028f));
            } else {
                bgAlpha = Math.min(0.7f, bgAlpha + 0.02f);
                label.setOpacity(Math.min(1f, alpha + 0.028f));
            }
            repaint();
            if (!closing && bgAlpha >= 0.7f) showTimer.stop();
            else if (closing && bgAlpha <= 0) {
                setVisible(false);
                showTimer.stop();
//                rotationTimer.stop();
            }
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

    public void setForeColor(Color color) {
        label.setForeground(color);
    }

    public void setIcon(ImageIcon icon) {
        label.setIcon(icon);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        int pw = getWidth(), ph = getHeight();

        // 画背景
        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg(), bgAlpha, false));
        g2d.fillRect(0, 0, pw, ph);

        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
