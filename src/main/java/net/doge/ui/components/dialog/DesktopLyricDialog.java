package net.doge.ui.components.dialog;

import javafx.util.Duration;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.components.StringTwoColor;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
//import sun.font.FontDesignMetrics;

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
    private int height;
    private Font font = Fonts.NORMAL_HUGE;
    private Color themeColor;
    private String lyric;
    private double ratio;
    private StringTwoColor stc;

    private PlayerFrame f;
    private UIStyle style;
    private Color foreColor;
    private JLabel lyricLabel = new JLabel("", JLabel.CENTER);
    private JLabel tempLabel = new JLabel("", JLabel.CENTER);
    private MainPanel mainPanel = new MainPanel();
    private JPanel buttonPanel = new JPanel();

    private String LOCK_TIP = "锁定桌面歌词";
    private String UNLOCK_TIP = "解锁桌面歌词";
    private String RESTORE_TIP = "还原桌面歌词位置";
    private ImageIcon lockIcon = new ImageIcon(SimplePath.ICON_PATH + "lock.png");
    private ImageIcon unlockIcon = new ImageIcon(SimplePath.ICON_PATH + "unlock.png");
    private ImageIcon restoreIcon = new ImageIcon(SimplePath.ICON_PATH + "restoreLocation.png");

    private CustomButton lock = new CustomButton();
    private CustomButton restore = new CustomButton();

    private int dx;
    private int dy;
    // 穿透
    private boolean touchOver;

    /**
     * 设置背景色
     *
     * @param themeColor
     */
    public void setThemeColor(Color themeColor) {
        this.themeColor = themeColor;
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
        if (stc == null || !stc.getTxt().equals(lyric) || !stc.getC1().equals(foreColor) || !stc.getC2().equals(themeColor))
            stc = new StringTwoColor(tempLabel, foreColor, themeColor, ratio, true, width);
        else stc.setRatio(ratio);
        lyricLabel.setIcon(stc.getImageIcon());
        // Icon 对象可能不变，一定要手动重绘刷新！
        lyricLabel.repaint();
    }

    public DesktopLyricDialog(PlayerFrame f) {
        this.f = f;
        this.style = f.getCurrUIStyle();
        foreColor = style.getHighlightColor();

        // 将桌面歌词窗口设置为固定大小与固定位置
//        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        FontMetrics metrics = tempLabel.getFontMetrics(font);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(new Dimension(width = (int) (screenSize.width * 0.5), height = metrics.getHeight() + 50));
        setLocation(dx = (screenSize.width - getWidth()) / 2, dy = screenSize.height - getHeight() - insets.bottom - 20);

        // 设置主题色
        themeColor = f.getCurrUIStyle().getLrcColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        tempLabel.setForeground(themeColor);
        tempLabel.setFont(font);
        setLyric(" ", 0);
        lyricLabel.setVerticalAlignment(SwingConstants.CENTER);
        lyricLabel.setVerticalTextPosition(SwingConstants.CENTER);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(15);
        buttonPanel.setLayout(fl);
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(lock);
        buttonPanel.add(restore);

        restore.setVisible(false);

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(lyricLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        initResponse();

        // 桌面歌词总在最顶层
        setAlwaysOnTop(true);
    }

    // 更新锁定
    public void updateLock() {
        boolean locked = f.desktopLyricLocked;
        lock.setIcon(locked ? lockIcon : unlockIcon);
        lock.setToolTipText(locked ? LOCK_TIP : UNLOCK_TIP);
    }

    // 更新样式
    public void updateStyle() {
        UIStyle st = f.getCurrUIStyle();
        Color bc = st.getButtonColor();

        unlockIcon = ImageUtils.dye(unlockIcon, bc);
        lockIcon = ImageUtils.dye(lockIcon, bc);
        restoreIcon = ImageUtils.dye(restoreIcon, bc);

        restore.setIcon(restoreIcon);
    }

    // 设置穿透
    public void setTouchOver(boolean touchOver) {
        this.touchOver = touchOver;
        mainPanel.repaint();
    }

    void showUI() {
        updateLock();
        mainPanel.setDrawBg(!f.desktopLyricLocked);
        restore.setVisible(!f.desktopLyricLocked);
        // 设置为不穿透，防止鼠标退出事件监听过早
        setTouchOver(false);
    }

    void hideUI() {
        lock.setIcon(null);
        restore.setVisible(false);
        mainPanel.setDrawBg(false);
        setTouchOver(true);
    }

    // 初始化响应事件
    void initResponse() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (f.desktopLyricLocked) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (f.desktopLyricLocked) return;
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

        lock.setBorder(null);
        lock.setFocusable(false);
        lock.setOpaque(false);
        lock.setContentAreaFilled(false);
        lock.addMouseListener(new ButtonMouseListener(lock, f));
        lock.setPreferredSize(new Dimension(lockIcon.getIconWidth(), lockIcon.getIconHeight()));
        lock.addActionListener(e -> {
            f.desktopLyricLocked = !f.desktopLyricLocked;
            updateLock();
        });
        restore.setBorder(null);
        restore.setFocusable(false);
        restore.setOpaque(false);
        restore.setContentAreaFilled(false);
        restore.setToolTipText(RESTORE_TIP);
        restore.addMouseListener(new ButtonMouseListener(restore, f));
        restore.setPreferredSize(new Dimension(restoreIcon.getIconWidth(), restoreIcon.getIconHeight()));
        restore.addActionListener(e -> {
            f.desktopLyricX = f.desktopLyricY = -1;
            setLocation(dx, dy);
            hideUI();
        });

        updateStyle();
    }

    private class MainPanel extends JPanel {
        private boolean drawBg;

        public MainPanel() {
            super();
        }

        public void setDrawBg(boolean drawBg) {
            this.drawBg = drawBg;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            if (!touchOver) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Colors.BLACK);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, drawBg ? 0.2f : 0.01f));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            super.paint(g);
        }
    }

    public static void main(String[] args) {
        DesktopLyricDialog test = new DesktopLyricDialog(null);
        test.setLyric("我一直在挥霍着时间", 0.3);
    }
}