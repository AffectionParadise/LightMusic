package net.doge.ui.widget.dialog;

import net.doge.constant.lang.I18n;
import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMIconManager;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 捐赠对话框
 * @Date 2021/1/5
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
    private CustomLabel thankLabel = new CustomLabel(StringUtil.textToHtml(THANK_MSG));
    private CustomPanel buttonPanel = new CustomPanel();

    public DonateDialog(MainFrame f) {
        super(f, I18n.getText("donateTitle"));

        Color textColor = f.currUIStyle.getTextColor();
        yes = new DialogButton(I18n.getText("ok"), textColor);
    }

    public void showDialog() {
        Color textColor = f.currUIStyle.getTextColor();

        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);

        messageLabel.setForeground(textColor);
        weixinLabel.setForeground(textColor);
        alipayLabel.setForeground(textColor);
        thankLabel.setForeground(textColor);

        messagePanel.add(messageLabel);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        weixinLabel.setVerticalTextPosition(SwingConstants.TOP);
        alipayLabel.setVerticalTextPosition(SwingConstants.TOP);
        weixinLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        alipayLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        final int gap = 15;
        weixinLabel.setIconTextGap(gap);
        alipayLabel.setIconTextGap(gap);
        weixinLabel.setIcon(weixinIcon);
        alipayLabel.setIcon(alipayIcon);
        leftPanel.add(weixinLabel);
        rightPanel.add(alipayLabel);
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));
        cPanel.add(leftPanel);
        cPanel.add(rightPanel);

        thankPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        thankPanel.add(thankLabel);

        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(messagePanel, BorderLayout.NORTH);
        centerPanel.add(cPanel, BorderLayout.CENTER);
        centerPanel.add(thankPanel, BorderLayout.SOUTH);

        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        buttonPanel.setLayout(fl);
        buttonPanel.add(yes);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        globalPanel.setLayout(new BorderLayout());
        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        yes.addActionListener(e -> close());

        initTitleBar();
        setContentPane(globalPanel);
        pack();

        updateBlur();

        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }
}