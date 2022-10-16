package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.*;
import net.doge.models.CommonResult;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.components.CustomTextField;
import net.doge.ui.components.SafeDocument;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.ListUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author yzx
 * @Description 图片浏览的对话框
 * @Date 2020/12/15
 */
public abstract class ImageViewDialog extends JDialog {
    private final String TITLE = "图片预览";
    private final int WIDTH = 1000;
    private final int HEIGHT = 850;
    // 加载图片提示
    private final String LOADING_IMG_MSG = "请稍候，图片加载中......";
    // 已经是第一张提示
    private final String FIRST_PAGE_MSG = "已经是第一张了";
    // 已经是最后一张提示
    private final String LAST_PAGE_MSG = "已经是最后一张了";
    // 非法页码提示
    private final String ILLEGAL_PAGE_MSG = "请输入合法页码";
    private ImageViewDialogPanel globalPanel = new ImageViewDialogPanel();

    private final String LAST_IMG = "上一张";
    private final String NEXT_IMG = "下一张";
    private final String FIRST_IMG = "第一张";
    private final String LST_IMG = "最后一张";
    private final String SAVE_IMG = "保存图片";
    private final String GO_TIP = "跳页";

    // 上一张图标
    private ImageIcon lastImgIcon = new ImageIcon(SimplePath.ICON_PATH + "lastPage.png");
    // 下一张图标
    private ImageIcon nextImgIcon = new ImageIcon(SimplePath.ICON_PATH + "nextPage.png");
    // 第一张图标
    private ImageIcon firstImgIcon = new ImageIcon(SimplePath.ICON_PATH + "firstImg.png");
    // 最后一张图标
    private ImageIcon lstImgIcon = new ImageIcon(SimplePath.ICON_PATH + "lastImg.png");
    // 保存图片图标
    private ImageIcon saveImgIcon = new ImageIcon(SimplePath.ICON_PATH + "saveImg.png");
    // 跳页图标
    private ImageIcon goIcon = new ImageIcon(SimplePath.ICON_PATH + "go.png");

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private JPanel centerPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    private JLabel imgLabel = new JLabel("", JLabel.CENTER);
    public CustomButton lastImgButton = new CustomButton(lastImgIcon);
    private JLabel pageLabel = new JLabel();
    // 页数框
    public CustomTextField pageTextField = new CustomTextField(3);
    // 跳页按钮
    private CustomButton goButton = new CustomButton(goIcon);
    public CustomButton nextImgButton = new CustomButton(nextImgIcon);
    private CustomButton saveImgButton = new CustomButton(saveImgIcon);
    public CustomButton firstImgButton = new CustomButton(firstImgIcon);
    private CustomButton lstImgButton = new CustomButton(lstImgIcon);

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.Y_AXIS);

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;
    private CommonResult<String> results;
    private List<String> cursors = new LinkedList<>();
    private int p = 1;

    private int pn = 1;
    private int limit;

    // 父窗口和是否是模态
    public ImageViewDialog(PlayerFrame f, int limit) {
        super(f, true);
        this.f = f;
        this.style = f.getCurrUIStyle();
        this.limit = limit;
    }

    public void showDialog() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });

        setTitle(TITLE);
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        TipDialog dialog = new TipDialog(f, LOADING_IMG_MSG, 0, false);
        dialog.showDialog();
        // 组装界面
        initView();
        dialog.close();

//        loadHotKeyListener();

        add(globalPanel, BorderLayout.CENTER);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setLocationRelativeTo(f);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        boolean slight = false;
        if (f.getIsBlur() && f.getPlayer().loadedMusic()) bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
        else {
            UIStyle style = f.getCurrUIStyle();
            bufferedImage = style.getImg();
            slight = style.isPureColor();
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage, slight);
    }

    // 初始化标题栏
    void initTitleBar() {
        titleLabel.setForeground(style.getLabelColor());
        titleLabel.setOpaque(false);
        titleLabel.setFont(globalFont);
        titleLabel.setText(TITLE);
        closeButton.setIcon(ImageUtils.dye(closeWindowIcon, style.getButtonColor()));
        closeButton.setPreferredSize(new Dimension(closeWindowIcon.getIconWidth() + 2, closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            dispose();
            f.currDialogs.remove(this);
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        // 不能聚焦
        closeButton.setFocusable(false);
        // 无填充
        closeButton.setContentAreaFilled(false);
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(40, 30));
        windowCtrlPanel.add(closeButton);
        windowCtrlPanel.setOpaque(false);
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    // 组装界面
    void initView() {
        // 容器透明
        globalPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        bottomPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(bottomBox, BorderLayout.SOUTH);

        // 标签
        imgLabel.setFont(globalFont);
        pageLabel.setFont(globalFont);
        imgLabel.setForeground(style.getLabelColor());
        pageLabel.setForeground(style.getLabelColor());

        // 上/下一张按钮
        lastImgButton.setToolTipText(LAST_IMG);
        lastImgButton.setFocusPainted(false);
        lastImgButton.setOpaque(false);
        lastImgButton.setContentAreaFilled(false);
        lastImgButton.setIcon(ImageUtils.dye((ImageIcon) lastImgButton.getIcon(), style.getButtonColor()));
        lastImgButton.addMouseListener(new ButtonMouseListener(lastImgButton, f));
        lastImgButton.setPreferredSize(new Dimension(lastImgIcon.getIconWidth(), lastImgIcon.getIconHeight()));
        lastImgButton.addActionListener(e -> {
            if (p == 1) {
                new TipDialog(f, FIRST_PAGE_MSG).showDialog();
                return;
            }
            showImg(--p);
        });
        nextImgButton.setToolTipText(NEXT_IMG);
        nextImgButton.setFocusPainted(false);
        nextImgButton.setOpaque(false);
        nextImgButton.setContentAreaFilled(false);
        nextImgButton.setIcon(ImageUtils.dye((ImageIcon) nextImgButton.getIcon(), style.getButtonColor()));
        nextImgButton.addMouseListener(new ButtonMouseListener(nextImgButton, f));
        nextImgButton.setPreferredSize(new Dimension(nextImgIcon.getIconWidth(), nextImgIcon.getIconHeight()));
        nextImgButton.addActionListener(e -> {
            if (results == null) return;
            if (p >= results.total) {
                new TipDialog(f, LAST_PAGE_MSG).showDialog();
                return;
            }
            showImg(++p);
        });
        // 第一张/最后一张按钮
        firstImgButton.setToolTipText(FIRST_IMG);
        firstImgButton.setFocusPainted(false);
        firstImgButton.setOpaque(false);
        firstImgButton.setContentAreaFilled(false);
        firstImgButton.setIcon(ImageUtils.dye((ImageIcon) firstImgButton.getIcon(), style.getButtonColor()));
        firstImgButton.addMouseListener(new ButtonMouseListener(firstImgButton, f));
        firstImgButton.setPreferredSize(new Dimension(firstImgIcon.getIconWidth(), firstImgIcon.getIconHeight()));
        firstImgButton.addActionListener(e -> {
            if (p == 1) {
                new TipDialog(f, FIRST_PAGE_MSG).showDialog();
                return;
            }
            showImg(p = 1);
        });
        lstImgButton.setToolTipText(LST_IMG);
        lstImgButton.setFocusPainted(false);
        lstImgButton.setOpaque(false);
        lstImgButton.setContentAreaFilled(false);
        lstImgButton.setIcon(ImageUtils.dye((ImageIcon) lstImgButton.getIcon(), style.getButtonColor()));
        lstImgButton.addMouseListener(new ButtonMouseListener(lstImgButton, f));
        lstImgButton.setPreferredSize(new Dimension(lstImgIcon.getIconWidth(), lstImgIcon.getIconHeight()));
        lstImgButton.addActionListener(e -> {
            if (results == null) return;
            if (p >= results.total) {
                new TipDialog(f, LAST_PAGE_MSG).showDialog();
                return;
            }
            showImg(p = results.total);
        });
        // 页数框
        pageTextField.setDocument(new SafeDocument(0, Integer.MAX_VALUE));
        pageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    goButton.doClick();
                }
            }
        });
        pageTextField.setFont(globalFont);
        pageTextField.setOpaque(false);
        pageTextField.setForeground(style.getForeColor());
        pageTextField.setCaretColor(style.getForeColor());
        // 跳页按钮
        goButton.setToolTipText(GO_TIP);
        goButton.setFocusPainted(false);
        goButton.setOpaque(false);
        goButton.setContentAreaFilled(false);
        goButton.setIcon(ImageUtils.dye((ImageIcon) goButton.getIcon(), style.getButtonColor()));
        goButton.addMouseListener(new ButtonMouseListener(goButton, f));
        goButton.setPreferredSize(new Dimension(goIcon.getIconWidth(), goIcon.getIconHeight()));
        goButton.addActionListener(e -> {
            if (results == null) return;
            String text = pageTextField.getText();
            pageTextField.setText("");
            if ("".equals(text)) return;
            int destPage = Integer.parseInt(text);
            if (destPage < 1 || destPage > results.total) {
                new TipDialog(f, ILLEGAL_PAGE_MSG).showDialog();
                return;
            }
            showImg(p = destPage);
        });

        // 保存图片
        saveImgButton.setToolTipText(SAVE_IMG);
        saveImgButton.setFocusPainted(false);
        saveImgButton.setOpaque(false);
        saveImgButton.setContentAreaFilled(false);
        saveImgButton.setIcon(ImageUtils.dye((ImageIcon) saveImgButton.getIcon(), style.getButtonColor()));
        saveImgButton.addMouseListener(new ButtonMouseListener(saveImgButton, f));
        saveImgButton.setPreferredSize(new Dimension(saveImgIcon.getIconWidth(), saveImgIcon.getIconHeight()));
        saveImgButton.addActionListener(e -> {
            saveImg(p);
        });

        centerPanel.add(imgLabel, BorderLayout.CENTER);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(20);
        bottomPanel.setLayout(fl);
        bottomPanel.add(firstImgButton);
        bottomPanel.add(lastImgButton);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextImgButton);
        bottomPanel.add(lstImgButton);
        bottomPanel.add(pageTextField);
        bottomPanel.add(goButton);
        bottomPanel.add(saveImgButton);
        bottomBox.add(bottomPanel);
        bottomBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        cursors.add("");
        // 默认显示第一张
        GlobalExecutors.imageExecutor.submit(() -> {
            if (!showImg(p)) {
                requestFailed();
                closeButton.doClick();
            }
        });
    }

    // 显示第 i 张图片
    boolean showImg(int i) {
        BufferedImage img = getImg(i);
        pageLabel.setText(String.format("%s / %s", i, results.total));
        if (img == null) {
            imgLabel.setIcon(null);
            imgLabel.setText("图片走丢了T_T");
            return false;
        }
        // 调整图像大小适应窗口
        int w = img.getWidth(), mw = WIDTH - 50, mh = HEIGHT - 150;
        if (w > mw) img = ImageUtils.width(img, mw);
        int h = img.getHeight();
        if (h > mh) img = ImageUtils.height(img, mh);
        imgLabel.setIcon(new ImageIcon(img));
        imgLabel.setText("");
        return true;
    }

    // 请求图片
    public abstract CommonResult<String> requestImgUrls(int pn, int limit, String cursor);

    // 第一次请求图片失败时调用
    public abstract void requestFailed();

    // 获取第 i 张图片
    BufferedImage getImg(int i) {
        try {
            // 请求指定页数的图片
            int dp = i % limit == 0 ? i / limit : i / limit + 1, di = (i - 1) % limit;
            if (results == null || pn != dp) {
                String cursor = results == null || cursors.size() == 1 ? "" : cursors.get(dp - 1);
                results = requestImgUrls(pn = dp, limit, cursor);
                String next = results.cursor;
                if (ListUtils.search(cursors, next) < 0) cursors.add(next);
            }
            String url = results.data.get(di);
            BufferedImage img = ImageUtils.read(new URL(url));
            // 单独处理 Webp 类型图片
//            if (img == null) img = ImageUtils.readWebp(url);
            return img;
        } catch (Exception e) {
            return null;
        }
    }

    // 导出第 i 张图片
    void saveImg(int i) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存图片");
        ObservableList<FileChooser.ExtensionFilter> filters = fileChooser.getExtensionFilters();
        // 添加可保存的图片格式
        for (String suffix : Format.WRITE_IMAGE_TYPE_SUPPORTED) {
            filters.add(new FileChooser.ExtensionFilter(suffix.toUpperCase(), "*." + suffix));
        }
        Platform.runLater(() -> {
            File outputFile = fileChooser.showSaveDialog(null);
            if (outputFile != null) {
                ImageUtils.toFile(getImg(i), outputFile);
            }
        });
    }

//    // 全局热键监听器
//    void loadHotKeyListener() {
//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//        toolkit.addAWTEventListener(event -> {
//            if (event instanceof KeyEvent) {
//                KeyEvent kE = (KeyEvent) event;
//                if (kE.getID() == KeyEvent.KEY_RELEASED && isShowing() && !pageTextField.hasFocus()) {
//                    if (kE.getKeyCode() == KeyEvent.VK_LEFT) {
//                        lastImgButton.doClick();
//                    } else if (kE.getKeyCode() == KeyEvent.VK_RIGHT) {
//                        nextImgButton.doClick();
//                    }
//                }
//            }
//        }, AWTEvent.KEY_EVENT_MASK);
//    }

    void doBlur(BufferedImage bufferedImage, boolean slight) {
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
            bufferedImage = slight ? ImageUtils.slightDarker(bufferedImage) : ImageUtils.darker(ImageUtils.doBlur(bufferedImage));
            // 放大至窗口大小
            bufferedImage = dw > dh ? ImageUtils.width(bufferedImage, dw) : ImageUtils.height(bufferedImage, dh);
            int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
            // 裁剪中间的一部分
            bufferedImage = Thumbnails.of(bufferedImage)
                    .scale(1f)
                    .sourceRegion(dw > dh ? 0 : (iw - dw) / 2, dw > dh ? (ih - dh) / 2 : 0, dw, dh)
                    .outputQuality(0.1)
                    .asBufferedImage();
            // 设置圆角
            bufferedImage = ImageUtils.setRadius(bufferedImage, 10);
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private class ImageViewDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public ImageViewDialogPanel() {
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
