package net.doge.ui.widget.dialog;

import com.alibaba.fastjson2.JSONObject;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.media.VideoQuality;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.core.ui.Fonts;
import net.doge.constant.core.ui.image.BackgroundEffectConstants;
import net.doge.constant.core.ui.lyric.LyricAlignment;
import net.doge.constant.core.ui.spectrum.SpectrumConstants;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.constant.core.ui.window.CloseWindowOptions;
import net.doge.constant.core.ui.window.WindowSize;
import net.doge.constant.core.ui.window.WindowState;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.checkbox.CustomCheckBox;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.StringComboBoxUI;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.tabbedpane.CustomTabbedPane;
import net.doge.ui.widget.tabbedpane.ui.CustomTabbedPaneUI;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.ui.widget.textfield.filter.LimitedDocumentFilter;
import net.doge.util.core.StringUtil;
import net.doge.util.core.collection.ListUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.json.JsonUtil;
import net.doge.util.core.os.DesktopUtil;
import net.doge.util.core.os.KeyUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Doge
 * @description 设置对话框
 * @date 2020/12/15
 */
public class SettingDialog extends AbstractTitledDialog {
    private final int WIDTH = ScaleUtil.scale(800);
    private final int HEIGHT = ScaleUtil.scale(650);
    // 目录不存在提示
    private final String CATALOG_NOT_FOUND_MSG = I18n.getText("catalogNotFoundMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    // 标签页
    private CustomTabbedPane tabbedPane = new CustomTabbedPane(CustomTabbedPane.LEFT);
    private CustomPanel generalPanel = new CustomPanel();
    private CustomLabel generalLabel = new CustomLabel(I18n.getText("generalTab"));
    private CustomBox generalContentBox = new CustomBox(BoxLayout.Y_AXIS);
    private CustomScrollPane generalScrollPane = new CustomScrollPane(generalContentBox);
    private CustomPanel appearancePanel = new CustomPanel();
    private CustomLabel appearanceLabel = new CustomLabel(I18n.getText("appearance"));
    private CustomBox appearanceContentBox = new CustomBox(BoxLayout.Y_AXIS);
    private CustomScrollPane appearanceScrollPane = new CustomScrollPane(appearanceContentBox);
    private CustomPanel downloadAndCachePanel = new CustomPanel();
    private CustomLabel downloadAndCacheLabel = new CustomLabel(I18n.getText("downloadAndCache"));
    private CustomBox downloadAndCacheContentBox = new CustomBox(BoxLayout.Y_AXIS);
    private CustomScrollPane downloadAndCacheScrollPane = new CustomScrollPane(downloadAndCacheContentBox);
    private CustomPanel playbackPanel = new CustomPanel();
    private CustomLabel playbackLabel = new CustomLabel(I18n.getText("playback"));
    private CustomBox playbackContentBox = new CustomBox(BoxLayout.Y_AXIS);
    private CustomScrollPane playbackScrollPane = new CustomScrollPane(playbackContentBox);
    private CustomPanel hotKeyPanel = new CustomPanel();
    private CustomLabel hotKeyLabel = new CustomLabel(I18n.getText("hotkey"));
    private CustomBox hotKeyContentBox = new CustomBox(BoxLayout.Y_AXIS);
    private CustomScrollPane hotKeyScrollPane = new CustomScrollPane(hotKeyContentBox);

    // 常规
    private CustomPanel autoUpdatePanel = new CustomPanel();
    private CustomCheckBox autoUpdateCheckBox = new CustomCheckBox(I18n.getText("autoUpdate"));
    private CustomPanel videoOnlyPanel = new CustomPanel();
    private CustomCheckBox videoOnlyCheckBox = new CustomCheckBox(I18n.getText("videoOnly"));
    private CustomPanel langPanel = new CustomPanel();
    private CustomLabel langLabel = new CustomLabel(I18n.getText("lang"));
    private CustomComboBox<String> langComboBox = new CustomComboBox<>();
    private CustomPanel fontPanel = new CustomPanel();
    private CustomLabel fontLabel = new CustomLabel(I18n.getText("font"));
    private CustomComboBox<String> fontComboBox = new CustomComboBox<>();
    private CustomPanel closeOptionPanel = new CustomPanel();
    private CustomLabel closeOptionLabel = new CustomLabel(I18n.getText("closeOption"));
    private CustomComboBox<String> closeOptionComboBox = new CustomComboBox<>();
    private CustomPanel windowSizePanel = new CustomPanel();
    private CustomLabel windowSizeLabel = new CustomLabel(I18n.getText("windowSize"));
    private CustomComboBox<String> windowSizeComboBox = new CustomComboBox<>();
    private CustomPanel tabEnabledPanel = new CustomPanel();
    private CustomLabel tabEnabledLabel = new CustomLabel(I18n.getText("tabEnabled"));
    private CustomPanel tabEnabledGroupPanel = new CustomPanel();
    private CustomCheckBox recommendTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netRecommend"));
    private CustomCheckBox rankTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netRank"));
    private CustomCheckBox playlistTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netPlaylist"));
    private CustomCheckBox albumTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netAlbum"));
    private CustomCheckBox artistTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netArtist"));
    private CustomCheckBox radioTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netRadio"));
    private CustomCheckBox mvTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netMv"));
    private CustomCheckBox userTabEnabledCheckBox = new CustomCheckBox(I18n.getText("netUser"));

    // 外观
    private CustomPanel showTabTextPanel = new CustomPanel();
    private CustomCheckBox showTabTextCheckBox = new CustomCheckBox(I18n.getText("showTabText"));
    private CustomPanel showSheetButtonPanel = new CustomPanel();
    private CustomCheckBox showSheetButtonCheckBox = new CustomCheckBox(I18n.getText("showSheetButton"));
    private CustomPanel showFobButtonPanel = new CustomPanel();
    private CustomCheckBox showFobButtonCheckBox = new CustomCheckBox(I18n.getText("showFobButton"));
    private CustomPanel lockDesktopLyricPanel = new CustomPanel();
    private CustomCheckBox lockDesktopLyricCheckBox = new CustomCheckBox(I18n.getText("lockDesktopLyric"));
    private CustomPanel lyricAlignmentPanel = new CustomPanel();
    private CustomLabel lyricAlignmentLabel = new CustomLabel(I18n.getText("lyricAlignment"));
    private CustomComboBox<String> lyricAlignmentComboBox = new CustomComboBox<>();
    private final int specMaxHeightLimit = 150;
    private CustomPanel specMaxHeightPanel = new CustomPanel();
    private CustomLabel specMaxHeightLabel = new CustomLabel(String.format(I18n.getText("specMaxHeight"), specMaxHeightLimit));
    private CustomTextField specMaxHeightTextField = new CustomTextField(10);
    private CustomPanel gsFactorPanel = new CustomPanel();
    private CustomLabel gsFactorLabel = new CustomLabel(I18n.getText("gsFactor"));
    private CustomComboBox<String> gsFactorComboBox = new CustomComboBox<>();
    private CustomPanel darkerFactorPanel = new CustomPanel();
    private CustomLabel darkerFactorLabel = new CustomLabel(I18n.getText("darkerFactor"));
    private CustomComboBox<String> darkerFactorComboBox = new CustomComboBox<>();

    // 下载与缓存
    private CustomPanel autoDownloadLyricPanel = new CustomPanel();
    private CustomCheckBox autoDownloadLyricCheckBox = new CustomCheckBox(I18n.getText("autoDownloadLyric"));
    private CustomPanel verbatimTimelinePanel = new CustomPanel();
    private CustomCheckBox verbatimTimelineCheckBox = new CustomCheckBox(I18n.getText("verbatimTimeline"));
    private CustomPanel audioDownQualityPanel = new CustomPanel();
    private CustomLabel audioDownQualityLabel = new CustomLabel(I18n.getText("audioDownQuality"));
    private CustomComboBox<String> audioDownQualityComboBox = new CustomComboBox<>();
    private CustomPanel videoDownQualityPanel = new CustomPanel();
    private CustomLabel videoDownQualityLabel = new CustomLabel(I18n.getText("videoDownQuality"));
    private CustomComboBox<String> videoDownQualityComboBox = new CustomComboBox<>();
    private CustomPanel musicDownPanel = new CustomPanel();
    private CustomLabel musicDownLabel = new CustomLabel(I18n.getText("musicDown"));
    private CustomTextField musicDownPathTextField = new CustomTextField(20);
    private CustomPanel mvDownPanel = new CustomPanel();
    private CustomLabel mvDownLabel = new CustomLabel(I18n.getText("mvDown"));
    private CustomTextField mvDownPathTextField = new CustomTextField(20);
    private CustomPanel cachePanel = new CustomPanel();
    private CustomLabel cacheLabel = new CustomLabel(I18n.getText("cache"));
    private CustomTextField cachePathTextField = new CustomTextField(20);
    private final int maxCacheSizeLimit = 4096;
    private CustomPanel maxCacheSizePanel = new CustomPanel();
    private CustomLabel maxCacheSizeLabel = new CustomLabel(String.format(I18n.getText("maxCacheSize"), maxCacheSizeLimit));
    private CustomTextField maxCacheSizeTextField = new CustomTextField(10);
    private final int maxConcurrentTaskCountLimit = 3;
    private CustomPanel maxConcurrentTaskCountPanel = new CustomPanel();
    private CustomLabel maxConcurrentTaskCountLabel = new CustomLabel(String.format(I18n.getText("maxConcurrentTaskCount"), maxConcurrentTaskCountLimit));
    private CustomTextField maxConcurrentTaskCountTextField = new CustomTextField(10);

    // 播放与历史
    private CustomPanel audioPlayQualityPanel = new CustomPanel();
    private CustomLabel audioPlayQualityLabel = new CustomLabel(I18n.getText("audioPlayQuality"));
    private CustomComboBox<String> audioPlayQualityComboBox = new CustomComboBox<>();
    private CustomPanel videoPlayQualityPanel = new CustomPanel();
    private CustomLabel videoPlayQualityLabel = new CustomLabel(I18n.getText("videoPlayQuality"));
    private CustomComboBox<String> videoPlayQualityComboBox = new CustomComboBox<>();
    private CustomPanel fobPanel = new CustomPanel();
    private CustomLabel fobLabel = new CustomLabel(I18n.getText("fob"));
    private CustomComboBox<String> fobComboBox = new CustomComboBox<>();
    private CustomPanel balancePanel = new CustomPanel();
    private CustomLabel balanceLabel = new CustomLabel(I18n.getText("balance"));
    private CustomComboBox<String> balanceComboBox = new CustomComboBox<>();
    private final int maxHistoryCountLimit = 500;
    private CustomPanel maxHistoryCountPanel = new CustomPanel();
    private CustomLabel maxHistoryCountLabel = new CustomLabel(String.format(I18n.getText("maxHistoryCount"), maxHistoryCountLimit));
    private CustomTextField maxHistoryCountTextField = new CustomTextField(10);
    private final int maxSearchHistoryLimit = 100;
    private CustomPanel maxSearchHistoryCountPanel = new CustomPanel();
    private CustomLabel maxSearchHistoryCountLabel = new CustomLabel(String.format(I18n.getText("maxSearchHistoryCount"), maxSearchHistoryLimit));
    private CustomTextField maxSearchHistoryCountTextField = new CustomTextField(10);
    private CustomPanel backupPanel = new CustomPanel();
    private CustomLabel backupLabel = new CustomLabel(I18n.getText("backupAndRestore"));

    // 快捷键
    public List<Integer> playOrPauseKeys = new LinkedList<>();
    public List<Integer> playLastKeys = new LinkedList<>();
    public List<Integer> playNextKeys = new LinkedList<>();
    public List<Integer> backwardKeys = new LinkedList<>();
    public List<Integer> forwardKeys = new LinkedList<>();
    public List<Integer> videoFullScreenKeys = new LinkedList<>();
    public LinkedList<Integer> currKeys = new LinkedList<>();
    private CustomPanel keyPanel = new CustomPanel();
    private CustomLabel keyLabel = new CustomLabel(I18n.getText("key"));
    private CustomCheckBox enableKeyCheckBox = new CustomCheckBox(I18n.getText("enableKey"));
    private CustomPanel playOrPausePanel = new CustomPanel();
    private CustomLabel playOrPauseLabel = new CustomLabel(I18n.getText("playOrPause"));
    private CustomTextField playOrPauseTextField = new CustomTextField(10);
    private CustomPanel playLastPanel = new CustomPanel();
    private CustomLabel playLastLabel = new CustomLabel(I18n.getText("playLast"));
    private CustomTextField playLastTextField = new CustomTextField(10);
    private CustomPanel playNextPanel = new CustomPanel();
    private CustomLabel playNextLabel = new CustomLabel(I18n.getText("playNext"));
    private CustomTextField playNextTextField = new CustomTextField(10);
    private CustomPanel backwardPanel = new CustomPanel();
    private CustomLabel backwardLabel = new CustomLabel(I18n.getText("backward"));
    private CustomTextField backwardTextField = new CustomTextField(10);
    private CustomPanel forwardPanel = new CustomPanel();
    private CustomLabel forwardLabel = new CustomLabel(I18n.getText("forward"));
    private CustomTextField forwardTextField = new CustomTextField(10);
    private CustomPanel videoFullScreenPanel = new CustomPanel();
    private CustomLabel videoFullScreenLabel = new CustomLabel(I18n.getText("videoFullScreen"));
    private CustomTextField videoFullScreenTextField = new CustomTextField(10);

    private DialogButton okButton;
    private DialogButton applyButton;
    private DialogButton cancelButton;

    public SettingDialog(MainFrame f) {
        super(f, I18n.getText("settingTitle"));

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        applyButton = new DialogButton(I18n.getText("dialogApply"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);
    }

    public void showDialog() {
        setSize(WIDTH, HEIGHT);

        initTitleBar();
        initView();
        initSettings();

        setLocationRelativeTo(null);
        updateBgImg();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        addSettingsTab(generalPanel, generalLabel, generalScrollPane);
        addSettingsTab(appearancePanel, appearanceLabel, appearanceScrollPane);
        addSettingsTab(downloadAndCachePanel, downloadAndCacheLabel, downloadAndCacheScrollPane);
        addSettingsTab(playbackPanel, playbackLabel, playbackScrollPane);
        addSettingsTab(hotKeyPanel, hotKeyLabel, hotKeyScrollPane);
        tabbedPane.setUI(new CustomTabbedPaneUI());
        tabbedPane.addChangeListener(e -> f.updateTabUI(tabbedPane, UIStyleStorage.currUIStyle));

        initGeneralView();
        initAppearanceView();
        initDownloadAndCacheView();
        initPlaybackView();
        initHotKeyView();

        CustomBox[] contentBoxes = {generalContentBox, appearanceContentBox, downloadAndCacheContentBox,
                playbackContentBox, hotKeyContentBox};
        for (CustomBox box : contentBoxes) box.add(CustomBox.createVerticalGlue());

        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        globalPanel.add(centerPanel, BorderLayout.CENTER);

        okButton.addActionListener(e -> {
            if (!applySettings()) return;
            close();
        });
        applyButton.addActionListener(e -> {
            if (!applySettings()) return;
            new TipDialog(f, I18n.getText("applySuccess"), true).showDialog();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // 常规
    private void initGeneralView() {
        addSettingRow(generalContentBox, autoUpdatePanel, autoUpdateCheckBox);
        addSettingRow(generalContentBox, videoOnlyPanel, videoOnlyCheckBox);
        addComboRow(generalContentBox, langPanel, langLabel, langComboBox, I18n.NAMES);
        addComboRow(generalContentBox, fontPanel, fontLabel, fontComboBox, Fonts.NAMES);
        addComboRow(generalContentBox, closeOptionPanel, closeOptionLabel, closeOptionComboBox, CloseWindowOptions.NAMES);
        addComboRow(generalContentBox, windowSizePanel, windowSizeLabel, windowSizeComboBox, WindowSize.NAMES);

        addSettingRow(generalContentBox, tabEnabledPanel, tabEnabledLabel);
        HDFlowLayout flow = new HDFlowLayout(HDFlowLayout.LEFT, 15, 5);
        tabEnabledGroupPanel.setLayout(flow);
        addSettingComponents(tabEnabledGroupPanel, recommendTabEnabledCheckBox, rankTabEnabledCheckBox,
                playlistTabEnabledCheckBox, albumTabEnabledCheckBox, artistTabEnabledCheckBox,
                radioTabEnabledCheckBox, mvTabEnabledCheckBox, userTabEnabledCheckBox);

        // 预留侧栏、边距和滚动条空间，再按流式布局测量换行后的高度
        int availableWidth = WIDTH - 2 * pixels - generalPanel.getPreferredSize().width
                - generalScrollPane.getVBar().getPreferredSize().width - ScaleUtil.scale(15);
        Dimension groupSize = tabEnabledGroupPanel.getPreferredSize();
        groupSize.width = Math.min(groupSize.width, availableWidth);
        tabEnabledGroupPanel.setSize(groupSize);
        tabEnabledGroupPanel.doLayout();
        for (Component checkBox : tabEnabledGroupPanel.getComponents())
            groupSize.height = Math.max(groupSize.height, checkBox.getY() + checkBox.getHeight() + flow.getVgap());
        tabEnabledGroupPanel.setPreferredSize(groupSize);
        tabEnabledGroupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, groupSize.height));
        generalContentBox.add(tabEnabledGroupPanel);
    }

    // 外观
    private void initAppearanceView() {
        addSettingRow(appearanceContentBox, showTabTextPanel, showTabTextCheckBox);
        addSettingRow(appearanceContentBox, showSheetButtonPanel, showSheetButtonCheckBox);
        addSettingRow(appearanceContentBox, showFobButtonPanel, showFobButtonCheckBox);
        addSettingRow(appearanceContentBox, lockDesktopLyricPanel, lockDesktopLyricCheckBox);
        addComboRow(appearanceContentBox, lyricAlignmentPanel, lyricAlignmentLabel, lyricAlignmentComboBox, LyricAlignment.NAMES);
        specMaxHeightTextField.setDocumentFilter(new LimitedDocumentFilter(0, specMaxHeightLimit));
        addSettingRow(appearanceContentBox, specMaxHeightPanel, specMaxHeightLabel, specMaxHeightTextField);
        addComboRow(appearanceContentBox, gsFactorPanel, gsFactorLabel, gsFactorComboBox, BackgroundEffectConstants.GAUSSIAN_FACTOR_NAME);
        addComboRow(appearanceContentBox, darkerFactorPanel, darkerFactorLabel, darkerFactorComboBox, BackgroundEffectConstants.DARKER_FACTOR_NAME);
    }

    // 下载与缓存
    private void initDownloadAndCacheView() {
        addSettingRow(downloadAndCacheContentBox, autoDownloadLyricPanel, autoDownloadLyricCheckBox);
        addSettingRow(downloadAndCacheContentBox, verbatimTimelinePanel, verbatimTimelineCheckBox);
        addComboRow(downloadAndCacheContentBox, audioDownQualityPanel, audioDownQualityLabel, audioDownQualityComboBox, AudioQuality.NAMES);
        addComboRow(downloadAndCacheContentBox, videoDownQualityPanel, videoDownQualityLabel, videoDownQualityComboBox, VideoQuality.NAMES);

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(I18n.getText("chooseFolder"));
        addDirectoryRow(musicDownPanel, musicDownLabel, musicDownPathTextField, dirChooser);
        addDirectoryRow(mvDownPanel, mvDownLabel, mvDownPathTextField, dirChooser);
        addDirectoryRow(cachePanel, cacheLabel, cachePathTextField, dirChooser);

        maxCacheSizeTextField.setDocumentFilter(new LimitedDocumentFilter(0, maxCacheSizeLimit));
        addSettingRow(downloadAndCacheContentBox, maxCacheSizePanel, maxCacheSizeLabel, maxCacheSizeTextField);
        maxConcurrentTaskCountTextField.setDocumentFilter(new LimitedDocumentFilter(1, maxConcurrentTaskCountLimit));
        addSettingRow(downloadAndCacheContentBox, maxConcurrentTaskCountPanel, maxConcurrentTaskCountLabel, maxConcurrentTaskCountTextField);
    }

    // 播放与历史
    private void initPlaybackView() {
        addComboRow(playbackContentBox, audioPlayQualityPanel, audioPlayQualityLabel, audioPlayQualityComboBox, AudioQuality.NAMES);
        addComboRow(playbackContentBox, videoPlayQualityPanel, videoPlayQualityLabel, videoPlayQualityComboBox, VideoQuality.NAMES);

        fobComboBox.setUI(new StringComboBoxUI(fobComboBox, f));
        for (int i = 5; i <= 60; i += 5) fobComboBox.addItem(i + I18n.getText("seconds"));
        addSettingRow(playbackContentBox, fobPanel, fobLabel, fobComboBox);
        addComboRow(playbackContentBox, balancePanel, balanceLabel, balanceComboBox,
                I18n.getText("leftChannel"), I18n.getText("stereo"), I18n.getText("rightChannel"));

        maxHistoryCountTextField.setDocumentFilter(new LimitedDocumentFilter(0, maxHistoryCountLimit));
        addSettingRow(playbackContentBox, maxHistoryCountPanel, maxHistoryCountLabel, maxHistoryCountTextField);
        maxSearchHistoryCountTextField.setDocumentFilter(new LimitedDocumentFilter(0, maxSearchHistoryLimit));
        addSettingRow(playbackContentBox, maxSearchHistoryCountPanel, maxSearchHistoryCountLabel, maxSearchHistoryCountTextField);

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(I18n.getText("jsonFile"), "*.json");
        fileChooser.getExtensionFilters().add(filter);
        DialogButton backupListButton = new DialogButton(I18n.getText("backup"), textColor);
        backupListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle(I18n.getText("saveFile"));
                File output = fileChooser.showSaveDialog(null);
                if (output == null) return;
                JSONObject config = new JSONObject();
                f.putLocalMusicList(config);
                f.putCollectedItemList(config);
                new TipDialog(f, JsonUtil.toFile(config, output) ? I18n.getText("backupSuccess") : I18n.getText("backupFailed"), true).showDialog();
            });
        });
        DialogButton restoreListButton = new DialogButton(I18n.getText("restore"), textColor);
        restoreListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle(I18n.getText("chooseFile"));
                File input = fileChooser.showOpenDialog(null);
                if (input == null) return;
                JSONObject config = JsonUtil.readOrCreate(input);
                f.loadLocalMusicList(config);
                f.loadCollectedMusicList(config);
                new TipDialog(f, I18n.getText("restoreSuccess"), true).showDialog();
            });
        });
        addSettingRow(playbackContentBox, backupPanel, backupLabel, backupListButton, restoreListButton);
    }

    // 热键
    private void initHotKeyView() {
        addSettingRow(hotKeyContentBox, keyPanel, keyLabel, enableKeyCheckBox);
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                // 保持按下状态会多次出发，屏蔽多余的按键
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
                CustomTextField tf = (CustomTextField) e.getSource();
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
                tf.setText(KeyUtil.toStr(currKeys));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (currKeys.isEmpty()) return;
                currKeys.removeLastOccurrence(e.getKeyCode());
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
        addHotKeyRow(playOrPausePanel, playOrPauseLabel, playOrPauseTextField, keyAdapter);
        addHotKeyRow(playLastPanel, playLastLabel, playLastTextField, keyAdapter);
        addHotKeyRow(playNextPanel, playNextLabel, playNextTextField, keyAdapter);
        addHotKeyRow(backwardPanel, backwardLabel, backwardTextField, keyAdapter);
        addHotKeyRow(forwardPanel, forwardLabel, forwardTextField, keyAdapter);
        addHotKeyRow(videoFullScreenPanel, videoFullScreenLabel, videoFullScreenTextField, keyAdapter);
    }

    // 添加设置标签页
    private void addSettingsTab(CustomPanel panel, CustomLabel label, CustomScrollPane scrollPane) {
        panel.setLayout(new BorderLayout());
        panel.setBorder(new HDEmptyBorder(0, 10, 0, 0));
        panel.setPreferredSize(new HDDimension(120, 40));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setForeground(UIStyleStorage.currUIStyle.getTextColor());
        panel.add(label, BorderLayout.CENTER);
        tabbedPane.add(scrollPane);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, panel);
    }

    // 添加一行设置项
    private void addSettingRow(CustomBox contentBox, CustomPanel panel, JComponent... components) {
        panel.setLayout(new HDFlowLayout(HDFlowLayout.LEFT));
        panel.setMaximumSize(new HDDimension(Integer.MAX_VALUE, 30));
        addSettingComponents(panel, components);
        if (contentBox.getComponentCount() > 0) contentBox.add(CustomBox.createVerticalStrut(ScaleUtil.scale(10)));
        contentBox.add(panel);
    }

    // 在设置项 Panel 中添加组件
    private void addSettingComponents(CustomPanel panel, JComponent... components) {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        for (JComponent component : components) {
            component.setForeground(textColor);
            if (component instanceof CustomCheckBox) {
                ((CustomCheckBox) component).updateIconStyle();
            } else if (component instanceof CustomTextField) {
                CustomTextField field = (CustomTextField) component;
                field.setCaretColor(textColor);
                field.setSelectedTextColor(textColor);
                field.setSelectionColor(ColorUtil.deriveAlpha(ColorUtil.darker(textColor), 0.5f));
            }
            panel.add(component);
        }
    }

    // 添加一行下拉框设置项
    private void addComboRow(CustomBox contentBox, CustomPanel panel, CustomLabel label,
                             CustomComboBox<String> comboBox, String... choices) {
        comboBox.setUI(new StringComboBoxUI(comboBox, f));
        for (String choice : choices) comboBox.addItem(choice);
        addSettingRow(contentBox, panel, label, comboBox);
    }

    // 添加一行文件夹设置项
    private void addDirectoryRow(CustomPanel panel, CustomLabel label, CustomTextField field, DirectoryChooser chooser) {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        DialogButton changeButton = new DialogButton(I18n.getText("change"), textColor);
        changeButton.addActionListener(e -> Platform.runLater(() -> {
            File dir = chooser.showDialog(null);
            if (dir != null && dir.exists()) field.setText(dir.getAbsolutePath());
        }));

        DialogButton openButton = new DialogButton(I18n.getText("dialogOpen"), textColor);
        openButton.addActionListener(e -> {
            File dir = new File(field.getText());
            if (dir.exists()) DesktopUtil.open(dir);
            else new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
        });

        addSettingRow(downloadAndCacheContentBox, panel, label, field, changeButton, openButton);
    }

    // 添加一行热键设置项
    private void addHotKeyRow(CustomPanel panel, CustomLabel label, CustomTextField field, KeyAdapter listener) {
        field.setEditable(false);
        field.addKeyListener(listener);
        addSettingRow(hotKeyContentBox, panel, label, field);
    }

    // 按标签页的显示顺序加载设置
    private void initSettings() {
        // 常规
        autoUpdateCheckBox.setSelected(f.autoUpdate);
        videoOnlyCheckBox.setSelected(f.videoOnly);
        langComboBox.setSelectedIndex(I18n.currLang);
        fontComboBox.setSelectedIndex(Fonts.currFont);
        closeOptionComboBox.setSelectedIndex(f.currCloseWindowOption);
        windowSizeComboBox.setSelectedIndex(f.windowSize);
        recommendTabEnabledCheckBox.setSelected(f.recommendTabEnabled);
        rankTabEnabledCheckBox.setSelected(f.rankTabEnabled);
        playlistTabEnabledCheckBox.setSelected(f.playlistTabEnabled);
        albumTabEnabledCheckBox.setSelected(f.albumTabEnabled);
        artistTabEnabledCheckBox.setSelected(f.artistTabEnabled);
        radioTabEnabledCheckBox.setSelected(f.radioTabEnabled);
        mvTabEnabledCheckBox.setSelected(f.mvTabEnabled);
        userTabEnabledCheckBox.setSelected(f.userTabEnabled);

        // 外观
        showTabTextCheckBox.setSelected(f.showTabText);
        showSheetButtonCheckBox.setSelected(f.showSheetButton);
        showFobButtonCheckBox.setSelected(f.showFobButton);
        lockDesktopLyricCheckBox.setSelected(f.desktopLyricLocked);
        lyricAlignmentComboBox.setSelectedIndex(LyricAlignment.lyricAlignmentIndex);
        specMaxHeightTextField.setText(String.valueOf(SpectrumConstants.maxBarHeight));
        gsFactorComboBox.setSelectedIndex(BackgroundEffectConstants.gsFactorIndex);
        darkerFactorComboBox.setSelectedIndex(BackgroundEffectConstants.darkerFactorIndex);

        // 下载与缓存
        autoDownloadLyricCheckBox.setSelected(f.autoDownloadLyric);
        verbatimTimelineCheckBox.setSelected(f.verbatimTimeline);
        audioDownQualityComboBox.setSelectedIndex(AudioQuality.downQuality);
        videoDownQualityComboBox.setSelectedIndex(VideoQuality.downQuality);
        musicDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MUSIC_PATH).getAbsolutePath());
        mvDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MV_PATH).getAbsolutePath());
        cachePathTextField.setText(new File(SimplePath.CACHE_PATH).getAbsolutePath());
        maxCacheSizeTextField.setText(String.valueOf(f.maxCacheSize));
        maxConcurrentTaskCountTextField.setText(String.valueOf(GlobalExecutors.downloadExecutor.getCorePoolSize()));

        // 播放与历史
        audioPlayQualityComboBox.setSelectedIndex(AudioQuality.playQuality);
        videoPlayQualityComboBox.setSelectedIndex(VideoQuality.playQuality);
        fobComboBox.setSelectedItem(f.forwardOrBackwardTime + I18n.getText("seconds"));
        balanceComboBox.setSelectedIndex((int) Math.round(f.currBalance) + 1);
        maxHistoryCountTextField.setText(String.valueOf(f.maxHistoryCount));
        maxSearchHistoryCountTextField.setText(String.valueOf(f.maxSearchHistoryCount));

        // 快捷键
        enableKeyCheckBox.setSelected(f.keyEnabled);
        playOrPauseKeys.addAll(f.playOrPauseKeys);
        playOrPauseTextField.setText(KeyUtil.toStr(f.playOrPauseKeys));
        playLastKeys.addAll(f.playLastKeys);
        playLastTextField.setText(KeyUtil.toStr(f.playLastKeys));
        playNextKeys.addAll(f.playNextKeys);
        playNextTextField.setText(KeyUtil.toStr(f.playNextKeys));
        backwardKeys.addAll(f.backwardKeys);
        backwardTextField.setText(KeyUtil.toStr(f.backwardKeys));
        forwardKeys.addAll(f.forwardKeys);
        forwardTextField.setText(KeyUtil.toStr(f.forwardKeys));
        videoFullScreenKeys.addAll(f.videoFullScreenKeys);
        videoFullScreenTextField.setText(KeyUtil.toStr(f.videoFullScreenKeys));
    }

    private boolean applySettings() {
        // 按显示顺序验证输入，全部通过后再更新设置
        String specMaxHeight = specMaxHeightTextField.getText();
        if (StringUtil.isEmpty(specMaxHeight)) return showInvalidSetting("invalidSpecMaxHeight");

        File musicDir = new File(musicDownPathTextField.getText());
        if (!musicDir.exists()) return showInvalidSetting("invalidMusicDown");
        File mvDir = new File(mvDownPathTextField.getText());
        if (!mvDir.exists()) return showInvalidSetting("invalidMvDown");
        File cacheDir = new File(cachePathTextField.getText());
        if (!cacheDir.exists()) return showInvalidSetting("invalidCache");

        String maxCacheSize = maxCacheSizeTextField.getText();
        if (StringUtil.isEmpty(maxCacheSize)) return showInvalidSetting("invalidMaxCacheSize");
        String maxConcurrentTaskCount = maxConcurrentTaskCountTextField.getText();
        if (StringUtil.isEmpty(maxConcurrentTaskCount)) return showInvalidSetting("invalidConcurrentTaskCount");

        String maxHistoryCount = maxHistoryCountTextField.getText();
        if (StringUtil.isEmpty(maxHistoryCount)) return showInvalidSetting("invalidHistoryCount");
        String maxSearchHistoryCount = maxSearchHistoryCountTextField.getText();
        if (StringUtil.isEmpty(maxSearchHistoryCount)) return showInvalidSetting("invalidSearchHistoryCount");

        // 常规
        f.autoUpdate = autoUpdateCheckBox.isSelected();
        f.videoOnly = videoOnlyCheckBox.isSelected();
        I18n.currLang = langComboBox.getSelectedIndex();
        Fonts.currFont = fontComboBox.getSelectedIndex();
        f.currCloseWindowOption = closeOptionComboBox.getSelectedIndex();
        f.windowSize = windowSizeComboBox.getSelectedIndex();
        f.windowWidth = WindowSize.DIMENSIONS[f.windowSize][0];
        f.windowHeight = WindowSize.DIMENSIONS[f.windowSize][1];
        f.x = f.y = 0x3f3f3f3f;
        if (f.windowState != WindowState.MAXIMIZED) f.setSize(f.windowWidth, f.windowHeight);

        f.recommendTabEnabled = recommendTabEnabledCheckBox.isSelected();
        f.rankTabEnabled = rankTabEnabledCheckBox.isSelected();
        f.playlistTabEnabled = playlistTabEnabledCheckBox.isSelected();
        f.albumTabEnabled = albumTabEnabledCheckBox.isSelected();
        f.artistTabEnabled = artistTabEnabledCheckBox.isSelected();
        f.radioTabEnabled = radioTabEnabledCheckBox.isSelected();
        f.mvTabEnabled = mvTabEnabledCheckBox.isSelected();
        f.userTabEnabled = userTabEnabledCheckBox.isSelected();
        f.updateTabVisibility();

        // 外观
        f.showTabText = showTabTextCheckBox.isSelected();
        f.updateTabSize();
        f.showSheetButton = showSheetButtonCheckBox.isSelected();
        f.updateSheetButtonShowing();
        f.showFobButton = showFobButtonCheckBox.isSelected();
        f.updateFobButtonShowing();
        f.desktopLyricLocked = lockDesktopLyricCheckBox.isSelected();
        f.updateDesktopLyricLocking();
        LyricAlignment.lyricAlignmentIndex = lyricAlignmentComboBox.getSelectedIndex();

        SpectrumConstants.maxBarHeight = Integer.parseInt(specMaxHeight);
        f.spectrumPanel.setPreferredSize(new Dimension(f.spectrumPanel.getPreferredSize().width, ScaleUtil.scale(SpectrumConstants.maxBarHeight)));
        f.spectrumPanel.setMinimumSize(new HDDimension(1, SpectrumConstants.maxBarHeight));
        f.spectrumPanel.revalidate();

        int gsFactorIndex = BackgroundEffectConstants.gsFactorIndex;
        int darkerFactorIndex = BackgroundEffectConstants.darkerFactorIndex;
        BackgroundEffectConstants.gsFactorIndex = gsFactorComboBox.getSelectedIndex();
        BackgroundEffectConstants.darkerFactorIndex = darkerFactorComboBox.getSelectedIndex();
        if (gsFactorIndex != BackgroundEffectConstants.gsFactorIndex || darkerFactorIndex != BackgroundEffectConstants.darkerFactorIndex)
            f.updateBgImg();

        // 下载与缓存
        f.autoDownloadLyric = autoDownloadLyricCheckBox.isSelected();
        f.verbatimTimeline = verbatimTimelineCheckBox.isSelected();
        AudioQuality.downQuality = audioDownQualityComboBox.getSelectedIndex();
        VideoQuality.downQuality = videoDownQualityComboBox.getSelectedIndex();
        SimplePath.DOWNLOAD_MUSIC_PATH = musicDir.getAbsolutePath() + File.separator;
        SimplePath.DOWNLOAD_MV_PATH = mvDir.getAbsolutePath() + File.separator;
        SimplePath.CACHE_PATH = cacheDir.getAbsolutePath() + File.separator;
        SimplePath.IMG_CACHE_PATH = SimplePath.CACHE_PATH + File.separator + "img" + File.separator;
        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);
        f.maxCacheSize = Long.parseLong(maxCacheSize);
        GlobalExecutors.downloadExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Integer.parseInt(maxConcurrentTaskCount));

        // 播放与历史
        AudioQuality.playQuality = audioPlayQualityComboBox.getSelectedIndex();
        VideoQuality.playQuality = videoPlayQualityComboBox.getSelectedIndex();
        f.forwardOrBackwardTime = Integer.parseInt(((String) fobComboBox.getSelectedItem()).replace(I18n.getText("seconds"), ""));
        f.currBalance = balanceComboBox.getSelectedIndex() - 1;
        f.player.setBalance(f.currBalance);

        f.maxHistoryCount = Integer.parseInt(maxHistoryCount);
        for (int i = f.maxHistoryCount, s = f.historyModel.size(); i < s; i++)
            f.historyModel.remove(f.maxHistoryCount);

        f.maxSearchHistoryCount = Integer.parseInt(maxSearchHistoryCount);
        CustomPanel[] searchHistoryPanels = {f.netMusicHistorySearchInnerPanel2, f.netPlaylistHistorySearchInnerPanel2,
                f.netAlbumHistorySearchInnerPanel2, f.netArtistHistorySearchInnerPanel2, f.netRadioHistorySearchInnerPanel2,
                f.netMvHistorySearchInnerPanel2, f.netUserHistorySearchInnerPanel2};
        for (CustomPanel panel : searchHistoryPanels) {
            for (int i = f.maxSearchHistoryCount, count = panel.getComponentCount(); i < count; i++)
                panel.remove(f.maxSearchHistoryCount);
        }

        // 快捷键
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

    private boolean showInvalidSetting(String textKey) {
        new TipDialog(f, I18n.getText(textKey), true).showDialog();
        return false;
    }
}
