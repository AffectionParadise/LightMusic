package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Colors;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.constants.Fonts;
import net.doge.constants.Format;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @Author yzx
 * @Description 自定义样式的对话框
 * @Date 2020/12/15
 */
public class CustomStyleDialog extends JDialog implements DocumentListener {
    private final String TITLE = "自定义主题";
    private final int imgWidth = 150;
    private final int imgHeight = 100;
    private final int rectWidth = 170;
    private final int rectHeight = 30;
    // 风格名称必填提示
    private final String STYLE_NAME_NOT_NULL_MSG = "emmm~~主题名称不能为无名氏哦";
    // 风格名称重复提示
    private final String STYLE_NAME_DUPLICATE_MSG = "emmm~该主题名称已存在，换一个吧";
    // 图片文件不存在提示
    private final String IMG_FILE_NOT_EXIST_MSG = "选定的图片路径无效";
    private CustomStyleDialogPanel globalPanel = new CustomStyleDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private JPanel centerPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    private final JLabel[] labels = {
            new JLabel("主题名称："),
            new JLabel("背景图片："),
            new JLabel("列表前景色："),
            new JLabel("列表选中项颜色："),
            new JLabel("歌词文字颜色："),
            new JLabel("歌词高亮颜色："),
            new JLabel("文字标签颜色："),
            new JLabel("进度条颜色："),
            new JLabel("按钮颜色："),
            new JLabel("滚动条颜色："),
            new JLabel("音量滑动条颜色："),
            new JLabel("频谱颜色："),
            new JLabel("菜单项颜色：")
    };

    private final Component[] components = {
            new CustomTextField(15),
            new DialogButton("选择图片"),
            new JLabel(),
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
    private DialogButton pureColor = new DialogButton("纯色");

    private DialogButton okButton;

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;
    // 面板展示的样式
    private UIStyle showedStyle;

    private boolean confirmed = false;
    private Object[] results = new Object[components.length];

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的样式(添加则用当前样式，编辑则用选中样式)
    public CustomStyleDialog(PlayerFrame f, boolean isModel, String okButtonText, UIStyle style) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();
        this.showedStyle = style;

        Color buttonColor = style.getButtonColor();
        okButton = new DialogButton(okButtonText, buttonColor);
    }

    public void showDialog() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
        okButton.addActionListener(e -> {
            // 风格名称不为空
            if (results[0].equals("")) {
                new TipDialog(f, STYLE_NAME_NOT_NULL_MSG).showDialog();
                return;
            }
            // 风格名称不重复
            List<UIStyle> styles = f.getStyles();
            for (UIStyle style : styles) {
                // 添加时，名称一定不相等；编辑时，只允许同一样式名称相等
                if (style.getStyleName().equals(results[0])
                        && (style != showedStyle || okButton.getText().contains("添加"))) {
                    new TipDialog(f, STYLE_NAME_DUPLICATE_MSG).showDialog();
                    return;
                }
            }
            // 更新菜单项文字显示
//            if (okButton.getText().contains("更新")) {
//                List<CustomRadioButtonMenuItem> mis = f.getStylePopupMenuItems();
//                String oriName = showedStyle.getStyleName();
//                for (int i = mis.size() - 1; i >= 0; i--) {
//                    CustomRadioButtonMenuItem mi = mis.get(i);
//                    if (mi.getText().trim().equals(oriName)) {
//                        mi.setText(results[0] + "     ");
//                        break;
//                    }
//                }
//            }
            // 图片路径
            if (results[1] instanceof String) {
                // 复制图片(如果有)
                File imgFile = new File(((String) results[1]));
                // 图片存在且是文件
                if (imgFile.exists() && imgFile.isFile()) {
                    try {
                        // 文件夹不存在就创建
                        File dir = new File(SimplePath.CUSTOM_STYLE_IMG_PATH);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        String newPath = SimplePath.CUSTOM_STYLE_IMG_PATH + imgFile.getName();
                        Files.copy(
                                Paths.get(imgFile.getPath()),
                                Paths.get(newPath),
                                StandardCopyOption.REPLACE_EXISTING
                        );
                        // 设置新的路径
                        results[1] = newPath;
                        // 更新时删除原来的图片
                        String imgPath = style.getStyleImgPath();
                        if (StringUtils.isNotEmpty(imgPath)) {
                            File sf = new File(imgPath);
                            File df = new File(newPath);
                            if (style == showedStyle && !sf.equals(df) && sf.getParentFile().equals(dir)) sf.delete();
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else {
                    new TipDialog(f, IMG_FILE_NOT_EXIST_MSG).showDialog();
                    return;
                }
            }
            confirmed = true;
            dispose();
            f.currDialogs.remove(this);
        });
        okButton.setFont(globalFont);
        buttonPanel.add(okButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

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
            dispose();
            f.currDialogs.remove(this);
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
        centerPanel.setLayout(new GridLayout(7, 2));

        // 容器透明
        centerPanel.setOpaque(false);
        buttonPanel.setOpaque(false);

        // 获得传入的界面样式
        results[0] = showedStyle.getStyleName();
        String styleImgPath = showedStyle.getStyleImgPath();
        results[1] = StringUtils.isEmpty(styleImgPath) ? showedStyle.getBgColor() : styleImgPath;
        results[2] = showedStyle.getForeColor();
        results[3] = showedStyle.getSelectedColor();
        results[4] = showedStyle.getLrcColor();
        results[5] = showedStyle.getHighlightColor();
        results[6] = showedStyle.getLabelColor();
        results[7] = showedStyle.getTimeBarColor();
        results[8] = showedStyle.getButtonColor();
        results[9] = showedStyle.getScrollBarColor();
        results[10] = showedStyle.getSliderColor();
        results[11] = showedStyle.getSpectrumColor();
        results[12] = showedStyle.getMenuItemColor();

        Border eb = BorderFactory.createEmptyBorder(0, 20, 0, 20);
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setOpaque(false);
            panel.setBorder(eb);
            // 添加标签
            labels[i].setForeground(style.getLabelColor());
            labels[i].setFont(globalFont);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof CustomTextField) {
                CustomTextField component = (CustomTextField) components[i];
                Color foreColor = style.getForeColor();
//                component.addFocusListener(new JTextFieldHintListener(component, "", foreColor));
                component.setForeground(foreColor);
                component.setCaretColor(foreColor);
                component.setOpaque(false);
                component.setFont(globalFont);
                // 加载风格名称
                component.setText((String) results[i]);
                Document document = component.getDocument();
                // 添加文本改变监听器
                document.addDocumentListener(this);
            } else if (components[i] instanceof DialogButton) {
                DialogButton component = (DialogButton) components[i];
                component.setForeColor(style.getButtonColor());
                component.setFont(globalFont);
                labels[i].setHorizontalTextPosition(SwingConstants.LEFT);
                // 加载当前样式背景图(显示一个缩略图)
                if (results[i] != null) {
                    if (results[i] instanceof String) {
                        BufferedImage image = ImageUtils.read((String) results[i]);
                        if (image != null) {
                            if (image.getWidth() >= image.getHeight())
                                labels[i].setIcon(new ImageIcon(ImageUtils.width(image, imgWidth)));
                            else labels[i].setIcon(new ImageIcon(ImageUtils.height(image, imgHeight)));
                        }
                    } else {
                        labels[i].setIcon(new ImageIcon(ImageUtils.width(ImageUtils.dyeRect(2, 1, (Color) results[i]), imgWidth)));
                    }
                }
                int finalI = i;
                // 图片文件选择
                component.addActionListener(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("选择图片");
                    ObservableList<FileChooser.ExtensionFilter> filters = fileChooser.getExtensionFilters();
                    // 添加可读取的图片格式
                    String allSuffix = "";
                    for (String suffix : Format.READ_IMAGE_TYPE_SUPPORTED) {
                        filters.add(new FileChooser.ExtensionFilter(suffix.toUpperCase(), "*." + suffix));
                        allSuffix += "*." + suffix + ";";
                    }
                    filters.add(0, new FileChooser.ExtensionFilter("图片文件", allSuffix));
                    Platform.runLater(() -> {
                        File file = fileChooser.showOpenDialog(null);
                        if (file != null) {
                            results[finalI] = file.getPath();
                            BufferedImage img = ImageUtils.read((String) results[finalI]);
                            if (img.getWidth() >= img.getHeight())
                                labels[finalI].setIcon(new ImageIcon(ImageUtils.width(img, imgWidth)));
                            else labels[finalI].setIcon(new ImageIcon(ImageUtils.height(img, imgHeight)));
                            pack();
                            pack();
                            setLocationRelativeTo(f);
                        }
                    });
                    pack();
                });
            } else if (components[i] instanceof JLabel) {
                JLabel component = (JLabel) components[i];
                component.setFont(globalFont);
                // 鼠标光标
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // 获取风格颜色并显示成小方格
                component.setIcon(ImageUtils.dyeRoundRect(rectWidth, rectHeight, ((Color) results[i])));
                int finalI = i;
                // 颜色选择
                component.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            ColorChooserDialog d = new ColorChooserDialog(f, (Color) results[finalI]);
                            d.showDialog();
                            if (!d.isConfirmed()) return;
                            Color color = d.getResult();
                            // 更改方框内颜色并保存
                            component.setIcon(ImageUtils.dyeRoundRect(rectWidth, rectHeight, color));
                            results[finalI] = color;
                        }
                    }
                });
            }
            panel.add(components[i]);
            centerPanel.add(panel);
        }

        // 纯色按钮
        pureColor.setForeColor(style.getButtonColor());
        pureColor.setFont(globalFont);
        pureColor.addActionListener(e -> {
            ColorChooserDialog d = new ColorChooserDialog(f, results[1] instanceof Color ? (Color) results[1] : Colors.THEME);
            d.showDialog();
            if (!d.isConfirmed()) return;
            Color color = d.getResult();
            // 更改方框内颜色并保存
            labels[1].setIcon(new ImageIcon(ImageUtils.width(ImageUtils.dyeRect(2, 1, color), imgWidth)));
            results[1] = color;
            pack();
            setLocationRelativeTo(f);
        });
        ((JPanel) centerPanel.getComponent(1)).add(pureColor);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }

    public boolean getConfirmed() {
        return confirmed;
    }

    public Object[] getResults() {
        return results;
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

    private class CustomStyleDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public CustomStyleDialogPanel() {
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
