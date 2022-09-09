package net.doge.ui.components;

import net.doge.constants.SpectrumConstants;
import net.doge.ui.PlayerFrame;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SpectrumPanel extends JPanel {
    private boolean drawSpectrum;
//    private Color currColor;
//    private int d = 0;
//    private int t = 0;

    private PlayerFrame f;

    public SpectrumPanel(PlayerFrame f) {
        this.f = f;
        drawSpectrum = false;

        setOpaque(false);
    }

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
        for (int i = 0, length = specs.length; i < length; i++) {
            // 得到频谱高度并绘制
            int sHeight = (int) specs[i];
//            if (d + i+10 > 255) {
//                t = (t + 1) % 6;
//                d = (d + i+10) % 255;
//            }
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
            g2d.fillRoundRect(
                    imgX + i * (SpectrumConstants.BAR_WIDTH + SpectrumConstants.BAR_GAP),
                    ph - sHeight,
                    SpectrumConstants.BAR_WIDTH,
                    sHeight,
                    4, 4
            );
        }
//        d++;
//        graphics.drawImage(spectrumImg, imgX, 0, null);
    }
}
