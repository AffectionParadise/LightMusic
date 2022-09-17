package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Fonts;
import net.doge.constants.Format;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.ui.listeners.ControlInputListener;
import net.doge.ui.listeners.JTextFieldHintListener;
import net.doge.utils.FileUtils;
import net.doge.utils.ImageUtils;
import net.doge.utils.JsonUtils;
import net.doge.utils.StringUtils;
import net.sf.json.JSONObject;

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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author yzx
 * @Description 设置对话框
 * @Date 2020/12/15
 */
public class SettingDialog extends JDialog {
    private final String TITLE = "设置";

    // 目录不存在提示
    private final String CATALOG_NOT_FOUND_MSG = "该目录不存在";

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");
    // 复选框图标
    private ImageIcon uncheckedIcon = new ImageIcon(SimplePath.ICON_PATH + "unchecked.png");
    private ImageIcon checkedIcon = new ImageIcon(SimplePath.ICON_PATH + "checked.png");

    private SettingDialogPanel globalPanel = new SettingDialogPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    // 设置项
    private JPanel autoDownloadLrcPanel = new JPanel();
    private JCheckBox autoDownloadLrcCheckBox = new JCheckBox("下载歌曲时自动下载歌词");
    private JPanel musicDownPanel = new JPanel();
    private JLabel musicDownLabel = new JLabel("歌曲下载路径：");
    private CustomTextField musicDownPathTextField = new CustomTextField(20);
    private DialogButton changeMusicDownPathButton;
    private DialogButton openMusicDownPathButton;
    private JPanel mvDownPanel = new JPanel();
    private JLabel mvDownLabel = new JLabel("MV 下载路径：");
    private CustomTextField mvDownPathTextField = new CustomTextField(20);
    private DialogButton changeMvDownPathButton;
    private DialogButton openMvDownPathButton;
    private JPanel cachePanel = new JPanel();
    private JLabel cacheLabel = new JLabel("缓存路径：");
    private CustomTextField cachePathTextField = new CustomTextField(20);
    private DialogButton changeCachePathButton;
    private DialogButton openCachePathButton;
    private JPanel maxCacheSizePanel = new JPanel();
    private JLabel maxCacheSizeLabel = new JLabel("最大缓存大小(MB)：");
    private CustomTextField maxCacheSizeTextField = new CustomTextField(10);
    private JPanel maxHistoryCountPanel = new JPanel();
    private JLabel maxHistoryCountLabel = new JLabel("最大播放历史数量：");
    private CustomTextField maxHistoryCountTextField = new CustomTextField(10);
    private JPanel maxSearchHistoryCountPanel = new JPanel();
    private JLabel maxSearchHistoryCountLabel = new JLabel("最大搜索历史数量：");
    private CustomTextField maxSearchHistoryCountTextField = new CustomTextField(10);
    private JPanel closeOptionPanel = new JPanel();
    private JLabel closeOptionLabel = new JLabel("关闭主界面时：");
    private JComboBox<String> closeOptionComboBox = new JComboBox();
    private JPanel fobPanel = new JPanel();
    private JLabel fobLabel = new JLabel("快进/快退时间：");
    private JComboBox<String> fobComboBox = new JComboBox();
    private JPanel ratePanel = new JPanel();
    private JLabel rateLabel = new JLabel("播放速率：");
    private JComboBox<String> rateComboBox = new JComboBox();
    private String[] rates = {"0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x", "4x", "8x"};
    private JPanel specStylePanel = new JPanel();
    private JLabel specStyleLabel = new JLabel("频谱样式：");
    private JComboBox<String> specStyleComboBox = new JComboBox();
    private JPanel balancePanel = new JPanel();
    private JLabel balanceLabel = new JLabel("声道平衡：");
    private JComboBox<String> balanceComboBox = new JComboBox();
    private JPanel backupPanel = new JPanel();
    private JLabel backupLabel = new JLabel("播放列表备份/恢复（仅包括离线音乐列表、所有收藏列表）");
    private DialogButton importListButton;
    private DialogButton exportListButton;

    private DialogButton okButton;
    private DialogButton applyButton;
    private DialogButton cancelButton;

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;

    // 父窗口和是否是模态，传入 OK 按钮文字
    public SettingDialog(PlayerFrame f) {
        super(f, true);
        this.f = f;
        this.style = f.getCurrUIStyle();

        Color buttonColor = style.getButtonColor();
        okButton = new DialogButton("保存", buttonColor);
        applyButton = new DialogButton("应用", buttonColor);
        cancelButton = new DialogButton("取消", buttonColor);
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
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();
        initSettings();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            if (!applySettings()) return;
            f.currDialogs.remove(this);
            dispose();
        });
        applyButton.addActionListener(e -> applySettings());
        cancelButton.addActionListener(e -> closeButton.doClick());
        okButton.setFont(globalFont);
        applyButton.setFont(globalFont);
        cancelButton.setFont(globalFont);
        buttonPanel.add(okButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        globalPanel.setOpaque(false);
        add(globalPanel, BorderLayout.CENTER);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        pack();
        setLocationRelativeTo(f);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.getIsBlur() && f.getPlayer().loadedMusic()) bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
        else bufferedImage = ImageUtils.read(f.getCurrUIStyle().getStyleImgPath());
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
        windowCtrlPanel.setMinimumSize(new Dimension(30, 30));
        windowCtrlPanel.add(closeButton);
        windowCtrlPanel.setOpaque(false);
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    void initView() {
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 字体
        autoDownloadLrcCheckBox.setFont(globalFont);
        musicDownLabel.setFont(globalFont);
        musicDownPathTextField.setFont(globalFont);
        mvDownLabel.setFont(globalFont);
        mvDownPathTextField.setFont(globalFont);
        cacheLabel.setFont(globalFont);
        cachePathTextField.setFont(globalFont);
        maxCacheSizeLabel.setFont(globalFont);
        maxCacheSizeTextField.setFont(globalFont);
        maxHistoryCountLabel.setFont(globalFont);
        maxHistoryCountTextField.setFont(globalFont);
        maxSearchHistoryCountLabel.setFont(globalFont);
        maxSearchHistoryCountTextField.setFont(globalFont);
        closeOptionLabel.setFont(globalFont);
        closeOptionComboBox.setFont(globalFont);
        fobLabel.setFont(globalFont);
        fobComboBox.setFont(globalFont);
        rateLabel.setFont(globalFont);
        rateComboBox.setFont(globalFont);
        specStyleLabel.setFont(globalFont);
        specStyleComboBox.setFont(globalFont);
        balanceLabel.setFont(globalFont);
        balanceComboBox.setFont(globalFont);
        backupLabel.setFont(globalFont);

        // 对齐
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        fl.setVgap(10);
        autoDownloadLrcPanel.setLayout(fl);
        musicDownPanel.setLayout(fl);
        mvDownPanel.setLayout(fl);
        cachePanel.setLayout(fl);
        maxCacheSizePanel.setLayout(fl);
        maxHistoryCountPanel.setLayout(fl);
        maxSearchHistoryCountPanel.setLayout(fl);
        closeOptionPanel.setLayout(fl);
        fobPanel.setLayout(fl);
        ratePanel.setLayout(fl);
        specStylePanel.setLayout(fl);
        balancePanel.setLayout(fl);
        backupPanel.setLayout(fl);

        // 边框
        Border b = BorderFactory.createEmptyBorder(0, 20, 0, 20);
        autoDownloadLrcPanel.setBorder(b);
        musicDownPanel.setBorder(b);
        mvDownPanel.setBorder(b);
        cachePanel.setBorder(b);
        maxCacheSizePanel.setBorder(b);
        maxHistoryCountPanel.setBorder(b);
        maxSearchHistoryCountPanel.setBorder(b);
        closeOptionPanel.setBorder(b);
        fobPanel.setBorder(b);
        ratePanel.setBorder(b);
        specStylePanel.setBorder(b);
        balancePanel.setBorder(b);
        backupPanel.setBorder(b);

        // 容器透明
        centerPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        autoDownloadLrcPanel.setOpaque(false);
        musicDownPanel.setOpaque(false);
        mvDownPanel.setOpaque(false);
        cachePanel.setOpaque(false);
        maxCacheSizePanel.setOpaque(false);
        maxHistoryCountPanel.setOpaque(false);
        maxSearchHistoryCountPanel.setOpaque(false);
        closeOptionPanel.setOpaque(false);
        fobPanel.setOpaque(false);
        ratePanel.setOpaque(false);
        specStylePanel.setOpaque(false);
        balancePanel.setOpaque(false);
        backupPanel.setOpaque(false);

        // 组件透明
        musicDownPathTextField.setOpaque(false);
        mvDownPathTextField.setOpaque(false);
        cachePathTextField.setOpaque(false);
        maxCacheSizeTextField.setOpaque(false);
        maxHistoryCountTextField.setOpaque(false);
        maxSearchHistoryCountTextField.setOpaque(false);
        autoDownloadLrcCheckBox.setOpaque(false);
        closeOptionComboBox.setOpaque(false);
        fobComboBox.setOpaque(false);
        rateComboBox.setOpaque(false);
        specStyleComboBox.setOpaque(false);
        balanceComboBox.setOpaque(false);

        // 组件不可聚焦
        autoDownloadLrcCheckBox.setFocusPainted(false);
        closeOptionComboBox.setFocusable(false);
        fobComboBox.setFocusable(false);
        rateComboBox.setFocusable(false);
        specStyleComboBox.setFocusable(false);
        balanceComboBox.setFocusable(false);

        // 字体颜色
        Color labelColor = style.getLabelColor();
        autoDownloadLrcCheckBox.setForeground(labelColor);
        musicDownLabel.setForeground(labelColor);
        mvDownLabel.setForeground(labelColor);
        cacheLabel.setForeground(labelColor);
        maxCacheSizeLabel.setForeground(labelColor);
        maxHistoryCountLabel.setForeground(labelColor);
        maxSearchHistoryCountLabel.setForeground(labelColor);
        closeOptionLabel.setForeground(labelColor);
        fobLabel.setForeground(labelColor);
        rateLabel.setForeground(labelColor);
        specStyleLabel.setForeground(labelColor);
        balanceLabel.setForeground(labelColor);
        backupLabel.setForeground(labelColor);

        // 文本框
        Color foreColor = style.getForeColor();
        musicDownPathTextField.setForeground(foreColor);
        musicDownPathTextField.setCaretColor(foreColor);
//        musicDownPathTextField.addFocusListener(new JTextFieldHintListener(musicDownPathTextField, "", foreColor));
        mvDownPathTextField.setForeground(foreColor);
        mvDownPathTextField.setCaretColor(foreColor);
//        mvDownPathTextField.addFocusListener(new JTextFieldHintListener(mvDownPathTextField, "", foreColor));
        cachePathTextField.setForeground(foreColor);
        cachePathTextField.setCaretColor(foreColor);
//        cachePathTextField.addFocusListener(new JTextFieldHintListener(cachePathTextField, "", foreColor));
        maxCacheSizeTextField.setForeground(foreColor);
        maxCacheSizeTextField.setCaretColor(foreColor);
//        maxCacheSizeTextField.addFocusListener(new JTextFieldHintListener(maxCacheSizeTextField, "", foreColor));
        maxCacheSizeTextField.addKeyListener(new ControlInputListener());
        maxHistoryCountTextField.setForeground(foreColor);
        maxHistoryCountTextField.setCaretColor(foreColor);
//        maxHistoryCountTextField.addFocusListener(new JTextFieldHintListener(maxHistoryCountTextField, "", foreColor));
        maxHistoryCountTextField.addKeyListener(new ControlInputListener());
        maxSearchHistoryCountTextField.setForeground(foreColor);
        maxSearchHistoryCountTextField.setCaretColor(foreColor);
        maxSearchHistoryCountTextField.addKeyListener(new ControlInputListener());

        // 下拉框 UI
        Color buttonColor = style.getButtonColor();
        closeOptionComboBox.setUI(new ComboBoxUI(closeOptionComboBox, f, globalFont, buttonColor));
        fobComboBox.setUI(new ComboBoxUI(fobComboBox, f, globalFont, buttonColor));
        rateComboBox.setUI(new ComboBoxUI(rateComboBox, f, globalFont, buttonColor));
        specStyleComboBox.setUI(new ComboBoxUI(specStyleComboBox, f, globalFont, buttonColor));
        balanceComboBox.setUI(new ComboBoxUI(balanceComboBox, f, globalFont, buttonColor));

        // 下拉框边框
        Border eb = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        closeOptionComboBox.setBorder(eb);
        fobComboBox.setBorder(eb);
        rateComboBox.setBorder(eb);
        specStyleComboBox.setBorder(eb);
        balanceComboBox.setBorder(eb);

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择歌曲文件夹");

        // 按钮
        changeMusicDownPathButton = new DialogButton("更改", buttonColor);
        changeMusicDownPathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir != null) {
                    // 文件夹不存在直接跳出
                    if (!dir.exists()) return;
                    musicDownPathTextField.setText(dir.getAbsolutePath());
                    pack();
                }
            });
        });
        openMusicDownPathButton = new DialogButton("打开", buttonColor);
        openMusicDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(musicDownPathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new ConfirmDialog(f, CATALOG_NOT_FOUND_MSG, "确定").showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeMvDownPathButton = new DialogButton("更改", buttonColor);
        changeMvDownPathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir != null) {
                    // 文件夹不存在直接跳出
                    if (!dir.exists()) return;
                    mvDownPathTextField.setText(dir.getAbsolutePath());
                    pack();
                }
            });
        });
        openMvDownPathButton = new DialogButton("打开", buttonColor);
        openMvDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(mvDownPathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new ConfirmDialog(f, CATALOG_NOT_FOUND_MSG, "确定").showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeCachePathButton = new DialogButton("更改", buttonColor);
        changeCachePathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir != null) {
                    // 文件夹不存在直接跳出
                    if (!dir.exists()) return;
                    cachePathTextField.setText(dir.getAbsolutePath());
                    pack();
                }
            });
        });
        openCachePathButton = new DialogButton("打开", buttonColor);
        openCachePathButton.addActionListener(e -> {
            try {
                File dir = new File(cachePathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new ConfirmDialog(f, CATALOG_NOT_FOUND_MSG, "确定").showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导入列表");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("json 文件", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        importListButton = new DialogButton("导入", buttonColor);
        importListButton.addActionListener(e -> {
            fileChooser.setTitle("导入列表");
            Platform.runLater(() -> {
                File input = fileChooser.showOpenDialog(null);
                if (input != null) {
                    JSONObject config = JsonUtils.readJson(input);
                    f.loadLocalMusicList(config);
                    f.loadCollectedMusicList(config);
                    new TipDialog(f, "导入成功").showDialog();
                }
            });
        });
        exportListButton = new DialogButton("导出", buttonColor);
        exportListButton.addActionListener(e -> {
            fileChooser.setTitle("导出列表");
            Platform.runLater(() -> {
                File output = fileChooser.showSaveDialog(null);
                if (output != null) {
                    JSONObject config = new JSONObject();
                    f.saveLocalMusicList(config);
                    f.saveCollectedMusicList(config);
                    try {
                        JsonUtils.saveJson(config, output);
                        new TipDialog(f, "导出成功").showDialog();
                    } catch (IOException ex) {
                        new TipDialog(f, "导出失败").showDialog();
                    }
                }
            });
        });

        // 按钮字体
        changeMusicDownPathButton.setFont(globalFont);
        openMusicDownPathButton.setFont(globalFont);
        changeMvDownPathButton.setFont(globalFont);
        openMvDownPathButton.setFont(globalFont);
        changeCachePathButton.setFont(globalFont);
        openCachePathButton.setFont(globalFont);
        importListButton.setFont(globalFont);
        exportListButton.setFont(globalFont);

        // 复选框图标
        autoDownloadLrcCheckBox.setIconTextGap(10);
        autoDownloadLrcCheckBox.setIcon(ImageUtils.dye(uncheckedIcon, buttonColor));
        autoDownloadLrcCheckBox.setSelectedIcon(ImageUtils.dye(checkedIcon, buttonColor));

        autoDownloadLrcPanel.add(autoDownloadLrcCheckBox);

        musicDownPanel.add(musicDownLabel);
        musicDownPanel.add(musicDownPathTextField);
        musicDownPanel.add(changeMusicDownPathButton);
        musicDownPanel.add(openMusicDownPathButton);

        mvDownPanel.add(mvDownLabel);
        mvDownPanel.add(mvDownPathTextField);
        mvDownPanel.add(changeMvDownPathButton);
        mvDownPanel.add(openMvDownPathButton);

        cachePanel.add(cacheLabel);
        cachePanel.add(cachePathTextField);
        cachePanel.add(changeCachePathButton);
        cachePanel.add(openCachePathButton);

        maxCacheSizePanel.add(maxCacheSizeLabel);
        maxCacheSizePanel.add(maxCacheSizeTextField);

        maxHistoryCountPanel.add(maxHistoryCountLabel);
        maxHistoryCountPanel.add(maxHistoryCountTextField);

        maxSearchHistoryCountPanel.add(maxSearchHistoryCountLabel);
        maxSearchHistoryCountPanel.add(maxSearchHistoryCountTextField);

        closeOptionComboBox.addItem("询问");
        closeOptionComboBox.addItem("隐藏到托盘");
        closeOptionComboBox.addItem("退出程序");
        closeOptionPanel.add(closeOptionLabel);
        closeOptionPanel.add(closeOptionComboBox);

        for (int i = 5; i <= 60; i += 5) {
            String item = i + " 秒";
            fobComboBox.addItem(item);
            if (f.forwardOrBackwardTime == i) fobComboBox.setSelectedItem(item);
        }
        fobPanel.add(fobLabel);
        fobPanel.add(fobComboBox);

        for (String rate : rates) {
            rateComboBox.addItem(rate);
            if (Math.abs(f.currRate - Double.parseDouble(rate.replace("x", ""))) < 0.001)
                rateComboBox.setSelectedItem(rate);
        }
        ratePanel.add(rateLabel);
        ratePanel.add(rateComboBox);

        specStyleComboBox.addItem("平地式");
        specStyleComboBox.addItem("悬空式");
        specStyleComboBox.addItem("折线式");
        specStyleComboBox.addItem("曲线式");
        specStyleComboBox.addItem("山峰式");
        specStyleComboBox.addItem("波浪式");
        specStylePanel.add(specStyleLabel);
        specStylePanel.add(specStyleComboBox);

        balanceComboBox.addItem("左声道");
        balanceComboBox.addItem("立体声");
        balanceComboBox.addItem("右声道");
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceComboBox);

        backupPanel.add(backupLabel);
        backupPanel.add(importListButton);
        backupPanel.add(exportListButton);

        centerPanel.add(autoDownloadLrcPanel);
        centerPanel.add(musicDownPanel);
        centerPanel.add(mvDownPanel);
        centerPanel.add(cachePanel);
        centerPanel.add(maxCacheSizePanel);
        centerPanel.add(maxHistoryCountPanel);
        centerPanel.add(maxSearchHistoryCountPanel);
        centerPanel.add(closeOptionPanel);
        centerPanel.add(fobPanel);
        centerPanel.add(ratePanel);
        centerPanel.add(specStylePanel);
        centerPanel.add(balancePanel);
        centerPanel.add(backupPanel);
    }

    // 加载设置
    void initSettings() {
        autoDownloadLrcCheckBox.setSelected(f.isAutoDownloadLrc);
        musicDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MUSIC_PATH).getAbsolutePath());
        mvDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MV_PATH).getAbsolutePath());
        cachePathTextField.setText(new File(SimplePath.CACHE_PATH).getAbsolutePath());
        maxCacheSizeTextField.setText(String.valueOf(f.maxCacheSize));
        maxHistoryCountTextField.setText(String.valueOf(f.maxHistoryCount));
        maxSearchHistoryCountTextField.setText(String.valueOf(f.maxSearchHistoryCount));
        closeOptionComboBox.setSelectedIndex(f.currCloseWindowOption);
        specStyleComboBox.setSelectedIndex(f.currSpecStyle);
        balanceComboBox.setSelectedIndex(Double.valueOf(f.currBalance).intValue() + 1);
    }

    // 应用设置
    boolean applySettings() {
        // 验证
        File musicDir = new File(musicDownPathTextField.getText());
        if (!musicDir.exists()) {
            new ConfirmDialog(f, "歌曲下载路径无效", "确定").showDialog();
            return false;
        }
        File mvDir = new File(mvDownPathTextField.getText());
        if (!mvDir.exists()) {
            new ConfirmDialog(f, "MV 下载路径无效", "确定").showDialog();
            return false;
        }
        File cacheDir = new File(cachePathTextField.getText());
        if (!cacheDir.exists()) {
            new ConfirmDialog(f, "缓存路径无效", "确定").showDialog();
            return false;
        }

        f.isAutoDownloadLrc = autoDownloadLrcCheckBox.isSelected();
        SimplePath.DOWNLOAD_MUSIC_PATH = musicDir.getAbsolutePath() + "/";
        SimplePath.DOWNLOAD_MV_PATH = mvDir.getAbsolutePath() + "/";
        SimplePath.CACHE_PATH = cacheDir.getAbsolutePath() + "/";
        // 更改缓存图像路径并创建
        new File(SimplePath.IMG_CACHE_PATH = SimplePath.CACHE_PATH + "img/").mkdirs();

        f.maxCacheSize = Long.parseLong(maxCacheSizeTextField.getText());
        f.maxHistoryCount = Integer.parseInt(maxHistoryCountTextField.getText());
        // 删除多余的播放历史记录
        for (int i = f.maxHistoryCount, s = f.historyModel.size(); i < s; i++)
            f.historyModel.remove(f.maxHistoryCount);
        f.maxSearchHistoryCount = Integer.parseInt(maxSearchHistoryCountTextField.getText());
        // 删除多余的搜索历史记录
        JPanel[] ps = new JPanel[]{f.netMusicHistorySearchInnerPanel2,
                f.netPlaylistHistorySearchInnerPanel2,
                f.netAlbumHistorySearchInnerPanel2,
                f.netArtistHistorySearchInnerPanel2,
                f.netRadioHistorySearchInnerPanel2,
                f.netMvHistorySearchInnerPanel2,
                f.netUserHistorySearchInnerPanel2};
        for (JPanel p : ps)
            for (int i = f.maxSearchHistoryCount, s = p.getComponentCount(); i < s; i++) {
                p.remove(f.maxSearchHistoryCount);
                p.repaint();
            }
        f.currCloseWindowOption = closeOptionComboBox.getSelectedIndex();
        f.forwardOrBackwardTime = Integer.parseInt(((String) fobComboBox.getSelectedItem()).replace(" 秒", ""));
        f.currRate = Double.parseDouble(((String) rateComboBox.getSelectedItem()).replace("x", ""));
        f.getPlayer().setRate(f.currRate);
        f.currSpecStyle = specStyleComboBox.getSelectedIndex();
        f.currBalance = balanceComboBox.getSelectedIndex() - 1;
        f.getPlayer().setBalance(f.currBalance);

        return true;
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

    private class SettingDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public SettingDialogPanel() {
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
