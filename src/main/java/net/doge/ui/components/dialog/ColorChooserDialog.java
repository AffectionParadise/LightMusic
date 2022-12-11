package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.models.HSV;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
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
import java.awt.event.ItemEvent;
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

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel cPanel = new CustomPanel();
    private CustomPanel leftPanel = new CustomPanel();
    private CustomPanel rightPanel = new CustomPanel();
    private CustomPanel tfPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton();

    // 预定义颜色面板
    private Box preBox = Box.createVerticalBox();
    private CustomPanel prePanel = new CustomPanel();
    // 预设标签
    private CustomPanel pPanel = new CustomPanel();
    private CustomLabel preLabel = new CustomLabel("预设");

    private Box customBox = Box.createVerticalBox();
    // 自定义标签
    private CustomPanel customPanel = new CustomPanel();
    private CustomLabel customLabel = new CustomLabel("自定义");
    // 调色板
//    private CustomPanel palettePanel = new CustomPanel();
//    private PaletteLabel paletteLabel = new PaletteLabel();
//    public CustomSlider vSlider = new CustomSlider();
    // r
    private CustomLabel rlb = new CustomLabel("R");
    public CustomSlider rSlider = new CustomSlider();
    // g
    private CustomLabel glb = new CustomLabel("G");
    public CustomSlider gSlider = new CustomSlider();
    // b
    private CustomLabel blb = new CustomLabel("B");
    public CustomSlider bSlider = new CustomSlider();
    // 显示
    private CustomLabel view = new CustomLabel();

    private CustomComboBox<String> modelComboBox = new CustomComboBox();
    // 文本框
    private CustomLabel rLabel = new CustomLabel("R：");
    private CustomTextField rTextField = new CustomTextField(3);
    private CustomLabel gLabel = new CustomLabel("G：");
    private CustomTextField gTextField = new CustomTextField(3);
    private CustomLabel bLabel = new CustomLabel("B：");
    private CustomTextField bTextField = new CustomTextField(3);
    private CustomLabel hexLabel = new CustomLabel("Hex：");
    private CustomTextField hexTextField = new CustomTextField(7);

    private DialogButton ok;
    private DialogButton cancel;
    private DialogButton reset;

    private final Color[] preColors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK, Color.MAGENTA,
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

        Color buttonColor = style.getButtonColor();
        ok = new DialogButton("确定", buttonColor);
        cancel = new DialogButton("取消", buttonColor);
        reset = new DialogButton("重置", buttonColor);
    }

    public void showDialog() {
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
        setLocationRelativeTo(null);
        updateBlur();

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

    // 初始化标题栏
    private void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setText(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        closeButton.setIcon(ImageUtils.dye(f.closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + 2, f.closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            f.currDialogs.remove(this);
            dispose();
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(40, 30));
        windowCtrlPanel.add(closeButton);
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

    private void initView() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

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
        preLabel.setForeground(labelColor);
        GridLayout gl = new GridLayout(3, 8);
        gl.setHgap(15);
        gl.setVgap(15);
        prePanel.setLayout(gl);
        prePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (Color c : preColors) {
            CustomLabel l = new CustomLabel();
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
        customLabel.setForeground(labelColor);
        // 调色板
//        paletteLabel.setPreferredSize(new Dimension(400, 100));
//        vSlider.setOrientation(SwingConstants.VERTICAL);
//        vSlider.setUI(new ColorVSliderUI(vSlider, this));
//        d = new Dimension(50, 100);
//        vSlider.setPreferredSize(d);
//        vSlider.setMinimum(0);
//        vSlider.setMaximum(359);
//        vSlider.addChangeListener(e -> {
//            if (updating) return;
//            h = vSlider.getValue();
//            updateColor(makeColor(h, s, v), true);
//        });

        rlb.setForeground(labelColor);
        rSlider.setUI(new ColorSliderUI(rSlider, this));
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
        glb.setForeground(labelColor);
        gSlider.setUI(new ColorSliderUI(gSlider, this));
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
        blb.setForeground(labelColor);
        bSlider.setUI(new ColorSliderUI(bSlider, this));
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
        ok.addMouseListener(new ButtonMouseListener(ok, f));
        ok.addActionListener(e -> {
            confirmed = true;
            result = makeColor();
            closeButton.doClick();
        });
        cancel.addMouseListener(new ButtonMouseListener(ok, f));
        cancel.addActionListener(e -> {
            closeButton.doClick();
        });
        reset.addMouseListener(new ButtonMouseListener(ok, f));
        reset.addActionListener(e -> {
            updateColor(source);
        });

        // 下拉框
        modelComboBox.setUI(new ComboBoxUI(modelComboBox, f, buttonColor, 80));
        modelComboBox.addItem("RGB");
        modelComboBox.addItem("HSV");
        modelComboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            updateColorModel();
        });
        // 文本框
        rLabel.setForeground(labelColor);
        rTextField.setForeground(foreColor);
        rTextField.setCaretColor(foreColor);

        gLabel.setForeground(labelColor);
        gTextField.setForeground(foreColor);
        gTextField.setCaretColor(foreColor);

        bLabel.setForeground(labelColor);
        bTextField.setForeground(foreColor);
        bTextField.setCaretColor(foreColor);

        hexLabel.setForeground(labelColor);
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
    private void updateColorModel() {
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
    private void updateUI() {
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

    private void updateColor(Color color) {
        updateColor(color, false);
    }

    // 更新 RGB 颜色
    private void updateColor(Color color, boolean sliderRequest) {
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

    private void checkDocument(DocumentEvent e) {
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

        if (!d4) hexTextField.setText(ColorUtils.toHex(makeColor(r, g, b)));
        else updateUI();
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
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

//    private class PaletteLabel extends CustomLabel {
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
