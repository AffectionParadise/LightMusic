package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleConstants;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.checkbox.CustomCheckBox;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.CustomList;
import net.doge.ui.widget.list.renderer.core.StyleListRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.scrollbar.ui.CustomScrollBarUI;
import net.doge.util.os.FileUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @Author Doge
 * @Description 管理自定义样式的对话框
 * @Date 2020/12/15
 */
public class ManageCustomStyleDialog extends AbstractTitledDialog {
    private final int WIDTH = ScaleUtil.scale(890);
    private final int HEIGHT = ScaleUtil.scale(720);
    private final String IMG_LOST_MSG = I18n.getText("bgImgLost");
    private final String EDIT_DENIED_MSG = I18n.getText("editDenied");
    private final String REMOVE_DENIED_MSG = I18n.getText("removeDenied");
    private final String ASK_REMOVE_MSG = I18n.getText("askRemoveStyleMsg");
    private final String SINGLE_SELECT_MSG = I18n.getText("singleSelectionMsg");

    private CustomPanel centerPanel = new CustomPanel();

    private CustomPanel northPanel = new CustomPanel();
    private CustomPanel tipPanel = new CustomPanel();
    private CustomLabel tipLabel = new CustomLabel(I18n.getText("manageStyleTip"));
    private CustomPanel customOnlyPanel = new CustomPanel();
    private CustomCheckBox customOnlyCheckBox = new CustomCheckBox(I18n.getText("customOnly"));
    private CustomList<UIStyle> styleList = new CustomList<>();
    private CustomScrollPane styleListScrollPane = new CustomScrollPane(styleList);
    private DefaultListModel<UIStyle> styleListModel = new DefaultListModel<>();
    private DefaultListModel<UIStyle> emptyListModel = new DefaultListModel<>();
    private DialogButton allSelectButton;
    private DialogButton nonSelectButton;
    private DialogButton applyButton;
    private DialogButton addButton;
    private DialogButton editButton;
    private DialogButton removeButton;

    // 底部盒子
    private CustomBox bottomBox = new CustomBox(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private CustomBox rightBox = new CustomBox(BoxLayout.Y_AXIS);

    public ManageCustomStyleDialog(MainFrame f) {
        super(f, I18n.getText("manageStyleTitle"));

        Color textColor = f.currUIStyle.getTextColor();
        allSelectButton = new DialogButton(I18n.getText("dialogAll"), textColor);
        nonSelectButton = new DialogButton(I18n.getText("dialogInvert"), textColor);
        applyButton = new DialogButton(I18n.getText("dialogApply"), textColor);
        addButton = new DialogButton(I18n.getText("dialogAdd"), textColor);
        editButton = new DialogButton(I18n.getText("dialogEdit"), textColor);
        removeButton = new DialogButton(I18n.getText("dialogRemove"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initStyles();

        setContentPane(globalPanel);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    // 组装界面
    private void initView() {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(new HDEmptyBorder(0, 10, 0, 10));
        globalPanel.add(centerPanel, BorderLayout.CENTER);

        Color textColor = f.currUIStyle.getTextColor();
        Color iconColor = f.currUIStyle.getIconColor();
        Color foreColor = f.currUIStyle.getForeColor();
        Color selectedColor = f.currUIStyle.getSelectedColor();

        // 添加标签
        tipLabel.setBorder(new HDEmptyBorder(10, 10, 10, 10));
        tipLabel.setForeground(textColor);
        tipPanel.add(tipLabel);
        customOnlyCheckBox.setSelected(f.customOnly);
        customOnlyCheckBox.setForeground(textColor);
        customOnlyCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        customOnlyCheckBox.setIcon(ImageUtil.dye(f.uncheckedIcon, iconColor));
        customOnlyCheckBox.setSelectedIcon(ImageUtil.dye(f.checkedIcon, iconColor));
        customOnlyCheckBox.addActionListener(e -> {
            f.customOnly = customOnlyCheckBox.isSelected();
            initStyles();
        });
        customOnlyPanel.add(customOnlyCheckBox);

        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(tipPanel);
        northPanel.add(customOnlyPanel);
        centerPanel.add(northPanel, BorderLayout.NORTH);
        // 全选事件
        allSelectButton.addActionListener(e -> {
            // 选择开始到结束(包含)的节点！
            styleList.getSelectionModel().setSelectionInterval(0, styleListModel.size() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> {
            styleList.clearSelection();
        });
        // 应用事件
        applyButton.addActionListener(e -> {
            UIStyle style = styleList.getSelectedValue();
            if (style == null) return;
            if (!style.hasImg()) {
                new TipDialog(f, IMG_LOST_MSG, true).showDialog();
                return;
            }
            f.changeUIStyle(style);
            updateStyle();
        });
        // 添加事件
        addButton.addActionListener(e -> {
            UIStyle value = styleList.getSelectedValue();
            CustomStyleDialog customStyleDialog = new CustomStyleDialog(f, I18n.getText("addStyle"), value != null ? value : f.currUIStyle);
            customStyleDialog.showDialog();
            if (customStyleDialog.isConfirmed()) {
                // 创建自定义样式并更换
                Object[] results = customStyleDialog.getResults();
                UIStyle customStyle = new UIStyle(
                        UIStyleConstants.CUSTOM,
                        ((String) results[0]),
                        "", ((Color) results[2]), ((Color) results[3]),
                        ((Color) results[4]), ((Color) results[5]), ((Color) results[6]),
                        ((Color) results[7]), ((Color) results[8]), ((Color) results[9]),
                        ((Color) results[10]), ((Color) results[11])
                );
                customStyle.setInvokeLater(() -> updateRenderer(styleList));
                if (results[1] instanceof Color) customStyle.setBgColor((Color) results[1]);
                else customStyle.setImgKey((String) results[1]);
                // 添加主题菜单项、按钮组，但不切换主题
                f.styles.add(customStyle);
                // 最后别忘了到列表中添加
                styleListModel.addElement(customStyle);
            }
        });
        // 删除事件
        removeButton.addActionListener(e -> {
            UIStyle value = styleList.getSelectedValue();
            if (value == null) return;
            if (value.isPreDefined()) {
                new TipDialog(f, REMOVE_DENIED_MSG, true).showDialog();
                return;
            }
            ConfirmDialog d = new ConfirmDialog(f, ASK_REMOVE_MSG, I18n.getText("yes"), I18n.getText("no"));
            d.showDialog();
            if (d.getResponse() == JOptionPane.YES_OPTION) {
                List<UIStyle> selectedStyles = styleList.getSelectedValuesList();
                List<UIStyle> styles = f.styles;
                UIStyle currUIStyle = f.currUIStyle;
                selectedStyles.forEach(style -> {
                    // 删除正在使用的样式，先换回默认样式，再删除
                    if (style == currUIStyle) {
                        f.changeUIStyle(styles.get(0));
                        updateStyle();
                    }
                    styles.remove(style);
                    // 删除图片文件
                    FileUtil.delete(style.getImgKey());
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
                new TipDialog(f, EDIT_DENIED_MSG, true).showDialog();
                return;
            }
            CustomStyleDialog dialog = new CustomStyleDialog(f, I18n.getText("updateStyle"), value);
            int length = styleList.getSelectedIndices().length;
            if (length == 0) return;
            // 只能单选
            if (length > 1) {
                new TipDialog(f, SINGLE_SELECT_MSG, true).showDialog();
                return;
            }
            dialog.showDialog();
            if (dialog.isConfirmed()) {
                Object[] results = dialog.getResults();
                UIStyle selectedStyle = styleList.getSelectedValue();
                selectedStyle.setName((String) results[0]);
                selectedStyle.setInvokeLater(() -> updateRenderer(styleList));
                // 先设置图片为空，避免更新主题时背景不切换
                selectedStyle.setImgKey("");
                if (results[1] instanceof Color) {
                    selectedStyle.setBgColor((Color) results[1]);
                } else {
                    selectedStyle.setImgKey((String) results[1]);
                    selectedStyle.setBgColor(null);
                }
                selectedStyle.setForeColor((Color) results[2]);
                selectedStyle.setSelectedColor((Color) results[3]);
                selectedStyle.setLrcColor((Color) results[4]);
                selectedStyle.setHighlightColor((Color) results[5]);
                selectedStyle.setTextColor((Color) results[6]);
                selectedStyle.setTimeBarColor((Color) results[7]);
                selectedStyle.setIconColor((Color) results[8]);
                selectedStyle.setScrollBarColor((Color) results[9]);
                selectedStyle.setSliderColor((Color) results[10]);
                selectedStyle.setSpectrumColor((Color) results[11]);
                // 若编辑的样式正在使用，则更换
                if (f.currUIStyle == selectedStyle) {
                    if (selectedStyle.hasImg()) {
                        SwingUtilities.invokeLater(() -> {
                            f.changeUIStyle(selectedStyle);
                            updateStyle();
                        });
                    } else {
                        // 提交给 swing 线程处理，避免发生渲染异常！
                        selectedStyle.setInvokeLater(() -> SwingUtilities.invokeLater(() -> {
                            f.changeUIStyle(selectedStyle);
                            updateStyle();
                        }));
                    }
                }
            }
        });
        // 添加右部按钮
        rightBox.setBorder(new HDEmptyBorder(0, 10, 10, 10));
        Dimension area = new HDDimension(1, 10);
        rightBox.add(CustomBox.createVerticalGlue());
        rightBox.add(allSelectButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(nonSelectButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(applyButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(addButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(editButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(removeButton);
        rightBox.add(CustomBox.createVerticalGlue());
        // 添加列表和右部按钮整体
        StyleListRenderer r = new StyleListRenderer(f);
        r.setForeColor(foreColor);
        r.setSelectedColor(selectedColor);
        r.setTextColor(textColor);
        styleList.setCellRenderer(r);
        styleList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        styleList.setModel(styleListModel);
        styleList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = styleList.locationToIndex(e.getPoint());
                Rectangle bounds = styleList.getCellBounds(index, index);
                if (bounds == null) return;
                setHoverIndex(bounds.contains(e.getPoint()) ? index : -1);
            }

            private void setHoverIndex(int index) {
                StyleListRenderer renderer = (StyleListRenderer) styleList.getCellRenderer();
                if (renderer == null) return;
                int hoverIndex = renderer.getHoverIndex();
                if (hoverIndex == index) return;
                renderer.setHoverIndex(index);
                // 奇怪的黑背景解决
                repaint();
            }
        });
        styleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                StyleListRenderer renderer = (StyleListRenderer) styleList.getCellRenderer();
                if (renderer == null) return;
                renderer.setHoverIndex(-1);
                repaint();
            }
        });
        styleList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标左键双击应用主题
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    applyButton.doClick();
                }
            }
        });
        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        styleListScrollPane.setHBarUI(new CustomScrollBarUI(scrollBarColor));
        styleListScrollPane.setVBarUI(new CustomScrollBarUI(scrollBarColor));
        styleListScrollPane.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        bottomBox.add(styleListScrollPane);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);
    }

    // 初始化数据
    private void initStyles() {
        List<UIStyle> styles = f.styles;
        styleList.setModel(emptyListModel);
        styleListModel.clear();
        if (f.customOnly) {
            styles.forEach(style -> {
                if (style.isCustom()) styleListModel.addElement(style);
            });
        } else {
            styles.forEach(style -> styleListModel.addElement(style));
        }
        styleList.setModel(styleListModel);
    }

    private void updateRenderer(JList list) {
        ListCellRenderer renderer = list.getCellRenderer();
        list.setCellRenderer(null);
        list.setCellRenderer(renderer);
    }

    // 主题更换时更新窗口主题
    private void updateStyle() {
        UIStyle st = f.currUIStyle;
        Color textColor = st.getTextColor();
        Color iconColor = st.getIconColor();
        Color scrollBarColor = st.getScrollBarColor();

        titleLabel.setForeground(textColor);
        closeButton.setIcon(ImageUtil.dye((ImageIcon) closeButton.getIcon(), iconColor));
        tipLabel.setForeground(textColor);
        customOnlyCheckBox.setForeground(textColor);
        customOnlyCheckBox.setIcon(ImageUtil.dye(f.uncheckedIcon, iconColor));
        customOnlyCheckBox.setSelectedIcon(ImageUtil.dye(f.checkedIcon, iconColor));
        allSelectButton.setForeColor(textColor);
        nonSelectButton.setForeColor(textColor);
        applyButton.setForeColor(textColor);
        addButton.setForeColor(textColor);
        editButton.setForeColor(textColor);
        removeButton.setForeColor(textColor);
        StyleListRenderer r = (StyleListRenderer) styleList.getCellRenderer();
        r.setForeColor(st.getForeColor());
        r.setSelectedColor(st.getSelectedColor());
        r.setTextColor(textColor);

        styleListScrollPane.setHBarUI(new CustomScrollBarUI(scrollBarColor));
        styleListScrollPane.setVBarUI(new CustomScrollBarUI(scrollBarColor));

        globalPanel.repaint();
    }
}
