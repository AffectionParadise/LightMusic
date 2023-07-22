package net.doge.ui.component.dialog.factory;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constant.ui.BlurConstants;
import net.doge.model.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.component.panel.GlobalPanel;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
            globalPanelExecutor.submit(() -> {
                globalPanel.setOpacity((float) Math.min(1, globalPanel.getOpacity() + 0.05));
                if (globalPanel.getOpacity() >= 1) globalPanelTimer.stop();
            });
        });
    }

    public void updateBlur() {
        BufferedImage img;
        if (f.blurType != BlurConstants.OFF && f.player.loadedMusic()) {
            img = f.player.getMusicInfo().getAlbumImage();
            if (img == null) img = f.defaultAlbumImage;
            if (f.blurType == BlurConstants.MC)
                img = ImageUtil.dyeRect(1, 1, ImageUtil.getAvgRGB(img));
        } else {
            UIStyle style = f.currUIStyle;
            img = style.getImg();
        }
        doBlur(img);
    }

    public void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth(), dh = getHeight();
        try {
            BufferedImage bgImg = bufferedImage;
            boolean loadedMusic = f.player.loadedMusic();
            // 截取中间的一部分(有的图片是长方形)
            if (loadedMusic && f.blurType == BlurConstants.CV) bufferedImage = ImageUtil.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            if (f.gsOn) bufferedImage = ImageUtil.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtil.eraseTranslucency(bufferedImage);
            // 高斯模糊
            if (f.gsOn) bufferedImage = ImageUtil.doBlur(bufferedImage);
            // 放大至窗口大小
            bufferedImage = ImageUtil.width(bufferedImage, dw);
            if (dh > bufferedImage.getHeight())
                bufferedImage = ImageUtil.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (!loadedMusic || f.blurType == BlurConstants.CV || f.blurType == BlurConstants.OFF) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtil.forceSize(bufferedImage, dw, dh);
            }
            if (f.blurType == BlurConstants.LG) bufferedImage = ImageUtil.toGradient(bgImg, dw, dh);
            if (f.darkerOn) bufferedImage = ImageUtil.darker(bufferedImage);
            // 设置圆角
            bufferedImage = ImageUtil.setRadius(bufferedImage, 10);
            globalPanel.setBackgroundImage(bufferedImage);
            globalPanelTimer.start();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}