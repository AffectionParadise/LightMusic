package net.doge.ui.widget.dialog;

import lombok.Getter;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @Author Doge
 * @Description 新建收藏夹的对话框
 * @Date 2020/12/15
 */
public class CreateLocalPlaylistDialog extends AbstractTitledDialog {
    private final int WIDTH = ScaleUtil.scale(560);
    private final int HEIGHT = ScaleUtil.scale(180);
    private final String NAME_NOT_NULL_MSG = I18n.getText("localPlaylistNameNotNullMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    private CustomLabel label = new CustomLabel(I18n.getText("localPlaylistName"));
    private CustomTextField textField = new CustomTextField(20);

    private DialogButton okButton;
    private DialogButton cancelButton;

    @Getter
    private boolean confirmed;
    @Getter
    private String result;

    // 父窗口
    public CreateLocalPlaylistDialog(MainFrame f) {
        super(f, I18n.getText("createLocalPlaylist"));

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            result = textField.getText();
            if (result.trim().isEmpty()) {
                new TipDialog(f, NAME_NOT_NULL_MSG, true).showDialog();
                return;
            }
            confirmed = true;
            close();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(new HDEmptyBorder(10, 0, 10, 0));
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
        Color darkerTextAlphaColor = ColorUtil.deriveAlpha(ColorUtil.darker(textColor), 0.5f);

        label.setForeground(textColor);
        panel.add(label);

        textField.setForeground(textColor);
        textField.setCaretColor(textColor);
        textField.setSelectedTextColor(textColor);
        textField.setSelectionColor(darkerTextAlphaColor);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButton.doClick();
                }
            }
        });
        panel.add(textField);

        panel.setBorder(new HDEmptyBorder(0, 20, 0, 20));
        centerPanel.add(CustomBox.createVerticalGlue());
        centerPanel.add(panel);
        centerPanel.add(CustomBox.createVerticalGlue());
    }
}
