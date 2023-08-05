package net.doge.ui.component.dialog;

import cn.hutool.http.HttpRequest;
import lombok.Getter;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.meta.SoftInfo;
import net.doge.constant.system.SimplePath;
import net.doge.constant.ui.Colors;
import net.doge.constant.ui.Fonts;
import net.doge.exception.InValidPackageFileException;
import net.doge.sdk.system.listener.DownloadListener;
import net.doge.sdk.util.MusicServerUtil;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.DialogButton;
import net.doge.ui.component.dialog.factory.AbstractShadowDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.slider.CustomSlider;
import net.doge.ui.component.slider.ui.MuteSliderUI;
import net.doge.util.common.CryptoUtil;
import net.doge.util.system.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @Author Doge
 * @Description 更新对话框
 * @Date 2020/12/15
 */
public class UpdateDialog extends AbstractShadowDialog {
    private final String DOWNLOAD_MSG = "正在下载更新包......";
    private final String DOWNLOAD_FAILED_MSG = "更新包下载失败，请稍后再试";
    private final String VALIDATING_MSG = "正在校验更新包完整性......";
    private final String VALIDATION_FAILED_MSG = "更新包已损坏，请重新下载";

    private CustomPanel centerPanel = new CustomPanel();

    private CustomPanel msgPanel = new CustomPanel();
    private CustomLabel msgLabel = new CustomLabel(DOWNLOAD_MSG);

    private CustomPanel progressPanel = new CustomPanel();
    private CustomSlider progressSlider = new CustomSlider();
    private CustomLabel percentLabel = new CustomLabel("0%");
    private DialogButton cancelButton;

    @Getter
    private boolean canceled;
    private String keyMD5;

    public UpdateDialog(MainFrame f, String keyMD5) {
        super(f);
        this.keyMD5 = keyMD5;

        Color textColor = f.currUIStyle.getTextColor();
        cancelButton = new DialogButton("取消", textColor);
    }

    public void showDialog() {
        setResizable(false);

        globalPanel.setLayout(new BorderLayout());

        initView();
        GlobalExecutors.requestExecutor.execute(() -> prepareUpdate());

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(globalPanel);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new BorderLayout());

        Color textColor = f.currUIStyle.getTextColor();
        Color sliderColor = f.currUIStyle.getSliderColor();

        // 信息面板
        msgLabel.setForeground(textColor);
        percentLabel.setForeground(textColor);
        FontMetrics m = percentLabel.getFontMetrics(Fonts.NORMAL);
        Dimension d = new Dimension(m.stringWidth("100%") + 2, m.getHeight());
        percentLabel.setPreferredSize(d);

        msgPanel.add(msgLabel);
        centerPanel.add(msgPanel, BorderLayout.NORTH);

        // 进度条
        progressSlider.setUI(new MuteSliderUI(progressSlider, sliderColor));
        progressSlider.setPreferredSize(new Dimension(300, 30));
        progressSlider.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        progressSlider.setMaximum(1000);
        progressSlider.setValue(0);

        // 进度面板
        progressPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        progressPanel.add(progressSlider);
        progressPanel.add(percentLabel);
        progressPanel.add(cancelButton);

        // 取消
        cancelButton.addActionListener(e -> close(true));

        centerPanel.add(progressPanel, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    // 准备更新包
    private void prepareUpdate() {
        String dest = SimplePath.TEMP_PATH + SoftInfo.PACKAGE_FILE_NAME;
        File packageFile = new File(dest);
        try {
            String releaseBody = HttpRequest.get(SoftInfo.RELEASE_ASSET).executeAsync().body();
            Document doc = Jsoup.parse(releaseBody);
            Element a = doc.select("li a").first();
            // 代理链接，避免下载链接无法响应
            String url = "https://ghdl.feizhuqwq.cf/https://github.com" + a.attr("href");
            MusicServerUtil.download(url, dest, new DownloadListener() {
                @Override
                public void progress(long finishedSize, long totalSize) {
                    msgLabel.setText(String.format(DOWNLOAD_MSG + "(%s / %s)", FileUtil.getUnitString(finishedSize), FileUtil.getUnitString(totalSize)));
                    double percent = (double) finishedSize / totalSize;
                    progressSlider.setValue((int) (percent * progressSlider.getMaximum()));
                    percentLabel.setText(String.format("%d%%", (int) (percent * 100)));
                }

                @Override
                public boolean canInterrupt() {
                    return canceled;
                }
            });
            if (canceled) {
                // 取消下载后删除软件包
                FileUtil.delete(packageFile);
                return;
            }
            // 校验更新包 MD5
            msgLabel.setText(VALIDATING_MSG);
            if (!keyMD5.equalsIgnoreCase(CryptoUtil.hashMD5(packageFile)))
                throw new InValidPackageFileException(VALIDATION_FAILED_MSG);
            close(false);
        } catch (Exception e) {
            // 下载中断后删除软件包
            FileUtil.delete(packageFile);
            close(true);
            new TipDialog(f, e instanceof InValidPackageFileException ? VALIDATION_FAILED_MSG : DOWNLOAD_FAILED_MSG).showDialog();
        }
    }

    private void close(boolean canceled) {
        this.canceled = canceled;
        f.currDialogs.remove(this);
        dispose();
    }
}
