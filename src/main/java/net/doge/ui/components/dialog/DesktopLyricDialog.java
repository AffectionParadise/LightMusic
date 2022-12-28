package net.doge.ui.components.dialog;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.StringTwoColor;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @Author yzx
 * @Description 桌面歌词对话框
 * @Date 2021/1/5
 */
public class DesktopLyricDialog extends JDialog {
    private int width;
    private Font font = Fonts.NORMAL_HUGE;
    private Color bgColor;
    private String lyric;
    private double ratio;
    private StringTwoColor stc;

    private PlayerFrame f;
    private UIStyle style;
    private Color foreColor;
    private CustomPanel lyricPanel = new CustomPanel();
    public LyricLabel lyricLabel = new LyricLabel("");
    private CustomLabel tempLabel = new CustomLabel("");
    private MainPanel mainPanel = new MainPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    private String LOCK_TIP = "锁定桌面歌词";
    private String UNLOCK_TIP = "解锁桌面歌词";
    private String RESTORE_TIP = "还原桌面歌词位置";
    private String ASCEND_TRANS_TIP = "增加透明度";
    private String DESCEND_TRANS_TIP = "减少透明度";
    private String ON_TOP_TIP = "置顶桌面歌词";
    private String CANCEL_ON_TOP_TIP = "取消置顶桌面歌词";
    private String CLOSE_TIP = "关闭桌面歌词";
    private ImageIcon lockIcon = new ImageIcon(SimplePath.ICON_PATH + "lock.png");
    private ImageIcon unlockIcon = new ImageIcon(SimplePath.ICON_PATH + "unlock.png");
    private ImageIcon restoreIcon = new ImageIcon(SimplePath.ICON_PATH + "restoreLocation.png");
    private ImageIcon descendTransIcon = new ImageIcon(SimplePath.ICON_PATH + "descendTrans.png");
    private ImageIcon ascendTransIcon = new ImageIcon(SimplePath.ICON_PATH + "ascendTrans.png");
    private ImageIcon onTopIcon = new ImageIcon(SimplePath.ICON_PATH + "onTop.png");
    private ImageIcon cancelOnTopIcon = new ImageIcon(SimplePath.ICON_PATH + "cancelOnTop.png");
    private ImageIcon closeIcon = new ImageIcon(SimplePath.ICON_PATH + "closeMedium.png");

    private CustomButton lock = new CustomButton();
    private CustomButton restore = new CustomButton();
    private CustomButton descendTrans = new CustomButton();
    private CustomButton ascendTrans = new CustomButton();
    private CustomButton onTop = new CustomButton();
    private CustomButton close = new CustomButton();

    private int dx;
    private int dy;
    // 穿透
    private boolean touchOver;

    /**
     * 设置背景色
     *
     * @param bgColor
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        setLyric(lyric, ratio);
    }

    /**
     * 设置前景色
     *
     * @param foreColor
     */
    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        setLyric(lyric, ratio);
    }

    public double getRatio() {
        return ratio;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric, double ratio) {
        this.lyric = lyric;
        this.ratio = ratio;

        tempLabel.setText(lyric);
        if (stc == null || !stc.getText().equals(lyric) || !stc.getC1().equals(foreColor) || !stc.getC2().equals(bgColor))
            stc = new StringTwoColor(tempLabel, lyric, foreColor, bgColor, ratio, true, width);
        else stc.setRatio(ratio);
        lyricLabel.setIcon(stc.getImageIcon());
        // Icon 对象可能不变，一定要手动重绘刷新！
        lyricLabel.repaint();
    }

    public DesktopLyricDialog(PlayerFrame f) {
        this.f = f;
        this.style = f.currUIStyle;
        foreColor = style.getHighlightColor();

        // 将桌面歌词窗口设置为固定大小与固定位置
//        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        FontMetrics metrics = tempLabel.getFontMetrics(font);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(new Dimension(width = (int) (screenSize.width * 0.5), metrics.getHeight() + 50));
        setLocation(dx = (screenSize.width - getWidth()) / 2, dy = screenSize.height - getHeight() - insets.bottom - 15);

        // 设置主题色
        bgColor = f.currUIStyle.getLrcColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        tempLabel.setForeground(bgColor);
        tempLabel.setFont(font);
        setLyric(" ", 0);
        lyricLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        FlowLayout fl = new FlowLayout();
        fl.setHgap(15);
        buttonPanel.setLayout(fl);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(lock);
        buttonPanel.add(restore);
        buttonPanel.add(descendTrans);
        buttonPanel.add(ascendTrans);
        buttonPanel.add(onTop);
        buttonPanel.add(close);

        restore.setVisible(false);
        descendTrans.setVisible(false);
        ascendTrans.setVisible(false);
        onTop.setVisible(false);
        close.setVisible(false);

        lyricPanel.setLayout(new BorderLayout());
        lyricPanel.add(lyricLabel);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(lyricPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        initResponse();
    }

    // 更新锁定
    public void updateLock() {
        boolean locked = f.desktopLyricLocked;
        lock.setIcon(locked ? lockIcon : unlockIcon);
        lock.setToolTipText(locked ? LOCK_TIP : UNLOCK_TIP);
    }

    // 更新置顶
    public void updateOnTop() {
        onTop.setIcon(f.desktopLyricOnTop ? cancelOnTopIcon : onTopIcon);
        onTop.setToolTipText(f.desktopLyricOnTop ? CANCEL_ON_TOP_TIP : ON_TOP_TIP);
    }

    // 更新样式
    public void updateStyle() {
        UIStyle st = f.currUIStyle;
        Color bc = st.getIconColor();

        unlockIcon = ImageUtils.dye(unlockIcon, bc);
        lockIcon = ImageUtils.dye(lockIcon, bc);
        restoreIcon = ImageUtils.dye(restoreIcon, bc);
        descendTransIcon = ImageUtils.dye(descendTransIcon, bc);
        ascendTransIcon = ImageUtils.dye(ascendTransIcon, bc);
        onTopIcon = ImageUtils.dye(onTopIcon, bc);
        cancelOnTopIcon = ImageUtils.dye(cancelOnTopIcon, bc);
        closeIcon = ImageUtils.dye(closeIcon, bc);

        restore.setIcon(restoreIcon);
        descendTrans.setIcon(descendTransIcon);
        ascendTrans.setIcon(ascendTransIcon);
        close.setIcon(closeIcon);
    }

    // 设置穿透
    public void setTouchOver(boolean touchOver) {
        this.touchOver = touchOver;
        mainPanel.repaint();
    }

    private void showUI() {
        updateLock();
        updateOnTop();
        boolean unlocked = !f.desktopLyricLocked;
        mainPanel.setDrawBg(unlocked);
        restore.setVisible(unlocked);
        descendTrans.setVisible(unlocked);
        ascendTrans.setVisible(unlocked);
        onTop.setVisible(unlocked);
        close.setVisible(unlocked);
        // 设置为不穿透，防止鼠标退出事件监听过早
        setTouchOver(false);
    }

    private void hideUI() {
        lock.setIcon(null);
        restore.setVisible(false);
        descendTrans.setVisible(false);
        ascendTrans.setVisible(false);
        onTop.setVisible(false);
        close.setVisible(false);
        mainPanel.setDrawBg(false);
        setTouchOver(true);
    }

    // 初始化响应事件
    private void initResponse() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (f.desktopLyricLocked) return;
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (f.desktopLyricLocked) return;
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point p = getLocation();
                int nx = p.x + e.getX() - origin.x, ny = p.y + e.getY() - origin.y;
                Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
                // 不准将桌面歌词拖出窗口之外
                if (nx < 0) nx = 0;
                else if (nx + getWidth() > ss.width) nx = ss.width - getWidth();
                if (ny < 0) ny = 0;
                else if (ny + getHeight() > ss.height) ny = ss.height - getHeight();
                setLocation(f.desktopLyricX = nx, f.desktopLyricY = ny);
            }
        });
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                showUI();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (mainPanel.getVisibleRect().contains(e.getPoint())) return;
                hideUI();
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                hideUI();
            }
        });
        // 从任务视图关闭桌面歌词窗口时，触发关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!isShowing()) return;
                close.doClick();
            }
        });

        lock.addMouseListener(new ButtonMouseListener(lock, f));
        lock.setPreferredSize(new Dimension(lockIcon.getIconWidth(), lockIcon.getIconHeight()));
        lock.addActionListener(e -> {
            f.desktopLyricLocked = !f.desktopLyricLocked;
            updateLock();
        });
        restore.setToolTipText(RESTORE_TIP);
        restore.addMouseListener(new ButtonMouseListener(restore, f));
        restore.setPreferredSize(new Dimension(restoreIcon.getIconWidth(), restoreIcon.getIconHeight()));
        restore.addActionListener(e -> {
            f.desktopLyricX = f.desktopLyricY = -1;
            setLocation(dx, dy);
            hideUI();
        });
        descendTrans.setToolTipText(DESCEND_TRANS_TIP);
        descendTrans.addMouseListener(new ButtonMouseListener(descendTrans, f));
        descendTrans.setPreferredSize(new Dimension(descendTransIcon.getIconWidth(), descendTransIcon.getIconHeight()));
        descendTrans.addActionListener(e -> {
            lyricLabel.decreaseAlpha();
        });
        ascendTrans.setToolTipText(ASCEND_TRANS_TIP);
        ascendTrans.addMouseListener(new ButtonMouseListener(ascendTrans, f));
        ascendTrans.setPreferredSize(new Dimension(ascendTransIcon.getIconWidth(), ascendTransIcon.getIconHeight()));
        ascendTrans.addActionListener(e -> {
            lyricLabel.increaseAlpha();
        });
        onTop.addMouseListener(new ButtonMouseListener(onTop, f));
        onTop.setPreferredSize(new Dimension(onTopIcon.getIconWidth(), onTopIcon.getIconHeight()));
        onTop.addActionListener(e -> {
            setAlwaysOnTop(f.desktopLyricOnTop = !f.desktopLyricOnTop);
            updateOnTop();
        });
        close.setToolTipText(CLOSE_TIP);
        close.addMouseListener(new ButtonMouseListener(close, f));
        close.setPreferredSize(new Dimension(closeIcon.getIconWidth(), closeIcon.getIconHeight()));
        close.addActionListener(e -> {
            f.desktopLyricButton.doClick();
        });

        updateStyle();
    }

    public void setAlpha(float alpha) {
        lyricLabel.setAlpha(alpha);
    }

    private class LyricLabel extends CustomLabel {
        private float alpha = 1;
        private final float min = 0.2f;
        private final float max = 1f;
        private final float step = 0.1f;

        public LyricLabel(String text) {
            super(text);
        }

        public void decreaseAlpha() {
            setAlpha(alpha - step);
        }

        public void increaseAlpha() {
            setAlpha(alpha + step);
        }

        public void setAlpha(float alpha) {
            if (alpha < min) alpha = min;
            else if (alpha > max) alpha = max;
            f.desktopLyricAlpha = this.alpha = alpha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g);
        }
    }

    private class MainPanel extends CustomPanel {
        private boolean drawBg;

        public MainPanel() {
            super();
        }

        public void setDrawBg(boolean drawBg) {
            this.drawBg = drawBg;
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            if (!touchOver) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Colors.BLACK);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, drawBg ? 0.2f : 0.01f));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            super.paintComponent(g);
        }
    }
}