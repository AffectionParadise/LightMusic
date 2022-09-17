package net.doge.ui.components;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.doge.constants.SpectrumConstants;
import net.doge.ui.PlayerFrame;
import net.doge.utils.ColorUtils;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

public class SpectrumPanel extends JFXPanel {
    private boolean drawSpectrum;
//    private Color currColor;
//    private int d = 0;
//    private int t = 0;

    private final Stroke stroke = new BasicStroke(3);
    private PlayerFrame f;
//    private Canvas canvas;
//    private Scene scene;
//    private GraphicsContext gc;

    public SpectrumPanel(PlayerFrame f) {
        this.f = f;
        drawSpectrum = false;

        setOpaque(false);

//        initBarGraph();
    }

//    void initBarGraph() {
//        canvas = new Canvas();
//        scene = new Scene(new Pane(canvas));
//        scene.setFill(Color.TRANSPARENT);
//        setScene(scene);
//        gc = canvas.getGraphicsContext2D();
//    }

    public void setDrawSpectrum(boolean drawSpectrum) {
        this.drawSpectrum = drawSpectrum;
        repaint();
    }

    public boolean isDrawSpectrum() {
        return drawSpectrum;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (drawSpectrum) paintSpectrum(g);
        super.paintComponent(g);
    }

    public void paintSpectrum(Graphics g) {
        double[] specs = f.getPlayer().getSpecs();
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;
//            // 获取歌词列表相对于窗口的坐标
//            Point pointLrcList = SwingUtilities.convertPoint(this, getX(), getY(), f);
//            int lrcX = pointLrcList.x - (int)(f.getWidth() * 0.4);
//        BufferedImage bufferedImage = new BufferedImage(lrcScrollPaneWidth, lrcScrollPaneHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = bufferedImage.createGraphics();
//        // 获取透明的 BufferedImage
//        BufferedImage bImageTranslucent
//                = g.getDeviceConfiguration().createCompatibleImage(lrcScrollPaneWidth, lrcScrollPaneHeight, Transparency.TRANSLUCENT);
//        g.dispose();
//        Graphics2D g = spectrumImg.createGraphics();
//        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
//        g.setColor(f.getCurrUIStyle().getSpectrumColor());
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        for (int i = 0, length = specs.length; i < length; i++) {
//            // 得到频谱高度并绘制
//            int sHeight = (int) specs[i];
//            g.fillRoundRect(
//                    i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
//                    lrcScrollPaneHeight - sHeight,
//                    SpectrumConstants.BAR_WIDTH,
//                    sHeight,
//                    4, 4
//            );
//        }
        int imgX = (pw - SpectrumConstants.BAR_WIDTH * SpectrumConstants.BAR_NUM
                - SpectrumConstants.BAR_GAP * (SpectrumConstants.BAR_NUM - 1)) / 2;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setColor(currColor);
        g2d.setColor(f.getCurrUIStyle().getSpectrumColor());
        g2d.setStroke(stroke);
//        if(canvas.getWidth()<=0)canvas.setWidth(pw);
//        if(canvas.getHeight()<=0)canvas.setHeight(ph);
//        gc.setFill(ColorUtils.javaFxColor(f.getCurrUIStyle().getSpectrumColor()));
//        gc.setEffect(null);
//        gc.clearRect(0, 0, pw, ph);
//        Glow glow = new Glow();
//        glow.setLevel(0.5);
//        gc.setEffect(glow);
        int style = f.currSpecStyle;
        for (int i = 0, length = specs.length; i < length; i++) {
            // 得到频谱高度并绘制
            int sHeight = (int) specs[i];
//            if (d + i+10 > 255) {
//                t = (t + 1) % 6;
//                d = (d + i+10) % 255;
//            }
//            gc.fillRoundRect(
//                    imgX + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
//                    ph - sHeight,
//                    SpectrumConstants.BAR_WIDTH,
//                    sHeight,
//                    4, 4
//            );
//            switch (t) {
//                case 0:
//                    currColor = new Color(255, (d + i+10) % 255, 0);
//                    break;
//                case 1:
//                    currColor = new Color(255 - ((d + i+10) % 255), 255, 0);
//                    break;
//                case 2:
//                    currColor = new Color(0, 255, (d + i+10) % 255);
//                    break;
//                case 3:
//                    currColor = new Color(0, 255 - ((d + i+10) % 255), 255);
//                    break;
//                case 4:
//                    currColor = new Color((d + i+10) % 255, 0, 255);
//                    break;
//                case 5:
//                    currColor = new Color(255, 0, 255 - ((d + i+10) % 255));
//                    break;
//            }
//            g2d.setColor(currColor);
            switch (style) {
                case SpectrumConstants.GROUND:
                    g2d.fillRoundRect(
                            imgX + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
                            ph - sHeight,
                            SpectrumConstants.BAR_WIDTH,
                            sHeight,
                            4, 4
                    );
                    break;
                case SpectrumConstants.ABOVE:
                    g2d.fillRoundRect(
                            imgX + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
                            (ph - sHeight) / 2,
                            SpectrumConstants.BAR_WIDTH,
                            sHeight,
                            4, 4
                    );
                    break;
                case SpectrumConstants.LINE:
                    if (i + 1 >= specs.length) return;
                    g2d.drawLine(
                            imgX + SpectrumConstants.BAR_WIDTH / 2 + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
                            ph - sHeight,
                            imgX + SpectrumConstants.BAR_WIDTH / 2 + (i + 1) * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
                            ph - (int) specs[i + 1]
                    );
                    break;
                case SpectrumConstants.CURVE:
                    if (i + 1 >= specs.length) return;
                    int p1x = imgX + SpectrumConstants.BAR_WIDTH / 2 + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    int p1y = ph - sHeight;
                    int p2x = imgX + SpectrumConstants.BAR_WIDTH / 2 + (i + 1) * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    int p2y = ph - (int) specs[i + 1];
                    int p3x = (p1x + p2x) / 2;
                    GeneralPath path = new GeneralPath();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    g2d.draw(path);
                    break;
                case SpectrumConstants.HILL:
                    if (i + 1 >= specs.length) return;
                    p1x = imgX + SpectrumConstants.BAR_WIDTH / 2 + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    p1y = ph - sHeight;
                    p2x = imgX + SpectrumConstants.BAR_WIDTH / 2 + (i + 1) * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    p2y = ph - (int) specs[i + 1];
                    Polygon polygon = new Polygon(new int[]{p1x, p1x, p2x, p2x}, new int[]{p1y, ph, ph, p2y}, 4);
                    g2d.fill(polygon);
                    break;
                case SpectrumConstants.WAVE:
                    if (i + 1 >= specs.length) return;
                    p1x = imgX + SpectrumConstants.BAR_WIDTH / 2 + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    p1y = ph - sHeight;
                    p2x = imgX + SpectrumConstants.BAR_WIDTH / 2 + (i + 1) * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                    p2y = ph - (int) specs[i + 1];
                    p3x = (p1x + p2x) / 2;
                    path = new GeneralPath();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    path.lineTo(p2x, ph);
                    path.lineTo(p1x, ph);
                    path.lineTo(p1x, p1y);
                    g2d.fill(path);
                    break;
            }

        }
//        d++;
//        graphics.drawImage(spectrumImg, imgX, 0, null);
    }
}
