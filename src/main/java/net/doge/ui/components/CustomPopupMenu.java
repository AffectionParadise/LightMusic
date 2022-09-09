package net.doge.ui.components;

import com.sun.awt.AWTUtilities;
import net.doge.constants.Colors;
import net.doge.models.MusicPlayer;
import net.doge.ui.PlayerFrame;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.DefaultMenuLayout;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description 菜单自定义 UI
 * @Date 2020/12/13
 */
public class CustomPopupMenu extends JPopupMenu {
    private PlayerFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    public CustomPopupMenu(PlayerFrame f) {
        super();
        this.f = f;

        // 阴影边框
        Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), border));

        setOpaque(false);
        setDefaultLightWeightPopupEnabled(false);
        setLightWeightPopupEnabled(false);
    }

    public CustomPopupMenu(String text) {
        super(text);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // 使 JPopupMenu 对应的 Window 透明！
            Window w = SwingUtilities.getWindowAncestor(this);
            if(!w.getBackground().equals(Color.black)) {
                w.setVisible(false);
                w.setBackground(Color.black);
            }
            AWTUtilities.setWindowOpaque(w, false);
            w.setVisible(true);
        }
        f.currPopup = b ? this : null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle rect = getVisibleRect();
        Graphics2D g2d = (Graphics2D) g;

//        try {
//            BufferedImage bgImg = f.getDefaultAlbumImage();
//            BufferedImage img = rect.width > rect.height ?
//                    Thumbnails.of(bgImg).width((int) (rect.width * 1.2)).asBufferedImage()
//                    : Thumbnails.of(bgImg).height((int) (rect.height * 1.2)).asBufferedImage();
//            int iw = img.getWidth(), ih = img.getHeight();
//            img = Thumbnails.of(img)
//                    .scale(1f)
//                    .sourceRegion((int) (iw * 0.1), (ih - rect.height) / 2, rect.width, rect.height)
//                    .outputQuality(0.1)
//                    .asBufferedImage();
//            img = ImageUtils.darker(ImageUtils.setRadius(ImageUtils.doBlur(ImageUtils.eraseTranslucency(img)), 0.1));
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ImageUtils.getAvgRGB(f.getGlobalPanel().getBackgroundImage()));
        g2d.fillRoundRect(rect.x + pixels, rect.y + pixels, rect.width - 2 * pixels, rect.height - 2 * pixels, 10, 10);

        // 画边框阴影
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
            g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void paintBorder(Graphics g) {
//        super.paintBorder(g);
    }

    @Override
    public void addSeparator() {
        add(new CustomSeparator(f));
    }

    //    @Override
//    protected void paintBorder(Graphics g) {
//        Rectangle rect = getVisibleRect();
//        Graphics2D g2d = (Graphics2D) g;
//        // 避免锯齿
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        if (foreColor != null) g2d.setColor(foreColor);
//        g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
//    }
}
