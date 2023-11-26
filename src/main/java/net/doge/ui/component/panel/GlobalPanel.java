package net.doge.ui.component.panel;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/12
 */
public class GlobalPanel extends JPanel {
    private BufferedImage lastImage;
    @Getter
    private BufferedImage backgroundImage;
    @Getter
    private float opacity;
    private float scale;

    public GlobalPanel() {
        setOpaque(false);
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        lastImage = this.backgroundImage;
        this.backgroundImage = backgroundImage;
        this.opacity = 0;
        this.scale = 1;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    public void setScale(float scale) {
        this.scale = scale;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage == null) return;

        int pw = getWidth(), ph = getHeight();
        int w = (int) (pw * scale), h = (int) (ph * scale), x = (pw - w) / 2, y = (ph - h) / 2;
        // opacity < 1 时绘制底图，避免不必要的操作占用 CPU！
        Graphics2D g2d = (Graphics2D) g;
        if (lastImage != null) {
            if (opacity < 1) {
                // 宽高设置为组件的宽高，observer 设置成组件就可以自适应
                g2d.drawImage(lastImage, x, y, w, h, this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            } else lastImage = null;
        }
        g2d.drawImage(backgroundImage, x, y, w, h, this);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
