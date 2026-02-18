package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.dialog.base.AbstractMiniDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Doge
 * @description 迷你模式对话框
 * @date 2020/12/15
 */
public class MiniDialog extends AbstractMiniDialog {
    private final int WIDTH = ScaleUtil.scale(460);
    private final int HEIGHT = ScaleUtil.scale(76);
    private final int offsetX = WIDTH - ScaleUtil.scale(6);
    private final int offsetY = HEIGHT - ScaleUtil.scale(6);
    private final String CLOSE_TIP = I18n.getText("closeTip");

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
        setBackground(Colors.TRANSPARENT);
        setContentPane(globalPanel);
    }

    public void showDialog() {
        setVisible(true);
    }

    // 初始化界面
    private void initView() {
        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color iconColor = style.getIconColor();

        // 颜色
        infoLabel.setForeground(textColor);
        // 正在播放的音乐信息
        infoLabel.setText(f.changePaneButton.getText());
        infoLabel.setIcon(f.changePaneButton.getIcon());
        infoLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        infoLabel.setIconTextGap(ScaleUtil.scale(10));
        infoLabel.setPreferredSize(new HDDimension(240, 66));

        // 提示
        playLastButton.setToolTipText(f.lastButton.getToolTipText());
        playOrPauseButton.setToolTipText(f.playOrPauseButton.getToolTipText());
        playNextButton.setToolTipText(f.nextButton.getToolTipText());
        closeButton.setToolTipText(CLOSE_TIP);

        // 图标
        playLastButton.setIcon(f.lastButton.getIcon());
        playOrPauseButton.setIcon(f.playOrPauseButton.getIcon());
        playNextButton.setIcon(f.nextButton.getIcon());
        closeButton.setIcon(ImageUtil.dye(closeMiniIcon, iconColor));

        playLastButton.addActionListener(e -> f.lastButton.doClick());
        playNextButton.addActionListener(e -> f.nextButton.doClick());
        playOrPauseButton.addActionListener(e -> f.playOrPauseButton.doClick());
        closeButton.addActionListener(e -> {
            dispose();
            f.miniDialog = null;
            if (f.showSpectrum) f.openSpectrum();
            f.setVisible(true);
            f.lyricScrollAnimation = true;
        });

        FlowLayout fl = new HDFlowLayout();
        fl.setHgap(ScaleUtil.scale(5));
        controlPanel.setLayout(fl);

        controlPanel.add(infoLabel);
        controlPanel.add(playLastButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(playNextButton);
        controlPanel.add(closeButton);

//        controlPanel.setPreferredSize(new HDDimension(300, 60));

        globalPanel.add(controlPanel, BorderLayout.CENTER);

        updateBlur();
    }
}
