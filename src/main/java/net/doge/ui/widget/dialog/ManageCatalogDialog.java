package net.doge.ui.widget.dialog;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import net.doge.constant.lang.I18n;
import net.doge.constant.ui.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.CustomList;
import net.doge.ui.widget.list.renderer.system.CatalogListRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;
import net.doge.util.common.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author Doge
 * @Description 管理歌曲目录的对话框
 * @Date 2020/12/15
 */
public class ManageCatalogDialog extends AbstractTitledDialog {
    private final String ASK_REMOVE_MSG = I18n.getText("askRemoveCatalogMsg");
    private final String CATALOG_EXISTS_MSG = I18n.getText("catalogExistsMsg");
    private final String CATALOG_NOT_FOUND_MSG = I18n.getText("catalogNotFoundMsg");

    private CustomPanel centerPanel = new CustomPanel();

    private CustomLabel tipLabel = new CustomLabel(I18n.getText("catalogTip"));
    private CustomList<File> catalogList = new CustomList<>();
    private DefaultListModel<File> catalogListModel = new DefaultListModel<>();
    private DialogButton allSelectButton;
    private DialogButton nonSelectButton;
    private DialogButton locateButton;
    private DialogButton addButton;
    private DialogButton removeButton;

    // 底部盒子
    private Box bottomBox = new Box(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private Box rightBox = new Box(BoxLayout.Y_AXIS);

    private List<File> catalogs;

    public ManageCatalogDialog(MainFrame f) {
        super(f, I18n.getText("manageCatalogTitle"));
        this.catalogs = f.catalogs;

        Color textColor = f.currUIStyle.getTextColor();
        allSelectButton = new DialogButton(I18n.getText("dialogAll"), textColor);
        nonSelectButton = new DialogButton(I18n.getText("dialogInvert"), textColor);
        locateButton = new DialogButton(I18n.getText("dialogOpen"), textColor);
        addButton = new DialogButton(I18n.getText("dialogAdd"), textColor);
        removeButton = new DialogButton(I18n.getText("dialogRemove"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(600, 500);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initCatalogs();

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
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        globalPanel.add(centerPanel, BorderLayout.CENTER);

        Color textColor = f.currUIStyle.getTextColor();
        Color foreColor = f.currUIStyle.getForeColor();
        Color selectedColor = f.currUIStyle.getSelectedColor();

        // 添加标签
        tipLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tipLabel.setForeground(textColor);
        centerPanel.add(tipLabel, BorderLayout.NORTH);
        // 全选事件
        allSelectButton.addActionListener(e -> {
            // 选择开始到结束(包含)的节点！
            catalogList.getSelectionModel().setSelectionInterval(0, catalogListModel.size() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> catalogList.clearSelection());
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(I18n.getText("chooseTrackFolder"));
        // 打开文件夹事件
        locateButton.addActionListener(e -> {
            try {
                File dir = catalogList.getSelectedValue();
                if (dir == null) return;
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {
                LogUtil.error(ex);
            }
        });
        // 添加事件
        addButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir == null) return;
                // 文件夹不存在直接跳出
                if (!dir.exists()) return;
                for (int i = 0, size = catalogListModel.size(); i < size; i++) {
                    if (catalogListModel.get(i).getAbsolutePath().equals(dir.getAbsolutePath())) {
                        new TipDialog(f, CATALOG_EXISTS_MSG, true).showDialog();
                        return;
                    }
                }
                catalogListModel.addElement(dir);
                catalogs.add(dir);
            });
        });
        // 删除事件
        removeButton.addActionListener(e -> {
            if (catalogList.getSelectedValue() == null) return;
            ConfirmDialog d = new ConfirmDialog(f, ASK_REMOVE_MSG, I18n.getText("yes"), I18n.getText("no"));
            d.showDialog();
            if (d.getResponse() != JOptionPane.YES_OPTION) return;
            List<File> cs = catalogList.getSelectedValuesList();
            for (File dir : cs) {
                catalogListModel.removeElement(dir);
                catalogs.remove(dir);
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
        CatalogListRenderer r = new CatalogListRenderer();
        r.setForeColor(foreColor);
        r.setSelectedColor(selectedColor);
        r.setTextColor(textColor);
        catalogList.setCellRenderer(r);
        catalogList.setModel(catalogListModel);
        catalogList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = catalogList.locationToIndex(e.getPoint());
                Rectangle bounds = catalogList.getCellBounds(index, index);
                if (bounds == null) return;
                setHoverIndex(bounds.contains(e.getPoint()) ? index : -1);
            }

            private void setHoverIndex(int index) {
                CatalogListRenderer renderer = (CatalogListRenderer) catalogList.getCellRenderer();
                if (renderer == null) return;
                int hoverIndex = renderer.getHoverIndex();
                if (hoverIndex == index) return;
                renderer.setHoverIndex(index);
                repaint();
            }
        });
        catalogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                CatalogListRenderer renderer = (CatalogListRenderer) catalogList.getCellRenderer();
                if (renderer == null) return;
                renderer.setHoverIndex(-1);
                repaint();
            }
        });
        catalogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标左键双击打开资源管理器
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    locateButton.doClick();
                }
            }
        });
        // 注意：将 JList 加到 JScrollPane 时必须使用构造器，而不是 add ！！！
        CustomScrollPane sp = new CustomScrollPane(catalogList);
        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        sp.setHBarUI(new ScrollBarUI(scrollBarColor));
        sp.setVBarUI(new ScrollBarUI(scrollBarColor));
        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        bottomBox.add(sp);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);
    }

    // 初始化数据
    private void initCatalogs() {
        for (File dir : catalogs) catalogListModel.addElement(dir);
    }
}
