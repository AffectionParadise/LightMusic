package net.doge.ui.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.NetAlbumInfo;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.ui.ImageUtil;
import net.doge.util.common.StringUtil;

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
public class NetAlbumListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon albumIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "albumItem.png"), ImageConstants.MEDIUM_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        albumIcon = ImageUtil.dye(albumIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetAlbumInfo netAlbumInfo = (NetAlbumInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel artistLabel = new CustomLabel();
        CustomLabel songNumLabel = new CustomLabel();
        CustomLabel publishTimeLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(netAlbumInfo.hasCoverImgThumb() ? new ImageIcon(netAlbumInfo.getCoverImgThumb()) : albumIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        songNumLabel.setForeground(textColor);
        publishTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        artistLabel.setFont(tinyFont);
        songNumLabel.setFont(tinyFont);
        publishTimeLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        artistLabel.setBluntAlpha(alpha);
        songNumLabel.setBluntAlpha(alpha);
        publishTimeLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(artistLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(songNumLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(publishTimeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = 180, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netAlbumInfo.getName(), tw));
        String artist = netAlbumInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netAlbumInfo.getArtist(), tw)) : "";
        String songNum = netAlbumInfo.hasSongNum() ? StringUtil.textToHtml(netAlbumInfo.isPhoto() ? netAlbumInfo.getSongNum() + " 图片" : netAlbumInfo.getSongNum() + " 歌曲") : "";
        String publishTime = netAlbumInfo.hasPublishTime() ? StringUtil.textToHtml(netAlbumInfo.getPublishTime() + " 发行") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        songNumLabel.setText(songNum);
        publishTimeLabel.setText(publishTime);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
