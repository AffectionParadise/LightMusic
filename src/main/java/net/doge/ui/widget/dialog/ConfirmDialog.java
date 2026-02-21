package net.doge.ui.widget.dialog;

import lombok.Getter;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.checkbox.CustomCheckBox;
import net.doge.ui.widget.dialog.base.AbstractShadowDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.util.core.StringUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Doge
 * @description 自定义用户交互对话框
 * @date 2021/1/5
 */
public class ConfirmDialog extends AbstractShadowDialog {
    private final int MAX_WIDTH = ScaleUtil.scale(900);
    private final int MAX_HEIGHT = ScaleUtil.scale(600);

    private DialogButton yes;
    private DialogButton no;
    private DialogButton cancel;
    @Getter
    private int response;

    private String message;
    private CustomPanel messagePanel = new CustomPanel();
    private CustomScrollPane messageScrollPane = new CustomScrollPane(messagePanel);
    private CustomLabel messageLabel = new CustomLabel(message);
    private boolean showCheck;
    private CustomPanel controlPanel = new CustomPanel();
    private CustomPanel checkPanel = new CustomPanel();
    private CustomCheckBox checkBox = new CustomCheckBox();
    private CustomPanel buttonPanel = new CustomPanel();

    public ConfirmDialog(MainFrame f, String message) {
        this(f, message, null);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText) {
        this(f, message, yesText, null);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText) {
        this(f, message, yesText, noText, null);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, String cancelText) {
        this(f, message, yesText, noText, cancelText, false, null);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, boolean showCheck, String checkText) {
        this(f, message, yesText, noText, null, showCheck, checkText);
    }

    public ConfirmDialog(MainFrame f, String message, String yesText, String noText, String cancelText, boolean showCheck, String checkText) {
        super(f);
        this.message = message;

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        yes = new DialogButton(yesText, textColor);
        no = new DialogButton(noText, textColor);
        cancel = new DialogButton(cancelText, textColor);
        this.showCheck = showCheck;
        checkBox.setText(checkText);
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void showDialog() {
        initView();
        pack();
        boolean wc = false, hc = false;
        Dimension size = getSize();
        if (size.width > MAX_WIDTH) {
            size.width = MAX_WIDTH;
            wc = true;
        }
        if (size.height > MAX_HEIGHT) {
            size.height = MAX_HEIGHT;
            hc = true;
        }
        int thickness = messageScrollPane.getThickness();
        if (wc) size.height += thickness;
        if (hc) size.width += thickness;
        boolean c = wc || hc;
        int top = ScaleUtil.scale(30), left = ScaleUtil.scale(c ? 35 : 55), bottom = ScaleUtil.scale(25), right = ScaleUtil.scale(c ? 10 : 55);
        globalPanel.setBorder(new EmptyBorder(top, left, bottom, right));
        if (c) {
            size.width += left + right;
            size.height += top + bottom;
            setSize(size);
        } else {
            pack();
        }

        updateBlur();
        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();

        checkBox.setForeground(textColor);
        checkBox.updateIconStyle();
        checkPanel.add(checkBox);
        checkPanel.setVisible(showCheck);
        Border eb = new HDEmptyBorder(0, 0, 20, 0);
        checkPanel.setBorder(eb);

        int thickness = messageScrollPane.getThickness();
        Dimension d = new Dimension(MAX_WIDTH - 2 * thickness, Integer.MAX_VALUE);
        messageLabel.setMaximumSize(d);
        messagePanel.setMaximumSize(d);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        messageLabel.setText(HtmlUtil.textToHtml(message));
        messageLabel.setForeground(textColor);
        messagePanel.add(messageLabel);
        messagePanel.setBorder(eb);
        messageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        FlowLayout fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(20));
        buttonPanel.setLayout(fl);
        if (StringUtil.notEmpty(yes.getPlainText())) buttonPanel.add(yes);
        if (StringUtil.notEmpty(no.getPlainText())) buttonPanel.add(no);
        if (StringUtil.notEmpty(cancel.getPlainText())) buttonPanel.add(cancel);

        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(checkPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        globalPanel.add(messageScrollPane, BorderLayout.CENTER);
        globalPanel.add(controlPanel, BorderLayout.SOUTH);

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
    }
}