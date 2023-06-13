package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.SimplePath;
import net.doge.models.entities.*;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;
import net.doge.utils.TimeUtil;

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
public class ItemRecommendListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon playlistIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "playlistItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon albumIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "albumItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon artistIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "artistItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon radioIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon mvIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon rankingIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "rankingItem.png"), ImageConstants.mediumWidth));
    private static ImageIcon userIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.mediumWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        playlistIcon = ImageUtil.dye(playlistIcon, iconColor);
        albumIcon = ImageUtil.dye(albumIcon, iconColor);
        artistIcon = ImageUtil.dye(artistIcon, iconColor);
        radioIcon = ImageUtil.dye(radioIcon, iconColor);
        mvIcon = ImageUtil.dye(mvIcon, iconColor);
        rankingIcon = ImageUtil.dye(rankingIcon, iconColor);
        userIcon = ImageUtil.dye(userIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        if (value instanceof NetPlaylistInfo) {
//            NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel creatorLabel = new CustomLabel();
//            CustomLabel playCountLabel = new CustomLabel();
//            CustomLabel trackCountLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(40);
//            iconLabel.setIcon(netPlaylistInfo.hasCoverImgThumb() ? new ImageIcon(netPlaylistInfo.getCoverImgThumb()) : playlistIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            creatorLabel.setForeground(textColor);
//            playCountLabel.setForeground(textColor);
//            trackCountLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            creatorLabel.setFont(customFont);
//            playCountLabel.setFont(customFont);
//            trackCountLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(creatorLabel);
//            outerPanel.add(trackCountLabel);
//            outerPanel.add(playCountLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netPlaylistInfo.getSource()]);
//            String name = netPlaylistInfo.hasName() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getName(), maxWidth)) : "";
//            String creator = netPlaylistInfo.hasCreator() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getCreator(), maxWidth)) : "";
//            String playCount = netPlaylistInfo.hasPlayCount() ? StringUtil.formatNumber(netPlaylistInfo.getPlayCount()) : "";
//            String trackCount = netPlaylistInfo.hasTrackCount() ? netPlaylistInfo.getTrackCount() + " 歌曲" : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            creatorLabel.setText(creator);
//            playCountLabel.setText(playCount);
//            trackCountLabel.setText(trackCount);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            Dimension ps3 = creatorLabel.getPreferredSize();
//            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel creatorLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel trackCountLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(netPlaylistInfo.hasCoverImgThumb() ? new ImageIcon(netPlaylistInfo.getCoverImgThumb()) : playlistIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            creatorLabel.setForeground(textColor);
            playCountLabel.setForeground(textColor);
            trackCountLabel.setForeground(textColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            creatorLabel.setFont(tinyFont);
            playCountLabel.setFont(tinyFont);
            trackCountLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            creatorLabel.setBluntAlpha(alpha);
            playCountLabel.setBluntAlpha(alpha);
            trackCountLabel.setBluntAlpha(alpha);

            BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
            outerPanel.setLayout(layout);

            final int sh = 10;
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(iconLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(nameLabel);
            outerPanel.add(Box.createVerticalGlue());
            outerPanel.add(creatorLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(trackCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(playCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getName(), tw));
            String creator = netPlaylistInfo.hasCreator() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netPlaylistInfo.getCreator(), tw)) : "";
            String playCount = netPlaylistInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netPlaylistInfo.getPlayCount())) : "";
            String trackCount = netPlaylistInfo.hasTrackCount() ? StringUtil.textToHtml(netPlaylistInfo.getTrackCount() + " 歌曲") : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            creatorLabel.setText(creator);
            playCountLabel.setText(playCount);
            trackCountLabel.setText(trackCount);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetAlbumInfo) {
//            NetAlbumInfo netAlbumInfo = (NetAlbumInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel artistLabel = new CustomLabel();
//            CustomLabel songNumLabel = new CustomLabel();
//            CustomLabel publishTimeLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(40);
//            iconLabel.setIcon(netAlbumInfo.hasCoverImgThumb() ? new ImageIcon(netAlbumInfo.getCoverImgThumb()) : albumIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            artistLabel.setForeground(textColor);
//            songNumLabel.setForeground(textColor);
//            publishTimeLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            artistLabel.setFont(customFont);
//            songNumLabel.setFont(customFont);
//            publishTimeLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(artistLabel);
//            outerPanel.add(songNumLabel);
//            outerPanel.add(publishTimeLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netAlbumInfo.getSource()]);
//            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netAlbumInfo.getName(), maxWidth));
//            String artist = netAlbumInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netAlbumInfo.getArtist(), maxWidth)) : "";
//            String songNum = netAlbumInfo.hasSongNum() ? netAlbumInfo.isPhoto() ? netAlbumInfo.getSongNum() + " 图片" : netAlbumInfo.getSongNum() + " 歌曲" : "";
//            String publishTime = netAlbumInfo.hasPublishTime() ? netAlbumInfo.getPublishTime() + " 发行" : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            artistLabel.setText(artist);
//            songNumLabel.setText(songNum);
//            publishTimeLabel.setText(publishTime);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            Dimension ps3 = artistLabel.getPreferredSize();
//            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
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

            final int pw = 200, tw = pw - 20;
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
        } else if (value instanceof NetArtistInfo) {
//            NetArtistInfo netArtistInfo = (NetArtistInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel songNumLabel = new CustomLabel();
//            CustomLabel albumNumLabel = new CustomLabel();
//            CustomLabel mvNumLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(40);
//            iconLabel.setIcon(netArtistInfo.hasCoverImgThumb() ? new ImageIcon(netArtistInfo.getCoverImgThumb()) : artistIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            songNumLabel.setForeground(textColor);
//            albumNumLabel.setForeground(textColor);
//            mvNumLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            songNumLabel.setFont(customFont);
//            albumNumLabel.setFont(customFont);
//            mvNumLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(songNumLabel);
//            outerPanel.add(albumNumLabel);
//            outerPanel.add(mvNumLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netArtistInfo.getSource()]);
//            String name = netArtistInfo.hasName() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(netArtistInfo.getName(), maxWidth)) : "";
//            String songNum = netArtistInfo.hasSongNum() ? netArtistInfo.fromME() ? netArtistInfo.getSongNum() + " 电台" : netArtistInfo.getSongNum() + " 歌曲" : "";
//            String albumNum = netArtistInfo.hasAlbumNum() ? netArtistInfo.getAlbumNum() + " 专辑" : "";
//            String mvNum = netArtistInfo.hasMvNum() ? netArtistInfo.getMvNum() + " MV" : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            songNumLabel.setText(songNum);
//            albumNumLabel.setText(albumNum);
//            mvNumLabel.setText(mvNum);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            int ph = Math.max(ps.height, ps2.height);
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetArtistInfo netArtistInfo = (NetArtistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel songNumLabel = new CustomLabel();
            CustomLabel albumNumLabel = new CustomLabel();
            CustomLabel mvNumLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(netArtistInfo.hasCoverImgThumb() ? new ImageIcon(netArtistInfo.getCoverImgThumb()) : artistIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            songNumLabel.setForeground(textColor);
            albumNumLabel.setForeground(textColor);
            mvNumLabel.setForeground(textColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            songNumLabel.setFont(tinyFont);
            albumNumLabel.setFont(tinyFont);
            mvNumLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            songNumLabel.setBluntAlpha(alpha);
            albumNumLabel.setBluntAlpha(alpha);
            mvNumLabel.setBluntAlpha(alpha);

            BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
            outerPanel.setLayout(layout);

            final int sh = 10;
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(iconLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(nameLabel);
            outerPanel.add(Box.createVerticalGlue());
            outerPanel.add(songNumLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(albumNumLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(mvNumLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netArtistInfo.getName(), tw));
            String songNum = netArtistInfo.hasSongNum() ? StringUtil.textToHtml(netArtistInfo.fromME() ? netArtistInfo.getSongNum() + " 电台" : netArtistInfo.getSongNum() + " 歌曲") : "";
            String albumNum = netArtistInfo.hasAlbumNum() ? StringUtil.textToHtml(netArtistInfo.getAlbumNum() + " 专辑") : "";
            String mvNum = netArtistInfo.hasMvNum() ? StringUtil.textToHtml(netArtistInfo.getMvNum() + " MV") : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            songNumLabel.setText(songNum);
            albumNumLabel.setText(albumNum);
            mvNumLabel.setText(mvNum);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetRadioInfo) {
//            NetRadioInfo netRadioInfo = (NetRadioInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel djLabel = new CustomLabel();
//            CustomLabel categoryLabel = new CustomLabel();
//            CustomLabel trackCountLabel = new CustomLabel();
//            CustomLabel playCountLabel = new CustomLabel();
////        CustomLabel createTimeLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(25);
//            iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            djLabel.setForeground(textColor);
//            categoryLabel.setForeground(textColor);
//            trackCountLabel.setForeground(textColor);
//            playCountLabel.setForeground(textColor);
////        createTimeLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            djLabel.setFont(customFont);
//            categoryLabel.setFont(customFont);
//            trackCountLabel.setFont(customFont);
//            playCountLabel.setFont(customFont);
////        createTimeLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(djLabel);
//            outerPanel.add(categoryLabel);
//            outerPanel.add(trackCountLabel);
//            outerPanel.add(playCountLabel);
////        outerPanel.add(createTimeLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netRadioInfo.getSource()]);
//            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.getName(), maxWidth));
//            String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", maxWidth));
//            String category = netRadioInfo.hasCategory() ? netRadioInfo.getCategory() : "";
//            String trackCount = netRadioInfo.hasTrackCount() ? netRadioInfo.getTrackCount() + " 节目" : "";
//            String playCount = netRadioInfo.hasPlayCount() ? StringUtil.formatNumber(netRadioInfo.getPlayCount()) : "";
////        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            djLabel.setText(dj);
//            categoryLabel.setText(category);
//            trackCountLabel.setText(trackCount);
//            playCountLabel.setText(playCount);
////        createTimeLabel.setText(createTime);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            Dimension ps3 = djLabel.getPreferredSize();
//            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetRadioInfo netRadioInfo = (NetRadioInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel dCustomLabel = new CustomLabel();
            CustomLabel categoryLabel = new CustomLabel();
            CustomLabel trackCountLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
//        CustomLabel createTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            dCustomLabel.setForeground(textColor);
            categoryLabel.setForeground(textColor);
            trackCountLabel.setForeground(textColor);
            playCountLabel.setForeground(textColor);
//        createTimeLabel.setForeground(textColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            dCustomLabel.setFont(tinyFont);
            categoryLabel.setFont(tinyFont);
            trackCountLabel.setFont(tinyFont);
            playCountLabel.setFont(tinyFont);
//        createTimeLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            dCustomLabel.setBluntAlpha(alpha);
            categoryLabel.setBluntAlpha(alpha);
            trackCountLabel.setBluntAlpha(alpha);
            playCountLabel.setBluntAlpha(alpha);

            BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
            outerPanel.setLayout(layout);

            final int sh = 10;
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(iconLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(nameLabel);
            outerPanel.add(Box.createVerticalGlue());
            outerPanel.add(dCustomLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(categoryLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(trackCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(playCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.getName(), tw));
            String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", tw));
            String category = netRadioInfo.hasCategory() ? StringUtil.textToHtml(netRadioInfo.getCategory()) : "";
            String trackCount = netRadioInfo.hasTrackCount() ? StringUtil.textToHtml(netRadioInfo.getTrackCount() + " 节目") : "";
            String playCount = netRadioInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netRadioInfo.getPlayCount())) : "";
//        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            dCustomLabel.setText(dj);
            categoryLabel.setText(category);
            trackCountLabel.setText(trackCount);
            playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetMvInfo) {
//            NetMvInfo netMvInfo = (NetMvInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel artistLabel = new CustomLabel();
//            CustomLabel durationLabel = new CustomLabel();
//            CustomLabel playCountLabel = new CustomLabel();
//            CustomLabel pubTimeLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(10);
//            iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : mvIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            artistLabel.setForeground(textColor);
//            durationLabel.setForeground(textColor);
//            playCountLabel.setForeground(textColor);
//            pubTimeLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            artistLabel.setFont(customFont);
//            durationLabel.setFont(customFont);
//            playCountLabel.setFont(customFont);
//            pubTimeLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(artistLabel);
//            outerPanel.add(durationLabel);
//            outerPanel.add(playCountLabel);
//            outerPanel.add(pubTimeLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netMvInfo.getSource()]);
//            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getName(), maxWidth));
//            String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getArtist(), maxWidth));
//            String duration = netMvInfo.hasDuration() ? TimeUtil.format(netMvInfo.getDuration()) : "--:--";
//            String playCount = netMvInfo.hasPlayCount() ? StringUtil.formatNumber(netMvInfo.getPlayCount()) : "";
//            String pubTime = netMvInfo.hasPubTime() ? netMvInfo.getPubTime() : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            artistLabel.setText(artist);
//            durationLabel.setText(duration);
//            playCountLabel.setText(playCount);
//            pubTimeLabel.setText(pubTime);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            Dimension ps3 = artistLabel.getPreferredSize();
//            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetMvInfo netMvInfo = (NetMvInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel artistLabel = new CustomLabel();
            CustomLabel durationLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel pubTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : mvIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            artistLabel.setForeground(textColor);
            durationLabel.setForeground(textColor);
            playCountLabel.setForeground(textColor);
            pubTimeLabel.setForeground(textColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            artistLabel.setFont(tinyFont);
            durationLabel.setFont(tinyFont);
            playCountLabel.setFont(tinyFont);
            pubTimeLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            artistLabel.setBluntAlpha(alpha);
            durationLabel.setBluntAlpha(alpha);
            playCountLabel.setBluntAlpha(alpha);
            pubTimeLabel.setBluntAlpha(alpha);

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
            outerPanel.add(durationLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(playCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(pubTimeLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getName(), tw));
            String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getArtist(), tw));
            String duration = StringUtil.textToHtml(netMvInfo.hasDuration() ? TimeUtil.format(netMvInfo.getDuration()) : "--:--");
            String playCount = netMvInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netMvInfo.getPlayCount())) : "";
            String pubTime = netMvInfo.hasPubTime() ? StringUtil.textToHtml(netMvInfo.getPubTime()) : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            artistLabel.setText(artist);
            durationLabel.setText(duration);
            playCountLabel.setText(playCount);
            pubTimeLabel.setText(pubTime);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetRankingInfo) {
//            NetRankingInfo netRankingInfo = (NetRankingInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel iconLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel playCountLabel = new CustomLabel();
//            CustomLabel updateFreLabel = new CustomLabel();
//            CustomLabel updateTimeLabel = new CustomLabel();
//
//            iconLabel.setHorizontalTextPosition(LEFT);
//            iconLabel.setIconTextGap(40);
//            iconLabel.setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : rankingIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            iconLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            playCountLabel.setForeground(textColor);
//            updateFreLabel.setForeground(textColor);
//            updateTimeLabel.setForeground(textColor);
//
//            iconLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            playCountLabel.setFont(customFont);
//            updateFreLabel.setFont(customFont);
//            updateTimeLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(iconLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(playCountLabel);
//            outerPanel.add(updateFreLabel);
//            outerPanel.add(updateTimeLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netRankingInfo.getSource()]);
//            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRankingInfo.getName(), maxWidth));
//            String playCount = netRankingInfo.hasPlayCount() ? StringUtil.formatNumber(netRankingInfo.getPlayCount()) : "";
//            String updateFre = netRankingInfo.hasUpdateFre() ? netRankingInfo.getUpdateFre() : "";
//            String updateTime = netRankingInfo.hasUpdateTime() ? netRankingInfo.getUpdateTime() + " 更新" : "";
//
//            iconLabel.setText(source);
//            nameLabel.setText(name);
//            playCountLabel.setText(playCount);
//            updateFreLabel.setText(updateFre);
//            updateTimeLabel.setText(updateTime);
//
//            Dimension ps = iconLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            int ph = Math.max(ps.height, ps2.height);
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetRankingInfo netRankingInfo = (NetRankingInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel updateFreLabel = new CustomLabel();
            CustomLabel updateTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : rankingIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            playCountLabel.setForeground(textColor);
            updateFreLabel.setForeground(textColor);
            updateTimeLabel.setForeground(textColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            playCountLabel.setFont(tinyFont);
            updateFreLabel.setFont(tinyFont);
            updateTimeLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            playCountLabel.setBluntAlpha(alpha);
            updateFreLabel.setBluntAlpha(alpha);
            updateTimeLabel.setBluntAlpha(alpha);

            BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
            outerPanel.setLayout(layout);

            final int sh = 10;
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(iconLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(nameLabel);
            outerPanel.add(Box.createVerticalGlue());
            outerPanel.add(playCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(updateFreLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(updateTimeLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRankingInfo.getName(), tw));
            String playCount = netRankingInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netRankingInfo.getPlayCount())) : "";
            String updateFre = netRankingInfo.hasUpdateFre() ? StringUtil.textToHtml(netRankingInfo.getUpdateFre()) : "";
            String updateTime = netRankingInfo.hasUpdateTime() ? StringUtil.textToHtml(netRankingInfo.getUpdateTime() + " 更新") : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            playCountLabel.setText(playCount);
            updateFreLabel.setText(updateFre);
            updateTimeLabel.setText(updateTime);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetUserInfo) {
//            NetUserInfo netUserInfo = (NetUserInfo) value;
//
//            CustomPanel outerPanel = new CustomPanel();
//            CustomLabel avatarLabel = new CustomLabel();
//            CustomLabel nameLabel = new CustomLabel();
//            CustomLabel genderLabel = new CustomLabel();
////        CustomLabel birthdayLabel = new CustomLabel();
////        CustomLabel areaLabel = new CustomLabel();
//            CustomLabel followLabel = new CustomLabel();
//            CustomLabel followedLabel = new CustomLabel();
//            CustomLabel playlistCountLabel = new CustomLabel();
//
//            avatarLabel.setHorizontalTextPosition(LEFT);
//            avatarLabel.setIconTextGap(25);
//            avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : userIcon);
//
//            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//            avatarLabel.setForeground(textColor);
//            nameLabel.setForeground(textColor);
//            genderLabel.setForeground(textColor);
////        birthdayLabel.setForeground(textColor);
////        areaLabel.setForeground(textColor);
//            followLabel.setForeground(textColor);
//            followedLabel.setForeground(textColor);
//            playlistCountLabel.setForeground(textColor);
//
//            avatarLabel.setFont(customFont);
//            nameLabel.setFont(customFont);
//            genderLabel.setFont(customFont);
////        birthdayLabel.setFont(customFont);
////        areaLabel.setFont(customFont);
//            followLabel.setFont(customFont);
//            followedLabel.setFont(customFont);
//            playlistCountLabel.setFont(customFont);
//
//            GridLayout layout = new GridLayout(1, 5);
//            layout.setHgap(15);
//            outerPanel.setLayout(layout);
//
//            outerPanel.add(avatarLabel);
//            outerPanel.add(nameLabel);
//            outerPanel.add(genderLabel);
////        outerPanel.add(birthdayLabel);
////        outerPanel.add(areaLabel);
//            outerPanel.add(playlistCountLabel);
//            outerPanel.add(followLabel);
//            outerPanel.add(followedLabel);
//
//            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//            String source = StringUtil.textToHtml(NetMusicSource.names[netUserInfo.getSource()]);
//            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netUserInfo.getName(), maxWidth));
//            String gender = netUserInfo.hasGender() ? netUserInfo.getGender() : "";
////        String birthday = netUserInfo.hasBirthday() ? netUserInfo.getBirthday() : "";
////        String area = netUserInfo.hasArea() ? netUserInfo.getArea() : "";
//            boolean hasRadioCount = netUserInfo.hasRadioCount(), hasProgramCount = netUserInfo.hasProgramCount();
//            String playlistCount = netUserInfo.hasPlaylistCount() ? netUserInfo.getPlaylistCount() + " 歌单"
//                    : hasRadioCount && hasProgramCount ? netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目"
//                    : hasRadioCount ? netUserInfo.getRadioCount() + " 电台"
//                    : hasProgramCount ? netUserInfo.getProgramCount() + (netUserInfo.fromDt() ? " 专辑" : netUserInfo.fromBI() ? " 视频" : " 节目")
//                    : "";
//            String follow = netUserInfo.hasFollow() ? StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注" : "";
//            String followed = netUserInfo.hasFollowed() ? StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝" : "";
//
//            avatarLabel.setText(source);
//            nameLabel.setText(name);
//            genderLabel.setText(gender);
////        birthdayLabel.setText(birthday);
////        areaLabel.setText(area);
//            playlistCountLabel.setText(playlistCount);
//            followLabel.setText(follow);
//            followedLabel.setText(followed);
//
//            Dimension ps = avatarLabel.getPreferredSize();
//            Dimension ps2 = nameLabel.getPreferredSize();
//            int ph = Math.max(ps.height, ps2.height);
//            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//            outerPanel.setPreferredSize(d);
//            list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//            outerPanel.setBluntDrawBg(true);
//            outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//            return outerPanel;
            NetUserInfo netUserInfo = (NetUserInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel avatarLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel genderLabel = new CustomLabel();
//        CustomLabel birthdayLabel = new CustomLabel();
//        CustomLabel areaLabel = new CustomLabel();
            CustomLabel followLabel = new CustomLabel();
            CustomLabel followedLabel = new CustomLabel();
            CustomLabel playlistCountLabel = new CustomLabel();

            avatarLabel.setIconTextGap(0);
            avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : userIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            avatarLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            genderLabel.setForeground(textColor);
//        birthdayLabel.setForeground(textColor);
//        areaLabel.setForeground(textColor);
            followLabel.setForeground(textColor);
            followedLabel.setForeground(textColor);
            playlistCountLabel.setForeground(textColor);

            avatarLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            genderLabel.setFont(tinyFont);
//        birthdayLabel.setFont(tinyFont);
//        areaLabel.setFont(tinyFont);
            followLabel.setFont(tinyFont);
            followedLabel.setFont(tinyFont);
            playlistCountLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            genderLabel.setBluntAlpha(alpha);
            followLabel.setBluntAlpha(alpha);
            followedLabel.setBluntAlpha(alpha);
            playlistCountLabel.setBluntAlpha(alpha);

            BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
            outerPanel.setLayout(layout);

            final int sh = 10;
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(avatarLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(nameLabel);
            outerPanel.add(Box.createVerticalGlue());
            outerPanel.add(genderLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(followLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(followedLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(playlistCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = 200, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netUserInfo.getName(), tw));
            String gender = netUserInfo.hasGender() ? StringUtil.textToHtml(netUserInfo.getGender()) : "";
            boolean hasRadioCount = netUserInfo.hasRadioCount(), hasProgramCount = netUserInfo.hasProgramCount();
            String playlistCount = netUserInfo.hasPlaylistCount() ? StringUtil.textToHtml(netUserInfo.getPlaylistCount() + " 歌单")
                    : hasRadioCount && hasProgramCount ? StringUtil.textToHtml(netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目")
                    : hasRadioCount ? StringUtil.textToHtml(netUserInfo.getRadioCount() + " 电台")
                    : hasProgramCount ? StringUtil.textToHtml(netUserInfo.getProgramCount() + (netUserInfo.fromDt() ? " 专辑" : netUserInfo.fromBI() ? " 视频" : " 节目"))
                    : "";
            String follow = netUserInfo.hasFollow() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注") : "";
            String followed = netUserInfo.hasFollowed() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝") : "";

            avatarLabel.setText(source);
            nameLabel.setText(name);
            genderLabel.setText(gender);
            playlistCountLabel.setText(playlistCount);
            followLabel.setText(follow);
            followedLabel.setText(followed);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        }

        return this;
    }
}
