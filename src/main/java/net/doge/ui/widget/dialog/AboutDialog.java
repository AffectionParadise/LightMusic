package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.meta.SoftInfo;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.MainFrame;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @Author Doge
 * @Description 关于对话框
 * @Date 2021/1/5
 */
public class AboutDialog extends AbstractTitledDialog {
    private DialogButton yes;

    private ImageIcon appIcon = LMIconManager.getIcon("title.title");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel appPanel = new CustomPanel();
    private CustomLabel appLabel = new CustomLabel();
    private CustomPanel versionPanel = new CustomPanel();
    private CustomLabel versionLabel = new CustomLabel(I18n.getText("version") + SoftInfo.VERSION);
    private CustomPanel jdkVersionPanel = new CustomPanel();
    private CustomLabel jdkVersionLabel = new CustomLabel(I18n.getText("jdkVersion") + SoftInfo.JDK_VERSION);
    private CustomPanel technoPanel = new CustomPanel();
    private CustomLabel technoLabel = new CustomLabel(I18n.getText("techno"));
    private CustomPanel websitePanel = new CustomPanel();
    private CustomLabel websiteLabel = new CustomLabel(I18n.getText("website") + SoftInfo.WEBSITE);
    private CustomPanel mailPanel = new CustomPanel();
    private CustomLabel mailLabel = new CustomLabel(I18n.getText("mail") + SoftInfo.MAIL);
    private CustomPanel buttonPanel = new CustomPanel();

    public AboutDialog(MainFrame f) {
        super(f, I18n.getText("aboutTitle"));

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        yes = new DialogButton(I18n.getText("ok"), textColor);
    }

    public void showDialog() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();

        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);

        appLabel.setText(f.TITLE);
        appLabel.setIcon(ImageUtil.dye(appIcon, textColor));
        appLabel.setIconTextGap(ScaleUtil.scale(15));

        appLabel.setForeground(textColor);
        versionLabel.setForeground(textColor);
        jdkVersionLabel.setForeground(textColor);
        technoLabel.setForeground(textColor);
        websiteLabel.setForeground(textColor);
        mailLabel.setForeground(textColor);

        float opacity = 0.8f;
        appLabel.setOpacity(opacity);
        versionLabel.setOpacity(opacity);
        jdkVersionLabel.setOpacity(opacity);
        technoLabel.setOpacity(opacity);
        websiteLabel.setOpacity(opacity);
        mailLabel.setOpacity(opacity);

        appPanel.add(appLabel);
        versionPanel.add(versionLabel);
        jdkVersionPanel.add(jdkVersionLabel);
        technoPanel.add(technoLabel);
        websitePanel.add(websiteLabel);
        mailPanel.add(mailLabel);

        Border eb = new HDEmptyBorder(0, 0, 10, 0);
        appPanel.setBorder(eb);
        versionPanel.setBorder(eb);
        jdkVersionPanel.setBorder(eb);
        technoPanel.setBorder(eb);
        websitePanel.setBorder(eb);
        mailPanel.setBorder(eb);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(appPanel);
        centerPanel.add(versionPanel);
        centerPanel.add(jdkVersionPanel);
        centerPanel.add(technoPanel);
        centerPanel.add(websitePanel);
        centerPanel.add(mailPanel);

        FlowLayout fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(20));
        buttonPanel.setLayout(fl);
        buttonPanel.add(yes);
        buttonPanel.setBorder(new HDEmptyBorder(15, 0, 10, 0));

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