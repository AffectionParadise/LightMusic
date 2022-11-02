package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.DialogButton;
import net.doge.ui.components.SafeDocument;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.JsonUtils;
import net.sf.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    private JPanel videoOnlyPanel = new JPanel();
    private JCheckBox videoOnlyCheckBox = new JCheckBox("播放视频时隐藏主界面");
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
    private JPanel maxConcurrentTaskCountPanel = new JPanel();
    private JLabel maxConcurrentTaskCountLabel = new JLabel("同时下载的最大任务数：");
    private CustomTextField maxConcurrentTaskCountTextField = new CustomTextField(10);
    private JPanel closeOptionPanel = new JPanel();
    private JLabel closeOptionLabel = new JLabel("关闭主界面时：");
    private JComboBox<String> closeOptionComboBox = new JComboBox();
    private JPanel windowSizePanel = new JPanel();
    private JLabel windowSizeLabel = new JLabel("窗口大小：");
    private JComboBox<String> windowSizeComboBox = new JComboBox();
    private JPanel fobPanel = new JPanel();
    private JLabel fobLabel = new JLabel("快进/快退时间：");
    private JComboBox<String> fobComboBox = new JComboBox();
    //    private JPanel ratePanel = new JPanel();
//    private JLabel rateLabel = new JLabel("播放速率：");
//    private JComboBox<String> rateComboBox = new JComboBox();
//    private String[] rates = {"0.25x", "0.5x", "0.75x", "1x", "1.25x", "1.5x", "1.75x", "2x", "4x", "8x"};
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
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        globalPanel.setOpaque(false);
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
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
        } else {
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
        windowCtrlPanel.setMinimumSize(new Dimension(30, 30));
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
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 字体
        autoDownloadLrcCheckBox.setFont(globalFont);
        videoOnlyCheckBox.setFont(globalFont);
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
        maxConcurrentTaskCountLabel.setFont(globalFont);
        maxSearchHistoryCountTextField.setFont(globalFont);
        maxConcurrentTaskCountTextField.setFont(globalFont);
        closeOptionLabel.setFont(globalFont);
        closeOptionComboBox.setFont(globalFont);
        windowSizeLabel.setFont(globalFont);
        windowSizeComboBox.setFont(globalFont);
        fobLabel.setFont(globalFont);
        fobComboBox.setFont(globalFont);
        specStyleLabel.setFont(globalFont);
        specStyleComboBox.setFont(globalFont);
        balanceLabel.setFont(globalFont);
        balanceComboBox.setFont(globalFont);
        backupLabel.setFont(globalFont);

        // 对齐
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        fl.setVgap(7);
        autoDownloadLrcPanel.setLayout(fl);
        videoOnlyPanel.setLayout(fl);
        musicDownPanel.setLayout(fl);
        mvDownPanel.setLayout(fl);
        cachePanel.setLayout(fl);
        maxCacheSizePanel.setLayout(fl);
        maxHistoryCountPanel.setLayout(fl);
        maxSearchHistoryCountPanel.setLayout(fl);
        maxConcurrentTaskCountPanel.setLayout(fl);
        closeOptionPanel.setLayout(fl);
        windowSizePanel.setLayout(fl);
        fobPanel.setLayout(fl);
        specStylePanel.setLayout(fl);
        balancePanel.setLayout(fl);
        backupPanel.setLayout(fl);

        // 边框
        Border b = BorderFactory.createEmptyBorder(0, 20, 0, 20);
        autoDownloadLrcPanel.setBorder(b);
        videoOnlyPanel.setBorder(b);
        musicDownPanel.setBorder(b);
        mvDownPanel.setBorder(b);
        cachePanel.setBorder(b);
        maxCacheSizePanel.setBorder(b);
        maxHistoryCountPanel.setBorder(b);
        maxSearchHistoryCountPanel.setBorder(b);
        maxConcurrentTaskCountPanel.setBorder(b);
        closeOptionPanel.setBorder(b);
        windowSizePanel.setBorder(b);
        fobPanel.setBorder(b);
        specStylePanel.setBorder(b);
        balancePanel.setBorder(b);
        backupPanel.setBorder(b);

        // 容器透明
        centerPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        autoDownloadLrcPanel.setOpaque(false);
        videoOnlyPanel.setOpaque(false);
        musicDownPanel.setOpaque(false);
        mvDownPanel.setOpaque(false);
        cachePanel.setOpaque(false);
        maxCacheSizePanel.setOpaque(false);
        maxHistoryCountPanel.setOpaque(false);
        maxSearchHistoryCountPanel.setOpaque(false);
        maxConcurrentTaskCountPanel.setOpaque(false);
        closeOptionPanel.setOpaque(false);
        windowSizePanel.setOpaque(false);
        fobPanel.setOpaque(false);
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
        maxConcurrentTaskCountTextField.setOpaque(false);
        autoDownloadLrcCheckBox.setOpaque(false);
        videoOnlyCheckBox.setOpaque(false);
        closeOptionComboBox.setOpaque(false);
        windowSizeComboBox.setOpaque(false);
        fobComboBox.setOpaque(false);
        specStyleComboBox.setOpaque(false);
        balanceComboBox.setOpaque(false);

        // 组件不可聚焦
        autoDownloadLrcCheckBox.setFocusPainted(false);
        videoOnlyCheckBox.setFocusPainted(false);
        closeOptionComboBox.setFocusable(false);
        windowSizeComboBox.setFocusable(false);
        fobComboBox.setFocusable(false);
        specStyleComboBox.setFocusable(false);
        balanceComboBox.setFocusable(false);

        // 字体颜色
        Color labelColor = style.getLabelColor();
        autoDownloadLrcCheckBox.setForeground(labelColor);
        videoOnlyCheckBox.setForeground(labelColor);
        musicDownLabel.setForeground(labelColor);
        mvDownLabel.setForeground(labelColor);
        cacheLabel.setForeground(labelColor);
        maxCacheSizeLabel.setForeground(labelColor);
        maxHistoryCountLabel.setForeground(labelColor);
        maxSearchHistoryCountLabel.setForeground(labelColor);
        maxConcurrentTaskCountLabel.setForeground(labelColor);
        closeOptionLabel.setForeground(labelColor);
        windowSizeLabel.setForeground(labelColor);
        fobLabel.setForeground(labelColor);
        specStyleLabel.setForeground(labelColor);
        balanceLabel.setForeground(labelColor);
        backupLabel.setForeground(labelColor);

        // 文本框
        Color foreColor = style.getForeColor();
        musicDownPathTextField.setForeground(foreColor);
        musicDownPathTextField.setCaretColor(foreColor);
        mvDownPathTextField.setForeground(foreColor);
        mvDownPathTextField.setCaretColor(foreColor);
        cachePathTextField.setForeground(foreColor);
        cachePathTextField.setCaretColor(foreColor);
        maxCacheSizeTextField.setForeground(foreColor);
        maxCacheSizeTextField.setCaretColor(foreColor);
        SafeDocument doc = new SafeDocument(0, 4096);
        maxCacheSizeTextField.setDocument(doc);
        maxHistoryCountTextField.setForeground(foreColor);
        maxHistoryCountTextField.setCaretColor(foreColor);
        doc = new SafeDocument(0, 1000);
        maxHistoryCountTextField.setDocument(doc);
        maxSearchHistoryCountTextField.setForeground(foreColor);
        maxSearchHistoryCountTextField.setCaretColor(foreColor);
        doc = new SafeDocument(0, 100);
        maxSearchHistoryCountTextField.setDocument(doc);
        maxConcurrentTaskCountTextField.setForeground(foreColor);
        maxConcurrentTaskCountTextField.setCaretColor(foreColor);
        doc = new SafeDocument(1, 5);
        maxConcurrentTaskCountTextField.setDocument(doc);

        // 下拉框 UI
        Color buttonColor = style.getButtonColor();
        closeOptionComboBox.setUI(new ComboBoxUI(closeOptionComboBox, f, globalFont, buttonColor));
        windowSizeComboBox.setUI(new ComboBoxUI(windowSizeComboBox, f, globalFont, buttonColor));
        fobComboBox.setUI(new ComboBoxUI(fobComboBox, f, globalFont, buttonColor));
        specStyleComboBox.setUI(new ComboBoxUI(specStyleComboBox, f, globalFont, buttonColor));
        balanceComboBox.setUI(new ComboBoxUI(balanceComboBox, f, globalFont, buttonColor));

        // 下拉框边框
        closeOptionComboBox.setBorder(null);
        windowSizeComboBox.setBorder(null);
        fobComboBox.setBorder(null);
        specStyleComboBox.setBorder(null);
        balanceComboBox.setBorder(null);

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
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG).showDialog();
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
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG).showDialog();
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
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG).showDialog();
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
        int gap = 10;
        ImageIcon icon = ImageUtils.dye(uncheckedIcon, labelColor);
        ImageIcon selectedIcon = ImageUtils.dye(checkedIcon, labelColor);
        autoDownloadLrcCheckBox.setIconTextGap(gap);
        autoDownloadLrcCheckBox.setIcon(icon);
        autoDownloadLrcCheckBox.setSelectedIcon(selectedIcon);
        videoOnlyCheckBox.setIconTextGap(gap);
        videoOnlyCheckBox.setIcon(icon);
        videoOnlyCheckBox.setSelectedIcon(selectedIcon);

        autoDownloadLrcPanel.add(autoDownloadLrcCheckBox);

        videoOnlyPanel.add(videoOnlyCheckBox);

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

        maxConcurrentTaskCountPanel.add(maxConcurrentTaskCountLabel);
        maxConcurrentTaskCountPanel.add(maxConcurrentTaskCountTextField);

        for (String name : CloseWindowOptions.names) closeOptionComboBox.addItem(name);
        closeOptionPanel.add(closeOptionLabel);
        closeOptionPanel.add(closeOptionComboBox);

        for (String name : WindowSize.names) windowSizeComboBox.addItem(name);
        windowSizePanel.add(windowSizeLabel);
        windowSizePanel.add(windowSizeComboBox);

        for (int i = 5; i <= 60; i += 5) {
            String item = i + " 秒";
            fobComboBox.addItem(item);
            if (f.forwardOrBackwardTime == i) fobComboBox.setSelectedItem(item);
        }
        fobPanel.add(fobLabel);
        fobPanel.add(fobComboBox);

//        for (String rate : rates) {
//            rateComboBox.addItem(rate);
//            if (Math.abs(f.currRate - Double.parseDouble(rate.replace("x", ""))) < 0.001)
//                rateComboBox.setSelectedItem(rate);
//        }
//        ratePanel.add(rateLabel);
//        ratePanel.add(rateComboBox);

        for (String name : SpectrumConstants.names) specStyleComboBox.addItem(name);
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
        centerPanel.add(videoOnlyPanel);
        centerPanel.add(musicDownPanel);
        centerPanel.add(mvDownPanel);
        centerPanel.add(cachePanel);
        centerPanel.add(maxCacheSizePanel);
        centerPanel.add(maxHistoryCountPanel);
        centerPanel.add(maxSearchHistoryCountPanel);
        centerPanel.add(maxConcurrentTaskCountPanel);
        centerPanel.add(closeOptionPanel);
        centerPanel.add(windowSizePanel);
        centerPanel.add(fobPanel);
//        centerPanel.add(ratePanel);
        centerPanel.add(specStylePanel);
        centerPanel.add(balancePanel);
        centerPanel.add(backupPanel);
    }

    // 加载设置
    void initSettings() {
        autoDownloadLrcCheckBox.setSelected(f.isAutoDownloadLrc);
        videoOnlyCheckBox.setSelected(f.videoOnly);
        musicDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MUSIC_PATH).getAbsolutePath());
        mvDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MV_PATH).getAbsolutePath());
        cachePathTextField.setText(new File(SimplePath.CACHE_PATH).getAbsolutePath());
        maxCacheSizeTextField.setText(String.valueOf(f.maxCacheSize));
        maxHistoryCountTextField.setText(String.valueOf(f.maxHistoryCount));
        maxSearchHistoryCountTextField.setText(String.valueOf(f.maxSearchHistoryCount));
        maxConcurrentTaskCountTextField.setText(String.valueOf(((ThreadPoolExecutor) GlobalExecutors.downloadExecutor).getCorePoolSize()));
        closeOptionComboBox.setSelectedIndex(f.currCloseWindowOption);
        windowSizeComboBox.setSelectedIndex(f.windowSize);
        specStyleComboBox.setSelectedIndex(f.currSpecStyle);
        balanceComboBox.setSelectedIndex(Double.valueOf(f.currBalance).intValue() + 1);
    }

    // 应用设置
    boolean applySettings() {
        // 验证
        File musicDir = new File(musicDownPathTextField.getText());
        if (!musicDir.exists()) {
            new TipDialog(f, "歌曲下载路径无效").showDialog();
            return false;
        }
        File mvDir = new File(mvDownPathTextField.getText());
        if (!mvDir.exists()) {
            new TipDialog(f, "MV 下载路径无效").showDialog();
            return false;
        }
        File cacheDir = new File(cachePathTextField.getText());
        if (!cacheDir.exists()) {
            new TipDialog(f, "缓存路径无效").showDialog();
            return false;
        }

        f.isAutoDownloadLrc = autoDownloadLrcCheckBox.isSelected();
        f.videoOnly = videoOnlyCheckBox.isSelected();
        SimplePath.DOWNLOAD_MUSIC_PATH = musicDir.getAbsolutePath() + File.separator;
        SimplePath.DOWNLOAD_MV_PATH = mvDir.getAbsolutePath() + File.separator;
        SimplePath.CACHE_PATH = cacheDir.getAbsolutePath() + File.separator;
        // 更改缓存图像路径并创建
        new File(SimplePath.IMG_CACHE_PATH = SimplePath.CACHE_PATH + File.separator + "img" + File.separator).mkdirs();

        String text = maxCacheSizeTextField.getText();
        if (text.isEmpty()) {
            new TipDialog(f, "最大缓存大小无效").showDialog();
            return false;
        }
        f.maxCacheSize = Long.parseLong(text);

        text = maxHistoryCountTextField.getText();
        if (text.isEmpty()) {
            new TipDialog(f, "最大播放历史数量无效").showDialog();
            return false;
        }
        f.maxHistoryCount = Integer.parseInt(text);

        text = maxSearchHistoryCountTextField.getText();
        if (text.isEmpty()) {
            new TipDialog(f, "最大搜索历史数量无效").showDialog();
            return false;
        }
        f.maxSearchHistoryCount = Integer.parseInt(text);

        text = maxConcurrentTaskCountTextField.getText();
        if (text.isEmpty()) {
            new TipDialog(f, "同时下载的最大任务数无效").showDialog();
            return false;
        }
        GlobalExecutors.downloadExecutor = Executors.newFixedThreadPool(Integer.parseInt(text));

        // 删除多余的播放历史记录
        for (int i = f.maxHistoryCount, s = f.historyModel.size(); i < s; i++)
            f.historyModel.remove(f.maxHistoryCount);

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
        f.windowSize = windowSizeComboBox.getSelectedIndex();
        f.windowWidth = WindowSize.dimensions[f.windowSize][0];
        f.windowHeight = WindowSize.dimensions[f.windowSize][1];
        if (f.windowState != WindowState.MAXIMIZED) f.setSize(f.windowWidth, f.windowHeight);

        f.forwardOrBackwardTime = Integer.parseInt(((String) fobComboBox.getSelectedItem()).replace(" 秒", ""));
//        f.currRate = Double.parseDouble(((String) rateComboBox.getSelectedItem()).replace("x", ""));
//        f.getPlayer().setRate(f.currRate);
        f.currSpecStyle = specStyleComboBox.getSelectedIndex();
        f.currBalance = balanceComboBox.getSelectedIndex() - 1;
        f.getPlayer().setBalance(f.currBalance);

        return true;
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
