package net.doge.ui.widget.dialog.base;

import net.doge.ui.MainFrame;
import net.doge.ui.widget.panel.GlobalPanel;

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

    public void updateBgImg() {
        int dw = getWidth(), dh = getHeight();
        BufferedImage img = f.processBgImg(dw, dh, false, true);
        globalPanel.setBgImg(img);
        globalPanelTimer.start();
    }
}