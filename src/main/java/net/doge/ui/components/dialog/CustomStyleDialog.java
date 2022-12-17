package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.Format;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.componentui.ScrollBarUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.FileUtils;
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
    private final int imgHeight = 120;
    private final int rectWidth = 170;
    private final int rectHeight = 30;
    private final String STYLE_NAME_NOT_NULL_MSG = "emmm~~主题名称不能为无名氏哦";
    private final String STYLE_NAME_DUPLICATE_MSG = "emmm~该主题名称已存在，换一个吧";
    private final String IMG_FILE_NOT_EXIST_MSG = "选定的图片路径无效";
    private final String IMG_NOT_VALID_MSG = "不是有效的图片文件";
    private CustomStyleDialogPanel globalPanel = new CustomStyleDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    private CustomPanel centerPanel = new CustomPanel();
    private CustomScrollPane centerScrollPane = new CustomScrollPane(centerPanel);
    private CustomPanel buttonPanel = new CustomPanel();

    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton();

    private final CustomLabel[] labels = {
            new CustomLabel("主题名称："),
            new CustomLabel("背景图片："),
            new CustomLabel("悬停框颜色："),
            new CustomLabel("选中框颜色："),
            new CustomLabel("歌词文字颜色："),
            new CustomLabel("歌词高亮颜色："),
            new CustomLabel("标签文字颜色："),
            new CustomLabel("时间条颜色："),
            new CustomLabel("图标颜色："),
            new CustomLabel("滚动条颜色："),
            new CustomLabel("音量滑动条颜色："),
            new CustomLabel("频谱颜色："),
            new CustomLabel("菜单项颜色：")
    };

    private final Component[] components = {
            new CustomTextField(15),
            new DialogButton("选择图片"),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel()
    };
    private DialogButton pureColor;

    private DialogButton okButton;

    private PlayerFrame f;
    private UIStyle style;
    // 面板展示的样式
    private UIStyle showedStyle;

    private boolean confirmed = false;
    private Object[] results = new Object[components.length];

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的样式(添加则用当前样式，编辑则用选中样式)
    public CustomStyleDialog(PlayerFrame f, boolean isModel, String okButtonText, UIStyle showedStyle) {
        super(f, isModel);
        this.f = f;
        this.style = f.currUIStyle;
        this.showedStyle = showedStyle;

        Color textColor = style.getTextColor();
        okButton = new DialogButton(okButtonText, textColor);
        pureColor = new DialogButton("纯色", textColor);
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
        setSize(960, 750);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerScrollPane, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            // 风格名称不为空
            if (results[0].equals("")) {
                new TipDialog(f, STYLE_NAME_NOT_NULL_MSG).showDialog();
                return;
            }
            // 风格名称不重复
            List<UIStyle> styles = f.styles;
            for (UIStyle style : styles) {
                // 添加时，名称一定不相等；编辑时，只允许同一样式名称相等
                if (style.getStyleName().equals(results[0])
                        && (style != showedStyle || okButton.getPlainText().contains("添加"))) {
                    new TipDialog(f, STYLE_NAME_DUPLICATE_MSG).showDialog();
                    return;
                }
            }
            // 图片路径
            if (results[1] instanceof String) {
                // 复制图片(如果有)
                File imgFile = new File(((String) results[1]));
                // 图片存在且是文件
                if (imgFile.exists() && imgFile.isFile()) {
                    try {
                        // 文件夹不存在就创建
                        File dir = new File(SimplePath.CUSTOM_STYLE_IMG_PATH);
                        FileUtils.makeSureDir(dir);
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
        buttonPanel.add(okButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

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
        if (f.blurType != BlurType.OFF && f.player.loadedMusic()) {
            bufferedImage = f.player.getMusicInfo().getAlbumImage();
            if (bufferedImage == f.defaultAlbumImage) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG)
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.currUIStyle;
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    // 初始化标题栏
    private void initTitleBar() {
        Color textColor = style.getTextColor();
        titleLabel.setForeground(textColor);
        titleLabel.setText(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        closeButton.setIcon(ImageUtils.dye(f.closeWindowIcon, style.getIconColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + 2, f.closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            dispose();
            f.currDialogs.remove(this);
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

    private void initView() {
        centerPanel.setLayout(new GridLayout(7, 2));

        // 获得传入的界面样式
        results[0] = showedStyle.getStyleName();
        String styleImgPath = showedStyle.getStyleImgPath();
        results[1] = StringUtils.isEmpty(styleImgPath) ? showedStyle.getBgColor() : styleImgPath;
        results[2] = showedStyle.getForeColor();
        results[3] = showedStyle.getSelectedColor();
        results[4] = showedStyle.getLrcColor();
        results[5] = showedStyle.getHighlightColor();
        results[6] = showedStyle.getTextColor();
        results[7] = showedStyle.getTimeBarColor();
        results[8] = showedStyle.getIconColor();
        results[9] = showedStyle.getScrollBarColor();
        results[10] = showedStyle.getSliderColor();
        results[11] = showedStyle.getSpectrumColor();
        results[12] = showedStyle.getMenuItemColor();

        Border eb = BorderFactory.createEmptyBorder(0, 20, 0, 20);

        Color textColor = style.getTextColor();
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            CustomPanel panel = new CustomPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(eb);
            // 添加标签
            labels[i].setForeground(textColor);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof CustomTextField) {
                CustomTextField component = (CustomTextField) components[i];
                component.setForeground(textColor);
                component.setCaretColor(textColor);
                // 加载风格名称
                component.setText((String) results[i]);
                Document document = component.getDocument();
                // 添加文本改变监听器
                document.addDocumentListener(this);
            } else if (components[i] instanceof DialogButton) {
                DialogButton component = (DialogButton) components[i];
                component.setForeColor(textColor);
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
                            if (img == null) {
                                new TipDialog(f, IMG_NOT_VALID_MSG).showDialog();
                                return;
                            }
                            if (img.getWidth() >= img.getHeight())
                                labels[finalI].setIcon(new ImageIcon(ImageUtils.width(img, imgWidth)));
                            else labels[finalI].setIcon(new ImageIcon(ImageUtils.height(img, imgHeight)));
                            setLocationRelativeTo(null);
                        }
                    });
                });
            } else if (components[i] instanceof CustomLabel) {
                CustomLabel component = (CustomLabel) components[i];
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

            CustomPanel outer = new CustomPanel();
            outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
            outer.add(Box.createVerticalGlue());
            outer.add(panel);
            outer.add(Box.createVerticalGlue());

            centerPanel.add(outer);
        }

        // 纯色按钮
        pureColor.addActionListener(e -> {
            ColorChooserDialog d = new ColorChooserDialog(f, results[1] instanceof Color ? (Color) results[1] : Colors.THEME);
            d.showDialog();
            if (!d.isConfirmed()) return;
            Color color = d.getResult();
            // 更改方框内颜色并保存
            labels[1].setIcon(new ImageIcon(ImageUtils.width(ImageUtils.dyeRect(2, 1, color), imgWidth)));
            results[1] = color;
            setLocationRelativeTo(null);
        });
        ((CustomPanel) ((CustomPanel) centerPanel.getComponent(1)).getComponent(1)).add(pureColor);

        Color scrollBarColor = style.getScrollBarColor();
        centerScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.player.loadedMusic();
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
