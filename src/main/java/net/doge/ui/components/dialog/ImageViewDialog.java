package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.doge.constants.Colors;
import net.doge.constants.Format;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.SimplePath;
import net.doge.models.CommonResult;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.components.button.CustomButton;
import net.doge.ui.components.dialog.factory.AbstractTitledDialog;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.ui.components.textfield.CustomTextField;
import net.doge.ui.components.textfield.SafeDocument;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;
import net.doge.utils.ListUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author yzx
 * @Description 图片浏览的对话框
 * @Date 2020/12/15
 */
public abstract class ImageViewDialog extends AbstractTitledDialog {
    private final int WIDTH = 1000;
    private final int HEIGHT = 850;
    private final int IMG_MAX_WIDTH = WIDTH - 100;
    private final int IMG_MAX_HEIGHT = HEIGHT - 150;
    // 加载图片提示
    private final String LOADING_IMG_MSG = "请稍候，图片加载中......";
    // 已经是第一张提示
    private final String FIRST_PAGE_MSG = "已经是第一张了";
    // 已经是最后一张提示
    private final String LAST_PAGE_MSG = "已经是最后一张了";
    // 非法页码提示
    private final String ILLEGAL_PAGE_MSG = "请输入合法页码";

    private final String ZOOM_IN = "放大";
    private final String ZOOM_OUT = "缩小";
    private final String LEFT_ROTATE = "逆时针旋转 90 度";
    private final String RIGHT_ROTATE = "顺时针旋转 90 度";
    private final String LAST_IMG = "上一张";
    private final String NEXT_IMG = "下一张";
    private final String FIRST_IMG = "第一张";
    private final String LST_IMG = "最后一张";
    private final String SAVE_IMG = "保存图片";
    private final String GO_TIP = "跳页";

    // 放大图标
    private ImageIcon zoomInIcon = new ImageIcon(SimplePath.ICON_PATH + "zoomIn.png");
    // 缩小图标
    private ImageIcon zoomOutIcon = new ImageIcon(SimplePath.ICON_PATH + "zoomOut.png");
    // 逆时针旋转图标
    private ImageIcon leftRotateIcon = new ImageIcon(SimplePath.ICON_PATH + "leftRotate.png");
    // 顺时针旋转图标
    private ImageIcon rightRotateIcon = new ImageIcon(SimplePath.ICON_PATH + "rightRotate.png");
    // 上一张图标
    private ImageIcon lastImgIcon = new ImageIcon(SimplePath.ICON_PATH + "lastPage.png");
    // 下一张图标
    private ImageIcon nextImgIcon = new ImageIcon(SimplePath.ICON_PATH + "nextPage.png");
    // 第一张图标
    private ImageIcon firstImgIcon = new ImageIcon(SimplePath.ICON_PATH + "startPage.png");
    // 最后一张图标
    private ImageIcon lstImgIcon = new ImageIcon(SimplePath.ICON_PATH + "endPage.png");
    // 保存图片图标
    private ImageIcon saveImgIcon = new ImageIcon(SimplePath.ICON_PATH + "saveImg.png");
    // 跳页图标
    private ImageIcon goIcon = new ImageIcon(SimplePath.ICON_PATH + "go.png");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel bottomPanel = new CustomPanel();

    private CustomLabel imgLabel = new CustomLabel("");
    public CustomButton zoomInButton = new CustomButton(zoomInIcon);
    public CustomButton zoomOutButton = new CustomButton(zoomOutIcon);
    public CustomButton leftRotateButton = new CustomButton(leftRotateIcon);
    public CustomButton rightRotateButton = new CustomButton(rightRotateIcon);
    public CustomButton firstImgButton = new CustomButton(firstImgIcon);
    public CustomButton lastImgButton = new CustomButton(lastImgIcon);
    private CustomLabel pageLabel = new CustomLabel();
    // 页数框
    public CustomTextField pageTextField = new CustomTextField(3);
    // 跳页按钮
    private CustomButton goButton = new CustomButton(goIcon);
    public CustomButton nextImgButton = new CustomButton(nextImgIcon);
    private CustomButton lstImgButton = new CustomButton(lstImgIcon);
    private CustomButton saveImgButton = new CustomButton(saveImgIcon);

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.Y_AXIS);

    private CommonResult<String> results;
    private List<String> cursors = new LinkedList<>();
    // 分页
    private int p = 1;
    private int pn = 1;
    private int limit;
    // 当前图片
    private BufferedImage img;
    // 大小比例
    private float scale;

    public ImageViewDialog(PlayerFrame f, int limit) {
        super(f, "图片预览");
        this.limit = limit;
    }

    public void showDialog() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        TipDialog dialog = new TipDialog(f, LOADING_IMG_MSG, 0);
        dialog.showDialog();
        // 组装界面
        initView();
        dialog.close();

        add(globalPanel, BorderLayout.CENTER);
        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    // 组装界面
    private void initView() {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(bottomBox, BorderLayout.SOUTH);

        Color textColor = f.currUIStyle.getTextColor();
        Color iconColor = f.currUIStyle.getIconColor();

        // 标签
        imgLabel.setForeground(textColor);
        pageLabel.setForeground(textColor);

        // 放大/缩小
        zoomInButton.setToolTipText(ZOOM_IN);
        zoomInButton.setIcon(ImageUtils.dye((ImageIcon) zoomInButton.getIcon(), iconColor));
        zoomInButton.addMouseListener(new ButtonMouseListener(zoomInButton, f));
        zoomInButton.setPreferredSize(new Dimension(zoomInIcon.getIconWidth() + 10, zoomInIcon.getIconHeight() + 10));
        zoomInButton.addActionListener(e -> showScaledImg(scale = Math.min(20f, scale + 0.1f)));
        zoomOutButton.setToolTipText(ZOOM_OUT);
        zoomOutButton.setIcon(ImageUtils.dye((ImageIcon) zoomOutButton.getIcon(), iconColor));
        zoomOutButton.addMouseListener(new ButtonMouseListener(zoomOutButton, f));
        zoomOutButton.setPreferredSize(new Dimension(zoomOutIcon.getIconWidth() + 10, zoomOutIcon.getIconHeight() + 10));
        zoomOutButton.addActionListener(e -> showScaledImg(scale = Math.max(0.1f, scale - 0.1f)));
        // 逆时针/顺时针旋转
        leftRotateButton.setToolTipText(LEFT_ROTATE);
        leftRotateButton.setIcon(ImageUtils.dye((ImageIcon) leftRotateButton.getIcon(), iconColor));
        leftRotateButton.addMouseListener(new ButtonMouseListener(leftRotateButton, f));
        leftRotateButton.setPreferredSize(new Dimension(leftRotateIcon.getIconWidth() + 10, leftRotateIcon.getIconHeight() + 10));
        leftRotateButton.addActionListener(e -> showRotatedImg(-90));
        rightRotateButton.setToolTipText(RIGHT_ROTATE);
        rightRotateButton.setIcon(ImageUtils.dye((ImageIcon) rightRotateButton.getIcon(), iconColor));
        rightRotateButton.addMouseListener(new ButtonMouseListener(rightRotateButton, f));
        rightRotateButton.setPreferredSize(new Dimension(rightRotateIcon.getIconWidth() + 10, rightRotateIcon.getIconHeight() + 10));
        rightRotateButton.addActionListener(e -> showRotatedImg(90));
        // 上/下一张按钮
        lastImgButton.setToolTipText(LAST_IMG);
        lastImgButton.setIcon(ImageUtils.dye((ImageIcon) lastImgButton.getIcon(), iconColor));
        lastImgButton.addMouseListener(new ButtonMouseListener(lastImgButton, f));
        lastImgButton.setPreferredSize(new Dimension(lastImgIcon.getIconWidth() + 10, lastImgIcon.getIconHeight() + 10));
        lastImgButton.addActionListener(e -> {
            if (p == 1) {
                new TipDialog(f, FIRST_PAGE_MSG).showDialog();
                return;
            }
            showImg(--p);
        });
        nextImgButton.setToolTipText(NEXT_IMG);
        nextImgButton.setIcon(ImageUtils.dye((ImageIcon) nextImgButton.getIcon(), iconColor));
        nextImgButton.addMouseListener(new ButtonMouseListener(nextImgButton, f));
        nextImgButton.setPreferredSize(new Dimension(nextImgIcon.getIconWidth() + 10, nextImgIcon.getIconHeight() + 10));
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
        firstImgButton.setIcon(ImageUtils.dye((ImageIcon) firstImgButton.getIcon(), iconColor));
        firstImgButton.addMouseListener(new ButtonMouseListener(firstImgButton, f));
        firstImgButton.setPreferredSize(new Dimension(firstImgIcon.getIconWidth() + 10, firstImgIcon.getIconHeight() + 10));
        firstImgButton.addActionListener(e -> {
            if (p == 1) {
                new TipDialog(f, FIRST_PAGE_MSG).showDialog();
                return;
            }
            showImg(p = 1);
        });
        lstImgButton.setToolTipText(LST_IMG);
        lstImgButton.setIcon(ImageUtils.dye((ImageIcon) lstImgButton.getIcon(), iconColor));
        lstImgButton.addMouseListener(new ButtonMouseListener(lstImgButton, f));
        lstImgButton.setPreferredSize(new Dimension(lstImgIcon.getIconWidth() + 10, lstImgIcon.getIconHeight() + 10));
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
                if (e.getKeyCode() != KeyEvent.VK_ENTER) return;
                goButton.doClick();
            }
        });
        pageTextField.setForeground(textColor);
        pageTextField.setCaretColor(textColor);
        // 跳页按钮
        goButton.setToolTipText(GO_TIP);
        goButton.setIcon(ImageUtils.dye((ImageIcon) goButton.getIcon(), iconColor));
        goButton.addMouseListener(new ButtonMouseListener(goButton, f));
        goButton.setPreferredSize(new Dimension(goIcon.getIconWidth() + 10, goIcon.getIconHeight() + 10));
        goButton.addActionListener(e -> {
            if (results == null) return;
            String text = pageTextField.getText();
            pageTextField.setText("");
            if (text.isEmpty()) return;
            int destPage = Integer.parseInt(text);
            if (destPage < 1 || destPage > results.total) {
                new TipDialog(f, ILLEGAL_PAGE_MSG).showDialog();
                return;
            }
            showImg(p = destPage);
        });

        // 保存图片
        saveImgButton.setToolTipText(SAVE_IMG);
        saveImgButton.setIcon(ImageUtils.dye((ImageIcon) saveImgButton.getIcon(), iconColor));
        saveImgButton.addMouseListener(new ButtonMouseListener(saveImgButton, f));
        saveImgButton.setPreferredSize(new Dimension(saveImgIcon.getIconWidth() + 10, saveImgIcon.getIconHeight() + 10));
        saveImgButton.addActionListener(e -> saveImg(p));

        centerPanel.add(imgLabel, BorderLayout.CENTER);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(5);
        bottomPanel.setLayout(fl);
        bottomPanel.add(zoomInButton);
        bottomPanel.add(zoomOutButton);
        bottomPanel.add(leftRotateButton);
        bottomPanel.add(rightRotateButton);
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
                close();
            }
        });
    }

    // 显示第 i 张图片
    private boolean showImg(int i) {
        img = getImg(i);
        pageLabel.setText(String.format("%s / %s", i, results.total));
        return showImg(img);
    }

    // 显示旋转后的图片
    private boolean showRotatedImg(double angle) {
        img = ImageUtils.rotate(img, angle);
        return showScaledImg(scale);
    }

    // 显示放大/缩小的图片
    private boolean showScaledImg(float scale) {
        return showImg(ImageUtils.scale(img, scale));
    }

    // 显示图片
    private boolean showImg(BufferedImage img) {
        if (img == null) {
            imgLabel.setIcon(null);
            imgLabel.setText("图片走丢了T_T");
            return false;
        }
        // 调整图像大小适应窗口
        int w = img.getWidth();
        if (w > IMG_MAX_WIDTH) img = ImageUtils.width(img, IMG_MAX_WIDTH);
        int h = img.getHeight();
        if (h > IMG_MAX_HEIGHT) img = ImageUtils.height(img, IMG_MAX_HEIGHT);
        // 调整后重新计算比例
        scale = (float) img.getWidth() / this.img.getWidth();
        imgLabel.setIcon(new ImageIcon(img));
        imgLabel.setText("");
        return true;
    }

    // 请求图片
    public abstract CommonResult<String> requestImgUrls(int pn, int limit, String cursor);

    // 第一次请求图片失败时调用
    public abstract void requestFailed();

    // 获取第 i 张图片
    private BufferedImage getImg(int i) {
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
    private void saveImg(int i) {
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
                ImageUtils.toFile(img, outputFile);
            }
        });
    }
}
