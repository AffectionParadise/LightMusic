package net.doge.ui.widget.list.renderer.service;

import lombok.Data;
import net.doge.entity.service.*;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class ItemRecommendListRenderer extends CustomListCellRenderer {
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private Component root;

    private NetPlaylistListRenderer playlistListRenderer = new NetPlaylistListRenderer();
    private NetAlbumListRenderer albumListRenderer = new NetAlbumListRenderer();
    private NetArtistListRenderer artistListRenderer = new NetArtistListRenderer();
    private NetRadioListRenderer radioListRenderer = new NetRadioListRenderer();
    private NetMvListRenderer mvListRenderer = new NetMvListRenderer();
    private NetRankingListRenderer rankingListRenderer = new NetRankingListRenderer();
    private NetUserListRenderer userListRenderer = new NetUserListRenderer();

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        playlistListRenderer.setForeColor(foreColor);
        albumListRenderer.setForeColor(foreColor);
        artistListRenderer.setForeColor(foreColor);
        radioListRenderer.setForeColor(foreColor);
        mvListRenderer.setForeColor(foreColor);
        rankingListRenderer.setForeColor(foreColor);
        userListRenderer.setForeColor(foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        playlistListRenderer.setSelectedColor(selectedColor);
        albumListRenderer.setSelectedColor(selectedColor);
        artistListRenderer.setSelectedColor(selectedColor);
        radioListRenderer.setSelectedColor(selectedColor);
        mvListRenderer.setSelectedColor(selectedColor);
        rankingListRenderer.setSelectedColor(selectedColor);
        userListRenderer.setSelectedColor(selectedColor);
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        playlistListRenderer.setTextColor(textColor);
        albumListRenderer.setTextColor(textColor);
        artistListRenderer.setTextColor(textColor);
        radioListRenderer.setTextColor(textColor);
        mvListRenderer.setTextColor(textColor);
        rankingListRenderer.setTextColor(textColor);
        userListRenderer.setTextColor(textColor);
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        playlistListRenderer.setIconColor(iconColor);
        albumListRenderer.setIconColor(iconColor);
        artistListRenderer.setIconColor(iconColor);
        radioListRenderer.setIconColor(iconColor);
        mvListRenderer.setIconColor(iconColor);
        rankingListRenderer.setIconColor(iconColor);
        userListRenderer.setIconColor(iconColor);
    }

    public void setHoverIndex(int hoverIndex) {
        this.hoverIndex = hoverIndex;
        playlistListRenderer.setHoverIndex(hoverIndex);
        albumListRenderer.setHoverIndex(hoverIndex);
        artistListRenderer.setHoverIndex(hoverIndex);
        radioListRenderer.setHoverIndex(hoverIndex);
        mvListRenderer.setHoverIndex(hoverIndex);
        rankingListRenderer.setHoverIndex(hoverIndex);
        userListRenderer.setHoverIndex(hoverIndex);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof NetPlaylistInfo)
            return root = playlistListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetAlbumInfo)
            return root = albumListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetArtistInfo)
            return root = artistListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetRadioInfo)
            return root = radioListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetMvInfo)
            return root = mvListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetRankingInfo)
            return root = rankingListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetUserInfo)
            return root = userListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        return this;
    }

    @Override
    public Component getRootComponent() {
        return root;
    }
}
