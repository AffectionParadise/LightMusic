package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurType;
import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.CustomSlider;
import net.doge.ui.componentui.VSliderUI;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 播放速率对话框
 * @Date 2020/12/15
 */
public class RateDialog extends JDialog {
    private RateDialogPanel globalPanel = new RateDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 最大/小值
    private final int MIN_VAL = 1;
    private final int MAX_VAL = 80;

    private CustomPanel centerPanel = new CustomPanel();
    private final CustomLabel valLabel = new CustomLabel();
    private final CustomSlider slider = new CustomSlider();

    private PlayerFrame f;
    private VideoDialog d;
    private JComponent comp;
    private UIStyle style;

    // 父窗口和是否是模态，传入 OK 按钮文字，要展示的文件
    public RateDialog(PlayerFrame f, VideoDialog d, JComponent comp) {
        super(d == null ? f : d);
        this.f = f;
        this.d = d;
        this.comp = comp;
        this.style = f.getCurrUIStyle();
    }

    public void close() {
        f.currDialogs.remove(this);
        dispose();
    }

    public void showDialog() {
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                close();
            }
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();

        // 调整位置使之在按钮上方
        Point p = comp.getLocation();
        Dimension s = getSize();
        SwingUtilities.convertPointToScreen(p, comp.getParent());
        setLocation(p.x - s.width / 2 + comp.getWidth() / 2, p.y - s.height - 5);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        boolean slight = false;
        if (f.blurType != BlurType.OFF && f.getPlayer().loadedMusic()) {
            bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
            if (bufferedImage == f.getDefaultAlbumImage()) bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurType.MC)
                bufferedImage = ImageUtils.dyeRect(1, 1, ImageUtils.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurType.LG) 
                bufferedImage = ImageUtils.toGradient(bufferedImage);
        } else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
            slight = style.isPureColor();
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage, slight);
    }

    void initView() {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 标签
        valLabel.setForeground(style.getLabelColor());
        centerPanel.add(valLabel, BorderLayout.NORTH);

        // 滑动条
        slider.setFont(Fonts.NORMAL);
        slider.setForeground(style.getLabelColor());
        slider.setUI(new VSliderUI(slider, style.getSliderColor(), style.getSliderColor()));
        slider.setPreferredSize(new Dimension(35, 500));
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        slider.setMinimum(MIN_VAL);
        slider.setMaximum(MAX_VAL);
        slider.setOrientation(SwingConstants.VERTICAL);
        double rate = d == null ? f.currRate : f.currVideoRate;
        int val = (int) (rate * 10);
        valLabel.setText(String.format("%.1fx", rate).replace(".0", ""));
        slider.setValue(val);

        slider.addChangeListener(e -> {
            // 更新值
            float newVal = (float) slider.getValue() / 10;
            String txt = String.format("%.1fx", newVal).replace(".0", "");
            valLabel.setText(txt);
            if (d == null) f.getPlayer().setRate(f.currRate = newVal);
            else d.mp.setRate(f.currVideoRate = newVal);
        });

        centerPanel.add(slider, BorderLayout.CENTER);
    }

    private void doBlur(BufferedImage bufferedImage, boolean slight) {
        Dimension size = getSize();
        int dw = size.width, dh = size.height;
        try {
            // 截取中间的一部分(有的图片是长方形)
            bufferedImage = ImageUtils.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            bufferedImage = ImageUtils.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtils.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            if (slight) {
                bufferedImage = ImageUtils.slightDarker(bufferedImage);
            } else {
                if (f.blurType == BlurType.GS) bufferedImage = ImageUtils.doBlur(bufferedImage);
                bufferedImage = ImageUtils.darker(bufferedImage);
            }
            // 放大至窗口大小
            bufferedImage = dw > dh ? ImageUtils.width(bufferedImage, dw) : ImageUtils.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (f.blurType == BlurType.GS) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(dw > dh ? 0 : (iw - dw) / 2, dw > dh ? (ih - dh) / 2 : 0, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtils.forceSize(bufferedImage, dw, dh);
            }
            // 设置圆角
            bufferedImage = ImageUtils.setRadius(bufferedImage, 10);
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class RateDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public RateDialogPanel() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}
