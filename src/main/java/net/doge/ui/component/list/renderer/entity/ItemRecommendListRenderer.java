package net.doge.ui.component.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.*;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.ui.ImageUtil;

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

    private static ImageIcon playlistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playlistItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon albumIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.albumItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon artistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.artistItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon radioIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.radioItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon mvIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.mvItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon rankingIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.rankingItem"), ImageConstants.MEDIUM_WIDTH));
    private static ImageIcon userIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.userItem"), ImageConstants.MEDIUM_WIDTH));

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
        if (value instanceof NetPlaylistInfo) {
            NetPlaylistInfo playlistInfo = (NetPlaylistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel creatorLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel trackCountLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(playlistInfo.hasCoverImgThumb() ? new ImageIcon(playlistInfo.getCoverImgThumb()) : playlistIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(playlistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String creator = playlistInfo.hasCreator() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                    StringUtil.shorten(playlistInfo.getCreator(), RendererConstants.STRING_MAX_LENGTH), tw)) : "";
            String playCount = playlistInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(playlistInfo.getPlayCount())) : "";
            String trackCount = playlistInfo.hasTrackCount() ? StringUtil.textToHtml(playlistInfo.getTrackCount() + " 歌曲") : "";

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
            NetAlbumInfo albumInfo = (NetAlbumInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel artistLabel = new CustomLabel();
            CustomLabel songNumLabel = new CustomLabel();
            CustomLabel publishTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(albumInfo.hasCoverImgThumb() ? new ImageIcon(albumInfo.getCoverImgThumb()) : albumIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(albumInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String artist = albumInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                    StringUtil.shorten(albumInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), tw)) : "";
            String songNum = albumInfo.hasSongNum() ? StringUtil.textToHtml(albumInfo.isPhoto() ? albumInfo.getSongNum() + " 图片" : albumInfo.getSongNum() + " 歌曲") : "";
            String publishTime = albumInfo.hasPublishTime() ? StringUtil.textToHtml(albumInfo.getPublishTime() + " 发行") : "";

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
            NetArtistInfo artistInfo = (NetArtistInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel songNumLabel = new CustomLabel();
            CustomLabel albumNumLabel = new CustomLabel();
            CustomLabel mvNumLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(artistInfo.hasCoverImgThumb() ? new ImageIcon(artistInfo.getCoverImgThumb()) : artistIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(artistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String songNum = artistInfo.hasSongNum() ? StringUtil.textToHtml(artistInfo.fromME() ? artistInfo.getSongNum() + " 电台" : artistInfo.getSongNum() + " 歌曲") : "";
            String albumNum = artistInfo.hasAlbumNum() ? StringUtil.textToHtml(artistInfo.getAlbumNum() + " 专辑") : "";
            String mvNum = artistInfo.hasMvNum() ? StringUtil.textToHtml(artistInfo.getMvNum() + " MV") : "";

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
            NetRadioInfo radioInfo = (NetRadioInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel dCustomLabel = new CustomLabel();
            CustomLabel categoryLabel = new CustomLabel();
            CustomLabel trackCountLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
//        CustomLabel createTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(radioInfo.hasCoverImgThumb() ? new ImageIcon(radioInfo.getCoverImgThumb()) : radioIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(radioInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                    StringUtil.shorten(radioInfo.hasDj() ? radioInfo.getDj() : "", RendererConstants.STRING_MAX_LENGTH), tw));
            String category = radioInfo.hasCategory() ? StringUtil.textToHtml(radioInfo.getCategory()) : "";
            String trackCount = radioInfo.hasTrackCount() ? StringUtil.textToHtml(radioInfo.getTrackCount() + " 节目") : "";
            String playCount = radioInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(radioInfo.getPlayCount())) : "";
//        String createTime = radioInfo.hasCreateTime() ? radioInfo.getCreateTime() : "";

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
            NetMvInfo mvInfo = (NetMvInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel artistLabel = new CustomLabel();
            CustomLabel durationLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel pubTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(mvInfo.hasCoverImgThumb() ? new ImageIcon(mvInfo.getCoverImgThumb()) : mvIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(mvInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(mvInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), tw));
            String duration = StringUtil.textToHtml(mvInfo.hasDuration() ? TimeUtil.format(mvInfo.getDuration()) : "--:--");
            String playCount = mvInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(mvInfo.getPlayCount())) : "";
            String pubTime = mvInfo.hasPubTime() ? StringUtil.textToHtml(mvInfo.getPubTime()) : "";

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
            NetRankingInfo rankingInfo = (NetRankingInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel iconLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel playCountLabel = new CustomLabel();
            CustomLabel updateFreLabel = new CustomLabel();
            CustomLabel updateTimeLabel = new CustomLabel();

            iconLabel.setIconTextGap(0);
            iconLabel.setIcon(rankingInfo.hasCoverImgThumb() ? new ImageIcon(rankingInfo.getCoverImgThumb()) : rankingIcon);

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

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(rankingInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String playCount = rankingInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(rankingInfo.getPlayCount())) : "";
            String updateFre = rankingInfo.hasUpdateFre() ? StringUtil.textToHtml(rankingInfo.getUpdateFre()) : "";
            String updateTime = rankingInfo.hasUpdateTime() ? StringUtil.textToHtml(rankingInfo.getUpdateTime() + " 更新") : "";

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
            NetUserInfo userInfo = (NetUserInfo) value;

            CustomPanel outerPanel = new CustomPanel();
            CustomLabel avatarLabel = new CustomLabel();
            CustomLabel nameLabel = new CustomLabel();
            CustomLabel genderLabel = new CustomLabel();
//        CustomLabel birthdayLabel = new CustomLabel();
//        CustomLabel areaLabel = new CustomLabel();
            CustomLabel followLabel = new CustomLabel();
            CustomLabel fanLabel = new CustomLabel();
            CustomLabel playlistCountLabel = new CustomLabel();

            avatarLabel.setIconTextGap(0);
            avatarLabel.setIcon(userInfo.hasAvatarThumb() ? new ImageIcon(userInfo.getAvatarThumb()) : userIcon);

            outerPanel.setForeground(isSelected ? selectedColor : foreColor);
            avatarLabel.setForeground(textColor);
            nameLabel.setForeground(textColor);
            genderLabel.setForeground(textColor);
//        birthdayLabel.setForeground(textColor);
//        areaLabel.setForeground(textColor);
            followLabel.setForeground(textColor);
            fanLabel.setForeground(textColor);
            playlistCountLabel.setForeground(textColor);

            avatarLabel.setFont(customFont);
            nameLabel.setFont(customFont);
            genderLabel.setFont(tinyFont);
//        birthdayLabel.setFont(tinyFont);
//        areaLabel.setFont(tinyFont);
            followLabel.setFont(tinyFont);
            fanLabel.setFont(tinyFont);
            playlistCountLabel.setFont(tinyFont);

            final float alpha = 0.5f;
            genderLabel.setBluntAlpha(alpha);
            followLabel.setBluntAlpha(alpha);
            fanLabel.setBluntAlpha(alpha);
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
            outerPanel.add(fanLabel);
            outerPanel.add(Box.createVerticalStrut(sh));
            outerPanel.add(playlistCountLabel);
            outerPanel.add(Box.createVerticalStrut(sh));

            final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
            String source = "<html></html>";
            String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(userInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
            String gender = userInfo.hasGender() ? StringUtil.textToHtml(userInfo.getGender()) : "";
            boolean hasRadioCount = userInfo.hasRadioCount(), hasProgramCount = userInfo.hasProgramCount();
            String playlistCount = userInfo.hasPlaylistCount() ? StringUtil.textToHtml(userInfo.getPlaylistCount() + " 歌单")
                    : hasRadioCount && hasProgramCount ? StringUtil.textToHtml(userInfo.getRadioCount() + " 电台，" + userInfo.getProgramCount() + " 节目")
                    : hasRadioCount ? StringUtil.textToHtml(userInfo.getRadioCount() + " 电台")
                    : hasProgramCount ? StringUtil.textToHtml(userInfo.getProgramCount() + (userInfo.fromDt() ? " 专辑" : userInfo.fromBI() ? " 视频" : " 节目"))
                    : "";
            String follow = userInfo.hasFollow() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(userInfo.getFollow()) + " 关注") : "";
            String fan = userInfo.hasFan() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(userInfo.getFan()) + " 粉丝") : "";

            avatarLabel.setText(source);
            nameLabel.setText(name);
            genderLabel.setText(gender);
            playlistCountLabel.setText(playlistCount);
            followLabel.setText(follow);
            fanLabel.setText(fan);

            list.setFixedCellWidth(pw);

            outerPanel.setBluntDrawBg(true);
            outerPanel.setDrawBg(isSelected || hoverIndex == index);

            return outerPanel;
        }

        return this;
    }
}
