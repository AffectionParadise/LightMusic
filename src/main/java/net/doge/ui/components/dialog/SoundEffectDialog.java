package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Colors;
import net.doge.constants.EqualizerData;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.componentui.VSliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 音效对话框
 * @Date 2020/12/15
 */
public class SoundEffectDialog extends JDialog {
    private final String TITLE = "音效";

    private SoundEffectDialogPanel globalPanel = new SoundEffectDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private JPanel centerPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    private JPanel soundEffectPanel = new JPanel();
    private JPanel sliderPanel = new JPanel();

    private JLabel soundEffectLabel = new JLabel("音效：");
    private JComboBox comboBox = new JComboBox<>();
    private final JPanel[] panels = {
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel(),
            new JPanel()
    };
    private final JLabel[] vals = {
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel()
    };
    private final JSlider[] sliders = {
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider(),
            new JSlider()
    };
    private final JLabel[] hzs = {
            new JLabel("31"),
            new JLabel("62"),
            new JLabel("125"),
            new JLabel("250"),
            new JLabel("500"),
            new JLabel("1k"),
            new JLabel("2k"),
            new JLabel("4k"),
            new JLabel("8k"),
            new JLabel("16k")
    };
    private boolean fitting;

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的文件
    public SoundEffectDialog(PlayerFrame f, boolean isModel) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();

        for (String se : EqualizerData.names) comboBox.addItem(se);
        comboBox.addItem("自定义");
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
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();
        setLocationRelativeTo(f);

        updateBlur();

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

    void initView() {
        centerPanel.setLayout(new BorderLayout());
        // 容器透明
        centerPanel.setOpaque(false);
        soundEffectPanel.setOpaque(false);
        sliderPanel.setOpaque(false);

        // 音效选择面板
        soundEffectLabel.setFont(globalFont);
        soundEffectLabel.setForeground(style.getLabelColor());
        comboBox.setFont(globalFont);
//        comboBox.setPreferredSize(new Dimension(150, 30));
        comboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            String s = (String) comboBox.getSelectedItem();
            // 记录当前音效
            f.currSoundEffectName = s;
            for (int i = 0, len = EqualizerData.names.length; i < len; i++) {
                if (EqualizerData.names[i].equals(s)) {
                    // 记录当前均衡
                    f.ed = EqualizerData.data[i];
                    f.getPlayer().adjustEqualizerBands(EqualizerData.data[i]);
                    fitData(EqualizerData.data[i]);
                    break;
                }
            }
        });
        // 下拉框透明
        comboBox.setOpaque(false);
        // 下拉框 UI
        Color buttonColor = style.getButtonColor();
        comboBox.setUI(new ComboBoxUI(comboBox, f, globalFont, buttonColor));
        // 下拉框边框
        comboBox.setBorder(null);
        soundEffectPanel.add(soundEffectLabel);
        soundEffectPanel.add(comboBox);
        centerPanel.add(soundEffectPanel, BorderLayout.NORTH);

        // 滑动条面板
        sliderPanel.setLayout(new GridLayout(1, 10));
        for (int i = 0, len = panels.length; i < len; i++) {
            JSlider s = sliders[i];
            // 滑动条
            s.setOpaque(false);
            s.setFocusable(false);
            s.setUI(new VSliderUI(s, style.getSliderColor(), style.getSliderColor()));
            s.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            s.setPreferredSize(new Dimension(30, 300));
            s.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            s.setMinimum(EqualizerData.MIN_GAIN);
            s.setMaximum(EqualizerData.MAX_GAIN);
            s.setOrientation(SwingConstants.VERTICAL);
//            s.setPaintTicks(true);
//            s.setPaintLabels(true);
//            s.setMajorTickSpacing(4);
//            s.setMinorTickSpacing(1);
//            s.setSnapToTicks(true);
            s.addChangeListener(e -> {
                // 更新值
                updateVals();
                if (!fitting) {
                    comboBox.setSelectedItem("自定义");
                    // 调整并记录当前均衡
                    f.getPlayer().adjustEqualizerBands(f.ed = getData());
                }
            });

            Color labelColor = style.getLabelColor();
            // 值
            vals[i].setForeground(labelColor);
            vals[i].setFont(globalFont);
            vals[i].setHorizontalAlignment(SwingConstants.CENTER);
            // 频率
            hzs[i].setForeground(labelColor);
            hzs[i].setFont(globalFont);
            hzs[i].setHorizontalAlignment(SwingConstants.CENTER);

            panels[i].setOpaque(false);
            panels[i].setLayout(new BorderLayout());
            panels[i].setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            panels[i].add(vals[i], BorderLayout.NORTH);
            panels[i].add(s, BorderLayout.CENTER);
            panels[i].add(hzs[i], BorderLayout.SOUTH);

            sliderPanel.add(panels[i]);
        }

        // 加载当前音效
        comboBox.setSelectedItem(f.currSoundEffectName);
        // 加载当前均衡
        fitData(f.ed);

        centerPanel.add(sliderPanel, BorderLayout.CENTER);
    }

    // 根据滑动条的值获取均衡数据
    double[] getData() {
        double[] data = new double[EqualizerData.BAND_NUM];
        int i = 0;
        for (JSlider slider : sliders) data[i++] = slider.getValue();
        return data;
    }

    // 根据均衡数据调整滑动条
    void fitData(double[] data) {
        fitting = true;
        for (int i = 0, len = data.length; i < len; i++) sliders[i].setValue((int) data[i]);
        fitting = false;
    }

    // 更新值显示
    void updateVals() {
        for (int i = 0, len = sliders.length; i < len; i++) {
            int val = sliders[i].getValue();
            String s = String.valueOf(val > 0 ? "+" + val : val);
            vals[i].setText(s);
        }
    }

    void doBlur(BufferedImage bufferedImage, boolean slight) {
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
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class SoundEffectDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public SoundEffectDialogPanel() {
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
