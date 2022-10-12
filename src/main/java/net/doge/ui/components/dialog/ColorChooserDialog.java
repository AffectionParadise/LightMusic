package net.doge.ui.components.dialog;

import javafx.scene.layout.HBox;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.HSV;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.ui.componentui.ColorSliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.ui.listeners.ColorControlInputListener;
import net.doge.ui.listeners.ControlInputListener;
import net.doge.ui.listeners.LengthControlInputListener;
import net.doge.utils.ColorUtils;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 颜色选择对话框
 * @Date 2020/12/15
 */
public class ColorChooserDialog extends JDialog implements DocumentListener {
    private final String TITLE = "选择颜色";

    private ColorChooserDialogPanel globalPanel = new ColorChooserDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private JPanel centerPanel = new JPanel();
    private JPanel cPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel tfPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    // 预定义颜色面板
    private Box preBox = Box.createVerticalBox();
    private JPanel prePanel = new JPanel();
    // 预设标签
    private JPanel pPanel = new JPanel();
    private JLabel preLabel = new JLabel("预设");

    private Box customBox = Box.createVerticalBox();
    // 自定义标签
    private JPanel customPanel = new JPanel();
    private JLabel customLabel = new JLabel("自定义");
    // r
    private JLabel rlb = new JLabel("R");
    public JSlider rSlider = new JSlider();
    // g
    private JLabel glb = new JLabel("G");
    public JSlider gSlider = new JSlider();
    // b
    private JLabel blb = new JLabel("B");
    public JSlider bSlider = new JSlider();
    // 显示
    private JLabel view = new JLabel();

    // 文本框
    private JLabel rLabel = new JLabel("R：");
    private CustomTextField rTextField = new CustomTextField(3);
    private JLabel gLabel = new JLabel("G：");
    private CustomTextField gTextField = new CustomTextField(3);
    private JLabel bLabel = new JLabel("B：");
    private CustomTextField bTextField = new CustomTextField(3);
    private JLabel webLabel = new JLabel("Web：");
    private CustomTextField webTextField = new CustomTextField(7);

    private DialogButton ok = new DialogButton("确定");
    private DialogButton cancel = new DialogButton("取消");
    private DialogButton reset = new DialogButton("重置");

    // 全局字体
    private Font globalFont = Fonts.NORMAL;
    private Color[] preColors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK, Color.MAGENTA,
            Colors.BRICK_RED, Colors.DEEP_ORANGE, Colors.GOLD3, Colors.SPRING_GREEN, Colors.CYAN_4, Colors.DEEP_BLUE, Colors.PINK3, Colors.ORCHID_3};
    private boolean confirmed;
    public int r;
    public int g;
    public int b;
    private Color source;
    private Color result;

    private PlayerFrame f;
    private UIStyle style;

    // 父窗口是否是模态
    public ColorChooserDialog(PlayerFrame f, Color color) {
        super(f, true);
        this.f = f;
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.source = color;
        this.style = f.getCurrUIStyle();
    }

    public void showDialog() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });

        setTitle(TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 500);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        setLocationRelativeTo(f);
        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.getIsBlur() && f.getPlayer().loadedMusic()) bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
        else {
            String styleImgPath = f.getCurrUIStyle().getStyleImgPath();
            if (StringUtils.isNotEmpty(styleImgPath)) bufferedImage = f.getCurrUIStyle().getImg();
            else bufferedImage = ImageUtils.dyeRect(1, 1, f.getCurrUIStyle().getBgColor());
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage);
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setOpaque(false);
        titleLabel.setFont(globalFont);
        titleLabel.setText(TITLE);
        closeButton.setIcon(ImageUtils.dye(closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(closeWindowIcon.getIconWidth() + 2, closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            f.currDialogs.remove(this);
            dispose();
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        // 不能聚焦
        closeButton.setFocusable(false);
        // 无填充
        closeButton.setContentAreaFilled(false);
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(40, 30));
        windowCtrlPanel.add(closeButton);
        windowCtrlPanel.setOpaque(false);
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Color getResult() {
        return result;
    }

    void initView() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        // 容器透明
        prePanel.setOpaque(false);
        pPanel.setOpaque(false);
        customPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        cPanel.setOpaque(false);
        leftPanel.setOpaque(false);
        rightPanel.setOpaque(false);
        tfPanel.setOpaque(false);
        buttonPanel.setOpaque(false);

        Dimension d = new Dimension(1, 10);
        Box rBox = Box.createHorizontalBox();
        rBox.add(rlb);
        rBox.add(rSlider);
        leftPanel.add(rBox);
        leftPanel.add(Box.createRigidArea(d));
        Box gBox = Box.createHorizontalBox();
        gBox.add(glb);
        gBox.add(gSlider);
        leftPanel.add(gBox);
        leftPanel.add(Box.createRigidArea(d));
        Box bBox = Box.createHorizontalBox();
        bBox.add(blb);
        bBox.add(bSlider);
        leftPanel.add(bBox);

        rightPanel.add(view);

        d = new Dimension(10, 1);
        tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.X_AXIS));
        tfPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tfPanel.add(rLabel);
        tfPanel.add(rTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(gLabel);
        tfPanel.add(gTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(bLabel);
        tfPanel.add(bTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(webLabel);
        tfPanel.add(webTextField);

        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        buttonPanel.add(reset);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        pPanel.add(preLabel);
        preBox.add(pPanel);
        preBox.add(prePanel);

        customPanel.add(customLabel);
        customBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        customBox.add(customPanel);
        customBox.add(cPanel);

        cPanel.setLayout(new BorderLayout());
        cPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cPanel.add(leftPanel, BorderLayout.CENTER);
        cPanel.add(rightPanel, BorderLayout.EAST);
        cPanel.add(tfPanel, BorderLayout.SOUTH);

        centerPanel.add(preBox, BorderLayout.NORTH);
        centerPanel.add(customBox, BorderLayout.CENTER);

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        Color foreColor = style.getForeColor();
        Color labelColor = style.getLabelColor();

        // 预设
        preLabel.setHorizontalAlignment(SwingConstants.CENTER);
        preLabel.setFont(globalFont);
        preLabel.setForeground(labelColor);
        GridLayout gl = new GridLayout(2, 8);
        gl.setHgap(15);
        gl.setVgap(15);
        prePanel.setLayout(gl);
        prePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (Color c : preColors) {
            JLabel l = new JLabel();
            l.setIcon(ImageUtils.dyeRoundRect(50, 50, c));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            l.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    updateColor(c);
                }
            });
            prePanel.add(l);
        }
        // 自定义
        customLabel.setHorizontalAlignment(SwingConstants.CENTER);
        customLabel.setFont(globalFont);
        customLabel.setForeground(labelColor);
        rlb.setFont(globalFont);
        rlb.setForeground(labelColor);
        rSlider.setOpaque(false);
        rSlider.setFocusable(false);
        rSlider.setUI(new ColorSliderUI(rSlider, this));
        rSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        d = new Dimension(400, 12);
        rSlider.setPreferredSize(d);
        rSlider.setMinimum(0);
        rSlider.setMaximum(255);
        rSlider.setValue(r);
        rSlider.addChangeListener(e -> {
            r = rSlider.getValue();
            updateUI();
        });
        glb.setFont(globalFont);
        glb.setForeground(labelColor);
        gSlider.setOpaque(false);
        gSlider.setFocusable(false);
        gSlider.setUI(new ColorSliderUI(gSlider, this));
        gSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gSlider.setPreferredSize(d);
        gSlider.setMinimum(0);
        gSlider.setMaximum(255);
        gSlider.setValue(g);
        gSlider.addChangeListener(e -> {
            g = gSlider.getValue();
            updateUI();
        });
        blb.setFont(globalFont);
        blb.setForeground(labelColor);
        bSlider.setOpaque(false);
        bSlider.setFocusable(false);
        bSlider.setUI(new ColorSliderUI(bSlider, this));
        bSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bSlider.setPreferredSize(d);
        bSlider.setMinimum(0);
        bSlider.setMaximum(255);
        bSlider.setValue(b);
        bSlider.addChangeListener(e -> {
            b = bSlider.getValue();
            updateUI();
        });

        // 按钮
        ok.setForeColor(style.getButtonColor());
        ok.setFont(globalFont);
        ok.addMouseListener(new ButtonMouseListener(ok, f));
        ok.addActionListener(e -> {
            confirmed = true;
            result = new Color(r, g, b);
            closeButton.doClick();
        });
        cancel.setForeColor(style.getButtonColor());
        cancel.setFont(globalFont);
        cancel.addMouseListener(new ButtonMouseListener(ok, f));
        cancel.addActionListener(e -> {
            closeButton.doClick();
        });
        reset.setForeColor(style.getButtonColor());
        reset.setFont(globalFont);
        reset.addMouseListener(new ButtonMouseListener(ok, f));
        reset.addActionListener(e -> {
            updateColor(source);
        });

        // 文本框
        rLabel.setForeground(labelColor);
        rLabel.setFont(globalFont);
        rTextField.setOpaque(false);
        rTextField.setFont(globalFont);
        rTextField.setForeground(foreColor);
        rTextField.setCaretColor(foreColor);
        rTextField.addKeyListener(new ColorControlInputListener(rTextField));
        rTextField.getDocument().addDocumentListener(this);
        gLabel.setForeground(labelColor);
        gLabel.setFont(globalFont);
        gTextField.setOpaque(false);
        gTextField.setFont(globalFont);
        gTextField.setForeground(foreColor);
        gTextField.setCaretColor(foreColor);
        gTextField.addKeyListener(new ColorControlInputListener(gTextField));
        gTextField.getDocument().addDocumentListener(this);
        bLabel.setForeground(labelColor);
        bLabel.setFont(globalFont);
        bTextField.setOpaque(false);
        bTextField.setFont(globalFont);
        bTextField.setForeground(foreColor);
        bTextField.setCaretColor(foreColor);
        bTextField.addKeyListener(new ColorControlInputListener(bTextField));
        bTextField.getDocument().addDocumentListener(this);
        webLabel.setForeground(labelColor);
        webLabel.setFont(globalFont);
        webTextField.setOpaque(false);
        webTextField.setFont(globalFont);
        webTextField.setForeground(foreColor);
        webTextField.setCaretColor(foreColor);
        webTextField.addKeyListener(new LengthControlInputListener(webTextField, 7));
        webTextField.getDocument().addDocumentListener(this);
        updateUI();
    }

    // 颜色更新时更新界面
    void updateUI() {
        rSlider.repaint();
        gSlider.repaint();
        bSlider.repaint();
        view.setIcon(ImageUtils.dyeRoundRect(80, 80, new Color(r, g, b)));
        try {
            rTextField.setText(String.valueOf(r));
            gTextField.setText(String.valueOf(g));
            bTextField.setText(String.valueOf(b));
            webTextField.setText(ColorUtils.toHex(new Color(r, g, b)));
        } catch (Exception e) {

        }
    }

    // 更新颜色
    void updateColor(Color color) {
        rSlider.setValue(r = color.getRed());
        gSlider.setValue(g = color.getGreen());
        bSlider.setValue(b = color.getBlue());
        updateUI();
    }

//    public static void main(String[] args) {
//        ColorChooserDialog colorChooserDialog = new ColorChooserDialog(null, Colors.THEME);
//        colorChooserDialog.showDialog();
//    }

    void doBlur(BufferedImage bufferedImage) {
        Dimension size = getSize();
        int dw = size.width, dh = size.height;
        try {
            // 截取中间的一部分(有的图片是长方形)
            bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            bufferedImage = ImageUtils.darker(ImageUtils.doBlur(bufferedImage));
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
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void checkDocument(DocumentEvent e) {
        Document doc = e.getDocument();
        JTextField tf = null;
        if (doc == rTextField.getDocument()) tf = rTextField;
        else if (doc == gTextField.getDocument()) tf = gTextField;
        else if (doc == bTextField.getDocument()) tf = bTextField;
        else if (doc == webTextField.getDocument()) {
            tf = webTextField;
            String text = tf.getText();
            Color color = ColorUtils.hexToColor(text);
            if (color != null) updateColor(color);
            return;
        }

        String text = tf.getText();
        if (StringUtils.isEmpty(text)) return;
        int i = Integer.parseInt(text);
        if (text.length() > 3 || i < 0 || i > 255) return;
        if (doc == rTextField.getDocument()) rSlider.setValue(r = i);
        else if (doc == gTextField.getDocument()) gSlider.setValue(g = i);
        else if (doc == bTextField.getDocument()) bSlider.setValue(b = i);
        updateUI();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        checkDocument(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkDocument(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    private class ColorChooserDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public ColorChooserDialogPanel() {
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
