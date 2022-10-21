package net.doge.ui.components.dialog;

import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.models.SimpleMusicInfo;
import net.doge.models.UIStyle;
import net.doge.ui.components.DialogButton;
import net.doge.ui.PlayerFrame;
import net.doge.utils.ImageUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * @Author yzx
 * @Description 自定义用户交互对话框
 * @Date 2021/1/5
 */
public class ConfirmDialog extends JDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    Dimension size;
    Font font = Fonts.NORMAL;
    private DialogButton yes = new DialogButton("");
    private DialogButton no = new DialogButton("");
    private DialogButton cancel = new DialogButton("");
    private int response;

    // 复选框图标
    private ImageIcon uncheckedIcon = new ImageIcon(SimplePath.ICON_PATH + "unchecked.png");
    private ImageIcon checkedIcon = new ImageIcon(SimplePath.ICON_PATH + "checked.png");

    private PlayerFrame f;
    private String message = "";
    private JPanel messagePanel = new JPanel();
    private JLabel messageLabel = new JLabel(message, JLabel.CENTER);
    private boolean showCheck;
    private JPanel checkPanel = new JPanel();
    private JCheckBox checkBox = new JCheckBox();
    private JPanel buttonPanel = new JPanel();
    private ConfirmDialogPanel mainPanel = new ConfirmDialogPanel();

    public ConfirmDialog(PlayerFrame f, String message) {
        // 一定要是模态对话框才能接收值！！！
        super(f, true);
        this.f = f;
        this.message = message;
    }

    public ConfirmDialog(PlayerFrame f, String message, String yesText) {
        this(f, message);
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        yes = new DialogButton(yesText, buttonColor);
    }

    public ConfirmDialog(PlayerFrame f, String message, String yesText, String noText) {
        this(f, message, yesText);
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        no = new DialogButton(noText, buttonColor);
    }

    public ConfirmDialog(PlayerFrame f, String message, String yesText, String noText, String cancelText) {
        this(f, message, yesText, noText);
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        cancel = new DialogButton(cancelText, buttonColor);
    }

    public ConfirmDialog(PlayerFrame f, String message, String yesText, String noText, boolean showCheck, String checkText) {
        this(f, message, yesText, noText);
        this.showCheck = showCheck;
        checkBox.setText(checkText);
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void showDialog() {
        Color labelColor = f.getCurrUIStyle().getLabelColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        messageLabel.setFont(font);
        yes.setFont(font);
        no.setFont(font);
        cancel.setFont(font);
        checkBox.setFont(font);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setForeground(labelColor);
        checkBox.setIconTextGap(10);
        checkBox.setIcon(ImageUtils.dye(uncheckedIcon, labelColor));
        checkBox.setSelectedIcon(ImageUtils.dye(checkedIcon, labelColor));
        checkPanel.setOpaque(false);
        checkPanel.add(checkBox);
        checkPanel.setVisible(showCheck);
        checkPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        messageLabel.setText(message);
        messageLabel.setForeground(labelColor);
        messagePanel.add(messageLabel);
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        buttonPanel.setLayout(fl);
        buttonPanel.setOpaque(false);

        if (StringUtils.isNotEmpty(yes.getText())) buttonPanel.add(yes);
        if (StringUtils.isNotEmpty(no.getText())) buttonPanel.add(no);
        if (StringUtils.isNotEmpty(cancel.getText())) buttonPanel.add(cancel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(messagePanel, BorderLayout.NORTH);
        mainPanel.add(checkPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 55, 35, 55));

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

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        pack();

        size = getSize();
        updateBlur();

        setLocationRelativeTo(f);

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        boolean slight = false;
        if (f.getIsBlur() && f.getPlayer().loadedMusic()) bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
        else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
            slight = style.isPureColor();
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage, slight);
    }

    void close() {
        f.currDialogs.remove(this);
        dispose();
    }

    public int getResponse() {
        return response;
    }

    void doBlur(BufferedImage bufferedImage, boolean slight) {
        int dw = size.width, dh = size.height;
        try {
            // 截取中间的一部分(有的图片是长方形)
            bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            bufferedImage = slight ? ImageUtils.slightDarker(bufferedImage) : ImageUtils.darker(ImageUtils.doBlur(bufferedImage));
            // 放大至窗口大小
            bufferedImage = dw > dh ? ImageUtils.width(bufferedImage, dw) : ImageUtils.height(bufferedImage, dh);
            int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
            // 裁剪中间的一部分
            bufferedImage = Thumbnails.of(bufferedImage)
                    .scale(1f)
                    .sourceRegion(dw > dh ? 0 : (iw - dw) / 2, dw > dh ? (ih - dh) / 2 : 0, dw, dh)
                    .outputQuality(0.1)
                    .asBufferedImage();
            // 设置圆角
            bufferedImage = ImageUtils.setRadius(bufferedImage, 10);
            mainPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class ConfirmDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public ConfirmDialogPanel() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}