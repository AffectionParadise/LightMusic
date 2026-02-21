package net.doge.ui.widget.dialog;

import lombok.Getter;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.meta.SoftInfo;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.exception.core.InvalidPackageFileException;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractShadowDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.MuteSliderUI;
import net.doge.util.core.crypto.CryptoUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.http.HttpUtil;
import net.doge.util.core.http.listener.DownloadListener;
import net.doge.util.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.*;
import java.io.File;

/**
 * @author Doge
 * @description 更新对话框
 * @date 2020/12/15
 */
public class UpdateDialog extends AbstractShadowDialog {
    private final String DOWNLOAD_MSG = I18n.getText("downloadMsg");
    private final String DOWNLOAD_FAILED_MSG = I18n.getText("downloadFailedMsg");
    private final String VALIDATING_MSG = I18n.getText("validatingMsg");
    private final String VALIDATION_FAILED_MSG = I18n.getText("validationFailedMsg");

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

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);
    }

    public void showDialog() {
        setResizable(false);

        globalPanel.setLayout(new BorderLayout());

        initView();
        GlobalExecutors.requestExecutor.execute(() -> prepareUpdate());

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(globalPanel);

        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        pack();
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new BorderLayout());

        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color sliderColor = style.getSliderColor();

        // 信息面板
        msgLabel.setForeground(textColor);
        percentLabel.setForeground(textColor);
        FontMetrics m = percentLabel.getFontMetrics(Fonts.NORMAL);
        Dimension d = new HDDimension(m.stringWidth("100%") + 2, m.getHeight());
        percentLabel.setPreferredSize(d);

        msgPanel.add(msgLabel);
        centerPanel.add(msgPanel, BorderLayout.NORTH);

        // 进度条
        progressSlider.setUI(new MuteSliderUI(progressSlider, sliderColor));
        progressSlider.setPreferredSize(new HDDimension(300, 30));
        progressSlider.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        progressSlider.setMaximum(1000);
        progressSlider.setValue(0);

        // 进度面板
        progressPanel.setLayout(new HDFlowLayout(HDFlowLayout.LEFT, 10, 5));
        progressPanel.add(progressSlider);
        progressPanel.add(percentLabel);
        progressPanel.add(cancelButton);

        // 取消
        cancelButton.addActionListener(e -> close(true));

        centerPanel.add(progressPanel, BorderLayout.CENTER);
        centerPanel.setBorder(new HDEmptyBorder(10, 20, 10, 20));
    }

    // 准备更新包
    private void prepareUpdate() {
        String dest = SimplePath.TEMP_PATH + SoftInfo.PACKAGE_FILE_NAME;
        File packageFile = new File(dest);
        try {
            String releaseBody = HttpRequest.get(SoftInfo.RELEASE_ASSET).executeAsStr();
            Document doc = Jsoup.parse(releaseBody);
            Element a = doc.select("li a").first();
            String url = "https://github.com" + a.attr("href");
            HttpUtil.download(url, dest, new DownloadListener() {
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
            if (!keyMD5.equalsIgnoreCase(CryptoUtil.md5(packageFile)))
                throw new InvalidPackageFileException(VALIDATION_FAILED_MSG);
            close(false);
        } catch (Exception e) {
            // 下载中断后删除软件包
            FileUtil.delete(packageFile);
            close(true);
            new TipDialog(f, e instanceof InvalidPackageFileException ? VALIDATION_FAILED_MSG : DOWNLOAD_FAILED_MSG).showDialog();
        }
    }

    private void close(boolean canceled) {
        this.canceled = canceled;
        close();
    }
}
