package net.doge.ui.components.dialog;

import net.doge.constants.Colors;
import net.doge.constants.SimplePath;
import net.doge.constants.SoftInfo;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.DialogButton;
import net.doge.ui.components.dialog.factory.AbstractTitledDialog;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @Author yzx
 * @Description 关于对话框
 * @Date 2021/1/5
 */
public class AboutDialog extends AbstractTitledDialog {
    private DialogButton yes;

    private ImageIcon appIcon = new ImageIcon(SimplePath.ICON_PATH + "title.png");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel appPanel = new CustomPanel();
    private CustomLabel appLabel = new CustomLabel();
    private CustomPanel editionPanel = new CustomPanel();
    private CustomLabel editionLabel = new CustomLabel("版本：" + SoftInfo.EDITION);
    private CustomPanel technoPanel = new CustomPanel();
    private CustomLabel technoLabel = new CustomLabel("基于 Swing 与 JavaFX (Java 8) 构建");
    private CustomPanel websitePanel = new CustomPanel();
    private CustomLabel websiteLabel = new CustomLabel("网址：" + SoftInfo.WEBSITE);
    private CustomPanel mailPanel = new CustomPanel();
    private CustomLabel mailLabel = new CustomLabel("邮箱：" + SoftInfo.MAIL);
    private CustomPanel buttonPanel = new CustomPanel();

    public AboutDialog(PlayerFrame f) {
        super(f, "关于");

        Color textColor = f.currUIStyle.getTextColor();
        yes = new DialogButton("确定", textColor);
    }

    public void showDialog() {
        Color textColor = f.currUIStyle.getTextColor();
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);

        appLabel.setText(f.TITLE);
        appLabel.setIcon(ImageUtils.dye(appIcon, textColor));
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
        setLayout(new BorderLayout());
        add(globalPanel, BorderLayout.CENTER);
        pack();

        updateBlur();

        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }
}