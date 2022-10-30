package net.doge.ui.components.dialog;

import javafx.embed.swing.JFXPanel;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Fonts;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.DialogButton;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author yzx
 * @Description 捐赠对话框
 * @Date 2021/1/5
 */
public class DonateDialog extends JDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    private Font globalFont = Fonts.NORMAL;
    private DialogButton yes = new DialogButton("确定");

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    // 标题
    private String title = "捐赠";

    // 标题面板
    private Box topBox = new Box(BoxLayout.X_AXIS);
    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    // 收款码
    private ImageIcon weixinIcon = new ImageIcon(SimplePath.ICON_PATH + "weixin.png");
    private ImageIcon alipayIcon = new ImageIcon(SimplePath.ICON_PATH + "alipay.png");

    private PlayerFrame f;
    private UIStyle style;
    private JPanel messagePanel = new JPanel();
    private JLabel messageLabel = new JLabel("如果您觉得这款软件还不错，可以请作者喝杯咖啡~~", JLabel.CENTER);
    private JPanel centerPanel = new JPanel();
    private JPanel cPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JLabel weixinLabel = new JLabel("微信");
    private JPanel rightPanel = new JPanel();
    private JLabel alipayLabel = new JLabel("支付宝");
    private JPanel buttonPanel = new JPanel();
    private ConfirmDialogPanel mainPanel = new ConfirmDialogPanel();

    public DonateDialog(PlayerFrame f) {
        // 一定要是模态对话框才能接收值！！！
        super(f, true);
        this.f = f;
        this.style = f.getCurrUIStyle();
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setOpaque(false);
        titleLabel.setFont(globalFont);
        titleLabel.setText(StringUtils.textToHtml(title));
        titleLabel.setPreferredSize(new Dimension(600, 30));
        closeButton.setIcon(ImageUtils.dye(closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(closeWindowIcon.getIconWidth() + 2, closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> close());
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        // 不能聚焦
        closeButton.setFocusable(false);
        // 无填充
        closeButton.setContentAreaFilled(false);
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(30, 30));
        windowCtrlPanel.add(closeButton);
        windowCtrlPanel.setOpaque(false);
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topBox.add(topPanel);
        topBox.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        mainPanel.add(topBox, BorderLayout.NORTH);
    }

    public void showDialog() {
        Color labelColor = f.getCurrUIStyle().getLabelColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        messageLabel.setFont(globalFont);
        weixinLabel.setFont(globalFont);
        alipayLabel.setFont(globalFont);
        yes.setFont(globalFont);

        messageLabel.setForeground(labelColor);
        weixinLabel.setForeground(labelColor);
        alipayLabel.setForeground(labelColor);

        messagePanel.add(messageLabel);
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        weixinLabel.setVerticalTextPosition(SwingConstants.TOP);
        alipayLabel.setVerticalTextPosition(SwingConstants.TOP);
        weixinLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        alipayLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        final int gap = 15;
        weixinLabel.setIconTextGap(gap);
        alipayLabel.setIconTextGap(gap);
        weixinLabel.setIcon(weixinIcon);
        alipayLabel.setIcon(alipayIcon);
        leftPanel.add(weixinLabel);
        rightPanel.add(alipayLabel);
        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        cPanel.setOpaque(false);
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.X_AXIS));
        cPanel.add(leftPanel);
        cPanel.add(rightPanel);
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(messagePanel, BorderLayout.NORTH);
        centerPanel.add(cPanel, BorderLayout.CENTER);

        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        buttonPanel.setLayout(fl);
        buttonPanel.setOpaque(false);
        buttonPanel.add(yes);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        yes.addActionListener(e -> close());

        initTitleBar();
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        pack();

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

    void doBlur(BufferedImage bufferedImage, boolean slight) {
        int dw = getWidth(), dh = getHeight();
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