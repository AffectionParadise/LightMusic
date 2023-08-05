package net.doge.ui.component.dialog;

import com.sun.media.jfxmedia.locator.Locator;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.ui.Colors;
import net.doge.constant.ui.Fonts;
import net.doge.constant.window.WindowSize;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.util.MusicServerUtil;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.CustomButton;
import net.doge.ui.component.button.listener.ButtonMouseListener;
import net.doge.ui.component.dialog.factory.AbstractTitledDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.menu.CustomPopupMenu;
import net.doge.ui.component.menu.CustomRadioButtonMenuItem;
import net.doge.ui.component.menu.ui.RadioButtonMenuItemUI;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.slider.CustomSlider;
import net.doge.ui.component.slider.ui.SliderUI;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import net.doge.util.system.LMIconManager;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author Doge
 * @Description 播放 MV 的对话框
 * @Date 2020/12/15
 */
public class VideoDialog extends AbstractTitledDialog {
    private final String DEFAULT_TIME = "00:00";
    private final int TIME_BAR_MIN = 0;
    private final int TIME_BAR_MAX = 0x3f3f3f3f;
    private final int MAX_VOLUME = 100;
    private int mediaWidth;
    private int mediaHeight;

    private final String FORWARD_TIP = "快进";
    private final String BACKWARD_TIP = "快退";
    private final String PLAY_TIP = "播放";
    private final String PAUSE_TIP = "暂停";
    private final String SOUND_TIP = "声音开启";
    private final String MUTE_TIP = "静音";
    private final String COLLECT_TIP = "收藏";
    private final String CANCEL_COLLECTION_TIP = "取消收藏";
    private final String DOWNLOAD_TIP = "下载";
    private final String RATE_TIP = "倍速";
    private final String FOB_TIME_TIP = "快进/快退时间";
    private final String FULL_SCREEN_TIP = "全屏";

    // 收藏成功提示
    private final String COLLECT_SUCCESS_MSG = "收藏成功";
    // 取消收藏成功提示
    private final String CANCEL_COLLECTION_SUCCESS_MSG = "取消收藏成功";
    // 播放异常提示
    private final String ERROR_MSG = "播放视频时发生异常";

    // 选定点图标
    private ImageIcon dotIcon = LMIconManager.getIcon("menu.dot");
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
    private ButtonGroup fobTimeMenuItemsButtonGroup = new ButtonGroup();
    private CustomRadioButtonMenuItem[] fobTimeMenuItems = {
            new CustomRadioButtonMenuItem("5秒"),
            new CustomRadioButtonMenuItem("10秒"),
            new CustomRadioButtonMenuItem("15秒"),
            new CustomRadioButtonMenuItem("20秒"),
            new CustomRadioButtonMenuItem("25秒"),
            new CustomRadioButtonMenuItem("30秒"),
            new CustomRadioButtonMenuItem("45秒"),
            new CustomRadioButtonMenuItem("60秒")
    };

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.Y_AXIS);

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
        super(f, StringUtil.textToHtml(StringUtil.shorten(mvInfo.toSimpleString(), 60)));
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
                currTimeLabel.setVisible(false);
                currTimeLabel.setVisible(true);
                timeBar.setPreferredSize(new Dimension(getWidth() - 2 * pixels - currTimeLabel.getPreferredSize().width - durationLabel.getPreferredSize().width - 20 * 2, 20));
                setSize(mediaWidth + 2 * pixels, mediaHeight + topPanel.getHeight() + bottomBox.getHeight() - 2 + 2 * pixels);
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
        setBackground(Colors.TRANSLUCENT);
        setContentPane(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    private void initRequestHeaders() {
        // b 站视频需要设置请求头
        if (isLocal || mvInfo.getSource() != NetMusicSource.BI) return;
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
            durationLabel.setText(TimeUtil.format(durationSeconds));
            // 设置当前播放时间标签的最佳大小，避免导致进度条长度发生变化！
            String t = durationLabel.getText().replaceAll("[1-9]", "0");
            FontMetrics m = durationLabel.getFontMetrics(globalFont);
            currTimeLabel.setPreferredSize(new Dimension(m.stringWidth(t) + 2, durationLabel.getHeight()));
            resized = true;
        });
        mp.setOnEndOfMedia(() -> {
            mp.seek(Duration.seconds(0));
            mp.pause();
            playOrPauseButton.setIcon(ImageUtil.dye(playIcon, f.currUIStyle.getIconColor()));
            timeBar.setValue(0);
            currTimeLabel.setText(DEFAULT_TIME);
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
                if (!isLocal) mvInfo.setUrl(uri = MusicServerUtil.fetchMvUrl(mvInfo));
                initAgain();
            }
            // 尝试多次无效直接关闭窗口
            if (++tryTime >= 3) {
                closeButton.doClick();
                new TipDialog(f, ERROR_MSG).showDialog();
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
        Color timeBarColor = f.currUIStyle.getTimeBarColor();
        timeBar.setUI(new SliderUI(timeBar, timeBarColor, timeBarColor, f, mp, true));
        playVideo();
    }

    // 初始化关闭响应事件
    private void initCloseResponse() {
        closeButton.addActionListener(e -> {
            // 恢复桌面歌词置顶
            f.desktopLyricDialog.setAlwaysOnTop(f.desktopLyricOnTop);
            Future<?> future = GlobalExecutors.requestExecutor.submit(() -> mp.dispose());
            try {
                future.get(100, TimeUnit.MILLISECONDS);
            } catch (Exception ex) {

            } finally {
                if (!future.isDone()) future.cancel(true);
            }
        });
    }

    // 进度条
    private void initTimeBar() {
        Color textColor = f.currUIStyle.getTextColor();
        Color timeBarColor = f.currUIStyle.getTimeBarColor();

        currTimeLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);
        timeBar.setMinimum(TIME_BAR_MIN);
        timeBar.setMaximum(TIME_BAR_MAX);
        timeBar.setValue(TIME_BAR_MIN);
        timeBar.setUI(new SliderUI(timeBar, timeBarColor, timeBarColor, f, mp, true));      // 自定义进度条 UI
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
            double t = (double) timeBar.getValue() / TIME_BAR_MAX * media.getDuration().toSeconds();
            currTimeLabel.setText(TimeUtil.format(t));
        });
        // 设置进度条最佳大小
        progressPanel.add(currTimeLabel);
        progressPanel.add(timeBar);
        progressPanel.add(durationLabel);
        bottomBox.add(progressPanel);
    }

    // 控制面板
    private void initControlPanel() {
        Color iconColor = f.currUIStyle.getIconColor();
        Color sliderColor = f.currUIStyle.getSliderColor();

        playOrPauseButton.setToolTipText(PLAY_TIP);
        playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, iconColor));
        playOrPauseButton.setPreferredSize(new Dimension(pauseIcon.getIconWidth() + 10, pauseIcon.getIconHeight() + 10));
        playOrPauseButton.addMouseListener(new ButtonMouseListener(playOrPauseButton, f));
        playOrPauseButton.addActionListener(e -> playOrPause());

        // 快进
        forwardButton.setToolTipText(FORWARD_TIP);
        forwardButton.setIcon(ImageUtil.dye((ImageIcon) forwardButton.getIcon(), iconColor));
        forwardButton.addMouseListener(new ButtonMouseListener(forwardButton, f));
        forwardButton.setPreferredSize(new Dimension(forwIcon.getIconWidth() + 10, forwIcon.getIconHeight() + 10));
        forwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().add(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 快退
        backwardButton.setToolTipText(BACKWARD_TIP);
        backwardButton.setIcon(ImageUtil.dye((ImageIcon) backwardButton.getIcon(), iconColor));
        backwardButton.addMouseListener(new ButtonMouseListener(backwardButton, f));
        backwardButton.setPreferredSize(new Dimension(backwIcon.getIconWidth() + 10, backwIcon.getIconHeight() + 10));
        backwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().subtract(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 静音
        muteButton.setToolTipText(SOUND_TIP);
        muteButton.setIcon(ImageUtil.dye(soundIcon, iconColor));
        muteButton.addMouseListener(new ButtonMouseListener(muteButton, f));
        muteButton.setPreferredSize(new Dimension(muteIcon.getIconWidth() + 10, muteIcon.getIconHeight() + 10));
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
        volumeSlider.setUI(new SliderUI(volumeSlider, sliderColor, sliderColor, f, mp, false));
        volumeSlider.setPreferredSize(new Dimension(100, 20));
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
        collectButton.addMouseListener(new ButtonMouseListener(collectButton, f));
        collectButton.setPreferredSize(new Dimension(collectIcon.getIconWidth() + 10, collectIcon.getIconHeight() + 10));
        collectButton.addActionListener(e -> {
            if (!f.hasBeenCollected(mvInfo)) {
                // 加载 MV 基本信息
                mvInfo.setInvokeLater(() -> f.updateRenderer(f.collectionList));
                GlobalExecutors.requestExecutor.execute(() -> MusicServerUtil.fillMvDetail(mvInfo));
                f.mvCollectionModel.add(0, mvInfo);
                collectButton.setToolTipText(CANCEL_COLLECTION_TIP);
                collectButton.setIcon(ImageUtil.dye(hasCollectedIcon, iconColor));
                new TipDialog(f, COLLECT_SUCCESS_MSG).showDialog();
            } else {
                f.mvCollectionModel.removeElement(mvInfo);
                collectButton.setToolTipText(COLLECT_TIP);
                collectButton.setIcon(ImageUtil.dye(collectIcon, iconColor));
                new TipDialog(f, CANCEL_COLLECTION_SUCCESS_MSG).showDialog();
            }
        });
        // 下载
        downloadButton.setEnabled(!isLocal || !mvInfo.isMp4());
        downloadButton.setToolTipText(DOWNLOAD_TIP);
        downloadButton.setIcon(ImageUtil.dye(downloadIcon, iconColor));
        downloadButton.setDisabledIcon(ImageUtil.dye(downloadIcon, ColorUtil.darker(iconColor)));
        downloadButton.addMouseListener(new ButtonMouseListener(downloadButton, f));
        downloadButton.setPreferredSize(new Dimension(downloadIcon.getIconWidth() + 10, downloadIcon.getIconHeight() + 10));
        downloadButton.addActionListener(e -> {
            downloadMv();
        });
        rateButton.setForeground(iconColor);
        rateButton.setToolTipText(RATE_TIP);
        rateButton.setIcon(ImageUtil.dye((ImageIcon) rateButton.getIcon(), iconColor));
        rateButton.addMouseListener(new ButtonMouseListener(rateButton, f));
        rateButton.setPreferredSize(new Dimension(rateIcon.getIconWidth() + 10, rateIcon.getIconHeight() + 10));
        rateButton.addActionListener(e -> {
            RateDialog rd = new RateDialog(f, this, rateButton);
            rd.showDialog();
        });
        // 快进/快退时间
        fobTimePopupMenu = new CustomPopupMenu(f);
        for (CustomRadioButtonMenuItem menuItem : fobTimeMenuItems) {
            Color textColor = f.currUIStyle.getTextColor();
            menuItem.setForeground(textColor);
            menuItem.setUI(new RadioButtonMenuItemUI(textColor));
            int time = Integer.parseInt(menuItem.getText().replace("秒", ""));
            menuItem.setSelected(time == f.videoForwardOrBackwardTime);
            menuItem.addActionListener(e -> {
                f.videoForwardOrBackwardTime = time;
//                fobTimeButton.setText(menuItem.getText().replace("秒", "s"));
                updateRadioButtonMenuItemIcon();
            });
            fobTimeMenuItemsButtonGroup.add(menuItem);
            fobTimePopupMenu.add(menuItem);
        }
        fobTimeButton.setForeground(iconColor);
        fobTimeButton.setToolTipText(FOB_TIME_TIP);
        fobTimeButton.setIcon(ImageUtil.dye((ImageIcon) fobTimeButton.getIcon(), iconColor));
        fobTimeButton.addMouseListener(new ButtonMouseListener(fobTimeButton, f));
        fobTimeButton.setPreferredSize(new Dimension(fobTimeIcon.getIconWidth() + 10, fobTimeIcon.getIconHeight() + 10));
        fobTimeButton.setComponentPopupMenu(fobTimePopupMenu);
        fobTimeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                fobTimePopupMenu.show(fobTimeButton, e.getX(), e.getY());
            }
        });
        fullScreenButton.setForeground(iconColor);
        fullScreenButton.setToolTipText(FULL_SCREEN_TIP);
        fullScreenButton.setIcon(ImageUtil.dye((ImageIcon) fullScreenButton.getIcon(), iconColor));
        fullScreenButton.addMouseListener(new ButtonMouseListener(fullScreenButton, f));
        fullScreenButton.setPreferredSize(new Dimension(fullScreenIcon.getIconWidth() + 10, fullScreenIcon.getIconHeight() + 10));
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

        updateRadioButtonMenuItemIcon();

        FlowLayout fl = new FlowLayout();
        fl.setHgap(3);
        controlPanel.setLayout(fl);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(collectButton);
        controlPanel.add(downloadButton);
        controlPanel.add(backwardButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(forwardButton);

        fl = new FlowLayout();
        fl.setHgap(0);
        volumePanel.setLayout(fl);
        volumePanel.add(muteButton);
        volumePanel.add(volumeSlider);
        controlPanel.add(volumePanel);

        controlPanel.add(rateButton);
        controlPanel.add(fobTimeButton);
        controlPanel.add(fullScreenButton);
        controlPanel.add(Box.createHorizontalGlue());
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
        setSize(mediaWidth + 2 * pixels, mediaHeight + topPanel.getHeight() + bottomBox.getHeight() - 2 + 2 * pixels);
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
        playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, f.currUIStyle.getIconColor()));
        playOrPauseButton.setToolTipText(PAUSE_TIP);
    }

    public void playOrPause() {
        switch (mp.getStatus()) {
            case PLAYING:
                mp.pause();
                playOrPauseButton.setIcon(ImageUtil.dye(playIcon, f.currUIStyle.getIconColor()));
                playOrPauseButton.setToolTipText(PLAY_TIP);
                break;
            case PAUSED:
                mp.play();
                playOrPauseButton.setIcon(ImageUtil.dye(pauseIcon, f.currUIStyle.getIconColor()));
                playOrPauseButton.setToolTipText(PAUSE_TIP);
                break;
            case READY:
                playVideo();
                break;
        }
    }

    // 下载 MV
    private void downloadMv() {
        f.multiDownloadMv(Collections.singletonList(mvInfo));
    }

    // 改变所有单选菜单项图标
    private void updateRadioButtonMenuItemIcon() {
        Component[] components = fobTimePopupMenu.getComponents();
        for (Component c : components) {
            CustomRadioButtonMenuItem mi = (CustomRadioButtonMenuItem) c;
            if (mi.isSelected()) mi.setIcon(ImageUtil.dye(dotIcon, f.currUIStyle.getIconColor()));
            else mi.setIcon(null);
        }
    }
}
