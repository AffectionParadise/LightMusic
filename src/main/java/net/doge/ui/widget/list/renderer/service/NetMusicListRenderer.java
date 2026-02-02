package net.doge.ui.widget.list.renderer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.core.player.MusicPlayer;
import net.doge.entity.service.NetMusicInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.DurationUtil;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
public class NetMusicListRenderer extends CustomListCellRenderer {
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomPanel innerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel artistLabel = new CustomLabel();
    private CustomLabel albumNameLabel = new CustomLabel();
    private CustomLabel durationLabel = new CustomLabel();
    private CustomLabel lrcMatchLabel = new CustomLabel();

    private MusicPlayer player;
    private static ImageIcon musicIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon musicMvIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicMvItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon programIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.programItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon playingIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playingItem"), ImageConstants.SMALL_WIDTH));

    private final float opacity = 0.7f;

    public NetMusicListRenderer(MusicPlayer player) {
        this.player = player;
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        musicIcon = ImageUtil.dye(musicIcon, iconColor);
        musicMvIcon = ImageUtil.dye(musicMvIcon, iconColor);
        programIcon = ImageUtil.dye(programIcon, iconColor);
        playingIcon = ImageUtil.dye(playingIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(ScaleUtil.scale(15));
        iconLabel.setHorizontalTextPosition(LEFT);

        artistLabel.setOpacity(opacity);
        albumNameLabel.setOpacity(opacity);
        lrcMatchLabel.setOpacity(opacity);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(ScaleUtil.scale(15));
        innerPanel.setLayout(layout);

        innerPanel.add(iconLabel);
        innerPanel.add(nameLabel);
        innerPanel.add(artistLabel);
        innerPanel.add(albumNameLabel);
        innerPanel.add(durationLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetMusicInfo musicInfo = (NetMusicInfo) value;

        // 播放中的文件图标不同
        if (player.loadedNetMusic(musicInfo)) {
            iconLabel.setIcon(playingIcon);
            iconLabel.setOpacity(1f);
        } else {
            if (musicInfo.hasMv()) iconLabel.setIcon(musicMvIcon);
            else if (musicInfo.isProgram()) iconLabel.setIcon(programIcon);
            else iconLabel.setIcon(musicIcon);
            iconLabel.setOpacity(opacity);
        }

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        albumNameLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);

        int lw = list.getVisibleRect().width - ScaleUtil.scale(10), maxWidth = (lw - (innerPanel.getComponentCount() - 1) * ((GridLayout) innerPanel.getLayout()).getHgap()) / innerPanel.getComponentCount();
        String source = HtmlUtil.textToHtml(NetMusicSource.NAMES[musicInfo.getSource()]
                + (musicInfo.hasQualityType() ? " " + AudioQuality.QT_NAMES[musicInfo.getQualityType()] : ""));
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(musicInfo.getName(), RendererConstants.STRING_MAX_LENGTH), maxWidth));
        String artist = musicInfo.hasArtist() ? HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(
                StringUtil.shorten(musicInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), maxWidth)) : "";
        String albumName = musicInfo.hasAlbumName() ? HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(
                StringUtil.shorten(musicInfo.getAlbumName(), RendererConstants.STRING_MAX_LENGTH), maxWidth)) : "";
        String duration = HtmlUtil.textToHtml(musicInfo.hasDuration() ? DurationUtil.format(musicInfo.getDuration()) : "--:--");

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        albumNameLabel.setText(albumName);
        durationLabel.setText(duration);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = artistLabel.getPreferredSize();
        Dimension ps3 = albumNameLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        Dimension d = new Dimension(lw, Math.max(ph, ScaleUtil.scale(36)));
        innerPanel.setPreferredSize(d);
        outerPanel.add(innerPanel, BorderLayout.CENTER);

        if (musicInfo.hasLrcMatch()) {
            String lrcMatch = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(musicInfo.getLrcMatch(), lw));
            lrcMatchLabel.setText(lrcMatch);
            lrcMatchLabel.setForeground(textColor);
            Dimension p = lrcMatchLabel.getPreferredSize();
            outerPanel.add(lrcMatchLabel, BorderLayout.SOUTH);
            outerPanel.setPreferredSize(new Dimension(d.width, d.height + p.height + ScaleUtil.scale(20)));
        } else {
            // 移除多余的歌词显示
            outerPanel.remove(lrcMatchLabel);
            outerPanel.setPreferredSize(null);
        }

        list.setFixedCellWidth(lw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
