package net.doge.ui.widget.panel;

import lombok.Getter;
import lombok.Setter;
import net.doge.constant.ui.SlideFrom;

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

    private Timer slideTimer;
    @Setter
    private Runnable onAfterSlide;

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

    // 是否在滑动动画状态
    public boolean isSlideAnimating() {
        return slideTimer != null && slideTimer.isRunning();
    }

    // 组件滑入滑出替换
    public void slideFrom(Component srcComp, Component targetComp, int from) {
        // 不存在该组件，跳出
        if (getComponentIndex(srcComp) < 0) return;
        Rectangle srcBounds = srcComp.getBounds(), gBounds = getBounds();
        LayoutManager layout = getLayout();
        // 根据窗口高度界定动画速率
        int SLIDE_STEP_H = (int) (gBounds.width * 0.043), SLIDE_STEP_V = (int) (gBounds.height * 0.032);
        slideTimer = new Timer(1, e -> {
            if (from == SlideFrom.TOP) {
                srcComp.setLocation(srcBounds.x, Math.min(gBounds.y + gBounds.height, srcComp.getY() + SLIDE_STEP_V));
                targetComp.setLocation(srcBounds.x, Math.min(srcBounds.y, targetComp.getY() + SLIDE_STEP_V));
            } else if (from == SlideFrom.BOTTOM) {
                srcComp.setLocation(srcBounds.x, Math.max(gBounds.y - srcBounds.height, srcComp.getY() - SLIDE_STEP_V));
                targetComp.setLocation(srcBounds.x, Math.max(srcBounds.y, targetComp.getY() - SLIDE_STEP_V));
            }
            // 左右滑入存在性能问题，由于 jlist 区域刷新耗时，慎用！
            else if (from == SlideFrom.LEFT) {
                srcComp.setLocation(Math.min(gBounds.x + gBounds.width, srcComp.getX() + SLIDE_STEP_H), srcBounds.y);
                targetComp.setLocation(Math.min(srcBounds.x, targetComp.getX() + SLIDE_STEP_H), srcBounds.y);
            } else if (from == SlideFrom.RIGHT) {
                srcComp.setLocation(Math.max(gBounds.x - srcBounds.width, srcComp.getX() - SLIDE_STEP_H), srcBounds.y);
                targetComp.setLocation(Math.max(srcBounds.x, targetComp.getX() - SLIDE_STEP_H), srcBounds.y);
            }
            repaint();
            if (from == SlideFrom.TOP && targetComp.getY() >= srcBounds.y
                    || from == SlideFrom.BOTTOM && targetComp.getY() <= srcBounds.y
                    || from == SlideFrom.LEFT && targetComp.getX() >= srcBounds.x
                    || from == SlideFrom.RIGHT && targetComp.getX() <= srcBounds.x) {
                setLayout(layout);
                slideTimer.stop();
                if (onAfterSlide != null) onAfterSlide.run();
            }
        });
        setLayout(null);
        // 初始化放置组件的大小
        targetComp.setSize(srcBounds.width, srcBounds.height);
        if (from == SlideFrom.TOP) targetComp.setLocation(srcBounds.x, gBounds.y - srcBounds.height);
        else if (from == SlideFrom.BOTTOM) targetComp.setLocation(srcBounds.x, gBounds.y + gBounds.height);
        else if (from == SlideFrom.LEFT) targetComp.setLocation(gBounds.x - srcBounds.width, srcBounds.y);
        else if (from == SlideFrom.RIGHT) targetComp.setLocation(gBounds.x + srcBounds.width, srcBounds.y);
        add(targetComp);
        slideTimer.start();
    }

    // 返回组件索引，找不到返回 -1
    public int getComponentIndex(Component comp) {
        Component[] components = getComponents();
        for (int i = 0, len = components.length; i < len; i++)
            if (components[i] == comp) return i;
        return -1;
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
