package net.doge.ui.widget.dialog;

import com.sun.media.jfxmedia.locator.Locator;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.constant.core.ui.window.WindowSize;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.core.ui.UIStyle;
import net.doge.entity.service.NetMvInfo;
import net.doge.sdk.util.MusicServerUtil;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.menu.CustomPopupMenu;
import net.doge.ui.widget.menu.CustomRadioButtonMenuItem;
import net.doge.ui.widget.menu.ui.CustomMenuItemUI;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.TimeSliderUI;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.media.DurationUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Doge
 * @description 播放 MV 的对话框
 * @date 2020/12/15
 */
public class VideoDialog extends AbstractTitledDialog {
    private final String DEFAULT_TIME = "00:00";
    private final int TIME_BAR_MIN = 0;
    private final int TIME_BAR_MAX = 0x3f3f3f3f;
    private final int MAX_VOLUME = 100;
    private int mediaWidth;
    private int mediaHeight;

    private final String FORWARD_TIP = I18n.getText("forwTip");
    private final String BACKWARD_TIP = I18n.getText("backwTip");
    private final String PLAY_TIP = I18n.getText("playTip");
    private final String PAUSE_TIP = I18n.getText("pauseTip");
    private final String SOUND_TIP = I18n.getText("soundTip");
    private final String MUTE_TIP = I18n.getText("muteTip");
    private final String COLLECT_TIP = I18n.getText("collectTip");
    private final String COLLECTED_TIP = I18n.getText("collectedTip");
    private final String DOWNLOAD_TIP = I18n.getText("downloadTip");
    private final String RATE_TIP = I18n.getText("rateTip");
    private final String FOB_TIME_TIP = I18n.getText("fobTimeTip");
    private final String FULL_SCREEN_TIP = I18n.getText("fullScreenTip");

    private final String COLLECT_SUCCESS_MSG = I18n.getText("collectSuccessMsg");
    private final String CANCEL_COLLECTION_SUCCESS_MSG = I18n.getText("cancelCollectionSuccessMsg");
    private final String ERROR_MSG = I18n.getText("videoErrorMsg");

    // 播放图标
    private ImageIcon playIcon = LMIconManager.getIcon("control.play");
    // 暂停图标
    private ImageIcon pauseIcon = LMIconManager.getIcon("control.pause");
    // 声音图标
    private ImageIcon soundIcon = LMIconManager.getIcon("control.sound");
    // 静音图标
    private ImageIcon muteIcon = LMIconManager.getIcon("control.mute");
    // 未收藏图标
    private ImageIcon collectIcon = LMIconManager.getIcon("control.collect");
    // 已收藏图标
    private ImageIcon hasCollectedIcon = LMIconManager.getIcon("control.collected");
    // 下载图标
    private ImageIcon downloadIcon = LMIconManager.getIcon("control.download");
    // 倍速图标
    private ImageIcon rateIcon = LMIconManager.getIcon("control.rate");
    // 全屏图标
    private ImageIcon fullScreenIcon = LMIconManager.getIcon("control.fullScreen");
    // 快退图标
    private ImageIcon backwIcon = LMIconManager.getIcon("control.backw");
    // 快进图标
    private ImageIcon forwIcon = LMIconManager.getIcon("control.forw");
    // 快进快退时间图标
    private ImageIcon fobTimeIcon = LMIconManager.getIcon("control.fobTime");

    private boolean isMute;

    private JFXPanel jfxPanel = new JFXPanel();

    // 进度条面板
    private CustomPanel progressPanel = new CustomPanel();
    private CustomSlider timeBar = new CustomSlider();
    private CustomLabel currTimeLabel = new CustomLabel(DEFAULT_TIME);
    private CustomLabel durationLabel = new CustomLabel(DEFAULT_TIME);

    // 控制面板
    private CustomPanel controlPanel = new CustomPanel();
    private CustomPanel volumePanel = new CustomPanel();
    private CustomButton playOrPauseButton = new CustomButton(playIcon);
    public CustomButton backwardButton = new CustomButton(backwIcon);
    public CustomButton forwardButton = new CustomButton(forwIcon);
    public CustomButton muteButton = new CustomButton(soundIcon);
    private CustomSlider volumeSlider = new CustomSlider();
    private CustomButton collectButton = new CustomButton(collectIcon);
    private CustomButton downloadButton = new CustomButton(downloadIcon);
    private CustomButton rateButton = new CustomButton(rateIcon);
    private CustomButton fobTimeButton = new CustomButton(fobTimeIcon);
    private CustomButton fullScreenButton = new CustomButton(fullScreenIcon);
    private CustomPopupMenu fobTimePopupMenu;
    private List<CustomRadioButtonMenuItem> fobButtonGroup = new LinkedList<>();
    private final String SECONDS = I18n.getText("seconds");
    private CustomRadioButtonMenuItem[] fobTimeMenuItems = {
            new CustomRadioButtonMenuItem("5" + SECONDS),
            new CustomRadioButtonMenuItem("10" + SECONDS),
            new CustomRadioButtonMenuItem("15" + SECONDS),
            new CustomRadioButtonMenuItem("20" + SECONDS),
            new CustomRadioButtonMenuItem("25" + SECONDS),
            new CustomRadioButtonMenuItem("30" + SECONDS),
            new CustomRadioButtonMenuItem("45" + SECONDS),
            new CustomRadioButtonMenuItem("60" + SECONDS)
    };

    // 底部盒子
    private CustomBox bottomBox = new CustomBox(BoxLayout.Y_AXIS);

    private boolean resized;
    private int tryTime;
    private boolean isLocal;
    private boolean fullScreen;
    private String uri;

    private int x = 0x3f3f3f3f;
    private int y = 0x3f3f3f3f;

    private Media media;
    public MediaPlayer mp;
    private MediaView mediaView;

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private NetMvInfo mvInfo;

    public VideoDialog(NetMvInfo mvInfo, String dest, MainFrame f) {
        super(f, HtmlUtil.textToHtml(StringUtil.shorten(mvInfo.toSimpleString(), ScaleUtil.scale(60))));
        this.isLocal = dest != null;
        this.mvInfo = mvInfo;
        this.uri = isLocal ? dest : mvInfo.getUrl();

        mediaWidth = WindowSize.VIDEO_DIMENSIONS[f.windowSize][0];
        mediaHeight = WindowSize.VIDEO_DIMENSIONS[f.windowSize][1];

        initUI();
    }

    public void initUI() {
        // 取消桌面歌词置顶避免遮挡视线
        f.desktopLyricDialog.setAlwaysOnTop(false);

        // 保持在屏幕正中间
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (fullScreen) return;
                if (x == 0x3f3f3f3f && y == 0x3f3f3f3f) setLocationRelativeTo(null);
                else setLocation(x, y);
                currTimeLabel.revalidate();
                timeBar.setPreferredSize(new Dimension(getWidth() - 2 * pixels - currTimeLabel.getPreferredSize().width - durationLabel.getPreferredSize().width - ScaleUtil.scale(20 * 2), ScaleUtil.scale(30)));
                setSize(mediaWidth + 2 * pixels, mediaHeight + topPanel.getHeight() + bottomBox.getHeight() - ScaleUtil.scale(2) + 2 * pixels);
            }
        });

        setUndecorated(true);
        setResizable(false);
        setSize(mediaWidth + 2 * pixels, mediaHeight + 2 * pixels);
        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initCloseResponse();
        initView();
        initTimeBar();
        initControlPanel();
        playVideo();

        updateBlur();

        // Dialog 背景透明
        setBackground(Colors.TRANSPARENT);
        setContentPane(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    private void initRequestHeaders() {
        // b 站视频需要设置请求头
        if (isLocal || mvInfo.getSource() != NetResourceSource.BI) return;
        try {
            // 由于 Media 类不能重写，只能通过反射机制设置请求头
            Field field = Media.class.getDeclaredField("jfxLocator");
            field.setAccessible(true);
            Locator locator = (Locator) field.get(media);
            locator.setConnectionProperty("referer", "http://www.bilibili.com/");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void fitMediaView() {
        // 视频实际尺寸
        int width = media.getWidth(), height = media.getHeight();
        if (width == 0 || height == 0) return;
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int dw = fullScreen ? ss.width : mediaWidth, dh = fullScreen ? ss.height : mediaHeight;
        // 调整为合适的尺寸
        int fw, fh;
        // 优先适应宽度
        fw = dw;
        fh = height * fw / width;
        // 调整好后，如果高度还超出范围，再去适应高度
        if (fh > dh) {
            fh = dh;
            fw = width * fh / height;
        }
        mediaView.setFitWidth(fw);
        mediaView.setFitHeight(fh);
    }

    // 更新时间标签
    private void updateTimeLabel() {
        // 设置当前播放时间标签的最佳大小，避免导致进度条长度发生变化！
        String t = durationLabel.getText().replaceAll("[1-9]", "0");
        FontMetrics m = durationLabel.getFontMetrics(globalFont);
        Dimension d = new Dimension(m.stringWidth(t) + ScaleUtil.scale(40), durationLabel.getHeight());
        currTimeLabel.setPreferredSize(d);
        durationLabel.setPreferredSize(d);
    }

    // 初始化视频界面
    private void initView() {
        if (isLocal) {
            File mediaFile = new File(uri);
            media = new Media(mediaFile.toURI().toString());
        } else media = new Media(uri);
        initRequestHeaders();
        mp = new MediaPlayer(media);
        mediaView = new MediaView(mp);
        // 视频宽高控制
        media.heightProperty().addListener((observable, oldValue, newValue) -> fitMediaView());
        // 刷新缓冲长度
        mp.bufferProgressTimeProperty().addListener((observable, oldValue, newValue) -> timeBar.repaint());
        mp.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double currTimeSeconds = newValue.toSeconds(), durationSeconds = media.getDuration().toSeconds();
            // 未被操作时频繁更新时间条
            if (!timeBar.getValueIsAdjusting())
                timeBar.setValue((int) (currTimeSeconds / durationSeconds * TIME_BAR_MAX));
            if (resized) return;
            if (!f.videoTimeElapsedMode) currTimeLabel.setText(DurationUtil.format(durationSeconds));
            durationLabel.setText(DurationUtil.format(durationSeconds));
            updateTimeLabel();
            resized = true;
        });
        mp.setOnEndOfMedia(() -> {
            mp.seek(Duration.seconds(0));
            mp.pause();
            playOrPauseButton.setIcon(ImageUtil.dye(playIcon, UIStyleStorage.currUIStyle.getIconColor()));
            timeBar.setValue(0);
        });
        mp.setOnError(() -> {
            MediaException.Type type = mp.getError().getType();
            // 耳机取下导致的播放异常，重新播放
            if (type == MediaException.Type.PLAYBACK_HALTED) {
                initAgain();
            }
            // 歌曲 url 过期后重新加载 url 再播放
            else if (type == MediaException.Type.MEDIA_INACCESSIBLE
                    || type == MediaException.Type.MEDIA_UNAVAILABLE
                    || type == MediaException.Type.UNKNOWN) {
                if (!isLocal) {
                    mvInfo.setUrl("");
                    MusicServerUtil.fillMvInfo(mvInfo);
                    uri = mvInfo.getUrl();
                }
                initAgain();
            }
            // 尝试多次无效直接关闭窗口
            if (++tryTime >= 3) {
                closeButton.doClick();
                new TipDialog(f, ERROR_MSG, true).showDialog();
            }
        });

        Scene scene = new Scene(new BorderPane(mediaView));
        // 视频边缘黑幕
        scene.setFill(javafx.scene.paint.Color.BLACK);
        jfxPanel.setScene(scene);
        globalPanel.add(jfxPanel, BorderLayout.CENTER);
    }

    // 出错时重新初始化
    private void initAgain() {
        initView();
        Color timeBarColor = UIStyleStorage.currUIStyle.getTimeBarColor();
        timeBar.setUI(new TimeSliderUI(timeBar, timeBarColor, timeBarColor, f, mp, true));
        playVideo();
    }

    // 初始化关闭响应事件
    private void initCloseResponse() {
        closeButton.addActionListener(e -> {
            // 恢复桌面歌词置顶
            f.desktopLyricDialog.setAlwaysOnTop(f.desktopLyricOnTop);
//            Future<?> future = GlobalExecutors.requestExecutor.submit(() -> mp.dispose());
//            try {
//                future.get(100, TimeUnit.MILLISECONDS);
//            } catch (Exception ex) {
//
//            } finally {
//                if (!future.isDone()) future.cancel(true);
//            }
            // 先暂停播放，避免 runLater 延迟调用
            mp.pause();
            Platform.runLater(() -> mp.dispose());
        });
    }

    // 进度条
    private void initTimeBar() {
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color timeBarColor = style.getTimeBarColor();

        currTimeLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);

        currTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        durationLabel.setHorizontalAlignment(SwingConstants.LEFT);

        int bw = 10;
        currTimeLabel.setBorder(new HDEmptyBorder(0, 0, 0, bw));
        durationLabel.setBorder(new HDEmptyBorder(0, bw, 0, 0));
        updateTimeLabel();

        // 当前播放时间
        currTimeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        currTimeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                f.videoTimeElapsedMode = !f.videoTimeElapsedMode;
                // 更新当前播放时间
                int val = timeBar.getValue();
                double t = (double) (f.videoTimeElapsedMode ? val : TIME_BAR_MAX - val) / TIME_BAR_MAX * media.getDuration().toSeconds();
                currTimeLabel.setText(DurationUtil.format(t));
            }
        });

        timeBar.setMinimum(TIME_BAR_MIN);
        timeBar.setMaximum(TIME_BAR_MAX);
        timeBar.setValue(TIME_BAR_MIN);
        timeBar.setUI(new TimeSliderUI(timeBar, timeBarColor, timeBarColor, f, mp, true));      // 自定义进度条 UI
        // 拖动播放时间条
        timeBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                double t = (double) timeBar.getValue() / TIME_BAR_MAX * media.getDuration().toSeconds();
                mp.seek(Duration.seconds(t));
            }
        });
        // 改变时间条的值，当前时间标签的值随之改变
        timeBar.addChangeListener(e -> {
            int val = timeBar.getValue();
            double t = (double) (f.videoTimeElapsedMode ? val : TIME_BAR_MAX - val) / TIME_BAR_MAX * media.getDuration().toSeconds();
            currTimeLabel.setText(DurationUtil.format(t));
        });
        // 设置进度条最佳大小
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
        progressPanel.add(currTimeLabel);
        progressPanel.add(timeBar);
        progressPanel.add(durationLabel);
        bottomBox.add(progressPanel);
    }

    // 控制面板
    private void initControlPanel() {
        Color iconColor = UIStyleStorage.currUIStyle.getIconColor();
        Color sliderColor = UIStyleStorage.currUIStyle.getSliderColor();

        playOrPauseButton.setToolTipText(PLAY_TIP);
        playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, iconColor));
        playOrPauseButton.addActionListener(e -> playOrPause());

        // 快进
        forwardButton.setToolTipText(FORWARD_TIP);
        forwardButton.updateIconStyle();
        forwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().add(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 快退
        backwardButton.setToolTipText(BACKWARD_TIP);
        backwardButton.updateIconStyle();
        backwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().subtract(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 静音
        muteButton.setToolTipText(SOUND_TIP);
        muteButton.setIcon(ImageUtil.dye(soundIcon, iconColor));
        muteButton.addActionListener(e -> {
            if (isMute = !isMute) {
                muteButton.setToolTipText(MUTE_TIP);
                muteButton.setIcon(ImageUtil.dye(muteIcon, iconColor));
            } else {
                muteButton.setToolTipText(SOUND_TIP);
                muteButton.setIcon(ImageUtil.dye(soundIcon, iconColor));
            }
            mp.setMute(isMute);
        });
        // 音量调节滑动条
        volumeSlider.setUI(new TimeSliderUI(volumeSlider, sliderColor, sliderColor, f, mp, false));
        volumeSlider.setPreferredSize(new HDDimension(100, 20));
        volumeSlider.setMaximum(MAX_VOLUME);
        volumeSlider.addChangeListener(e -> {
            mp.setVolume((float) volumeSlider.getValue() / MAX_VOLUME);
            if (!isMute) return;
            muteButton.setToolTipText(SOUND_TIP);
            muteButton.setIcon(ImageUtil.dye(soundIcon, iconColor));
            mp.setMute(isMute = false);
        });
        // 收藏
        collectButton.setToolTipText(COLLECT_TIP);
        collectButton.setIcon(ImageUtil.dye(f.hasBeenCollected(mvInfo) ? hasCollectedIcon : collectIcon, iconColor));
        collectButton.addActionListener(e -> {
            if (!f.hasBeenCollected(mvInfo)) {
                // 加载 MV 基本信息
                mvInfo.setInvokeLater(() -> f.updateRenderer(f.collectionList));
                GlobalExecutors.requestExecutor.execute(() -> MusicServerUtil.fillMvDetail(mvInfo));
                f.mvCollectionModel.add(0, mvInfo);
                collectButton.setToolTipText(COLLECTED_TIP);
                collectButton.setIcon(ImageUtil.dye(hasCollectedIcon, iconColor));
                new TipDialog(f, COLLECT_SUCCESS_MSG, true).showDialog();
            } else {
                f.mvCollectionModel.removeElement(mvInfo);
                collectButton.setToolTipText(COLLECT_TIP);
                collectButton.setIcon(ImageUtil.dye(collectIcon, iconColor));
                new TipDialog(f, CANCEL_COLLECTION_SUCCESS_MSG, true).showDialog();
            }
        });
        // 下载
        downloadButton.setEnabled(!isLocal || !mvInfo.isMp4());
        downloadButton.setToolTipText(DOWNLOAD_TIP);
        downloadButton.setIcon(ImageUtil.dye(downloadIcon, iconColor));
        downloadButton.setDisabledIcon(ImageUtil.dye(downloadIcon, ColorUtil.darker(iconColor)));
        downloadButton.addActionListener(e -> downloadMv());
        rateButton.setForeground(iconColor);
        rateButton.setToolTipText(RATE_TIP);
        rateButton.updateIconStyle();
        rateButton.addActionListener(e -> {
            RateDialog rd = new RateDialog(f, this, rateButton);
            rd.showDialog();
        });
        // 快进/快退时间
        fobTimePopupMenu = new CustomPopupMenu(f);
        CustomRadioButtonMenuItem selectedFobMenuItem = null;
        for (CustomRadioButtonMenuItem menuItem : fobTimeMenuItems) {
            Color textColor = UIStyleStorage.currUIStyle.getTextColor();
            menuItem.setForeground(textColor);
            menuItem.setUI(new CustomMenuItemUI());
            int time = Integer.parseInt(menuItem.getText().replace(SECONDS, ""));
            if (time == f.videoForwardOrBackwardTime) selectedFobMenuItem = menuItem;
            menuItem.addActionListener(e -> {
                f.videoForwardOrBackwardTime = time;
                updateMenuItemStatus(menuItem);
            });
            fobButtonGroup.add(menuItem);
            fobTimePopupMenu.add(menuItem);
        }
        fobTimeButton.setForeground(iconColor);
        fobTimeButton.setToolTipText(FOB_TIME_TIP);
        fobTimeButton.updateIconStyle();
        fobTimeButton.setComponentPopupMenu(fobTimePopupMenu);
        fobTimeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                fobTimePopupMenu.show(fobTimeButton, e.getX(), e.getY());
            }
        });
        fullScreenButton.setForeground(iconColor);
        fullScreenButton.setToolTipText(FULL_SCREEN_TIP);
        fullScreenButton.updateIconStyle();
        fullScreenButton.addActionListener(e -> toFullScreen());
        jfxPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 右键或者左键双击切换全屏
                if (fullScreen && e.getButton() == MouseEvent.BUTTON3
                        || e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    switchWindow();
                }
            }
        });

        updateMenuItemStatus(selectedFobMenuItem);

        FlowLayout fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(3));
        controlPanel.setLayout(fl);
        controlPanel.add(CustomBox.createHorizontalGlue());
        controlPanel.add(collectButton);
        controlPanel.add(downloadButton);
        controlPanel.add(backwardButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(forwardButton);

        fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(0));
        volumePanel.setLayout(fl);
        volumePanel.add(muteButton);
        volumePanel.add(volumeSlider);
        controlPanel.add(volumePanel);

        controlPanel.add(rateButton);
        controlPanel.add(fobTimeButton);
        controlPanel.add(fullScreenButton);
        controlPanel.add(CustomBox.createHorizontalGlue());
        bottomBox.add(controlPanel);
        globalPanel.add(bottomBox, BorderLayout.SOUTH);
    }

    public void switchWindow() {
        if (fullScreen) restoreWindow();
        else toFullScreen();
    }

    private void toFullScreen() {
        fullScreen = true;
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        topPanel.setVisible(false);
        bottomBox.setVisible(false);
        globalPanel.eraseBorder();
        setBounds(0, 0, ss.width, ss.height);
        fitMediaView();
    }

    private void restoreWindow() {
        setSize(mediaWidth + 2 * pixels, mediaHeight + topPanel.getHeight() + bottomBox.getHeight() - ScaleUtil.scale(2) + 2 * pixels);
        fullScreen = false;
        topPanel.setVisible(true);
        bottomBox.setVisible(true);
        globalPanel.initBorder();
        fitMediaView();
    }

    private void playVideo() {
        volumeSlider.setValue(f.volumeSlider.getValue());
        mp.setRate(f.currVideoRate);
        mp.play();
        playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, UIStyleStorage.currUIStyle.getIconColor()));
        playOrPauseButton.setToolTipText(PAUSE_TIP);
    }

    public void playOrPause() {
        switch (mp.getStatus()) {
            case PLAYING:
                mp.pause();
                playOrPauseButton.setIcon(ImageUtil.dye(playIcon, UIStyleStorage.currUIStyle.getIconColor()));
                playOrPauseButton.setToolTipText(PLAY_TIP);
                break;
            case PAUSED:
                mp.play();
                playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, UIStyleStorage.currUIStyle.getIconColor()));
                playOrPauseButton.setToolTipText(PAUSE_TIP);
                break;
            case READY:
                playVideo();
                break;
        }
    }

    // 下载 MV
    private void downloadMv() {
        f.multiDownloadMv(Collections.singletonList(mvInfo), true);
    }

    // 更新单选菜单项状态
    private void updateMenuItemStatus(CustomRadioButtonMenuItem menuItem) {
        fobButtonGroup.forEach(mi -> mi.setSelected(mi == menuItem));
    }
}
