package net.doge.ui.widget.dialog.base;

import lombok.Setter;
import net.doge.constant.core.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Doge
 * @description 抽象阴影对话框
 * @date 2021/1/5
 */
public abstract class AbstractShadowDialog extends BaseDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    protected final int pixels = ScaleUtil.scale(10);

    protected DialogPanel globalPanel = new DialogPanel();
    protected MainFrame f;

    public AbstractShadowDialog(Window owner) {
        this(owner, false);
    }

    public AbstractShadowDialog(Window owner, boolean modal) {
        super(owner, modal);
        if (owner instanceof MainFrame) this.f = (MainFrame) owner;
        init();
    }

    private void init() {
        globalPanel.setLayout(new BorderLayout());
        setContentPane(globalPanel);
    }

    public void updateBgImg() {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        BufferedImage img = f.processBgImg(dw, dh, false, true);
        globalPanel.setBgImg(img);
    }

    protected void close() {
        if (f != null) f.currDialogs.remove(this);
        dispose();
    }

    @Setter
    protected class DialogPanel extends JPanel {
        private BufferedImage bgImg;

        public DialogPanel() {
            initBorder();
        }

        public void initBorder() {
            // 阴影边框
            setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
        }

        public void eraseBorder() {
            setBorder(null);
        }

        public void setBgImg(BufferedImage bgImg) {
            this.bgImg = bgImg;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            int w = getWidth(), h = getHeight();
            if (bgImg != null) {
//            GraphicsUtil.srcOver(g2d, 0.8f);
                g2d.drawImage(bgImg, pixels, pixels, w - 2 * pixels, h - 2 * pixels, null);
            }
            // 画边框阴影
            int step = TOP_OPACITY / pixels;
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
                int arc = ScaleUtil.scale(10);
                g2d.drawRoundRect(i, i, w - i * 2, h - i * 2, arc, arc);
            }
        }
    }
}