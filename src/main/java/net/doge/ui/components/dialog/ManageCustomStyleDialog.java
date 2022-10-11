package net.doge.ui.components.dialog;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.Fonts;
import net.doge.constants.SimplePath;
import net.doge.constants.UIStyleConstants;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.DialogButton;
import net.doge.ui.componentui.ScrollBarUI;
import net.doge.ui.listeners.ButtonMouseListener;
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
 * @Description 管理自定义样式的对话框
 * @Date 2020/12/15
 */
public class ManageCustomStyleDialog extends JDialog {
    private final String TITLE = "管理主题";
    // 预设主题不能编辑提示
    private final String EDIT_DENIED_MSG = "不能编辑预设的主题";
    // 预设主题不能删除提示
    private final String REMOVE_DENIED_MSG = "不能删除预设的主题";
    // 删除确认提示
    private final String ASK_REMOVE_MSG = "确定删除选中的主题？";
    // 编辑时单选提示
    private final String SINGLE_SELECT_MSG = "需要编辑的主题一次只能选择一个";
    private ManageCustomStyleDialogPanel globalPanel = new ManageCustomStyleDialogPanel();

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

    private final JLabel tipLabel = new JLabel("应用、添加、编辑或删除主题（预设主题不能修改）");
    private final JList<UIStyle> styleList = new JList<>();
    private final JScrollPane styleListScrollPane = new JScrollPane(styleList);
    private final DefaultListModel<UIStyle> styleListModel = new DefaultListModel<>();
    private DialogButton allSelectButton;
    private DialogButton nonSelectButton;
    private DialogButton applyButton;
    private DialogButton addButton;
    private DialogButton editButton;
    private DialogButton removeButton;

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private Box rightBox = new Box(BoxLayout.Y_AXIS);

    // 全局字体
    private Font globalFont = Fonts.NORMAL;

    private PlayerFrame f;
    private UIStyle style;

    // 父窗口和是否是模态
    public ManageCustomStyleDialog(PlayerFrame f, boolean isModel) {
        super(f, isModel);
        this.f = f;
        this.style = f.getCurrUIStyle();

        Color buttonColor = style.getButtonColor();
        allSelectButton = new DialogButton("全选", buttonColor);
        nonSelectButton = new DialogButton("反选", buttonColor);
        applyButton = new DialogButton("应用", buttonColor);
        addButton = new DialogButton("添加", buttonColor);
        editButton = new DialogButton("编辑", buttonColor);
        removeButton = new DialogButton("删除", buttonColor);
    }

    public void showDialog() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
        setSize(800, 640);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initStyles();

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
        if (f.getIsBlur() && f.getPlayer().loadedMusic()) bufferedImage = f.getPlayer().getMusicInfo().getAlbumImage();
        else {
            String styleImgPath = f.getCurrUIStyle().getStyleImgPath();
            if (StringUtils.isNotEmpty(styleImgPath)) bufferedImage = f.getCurrUIStyle().getImg();
            else bufferedImage = ImageUtils.dyeRect(1, 1, f.getCurrUIStyle().getBgColor());
        }
        if (bufferedImage == null) bufferedImage = f.getDefaultAlbumImage();
        doBlur(bufferedImage);
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
        styleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // 添加标签
        tipLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tipLabel.setForeground(style.getLabelColor());
        centerPanel.add(tipLabel, BorderLayout.NORTH);
        // 全选事件
        allSelectButton.addActionListener(e -> {
            // 选择开始到结束(包含)的节点！
            styleList.getSelectionModel().setSelectionInterval(0, styleListModel.getSize() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> {
            styleList.clearSelection();
        });
        // 应用事件
        applyButton.addActionListener(e -> {
            try {
                UIStyle style = styleList.getSelectedValue();
                if (style == null) return;
                f.changeUIStyle(style);
                updateStyle();
                // 选中应用的风格
//                List<CustomRadioButtonMenuItem> stylePopupMenuItems = f.getStylePopupMenuItems();
//                for (int i = 0; i < stylePopupMenuItems.size(); i++) {
//                    if (stylePopupMenuItems.get(i).getText().trim().equals(style.getStyleName())) {
//                        stylePopupMenuItems.get(i).setSelected(true);
//                        break;
//                    }
//                }
//                f.updateRadioButtonMenuItemIcon(f.getStylePopupMenu());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (AWTException ex) {
                throw new RuntimeException(ex);
            }
        });
        // 添加事件
        addButton.addActionListener(e -> {
            CustomStyleDialog customStyleDialog = new CustomStyleDialog(f, true, "添加", f.getCurrUIStyle());
            try {
                customStyleDialog.showDialog();
                if (customStyleDialog.getConfirmed()) {
                    // 创建自定义样式并更换
                    Object[] results = customStyleDialog.getResults();
                    UIStyle customStyle = new UIStyle(
                            UIStyleConstants.CUSTOM,
                            ((String) results[0]),
                            "", ((Color) results[2]), ((Color) results[3]),
                            ((Color) results[4]), ((Color) results[5]), ((Color) results[6]),
                            ((Color) results[7]), ((Color) results[8]), ((Color) results[9]),
                            ((Color) results[10]), ((Color) results[11]), ((Color) results[12])
                    );
                    customStyle.setInvokeLater(() -> updateRenderer(styleList));
                    if (results[1] instanceof Color) customStyle.setBgColor((Color) results[1]);
                    else customStyle.setStyleImgPath((String) results[1]);
                    // 添加风格菜单项、按钮组，但不切换风格
                    f.addStyle(customStyle, false);
                    // 最后别忘了到列表中添加
                    styleListModel.addElement(customStyle);
                }
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            } catch (InstantiationException instantiationException) {
                instantiationException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            }
        });
        // 删除事件
        removeButton.addActionListener(e -> {
            UIStyle value = styleList.getSelectedValue();
            if (value == null) return;
            if (value.isPreDefined()) {
                new TipDialog(f, REMOVE_DENIED_MSG).showDialog();
                return;
            }
            ConfirmDialog d = new ConfirmDialog(f, ASK_REMOVE_MSG, "是", "否");
            d.showDialog();
            if (d.getResponse() == JOptionPane.YES_OPTION) {
                List<UIStyle> selectedStyles = styleList.getSelectedValuesList();
                // 获取应处理的集合
//                    List<CustomRadioButtonMenuItem> stylePopupMenuItems = f.getStylePopupMenuItems();
//                    CustomPopupMenu stylePopupMenu = f.getStylePopupMenu();
                List<UIStyle> styles = f.getStyles();
                UIStyle currUIStyle = f.getCurrUIStyle();
                selectedStyles.forEach(style -> {
                    // 删除正在使用的样式，先换回默认样式，再删除
                    if (style == currUIStyle) {
                        try {
                            f.changeUIStyle(styles.get(0));
//                                stylePopupMenuItems.get(0).setSelected(true);
//                                f.updateRadioButtonMenuItemIcon(stylePopupMenu);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        } catch (ClassNotFoundException classNotFoundException) {
                            classNotFoundException.printStackTrace();
                        } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                            unsupportedLookAndFeelException.printStackTrace();
                        } catch (InstantiationException instantiationException) {
                            instantiationException.printStackTrace();
                        } catch (IllegalAccessException illegalAccessException) {
                            illegalAccessException.printStackTrace();
                        } catch (AWTException awtException) {
                            awtException.printStackTrace();
                        }
                    }
                    // 从已添加到界面上的菜单项中删除
//                        for (int i = 0; i < stylePopupMenuItems.size(); i++) {
//                            if (stylePopupMenuItems.get(i).getText().trim().equals(style.getStyleName())) {
//                                stylePopupMenu.remove(stylePopupMenuItems.get(i));
//                                stylePopupMenuItems.remove(stylePopupMenuItems.get(i--));
//                            }
//                        }
                    styles.remove(style);
                    // 删除图片文件
                    File file = new File(style.getStyleImgPath());
                    // 确保要删除的文件不被其他主题使用
                    boolean canDelete = true;
                    for (UIStyle st : styles) {
                        if (file.equals(new File(st.getStyleImgPath()))) {
                            canDelete = false;
                            break;
                        }
                    }
                    if (canDelete) file.delete();
                    // 最后别忘了从列表中删除
                    styleListModel.removeElement(style);
                });
            }
        });
        // 编辑事件
        editButton.addActionListener(e -> {
            UIStyle value = styleList.getSelectedValue();
            if (value == null) return;
            if (value.isPreDefined()) {
                new TipDialog(f, EDIT_DENIED_MSG).showDialog();
                return;
            }
            CustomStyleDialog dialog = new CustomStyleDialog(f, true, "更新", value);
            try {
                int length = styleList.getSelectedIndices().length;
                if (length == 0) return;
                // 只能单选
                if (length > 1) {
                    new TipDialog(f, SINGLE_SELECT_MSG).showDialog();
                    return;
                }
                dialog.showDialog();
                if (dialog.getConfirmed()) {
                    Object[] results = dialog.getResults();
                    UIStyle selectedStyle = styleList.getSelectedValue();
                    selectedStyle.setStyleName((String) results[0]);
                    if (results[1] instanceof Color) {
                        selectedStyle.setStyleImgPath("");
                        selectedStyle.setBgColor((Color) results[1]);
                    } else {
                        selectedStyle.setStyleImgPath((String) results[1]);
                        selectedStyle.setBgColor(null);
                    }
                    selectedStyle.setForeColor((Color) results[2]);
                    selectedStyle.setSelectedColor((Color) results[3]);
                    selectedStyle.setLrcColor((Color) results[4]);
                    selectedStyle.setHighlightColor((Color) results[5]);
                    selectedStyle.setLabelColor((Color) results[6]);
                    selectedStyle.setTimeBarColor((Color) results[7]);
                    selectedStyle.setButtonColor((Color) results[8]);
                    selectedStyle.setScrollBarColor((Color) results[9]);
                    selectedStyle.setSliderColor((Color) results[10]);
                    selectedStyle.setSpectrumColor((Color) results[11]);
                    selectedStyle.setMenuItemColor((Color) results[12]);
                    // 若编辑的样式正在使用，则更换
                    if (f.getCurrUIStyle() == selectedStyle) {
                        f.changeUIStyle(selectedStyle);
                        updateStyle();
                    }
//                    styleList.clearSelection();
                }
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            } catch (InstantiationException instantiationException) {
                instantiationException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            } catch (InvocationTargetException invocationTargetException) {
                invocationTargetException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (AWTException awtException) {
                awtException.printStackTrace();
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
        rightBox.add(applyButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(addButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(editButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(removeButton);
        rightBox.add(Box.createVerticalGlue());
        // 添加列表和右部按钮整体
        DefaultStyleListRenderer r = new DefaultStyleListRenderer(f);
        r.setCustomFont(globalFont);
        r.setForeColor(style.getForeColor());
        r.setSelectedColor(style.getSelectedColor());
        styleList.setCellRenderer(r);
        styleList.setOpaque(false);
        styleList.setFocusable(false);
        styleList.setModel(styleListModel);
        styleList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = styleList.locationToIndex(e.getPoint());
                Rectangle bounds = styleList.getCellBounds(index, index);
                if (bounds != null) setHoverIndex(bounds.contains(e.getPoint()) ? index : -1);
            }

            private void setHoverIndex(int index) {
                DefaultStyleListRenderer renderer = (DefaultStyleListRenderer) styleList.getCellRenderer();
                if (renderer != null) {
                    int hoverIndex = renderer.getHoverIndex();
                    if (hoverIndex == index) return;
                    renderer.setHoverIndex(index);
                    styleList.repaint();
                }
            }
        });
        styleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                DefaultStyleListRenderer renderer = (DefaultStyleListRenderer) styleList.getCellRenderer();
                if (renderer != null) {
                    renderer.setHoverIndex(-1);
                    styleList.repaint();
                }
            }
        });
        styleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标左键双击应用风格
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    applyButton.doClick();
                }
            }
        });
        // 注意：将 JList 加到 JScrollPane 时必须使用构造器，而不是 add ！！！
        scrollPaneOpaque();
        styleListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        styleListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        styleListScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        styleListScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        bottomBox.add(styleListScrollPane);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);

        // 字体
        tipLabel.setFont(globalFont);
        styleList.setFont(globalFont);
        allSelectButton.setFont(globalFont);
        nonSelectButton.setFont(globalFont);
        applyButton.setFont(globalFont);
        addButton.setFont(globalFont);
        editButton.setFont(globalFont);
        removeButton.setFont(globalFont);
    }

    // 初始化数据
    void initStyles() {
        List<UIStyle> styles = f.getStyles();
        for (UIStyle style : styles) {
            styleListModel.addElement(style);
        }
    }

    void scrollPaneOpaque() {
        styleListScrollPane.setOpaque(false);
        styleListScrollPane.getViewport().setOpaque(false);
        styleListScrollPane.getHorizontalScrollBar().setOpaque(false);
        styleListScrollPane.getVerticalScrollBar().setOpaque(false);
        styleListScrollPane.getHorizontalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
        styleListScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
        styleListScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
    }

    void updateRenderer(JList list) {
        ListCellRenderer renderer = list.getCellRenderer();
        list.setCellRenderer(null);
        list.setCellRenderer(renderer);
    }

    // 主题更换时更新窗口主题
    void updateStyle() {
        UIStyle st = f.getCurrUIStyle();
        Color labelColor = st.getLabelColor();
        Color buttonColor = st.getButtonColor();

        titleLabel.setForeground(labelColor);
        closeButton.setIcon(ImageUtils.dye((ImageIcon) closeButton.getIcon(), buttonColor));
        tipLabel.setForeground(labelColor);
        allSelectButton.setForeColor(buttonColor);
        nonSelectButton.setForeColor(buttonColor);
        applyButton.setForeColor(buttonColor);
        addButton.setForeColor(buttonColor);
        editButton.setForeColor(buttonColor);
        removeButton.setForeColor(buttonColor);
        DefaultStyleListRenderer r = (DefaultStyleListRenderer) styleList.getCellRenderer();
        r.setForeColor(st.getForeColor());
        r.setSelectedColor(st.getSelectedColor());
        styleListScrollPane.getHorizontalScrollBar().setUI(new ScrollBarUI(st.getScrollBarColor()));
        styleListScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(st.getScrollBarColor()));
    }

    void doBlur(BufferedImage bufferedImage) {
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
            bufferedImage = ImageUtils.darker(ImageUtils.doBlur(bufferedImage));
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

    private class ManageCustomStyleDialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public ManageCustomStyleDialogPanel() {
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
