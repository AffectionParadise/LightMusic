package net.doge.ui.widget.dialog.factory;

import net.doge.constant.ui.BlurConstants;
import net.doge.constant.ui.ImageConstants;
import net.doge.model.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 抽象阴影对话框
 * @Date 2021/1/5
 */
public abstract class AbstractShadowDialog extends JDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    protected final int pixels = 10;

    protected DialogPanel globalPanel = new DialogPanel();

    protected MainFrame f;

    public AbstractShadowDialog(Window owner) {
        this(owner, true);
    }

    public AbstractShadowDialog(Window owner, boolean modal) {
        super(owner);
        if (owner instanceof MainFrame) this.f = (MainFrame) owner;
        setModal(modal);
    }

    public void updateBlur() {
        BufferedImage img;
        if (f.blurType != BlurConstants.OFF && f.player.loadedMusicResource()) {
            img = f.player.getMetaMusicInfo().getAlbumImage();
            if (img == null) img = ImageConstants.DEFAULT_IMG;
            if (f.blurType == BlurConstants.MC) img = ImageUtil.dyeRect(1, 1, ImageUtil.getBestAvgColor(img));
        } else {
            UIStyle style = f.currUIStyle;
            img = style.getImg();
        }
        doBlur(img);
    }

    private void doBlur(BufferedImage img) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        boolean loadedMusicResource = f.player.loadedMusicResource();
        // 截取正方形(有的图片是长方形)
        if (loadedMusicResource && (f.blurType == BlurConstants.CV || f.blurType == BlurConstants.LG))
            img = ImageUtil.cropCenter(img);
        // 消除透明度
        img = ImageUtil.eraseTransparency(img);
        if (loadedMusicResource) {
            // 线性渐变
            if (f.blurType == BlurConstants.LG) img = ImageUtil.toGradientImage(img, dw, dh);
                // 迷幻纹理
            else if (f.blurType == BlurConstants.FBM) img = ImageUtil.toFbmImage(img, dw, dh);
        }
        if (f.gsOn) {
            // 处理成 100 * 100 大小
            img = ImageUtil.width(img, 100);
            // 高斯模糊
            img = ImageUtil.gaussianBlur(img);
        }
        // 缩放至窗口大小
        img = ImageUtil.width(img, dw);
        if (dh > img.getHeight())
            img = ImageUtil.height(img, dh);
        // 裁剪中间的一部分
        if (!loadedMusicResource || f.blurType == BlurConstants.CV || f.blurType == BlurConstants.OFF) {
            int iw = img.getWidth(), ih = img.getHeight();
            img = ImageUtil.region(img, iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh);
            img = ImageUtil.quality(img, 0.1);
        } else {
            img = ImageUtil.forceSize(img, dw, dh);
        }
        if (f.darkerOn) img = ImageUtil.darker(img);
        // 设置圆角
        img = ImageUtil.setRadius(img, 10);
        globalPanel.setBackgroundImage(img);
        repaint();
    }

    protected class DialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public DialogPanel() {
            initBorder();
        }

        public void initBorder() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void eraseBorder() {
            setBorder(null);
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}