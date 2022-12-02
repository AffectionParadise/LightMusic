package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomCheckBox;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.DialogButton;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    private Font font = Fonts.NORMAL;
    private DialogButton yes = new DialogButton("");
    private DialogButton no = new DialogButton("");
    private DialogButton cancel = new DialogButton("");
    private int response;

    // 复选框图标
    private ImageIcon uncheckedIcon = new ImageIcon(SimplePath.ICON_PATH + "unchecked.png");
    private ImageIcon checkedIcon = new ImageIcon(SimplePath.ICON_PATH + "checked.png");

    private PlayerFrame f;
    private String message = "";
    private CustomPanel messagePanel = new CustomPanel();
    private CustomLabel messageLabel = new CustomLabel(message);
    private boolean showCheck;
    private CustomPanel checkPanel = new CustomPanel();
    private CustomCheckBox checkBox = new CustomCheckBox();
    private CustomPanel buttonPanel = new CustomPanel();
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

    public ConfirmDialog(PlayerFrame f, String message, String yesText, String noText, String cancelText, boolean showCheck, String checkText) {
        this(f, message, yesText, noText, cancelText);
        this.showCheck = showCheck;
        checkBox.setText(checkText);
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
        setBackground(Colors.TRANSLUCENT);
        messageLabel.setFont(font);
        yes.setFont(font);
        no.setFont(font);
        cancel.setFont(font);
        checkBox.setFont(font);
        checkBox.setForeground(labelColor);
        checkBox.setIconTextGap(10);
        checkBox.setIcon(ImageUtils.dye(uncheckedIcon, labelColor));
        checkBox.setSelectedIcon(ImageUtils.dye(checkedIcon, labelColor));
        checkPanel.add(checkBox);
        checkPanel.setVisible(showCheck);
        Border eb = BorderFactory.createEmptyBorder(0, 0, 20, 0);
        checkPanel.setBorder(eb);

        messageLabel.setText(StringUtils.textToHtml(message));
        messageLabel.setForeground(labelColor);
        messagePanel.add(messageLabel);
        messagePanel.setBorder(eb);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        buttonPanel.setLayout(fl);

        if (StringUtils.isNotEmpty(yes.getPlainText())) buttonPanel.add(yes);
        if (StringUtils.isNotEmpty(no.getPlainText())) buttonPanel.add(no);
        if (StringUtils.isNotEmpty(cancel.getPlainText())) buttonPanel.add(cancel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(messagePanel, BorderLayout.NORTH);
        mainPanel.add(checkPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 55, 25, 55));

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

        updateBlur();

        setLocationRelativeTo(null);

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (bufferedImage == f.getDefaultAlbumImage()) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG)
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    void close() {
        f.currDialogs.remove(this);
        dispose();
    }

    public int getResponse() {
        return response;
    }

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.getPlayer().loadedMusic();
            // 截取中间的一部分(有的图片是长方形)
            if (loadedMusic && f.blurType == BlurType.CV) bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            if (f.gsOn) bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            if (f.gsOn) bufferedImage = ImageUtils.doBlur(bufferedImage);
            if (f.darkerOn) bufferedImage = ImageUtils.darker(bufferedImage);
            // 放大至窗口大小
            bufferedImage = ImageUtils.width(bufferedImage, dw);
            if (dh > bufferedImage.getHeight())
                bufferedImage = ImageUtils.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (!loadedMusic || f.blurType == BlurType.CV || f.blurType == BlurType.OFF) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtils.forceSize(bufferedImage, dw, dh);
            }
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