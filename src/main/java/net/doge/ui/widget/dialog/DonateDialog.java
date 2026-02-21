package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.MainFrame;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 捐赠对话框
 * @date 2021/1/5
 */
public class DonateDialog extends AbstractTitledDialog {
    private DialogButton yes;

    private final String THANK_MSG = "同时感谢以下为本项目提供思路的开源项目，世界因你们这些无私的开发者而美丽~~\n\n" +
            "https://github.com/Binaryify/NeteaseCloudMusicApi\n" +
            "https://github.com/ecitlm/Kugou-api\n" +
            "https://github.com/MakcRe/KuGouMusicApi\n" +
            "https://github.com/jsososo/QQMusicApi\n" +
            "https://github.com/QiuYaohong/kuwoMusicApi\n" +
            "https://github.com/jsososo/MiguMusicApi\n" +
            "https://github.com/SocialSisterYi/bilibili-API-collect\n" +
            "https://github.com/lyswhut/lx-music-source\n" +
            "https://github.com/QiuChenlyOpenSource/MusicDownload\n";

    // 收款码
    private ImageIcon weixinIcon = LMIconManager.getIcon("dialog.weixin");
    private ImageIcon alipayIcon = LMIconManager.getIcon("dialog.alipay");

    private CustomPanel messagePanel = new CustomPanel();
    private CustomLabel messageLabel = new CustomLabel();
    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel cPanel = new CustomPanel();
    private CustomPanel leftPanel = new CustomPanel();
    private CustomLabel weixinLabel = new CustomLabel(I18n.getText("weixin"));
    private CustomPanel rightPanel = new CustomPanel();
    private CustomLabel alipayLabel = new CustomLabel(I18n.getText("alipay"));
    private CustomPanel thankPanel = new CustomPanel();
    private CustomLabel thankLabel = new CustomLabel(HtmlUtil.textToHtml(THANK_MSG));
    private CustomPanel buttonPanel = new CustomPanel();

    public DonateDialog(MainFrame f) {
        super(f, I18n.getText("donateTitle"));

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        yes = new DialogButton(I18n.getText("ok"), textColor);
    }

    public void showDialog() {
        initTitleBar();
        initView();
        pack();
        updateBlur();
        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();

        messageLabel.setForeground(textColor);
        weixinLabel.setForeground(textColor);
        alipayLabel.setForeground(textColor);
        thankLabel.setForeground(textColor);

        thankLabel.setOpacity(0.8f);

        messagePanel.add(messageLabel);
        messagePanel.setBorder(new HDEmptyBorder(0, 0, 20, 0));

        weixinLabel.setVerticalTextPosition(SwingConstants.TOP);
        alipayLabel.setVerticalTextPosition(SwingConstants.TOP);
        weixinLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        alipayLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        int gap = ScaleUtil.scale(15);
        weixinLabel.setIconTextGap(gap);
        alipayLabel.setIconTextGap(gap);
        weixinLabel.setIcon(weixinIcon);
        alipayLabel.setIcon(alipayIcon);
        leftPanel.add(weixinLabel);
        rightPanel.add(alipayLabel);
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));
        cPanel.add(leftPanel);
        cPanel.add(rightPanel);

        thankPanel.setBorder(new HDEmptyBorder(15, 0, 0, 0));
        thankPanel.add(thankLabel);

        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(messagePanel, BorderLayout.NORTH);
        centerPanel.add(cPanel, BorderLayout.CENTER);
        centerPanel.add(thankPanel, BorderLayout.SOUTH);

        FlowLayout fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(20));
        buttonPanel.setLayout(fl);
        buttonPanel.add(yes);
        buttonPanel.setBorder(new HDEmptyBorder(15, 0, 10, 0));

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        yes.addActionListener(e -> close());
    }
}