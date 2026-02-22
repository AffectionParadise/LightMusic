package net.doge.ui.widget.dialog.base;

import net.doge.constant.core.ui.image.BlurConstants;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.panel.GlobalPanel;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Doge
 * @description 抽象迷你对话框
 * @date 2021/1/5
 */
public abstract class AbstractMiniDialog extends BaseDialog {
    private ExecutorService globalPanelExecutor = Executors.newSingleThreadExecutor();
    private Timer globalPanelTimer;

    protected GlobalPanel globalPanel = new GlobalPanel();
    protected MainFrame f;

    public AbstractMiniDialog(MainFrame f) {
        super(f);
        this.f = f;
        init();

        globalPanelTimer = new Timer(10, e -> {
            globalPanelExecutor.execute(() -> {
                globalPanel.setImgOpacity((float) Math.min(1, globalPanel.getImgOpacity() + 0.05));
                if (globalPanel.getImgOpacity() >= 1) globalPanelTimer.stop();
            });
        });
    }

    private void init() {
        globalPanel.setLayout(new BorderLayout());
        setContentPane(globalPanel);
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

    public void doBlur(BufferedImage img) {
        int dw = getWidth(), dh = getHeight();
        boolean loadedMusicResource = f.player.loadedMusicResource();
        // 截取正方形(有的图片是长方形)
        if (loadedMusicResource && (f.blurType == BlurConstants.CV || f.blurType == BlurConstants.LG))
            img = ImageUtil.cropCenter(img);
        // 消除透明度
        img = ImageUtil.eraseTransparency(img);
        if (loadedMusicResource) {
            // 线性渐变
            if (f.blurType == BlurConstants.LG) img = ImageUtil.gradientImage(img, dw, dh);
                // 迷幻纹理
            else if (f.blurType == BlurConstants.FBM) img = ImageUtil.fbmImage(img, dw, dh);
        }
        // 缩小
        img = ImageUtil.width(img, 256);
        // 流体图
        if (f.fluidOn) img = ImageUtil.fluidImage(img);
        if (f.maskOn) img = ImageUtil.mask(img);
        // 高斯模糊
        if (f.gsOn) img = ImageUtil.gaussianBlur(img);
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
        // 暗角滤镜
        if (f.darkerOn) img = ImageUtil.darker(img);
        // 设置圆角
        img = ImageUtil.radius(img, ScaleUtil.scale(10));
        globalPanel.setBgImg(img);
        globalPanelTimer.start();
    }
}