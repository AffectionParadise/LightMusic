package net.doge.ui.components.dialog;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.StringTwoColor;
import net.doge.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Author yzx
 * @Description 桌面歌词对话框
 * @Date 2021/1/5
 */
public class DesktopLyricDialog1 extends JDialog {
    private int width;
    private int height;
    private Font font = Fonts.NORMAL_HUGE;
    private Color themeColor;
    private String lyric;
    private double ratio;
    private StringTwoColor stc;

    private PlayerFrame f;
    private Color foreColor;
    private JFXPanel lyricPanel = new JFXPanel();
    private JPanel mainPanel = new JPanel();
    private JLabel tempLabel = new JLabel();

    private Text text;

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

        if (text == null) {
            DropShadow ds = new DropShadow();
            ds.setRadius(3);

            text = new Text(lyric);
            try {
                text.setFont(javafx.scene.text.Font.loadFont(new FileInputStream(SimplePath.FONT_PATH + Fonts.NORMAL_BOLD_NAME), Fonts.HUGE_SIZE));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            text.setEffect(ds);
            // 字体抗锯齿
            text.setSmooth(true);
            text.setFontSmoothingType(FontSmoothingType.GRAY);
        } else text.setText(lyric);
        Stop[] stops = new Stop[]{new Stop(0, ColorUtils.javaFxColor(foreColor)), new Stop(ratio, ColorUtils.javaFxColor(themeColor))};
        LinearGradient linear = new LinearGradient(ratio - 0.001, 0, ratio, 0, true, CycleMethod.NO_CYCLE, stops);
        text.setFill(linear);
    }

    public DesktopLyricDialog1(PlayerFrame f) {
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
        setLyric(" ", 0);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout());

        StackPane pane = new StackPane(text);
        Scene scene = new Scene(pane, width, height);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        lyricPanel.setScene(scene);
        lyricPanel.setOpaque(false);
        mainPanel.add(lyricPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // 桌面歌词总在最顶层
        setAlwaysOnTop(true);
    }

    public static void main(String[] args) {
        DesktopLyricDialog1 test = new DesktopLyricDialog1(null);
        test.setLyric("我一直在挥霍着时间", 0.3);
    }
}