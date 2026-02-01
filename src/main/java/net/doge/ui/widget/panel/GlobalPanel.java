package net.doge.ui.widget.panel;

import lombok.Data;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/12
 */
@Data
public class GlobalPanel extends JPanel {
//    // 最大阴影透明度
//    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
//    // 阴影大小像素
//    protected final int pixels = ScaleUtil.scale(10);

    private BufferedImage lImg;
    private BufferedImage bgImg;
    // 放大后的背景图，用于旋转
    private BufferedImage lImgScaled;
    private BufferedImage bgImgScaled;
    private float opacity;
    // 旋转律动
    private boolean grooveOn;
    private double angle;
    private Runnable onImgScaledReady;

//    private Timer slideTimer;
//    private Runnable onAfterSlide;

    public GlobalPanel() {
        setOpaque(false);
//        initBorder();
    }

    public void setBgImg(BufferedImage bgImg) {
        lImg = this.bgImg;
        lImgScaled = this.bgImgScaled;
        this.bgImg = bgImg;
        // 确保旋转后完全覆盖区域的最小放大 k 值
        SwingUtilities.invokeLater(() -> {
            int w = bgImg.getWidth(), h = bgImg.getHeight();
            this.bgImgScaled = ImageUtil.scale(bgImg, (float) (Math.sqrt(w * w + h * h) / Math.min(w, h)));
            if (onImgScaledReady != null) onImgScaledReady.run();
        });
        this.opacity = 0;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        repaint();
    }

    public void setGrooveOn(boolean grooveOn) {
        this.grooveOn = grooveOn;
        setAngle(0);
    }

    public void setAngle(double angle) {
        this.angle = angle;
        repaint();
    }

//    // 是否在滑动动画状态
//    public boolean isSlideAnimating() {
//        return slideTimer != null && slideTimer.isRunning();
//    }

//    public void initBorder() {
//        // 阴影边框
//        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
//    }

//    // 组件滑入滑出替换
//    public void slideFrom(Component srcComp, Component targetComp, int from) {
//        // 不存在该组件，跳出
//        if (getComponentIndex(srcComp) < 0) return;
//        Rectangle srcBounds = srcComp.getBounds(), gBounds = getBounds();
//        LayoutManager layout = getLayout();
//        // 根据窗口高度界定动画速率
//        int SLIDE_STEP_H = (int) (gBounds.width * 0.043), SLIDE_STEP_V = (int) (gBounds.height * 0.032);
//        slideTimer = new Timer(1, e -> {
//            if (from == SlideFrom.TOP) {
//                srcComp.setLocation(srcBounds.x, Math.min(gBounds.y + gBounds.height, srcComp.getY() + SLIDE_STEP_V));
//                targetComp.setLocation(srcBounds.x, Math.min(srcBounds.y, targetComp.getY() + SLIDE_STEP_V));
//            } else if (from == SlideFrom.BOTTOM) {
//                srcComp.setLocation(srcBounds.x, Math.max(gBounds.y - srcBounds.height, srcComp.getY() - SLIDE_STEP_V));
//                targetComp.setLocation(srcBounds.x, Math.max(srcBounds.y, targetComp.getY() - SLIDE_STEP_V));
//            }
//            // 左右滑入存在性能问题，由于 jlist 区域刷新耗时，慎用！
//            else if (from == SlideFrom.LEFT) {
//                srcComp.setLocation(Math.min(gBounds.x + gBounds.width, srcComp.getX() + SLIDE_STEP_H), srcBounds.y);
//                targetComp.setLocation(Math.min(srcBounds.x, targetComp.getX() + SLIDE_STEP_H), srcBounds.y);
//            } else if (from == SlideFrom.RIGHT) {
//                srcComp.setLocation(Math.max(gBounds.x - srcBounds.width, srcComp.getX() - SLIDE_STEP_H), srcBounds.y);
//                targetComp.setLocation(Math.max(srcBounds.x, targetComp.getX() - SLIDE_STEP_H), srcBounds.y);
//            }
//            repaint();
//            if (from == SlideFrom.TOP && targetComp.getY() >= srcBounds.y
//                    || from == SlideFrom.BOTTOM && targetComp.getY() <= srcBounds.y
//                    || from == SlideFrom.LEFT && targetComp.getX() >= srcBounds.x
//                    || from == SlideFrom.RIGHT && targetComp.getX() <= srcBounds.x) {
//                setLayout(layout);
//                slideTimer.stop();
//                if (onAfterSlide != null) onAfterSlide.run();
//            }
//        });
//        setLayout(null);
//        // 初始化放置组件的大小
//        targetComp.setSize(srcBounds.width, srcBounds.height);
//        if (from == SlideFrom.TOP) targetComp.setLocation(srcBounds.x, gBounds.y - srcBounds.height);
//        else if (from == SlideFrom.BOTTOM) targetComp.setLocation(srcBounds.x, gBounds.y + gBounds.height);
//        else if (from == SlideFrom.LEFT) targetComp.setLocation(gBounds.x - srcBounds.width, srcBounds.y);
//        else if (from == SlideFrom.RIGHT) targetComp.setLocation(gBounds.x + srcBounds.width, srcBounds.y);
//        add(targetComp);
//        slideTimer.start();
//    }

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

        if (bgImg == null) return;

//        int pw = getWidth() - 2 * pixels, ph = getHeight() - 2 * pixels;
        int pw = getWidth(), ph = getHeight();
        Graphics2D g2d = GraphicsUtil.setup(g);

        // 律动动画
        if (grooveOn) {
            if (lImgScaled != null) {
                // opacity < 1 时绘制底图，避免不必要的操作占用 CPU！
                if (opacity < 1) {
                    paintRotatedImg(g2d, lImgScaled);
                    GraphicsUtil.srcOver(g2d, opacity);
                } else lImgScaled = null;
            }
            paintRotatedImg(g2d, bgImgScaled);
        } else {
            if (lImg != null) {
                if (opacity < 1) {
                    // 宽高设置为组件的宽高，observer 设置成组件就可以自适应
//                    g2d.drawImage(lImg, pixels, pixels, pw, ph, this);
                    g2d.drawImage(lImg, 0, 0, pw, ph, this);
                    GraphicsUtil.srcOver(g2d, opacity);
                } else lImg = null;
            }
//            g2d.drawImage(bgImg, pixels, pixels, pw, ph, this);
            g2d.drawImage(bgImg, 0, 0, pw, ph, this);
        }

//        // 画边框阴影
//        int step = TOP_OPACITY / pixels;
//        for (int i = 0; i < pixels; i++) {
//            g2d.setColor(ColorUtil.deriveAlphaColor(Colors.BLACK, step * i));
//            int arc = ScaleUtil.scale(10);
//            g2d.drawRoundRect(i, i, getWidth() - (i * 2 + 1), getHeight() - (i * 2 + 1), arc, arc);
//        }

        GraphicsUtil.srcOver(g2d);
    }

    // 画出旋转后的图像
    private void paintRotatedImg(Graphics2D g2d, BufferedImage img) {
        // 保存原始变换状态
        AffineTransform originalTransform = g2d.getTransform();
        // 面板中心
        int cx = getWidth() / 2, cy = getHeight() / 2;
        // 旋转
        g2d.rotate(Math.toRadians(angle), cx, cy);
        // 绘制图像（坐标需调整为以中心点为基准）
        g2d.drawImage(img, cx - img.getWidth() / 2, cy - img.getHeight() / 2, this);
        // 恢复原始变换（重要！）
        g2d.setTransform(originalTransform);
    }
}
