package net.doge.ui.component.dialog.factory;

import net.doge.constant.ui.BlurConstants;
import net.doge.constant.ui.ImageConstants;
import net.doge.model.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.component.panel.GlobalPanel;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Doge
 * @Description 抽象迷你对话框
 * @Date 2021/1/5
 */
public abstract class AbstractMiniDialog extends JDialog {
    private ExecutorService globalPanelExecutor = Executors.newSingleThreadExecutor();
    private Timer globalPanelTimer;

    protected GlobalPanel globalPanel = new GlobalPanel();

    protected MainFrame f;

    public AbstractMiniDialog(MainFrame f) {
        super(f);
        this.f = f;

        globalPanelTimer = new Timer(10, e -> {
            globalPanelExecutor.execute(() -> {
                globalPanel.setOpacity((float) Math.min(1, globalPanel.getOpacity() + 0.05));
                if (globalPanel.getOpacity() >= 1) globalPanelTimer.stop();
            });
        });
    }

    public void updateBlur() {
        BufferedImage img;
        if (f.blurType != BlurConstants.OFF && f.player.loadedMusicResource()) {
            img = f.player.getMetaMusicInfo().getAlbumImage();
            if (img == null) img = ImageConstants.DEFAULT_IMG;
            if (f.blurType == BlurConstants.MC) img = ImageUtil.dyeRect(1, 1, ImageUtil.getAvgRGB(img));
        } else {
            UIStyle style = f.currUIStyle;
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
        // 线性渐变
        if (loadedMusicResource && f.blurType == BlurConstants.LG) img = ImageUtil.toGradientImage(img, dw, dh);
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
        globalPanelTimer.start();
    }
}