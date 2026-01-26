package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.meta.SoftInfo;
import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ImageUtil;

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
    private CustomPanel editionPanel = new CustomPanel();
    private CustomLabel editionLabel = new CustomLabel(I18n.getText("version") + SoftInfo.VERSION);
    private CustomPanel technoPanel = new CustomPanel();
    private CustomLabel technoLabel = new CustomLabel(I18n.getText("techno"));
    private CustomPanel websitePanel = new CustomPanel();
    private CustomLabel websiteLabel = new CustomLabel(I18n.getText("website") + SoftInfo.WEBSITE);
    private CustomPanel mailPanel = new CustomPanel();
    private CustomLabel mailLabel = new CustomLabel(I18n.getText("mail") + SoftInfo.MAIL);
    private CustomPanel buttonPanel = new CustomPanel();

    public AboutDialog(MainFrame f) {
        super(f, I18n.getText("aboutTitle"));

        Color textColor = f.currUIStyle.getTextColor();
        yes = new DialogButton(I18n.getText("ok"), textColor);
    }

    public void showDialog() {
        Color textColor = f.currUIStyle.getTextColor();

        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);

        appLabel.setText(f.TITLE);
        appLabel.setIcon(ImageUtil.dye(appIcon, textColor));
        appLabel.setIconTextGap(15);

        appLabel.setForeground(textColor);
        editionLabel.setForeground(textColor);
        technoLabel.setForeground(textColor);
        websiteLabel.setForeground(textColor);
        mailLabel.setForeground(textColor);

        float alpha = 0.8f;
        appLabel.setInstantAlpha(alpha);
        editionLabel.setInstantAlpha(alpha);
        technoLabel.setInstantAlpha(alpha);
        websiteLabel.setInstantAlpha(alpha);
        mailLabel.setInstantAlpha(alpha);

        appPanel.add(appLabel);
        editionPanel.add(editionLabel);
        technoPanel.add(technoLabel);
        websitePanel.add(websiteLabel);
        mailPanel.add(mailLabel);

        Border eb = BorderFactory.createEmptyBorder(0, 0, 10, 0);
        appPanel.setBorder(eb);
        editionPanel.setBorder(eb);
        technoPanel.setBorder(eb);
        websitePanel.setBorder(eb);
        mailPanel.setBorder(eb);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(appPanel);
        centerPanel.add(editionPanel);
        centerPanel.add(technoPanel);
        centerPanel.add(websitePanel);
        centerPanel.add(mailPanel);

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