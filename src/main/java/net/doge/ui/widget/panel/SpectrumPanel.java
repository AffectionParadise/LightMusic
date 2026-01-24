package net.doge.ui.widget.panel;

import lombok.Getter;
import net.doge.constant.core.ui.spectrum.SpectrumConstants;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;

public class SpectrumPanel extends JPanel {
    private final Stroke STROKE = new BasicStroke(3);
    private final int SPACE = 90;

    @Getter
    private boolean drawSpectrum;

    private MainFrame f;

    public SpectrumPanel(MainFrame f) {
        this.f = f;

        setOpaque(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int num = (getWidth() - SPACE * 2 + SpectrumConstants.BAR_GAP) / (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP);
                SpectrumConstants.barNum = Math.min(num, SpectrumConstants.NUM_BANDS);
            }
        });
    }

    public void setDrawSpectrum(boolean drawSpectrum) {
        this.drawSpectrum = drawSpectrum;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (drawSpectrum) paintSpectrum(g);
        super.paintComponent(g);
    }

    private void paintSpectrum(Graphics g) {
        double[] specs = f.player.getSpecs();
        int pw = getWidth(), ph = getHeight();
        if (pw == 0 || ph == 0) return;
        int barNum = SpectrumConstants.barNum, viewX = (pw - SpectrumConstants.BAR_WIDTH * barNum - SpectrumConstants.BAR_GAP * (barNum - 1)) / 2,
                barWidth = SpectrumConstants.BAR_WIDTH, barGap = SpectrumConstants.BAR_GAP;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f.specOpacity));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color spectrumColor = f.currUIStyle.getSpectrumColor();

        // 频谱渐变
        Color transparentColor = ColorUtil.deriveAlphaColor(spectrumColor, 0);
        GradientPaint groundGP = new GradientPaint(0, 0, spectrumColor, 0, ph, transparentColor);
        LinearGradientPaint aboveGP = new LinearGradientPaint(0, 0, 0, ph, new float[]{0, 0.5f, 1f},
                new Color[]{spectrumColor, transparentColor, ColorUtil.deriveAlphaColor(spectrumColor, 0.3f)});

        switch (f.currSpecStyle) {
            case SpectrumConstants.GROUND:
                if (f.specGradient) g2d.setPaint(groundGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    g2d.fillRoundRect(
                            viewX + i * (barWidth + barGap),
                            ph - sHeight,
                            barWidth,
                            sHeight,
                            4, 4
                    );
                }
                break;
            case SpectrumConstants.ABOVE:
                if (f.specGradient) g2d.setPaint(aboveGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    g2d.fillRoundRect(
                            viewX + i * (barWidth + barGap),
                            (ph - sHeight) / 2,
                            barWidth,
                            sHeight,
                            4, 4
                    );
                }
                break;
            case SpectrumConstants.LINE:
                // 设置画笔粗细，画线段时需要
                g2d.setStroke(STROKE);
                if (f.specGradient) g2d.setPaint(groundGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    int x1 = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int y1 = ph - sHeight;
                    int x2 = x1 + barWidth + barGap;
                    int y2 = ph - (int) specs[i + 1];
                    if (y1 == ph && y2 == ph) continue;
                    g2d.drawLine(x1, y1, x2, y2);
                }
                break;
            case SpectrumConstants.CURVE:
                // 设置画笔粗细，画线段时需要
                g2d.setStroke(STROKE);
                if (f.specGradient) g2d.setPaint(groundGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    int p1x = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int p1y = ph - sHeight;
                    int p2x = p1x + barWidth + barGap;
                    int p2y = ph - (int) specs[i + 1];
                    int p3x = (p1x + p2x) / 2;
                    if (p1y == ph && p2y == ph) continue;
                    GeneralPath path = new GeneralPath();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    g2d.draw(path);
                }
                break;
            case SpectrumConstants.HILL:
                if (f.specGradient) g2d.setPaint(groundGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    int p1x = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int p1y = ph - sHeight;
                    int p2x = p1x + barWidth + barGap;
                    int p2y = ph - (int) specs[i + 1];
                    Polygon polygon = new Polygon(new int[]{p1x, p1x, p2x, p2x}, new int[]{p1y, ph, ph, p2y}, 4);
                    g2d.fill(polygon);
                }
                break;
            case SpectrumConstants.WAVE:
                if (f.specGradient) g2d.setPaint(groundGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    int p1x = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int p1y = ph - sHeight;
                    int p2x = p1x + barWidth + barGap;
                    int p2y = ph - (int) specs[i + 1];
                    int p3x = (p1x + p2x) / 2;
                    GeneralPath path = new GeneralPath();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    path.lineTo(p2x, ph);
                    path.lineTo(p1x, ph);
                    path.closePath();
                    g2d.fill(path);
                }
                break;
            case SpectrumConstants.SYM_HILL:
                if (f.specGradient) g2d.setPaint(aboveGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    // 上半部分
                    int p1x = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int p1y = (ph - sHeight) / 2;
                    int p2x = p1x + barWidth + barGap;
                    int p2y = (ph - (int) specs[i + 1]) / 2;
                    Polygon polygon = new Polygon(new int[]{p1x, p1x, p2x, p2x}, new int[]{p1y, ph / 2, ph / 2, p2y}, 4);
                    g2d.fill(polygon);
                    // 下半部分
                    p1y = (ph + sHeight) / 2;
                    p2y = (ph + (int) specs[i + 1]) / 2;
                    polygon = new Polygon(new int[]{p1x, p1x, p2x, p2x}, new int[]{p1y, ph / 2, ph / 2, p2y}, 4);
                    g2d.fill(polygon);
                }
                break;
            case SpectrumConstants.SYM_WAVE:
                if (f.specGradient) g2d.setPaint(aboveGP);
                else g2d.setColor(spectrumColor);
                for (int i = 0; i < barNum; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) specs[i];
                    if (i + 1 >= barNum) return;
                    // 上半部分
                    int p1x = viewX + barWidth / 2 + i * (barWidth + barGap);
                    int p1y = (ph - sHeight) / 2;
                    int p2x = p1x + barWidth + barGap;
                    int p2y = (ph - (int) specs[i + 1]) / 2;
                    int p3x = (p1x + p2x) / 2;
                    GeneralPath path = new GeneralPath();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    path.lineTo(p2x, ph / 2);
                    path.lineTo(p1x, ph / 2);
                    path.closePath();
                    g2d.fill(path);
                    // 下半部分
                    p1y = (ph + sHeight) / 2;
                    p2y = (ph + (int) specs[i + 1]) / 2;
                    p3x = (p1x + p2x) / 2;
                    path.reset();
                    path.moveTo(p1x, p1y);
                    path.curveTo(p3x, p1y, p3x, p2y, p2x, p2y);
                    path.lineTo(p2x, ph / 2);
                    path.lineTo(p1x, ph / 2);
                    path.closePath();
                    g2d.fill(path);
                }
                break;
        }
    }
}
