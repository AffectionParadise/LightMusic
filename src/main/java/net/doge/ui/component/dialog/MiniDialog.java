package net.doge.ui.component.dialog;

import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.CustomButton;
import net.doge.ui.component.button.listener.ButtonMouseListener;
import net.doge.ui.component.dialog.factory.AbstractMiniDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.system.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Doge
 * @Description 迷你模式对话框
 * @Date 2020/12/15
 */
public class MiniDialog extends AbstractMiniDialog {
    private final int WIDTH = 460;
    private final int HEIGHT = 76;
    private final int offsetX = WIDTH - 6;
    private final int offsetY = HEIGHT - 6;
    private final String CLOSE_TIP = "关闭";

    // 关闭窗口图标
    private ImageIcon closeMiniIcon = LMIconManager.getIcon("dialog.close");

    private CustomPanel controlPanel = new CustomPanel();

    // 标签 + 按钮
    public CustomLabel infoLabel = new CustomLabel();
    public CustomButton playOrPauseButton = new CustomButton();
    public CustomButton playLastButton = new CustomButton();
    public CustomButton playNextButton = new CustomButton();
    public CustomButton closeButton = new CustomButton();

    public ExecutorService globalExecutor = Executors.newSingleThreadExecutor();
    private Timer locationTimer;

    private int destX;
    private int destY;

    private boolean dragged;

    public MiniDialog(MainFrame f) {
        super(f);
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
        locationTimer = new Timer(0, e -> {
            Point p = getLocation();
            if (p.x == destX && p.y == destY) locationTimer.stop();
            else if (p.x < destX) setLocation(Math.min(p.x + 2, destX), p.y);
            else if (p.x > destX) setLocation(Math.max(p.x - 2, destX), p.y);
            else if (p.y < destY) setLocation(p.x, Math.min(p.y + 2, destY));
            else setLocation(p.x, Math.max(p.y - 2, destY));
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
        setContentPane(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    // 初始化界面
    private void initView() {
        // 颜色
        infoLabel.setForeground(f.currUIStyle.getTextColor());
        // 正在播放的音乐信息
        infoLabel.setText(f.changePaneButton.getText());
        infoLabel.setIcon(f.changePaneButton.getIcon());
        infoLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        infoLabel.setIconTextGap(10);
        infoLabel.setPreferredSize(new Dimension(240, 66));

        // 提示
        playLastButton.setToolTipText(f.lastButton.getToolTipText());
        playOrPauseButton.setToolTipText(f.playOrPauseButton.getToolTipText());
        playNextButton.setToolTipText(f.nextButton.getToolTipText());
        closeButton.setToolTipText(CLOSE_TIP);

        // 监听事件
        playLastButton.addMouseListener(new ButtonMouseListener(playLastButton, f));
        playOrPauseButton.addMouseListener(new ButtonMouseListener(playOrPauseButton, f));
        playNextButton.addMouseListener(new ButtonMouseListener(playNextButton, f));
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));

        // 图标
        playLastButton.setIcon(f.lastButton.getIcon());
        playOrPauseButton.setIcon(f.playOrPauseButton.getIcon());
        playNextButton.setIcon(f.nextButton.getIcon());
        closeButton.setIcon(ImageUtil.dye(closeMiniIcon, f.currUIStyle.getIconColor()));

        // 按钮大小
        playLastButton.setPreferredSize(new Dimension(playLastButton.getIcon().getIconWidth() + 10, playLastButton.getIcon().getIconHeight() + 10));
        playOrPauseButton.setPreferredSize(new Dimension(playOrPauseButton.getIcon().getIconWidth() + 10, playOrPauseButton.getIcon().getIconHeight() + 10));
        playNextButton.setPreferredSize(new Dimension(playNextButton.getIcon().getIconWidth() + 10, playNextButton.getIcon().getIconHeight() + 10));
        closeButton.setPreferredSize(new Dimension(closeButton.getIcon().getIconWidth() + 10, closeButton.getIcon().getIconHeight() + 10));

        playLastButton.addActionListener(e -> f.lastButton.doClick());
        playNextButton.addActionListener(e -> f.nextButton.doClick());
        playOrPauseButton.addActionListener(e -> f.playOrPauseButton.doClick());
        closeButton.addActionListener(e -> {
            dispose();
            f.miniDialog = null;
            if (f.showSpectrum) f.openSpectrum();
            f.setVisible(true);
            f.lrcScrollAnimation = true;
        });

        FlowLayout fl = new FlowLayout();
        fl.setHgap(5);
        controlPanel.setLayout(fl);

        controlPanel.add(infoLabel);
        controlPanel.add(playLastButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(playNextButton);
        controlPanel.add(closeButton);

//        controlPanel.setPreferredSize(new Dimension(300, 60));

        globalPanel.add(controlPanel, BorderLayout.CENTER);

        updateBlur();
    }
}
