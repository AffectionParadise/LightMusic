package net.doge.ui.components.dialog;

import com.sun.media.jfxmedia.locator.Locator;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.NetMvInfo;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.componentui.RadioButtonMenuItemUI;
import net.doge.ui.componentui.SliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author yzx
 * @Description 播放 MV 的对话框
 * @Date 2020/12/15
 */
public class VideoDialog extends JDialog {
    private final String DEFAULT_TIME = "00:00";
    private final int TIME_BAR_MIN = 0;
    private final int TIME_BAR_MAX = 0x3f3f3f3f;
    private final int MAX_VOLUME = 100;
    private int mediaWidth;
    private int mediaHeight;

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

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
    private ImageIcon dotIcon = new ImageIcon(SimplePath.MENU_ICON_PATH + "dot.png");
    // 播放图标
    private ImageIcon playIcon = new ImageIcon(SimplePath.ICON_PATH + "play.png");
    // 暂停图标
    private ImageIcon pauseIcon = new ImageIcon(SimplePath.ICON_PATH + "pause.png");
    // 声音图标
    private ImageIcon soundIcon = new ImageIcon(SimplePath.ICON_PATH + "sound.png");
    // 静音图标
    private ImageIcon muteIcon = new ImageIcon(SimplePath.ICON_PATH + "mute.png");
    // 未收藏图标
    private ImageIcon collectIcon = new ImageIcon(SimplePath.ICON_PATH + "collect.png");
    // 已收藏图标
    private ImageIcon hasCollectedIcon = new ImageIcon(SimplePath.ICON_PATH + "hasCollected.png");
    // 下载图标
    private ImageIcon downloadIcon = new ImageIcon(SimplePath.ICON_PATH + "download.png");
    // 倍速图标
    private ImageIcon rateIcon = new ImageIcon(SimplePath.ICON_PATH + "videoRate.png");
    // 全屏图标
    private ImageIcon fullScreenIcon = new ImageIcon(SimplePath.ICON_PATH + "fullScreen.png");
    // 快退图标
    private ImageIcon backwIcon = new ImageIcon(SimplePath.ICON_PATH + "backw.png");
    // 快进图标
    private ImageIcon forwIcon = new ImageIcon(SimplePath.ICON_PATH + "forw.png");
    // 快进快退时间图标
    private ImageIcon fobTimeIcon = new ImageIcon(SimplePath.ICON_PATH + "fobTime.png");

    // 标题
    private String title = "";
    private boolean isMute;

    private VideoDialogPanel globalPanel = new VideoDialogPanel();
    private JFXPanel jfxPanel = new JFXPanel();

    // 标题面板
    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton();

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

    private PlayerFrame f;
    private NetMvInfo netMvInfo;
    private UIStyle style;

    // true 表示是模态的
//    public VideoDialog(NetMvInfo netMvInfo, boolean isLocal, PlayerFrame f) {
//        super(f, true);
//        this.f = f;
//        this.isLocal = isLocal;
//        this.netMvInfo = netMvInfo;
//        this.uri = isLocal ? SimplePath.DOWNLOAD_MV_PATH + netMvInfo.toSimpleFileName() : netMvInfo.getUrl();
//        title = netMvInfo.toSimpleString();
//        style = f.getCurrUIStyle();
//
//        initUI();
//    }

    public VideoDialog(NetMvInfo netMvInfo, String dest, PlayerFrame f) {
        super(f, true);
        this.f = f;
        this.isLocal = dest != null;
        this.netMvInfo = netMvInfo;
        this.uri = isLocal ? dest : netMvInfo.getUrl();
        title = netMvInfo.toSimpleString();
        style = f.getCurrUIStyle();

        mediaWidth = WindowSize.videoDimensions[f.windowSize][0];
        mediaHeight = WindowSize.videoDimensions[f.windowSize][1];

        initUI();
    }

    public void initUI() {
        // 取消桌面歌词置顶避免遮挡视线
        f.getDesktopLyricDialog().setAlwaysOnTop(false);

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
                setLocation(x = p.x + e.getX() - origin.x, y = p.y + e.getY() - origin.y);
            }
        });
        // 保持在屏幕正中间
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (fullScreen) return;
                if (x == 0x3f3f3f3f && y == 0x3f3f3f3f) setLocationRelativeTo(null);
                else setLocation(x, y);
                currTimeLabel.setVisible(false);
                currTimeLabel.setVisible(true);
                timeBar.setPreferredSize(new Dimension(getWidth() - 2 * pixels - currTimeLabel.getPreferredSize().width - durationLabel.getPreferredSize().width - 20 * 2, 12));
                setSize(mediaWidth + 2 * pixels, mediaHeight + topPanel.getHeight() + bottomBox.getHeight() - 2 + 2 * pixels);
            }
        });

        setUndecorated(true);
        setResizable(false);
        setSize(mediaWidth + 2 * pixels, mediaHeight + 2 * pixels);
        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();
        initTimeBar();
        initControlPanel();
        playVideo();

        updateBlur();

        // Dialog 背景透明
        setBackground(Colors.TRANSLUCENT);
        add(globalPanel);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (bufferedImage == f.getDefaultAlbumImage()) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG)
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    public void showDialog() {
        setVisible(true);
    }

    private void initRequestHeaders() {
        // b 站视频需要设置请求头
        if (isLocal || netMvInfo.getSource() != NetMusicSource.BI) return;
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

    void fitMediaView() {
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
    void initView() {
        if (isLocal) {
            File mediaFile = new File(uri);
            media = new Media(mediaFile.toURI().toString());
        } else media = new Media(uri);
        initRequestHeaders();
        mp = new MediaPlayer(media);
        mediaView = new MediaView(mp);
        // 视频宽高控制
//            media.widthProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                    int width = media.getWidth(), height = media.getHeight();
//                    if (width > height) mediaView.setFitWidth(mediaWidth);
//                    else mediaView.setFitWidth(mediaWidth_2);
//                }
//            });
        media.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            fitMediaView();
        });
        // 刷新缓冲长度
        mp.bufferProgressTimeProperty().addListener(l -> timeBar.repaint());
        mp.currentTimeProperty().addListener(l -> {
            // 未被操作时频繁更新时间条
            if (!timeBar.getValueIsAdjusting())
                timeBar.setValue((int) (mp.getCurrentTime().toSeconds() / media.getDuration().toSeconds() * TIME_BAR_MAX));
            currTimeLabel.setText(TimeUtils.format(mp.getCurrentTime().toSeconds()));
            if (resized) return;
            durationLabel.setText(TimeUtils.format(media.getDuration().toSeconds()));
            // 设置当前播放时间标签的最佳大小，避免导致进度条长度发生变化！
            String t = durationLabel.getText().replaceAll("[1-9]", "0");
            FontMetrics m = durationLabel.getFontMetrics(globalFont);
            currTimeLabel.setPreferredSize(new Dimension(m.stringWidth(t) + 2, durationLabel.getHeight()));
            resized = true;
        });
        mp.setOnEndOfMedia(() -> {
            mp.seek(Duration.seconds(0));
            mp.pause();
            playOrPauseButton.setIcon(ImageUtils.dye(playIcon, style.getButtonColor()));
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
            else if (type == MediaException.Type.MEDIA_INACCESSIBLE || type == MediaException.Type.UNKNOWN) {
                if (!isLocal) netMvInfo.setUrl(uri = MusicServerUtils.fetchMvUrl(netMvInfo));
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
    void initAgain() {
        initView();
        timeBar.setUI(new SliderUI(timeBar, style.getTimeBarColor(), style.getTimeBarColor(), f, mp, true));
        playVideo();
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setText(StringUtils.textToHtml(title));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setPreferredSize(new Dimension(600, 30));
        closeButton.setIcon(ImageUtils.dye(f.closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + 2, f.closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            Future<?> future = GlobalExecutors.requestExecutor.submit(() -> {
                dispose();
                // 恢复桌面歌词置顶
                f.getDesktopLyricDialog().setAlwaysOnTop(f.desktopLyricOnTop);
                mp.dispose();
            });
            try {
                future.get(10, TimeUnit.MILLISECONDS);
                if (!future.isDone()) future.cancel(true);
            } catch (Exception ex) {

            }
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(30, 30));
        windowCtrlPanel.add(closeButton);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    // 进度条
    void initTimeBar() {
        currTimeLabel.setForeground(style.getTimeBarColor());
        durationLabel.setForeground(style.getTimeBarColor());
        timeBar.setMinimum(TIME_BAR_MIN);
        timeBar.setMaximum(TIME_BAR_MAX);
        timeBar.setValue(TIME_BAR_MIN);
        timeBar.setUI(new SliderUI(timeBar, style.getTimeBarColor(), style.getTimeBarColor(), f, mp, true));      // 自定义进度条 UI
        // 拖动播放时间条
        timeBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double t = media.getDuration().toSeconds() * timeBar.getValue() / TIME_BAR_MAX;
                currTimeLabel.setText(TimeUtils.format(t));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                double t = media.getDuration().toSeconds() * timeBar.getValue() / TIME_BAR_MAX;
                mp.seek(Duration.seconds(t));
            }
        });
        // 设置进度条最佳大小
        progressPanel.add(currTimeLabel);
        progressPanel.add(timeBar);
        progressPanel.add(durationLabel);
        bottomBox.add(progressPanel);
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    // 控制面板
    void initControlPanel() {
        playOrPauseButton.setToolTipText(PLAY_TIP);
        playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, style.getButtonColor()));
        playOrPauseButton.setPreferredSize(new Dimension(pauseIcon.getIconWidth(), pauseIcon.getIconHeight()));
        playOrPauseButton.addMouseListener(new ButtonMouseListener(playOrPauseButton, f));
        playOrPauseButton.addActionListener(e -> playOrPause());

        // 快进
        forwardButton.setToolTipText(FORWARD_TIP);
        forwardButton.setIcon(ImageUtils.dye((ImageIcon) forwardButton.getIcon(), style.getButtonColor()));
        forwardButton.addMouseListener(new ButtonMouseListener(forwardButton, f));
        forwardButton.setPreferredSize(new Dimension(forwIcon.getIconWidth(), forwIcon.getIconHeight()));
        forwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().add(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 快退
        backwardButton.setToolTipText(BACKWARD_TIP);
        backwardButton.setIcon(ImageUtils.dye((ImageIcon) backwardButton.getIcon(), style.getButtonColor()));
        backwardButton.addMouseListener(new ButtonMouseListener(backwardButton, f));
        backwardButton.setPreferredSize(new Dimension(backwIcon.getIconWidth(), backwIcon.getIconHeight()));
        backwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().subtract(Duration.seconds(f.videoForwardOrBackwardTime)));
        });
        // 静音
        muteButton.setToolTipText(SOUND_TIP);
        muteButton.setIcon(ImageUtils.dye(soundIcon, style.getButtonColor()));
        muteButton.addMouseListener(new ButtonMouseListener(muteButton, f));
        muteButton.setPreferredSize(new Dimension(muteIcon.getIconWidth(), muteIcon.getIconHeight()));
        muteButton.addActionListener(e -> {
            if (isMute = !isMute) {
                muteButton.setToolTipText(MUTE_TIP);
                muteButton.setIcon(ImageUtils.dye(muteIcon, style.getButtonColor()));
            } else {
                muteButton.setToolTipText(SOUND_TIP);
                muteButton.setIcon(ImageUtils.dye(soundIcon, style.getButtonColor()));
            }
            mp.setMute(isMute);
        });
        // 音量调节滑动条
        volumeSlider.setUI(new SliderUI(volumeSlider, style.getSliderColor(), style.getSliderColor(), f, mp, false));
        volumeSlider.setPreferredSize(new Dimension(100, 12));
        volumeSlider.setMaximum(MAX_VOLUME);
        volumeSlider.addChangeListener(e -> {
            mp.setVolume((float) volumeSlider.getValue() / MAX_VOLUME);
            if (isMute) {
                muteButton.setToolTipText(SOUND_TIP);
                muteButton.setIcon(ImageUtils.dye(soundIcon, style.getButtonColor()));
                mp.setMute(isMute = false);
            }
        });
        // 移入手势
        volumeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                volumeSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });
        // 收藏
        collectButton.setToolTipText(COLLECT_TIP);
        collectButton.setIcon(ImageUtils.dye(f.hasBeenCollected(netMvInfo) ? hasCollectedIcon : collectIcon, style.getButtonColor()));
        collectButton.addMouseListener(new ButtonMouseListener(collectButton, f));
        collectButton.setPreferredSize(new Dimension(collectIcon.getIconWidth(), collectIcon.getIconHeight()));
        collectButton.addActionListener(e -> {
            if (!f.hasBeenCollected(netMvInfo)) {
                // 加载 MV 基本信息
                netMvInfo.setInvokeLater(() -> f.updateRenderer(f.collectionList));
                GlobalExecutors.requestExecutor.submit(() -> MusicServerUtils.fillMvDetail(netMvInfo));
                f.mvCollectionModel.add(0, netMvInfo);
                collectButton.setToolTipText(CANCEL_COLLECTION_TIP);
                collectButton.setIcon(ImageUtils.dye(hasCollectedIcon, style.getButtonColor()));
                new TipDialog(f, COLLECT_SUCCESS_MSG).showDialog();
            } else {
                f.mvCollectionModel.removeElement(netMvInfo);
                collectButton.setToolTipText(COLLECT_TIP);
                collectButton.setIcon(ImageUtils.dye(collectIcon, style.getButtonColor()));
                new TipDialog(f, CANCEL_COLLECTION_SUCCESS_MSG).showDialog();
            }
        });
        // 下载
        downloadButton.setEnabled(!isLocal || !netMvInfo.isMp4());
        downloadButton.setToolTipText(DOWNLOAD_TIP);
        downloadButton.setIcon(ImageUtils.dye(downloadIcon, style.getButtonColor()));
        downloadButton.setDisabledIcon(ImageUtils.dye(downloadIcon, ColorUtils.darker(style.getButtonColor())));
        downloadButton.addMouseListener(new ButtonMouseListener(downloadButton, f));
        downloadButton.setPreferredSize(new Dimension(downloadIcon.getIconWidth(), downloadIcon.getIconHeight()));
        downloadButton.addActionListener(e -> {
            downloadMv();
        });
        rateButton.setForeground(style.getButtonColor());
        rateButton.setToolTipText(RATE_TIP);
        rateButton.setIcon(ImageUtils.dye((ImageIcon) rateButton.getIcon(), style.getButtonColor()));
        rateButton.addMouseListener(new ButtonMouseListener(rateButton, f));
        rateButton.setPreferredSize(new Dimension(rateIcon.getIconWidth(), rateIcon.getIconHeight()));
        rateButton.addActionListener(e -> {
            RateDialog rd = new RateDialog(f, this, rateButton);
            rd.showDialog();
        });
        // 快进/快退时间
        fobTimePopupMenu = new CustomPopupMenu(f);
        for (CustomRadioButtonMenuItem menuItem : fobTimeMenuItems) {
            Color menuItemColor = style.getMenuItemColor();
            menuItem.setForeground(menuItemColor);
            menuItem.setUI(new RadioButtonMenuItemUI(menuItemColor));
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
        fobTimeButton.setForeground(style.getButtonColor());
        fobTimeButton.setToolTipText(FOB_TIME_TIP);
        fobTimeButton.setIcon(ImageUtils.dye((ImageIcon) fobTimeButton.getIcon(), style.getButtonColor()));
        fobTimeButton.addMouseListener(new ButtonMouseListener(fobTimeButton, f));
        fobTimeButton.setPreferredSize(new Dimension(fobTimeIcon.getIconWidth(), fobTimeIcon.getIconHeight()));
        fobTimeButton.setComponentPopupMenu(fobTimePopupMenu);
        fobTimeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                fobTimePopupMenu.show(fobTimeButton, e.getX(), e.getY());
            }
        });
        fullScreenButton.setForeground(style.getButtonColor());
        fullScreenButton.setToolTipText(FULL_SCREEN_TIP);
        fullScreenButton.setIcon(ImageUtils.dye((ImageIcon) fullScreenButton.getIcon(), style.getButtonColor()));
        fullScreenButton.addMouseListener(new ButtonMouseListener(fullScreenButton, f));
        fullScreenButton.setPreferredSize(new Dimension(fullScreenIcon.getIconWidth(), fullScreenIcon.getIconHeight()));
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
        fl.setHgap(13);
        controlPanel.setLayout(fl);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(collectButton);
        controlPanel.add(downloadButton);
        controlPanel.add(backwardButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(forwardButton);
        fl = new FlowLayout();
        fl.setHgap(10);
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
        volumeSlider.setValue(f.getVolumeSlider().getValue());
        mp.setRate(f.currVideoRate);
        mp.play();
        playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, style.getButtonColor()));
        playOrPauseButton.setToolTipText(PAUSE_TIP);
    }

    public void playOrPause() {
        switch (mp.getStatus()) {
            case PLAYING:
                mp.pause();
                playOrPauseButton.setIcon(ImageUtils.dye(playIcon, style.getButtonColor()));
                playOrPauseButton.setToolTipText(PLAY_TIP);
                break;
            case PAUSED:
                mp.play();
                playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, style.getButtonColor()));
                playOrPauseButton.setToolTipText(PAUSE_TIP);
                break;
            case READY:
                playVideo();
                break;
        }
    }

    // 下载 MV
    private void downloadMv() {
        f.multiDownloadMv(Collections.singletonList(netMvInfo));
    }

    // 改变所有单选菜单项图标
    private void updateRadioButtonMenuItemIcon() {
//        Component[] components = ratePopupMenu.getComponents();
//        for (Component c : components) {
//            CustomRadioButtonMenuItem mi = (CustomRadioButtonMenuItem) c;
//            if (mi.isSelected()) mi.setIcon(ImageUtils.dye(dotIcon, style.getMenuItemColor()));
//            else mi.setIcon(null);
//        }
        Component[] components = fobTimePopupMenu.getComponents();
        for (Component c : components) {
            CustomRadioButtonMenuItem mi = (CustomRadioButtonMenuItem) c;
            if (mi.isSelected()) mi.setIcon(ImageUtils.dye(dotIcon, style.getMenuItemColor()));
            else mi.setIcon(null);
        }
    }

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.getPlayer().loadedMusic();
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

    private class VideoDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public VideoDialogPanel() {
            initBorder();
        }

        public void initBorder() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void eraseBorder() {
            setBorder(null);
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
            if (!fullScreen) {
                for (int i = 0; i < pixels; i++) {
                    g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                    g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
                }
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}
