package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.constants.UIStyleConstants;
import net.doge.models.AudioFile;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomRadioButtonMenuItem;
import net.doge.ui.components.DialogButton;
import net.doge.ui.componentui.ScrollBarUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.ui.renderers.DefaultCatalogListRenderer;
import net.doge.ui.renderers.DefaultStyleListRenderer;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author yzx
 * @Description 管理歌曲目录的对话框
 * @Date 2020/12/15
 */
public class ManageCatalogDialog extends JDialog {
    private final String TITLE = "管理歌曲目录";
    // 删除确认提示
    private final String ASK_REMOVE_MSG = "确定删除选中的目录？";
    // 目录已存在提示
    private final String CATALOG_EXISTS_MSG = "目录已存在，无法继续添加";
    // 目录不存在提示
    private final String CATALOG_NOT_FOUND_MSG = "该目录不存在";
    private ManageCatalogDialogPanel globalPanel = new ManageCatalogDialogPanel();

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    // 关闭窗口图标
    private ImageIcon closeWindowIcon = new ImageIcon(SimplePath.ICON_PATH + "closeWindow.png");

    private JPanel centerPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel windowCtrlPanel = new JPanel();
    private JButton closeButton = new JButton(closeWindowIcon);

    private final JLabel tipLabel = new JLabel("重新导入歌曲时将从以下目录查找歌曲");
    private final JList<File> catalogList = new JList<>();
    private final DefaultListModel<File> catalogListModel = new DefaultListModel<>();
    private DialogButton allSelectButton;
    private DialogButton nonSelectButton;
    private DialogButton locateButton;
    private DialogButton addButton;
    private DialogButton removeButton;

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private Box rightBox = new Box(BoxLayout.Y_AXIS);

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;
    private List<File> catalogs;

    // 父窗口和是否是模态
    public ManageCatalogDialog(PlayerFrame f, boolean isModel) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();
        this.catalogs = f.getCatalogs();

        Color buttonColor = style.getButtonColor();
        allSelectButton = new DialogButton("全选", buttonColor);
        nonSelectButton = new DialogButton("反选", buttonColor);
        locateButton = new DialogButton("打开", buttonColor);
        addButton = new DialogButton("添加", buttonColor);
        removeButton = new DialogButton("删除", buttonColor);
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
        setSize(500, 400);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initCatalogs();

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
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        globalPanel.add(centerPanel, BorderLayout.CENTER);
        // 可多选
        catalogList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // 添加标签
        tipLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tipLabel.setForeground(style.getLabelColor());
        centerPanel.add(tipLabel, BorderLayout.NORTH);
        // 全选事件
        allSelectButton.addActionListener(e -> {
            // 选择开始到结束(包含)的节点！
            catalogList.getSelectionModel().setSelectionInterval(0, catalogListModel.getSize() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> {
            catalogList.clearSelection();
        });
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择歌曲文件夹");
        // 打开文件夹事件
        locateButton.addActionListener(e -> {
            try {
                File dir = catalogList.getSelectedValue();
                if (dir == null) return;
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        // 添加事件
        addButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir != null) {
                    // 文件夹不存在直接跳出
                    if (!dir.exists()) return;
                    for (int i = 0, size = catalogListModel.size(); i < size; i++) {
                        if (catalogListModel.get(i).getAbsolutePath().equals(dir.getAbsolutePath())) {
                            new TipDialog(f, CATALOG_EXISTS_MSG).showDialog();
                            return;
                        }
                    }
                    catalogListModel.addElement(dir);
                    catalogs.add(dir);
                }
            });
        });
        // 删除事件
        removeButton.addActionListener(e -> {
            if (catalogList.getSelectedValue() != null) {
                ConfirmDialog d = new ConfirmDialog(f, ASK_REMOVE_MSG, "是", "否");
                d.showDialog();
                if (d.getResponse() == JOptionPane.YES_OPTION) {
                    List<File> cs = catalogList.getSelectedValuesList();
                    for (File dir : cs) {
                        catalogListModel.removeElement(dir);
                        catalogs.remove(dir);
                    }
                }
            }
        });

        // 添加右部按钮
        rightBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        Dimension area = new Dimension(1, 10);
        rightBox.add(Box.createVerticalGlue());
        rightBox.add(allSelectButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(nonSelectButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(locateButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(addButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(removeButton);
        rightBox.add(Box.createVerticalGlue());
        // 添加列表和右部按钮整体
        DefaultCatalogListRenderer r = new DefaultCatalogListRenderer();
        r.setCustomFont(globalFont);
        r.setForeColor(style.getForeColor());
        r.setSelectedColor(style.getSelectedColor());
        catalogList.setCellRenderer(r);
        catalogList.setOpaque(false);
        catalogList.setFocusable(false);
        catalogList.setModel(catalogListModel);
        catalogList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = catalogList.locationToIndex(e.getPoint());
                Rectangle bounds = catalogList.getCellBounds(index, index);
                if (bounds != null) setHoverIndex(bounds.contains(e.getPoint()) ? index : -1);
            }

            private void setHoverIndex(int index) {
                DefaultCatalogListRenderer renderer = (DefaultCatalogListRenderer) catalogList.getCellRenderer();
                if (renderer != null) {
                    int hoverIndex = renderer.getHoverIndex();
                    if (hoverIndex == index) return;
                    renderer.setHoverIndex(index);
                    catalogList.repaint();
                }
            }
        });
        catalogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                DefaultCatalogListRenderer renderer = (DefaultCatalogListRenderer) catalogList.getCellRenderer();
                if (renderer != null) {
                    renderer.setHoverIndex(-1);
                    catalogList.repaint();
                }
            }
        });
        catalogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标左键双击应用风格
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    locateButton.doClick();
                }
            }
        });
        // 注意：将 JList 加到 JScrollPane 时必须使用构造器，而不是 add ！！！
        JScrollPane sp = new JScrollPane(catalogList);
        scrollPaneOpaque(sp);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(30);
//        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        bottomBox.add(sp);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);

        // 字体
        tipLabel.setFont(globalFont);
        catalogList.setFont(globalFont);
        allSelectButton.setFont(globalFont);
        nonSelectButton.setFont(globalFont);
        locateButton.setFont(globalFont);
        addButton.setFont(globalFont);
        removeButton.setFont(globalFont);
    }

    // 初始化数据
    void initCatalogs() {
        for (File dir : catalogs) {
            catalogListModel.addElement(dir);
        }
    }

    void scrollPaneOpaque(JScrollPane sp) {
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getHorizontalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getHorizontalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
        sp.getVerticalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
    }

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

    private class ManageCatalogDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public ManageCatalogDialogPanel() {
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
