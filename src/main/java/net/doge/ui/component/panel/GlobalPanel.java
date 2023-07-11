package net.doge.ui.component.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/12
 */
public class GlobalPanel extends JPanel {
    private BufferedImage lastImage;
    private BufferedImage backgroundImage;
    private float opacity;

    public GlobalPanel() {
        setOpaque(false);
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        lastImage = this.backgroundImage;
        this.backgroundImage = backgroundImage;
        this.opacity = 0;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    public float getOpacity() {
        return opacity;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage == null) return;

        int w = getWidth(), h = getHeight();
        // opacity < 1 时绘制底图，避免不必要的操作占用 CPU！
        Graphics2D g2d = (Graphics2D) g;
        if (lastImage != null) {
            if (opacity < 1) {
                // 宽高设置为组件的宽高，observer 设置成组件就可以自适应
                g2d.drawImage(lastImage, 0, 0, w, h, this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            } else lastImage = null;
        }
        g2d.drawImage(backgroundImage, 0, 0, w, h, this);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}