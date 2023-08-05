package net.doge.ui.component.dialog;

import net.doge.constant.meta.SoftInfo;
import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.DialogButton;
import net.doge.ui.component.dialog.factory.AbstractTitledDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.system.LMIconManager;
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
    private CustomLabel editionLabel = new CustomLabel("版本：" + SoftInfo.VERSION);
    private CustomPanel technoPanel = new CustomPanel();
    private CustomLabel technoLabel = new CustomLabel("基于 Swing 与 JavaFX (Java 8) 构建");
    private CustomPanel websitePanel = new CustomPanel();
    private CustomLabel websiteLabel = new CustomLabel("网址：" + SoftInfo.WEBSITE);
    private CustomPanel mailPanel = new CustomPanel();
    private CustomLabel mailLabel = new CustomLabel("邮箱：" + SoftInfo.MAIL);
    private CustomPanel buttonPanel = new CustomPanel();

    public AboutDialog(MainFrame f) {
        super(f, "关于");

        Color textColor = f.currUIStyle.getTextColor();
        yes = new DialogButton("确定", textColor);
    }

    public void showDialog() {
        Color textColor = f.currUIStyle.getTextColor();

        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);

        appLabel.setText(f.TITLE);
        appLabel.setIcon(ImageUtil.dye(appIcon, textColor));
        appLabel.setIconTextGap(15);

        appLabel.setForeground(textColor);
        editionLabel.setForeground(textColor);
        technoLabel.setForeground(textColor);
        websiteLabel.setForeground(textColor);
        mailLabel.setForeground(textColor);

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