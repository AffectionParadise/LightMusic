package net.doge.ui.component.dialog;

import lombok.Getter;
import net.doge.constant.ui.Colors;
import net.doge.constant.ui.Fonts;
import net.doge.model.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.CustomButton;
import net.doge.ui.component.button.listener.ButtonMouseListener;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.lyric.StringTwoColor;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @Author Doge
 * @Description 桌面歌词对话框
 * @Date 2021/1/5
 */
public class DesktopLyricDialog extends JDialog {
    private int width;
    private Font font = Fonts.NORMAL_HUGE;
    private final int MIN_FONT_SIZE = Fonts.HUGE_SIZE - 20;
    private final int MAX_FONT_SIZE = Fonts.HUGE_SIZE + 20;
    private Color bgColor;
    @Getter
    private String lyric;
    @Getter
    private double ratio;
    private StringTwoColor stc;

    private MainFrame f;
    private UIStyle style;
    private Color foreColor;
    private CustomPanel lyricPanel = new CustomPanel();
    public LyricLabel lyricLabel = new LyricLabel();
    private CustomLabel tempLabel = new CustomLabel();
    private MainPanel mainPanel = new MainPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    private final String LOCK_TIP = "锁定桌面歌词";
    private final String UNLOCK_TIP = "解锁桌面歌词";
    private final String RESTORE_TIP = "还原桌面歌词位置";
    private final String DESCEND_TRANS_TIP = "减少透明度";
    private final String ASCEND_TRANS_TIP = "增加透明度";
    private final String DECREASE_FONT_TIP = "缩小字体";
    private final String INCREASE_FONT_TIP = "放大字体";
    private final String ON_TOP_TIP = "置顶桌面歌词";
    private final String CANCEL_ON_TOP_TIP = "取消置顶桌面歌词";
    private final String CLOSE_TIP = "关闭桌面歌词";
    private ImageIcon lockIcon = LMIconManager.getIcon("dialog.lock");
    private ImageIcon unlockIcon = LMIconManager.getIcon("dialog.unlock");
    private ImageIcon restoreIcon = LMIconManager.getIcon("dialog.restoreLocation");
    private ImageIcon descendTransIcon = LMIconManager.getIcon("dialog.descendTrans");
    private ImageIcon ascendTransIcon = LMIconManager.getIcon("dialog.ascendTrans");
    private ImageIcon decreaseFontIcon = LMIconManager.getIcon("dialog.decreaseFont");
    private ImageIcon increaseFontIcon = LMIconManager.getIcon("dialog.increaseFont");
    private ImageIcon onTopIcon = LMIconManager.getIcon("dialog.onTop");
    private ImageIcon cancelOnTopIcon = LMIconManager.getIcon("dialog.cancelOnTop");
    private ImageIcon closeIcon = LMIconManager.getIcon("dialog.close");
    private ImageIcon emptyIcon = new ImageIcon(ImageUtil.createTransparentImage(lockIcon.getIconWidth(), lockIcon.getIconHeight()));

    private CustomButton lock = new CustomButton();
    private CustomButton restore = new CustomButton();
    private CustomButton descendTrans = new CustomButton();
    private CustomButton ascendTrans = new CustomButton();
    private CustomButton decreaseFont = new CustomButton();
    private CustomButton increaseFont = new CustomButton();
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

    public void setLyric(String lyric, double ratio) {
        this.lyric = lyric;
        this.ratio = ratio;

        tempLabel.setText(lyric);
        if (stc == null || !stc.getText().equals(lyric) || !stc.getC1().equals(foreColor) || !stc.getC2().equals(bgColor)
                || !stc.getLabelFont().equals(tempLabel.getFont()))
            stc = new StringTwoColor(tempLabel, lyric, foreColor, bgColor, ratio, true, width);
        else stc.setRatio(ratio);
        lyricLabel.setIcon(stc.getImageIcon());
        // Icon 对象可能不变，一定要手动重绘刷新！
        mainPanel.repaint();
    }

    // 更新大小
    private void updateSize() {
        FontMetrics metrics = tempLabel.getFontMetrics(font);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(width = (int) (screenSize.width * 0.5), metrics.getHeight() + 50));
    }

    // 更新位置
    private void updateLocation() {
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dx = (screenSize.width - getWidth()) / 2, dy = screenSize.height - getHeight() - insets.bottom - 15);
    }

    public DesktopLyricDialog(MainFrame f) {
        this.f = f;
        this.style = f.currUIStyle;
        foreColor = style.getHighlightColor();

        setTitle("桌面歌词");
        // 将桌面歌词窗口设置为固定大小与固定位置
        updateSize();
        updateLocation();

        // 设置主题色
        bgColor = f.currUIStyle.getLrcColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        tempLabel.setForeground(bgColor);
        tempLabel.setFont(font);
        setLyric(" ", 0);
        lyricLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        FlowLayout fl = new FlowLayout();
        fl.setHgap(10);
        buttonPanel.setLayout(fl);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(lock);
        buttonPanel.add(restore);
        buttonPanel.add(descendTrans);
        buttonPanel.add(ascendTrans);
        buttonPanel.add(decreaseFont);
        buttonPanel.add(increaseFont);
        buttonPanel.add(onTop);
        buttonPanel.add(close);

        restore.setVisible(false);
        descendTrans.setVisible(false);
        ascendTrans.setVisible(false);
        decreaseFont.setVisible(false);
        increaseFont.setVisible(false);
        onTop.setVisible(false);
        close.setVisible(false);

        lyricPanel.setLayout(new BorderLayout());
        lyricPanel.add(lyricLabel);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(lyricPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        initResponse();
    }

    // 更新锁定
    public void updateLock() {
        boolean locked = f.desktopLyricLocked;
        lock.setIcon(locked ? unlockIcon : lockIcon);
        lock.setToolTipText(locked ? UNLOCK_TIP : LOCK_TIP);
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

        unlockIcon = ImageUtil.dye(unlockIcon, bc);
        lockIcon = ImageUtil.dye(lockIcon, bc);
        restoreIcon = ImageUtil.dye(restoreIcon, bc);
        descendTransIcon = ImageUtil.dye(descendTransIcon, bc);
        ascendTransIcon = ImageUtil.dye(ascendTransIcon, bc);
        decreaseFontIcon = ImageUtil.dye(decreaseFontIcon, bc);
        increaseFontIcon = ImageUtil.dye(increaseFontIcon, bc);
        onTopIcon = ImageUtil.dye(onTopIcon, bc);
        cancelOnTopIcon = ImageUtil.dye(cancelOnTopIcon, bc);
        closeIcon = ImageUtil.dye(closeIcon, bc);

        restore.setIcon(restoreIcon);
        descendTrans.setIcon(descendTransIcon);
        ascendTrans.setIcon(ascendTransIcon);
        decreaseFont.setIcon(decreaseFontIcon);
        increaseFont.setIcon(increaseFontIcon);
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
        decreaseFont.setVisible(unlocked);
        increaseFont.setVisible(unlocked);
        onTop.setVisible(unlocked);
        close.setVisible(unlocked);
        // 设置为不穿透，防止鼠标退出事件监听过早
        setTouchOver(false);
    }

    private void hideUI() {
        lock.setIcon(emptyIcon);
        restore.setVisible(false);
        descendTrans.setVisible(false);
        ascendTrans.setVisible(false);
        decreaseFont.setVisible(false);
        increaseFont.setVisible(false);
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
                int w = getWidth(), h = getHeight();
                // 不准将桌面歌词拖出窗口之外
                if (nx < 0) nx = 0;
                else if (nx + w > ss.width) nx = ss.width - w;
                if (ny < 0) ny = 0;
                else if (ny + h > ss.height) ny = ss.height - h;
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
        lock.setPreferredSize(new Dimension(lockIcon.getIconWidth() + 10, lockIcon.getIconHeight() + 10));
        lock.addActionListener(e -> {
            f.desktopLyricLocked = !f.desktopLyricLocked;
            updateLock();
        });
        restore.setToolTipText(RESTORE_TIP);
        restore.addMouseListener(new ButtonMouseListener(restore, f));
        restore.setPreferredSize(new Dimension(restoreIcon.getIconWidth() + 10, restoreIcon.getIconHeight() + 10));
        restore.addActionListener(e -> {
            f.desktopLyricX = f.desktopLyricY = -1;
            setLocation(dx, dy);
            hideUI();
        });
        descendTrans.setToolTipText(DESCEND_TRANS_TIP);
        descendTrans.addMouseListener(new ButtonMouseListener(descendTrans, f));
        descendTrans.setPreferredSize(new Dimension(descendTransIcon.getIconWidth() + 10, descendTransIcon.getIconHeight() + 10));
        descendTrans.addActionListener(e -> lyricLabel.decreaseAlpha());
        ascendTrans.setToolTipText(ASCEND_TRANS_TIP);
        ascendTrans.addMouseListener(new ButtonMouseListener(ascendTrans, f));
        ascendTrans.setPreferredSize(new Dimension(ascendTransIcon.getIconWidth() + 10, ascendTransIcon.getIconHeight() + 10));
        ascendTrans.addActionListener(e -> lyricLabel.increaseAlpha());
        decreaseFont.setToolTipText(DECREASE_FONT_TIP);
        decreaseFont.addMouseListener(new ButtonMouseListener(decreaseFont, f));
        decreaseFont.setPreferredSize(new Dimension(decreaseFontIcon.getIconWidth() + 10, decreaseFontIcon.getIconHeight() + 10));
        decreaseFont.addActionListener(e -> decreaseFont());
        increaseFont.setToolTipText(INCREASE_FONT_TIP);
        increaseFont.addMouseListener(new ButtonMouseListener(increaseFont, f));
        increaseFont.setPreferredSize(new Dimension(increaseFontIcon.getIconWidth() + 10, increaseFontIcon.getIconHeight() + 10));
        increaseFont.addActionListener(e -> increaseFont());
        onTop.addMouseListener(new ButtonMouseListener(onTop, f));
        onTop.setPreferredSize(new Dimension(onTopIcon.getIconWidth() + 10, onTopIcon.getIconHeight() + 10));
        onTop.addActionListener(e -> {
            setAlwaysOnTop(f.desktopLyricOnTop = !f.desktopLyricOnTop);
            updateOnTop();
        });
        close.setToolTipText(CLOSE_TIP);
        close.addMouseListener(new ButtonMouseListener(close, f));
        close.setPreferredSize(new Dimension(closeIcon.getIconWidth() + 10, closeIcon.getIconHeight() + 10));
        close.addActionListener(e -> {
            f.desktopLyricButton.doClick();
        });

        updateStyle();
    }

    private void decreaseFont() {
        updateFontSize(f.desktopLyricFontSize = Math.max(MIN_FONT_SIZE, f.desktopLyricFontSize - 1));
    }

    private void increaseFont() {
        updateFontSize(f.desktopLyricFontSize = Math.min(MAX_FONT_SIZE, f.desktopLyricFontSize + 1));
    }

    public void updateFontSize(int fontSize) {
        font = font.deriveFont((float) fontSize);
        tempLabel.setFont(font);
        updateSize();
    }

    public void setAlpha(float alpha) {
        lyricLabel.setAlpha(alpha);
    }

    private class LyricLabel extends CustomLabel {
        private final float min = 0.2f;
        private final float max = 1f;
        private final float step = 0.1f;

        public void decreaseAlpha() {
            setAlpha(alpha - step);
        }

        public void increaseAlpha() {
            setAlpha(alpha + step);
        }

        public void setAlpha(float alpha) {
            if (alpha < min) alpha = min;
            else if (alpha > max) alpha = max;
            super.setAlpha(alpha);
            f.desktopLyricAlpha = alpha;
        }
    }

    private class MainPanel extends CustomPanel {
        private boolean drawBg;

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