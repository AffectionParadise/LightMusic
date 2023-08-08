package net.doge.ui.component.dialog;

import com.alibaba.fastjson2.JSONObject;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.Quality;
import net.doge.constant.system.SimplePath;
import net.doge.constant.ui.BlurConstants;
import net.doge.constant.ui.Colors;
import net.doge.constant.window.CloseWindowOptions;
import net.doge.constant.window.WindowSize;
import net.doge.constant.window.WindowState;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.DialogButton;
import net.doge.ui.component.checkbox.CustomCheckBox;
import net.doge.ui.component.combobox.CustomComboBox;
import net.doge.ui.component.combobox.ui.ComboBoxUI;
import net.doge.ui.component.dialog.factory.AbstractTitledDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.scrollpane.CustomScrollPane;
import net.doge.ui.component.scrollpane.ui.ScrollBarUI;
import net.doge.ui.component.textfield.CustomTextField;
import net.doge.ui.component.textfield.SafeDocument;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.system.FileUtil;
import net.doge.util.system.KeyUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author Doge
 * @Description 设置对话框
 * @Date 2020/12/15
 */
public class SettingDialog extends AbstractTitledDialog {
    // 目录不存在提示
    private final String CATALOG_NOT_FOUND_MSG = "该目录不存在";

    private CustomPanel centerPanel = new CustomPanel();
    private CustomScrollPane centerScrollPane = new CustomScrollPane(centerPanel);
    private CustomPanel buttonPanel = new CustomPanel();

    // 标题
    private CustomPanel generalPanel = new CustomPanel();
    private CustomLabel generalLabel = new CustomLabel("常规");
    private CustomPanel appearancePanel = new CustomPanel();
    private CustomLabel appearanceLabel = new CustomLabel("外观");
    private CustomPanel downloadAndCachePanel = new CustomPanel();
    private CustomLabel downloadAndCacheLabel = new CustomLabel("下载与缓存");
    private CustomPanel playbackPanel = new CustomPanel();
    private CustomLabel playbackLabel = new CustomLabel("播放与历史");
    private CustomPanel hotKeyPanel = new CustomPanel();
    private CustomLabel hotKeyLabel = new CustomLabel("快捷键");

    // 设置项
    private CustomPanel autoUpdatePanel = new CustomPanel();
    private CustomCheckBox autoUpdateCheckBox = new CustomCheckBox("启动时自动检查更新");
    private CustomPanel autoDownloadLrcPanel = new CustomPanel();
    private CustomCheckBox autoDownloadLrcCheckBox = new CustomCheckBox("下载歌曲时自动下载歌词");
    private CustomPanel videoOnlyPanel = new CustomPanel();
    private CustomCheckBox videoOnlyCheckBox = new CustomCheckBox("播放视频时隐藏主界面");
    private CustomPanel closeOptionPanel = new CustomPanel();
    private CustomLabel closeOptionLabel = new CustomLabel("关闭主界面时：");
    private CustomComboBox<String> closeOptionComboBox = new CustomComboBox();
    private CustomPanel windowSizePanel = new CustomPanel();
    private CustomLabel windowSizeLabel = new CustomLabel("窗口大小：");
    private CustomComboBox<String> windowSizeComboBox = new CustomComboBox();

    private CustomPanel showTabTextPanel = new CustomPanel();
    private CustomCheckBox showTabTextCheckBox = new CustomCheckBox("显示侧边栏文字");
    private CustomPanel gsFactorPanel = new CustomPanel();
    private CustomLabel gsFactorLabel = new CustomLabel("高斯模糊半径（半径越大越模糊）：");
    private CustomComboBox<String> gsFactorComboBox = new CustomComboBox();
    private CustomPanel darkerFactorPanel = new CustomPanel();
    private CustomLabel darkerFactorLabel = new CustomLabel("暗角滤镜因子（因子越小越暗）：");
    private CustomComboBox<String> darkerFactorComboBox = new CustomComboBox();
    //    private CustomPanel gradientColorStylePanel = new CustomPanel();
//    private CustomLabel gradientColorStyleLabel = new CustomLabel("线性渐变色彩风格：");
//    private CustomComboBox<String> gradientColorStyleComboBox = new CustomComboBox();

    private CustomPanel musicDownPanel = new CustomPanel();
    private CustomLabel musicDownLabel = new CustomLabel("歌曲下载路径：");
    private CustomTextField musicDownPathTextField = new CustomTextField(20);
    private DialogButton changeMusicDownPathButton;
    private DialogButton openMusicDownPathButton;
    private CustomPanel mvDownPanel = new CustomPanel();
    private CustomLabel mvDownLabel = new CustomLabel("MV 下载路径：");
    private CustomTextField mvDownPathTextField = new CustomTextField(20);
    private DialogButton changeMvDownPathButton;
    private DialogButton openMvDownPathButton;
    private CustomPanel cachePanel = new CustomPanel();
    private CustomLabel cacheLabel = new CustomLabel("缓存路径：");
    private CustomTextField cachePathTextField = new CustomTextField(20);
    private DialogButton changeCachePathButton;
    private DialogButton openCachePathButton;
    private final int maxCacheSizeLimit = 4096;
    private CustomPanel maxCacheSizePanel = new CustomPanel();
    private CustomLabel maxCacheSizeLabel = new CustomLabel(String.format("最大缓存大小(≤%sMB)：", maxCacheSizeLimit));
    private CustomTextField maxCacheSizeTextField = new CustomTextField(10);

    private CustomPanel qualityPanel = new CustomPanel();
    private CustomLabel qualityLabel = new CustomLabel("优先音质（如果可用）：");
    private CustomComboBox<String> qualityComboBox = new CustomComboBox();
    private CustomPanel fobPanel = new CustomPanel();
    private CustomLabel fobLabel = new CustomLabel("快进/快退时间：");
    private CustomComboBox<String> fobComboBox = new CustomComboBox();
    private CustomPanel balancePanel = new CustomPanel();
    private CustomLabel balanceLabel = new CustomLabel("声道平衡：");
    private CustomComboBox<String> balanceComboBox = new CustomComboBox();
    private final int maxHistoryCountLimit = 500;
    private CustomPanel maxHistoryCountPanel = new CustomPanel();
    private CustomLabel maxHistoryCountLabel = new CustomLabel(String.format("最大播放历史数量(≤%s)：", maxHistoryCountLimit));
    private CustomTextField maxHistoryCountTextField = new CustomTextField(10);
    private final int maxSearchHistoryLimit = 100;
    private CustomPanel maxSearchHistoryCountPanel = new CustomPanel();
    private CustomLabel maxSearchHistoryCountLabel = new CustomLabel(String.format("最大搜索历史数量(≤%s)：", maxSearchHistoryLimit));
    private CustomTextField maxSearchHistoryCountTextField = new CustomTextField(10);
    private final int maxConcurrentTaskCountLimit = 3;
    private CustomPanel maxConcurrentTaskCountPanel = new CustomPanel();
    private CustomLabel maxConcurrentTaskCountLabel = new CustomLabel(String.format("同时下载的最大任务数(≤%s)：", maxConcurrentTaskCountLimit));
    private CustomTextField maxConcurrentTaskCountTextField = new CustomTextField(10);
    private CustomPanel backupPanel = new CustomPanel();
    private CustomLabel backupLabel = new CustomLabel("播放列表备份/恢复（仅包括本地音乐列表、所有收藏列表）");
    private DialogButton backupListButton;
    private DialogButton restoreListButton;

    public List<Integer> playOrPauseKeys = new LinkedList<>();
    public List<Integer> playLastKeys = new LinkedList<>();
    public List<Integer> playNextKeys = new LinkedList<>();
    public List<Integer> backwardKeys = new LinkedList<>();
    public List<Integer> forwardKeys = new LinkedList<>();
    public List<Integer> videoFullScreenKeys = new LinkedList<>();
    public LinkedList<Integer> currKeys = new LinkedList<>();
    private CustomPanel keyPanel = new CustomPanel();
    private CustomLabel keyLabel = new CustomLabel("全局快捷键：");
    private CustomCheckBox enableKeyCheckBox = new CustomCheckBox("是否启用");
    private CustomPanel playOrPausePanel = new CustomPanel();
    private CustomLabel playOrPauseLabel = new CustomLabel("播放/暂停控制：");
    private CustomTextField playOrPauseTextField = new CustomTextField(10);
    private CustomPanel playLastPanel = new CustomPanel();
    private CustomLabel playLastLabel = new CustomLabel("上一首：");
    private CustomTextField playLastTextField = new CustomTextField(10);
    private CustomPanel playNextPanel = new CustomPanel();
    private CustomLabel playNextLabel = new CustomLabel("下一首：");
    private CustomTextField playNextTextField = new CustomTextField(10);
    private CustomPanel backwardPanel = new CustomPanel();
    private CustomLabel backwardLabel = new CustomLabel("快退：");
    private CustomTextField backwardTextField = new CustomTextField(10);
    private CustomPanel forwardPanel = new CustomPanel();
    private CustomLabel forwardLabel = new CustomLabel("快进：");
    private CustomTextField forwardTextField = new CustomTextField(10);
    private CustomPanel videoFullScreenPanel = new CustomPanel();
    private CustomLabel videoFullScreenLabel = new CustomLabel("视频全屏切换：");
    private CustomTextField videoFullScreenTextField = new CustomTextField(10);

    private DialogButton okButton;
    private DialogButton applyButton;
    private DialogButton cancelButton;

    private Object currKeyComp;
    private AWTEventListener keyBindListener;
    private AWTEventListener mouseListener;

    public SettingDialog(MainFrame f) {
        super(f, "设置");

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton("保存", textColor);
        applyButton = new DialogButton("应用", textColor);
        cancelButton = new DialogButton("取消", textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(720, 750);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();
        initSettings();

        globalPanel.add(centerScrollPane, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            if (!applySettings()) return;
            f.currDialogs.remove(this);
            // 移除监听器
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            toolkit.removeAWTEventListener(keyBindListener);
            toolkit.removeAWTEventListener(mouseListener);
            dispose();
        });
        applyButton.addActionListener(e -> {
            applySettings();
            new TipDialog(f, "应用成功", true).showDialog();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(globalPanel);
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 对齐
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        fl.setVgap(7);
        generalPanel.setLayout(fl);
        appearancePanel.setLayout(fl);
        downloadAndCachePanel.setLayout(fl);
        playbackPanel.setLayout(fl);
        hotKeyPanel.setLayout(fl);

        autoUpdatePanel.setLayout(fl);
        autoDownloadLrcPanel.setLayout(fl);
        videoOnlyPanel.setLayout(fl);
        showTabTextPanel.setLayout(fl);
        gsFactorPanel.setLayout(fl);
        darkerFactorPanel.setLayout(fl);
//        gradientColorStylePanel.setLayout(fl);
        musicDownPanel.setLayout(fl);
        mvDownPanel.setLayout(fl);
        cachePanel.setLayout(fl);
        maxCacheSizePanel.setLayout(fl);
        maxHistoryCountPanel.setLayout(fl);
        maxSearchHistoryCountPanel.setLayout(fl);
        maxConcurrentTaskCountPanel.setLayout(fl);
        closeOptionPanel.setLayout(fl);
        windowSizePanel.setLayout(fl);
        qualityPanel.setLayout(fl);
        fobPanel.setLayout(fl);
        balancePanel.setLayout(fl);
        backupPanel.setLayout(fl);
        keyPanel.setLayout(fl);
        playOrPausePanel.setLayout(fl);
        playLastPanel.setLayout(fl);
        playNextPanel.setLayout(fl);
        backwardPanel.setLayout(fl);
        forwardPanel.setLayout(fl);
        videoFullScreenPanel.setLayout(fl);

        generalLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // 标题边框
        Border tb = BorderFactory.createEmptyBorder(10, 20, 0, 0);
        generalPanel.setBorder(tb);
        appearancePanel.setBorder(tb);
        downloadAndCachePanel.setBorder(tb);
        playbackPanel.setBorder(tb);
        hotKeyPanel.setBorder(tb);

        // 边框
        Border b = BorderFactory.createEmptyBorder(0, 50, 0, 20);
        autoUpdatePanel.setBorder(b);
        autoDownloadLrcPanel.setBorder(b);
        videoOnlyPanel.setBorder(b);
        showTabTextPanel.setBorder(b);
        gsFactorPanel.setBorder(b);
        darkerFactorPanel.setBorder(b);
//        gradientColorStylePanel.setBorder(b);
        musicDownPanel.setBorder(b);
        mvDownPanel.setBorder(b);
        cachePanel.setBorder(b);
        maxCacheSizePanel.setBorder(b);
        maxHistoryCountPanel.setBorder(b);
        maxSearchHistoryCountPanel.setBorder(b);
        maxConcurrentTaskCountPanel.setBorder(b);
        closeOptionPanel.setBorder(b);
        windowSizePanel.setBorder(b);
        qualityPanel.setBorder(b);
        fobPanel.setBorder(b);
        balancePanel.setBorder(b);
        backupPanel.setBorder(b);
        keyPanel.setBorder(b);
        playOrPausePanel.setBorder(b);
        playLastPanel.setBorder(b);
        playNextPanel.setBorder(b);
        backwardPanel.setBorder(b);
        forwardPanel.setBorder(b);
        videoFullScreenPanel.setBorder(b);

        // 字体颜色
        Color textColor = f.currUIStyle.getTextColor();
        generalLabel.setForeground(textColor);
        appearanceLabel.setForeground(textColor);
        downloadAndCacheLabel.setForeground(textColor);
        playbackLabel.setForeground(textColor);
        hotKeyLabel.setForeground(textColor);

        autoUpdateCheckBox.setForeground(textColor);
        autoDownloadLrcCheckBox.setForeground(textColor);
        videoOnlyCheckBox.setForeground(textColor);
        showTabTextCheckBox.setForeground(textColor);
        gsFactorLabel.setForeground(textColor);
        darkerFactorLabel.setForeground(textColor);
//        gradientColorStyleLabel.setForeground(textColor);
        musicDownLabel.setForeground(textColor);
        mvDownLabel.setForeground(textColor);
        cacheLabel.setForeground(textColor);
        maxCacheSizeLabel.setForeground(textColor);
        maxHistoryCountLabel.setForeground(textColor);
        maxSearchHistoryCountLabel.setForeground(textColor);
        maxConcurrentTaskCountLabel.setForeground(textColor);
        closeOptionLabel.setForeground(textColor);
        windowSizeLabel.setForeground(textColor);
        qualityLabel.setForeground(textColor);
        fobLabel.setForeground(textColor);
        balanceLabel.setForeground(textColor);
        backupLabel.setForeground(textColor);
        keyLabel.setForeground(textColor);
        enableKeyCheckBox.setForeground(textColor);
        playOrPauseLabel.setForeground(textColor);
        playLastLabel.setForeground(textColor);
        playNextLabel.setForeground(textColor);
        backwardLabel.setForeground(textColor);
        forwardLabel.setForeground(textColor);
        videoFullScreenLabel.setForeground(textColor);

        // 文本框
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);
        musicDownPathTextField.setForeground(textColor);
        musicDownPathTextField.setCaretColor(textColor);
        musicDownPathTextField.setSelectionColor(darkerTextAlphaColor);
        mvDownPathTextField.setForeground(textColor);
        mvDownPathTextField.setCaretColor(textColor);
        mvDownPathTextField.setSelectionColor(darkerTextAlphaColor);
        cachePathTextField.setForeground(textColor);
        cachePathTextField.setCaretColor(textColor);
        cachePathTextField.setSelectionColor(darkerTextAlphaColor);
        maxCacheSizeTextField.setForeground(textColor);
        maxCacheSizeTextField.setCaretColor(textColor);
        maxCacheSizeTextField.setSelectionColor(darkerTextAlphaColor);
        SafeDocument doc = new SafeDocument(0, maxCacheSizeLimit);
        maxCacheSizeTextField.setDocument(doc);
        maxHistoryCountTextField.setForeground(textColor);
        maxHistoryCountTextField.setCaretColor(textColor);
        maxHistoryCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(0, maxHistoryCountLimit);
        maxHistoryCountTextField.setDocument(doc);
        maxSearchHistoryCountTextField.setForeground(textColor);
        maxSearchHistoryCountTextField.setCaretColor(textColor);
        maxSearchHistoryCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(0, maxSearchHistoryLimit);
        maxSearchHistoryCountTextField.setDocument(doc);
        maxConcurrentTaskCountTextField.setForeground(textColor);
        maxConcurrentTaskCountTextField.setCaretColor(textColor);
        maxConcurrentTaskCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(1, maxConcurrentTaskCountLimit);
        maxConcurrentTaskCountTextField.setDocument(doc);
        keyBindListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!(event instanceof KeyEvent) || currKeyComp == null) return;
                KeyEvent e = (KeyEvent) event;
                int code = e.getKeyCode();
                boolean released = e.getID() == KeyEvent.KEY_RELEASED, pressed = e.getID() == KeyEvent.KEY_PRESSED;
                if (!released && !pressed) return;

                if (released && !currKeys.isEmpty()) currKeys.removeLast();

                if (pressed) {
                    CustomTextField tf = (CustomTextField) currKeyComp;
                    if (currKeys.contains(code)) return;
                    currKeys.add(code);
                    // 检查重复按键
                    List<Integer> keyList = checkKeyDuplicated();
                    if (keyList != null) {
                        if (keyList == playOrPauseKeys) {
                            playOrPauseKeys.clear();
                            playOrPauseTextField.setText("");
                        } else if (keyList == playLastKeys) {
                            playLastKeys.clear();
                            playLastTextField.setText("");
                        } else if (keyList == playNextKeys) {
                            playNextKeys.clear();
                            playNextTextField.setText("");
                        } else if (keyList == backwardKeys) {
                            backwardKeys.clear();
                            backwardTextField.setText("");
                        } else if (keyList == forwardKeys) {
                            forwardKeys.clear();
                            forwardTextField.setText("");
                        } else if (keyList == videoFullScreenKeys) {
                            videoFullScreenKeys.clear();
                            videoFullScreenTextField.setText("");
                        }
                    }
                    // 暂存快捷键
                    if (tf == playOrPauseTextField) {
                        playOrPauseKeys.clear();
                        playOrPauseKeys.addAll(currKeys);
                    } else if (tf == playLastTextField) {
                        playLastKeys.clear();
                        playLastKeys.addAll(currKeys);
                    } else if (tf == playNextTextField) {
                        playNextKeys.clear();
                        playNextKeys.addAll(currKeys);
                    } else if (tf == backwardTextField) {
                        backwardKeys.clear();
                        backwardKeys.addAll(currKeys);
                    } else if (tf == forwardTextField) {
                        forwardKeys.clear();
                        forwardKeys.addAll(currKeys);
                    } else if (tf == videoFullScreenTextField) {
                        videoFullScreenKeys.clear();
                        videoFullScreenKeys.addAll(currKeys);
                    }
                    tf.setText(KeyUtil.join(currKeys));
                }
            }

            // 检查是否有重复按键
            private List<Integer> checkKeyDuplicated() {
                if (ListUtil.equals(currKeys, playOrPauseKeys)) return playOrPauseKeys;
                if (ListUtil.equals(currKeys, playLastKeys)) return playLastKeys;
                if (ListUtil.equals(currKeys, playNextKeys)) return playNextKeys;
                if (ListUtil.equals(currKeys, backwardKeys)) return backwardKeys;
                if (ListUtil.equals(currKeys, forwardKeys)) return forwardKeys;
                if (ListUtil.equals(currKeys, videoFullScreenKeys)) return videoFullScreenKeys;
                return null;
            }
        };
        mouseListener = event -> {
            if (event instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) event;
                if (me.getID() == MouseEvent.MOUSE_PRESSED) currKeyComp = null;
            }
        };
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.addAWTEventListener(keyBindListener, AWTEvent.KEY_EVENT_MASK);
        toolkit.addAWTEventListener(mouseListener, AWTEvent.MOUSE_EVENT_MASK);
        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                currKeyComp = e.getSource();
                currKeys.clear();
            }
        };
        playOrPauseTextField.setForeground(textColor);
        playOrPauseTextField.setCaretColor(textColor);
        playOrPauseTextField.setSelectionColor(darkerTextAlphaColor);
        playOrPauseTextField.setEditable(false);
        playOrPauseTextField.addFocusListener(focusAdapter);
        playLastTextField.setForeground(textColor);
        playLastTextField.setCaretColor(textColor);
        playLastTextField.setSelectionColor(darkerTextAlphaColor);
        playLastTextField.setEditable(false);
        playLastTextField.addFocusListener(focusAdapter);
        playNextTextField.setForeground(textColor);
        playNextTextField.setCaretColor(textColor);
        playNextTextField.setSelectionColor(darkerTextAlphaColor);
        playNextTextField.setEditable(false);
        playNextTextField.addFocusListener(focusAdapter);
        backwardTextField.setForeground(textColor);
        backwardTextField.setCaretColor(textColor);
        backwardTextField.setSelectionColor(darkerTextAlphaColor);
        backwardTextField.setEditable(false);
        backwardTextField.addFocusListener(focusAdapter);
        forwardTextField.setForeground(textColor);
        forwardTextField.setCaretColor(textColor);
        forwardTextField.setSelectionColor(darkerTextAlphaColor);
        forwardTextField.setEditable(false);
        forwardTextField.addFocusListener(focusAdapter);
        videoFullScreenTextField.setForeground(textColor);
        videoFullScreenTextField.setCaretColor(textColor);
        videoFullScreenTextField.setSelectionColor(darkerTextAlphaColor);
        videoFullScreenTextField.setEditable(false);
        videoFullScreenTextField.addFocusListener(focusAdapter);

        // 下拉框 UI
        gsFactorComboBox.setUI(new ComboBoxUI(gsFactorComboBox, f));
        darkerFactorComboBox.setUI(new ComboBoxUI(darkerFactorComboBox, f));
//        gradientColorStyleComboBox.setUI(new ComboBoxUI(gradientColorStyleComboBox, f));
        closeOptionComboBox.setUI(new ComboBoxUI(closeOptionComboBox, f));
        windowSizeComboBox.setUI(new ComboBoxUI(windowSizeComboBox, f));
        qualityComboBox.setUI(new ComboBoxUI(qualityComboBox, f));
        fobComboBox.setUI(new ComboBoxUI(fobComboBox, f));
        balanceComboBox.setUI(new ComboBoxUI(balanceComboBox, f));

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择文件夹");

        // 按钮
        changeMusicDownPathButton = new DialogButton("更改", textColor);
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
        openMusicDownPathButton = new DialogButton("打开", textColor);
        openMusicDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(musicDownPathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeMvDownPathButton = new DialogButton("更改", textColor);
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
        openMvDownPathButton = new DialogButton("打开", textColor);
        openMvDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(mvDownPathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeCachePathButton = new DialogButton("更改", textColor);
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
        openCachePathButton = new DialogButton("打开", textColor);
        openCachePathButton.addActionListener(e -> {
            try {
                File dir = new File(cachePathTextField.getText());
                if (dir == null) return;
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("json 文件", "*.json");
        fileChooser.getExtensionFilters().add(filter);
        backupListButton = new DialogButton("备份", textColor);
        backupListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle("保存文件");
                File output = fileChooser.showSaveDialog(null);
                if (output == null) return;
                JSONObject config = new JSONObject();
                f.putLocalMusicList(config);
                f.putCollectedItemList(config);
                new TipDialog(f, JsonUtil.toFile(config, output) ? "备份成功" : "备份失败", true).showDialog();
            });
        });
        restoreListButton = new DialogButton("恢复", textColor);
        restoreListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle("选择文件");
                File input = fileChooser.showOpenDialog(null);
                if (input == null) return;
                JSONObject config = JsonUtil.read(input);
                f.loadLocalMusicList(config);
                f.loadCollectedMusicList(config);
                new TipDialog(f, "恢复成功", true).showDialog();
            });
        });

        // 复选框图标
        Color iconColor = f.currUIStyle.getIconColor();
        ImageIcon icon = ImageUtil.dye(f.uncheckedIcon, iconColor);
        ImageIcon selectedIcon = ImageUtil.dye(f.checkedIcon, iconColor);
        autoUpdateCheckBox.setIcon(icon);
        autoUpdateCheckBox.setSelectedIcon(selectedIcon);
        autoDownloadLrcCheckBox.setIcon(icon);
        autoDownloadLrcCheckBox.setSelectedIcon(selectedIcon);
        videoOnlyCheckBox.setIcon(icon);
        videoOnlyCheckBox.setSelectedIcon(selectedIcon);
        showTabTextCheckBox.setIcon(icon);
        showTabTextCheckBox.setSelectedIcon(selectedIcon);
        enableKeyCheckBox.setIcon(icon);
        enableKeyCheckBox.setSelectedIcon(selectedIcon);

        // 标题
        generalPanel.add(generalLabel);
        appearancePanel.add(appearanceLabel);
        downloadAndCachePanel.add(downloadAndCacheLabel);
        playbackPanel.add(playbackLabel);
        hotKeyPanel.add(hotKeyLabel);

        autoUpdatePanel.add(autoUpdateCheckBox);

        autoDownloadLrcPanel.add(autoDownloadLrcCheckBox);

        videoOnlyPanel.add(videoOnlyCheckBox);

        showTabTextPanel.add(showTabTextCheckBox);

        for (String name : BlurConstants.gaussianFactorName) gsFactorComboBox.addItem(name);
        gsFactorPanel.add(gsFactorLabel);
        gsFactorPanel.add(gsFactorComboBox);

        for (String name : BlurConstants.darkerFactorName) darkerFactorComboBox.addItem(name);
        darkerFactorPanel.add(darkerFactorLabel);
        darkerFactorPanel.add(darkerFactorComboBox);

//        for (String name : BlurConstants.gradientColorStyleName) gradientColorStyleComboBox.addItem(name);
//        gradientColorStylePanel.add(gradientColorStyleLabel);
//        gradientColorStylePanel.add(gradientColorStyleComboBox);

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

        for (String name : CloseWindowOptions.NAMES) closeOptionComboBox.addItem(name);
        closeOptionPanel.add(closeOptionLabel);
        closeOptionPanel.add(closeOptionComboBox);

        for (String name : WindowSize.NAMES) windowSizeComboBox.addItem(name);
        windowSizePanel.add(windowSizeLabel);
        windowSizePanel.add(windowSizeComboBox);

        for (String name : Quality.NAMES) qualityComboBox.addItem(name);
        qualityPanel.add(qualityLabel);
        qualityPanel.add(qualityComboBox);

        for (int i = 5; i <= 60; i += 5) {
            String item = i + " 秒";
            fobComboBox.addItem(item);
            if (f.forwardOrBackwardTime == i) fobComboBox.setSelectedItem(item);
        }
        fobPanel.add(fobLabel);
        fobPanel.add(fobComboBox);

//        for (String name : SpectrumConstants.NAMES) specStyleComboBox.addItem(name);
//        specStylePanel.add(specStyleLabel);
//        specStylePanel.add(specStyleComboBox);

        balanceComboBox.addItem("左声道");
        balanceComboBox.addItem("立体声");
        balanceComboBox.addItem("右声道");
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceComboBox);

        backupPanel.add(backupLabel);
        backupPanel.add(backupListButton);
        backupPanel.add(restoreListButton);

        keyPanel.add(keyLabel);
        keyPanel.add(enableKeyCheckBox);
        playOrPausePanel.add(playOrPauseLabel);
        playOrPausePanel.add(playOrPauseTextField);
        playLastPanel.add(playLastLabel);
        playLastPanel.add(playLastTextField);
        playNextPanel.add(playNextLabel);
        playNextPanel.add(playNextTextField);
        backwardPanel.add(backwardLabel);
        backwardPanel.add(backwardTextField);
        forwardPanel.add(forwardLabel);
        forwardPanel.add(forwardTextField);
        videoFullScreenPanel.add(videoFullScreenLabel);
        videoFullScreenPanel.add(videoFullScreenTextField);

        centerPanel.add(generalPanel);
        centerPanel.add(autoUpdatePanel);
        centerPanel.add(autoDownloadLrcPanel);
        centerPanel.add(videoOnlyPanel);
        centerPanel.add(closeOptionPanel);
        centerPanel.add(windowSizePanel);

        centerPanel.add(appearancePanel);
        centerPanel.add(showTabTextPanel);
        centerPanel.add(gsFactorPanel);
        centerPanel.add(darkerFactorPanel);
//        centerPanel.add(gradientColorStylePanel);

        centerPanel.add(downloadAndCachePanel);
        centerPanel.add(musicDownPanel);
        centerPanel.add(mvDownPanel);
        centerPanel.add(cachePanel);
        centerPanel.add(maxCacheSizePanel);
        centerPanel.add(maxConcurrentTaskCountPanel);

        centerPanel.add(playbackPanel);
        centerPanel.add(qualityPanel);
        centerPanel.add(fobPanel);
        centerPanel.add(balancePanel);
        centerPanel.add(maxHistoryCountPanel);
        centerPanel.add(maxSearchHistoryCountPanel);
        centerPanel.add(backupPanel);

        centerPanel.add(hotKeyPanel);
        centerPanel.add(keyPanel);
        centerPanel.add(playOrPausePanel);
        centerPanel.add(playLastPanel);
        centerPanel.add(playNextPanel);
        centerPanel.add(backwardPanel);
        centerPanel.add(forwardPanel);
        centerPanel.add(videoFullScreenPanel);

        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        centerScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    // 加载设置
    private void initSettings() {
        autoUpdateCheckBox.setSelected(f.autoUpdate);
        autoDownloadLrcCheckBox.setSelected(f.isAutoDownloadLrc);
        videoOnlyCheckBox.setSelected(f.videoOnly);
        showTabTextCheckBox.setSelected(f.showTabText);
        gsFactorComboBox.setSelectedIndex(BlurConstants.gsFactorIndex);
        darkerFactorComboBox.setSelectedIndex(BlurConstants.darkerFactorIndex);
//        gradientColorStyleComboBox.setSelectedIndex(BlurConstants.gradientColorStyleIndex);
        musicDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MUSIC_PATH).getAbsolutePath());
        mvDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MV_PATH).getAbsolutePath());
        cachePathTextField.setText(new File(SimplePath.CACHE_PATH).getAbsolutePath());
        maxCacheSizeTextField.setText(String.valueOf(f.maxCacheSize));
        maxHistoryCountTextField.setText(String.valueOf(f.maxHistoryCount));
        maxSearchHistoryCountTextField.setText(String.valueOf(f.maxSearchHistoryCount));
        maxConcurrentTaskCountTextField.setText(String.valueOf(((ThreadPoolExecutor) GlobalExecutors.downloadExecutor).getCorePoolSize()));
        closeOptionComboBox.setSelectedIndex(f.currCloseWindowOption);
        windowSizeComboBox.setSelectedIndex(f.windowSize);
        qualityComboBox.setSelectedIndex(Quality.quality);
        balanceComboBox.setSelectedIndex(Double.valueOf(f.currBalance).intValue() + 1);

        enableKeyCheckBox.setSelected(f.keyEnabled);
        playOrPauseKeys.addAll(f.playOrPauseKeys);
        playOrPauseTextField.setText(KeyUtil.join(f.playOrPauseKeys));
        playLastKeys.addAll(f.playLastKeys);
        playLastTextField.setText(KeyUtil.join(f.playLastKeys));
        playNextKeys.addAll(f.playNextKeys);
        playNextTextField.setText(KeyUtil.join(f.playNextKeys));
        backwardKeys.addAll(f.backwardKeys);
        backwardTextField.setText(KeyUtil.join(f.backwardKeys));
        forwardKeys.addAll(f.forwardKeys);
        forwardTextField.setText(KeyUtil.join(f.forwardKeys));
        videoFullScreenKeys.addAll(f.videoFullScreenKeys);
        videoFullScreenTextField.setText(KeyUtil.join(f.videoFullScreenKeys));
    }

    // 应用设置
    private boolean applySettings() {
        // 验证
        File musicDir = new File(musicDownPathTextField.getText());
        if (!musicDir.exists()) {
            new TipDialog(f, "歌曲下载路径无效", true).showDialog();
            return false;
        }
        File mvDir = new File(mvDownPathTextField.getText());
        if (!mvDir.exists()) {
            new TipDialog(f, "MV 下载路径无效", true).showDialog();
            return false;
        }
        File cacheDir = new File(cachePathTextField.getText());
        if (!cacheDir.exists()) {
            new TipDialog(f, "缓存路径无效", true).showDialog();
            return false;
        }

        f.autoUpdate = autoUpdateCheckBox.isSelected();
        f.isAutoDownloadLrc = autoDownloadLrcCheckBox.isSelected();
        f.videoOnly = videoOnlyCheckBox.isSelected();

        f.showTabText = showTabTextCheckBox.isSelected();
        f.updateTabSize();

        int gsFactorIndex = BlurConstants.gsFactorIndex;
        BlurConstants.gsFactorIndex = gsFactorComboBox.getSelectedIndex();

        int darkerFactorIndex = BlurConstants.darkerFactorIndex;
        BlurConstants.darkerFactorIndex = darkerFactorComboBox.getSelectedIndex();

//        int gradientColorStyleIndex = BlurConstants.gradientColorStyleIndex;
//        BlurConstants.gradientColorStyleIndex = gradientColorStyleComboBox.getSelectedIndex();

        if (gsFactorIndex != BlurConstants.gsFactorIndex || darkerFactorIndex != BlurConstants.darkerFactorIndex)
            f.doBlur();

        SimplePath.DOWNLOAD_MUSIC_PATH = musicDir.getAbsolutePath() + File.separator;
        SimplePath.DOWNLOAD_MV_PATH = mvDir.getAbsolutePath() + File.separator;
        SimplePath.CACHE_PATH = cacheDir.getAbsolutePath() + File.separator;
        SimplePath.IMG_CACHE_PATH = SimplePath.CACHE_PATH + File.separator + "img" + File.separator;
        // 更改缓存图像路径并创建
        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);

        String text = maxCacheSizeTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, "最大缓存大小无效", true).showDialog();
            return false;
        }
        f.maxCacheSize = Long.parseLong(text);

        text = maxHistoryCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, "最大播放历史数量无效", true).showDialog();
            return false;
        }
        f.maxHistoryCount = Integer.parseInt(text);

        text = maxSearchHistoryCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, "最大搜索历史数量无效", true).showDialog();
            return false;
        }
        f.maxSearchHistoryCount = Integer.parseInt(text);

        text = maxConcurrentTaskCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, "同时下载的最大任务数无效", true).showDialog();
            return false;
        }
        GlobalExecutors.downloadExecutor = Executors.newFixedThreadPool(Integer.parseInt(text));

        // 删除多余的播放历史记录
        for (int i = f.maxHistoryCount, s = f.historyModel.size(); i < s; i++)
            f.historyModel.remove(f.maxHistoryCount);

        // 删除多余的搜索历史记录
        CustomPanel[] ips = new CustomPanel[]{f.netMusicHistorySearchInnerPanel2,
                f.netPlaylistHistorySearchInnerPanel2,
                f.netAlbumHistorySearchInnerPanel2,
                f.netArtistHistorySearchInnerPanel2,
                f.netRadioHistorySearchInnerPanel2,
                f.netMvHistorySearchInnerPanel2,
                f.netUserHistorySearchInnerPanel2};
        for (int i = 0, len = ips.length; i < len; i++) {
            CustomPanel ip = ips[i];
            for (int j = f.maxSearchHistoryCount, c = ip.getComponentCount(); j < c; j++) {
                ip.remove(f.maxSearchHistoryCount);
            }
        }

        f.currCloseWindowOption = closeOptionComboBox.getSelectedIndex();
        f.windowSize = windowSizeComboBox.getSelectedIndex();
        f.windowWidth = WindowSize.DIMENSIONS[f.windowSize][0];
        f.windowHeight = WindowSize.DIMENSIONS[f.windowSize][1];
        f.x = f.y = 0x3f3f3f3f;
        if (f.windowState != WindowState.MAXIMIZED) f.setSize(f.windowWidth, f.windowHeight);

        Quality.quality = qualityComboBox.getSelectedIndex();
        f.forwardOrBackwardTime = Integer.parseInt(((String) fobComboBox.getSelectedItem()).replace(" 秒", ""));
        f.currBalance = balanceComboBox.getSelectedIndex() - 1;
        f.player.setBalance(f.currBalance);

        f.keyEnabled = enableKeyCheckBox.isSelected();
        f.playOrPauseKeys.clear();
        f.playOrPauseKeys.addAll(playOrPauseKeys);
        f.playLastKeys.clear();
        f.playLastKeys.addAll(playLastKeys);
        f.playNextKeys.clear();
        f.playNextKeys.addAll(playNextKeys);
        f.backwardKeys.clear();
        f.backwardKeys.addAll(backwardKeys);
        f.forwardKeys.clear();
        f.forwardKeys.addAll(forwardKeys);
        f.videoFullScreenKeys.clear();
        f.videoFullScreenKeys.addAll(videoFullScreenKeys);

        return true;
    }
}
