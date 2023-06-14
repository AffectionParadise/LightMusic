package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.entities.AudioFile;
import net.doge.models.MusicPlayer;
import net.doge.models.entities.NetMusicInfo;
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
public class MusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private MusicPlayer player;
    private static ImageIcon musicIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "musicItem.png"), ImageConstants.smallWidth));
    private static ImageIcon musicMvIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "musicMvItem.png"), ImageConstants.smallWidth));
    private static ImageIcon programIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "programItem.png"), ImageConstants.smallWidth));
    private static ImageIcon playingIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "playingItem.png"), ImageConstants.smallWidth));

    public MusicListRenderer(MusicPlayer player) {
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
        if (!(value instanceof AudioFile) && !(value instanceof NetMusicInfo)) return this;

        AudioFile file = null;
        NetMusicInfo netMusicInfo = null;
        boolean isFile = value instanceof AudioFile;
        if (isFile) file = (AudioFile) value;
        else netMusicInfo = (NetMusicInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel artistLabel = new CustomLabel();
        CustomLabel albumNameLabel = new CustomLabel();
        CustomLabel durationLabel = new CustomLabel();

        if (isFile) {
            // 播放中的文件图标不同
            if (!player.loadedAudioFile(file)) iconLabel.setIcon(musicIcon);
            else iconLabel.setIcon(playingIcon);
        } else {
            // 播放中的文件图标不同
            if (!player.loadedNetMusic(netMusicInfo)) {
                if (netMusicInfo.hasMv()) iconLabel.setIcon(musicMvIcon);
                else if (netMusicInfo.isProgram()) iconLabel.setIcon(programIcon);
                else iconLabel.setIcon(musicIcon);
            } else iconLabel.setIcon(playingIcon);
        }

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
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(artistLabel);
        outerPanel.add(albumNameLabel);
        outerPanel.add(durationLabel);

        final int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtil.textToHtml(isFile ? "  " : NetMusicSource.names[netMusicInfo.getSource()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(isFile ? file.hasSongName() ? file.getSongName() : file.toString() : netMusicInfo.getName(), maxWidth));
        String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(isFile ? file.hasArtist() ? file.getArtist() : ""
                : netMusicInfo.hasArtist() ? netMusicInfo.getArtist() : "", maxWidth));
        String albumName = StringUtil.textToHtml(StringUtil.wrapLineByWidth(isFile ? (file.hasAlbum() ? file.getAlbum() : "")
                : netMusicInfo.hasAlbumName() ? netMusicInfo.getAlbumName() : "", maxWidth));
        String duration = StringUtil.textToHtml(isFile ? file.hasDuration() ? TimeUtil.format(file.getDuration()) : "--:--"
                : netMusicInfo.hasDuration() ? TimeUtil.format(netMusicInfo.getDuration()) : "--:--");

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        albumNameLabel.setText(albumName);
        durationLabel.setText(duration);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = artistLabel.getPreferredSize();
        Dimension ps3 = albumNameLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        Dimension d = new Dimension(lw, Math.max(ph + 10, 46));
        outerPanel.setPreferredSize(d);
        // 设置 list 元素宽度防止 outerPanel 设置最佳大小时不改变大小！
        list.setFixedCellWidth(lw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
