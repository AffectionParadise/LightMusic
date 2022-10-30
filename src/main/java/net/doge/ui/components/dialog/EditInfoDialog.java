package net.doge.ui.components.dialog;

import com.mpatric.mp3agic.ID3v1Genres;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import it.sauronsoftware.jave.EncoderException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.constants.Format;
import net.doge.constants.SimplePath;
import net.doge.models.AudioFile;
import net.doge.models.MediaInfo;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.ui.listeners.JTextFieldHintListener;
import net.doge.utils.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Author yzx
 * @Description 编辑歌曲信息的对话框
 * @Date 2020/12/15
 */
public class EditInfoDialog extends JDialog {
    private final String TITLE = "歌曲信息";

    // 文件正在使用提示
    private final String FILE_BEING_USED_MSG = "文件正在被占用，无法修改";
    private EditInfoDialogPanel globalPanel = new EditInfoDialogPanel();

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
            new JLabel("文件名："),
            new JLabel("文件大小："),
            new JLabel("创建时间："),
            new JLabel("修改时间："),
            new JLabel("访问时间："),
            new JLabel("时长："),
            new JLabel("标题："),
            new JLabel("艺术家："),
            new JLabel("专辑："),
            new JLabel("流派："),
            new JLabel("注释："),
            new JLabel("版权："),
            new JLabel("封面图片：")
    };

    private JComboBox<String> comboBox = new JComboBox<>();
    private final int columns = 20;
    private final Component[] components = {
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new JLabel(),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            comboBox,
            new CustomTextField(columns),
            new CustomTextField(columns),
            new DialogButton("选择图片")
    };

    private DialogButton okButton;

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;
    // 面板展示的文件
    private AudioFile file;

    private boolean confirmed = false;
    private Object[] results = new Object[components.length];

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的文件
    public EditInfoDialog(PlayerFrame f, boolean isModel, String okButtonText, AudioFile file) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();
        this.file = file;

        Color buttonColor = style.getButtonColor();
        okButton = new DialogButton(okButtonText, buttonColor);

        comboBox.addItem("");
        for (String genre : ID3v1Genres.GENRES) {
            comboBox.addItem(genre);
        }
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
            if (f.getPlayer().isPlayingFile(file)) {
                new TipDialog(f, FILE_BEING_USED_MSG).showDialog();
                return;
            } else {
                for (int i = 0, size = labels.length; i < size; i++) {
                    if (components[i] instanceof CustomTextField) {
                        results[i] = ((CustomTextField) components[i]).getText();
                    }
                }
                try {
                    MediaInfo mediaInfo = new MediaInfo(
                            (String) results[6],
                            (String) results[7],
                            (String) results[8],
                            (String) results[9],
                            (String) results[10],
                            (String) results[11],
                            (BufferedImage) results[12]
                    );
                    MusicUtils.writeMP3Info(file.getAbsolutePath(), mediaInfo);
                    // 歌曲信息更改后重新填充
                    MusicUtils.fillAudioFileInfo(file);
                } catch (InvalidDataException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedTagException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NotSupportedException ex) {
                    ex.printStackTrace();
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

        // 获得传入的歌曲信息
        String fileName = file.getName();
        String fileSize = FileUtils.getUnitString(FileUtils.getDirOrFileSize(file));
        String creationTime = TimeUtils.msToDatetime(FileUtils.getCreationTime(file));
        String lastModifiedTime = TimeUtils.msToDatetime(file.lastModified());
        String lastAccessTime = TimeUtils.msToDatetime(FileUtils.getAccessTime(file));
        String duration = TimeUtils.format(file.getDuration());
        String title = file.getSongName();
        String artist = file.getArtist();
        String album = file.getAlbum();
        String genre = MusicUtils.getGenre(file);
        String comment = MusicUtils.getComment(file);
        String copyright = MusicUtils.getCopyright(file);
        BufferedImage albumImage = MusicUtils.getAlbumImage(file);

        results[0] = fileName;
        results[1] = fileSize;
        results[2] = creationTime;
        results[3] = lastModifiedTime;
        results[4] = lastAccessTime;
        results[5] = duration;
        results[6] = title;
        results[7] = artist;
        results[8] = album;
        results[9] = genre;
        results[10] = comment;
        results[11] = copyright;
        results[12] = albumImage;

        Border b = BorderFactory.createEmptyBorder(0, 20, 0, 20);
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setOpaque(false);
            panel.setBorder(b);
            // 添加标签
            labels[i].setForeground(style.getLabelColor());
            // 添加标签
            labels[i].setFont(globalFont);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof JLabel) {
                JLabel component = (JLabel) components[i];
                component.setForeground(style.getLabelColor());
                component.setFont(globalFont);
                component.setText(StringUtils.textToHtml((String) results[i]));
            } else if (components[i] instanceof CustomTextField) {
                CustomTextField component = (CustomTextField) components[i];
                Color foreColor = style.getForeColor();
                component.setForeground(foreColor);
                component.setCaretColor(foreColor);
                component.setOpaque(false);
                component.setFont(globalFont);
                component.setText((String) results[i]);
            } else if (components[i] instanceof JComboBox) {
                JComboBox component = (JComboBox) components[i];
                // 下拉框透明
                component.setOpaque(false);
                // 下拉框 UI
                Color buttonColor = style.getButtonColor();
                component.setUI(new ComboBoxUI(component, f, globalFont, buttonColor));
                // 下拉框边框
                component.setBorder(null);

                component.setFont(globalFont);
                int finalI = i;
                component.addItemListener(e -> {
                    results[finalI] = e.getItem().toString();
                });
                component.setSelectedItem(results[i]);
            } else if (components[i] instanceof DialogButton) {
                DialogButton component = (DialogButton) components[i];
                component.setForeColor(style.getButtonColor());
                component.setFont(globalFont);
                labels[i].setHorizontalTextPosition(SwingConstants.LEFT);
                // 加载封面图片(显示一个缩略图)
                if (results[i] != null) {
                    BufferedImage image = (BufferedImage) results[i];
                    if (image.getWidth() >= image.getHeight())
                        labels[i].setIcon(new ImageIcon(ImageUtils.width(image, 100)));
                    else labels[i].setIcon(new ImageIcon(ImageUtils.height(image, 100)));
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
                            results[finalI] = ImageUtils.read(file);
                            labels[finalI].setIcon(new ImageIcon(
                                    ImageUtils.width((BufferedImage) results[finalI], 100)
                            ));
                            pack();
                            pack();
                            setLocationRelativeTo(f);
                        }
                    });
                });
            }
            panel.add(components[i]);
            centerPanel.add(panel);
        }
    }

    public boolean getConfirmed() {
        return confirmed;
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

    private class EditInfoDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public EditInfoDialogPanel() {
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
