package net.doge.ui.widget.dialog.base;

import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.image.BlurConstants;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 抽象阴影对话框
 * @Date 2021/1/5
 */
public abstract class AbstractShadowDialog extends JDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    protected final int pixels = ScaleUtil.scale(10);

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
            UIStyle style = UIStyleStorage.currUIStyle;
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
        if (f.maskOn) img = ImageUtil.mask(img);
        if (f.gsOn) {
            // 缩小
            img = ImageUtil.width(img, 256);
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
        img = ImageUtil.radius(img, ScaleUtil.scale(10));
        globalPanel.setBgImg(img);
        repaint();
    }

    protected class DialogPanel extends JPanel {
        private BufferedImage bgImg;

        public DialogPanel() {
            initBorder();
        }

        public void initBorder() {
            // 阴影边框
            setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
        }

        public void eraseBorder() {
            setBorder(null);
        }

        public void setBgImg(BufferedImage bgImg) {
            this.bgImg = bgImg;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = GraphicsUtil.setup(g);

            int w = getWidth(), h = getHeight();

            if (bgImg != null) {
//            GraphicsUtil.srcOver(g2d, 0.8f);
                g2d.drawImage(bgImg, pixels, pixels, w - 2 * pixels, h - 2 * pixels, this);
            }

            // 画边框阴影
            int step = TOP_OPACITY / pixels;
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
                int arc = ScaleUtil.scale(10);
                g2d.drawRoundRect(i, i, w - (i * 2 + 1), h - (i * 2 + 1), arc, arc);
            }
        }
    }
}