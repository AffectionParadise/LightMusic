package net.doge.ui.widget.dialog;

import net.doge.constant.lang.I18n;
import net.doge.constant.tab.PersonalMusicTabIndex;
import net.doge.constant.ui.Colors;
import net.doge.model.entity.AudioFile;
import net.doge.model.entity.LocalPlaylist;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.base.MusicResource;
import net.doge.model.system.ChoosableListItem;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.CustomList;
import net.doge.ui.widget.list.renderer.system.LocalPlaylistListRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;
import net.doge.util.collection.ListUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @Author Doge
 * @Description 收藏歌曲的对话框
 * @Date 2020/12/15
 */
public class AddToFavoritesDialog extends AbstractTitledDialog {
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
    private Box bottomBox = new Box(BoxLayout.X_AXIS);
    // 右部按钮盒子
    private Box rightBox = new Box(BoxLayout.Y_AXIS);

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

        Color textColor = f.currUIStyle.getTextColor();
        newButton = new DialogButton(I18n.getText("dialogNew"), textColor);
        allSelectButton = new DialogButton(I18n.getText("dialogAll"), textColor);
        nonSelectButton = new DialogButton(I18n.getText("dialogInvert"), textColor);
        addButton = new DialogButton(I18n.getText("dialogAdd"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(600, 500);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        // 组装界面
        initView();
        // 初始化数据
        initLocalPlaylists();

        bottomPanel.add(addButton);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        globalPanel.add(centerPanel, BorderLayout.CENTER);

        Color textColor = f.currUIStyle.getTextColor();
        Color foreColor = f.currUIStyle.getForeColor();
        Color selectedColor = f.currUIStyle.getSelectedColor();

        // 添加标签
        tipLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
                        musicListModel.addElement(resource);
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
        rightBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        Dimension area = new Dimension(1, 10);
        rightBox.add(Box.createVerticalGlue());
        rightBox.add(newButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(allSelectButton);
        rightBox.add(Box.createRigidArea(area));
        rightBox.add(nonSelectButton);
        rightBox.add(Box.createVerticalGlue());
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
                    ChoosableListItem<LocalPlaylist> item = localPlaylistList.getSelectedValue();
                    if (item == null) return;
                    item.setSelected(!item.isSelected());
                    localPlaylistList.repaint();
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
        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        sp.setHUI(new ScrollBarUI(scrollBarColor));
        sp.setVUI(new ScrollBarUI(scrollBarColor));
        sp.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        bottomBox.add(sp);
        bottomBox.add(rightBox);
        centerPanel.add(bottomBox, BorderLayout.CENTER);
    }

    // 初始化数据
    private void initLocalPlaylists() {
        for (LocalPlaylist playlist : playlists)
            localPlaylistListModel.addElement(new ChoosableListItem<>(playlist, playlist.getMusicListModel().contains(firstResource)));
    }
}
