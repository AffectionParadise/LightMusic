package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.AudioQuality;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.player.MusicPlayer;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.DurationUtil;
import net.doge.util.common.StringUtil;
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
public class NetMusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private MusicPlayer player;
    private static ImageIcon musicIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon musicMvIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicMvItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon programIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.programItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon playingIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playingItem"), ImageConstants.SMALL_WIDTH));

    public NetMusicListRenderer(MusicPlayer player) {
        this.player = player;
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        musicIcon = ImageUtil.dye(musicIcon, iconColor);
        musicMvIcon = ImageUtil.dye(musicMvIcon, iconColor);
        programIcon = ImageUtil.dye(programIcon, iconColor);
        playingIcon = ImageUtil.dye(playingIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetMusicInfo musicInfo = (NetMusicInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomPanel innerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel artistLabel = new CustomLabel();
        CustomLabel albumNameLabel = new CustomLabel();
        CustomLabel durationLabel = new CustomLabel();

        // 播放中的文件图标不同
        if (!player.loadedNetMusic(musicInfo)) {
            if (musicInfo.hasMv()) iconLabel.setIcon(musicMvIcon);
            else if (musicInfo.isProgram()) iconLabel.setIcon(programIcon);
            else iconLabel.setIcon(musicIcon);
        } else iconLabel.setIcon(playingIcon);

        iconLabel.setIconTextGap(15);
        iconLabel.setHorizontalTextPosition(LEFT);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        albumNameLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        artistLabel.setFont(customFont);
        albumNameLabel.setFont(customFont);
        durationLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        innerPanel.setLayout(layout);

        innerPanel.add(iconLabel);
        innerPanel.add(nameLabel);
        innerPanel.add(artistLabel);
        innerPanel.add(albumNameLabel);
        innerPanel.add(durationLabel);

        final int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (innerPanel.getComponentCount() - 1) * layout.getHgap()) / innerPanel.getComponentCount();
        String source = StringUtil.textToHtml(NetMusicSource.NAMES[musicInfo.getSource()]
                + (musicInfo.hasQualityType() ? " " + AudioQuality.QT_NAMES[musicInfo.getQualityType()] : ""));
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(musicInfo.getName(), RendererConstants.STRING_MAX_LENGTH), maxWidth));
        String artist = musicInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(musicInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), maxWidth)) : "";
        String albumName = musicInfo.hasAlbumName() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(musicInfo.getAlbumName(), RendererConstants.STRING_MAX_LENGTH), maxWidth)) : "";
        String duration = StringUtil.textToHtml(musicInfo.hasDuration() ? DurationUtil.format(musicInfo.getDuration()) : "--:--");

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        albumNameLabel.setText(albumName);
        durationLabel.setText(duration);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = artistLabel.getPreferredSize();
        Dimension ps3 = albumNameLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        Dimension d = new Dimension(lw, Math.max(ph, 36));
        innerPanel.setPreferredSize(d);
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        list.setFixedCellWidth(lw);

        if (musicInfo.hasLrcMatch()) {
            String lrcMatch = StringUtil.textToHtml(StringUtil.wrapLineByWidth("词： " + musicInfo.getLrcMatch(), lw));
            CustomLabel lrcMatchLabel = new CustomLabel(lrcMatch);
            lrcMatchLabel.setForeground(textColor);
            lrcMatchLabel.setFont(customFont);
            Dimension p = lrcMatchLabel.getPreferredSize();
            outerPanel.add(lrcMatchLabel, BorderLayout.SOUTH);
            outerPanel.setPreferredSize(new Dimension(d.width, d.height + p.height + 20));
        }

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
