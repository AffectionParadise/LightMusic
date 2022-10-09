package net.doge.ui.components.dialog;

import javafx.scene.media.MediaException;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.SimplePath;
import net.doge.models.*;
import net.doge.ui.components.CustomPopupMenu;
import net.doge.ui.components.CustomRadioButtonMenuItem;
import net.doge.ui.components.GlobalPanel;
import net.doge.ui.PlayerFrame;
import net.doge.ui.componentui.RadioButtonMenuItemUI;
import net.doge.ui.componentui.SliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.MusicServerUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import net.coobird.thumbnailator.Thumbnails;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private final int MEDIA_WIDTH = 1150;
    private final int MEDIA_MIN_WIDTH = 500;
    private final int MEDIA_HEIGHT = 800;

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

    // 收藏成功提示
    private final String COLLECT_SUCCESS_MSG = "收藏成功";
    // 取消收藏成功提示
    private final String CANCEL_COLLECTION_SUCCESS_MSG = "取消收藏成功";
    // 播放异常提示
    private final String ERROR_MSG = "播放视频时发生异常";

    // 选定点图标
    private ImageIcon dotIcon = new ImageIcon(SimplePath.ICON_PATH + "dot.png");
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
    // 快退图标
    private ImageIcon backwIcon = new ImageIcon(SimplePath.ICON_PATH + "backw.png");
    // 快进图标
    private ImageIcon forwIcon = new ImageIcon(SimplePath.ICON_PATH + "forw.png");
    // 快进快退时间图标
    private ImageIcon fobTimeIcon = new ImageIcon(SimplePath.ICON_PATH + "fobTime.png");
    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    // 标题
    private String title = "";
    // 是否静音
    private boolean isMute = false;
    // 当前快进/快退时间
    private int currFobTime = 10;

    private VideoDialogPanel globalPanel = new VideoDialogPanel();
    private JFXPanel jfxPanel = new JFXPanel();

    // 标题面板
    private Box topBox = new Box(BoxLayout.X_AXIS);
    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    // 进度条面板
    private JPanel progressPanel = new JPanel();
    private JSlider timeBar = new JSlider();
    private JLabel currTimeLabel = new JLabel(DEFAULT_TIME);
    private JLabel durationLabel = new JLabel(DEFAULT_TIME);

    // 控制面板
    private JPanel controlPanel = new JPanel();
    private JPanel volumePanel = new JPanel();
    private JButton playOrPauseButton = new JButton(playIcon);
    private JButton backwardButton = new JButton(backwIcon);
    private JButton forwardButton = new JButton(forwIcon);
    private JButton muteButton = new JButton(soundIcon);
    private JSlider volumeSlider = new JSlider();
    private JButton collectButton = new JButton(collectIcon);
    private JButton downloadButton = new JButton(downloadIcon);
    private JButton rateButton = new JButton(rateIcon);
    //    private CustomPopupMenu ratePopupMenu;
//    private ButtonGroup rateMenuItemsButtonGroup = new ButtonGroup();
//    private CustomRadioButtonMenuItem[] rateMenuItems = {
//            new CustomRadioButtonMenuItem("0.2x"),
//            new CustomRadioButtonMenuItem("0.3x"),
//            new CustomRadioButtonMenuItem("0.4x"),
//            new CustomRadioButtonMenuItem("0.5x"),
//            new CustomRadioButtonMenuItem("0.6x"),
//            new CustomRadioButtonMenuItem("0.7x"),
//            new CustomRadioButtonMenuItem("0.8x"),
//            new CustomRadioButtonMenuItem("0.9x"),
//            new CustomRadioButtonMenuItem("1x", true),
//            new CustomRadioButtonMenuItem("1.1x"),
//            new CustomRadioButtonMenuItem("1.2x"),
//            new CustomRadioButtonMenuItem("1.3x"),
//            new CustomRadioButtonMenuItem("1.4x"),
//            new CustomRadioButtonMenuItem("1.5x"),
//            new CustomRadioButtonMenuItem("1.6x"),
//            new CustomRadioButtonMenuItem("1.7x"),
//            new CustomRadioButtonMenuItem("1.8x"),
//            new CustomRadioButtonMenuItem("1.9x"),
//            new CustomRadioButtonMenuItem("2x"),
//            new CustomRadioButtonMenuItem("3x"),
//            new CustomRadioButtonMenuItem("4x"),
//            new CustomRadioButtonMenuItem("5x"),
//            new CustomRadioButtonMenuItem("6x"),
//            new CustomRadioButtonMenuItem("7x"),
//            new CustomRadioButtonMenuItem("8x")
//    };
    private JButton fobTimeButton = new JButton(fobTimeIcon);
    private CustomPopupMenu fobTimePopupMenu;
    private ButtonGroup fobTimeMenuItemsButtonGroup = new ButtonGroup();
    private CustomRadioButtonMenuItem[] fobTimeMenuItems = {
            new CustomRadioButtonMenuItem("5秒"),
            new CustomRadioButtonMenuItem("10秒", true),
            new CustomRadioButtonMenuItem("15秒"),
            new CustomRadioButtonMenuItem("20秒"),
            new CustomRadioButtonMenuItem("25秒"),
            new CustomRadioButtonMenuItem("30秒"),
            new CustomRadioButtonMenuItem("45秒"),
            new CustomRadioButtonMenuItem("60秒")
    };

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.Y_AXIS);

    private int tryTime;
    private boolean isLocal;
    private String uri;

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

        initUI();
    }

    public void initUI() {
        // 取消桌面歌词置顶避免遮挡视线
        f.getDesktopLyricDialog().setAlwaysOnTop(false);

        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        titleLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });
        // 保持在屏幕正中间
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setLocationRelativeTo(null);
            }
        });

        setUndecorated(true);
        setResizable(false);
        setSize(800, 600);
        globalPanel.setLayout(new BorderLayout());

        initView();
        initTitleBar();
        initTimeBar();
        initControlPanel();
        playVideo();

        // Dialog 背景透明
        setBackground(Colors.TRANSLUCENT);
        globalPanel.setOpaque(false);
        add(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    // 初始化视频界面
    void initView() {
        if (isLocal) {
            File mediaFile = new File(uri);
            media = new Media(mediaFile.toURI().toString());
        } else media = new Media(uri);
        mp = new MediaPlayer(media);
        mediaView = new MediaView(mp);

        // 视频宽高控制
//            media.widthProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                    int width = media.getWidth(), height = media.getHeight();
//                    if (width > height) mediaView.setFitWidth(MEDIA_WIDTH);
//                    else mediaView.setFitWidth(MEDIA_WIDTH_2);
//                }
//            });
        media.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            int width = media.getWidth(), height = media.getHeight();
            // 调整视频大小使之不超出屏幕
            if (width > height) {
                mediaView.setFitWidth(MEDIA_WIDTH);
                mediaView.setFitHeight(height * MEDIA_WIDTH / width);
            } else {
                mediaView.setFitHeight(MEDIA_HEIGHT);
                // 宽度不得低于最小值
                int ow = width * MEDIA_HEIGHT / height;
                mediaView.setFitWidth(Math.max(MEDIA_MIN_WIDTH, ow));
                // 视频位于中央
                mediaView.setX((getWidth() - 2 * pixels - ow) / 2);
            }
            MusicPlayer player = f.getPlayer();
            SimpleMusicInfo musicInfo = player.getMusicInfo();
            doBlur(!f.getIsBlur() || !player.loadedMusic() ? ImageUtils.read(style.getStyleImgPath()) : musicInfo.hasAlbumImage() ? musicInfo.getAlbumImage() : f.getDefaultAlbumImage());
        });
        mediaView.fitWidthProperty().addListener((observableValue, oldValue, newValue) -> {
            int nv = newValue.intValue();
            jfxPanel.setPreferredSize(new Dimension(nv, media.getHeight()));
            setSize(nv - 2 + 2 * pixels, getHeight());
            timeBar.setPreferredSize(new Dimension(getWidth() - 2 * pixels - currTimeLabel.getWidth() - durationLabel.getWidth() - 20 * 2, 12));
        });
        mediaView.fitHeightProperty().addListener((observableValue, oldValue, newValue) -> {
            int nv = newValue.intValue();
            jfxPanel.setPreferredSize(new Dimension(media.getWidth(), nv + 2 * pixels));
            setSize(getWidth(), nv + 129 + 2 * pixels);
        });
        // 刷新缓冲长度
        mp.bufferProgressTimeProperty().addListener(l -> timeBar.repaint());
        mp.currentTimeProperty().addListener(l -> {
            // 未被操作时频繁更新时间条
            if (!timeBar.getValueIsAdjusting())
                timeBar.setValue((int) (mp.getCurrentTime().toSeconds() / media.getDuration().toSeconds() * TIME_BAR_MAX));
            currTimeLabel.setText(TimeUtils.format(mp.getCurrentTime().toSeconds()));
            durationLabel.setText(TimeUtils.format(media.getDuration().toSeconds()));
            // 设置当前播放时间标签的最佳大小，避免导致进度条长度发生变化！
            String t = durationLabel.getText().replaceAll("[1-9]", "0");
            FontMetrics m = durationLabel.getFontMetrics(globalFont);
            currTimeLabel.setPreferredSize(new Dimension(m.stringWidth(t) + 2, durationLabel.getHeight()));
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
                initView();
                playVideo();
            }
            // 歌曲 url 过期后重新加载 url 再播放
            else if (type == MediaException.Type.MEDIA_INACCESSIBLE || type == MediaException.Type.UNKNOWN) {
                if (!isLocal) netMvInfo.setUrl(uri = MusicServerUtils.fetchMvUrl(netMvInfo));
                initView();
                playVideo();
            }
            // 尝试多次无效直接关闭窗口
            if (++tryTime >= 3) {
                closeButton.doClick();
                new TipDialog(f, ERROR_MSG).showDialog();
            }
        });

        Scene scene = new Scene(new Pane(mediaView), media.getWidth(), media.getHeight());
        // 视频边缘黑幕
        scene.setFill(javafx.scene.paint.Color.BLACK);
        jfxPanel.setScene(scene);
        globalPanel.add(jfxPanel, BorderLayout.CENTER);
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setOpaque(false);
        titleLabel.setFont(globalFont);
        titleLabel.setText(StringUtils.textToHtml(title));
        titleLabel.setPreferredSize(new Dimension(600, 30));
        closeButton.setIcon(ImageUtils.dye(closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(closeWindowIcon.getIconWidth() + 2, closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            Future<?> future = GlobalExecutors.requestExecutor.submit(() -> {
                dispose();
                // 恢复桌面歌词置顶
                f.getDesktopLyricDialog().setAlwaysOnTop(true);
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
        topBox.add(topPanel);
        topBox.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topBox, BorderLayout.NORTH);
    }

    // 进度条
    void initTimeBar() {
        currTimeLabel.setFont(globalFont);
        durationLabel.setFont(globalFont);
        currTimeLabel.setForeground(style.getTimeBarColor());
        durationLabel.setForeground(style.getTimeBarColor());
        timeBar.setFocusable(false);
        timeBar.setMinimum(TIME_BAR_MIN);
        timeBar.setMaximum(TIME_BAR_MAX);
        timeBar.setValue(TIME_BAR_MIN);
        timeBar.setOpaque(false);
        timeBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        progressPanel.setOpaque(false);
        progressPanel.add(currTimeLabel);
        progressPanel.add(timeBar);
        progressPanel.add(durationLabel);
        bottomBox.add(progressPanel);
    }

    // 控制面板
    void initControlPanel() {
        playOrPauseButton.setFocusable(false);
        playOrPauseButton.setContentAreaFilled(false);
        playOrPauseButton.setOpaque(false);
        playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, style.getButtonColor()));
        playOrPauseButton.setPreferredSize(new Dimension(pauseIcon.getIconWidth(), pauseIcon.getIconHeight()));
        playOrPauseButton.addMouseListener(new ButtonMouseListener(playOrPauseButton, f));
        playOrPauseButton.addActionListener(e -> playOrPause());

        // 快进
        forwardButton.setToolTipText(FORWARD_TIP);
        forwardButton.setFocusable(false);
        forwardButton.setOpaque(false);
        forwardButton.setContentAreaFilled(false);
        forwardButton.setIcon(ImageUtils.dye((ImageIcon) forwardButton.getIcon(), style.getButtonColor()));
        forwardButton.addMouseListener(new ButtonMouseListener(forwardButton, f));
        forwardButton.setPreferredSize(new Dimension(forwIcon.getIconWidth(), forwIcon.getIconHeight()));
        forwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().add(Duration.seconds(currFobTime)));
        });
        // 快退
        backwardButton.setToolTipText(BACKWARD_TIP);
        backwardButton.setFocusable(false);
        backwardButton.setOpaque(false);
        backwardButton.setContentAreaFilled(false);
        backwardButton.setIcon(ImageUtils.dye((ImageIcon) backwardButton.getIcon(), style.getButtonColor()));
        backwardButton.addMouseListener(new ButtonMouseListener(backwardButton, f));
        backwardButton.setPreferredSize(new Dimension(backwIcon.getIconWidth(), backwIcon.getIconHeight()));
        backwardButton.addActionListener(e -> {
            mp.seek(mp.getCurrentTime().subtract(Duration.seconds(currFobTime)));
        });
        // 静音
        muteButton.setToolTipText(SOUND_TIP);
        muteButton.setFocusable(false);
        muteButton.setOpaque(false);
        muteButton.setContentAreaFilled(false);
        muteButton.setIcon(ImageUtils.dye((ImageIcon) muteButton.getIcon(), style.getButtonColor()));
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
        volumeSlider.setOpaque(false);
        volumeSlider.setFocusable(false);
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
        collectButton.setFocusable(false);
        collectButton.setOpaque(false);
        collectButton.setContentAreaFilled(false);
        collectButton.setIcon(ImageUtils.dye(f.hasBeenCollected(netMvInfo) ? hasCollectedIcon : collectIcon, style.getButtonColor()));
        collectButton.addMouseListener(new ButtonMouseListener(collectButton, f));
        collectButton.setPreferredSize(new Dimension(collectIcon.getIconWidth(), collectIcon.getIconHeight()));
        collectButton.addActionListener(e -> {
            if (!f.hasBeenCollected(netMvInfo)) {
                // 加载 MV 基本信息
                GlobalExecutors.requestExecutor.submit(() -> MusicServerUtils.fillMvDetail(netMvInfo));
                f.getMvCollectionModel().add(0, netMvInfo);
                collectButton.setToolTipText(CANCEL_COLLECTION_TIP);
                collectButton.setIcon(ImageUtils.dye(hasCollectedIcon, style.getButtonColor()));
                new TipDialog(f, COLLECT_SUCCESS_MSG).showDialog();
            } else {
                f.getMvCollectionModel().removeElement(netMvInfo);
                collectButton.setToolTipText(COLLECT_TIP);
                collectButton.setIcon(ImageUtils.dye(collectIcon, style.getButtonColor()));
                new TipDialog(f, CANCEL_COLLECTION_SUCCESS_MSG).showDialog();
            }
        });
        // 下载
        downloadButton.setEnabled(!isLocal || !netMvInfo.isMp4());
        downloadButton.setToolTipText(DOWNLOAD_TIP);
        downloadButton.setFocusable(false);
        downloadButton.setOpaque(false);
        downloadButton.setContentAreaFilled(false);
        downloadButton.setIcon(ImageUtils.dye(downloadIcon, style.getButtonColor()));
        downloadButton.setDisabledIcon(ImageUtils.dye(downloadIcon, style.getButtonColor().darker()));
        downloadButton.addMouseListener(new ButtonMouseListener(downloadButton, f));
        downloadButton.setPreferredSize(new Dimension(downloadIcon.getIconWidth(), downloadIcon.getIconHeight()));
        downloadButton.addActionListener(e -> {
            downloadMv();
        });
        // 倍速
//        ratePopupMenu = new CustomPopupMenu(f);
//        for (CustomRadioButtonMenuItem menuItem : rateMenuItems) {
//            menuItem.setFont(globalFont);
//            Color menuItemColor = style.getMenuItemColor();
//            menuItem.setForeground(menuItemColor);
//            menuItem.setUI(new RadioButtonMenuItemUI(menuItemColor));
//            menuItem.addActionListener(e -> {
//                mp.setRate(Double.parseDouble(menuItem.getText().replace("x", "")));
//                rateButton.setText(menuItem.getText());
//                updateRadioButtonMenuItemIcon();
//            });
//            rateMenuItemsButtonGroup.add(menuItem);
//            ratePopupMenu.add(menuItem);
//        }
        rateButton.setFont(globalFont);
        rateButton.setForeground(style.getButtonColor());
        rateButton.setToolTipText(RATE_TIP);
        rateButton.setFocusable(false);
        rateButton.setOpaque(false);
        rateButton.setContentAreaFilled(false);
        rateButton.setIcon(ImageUtils.dye((ImageIcon) rateButton.getIcon(), style.getButtonColor()));
        rateButton.addMouseListener(new ButtonMouseListener(rateButton, f));
        rateButton.setPreferredSize(new Dimension(rateIcon.getIconWidth(), rateIcon.getIconHeight()));
        rateButton.addActionListener(e -> {
            RateDialog rd = new RateDialog(f, this, rateButton);
            rd.showDialog();
        });
//        rateButton.setComponentPopupMenu(ratePopupMenu);
//        rateButton.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                ratePopupMenu.show(rateButton, e.getX(), e.getY());
//            }
//        });
        // 快进/快退时间
        fobTimePopupMenu = new CustomPopupMenu(f);
        for (CustomRadioButtonMenuItem menuItem : fobTimeMenuItems) {
            menuItem.setFont(globalFont);
            Color menuItemColor = style.getMenuItemColor();
            menuItem.setForeground(menuItemColor);
            menuItem.setUI(new RadioButtonMenuItemUI(menuItemColor));
            menuItem.addActionListener(e -> {
                currFobTime = Integer.parseInt(menuItem.getText().replace("秒", ""));
//                fobTimeButton.setText(menuItem.getText().replace("秒", "s"));
                updateRadioButtonMenuItemIcon();
            });
            fobTimeMenuItemsButtonGroup.add(menuItem);
            fobTimePopupMenu.add(menuItem);
        }
        fobTimeButton.setFont(globalFont);
        fobTimeButton.setForeground(style.getButtonColor());
        fobTimeButton.setToolTipText(FOB_TIME_TIP);
        fobTimeButton.setFocusable(false);
        fobTimeButton.setOpaque(false);
        fobTimeButton.setContentAreaFilled(false);
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

        updateRadioButtonMenuItemIcon();

        controlPanel.setOpaque(false);
        volumePanel.setOpaque(false);
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
        controlPanel.add(Box.createHorizontalGlue());
        bottomBox.add(controlPanel);
        globalPanel.add(bottomBox, BorderLayout.SOUTH);
    }

    void playVideo() {
        volumeSlider.setValue(f.getVolumeSlider().getValue());
        mp.play();
        playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, style.getButtonColor()));
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
    void downloadMv() {
        f.multiDownloadMv(Collections.singletonList(netMvInfo));
    }

    // 改变所有单选菜单项图标
    void updateRadioButtonMenuItemIcon() {
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

    private class VideoDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public VideoDialogPanel() {
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
