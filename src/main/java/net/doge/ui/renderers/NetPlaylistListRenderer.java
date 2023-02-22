package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.entity.NetPlaylistInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetPlaylistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private ImageIcon playlistIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "playlistItem.png"), ImageConstants.profileWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        playlistIcon = ImageUtil.dye(playlistIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel creatorLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel trackCountLabel = new CustomLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(40);
        iconLabel.setIcon(netPlaylistInfo.hasCoverImgThumb() ? new ImageIcon(netPlaylistInfo.getCoverImgThumb()) : playlistIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        creatorLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        creatorLabel.setFont(customFont);
        playCountLabel.setFont(customFont);
        trackCountLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(creatorLabel);
        outerPanel.add(trackCountLabel);
        outerPanel.add(playCountLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtil.textToHtml(NetMusicSource.names[netPlaylistInfo.getSource()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getName(), maxWidth));
        String creator = netPlaylistInfo.hasCreator() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getCreator(), maxWidth)) : "";
        String playCount = netPlaylistInfo.hasPlayCount() ? StringUtil.formatNumber(netPlaylistInfo.getPlayCount()) : "";
        String trackCount = netPlaylistInfo.hasTrackCount() ? netPlaylistInfo.getTrackCount() + " 歌曲" : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        creatorLabel.setText(creator);
        playCountLabel.setText(playCount);
        trackCountLabel.setText(trackCount);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        Dimension ps3 = creatorLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
