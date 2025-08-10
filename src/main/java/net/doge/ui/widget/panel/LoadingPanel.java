package net.doge.ui.widget.panel;

import lombok.Getter;
import net.doge.constant.ui.Fonts;
import net.doge.ui.MainFrame;
import net.doge.util.common.StringUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;

/**
 * @Author Doge
 * @Description loading 面板
 * @Date 2020/12/21
 */
public class LoadingPanel extends JComponent implements MouseListener {
    private static final long serialVersionUID = 1L;
    protected Area[] ticker = null;
    protected Thread animation = null;
    protected boolean started = false;
    protected int alphaLevel = 254;
    protected int rampDelay;
    protected float shield;
    @Getter
    protected String text;
    protected int barsCount;
    protected float fps;
    protected Font font = Fonts.NORMAL_BIG;

    private MainFrame f;

    protected RenderingHints hints;

    public LoadingPanel(MainFrame f) {
        this("");
        this.f = f;
    }

    public LoadingPanel(String text) {
        this(text, 14);
    }

    public LoadingPanel(String text, int barsCount) {
        this(text, barsCount, 0.4f);
    }

    public LoadingPanel(String text, int barsCount, float shield) {
        this(text, barsCount, shield, 15);
    }

    public LoadingPanel(String text, int barsCount, float shield, float fps) {
        this(text, barsCount, shield, fps, 200);
    }

    public LoadingPanel(String text, int barsCount, float shield, float fps, int rampDelay) {
        this.text = text;
        this.rampDelay = Math.max(rampDelay, 0);
        this.shield = Math.max(shield, 0);
        this.fps = fps > 0 ? fps : 15;
        this.barsCount = barsCount > 0 ? barsCount : 14;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    public void setText(String text) {
        repaint();
        this.text = text;
    }

    public void start() {
        if (isShowing()) return;
        addMouseListener(this);
        setVisible(true);
        ticker = buildTicker();
        animation = new Thread(new Animator(true));
        animation.start();
    }

    public void stop() {
        if (animation == null) return;
        animation.stop();
        animation = null;
        animation = new Thread(new Animator(false));
        animation.start();
    }

//    public void interrupt() {
//        if (animation == null) return;
//        animation.interrupt();
//        animation = null;
//
//        removeMouseListener(this);
//        setVisible(false);
//    }

    @Override
    public void paintComponent(Graphics g) {
        if (!started) return;
        int width = getWidth();
        int height = getHeight();

        double maxY = 0;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(hints);

        g2.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg(), shield, false));
        g2.fillRect(0, 0, width, height);

        for (int i = 0; i < ticker.length; i++) {
            int channel = 224 - 128 / (i + 1);
            g2.setColor(new Color(channel, channel, channel, alphaLevel));
            g2.fill(ticker[i]);

            Rectangle2D bounds = ticker[i].getBounds2D();
            if (bounds.getMaxY() > maxY) {
                maxY = bounds.getMaxY();
            }
        }

        if (StringUtil.notEmpty(text)) {
            FontRenderContext context = g2.getFontRenderContext();
//                TextLayout layout = new TextLayout(text, getFont(), context);
            // 自定义字体
            TextLayout layout = new TextLayout(text, font, context);
            Rectangle2D bounds = layout.getBounds();
            g2.setColor(f.currUIStyle.getTextColor());
            layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
                    (float) (maxY + layout.getLeading() + 2 * layout.getAscent()));
        }
    }

    private Area[] buildTicker() {
        Area[] ticker = new Area[barsCount];
        Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
        double fixedAngle = 2 * Math.PI / (barsCount);

        for (double i = 0; i < barsCount; i++) {
            Area primitive = buildPrimitive();

            AffineTransform toCenter = AffineTransform.getTranslateInstance(center.getX(), center.getY());
            AffineTransform toBorder = AffineTransform.getTranslateInstance(45, -6);
            AffineTransform toCircle = AffineTransform.getRotateInstance(-i * fixedAngle, center.getX(), center.getY());

            AffineTransform toWheel = new AffineTransform();
            toWheel.concatenate(toCenter);
            toWheel.concatenate(toBorder);

            primitive.transform(toWheel);
            primitive.transform(toCircle);

            ticker[(int) i] = primitive;
        }

        return ticker;
    }

    private Area buildPrimitive() {
        Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Area tick = new Area(body);
        tick.add(new Area(head));
        tick.add(new Area(tail));

        return tick;
    }

    protected class Animator implements Runnable {
        private boolean rampUp = true;

        protected Animator(boolean rampUp) {
            this.rampUp = rampUp;
        }

        @Override
        public void run() {
            Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
            double fixedIncrement = 2.0 * Math.PI / (barsCount);
            AffineTransform toCircle = AffineTransform.getRotateInstance(fixedIncrement, center.getX(), center.getY());

            long start = System.currentTimeMillis();
            if (rampDelay == 0) {
                alphaLevel = rampUp ? 255 : 0;
            }

            started = true;
            boolean inRamp = rampUp;

            while (!Thread.interrupted()) {
                if (!inRamp) {
                    for (int i = 0; i < ticker.length; i++) {
                        ticker[i].transform(toCircle);
                    }
                }

                repaint();

                if (rampUp) {
                    if (alphaLevel < 255) {
                        alphaLevel = (int) (255 * (System.currentTimeMillis() - start) / rampDelay);
                        if (alphaLevel >= 255) {
                            alphaLevel = 255;
                            inRamp = false;
                        }
                    }
                } else if (alphaLevel > 0) {
                    alphaLevel = (int) (255 - (255 * (System.currentTimeMillis() - start) / rampDelay));
                    if (alphaLevel <= 0) {
                        alphaLevel = 0;
                        break;
                    }
                }

                try {
                    Thread.sleep(inRamp ? 10 : (int) (1000 / fps));
                } catch (InterruptedException ie) {
                    break;
                }
                Thread.yield();
            }

            if (!rampUp) {
                started = false;
                repaint();

                setVisible(false);
                removeMouseListener(LoadingPanel.this);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        // ...
//        LoadingPanel glasspane = new LoadingPanel((MainFrame) null);
//
//        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
//        glasspane.setBounds(100, 100, (dimension.width) / 2, (dimension.height) / 2);
//        frame.setGlassPane(glasspane);
//        glasspane.setText("Loading data, Please wait ...");
//        glasspane.start();//开始动画加载效果
//        frame.setVisible(true);
//
//        // Later, to disable,在合适的地方关闭动画效果
////      glasspane.stop();
//    }
}
