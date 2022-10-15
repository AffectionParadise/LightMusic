package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.HSV;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.ui.components.SafeDocument;
import net.doge.ui.componentui.ColorSliderUI;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ColorUtils;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
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
    // 调色板
//    private JPanel palettePanel = new JPanel();
//    private PaletteLabel paletteLabel = new PaletteLabel();
//    public JSlider vSlider = new JSlider();
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

    private JComboBox<String> modelComboBox = new JComboBox();
    // 文本框
    private JLabel rLabel = new JLabel("R：");
    private CustomTextField rTextField = new CustomTextField(3);
    private JLabel gLabel = new JLabel("G：");
    private CustomTextField gTextField = new CustomTextField(3);
    private JLabel bLabel = new JLabel("B：");
    private CustomTextField bTextField = new CustomTextField(3);
    private JLabel hexLabel = new JLabel("Hex：");
    private CustomTextField hexTextField = new CustomTextField(7);

    private DialogButton ok = new DialogButton("确定");
    private DialogButton cancel = new DialogButton("取消");
    private DialogButton reset = new DialogButton("重置");

    // 全局字体
    private Font globalFont = Fonts.NORMAL;
    private Color[] preColors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK, Color.MAGENTA,
            Colors.BRICK_RED, Colors.DEEP_ORANGE, Colors.GOLD3, Colors.SPRING_GREEN, Colors.SKY, Colors.DEEP_BLUE, Colors.PINK3, Colors.ORCHID_3,
            Colors.DEEP_RED, Colors.BROWN, Colors.CARTON, Colors.TEA, Colors.CYAN_4, Colors.DODGER, Colors.PINK4, Colors.ORCHID_4};
    private boolean confirmed;
    private boolean updating;
    public int r;
    public int g;
    public int b;
    public float h;
    public float s;
    public float v;
    public int max1;
    public int max2;
    public int max3;
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
        HSV hsv = ColorUtils.colorToHsv(color);
        this.h = hsv.h;
        this.s = hsv.s;
        this.v = hsv.v;
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
        setSize(600, 580);

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
        else bufferedImage = f.getCurrUIStyle().getImg();
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
//        palettePanel.setOpaque(false);
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
        tfPanel.add(modelComboBox);
        tfPanel.add(rLabel);
        tfPanel.add(rTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(gLabel);
        tfPanel.add(gTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(bLabel);
        tfPanel.add(bTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(hexLabel);
        tfPanel.add(hexTextField);

        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        buttonPanel.add(reset);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        pPanel.add(preLabel);
        preBox.add(pPanel);
        preBox.add(prePanel);

//        palettePanel.setLayout(new BorderLayout());
//        palettePanel.add(paletteLabel, BorderLayout.CENTER);
//        palettePanel.add(vSlider, BorderLayout.EAST);

        customPanel.add(customLabel);
        customBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        customBox.add(customPanel);
        customBox.add(cPanel);

        cPanel.setLayout(new BorderLayout());
        cPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
//        cPanel.add(palettePanel, BorderLayout.NORTH);
        cPanel.add(leftPanel, BorderLayout.CENTER);
        cPanel.add(rightPanel, BorderLayout.EAST);
        cPanel.add(tfPanel, BorderLayout.SOUTH);

        centerPanel.add(preBox, BorderLayout.NORTH);
        centerPanel.add(customBox, BorderLayout.CENTER);

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        Color foreColor = style.getForeColor();
        Color labelColor = style.getLabelColor();
        Color buttonColor = style.getButtonColor();

        // 预设
        preLabel.setHorizontalAlignment(SwingConstants.CENTER);
        preLabel.setFont(globalFont);
        preLabel.setForeground(labelColor);
        GridLayout gl = new GridLayout(3, 8);
        gl.setHgap(15);
        gl.setVgap(15);
        prePanel.setLayout(gl);
        prePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (Color c : preColors) {
            JLabel l = new JLabel();
            l.setIcon(ImageUtils.dyeCircle(50, c));
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
        // 调色板
//        paletteLabel.setPreferredSize(new Dimension(400, 100));
//        vSlider.setOpaque(false);
//        vSlider.setFocusable(false);
//        vSlider.setOrientation(SwingConstants.VERTICAL);
//        vSlider.setUI(new ColorVSliderUI(vSlider, this));
//        vSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        d = new Dimension(50, 100);
//        vSlider.setPreferredSize(d);
//        vSlider.setMinimum(0);
//        vSlider.setMaximum(359);
//        vSlider.addChangeListener(e -> {
//            if (updating) return;
//            h = vSlider.getValue();
//            updateColor(makeColor(h, s, v), true);
//        });

        rlb.setFont(globalFont);
        rlb.setForeground(labelColor);
        rSlider.setOpaque(false);
        rSlider.setFocusable(false);
        rSlider.setUI(new ColorSliderUI(rSlider, this));
        rSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        d = new Dimension(400, 12);
        rSlider.setPreferredSize(d);
        rSlider.setMinimum(0);
        rSlider.addChangeListener(e -> {
            if (updating) return;
            int val = rSlider.getValue();
            boolean rgb = isRGB();
            if (rgb) r = val;
            else h = val;
            updateColor(makeColor(), true);
        });
        glb.setFont(globalFont);
        glb.setForeground(labelColor);
        gSlider.setOpaque(false);
        gSlider.setFocusable(false);
        gSlider.setUI(new ColorSliderUI(gSlider, this));
        gSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gSlider.setPreferredSize(d);
        gSlider.setMinimum(0);
        gSlider.addChangeListener(e -> {
            if (updating) return;
            int val = gSlider.getValue();
            boolean rgb = isRGB();
            if (rgb) g = val;
            else s = val;
            updateColor(makeColor(), true);
        });
        blb.setFont(globalFont);
        blb.setForeground(labelColor);
        bSlider.setOpaque(false);
        bSlider.setFocusable(false);
        bSlider.setUI(new ColorSliderUI(bSlider, this));
        bSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bSlider.setPreferredSize(d);
        bSlider.setMinimum(0);
        bSlider.addChangeListener(e -> {
            if (updating) return;
            int val = bSlider.getValue();
            boolean rgb = isRGB();
            if (rgb) b = val;
            else v = val;
            updateColor(makeColor(), true);
        });

        // 按钮
        ok.setForeColor(buttonColor);
        ok.setFont(globalFont);
        ok.addMouseListener(new ButtonMouseListener(ok, f));
        ok.addActionListener(e -> {
            confirmed = true;
            result = makeColor();
            closeButton.doClick();
        });
        cancel.setForeColor(buttonColor);
        cancel.setFont(globalFont);
        cancel.addMouseListener(new ButtonMouseListener(ok, f));
        cancel.addActionListener(e -> {
            closeButton.doClick();
        });
        reset.setForeColor(buttonColor);
        reset.setFont(globalFont);
        reset.addMouseListener(new ButtonMouseListener(ok, f));
        reset.addActionListener(e -> {
            updateColor(source);
        });

        // 下拉框
        modelComboBox.setFont(globalFont);
        modelComboBox.setOpaque(false);
        modelComboBox.setFocusable(false);
        modelComboBox.setUI(new ComboBoxUI(modelComboBox, f, globalFont, buttonColor, 80));
        modelComboBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        modelComboBox.addItem("RGB");
        modelComboBox.addItem("HSV");
        modelComboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            updateColorModel();
        });
        // 文本框
        rLabel.setForeground(labelColor);
        rLabel.setFont(globalFont);
        rTextField.setOpaque(false);
        rTextField.setFont(globalFont);
        rTextField.setForeground(foreColor);
        rTextField.setCaretColor(foreColor);

        gLabel.setForeground(labelColor);
        gLabel.setFont(globalFont);
        gTextField.setOpaque(false);
        gTextField.setFont(globalFont);
        gTextField.setForeground(foreColor);
        gTextField.setCaretColor(foreColor);

        bLabel.setForeground(labelColor);
        bLabel.setFont(globalFont);
        bTextField.setOpaque(false);
        bTextField.setFont(globalFont);
        bTextField.setForeground(foreColor);
        bTextField.setCaretColor(foreColor);

        hexLabel.setForeground(labelColor);
        hexLabel.setFont(globalFont);
        hexTextField.setOpaque(false);
        hexTextField.setFont(globalFont);
        hexTextField.setForeground(foreColor);
        hexTextField.setCaretColor(foreColor);
        SafeDocument doc = new SafeDocument(7);
        doc.addDocumentListener(this);
        hexTextField.setDocument(doc);

        updateColorModel();
    }

    public boolean isRGB() {
        return modelComboBox.getSelectedIndex() == 0;
    }

    public boolean isHSV() {
        return modelComboBox.getSelectedIndex() == 1;
    }

    public Color makeColor(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public Color makeColor(float h, float s, float v) {
        return ColorUtils.hsvToColor(h, s, v);
    }

    public Color makeColor() {
        return isRGB() ? makeColor(r, g, b) : makeColor(h, s, v);
    }

    // 改变颜色模型
    void updateColorModel() {
        updating = true;
        if (isRGB()) {
            max1 = max2 = max3 = 255;
            rlb.setText("R");
            glb.setText("G");
            blb.setText("B");
            rLabel.setText("R：");
            gLabel.setText("G：");
            bLabel.setText("B：");
        } else {
            max1 = 359;
            max2 = max3 = 100;
            rlb.setText("H");
            glb.setText("S");
            blb.setText("V");
            rLabel.setText("H：");
            gLabel.setText("S：");
            bLabel.setText("V：");
        }

        rSlider.setMaximum(max1);
        gSlider.setMaximum(max2);
        bSlider.setMaximum(max3);

        SafeDocument doc = new SafeDocument(0, max1);
        doc.addDocumentListener(this);
        rTextField.setDocument(doc);
        doc = new SafeDocument(0, max2);
        doc.addDocumentListener(this);
        gTextField.setDocument(doc);
        doc = new SafeDocument(0, max3);
        doc.addDocumentListener(this);
        bTextField.setDocument(doc);

        updateColor(makeColor());
        updating = false;
    }

    // 颜色更新时更新界面
    void updateUI() {
//        paletteLabel.repaint();
//        vSlider.repaint();
        rSlider.repaint();
        gSlider.repaint();
        bSlider.repaint();
        view.setIcon(ImageUtils.dyeCircle(80, makeColor()));
//        paletteLabel.setIcon(ImageUtils.palette(h, 400));
        try {
            boolean rgb = isRGB();
            rTextField.setText(String.valueOf(rgb ? r : (int) h));
            gTextField.setText(String.valueOf(rgb ? g : (int) s));
            bTextField.setText(String.valueOf(rgb ? b : (int) v));
            hexTextField.setText(ColorUtils.toHex(makeColor()));
        } catch (Exception e) {

        }
    }

    void updateColor(Color color) {
        updateColor(color, false);
    }

    // 更新 RGB 颜色
    void updateColor(Color color, boolean sliderRequest) {
        updating = true;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        boolean rgb = isRGB();
        // 针对 HSV 防止因转换误差带来的滑动条调整
        if (rgb || !sliderRequest) {
            HSV hsv = ColorUtils.colorToHsv(color);
            h = hsv.h;
            s = hsv.s;
            v = hsv.v;
        }
//        paletteLabel.locateSV(s, v);
//        vSlider.setValue((int) h);
        if (rgb) {
            rSlider.setValue(r);
            gSlider.setValue(g);
            bSlider.setValue(b);
        } else {
            rSlider.setValue((int) h);
            gSlider.setValue((int) s);
            bSlider.setValue((int) v);
        }
        updateUI();
        updating = false;
    }

    void checkDocument(DocumentEvent e) {
        Document doc = e.getDocument();
        JTextField tf = null;
        boolean d1 = doc == rTextField.getDocument(), d2 = doc == gTextField.getDocument(),
                d3 = doc == bTextField.getDocument(), d4 = doc == hexTextField.getDocument();
        if (d1) tf = rTextField;
        else if (d2) tf = gTextField;
        else if (d3) tf = bTextField;
        else if (d4) {
            tf = hexTextField;
            String text = tf.getText();
            Color color = ColorUtils.hexToColor(text);
            if (color != null) updateColor(color);
            return;
        }

        String text = tf.getText();
        if (StringUtils.isEmpty(text)) return;
        int i = Integer.parseInt(text);
        boolean rgb = isRGB();
        if (d1) rSlider.setValue(rgb ? (r = i) : (int) (h = i));
        else if (d2) gSlider.setValue(rgb ? (g = i) : (int) (s = i));
        else if (d3) bSlider.setValue(rgb ? (b = i) : (int) (v = i));
//        vSlider.setValue((int) h);
        updateUI();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!updating) checkDocument(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (!updating) checkDocument(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

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

//    private class PaletteLabel extends JLabel {
//        private Color currColor;
//        private Point point;
//
//        public PaletteLabel() {
//            addMouseListener(new MouseAdapter() {
//                @Override
//                public void mousePressed(MouseEvent e) {
//                    update(e.getPoint());
//                }
//            });
//            addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseDragged(MouseEvent e) {
//                    update(e.getPoint());
//                }
//            });
//        }
//
//        // 定位颜色
//        void locateSV(float s, float v) {
//            ImageIcon icon = (ImageIcon) paletteLabel.getIcon();
//            if (icon == null) return;
//            BufferedImage img = (BufferedImage) icon.getImage();
//            int w = img.getWidth(), h = img.getHeight();
//            for (int i = 0; i < h; i++) {
//                for (int j = 0; j < w; j++) {
//                    if(img.getRGB(i,j) == ColorUtils.merge(r,g,b)){
//                        point = new Point(i, j);
//                        currColor = new Color(img.getRGB(i, j));
//                        repaint();
//                        return;
//                    }
//                }
//            }
//        }
//
//        // 更新某点光标
//        void update(Point p) {
//            BufferedImage img = (BufferedImage) ((ImageIcon) paletteLabel.getIcon()).getImage();
//            int w = img.getWidth(), h = img.getHeight();
//            if (p.x < 0) p.x = 0;
//            else if (p.x >= w) p.x = w - 1;
//            if (p.y < 0) p.y = 0;
//            else if (p.y >= h) p.y = h - 1;
//            point = p;
//            int rgb = img.getRGB(point.x, point.y);
//            currColor = new Color(rgb);
//            updateColor(currColor);
//            repaint();
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            // 画一个圆环光标
//            if (point == null) return;
//            Graphics2D g2d = (Graphics2D) paletteLabel.getGraphics();
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            final int outerRadius = 6, innerRadius = 3;
//            g2d.setColor(Colors.WHITE);
//            g2d.fillOval(point.x - outerRadius, point.y - outerRadius, outerRadius * 2, outerRadius * 2);
//            g2d.setColor(currColor);
//            g2d.fillOval(point.x - innerRadius, point.y - innerRadius, innerRadius * 2, innerRadius * 2);
//        }
//    }

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
