package net.doge.ui.widget.dialog;

import net.doge.constant.lang.I18n;
import net.doge.constant.ui.Colors;
import net.doge.model.entity.LocalPlaylist;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 编辑收藏夹的对话框
 * @Date 2020/12/15
 */
public class EditLocalPlaylistDialog extends AbstractTitledDialog {
    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    private CustomLabel label = new CustomLabel(I18n.getText("localPlaylistName"));
    private CustomTextField textField = new CustomTextField(20);

    private DialogButton okButton;
    private DialogButton cancelButton;

    private LocalPlaylist playlist;

    // 父窗口
    public EditLocalPlaylistDialog(MainFrame f, LocalPlaylist playlist) {
        super(f, I18n.getText("editLocalPlaylist"));

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);

        this.playlist = playlist;
    }

    public void showDialog() {
        setResizable(false);
        setSize(560, 200);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            playlist.setName(textField.getText());
            close();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(globalPanel);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        CustomPanel panel = new CustomPanel();

        Color textColor = f.currUIStyle.getTextColor();
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);

        label.setForeground(textColor);
        panel.add(label);

        textField.setForeground(textColor);
        textField.setCaretColor(textColor);
        textField.setSelectedTextColor(textColor);
        textField.setSelectionColor(darkerTextAlphaColor);
        textField.setText(playlist.getName());
        panel.add(textField);

        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(panel);
        centerPanel.add(Box.createVerticalGlue());
    }
}
