//package net.doge.ui.componentui;
//
//import net.doge.ui.components.dialog.ColorChooserDialog;
//
//import javax.swing.*;
//import javax.swing.plaf.basic.BasicSliderUI;
//import java.awt.*;
//import java.awt.event.MouseEvent;
//
///**
// * @Author yzx
// * @Description 颜色垂直滑动条自定义 UI
// * @Date 2020/12/13
// */
//public class ColorVSliderUI extends BasicSliderUI {
//    private ColorChooserDialog d;
//
//    private final float[] ratios = {0, 0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f, 1};
//    private final Color[] colors = {Color.RED, Color.PINK, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED};
//
//    public ColorVSliderUI(JSlider slider, ColorChooserDialog d) {
//        super(slider);
//        this.d = d;
//    }
//
//    /**
//     * 自定义把手
//     *
//     * @param g
//     */
//    @Override
//    public void paintThumb(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//        // 避免锯齿
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        g2d.setColor(d.makeColor(d.r, d.g, d.b));
//        g2d.fillOval(thumbRect.x + 4, thumbRect.y - 2, thumbRect.width - 4, thumbRect.width - 4);
//    }
//
//    /**
//     * 自定义滑道
//     *
//     * @param g
//     */
//    @Override
//    public void paintTrack(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//        // 避免锯齿
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        LinearGradientPaint lgp = new LinearGradientPaint(trackRect.x, trackRect.y, trackRect.x, trackRect.y + trackRect.height, ratios, colors);
//        g2d.setPaint(lgp);
//        g2d.fillRoundRect(
//                trackRect.x + 9,
//                trackRect.y,
//                trackRect.width - 7,
//                trackRect.height, 4, 4
//        );
//    }
//
//    @Override
//    protected TrackListener createTrackListener(JSlider s) {
//        return new TrackListener() {
//            private void update(MouseEvent e) {
//                s.setValue((int) (s.getMinimum() + (double) (trackRect.height - e.getY() + trackRect.y) / trackRect.height * (s.getMaximum() - s.getMinimum())));
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//                update(e);
//            }
//
//            // 拖动时重绘，避免滑块重叠绘制！
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                update(e);
//            }
//        };
//    }
//}
