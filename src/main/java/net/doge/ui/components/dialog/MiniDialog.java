package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.GlobalPanel;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ColorThiefUtils;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author yzx
 * @Description 迷你模式对话框
 * @Date 2020/12/15
 */
public class MiniDialog extends JDialog {
    private final int WIDTH = 480;
    private final int HEIGHT = 76;
    private final int offsetX = WIDTH - 6;
    private final int offsetY = HEIGHT - 6;
    private final String CLOSE_TIP = "关闭";

    // 关闭窗口图标
    private ImageIcon closeMiniIcon = new ImageIcon(SimplePath.ICON_PATH + "closeMini.png");

    private GlobalPanel globalPanel = new GlobalPanel();
    private CustomPanel controlPanel = new CustomPanel();

    // 标签 + 按钮
    public CustomLabel infoLabel = new CustomLabel();
    public CustomButton playOrPauseButton = new CustomButton();
    public CustomButton playLastButton = new CustomButton();
    public CustomButton playNextButton = new CustomButton();
    public CustomButton closeButton = new CustomButton();

    private PlayerFrame f;
    private CustomButton changePaneButton;
    private CustomButton popButton;
    private CustomButton lastButton;
    private CustomButton nextButton;
    private UIStyle style;

    public ExecutorService globalExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService globalPanelExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService locationExecutor = Executors.newSingleThreadExecutor();
    private Timer globalPanelTimer;
    private Timer locationTimer;

    private int destX;
    private int destY;

    private boolean dragged;

    public MiniDialog(PlayerFrame f) {
        super();
        this.f = f;
        style = f.getCurrUIStyle();
        changePaneButton = f.getChangePaneButton();
        popButton = f.getPlayOrPauseButton();
        lastButton = f.getLastButton();
        nextButton = f.getNextButton();
        initUI();
    }

    private void popOut() {
        Point p = getLocation();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        // 隐藏在上方/左方/右方/下方
        if (p.y <= 0) {
            destX = p.x;
            destY = -offsetY;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.x <= 0) {
            destX = -offsetX;
            destY = p.y;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.x + WIDTH >= d.width) {
            destX = d.width - WIDTH + offsetX;
            destY = p.y;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.y + HEIGHT >= d.height) {
            destX = p.x;
            destY = d.height - HEIGHT + offsetY;
            if (!locationTimer.isRunning()) locationTimer.start();
        }
    }

    private void popIn() {
        // 从上方/左方/右方/下方显示
        Point p = getLocation();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (p.y <= 0) {
            destX = p.x;
            destY = 0;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.x <= 0) {
            destX = 0;
            destY = p.y;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.x + WIDTH >= d.width) {
            destX = d.width - WIDTH;
            destY = p.y;
            if (!locationTimer.isRunning()) locationTimer.start();
        } else if (p.y + HEIGHT >= d.height) {
            destX = p.x;
            destY = d.height - HEIGHT;
            if (!locationTimer.isRunning()) locationTimer.start();
        }
    }

    public void initUI() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        globalPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }

            // 拖动完成松开鼠标后隐藏
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragged) return;
                popOut();
                dragged = false;
            }

            // 鼠标进入后从隐藏位置显示
            @Override
            public void mouseEntered(MouseEvent e) {
                popIn();
            }

            // 鼠标退出后继续隐藏
            @Override
            public void mouseExited(MouseEvent e) {
                // 防止对鼠标退出事件的误判
                if (globalPanel.contains(e.getX(), e.getY())) return;
                popOut();
            }
        });
        globalPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                dragged = true;
                // 动画过程拖动时停止动画
                if (locationTimer.isRunning()) locationTimer.stop();
                Point p = getLocation();
                int nx = p.x + e.getX() - origin.x, ny = p.y + e.getY() - origin.y;
                setLocation(f.miniX = nx, f.miniY = ny);
            }
        });

        // 窗口隐藏与显示动画
        locationTimer = new Timer(1, e -> {
            locationExecutor.submit(() -> {
                Point p = getLocation();
                if (p.x == destX && p.y == destY) locationTimer.stop();
                else if (p.x < destX) setLocation(Math.min(p.x + 2, destX), p.y);
                else if (p.x > destX) setLocation(Math.max(p.x - 2, destX), p.y);
                else if (p.y < destY) setLocation(p.x, Math.min(p.y + 2, destY));
                else if (p.y > destY) setLocation(p.x, Math.max(p.y - 2, destY));
            });
        });

        setSize(WIDTH, HEIGHT);
        // 窗口位置初始化
        if (f.miniX == -0x3f3f3f3f || f.miniY == -0x3f3f3f3f) setLocationRelativeTo(null);
        else {
            setLocation(f.miniX, f.miniY);
            popOut();
        }
        setUndecorated(true);
        setAlwaysOnTop(true);
        setResizable(false);
        globalPanel.setLayout(new BorderLayout());

        initView();

        // Dialog 背景透明
        setBackground(Colors.TRANSLUCENT);
        add(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    // 初始化界面
    void initView() {
        globalPanelTimer = new Timer(10, e -> {
            globalPanelExecutor.submit(() -> {
                globalPanel.setOpacity((float) Math.min(1, globalPanel.getOpacity() + 0.05));
                if (globalPanel.getOpacity() >= 1) globalPanelTimer.stop();
            });
        });

        // 颜色
        infoLabel.setForeground(style.getLabelColor());
        // 正在播放的音乐信息
        infoLabel.setText(changePaneButton.getText());
        infoLabel.setIcon(changePaneButton.getIcon());
        infoLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        infoLabel.setIconTextGap(10);
        infoLabel.setPreferredSize(new Dimension(240, 66));

        // 提示
        playLastButton.setToolTipText(lastButton.getToolTipText());
        playOrPauseButton.setToolTipText(popButton.getToolTipText());
        playNextButton.setToolTipText(nextButton.getToolTipText());
        closeButton.setToolTipText(CLOSE_TIP);

        // 监听事件
        playLastButton.addMouseListener(new ButtonMouseListener(playLastButton, f));
        playOrPauseButton.addMouseListener(new ButtonMouseListener(playOrPauseButton, f));
        playNextButton.addMouseListener(new ButtonMouseListener(playNextButton, f));
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));

        // 图标
        playLastButton.setIcon(lastButton.getIcon());
        playOrPauseButton.setIcon(popButton.getIcon());
        playNextButton.setIcon(nextButton.getIcon());
        closeButton.setIcon(ImageUtils.dye(closeMiniIcon, style.getButtonColor()));

        // 按钮大小
        playLastButton.setPreferredSize(new Dimension(playLastButton.getIcon().getIconWidth(), playLastButton.getIcon().getIconHeight()));
        playOrPauseButton.setPreferredSize(new Dimension(playOrPauseButton.getIcon().getIconWidth(), playOrPauseButton.getIcon().getIconHeight()));
        playNextButton.setPreferredSize(new Dimension(playNextButton.getIcon().getIconWidth(), playNextButton.getIcon().getIconHeight()));
        closeButton.setPreferredSize(new Dimension(closeButton.getIcon().getIconWidth(), closeButton.getIcon().getIconHeight()));

        playLastButton.addActionListener(e -> lastButton.doClick());
        playNextButton.addActionListener(e -> nextButton.doClick());
        playOrPauseButton.addActionListener(e -> popButton.doClick());
        closeButton.addActionListener(e -> {
            dispose();
            f.miniDialog = null;
            if (f.isShowSpectrum()) f.openSpectrum();
            f.setVisible(true);
            f.lrcScrollAnimation = true;
        });

        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        fl.setHgap(20);
        controlPanel.setLayout(fl);

        controlPanel.add(infoLabel);
        controlPanel.add(playLastButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(playNextButton);
        controlPanel.add(closeButton);

        controlPanel.setPreferredSize(new Dimension(300, 60));

        globalPanel.add(controlPanel, BorderLayout.CENTER);

        updateBlur();
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        boolean slight = false;
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG) 
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
            slight = style.isPureColor();
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage, slight);
    }

    public void doBlur(BufferedImage bufferedImage, boolean slight) {
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
            bufferedImage = ImageUtils.doBlur(bufferedImage);
            if (!slight) bufferedImage = ImageUtils.darker(bufferedImage);
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
            if (!globalPanelTimer.isRunning()) globalPanelTimer.start();
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
