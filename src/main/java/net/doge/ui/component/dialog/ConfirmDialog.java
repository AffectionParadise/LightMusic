package net.doge.ui.component.dialog;

import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.component.checkbox.CustomCheckBox;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.button.DialogButton;
import net.doge.ui.component.dialog.factory.AbstractShadowDialog;
import net.doge.util.ui.ImageUtil;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @Author Doge
 * @Description 自定义用户交互对话框
 * @Date 2021/1/5
 */
public class ConfirmDialog extends AbstractShadowDialog {
    private DialogButton yes = new DialogButton("");
    private DialogButton no = new DialogButton("");
    private DialogButton cancel = new DialogButton("");
    private int response;

    private String message = "";
    private CustomPanel messagePanel = new CustomPanel();
    private CustomLabel messageLabel = new CustomLabel(message);
    private boolean showCheck;
    private CustomPanel checkPanel = new CustomPanel();
    private CustomCheckBox checkBox = new CustomCheckBox();
    private CustomPanel buttonPanel = new CustomPanel();

    public ConfirmDialog(MainFrame f, String message) {
        super(f);
        this.message = message;
    }

    public ConfirmDialog(MainFrame f, String message, String yesText) {
        this(f, message);
        Color textColor = f.currUIStyle.getTextColor();
        yes = new DialogButton(yesText, textColor);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText) {
        this(f, message, yesText);
        Color textColor = f.currUIStyle.getTextColor();
        no = new DialogButton(noText, textColor);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, String cancelText) {
        this(f, message, yesText, noText);
        Color textColor = f.currUIStyle.getTextColor();
        cancel = new DialogButton(cancelText, textColor);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, String cancelText, boolean showCheck, String checkText) {
        this(f, message, yesText, noText, cancelText);
        this.showCheck = showCheck;
        checkBox.setText(checkText);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, boolean showCheck, String checkText) {
        this(f, message, yesText, noText);
        this.showCheck = showCheck;
        checkBox.setText(checkText);
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void showDialog() {
        Color textColor = f.currUIStyle.getTextColor();
        Color iconColor = f.currUIStyle.getIconColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        checkBox.setForeground(textColor);
        checkBox.setIcon(ImageUtil.dye(f.uncheckedIcon, iconColor));
        checkBox.setSelectedIcon(ImageUtil.dye(f.checkedIcon, iconColor));
        checkPanel.add(checkBox);
        checkPanel.setVisible(showCheck);
        Border eb = BorderFactory.createEmptyBorder(0, 0, 20, 0);
        checkPanel.setBorder(eb);

        messageLabel.setText(StringUtil.textToHtml(message));
        messageLabel.setForeground(textColor);
        messagePanel.add(messageLabel);
        messagePanel.setBorder(eb);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        buttonPanel.setLayout(fl);

        if (StringUtil.notEmpty(yes.getPlainText())) buttonPanel.add(yes);
        if (StringUtil.notEmpty(no.getPlainText())) buttonPanel.add(no);
        if (StringUtil.notEmpty(cancel.getPlainText())) buttonPanel.add(cancel);
        globalPanel.setLayout(new BorderLayout());
        globalPanel.add(messagePanel, BorderLayout.NORTH);
        globalPanel.add(checkPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);
        globalPanel.setBorder(BorderFactory.createEmptyBorder(25, 55, 25, 55));

        yes.addActionListener(e -> {
            response = JOptionPane.YES_OPTION;
            close();
        });
        no.addActionListener(e -> {
            response = JOptionPane.NO_OPTION;
            close();
        });
        cancel.addActionListener(e -> {
            response = JOptionPane.CANCEL_OPTION;
            close();
        });

        setContentPane(globalPanel);
        pack();

        updateBlur();

        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void close() {
        f.currDialogs.remove(this);
        dispose();
    }

    public int getResponse() {
        return response;
    }
}