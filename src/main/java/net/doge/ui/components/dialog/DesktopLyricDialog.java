package net.doge.ui.components.dialog;

import net.doge.constants.Fonts;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.StringTwoColor;
//import sun.font.FontDesignMetrics;

import javax.swing.*;
import java.awt.*;

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
    private Color foreColor;
    private JLabel lyricLabel = new JLabel("", JLabel.CENTER);
    private JLabel tempLabel = new JLabel("", JLabel.CENTER);
    private JPanel mainPanel = new JPanel();

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
        foreColor = f.getCurrUIStyle().getHighlightColor();

        // 将桌面歌词窗口设置为固定大小与固定位置
//        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        FontMetrics metrics = tempLabel.getFontMetrics(font);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(width = screenSize.width - 400, height = metrics.getHeight()));
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2 + 440);

        // 设置主题色
        themeColor = f.getCurrUIStyle().getLrcColor();
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        tempLabel.setForeground(themeColor);
        tempLabel.setFont(font);
        tempLabel.setOpaque(false);
        setLyric(" ", 0);
        lyricLabel.setOpaque(false);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(lyricLabel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // 桌面歌词总在最顶层
        setAlwaysOnTop(true);
    }

    public static void main(String[] args) {
        DesktopLyricDialog test = new DesktopLyricDialog(null);
        test.setLyric("我一直在挥霍着时间", 0.3);
    }
}