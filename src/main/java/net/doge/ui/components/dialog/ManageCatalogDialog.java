package net.doge.ui.components.dialog;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import net.doge.constants.Colors;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.components.dialog.factory.AbstractTitledDialog;
import net.doge.ui.componentui.ScrollBarUI;
import net.doge.ui.renderers.DefaultCatalogListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author yzx
 * @Description 管理歌曲目录的对话框
 * @Date 2020/12/15
 */
public class ManageCatalogDialog extends AbstractTitledDialog {
    private final String ASK_REMOVE_MSG = "确定删除选中的目录？";
    private final String CATALOG_EXISTS_MSG = "目录已存在，无法重复添加";
    private final String CATALOG_NOT_FOUND_MSG = "该目录不存在";

    private CustomPanel centerPanel = new CustomPanel();

    private CustomLabel tipLabel = new CustomLabel("重新导入歌曲时将从以下目录查找歌曲");
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

    public ManageCatalogDialog(PlayerFrame f) {
        super(f, "管理歌曲目录");
        this.catalogs = f.catalogs;

        Color textColor = f.currUIStyle.getTextColor();
        allSelectButton = new DialogButton("全选", textColor);
        nonSelectButton = new DialogButton("反选", textColor);
        locateButton = new DialogButton("打开", textColor);
        addButton = new DialogButton("添加", textColor);
        removeButton = new DialogButton("删除", textColor);
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
            catalogList.getSelectionModel().setSelectionInterval(0, catalogListModel.getSize() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> catalogList.clearSelection());
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
                if (dir == null) return;
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
            });
        });
        // 删除事件
        removeButton.addActionListener(e -> {
            if (catalogList.getSelectedValue() == null) return;
            ConfirmDialog d = new ConfirmDialog(f, ASK_REMOVE_MSG, "是", "否");
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
        DefaultCatalogListRenderer r = new DefaultCatalogListRenderer();
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
                DefaultCatalogListRenderer renderer = (DefaultCatalogListRenderer) catalogList.getCellRenderer();
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
                DefaultCatalogListRenderer renderer = (DefaultCatalogListRenderer) catalogList.getCellRenderer();
                if (renderer == null) return;
                renderer.setHoverIndex(-1);
                repaint();
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
        CustomScrollPane sp = new CustomScrollPane(catalogList);
        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        sp.setHUI(new ScrollBarUI(scrollBarColor));
        sp.setVUI(new ScrollBarUI(scrollBarColor));
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
