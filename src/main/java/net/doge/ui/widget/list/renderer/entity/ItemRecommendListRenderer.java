package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.model.entity.*;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRecommendListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

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
            return playlistListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetAlbumInfo)
            return albumListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetArtistInfo)
            return artistListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetRadioInfo)
            return radioListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetMvInfo)
            return mvListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetRankingInfo)
            return rankingListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        else if (value instanceof NetUserInfo)
            return userListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        return this;
    }
}
