package net.doge.ui.renderers;

import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.*;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentItemRecommendListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon playlistIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "playlistItem.png"), ImageConstants.profileWidth));
    private ImageIcon albumIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "albumItem.png"), ImageConstants.profileWidth));
    private ImageIcon artistIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "artistItem.png"), ImageConstants.profileWidth));
    private ImageIcon radioIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.profileWidth));
    private ImageIcon mvIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.profileWidth));
    private ImageIcon rankingIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "rankingItem.png"), ImageConstants.profileWidth));
    private ImageIcon userIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.profileWidth));

    private ImageIcon playlistSIcon;
    private ImageIcon albumSIcon;
    private ImageIcon artistSIcon;
    private ImageIcon radioSIcon;
    private ImageIcon mvSIcon;
    private ImageIcon rankingSIcon;
    private ImageIcon userSIcon;

    public TranslucentItemRecommendListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        playlistIcon = ImageUtils.dye(playlistIcon, foreColor);
        albumIcon = ImageUtils.dye(albumIcon, foreColor);
        artistIcon = ImageUtils.dye(artistIcon, foreColor);
        radioIcon = ImageUtils.dye(radioIcon, foreColor);
        mvIcon = ImageUtils.dye(mvIcon, foreColor);
        rankingIcon = ImageUtils.dye(rankingIcon, foreColor);
        userIcon = ImageUtils.dye(userIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        playlistSIcon = ImageUtils.dye(playlistIcon, selectedColor);
        albumSIcon = ImageUtils.dye(albumIcon, selectedColor);
        artistSIcon = ImageUtils.dye(artistIcon, selectedColor);
        radioSIcon = ImageUtils.dye(radioIcon, selectedColor);
        mvSIcon = ImageUtils.dye(mvIcon, selectedColor);
        rankingSIcon = ImageUtils.dye(rankingIcon, selectedColor);
        userSIcon = ImageUtils.dye(userIcon, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        setFont(customFont);
//        JLabel label = (JLabel) component;
//        label.setForeground(isSelected ? selectedColor : foreColor);
//        setDrawBg(isSelected);
//
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        list.setFixedCellWidth(list.getVisibleRect().width - 10);
        if (value instanceof NetPlaylistInfo) {
            NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel creatorLabel = new JLabel();
            JLabel playCountLabel = new JLabel();
            JLabel trackCountLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(40);
            iconLabel.setIcon(netPlaylistInfo.hasCoverImgThumb() ? new ImageIcon(netPlaylistInfo.getCoverImgThumb()) : isSelected ? playlistSIcon : playlistIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            creatorLabel.setHorizontalAlignment(CENTER);
            playCountLabel.setHorizontalAlignment(CENTER);
            trackCountLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            creatorLabel.setVerticalAlignment(CENTER);
            playCountLabel.setVerticalAlignment(CENTER);
            trackCountLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            creatorLabel.setOpaque(false);
            playCountLabel.setOpaque(false);
            trackCountLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            creatorLabel.setForeground(isSelected ? selectedColor : foreColor);
            playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
            trackCountLabel.setForeground(isSelected ? selectedColor : foreColor);

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
            String source = StringUtils.textToHtml(NetMusicSource.names[netPlaylistInfo.getSource()]);
            String name = netPlaylistInfo.hasName() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(netPlaylistInfo.getName(), maxWidth)) : "";
            String creator = netPlaylistInfo.hasCreator() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(netPlaylistInfo.getCreator(), maxWidth)) : "";
            String playCount = netPlaylistInfo.hasPlayCount() ? StringUtils.formatNumber(netPlaylistInfo.getPlayCount()) : "";
            String trackCount = netPlaylistInfo.hasTrackCount() ? netPlaylistInfo.getTrackCount() + " 首歌曲" : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            creatorLabel.setText(creator);
            playCountLabel.setText(playCount);
            trackCountLabel.setText(trackCount);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            Dimension ps3 = creatorLabel.getPreferredSize();
            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetAlbumInfo) {
            NetAlbumInfo netAlbumInfo = (NetAlbumInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel artistLabel = new JLabel();
            JLabel songNumLabel = new JLabel();
            JLabel publishTimeLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(40);
            iconLabel.setIcon(netAlbumInfo.hasCoverImgThumb() ? new ImageIcon(netAlbumInfo.getCoverImgThumb()) : isSelected ? albumSIcon : albumIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            artistLabel.setHorizontalAlignment(CENTER);
            songNumLabel.setHorizontalAlignment(CENTER);
            publishTimeLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            artistLabel.setVerticalAlignment(CENTER);
            songNumLabel.setVerticalAlignment(CENTER);
            publishTimeLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            artistLabel.setOpaque(false);
            songNumLabel.setOpaque(false);
            publishTimeLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            artistLabel.setForeground(isSelected ? selectedColor : foreColor);
            songNumLabel.setForeground(isSelected ? selectedColor : foreColor);
            publishTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            artistLabel.setFont(customFont);
            songNumLabel.setFont(customFont);
            publishTimeLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(iconLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(artistLabel);
            outerPanel.add(songNumLabel);
            outerPanel.add(publishTimeLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netAlbumInfo.getSource()]);
            String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netAlbumInfo.getName(), maxWidth));
            String artist = netAlbumInfo.hasArtist() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(netAlbumInfo.getArtist(), maxWidth)) : "";
            String songNum = netAlbumInfo.hasSongNum() ? netAlbumInfo.isPhoto() ? netAlbumInfo.getSongNum() + " 张图片" : netAlbumInfo.getSongNum() + " 首歌曲" : "";
            String publishTime = netAlbumInfo.hasPublishTime() ? netAlbumInfo.getPublishTime() + " 发行" : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            artistLabel.setText(artist);
            songNumLabel.setText(songNum);
            publishTimeLabel.setText(publishTime);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            Dimension ps3 = artistLabel.getPreferredSize();
            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetArtistInfo) {
            NetArtistInfo netArtistInfo = (NetArtistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel songNumLabel = new JLabel();
            JLabel albumNumLabel = new JLabel();
            JLabel mvNumLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(40);
            iconLabel.setIcon(netArtistInfo.hasCoverImgThumb() ? new ImageIcon(netArtistInfo.getCoverImgThumb()) : isSelected ? artistSIcon : artistIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            songNumLabel.setHorizontalAlignment(CENTER);
            albumNumLabel.setHorizontalAlignment(CENTER);
            mvNumLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            songNumLabel.setVerticalAlignment(CENTER);
            albumNumLabel.setVerticalAlignment(CENTER);
            mvNumLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            songNumLabel.setOpaque(false);
            albumNumLabel.setOpaque(false);
            mvNumLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            songNumLabel.setForeground(isSelected ? selectedColor : foreColor);
            albumNumLabel.setForeground(isSelected ? selectedColor : foreColor);
            mvNumLabel.setForeground(isSelected ? selectedColor : foreColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            songNumLabel.setFont(customFont);
            albumNumLabel.setFont(customFont);
            mvNumLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(iconLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(songNumLabel);
            outerPanel.add(albumNumLabel);
            outerPanel.add(mvNumLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netArtistInfo.getSource()]);
            String name = netArtistInfo.hasName() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(netArtistInfo.getName(), maxWidth)) : "";
            String songNum = netArtistInfo.hasSongNum() ? netArtistInfo.getSongNum() + " 首歌曲" : "";
            String albumNum = netArtistInfo.hasAlbumNum() ? netArtistInfo.getAlbumNum() + " 张专辑" : "";
            String mvNum = netArtistInfo.hasMvNum() ? netArtistInfo.getMvNum() + " 部 MV" : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            songNumLabel.setText(songNum);
            albumNumLabel.setText(albumNum);
            mvNumLabel.setText(mvNum);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            int ph = Math.max(ps.height, ps2.height);
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetRadioInfo) {
            NetRadioInfo netRadioInfo = (NetRadioInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel djLabel = new JLabel();
            JLabel categoryLabel = new JLabel();
            JLabel trackCountLabel = new JLabel();
            JLabel playCountLabel = new JLabel();
//        JLabel createTimeLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(25);
            iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : isSelected ? radioSIcon : radioIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            djLabel.setHorizontalAlignment(CENTER);
            categoryLabel.setHorizontalAlignment(CENTER);
            trackCountLabel.setHorizontalAlignment(CENTER);
            playCountLabel.setHorizontalAlignment(CENTER);
//        createTimeLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            djLabel.setVerticalAlignment(CENTER);
            categoryLabel.setVerticalAlignment(CENTER);
            trackCountLabel.setVerticalAlignment(CENTER);
            playCountLabel.setVerticalAlignment(CENTER);
//        createTimeLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            djLabel.setOpaque(false);
            categoryLabel.setOpaque(false);
            trackCountLabel.setOpaque(false);
            playCountLabel.setOpaque(false);
//        createTimeLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            djLabel.setForeground(isSelected ? selectedColor : foreColor);
            categoryLabel.setForeground(isSelected ? selectedColor : foreColor);
            trackCountLabel.setForeground(isSelected ? selectedColor : foreColor);
            playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
//        createTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            djLabel.setFont(customFont);
            categoryLabel.setFont(customFont);
            trackCountLabel.setFont(customFont);
            playCountLabel.setFont(customFont);
//        createTimeLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(iconLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(djLabel);
            outerPanel.add(categoryLabel);
            outerPanel.add(trackCountLabel);
            outerPanel.add(playCountLabel);
//        outerPanel.add(createTimeLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netRadioInfo.getSource()]);
            String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.getName(), maxWidth));
            String dj = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", maxWidth));
            String category = netRadioInfo.hasCategory() ? netRadioInfo.getCategory() : "";
            String trackCount = netRadioInfo.hasTrackCount() ? netRadioInfo.getTrackCount() + " 期节目" : "";
            String playCount = netRadioInfo.hasPlayCount() ? StringUtils.formatNumber(netRadioInfo.getPlayCount()) : "";
//        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            djLabel.setText(dj);
            categoryLabel.setText(category);
            trackCountLabel.setText(trackCount);
            playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            Dimension ps3 = djLabel.getPreferredSize();
            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetMvInfo) {
            NetMvInfo netMvInfo = (NetMvInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel artistLabel = new JLabel();
            JLabel durationLabel = new JLabel();
            JLabel playCountLabel = new JLabel();
            JLabel pubTimeLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(10);
            iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : isSelected ? mvSIcon : mvIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            artistLabel.setHorizontalAlignment(CENTER);
            durationLabel.setHorizontalAlignment(CENTER);
            playCountLabel.setHorizontalAlignment(CENTER);
            pubTimeLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            artistLabel.setVerticalAlignment(CENTER);
            durationLabel.setVerticalAlignment(CENTER);
            playCountLabel.setVerticalAlignment(CENTER);
            pubTimeLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            artistLabel.setOpaque(false);
            durationLabel.setOpaque(false);
            playCountLabel.setOpaque(false);
            pubTimeLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            artistLabel.setForeground(isSelected ? selectedColor : foreColor);
            durationLabel.setForeground(isSelected ? selectedColor : foreColor);
            playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
            pubTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            artistLabel.setFont(customFont);
            durationLabel.setFont(customFont);
            playCountLabel.setFont(customFont);
            pubTimeLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(iconLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(artistLabel);
            outerPanel.add(durationLabel);
            outerPanel.add(playCountLabel);
            outerPanel.add(pubTimeLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netMvInfo.getSource()]);
            String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netMvInfo.getName(), maxWidth));
            String artist = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netMvInfo.getArtist(), maxWidth));
            String duration = netMvInfo.hasDuration() ? TimeUtils.format(netMvInfo.getDuration()) : "--:--";
            String playCount = netMvInfo.hasPlayCount() ? StringUtils.formatNumber(netMvInfo.getPlayCount()) : "";
            String pubTime = netMvInfo.hasPubTime() ? netMvInfo.getPubTime() : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            artistLabel.setText(artist);
            durationLabel.setText(duration);
            playCountLabel.setText(playCount);
            pubTimeLabel.setText(pubTime);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            Dimension ps3 = artistLabel.getPreferredSize();
            int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetRankingInfo) {
            NetRankingInfo netRankingInfo = (NetRankingInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel iconLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel playCountLabel = new JLabel();
            JLabel updateFreLabel = new JLabel();
            JLabel updateTimeLabel = new JLabel();

            iconLabel.setHorizontalTextPosition(LEFT);
            iconLabel.setIconTextGap(40);
            iconLabel.setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : isSelected ? rankingSIcon : rankingIcon);

            iconLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            playCountLabel.setHorizontalAlignment(CENTER);
            updateFreLabel.setHorizontalAlignment(CENTER);
            updateTimeLabel.setHorizontalAlignment(CENTER);

            iconLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            playCountLabel.setVerticalAlignment(CENTER);
            updateFreLabel.setVerticalAlignment(CENTER);
            updateTimeLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            iconLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            playCountLabel.setOpaque(false);
            updateFreLabel.setOpaque(false);
            updateTimeLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            iconLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
            updateFreLabel.setForeground(isSelected ? selectedColor : foreColor);
            updateTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

            iconLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            playCountLabel.setFont(customFont);
            updateFreLabel.setFont(customFont);
            updateTimeLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(iconLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(playCountLabel);
            outerPanel.add(updateFreLabel);
            outerPanel.add(updateTimeLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netRankingInfo.getSource()]);
            String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRankingInfo.getName(), maxWidth));
            String playCount = netRankingInfo.hasPlayCount() ? StringUtils.formatNumber(netRankingInfo.getPlayCount()) : "";
            String updateFre = netRankingInfo.hasUpdateFre() ? netRankingInfo.getUpdateFre() : "";
            String updateTime = netRankingInfo.hasUpdateTime() ? netRankingInfo.getUpdateTime() + " 更新" : "";

            iconLabel.setText(source);
            nameLabel.setText(name);
            playCountLabel.setText(playCount);
            updateFreLabel.setText(updateFre);
            updateTimeLabel.setText(updateTime);

            Dimension ps = iconLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            int ph = Math.max(ps.height, ps2.height);
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        } else if (value instanceof NetUserInfo) {
            NetUserInfo netUserInfo = (NetUserInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            JLabel avatarLabel = new JLabel();
            JLabel nameLabel = new JLabel();
            JLabel genderLabel = new JLabel();
//        JLabel birthdayLabel = new JLabel();
//        JLabel areaLabel = new JLabel();
            JLabel followLabel = new JLabel();
            JLabel followedLabel = new JLabel();
            JLabel playlistCountLabel = new JLabel();

            avatarLabel.setHorizontalTextPosition(LEFT);
            avatarLabel.setIconTextGap(25);
            avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : isSelected ? userSIcon : userIcon);

            avatarLabel.setHorizontalAlignment(CENTER);
            nameLabel.setHorizontalAlignment(CENTER);
            genderLabel.setHorizontalAlignment(CENTER);
//        birthdayLabel.setHorizontalAlignment(CENTER);
//        areaLabel.setHorizontalAlignment(CENTER);
            followLabel.setHorizontalAlignment(CENTER);
            followedLabel.setHorizontalAlignment(CENTER);
            playlistCountLabel.setHorizontalAlignment(CENTER);

            avatarLabel.setVerticalAlignment(CENTER);
            nameLabel.setVerticalAlignment(CENTER);
            genderLabel.setVerticalAlignment(CENTER);
//        birthdayLabel.setVerticalAlignment(CENTER);
//        areaLabel.setVerticalAlignment(CENTER);
            followLabel.setVerticalAlignment(CENTER);
            followedLabel.setVerticalAlignment(CENTER);
            playlistCountLabel.setVerticalAlignment(CENTER);

            outerPanel.setOpaque(false);
            avatarLabel.setOpaque(false);
            nameLabel.setOpaque(false);
            genderLabel.setOpaque(false);
//        birthdayLabel.setOpaque(false);
//        areaLabel.setOpaque(false);
            followLabel.setOpaque(false);
            followedLabel.setOpaque(false);
            playlistCountLabel.setOpaque(false);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            avatarLabel.setForeground(isSelected ? selectedColor : foreColor);
            nameLabel.setForeground(isSelected ? selectedColor : foreColor);
            genderLabel.setForeground(isSelected ? selectedColor : foreColor);
//        birthdayLabel.setForeground(isSelected ? selectedColor : foreColor);
//        areaLabel.setForeground(isSelected ? selectedColor : foreColor);
            followLabel.setForeground(isSelected ? selectedColor : foreColor);
            followedLabel.setForeground(isSelected ? selectedColor : foreColor);
            playlistCountLabel.setForeground(isSelected ? selectedColor : foreColor);

            avatarLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            genderLabel.setFont(customFont);
//        birthdayLabel.setFont(customFont);
//        areaLabel.setFont(customFont);
            followLabel.setFont(customFont);
            followedLabel.setFont(customFont);
            playlistCountLabel.setFont(customFont);

            GridLayout layout = new GridLayout(1, 5);
            layout.setHgap(15);
            outerPanel.setLayout(layout);

            outerPanel.add(avatarLabel);
            outerPanel.add(nameLabel);
            outerPanel.add(genderLabel);
//        outerPanel.add(birthdayLabel);
//        outerPanel.add(areaLabel);
            outerPanel.add(playlistCountLabel);
            outerPanel.add(followLabel);
            outerPanel.add(followedLabel);

            final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
            String source = StringUtils.textToHtml(NetMusicSource.names[netUserInfo.getSource()]);
            String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netUserInfo.getName(), maxWidth));
            String gender = netUserInfo.hasGender() ? netUserInfo.getGender() : "";
//        String birthday = netUserInfo.hasBirthday() ? netUserInfo.getBirthday() : "";
//        String area = netUserInfo.hasArea() ? netUserInfo.getArea() : "";
            String playlistCount = netUserInfo.hasPlaylistCount() ? netUserInfo.getPlaylistCount() + " 歌单"
                    : netUserInfo.hasRadioCount() ? netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目"
                    : netUserInfo.hasProgramCount() ? netUserInfo.getProgramCount() + " 节目"
                    : "";
            String follow = netUserInfo.hasFollow() ? StringUtils.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注" : "";
            String followed = netUserInfo.hasFollowed() ? StringUtils.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝" : "";

            avatarLabel.setText(source);
            nameLabel.setText(name);
            genderLabel.setText(gender);
//        birthdayLabel.setText(birthday);
//        areaLabel.setText(area);
            playlistCountLabel.setText(playlistCount);
            followLabel.setText(follow);
            followedLabel.setText(followed);

            Dimension ps = avatarLabel.getPreferredSize();
            Dimension ps2 = nameLabel.getPreferredSize();
            int ph = Math.max(ps.height, ps2.height);
            Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
            outerPanel.setPreferredSize(d);
            list.setFixedCellWidth(list.getVisibleRect().width - 10);

            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        }

        // 所有标签透明
//        label.setOpaque(false);
        return this;
    }

    @Override
    public void paint(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            // 注意这里不能用 getVisibleRect ！！！
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paint(g);
    }
}
