package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.constant.core.ui.tab.PersonalMusicTabIndex;
import net.doge.entity.core.ui.UIStyle;
import net.doge.entity.service.AudioFile;
import net.doge.entity.service.LocalPlaylist;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.base.MusicResource;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.CustomList;
import net.doge.ui.widget.list.entity.ChoosableListItem;
import net.doge.ui.widget.list.renderer.core.LocalPlaylistListRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.util.core.collection.ListUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Doge
 * @description 收藏歌曲的对话框
 * @date 2020/12/15
 */
public class AddToFavoritesDialog extends AbstractTitledDialog {
    private final int WIDTH = ScaleUtil.scale(600);
    private final int HEIGHT = ScaleUtil.scale(500);
    private final String TIP_MSG = I18n.getText("addToFavoritesTip");
    private final String MULTI_TIP_MSG = I18n.getText("addMultiToFavoritesTip");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel bottomPanel = new CustomPanel();

    private CustomLabel tipLabel = new CustomLabel();
    private CustomList<ChoosableListItem<LocalPlaylist>> localPlaylistList = new CustomList<>();
    private DefaultListModel<ChoosableListItem<LocalPlaylist>> localPlaylistListModel = new DefaultListModel<>();
    private DialogButton newButton;
    private DialogButton allSelectButton;
    private DialogButton nonSelectButton;
    private DialogButton addButton;

    // 底部盒子
    private CustomBox bottomBox = new CustomBox(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private CustomBox rightBox = new CustomBox(BoxLayout.Y_AXIS);

    private List<LocalPlaylist> playlists;
    private List<MusicResource> resources;
    private MusicResource firstResource;

    public AddToFavoritesDialog(MainFrame f, MusicResource resource) {
        this(f, ListUtil.of(resource));
    }

    public AddToFavoritesDialog(MainFrame f, List<MusicResource> resources) {
        super(f, I18n.getText("addToFavoritesTitle"));
        this.playlists = f.collectionPlaylists;
        this.resources = resources;
        firstResource = resources.get(0);

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        newButton = new DialogButton(I18n.getText("dialogNew"), textColor);
        allSelectButton = new DialogButton(I18n.getText("dialogAll"), textColor);
        nonSelectButton = new DialogButton(I18n.getText("dialogInvert"), textColor);
        addButton = new DialogButton(I18n.getText("dialogAdd"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initLocalPlaylists();

        bottomPanel.add(addButton);
        bottomPanel.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        globalPanel.add(bottomPanel, BorderLayout.SOUTH);

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

        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color foreColor = style.getForeColor();
        Color selectedColor = style.getSelectedColor();

        // 添加标签
        tipLabel.setBorder(new HDEmptyBorder(10, 10, 10, 10));
        tipLabel.setForeground(textColor);
        String name = null;
        if (firstResource instanceof AudioFile) {
            AudioFile audioFile = (AudioFile) firstResource;
            name = audioFile.toString();
        } else if (firstResource instanceof NetMusicInfo) {
            NetMusicInfo musicInfo = (NetMusicInfo) firstResource;
            name = musicInfo.toSimpleString();
        }
        int size = resources.size();
        if (size <= 1) tipLabel.setText(String.format(TIP_MSG, name));
        else tipLabel.setText(String.format(MULTI_TIP_MSG, name, size));
        centerPanel.add(tipLabel, BorderLayout.NORTH);
        // 新建
        newButton.addActionListener(e -> {
            CreateLocalPlaylistDialog d = new CreateLocalPlaylistDialog(f);
            d.showDialog();
            if (!d.isConfirmed()) return;
            String playlistName = d.getResult();
            LocalPlaylist playlist = new LocalPlaylist(playlistName);
            // 为新的 ListModel 添加监听器
            DefaultListModel<MusicResource> newModel = playlist.getMusicListModel();
            newModel.addListDataListener(f.countListener);
            f.collectionPlaylists.add(playlist);
            localPlaylistListModel.addElement(new ChoosableListItem<>(playlist));
            if (f.currPersonalMusicTab == PersonalMusicTabIndex.COLLECTION) f.localPlaylistComboBox.addItem(playlist);
        });
        // 全选事件
        allSelectButton.addActionListener(e -> {
            // 选择开始到结束(包含)的节点！
            localPlaylistList.getSelectionModel().setSelectionInterval(0, localPlaylistListModel.size() - 1);
        });
        // 取消全选事件
        nonSelectButton.addActionListener(e -> localPlaylistList.clearSelection());
        // 添加事件
        addButton.addActionListener(e -> {
            for (int i = 0, len = localPlaylistListModel.size(); i < len; i++) {
                ChoosableListItem<LocalPlaylist> item = localPlaylistListModel.get(i);
                LocalPlaylist playlist = item.getItem();
                DefaultListModel<MusicResource> musicListModel = playlist.getMusicListModel();
                // 收藏
                if (item.isSelected()) {
                    for (MusicResource resource : resources) {
                        if (musicListModel.contains(resource)) continue;
                        musicListModel.add(0, resource);
                    }
                }
                // 取消收藏
                else {
                    for (MusicResource resource : resources) musicListModel.removeElement(resource);
                }
            }
            close();
        });

        // 添加右部按钮
        rightBox.setBorder(new HDEmptyBorder(0, 10, 10, 10));
        Dimension area = new HDDimension(1, 10);
        rightBox.add(CustomBox.createVerticalGlue());
        rightBox.add(newButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(allSelectButton);
        rightBox.add(CustomBox.createRigidArea(area));
        rightBox.add(nonSelectButton);
        rightBox.add(CustomBox.createVerticalGlue());
        // 添加列表和右部按钮整体
        LocalPlaylistListRenderer r = new LocalPlaylistListRenderer();
        r.setForeColor(foreColor);
        r.setSelectedColor(selectedColor);
        r.setTextColor(textColor);
        localPlaylistList.setCellRenderer(r);
        localPlaylistList.setModel(localPlaylistListModel);
        localPlaylistList.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = localPlaylistList.locationToIndex(e.getPoint());
                Rectangle bounds = localPlaylistList.getCellBounds(index, index);
                if (bounds == null) return;
                setHoverIndex(bounds.contains(e.getPoint()) ? index : -1);
            }

            private void setHoverIndex(int index) {
                LocalPlaylistListRenderer renderer = (LocalPlaylistListRenderer) localPlaylistList.getCellRenderer();
                if (renderer == null) return;
                int hoverIndex = renderer.getHoverIndex();
                if (hoverIndex == index) return;
                renderer.setHoverIndex(index);
                repaint();
            }
        });
        localPlaylistList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // 左键进行复选
                if (e.getButton() == MouseEvent.BUTTON1) {
                    switchLocalPlaylistSelection(localPlaylistList.getSelectedValue());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                LocalPlaylistListRenderer renderer = (LocalPlaylistListRenderer) localPlaylistList.getCellRenderer();
                if (renderer == null) return;
                renderer.setHoverIndex(-1);
                repaint();
            }
        });
        // 注意：将 JList 加到 JScrollPane 时必须使用构造器，而不是 add ！！！
        CustomScrollPane sp = new CustomScrollPane(localPlaylistList);
        sp.setBorder(new HDEmptyBorder(0, 10, 10, 0));
        bottomBox.add(sp);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);
    }

    // 复选
    private void switchLocalPlaylistSelection(ChoosableListItem<LocalPlaylist> item) {
        if (item == null) return;
        item.setSelected(!item.isSelected());
        localPlaylistList.repaint();
    }

    // 初始化数据
    private void initLocalPlaylists() {
        boolean noOneContains = true;
        for (LocalPlaylist playlist : playlists) {
            boolean contains = playlist.getMusicListModel().contains(firstResource);
            if (contains) noOneContains = false;
            localPlaylistListModel.addElement(new ChoosableListItem<>(playlist, contains));
        }
        // 没有收藏过的资源，默认选中第一个收藏夹
        if (noOneContains && !playlists.isEmpty()) {
            switchLocalPlaylistSelection(localPlaylistListModel.get(0));
        }
    }
}
