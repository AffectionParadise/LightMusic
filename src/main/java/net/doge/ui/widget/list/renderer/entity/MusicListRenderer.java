package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.AudioQuality;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.AudioFile;
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
public class MusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel artistLabel = new CustomLabel();
    private CustomLabel albumNameLabel = new CustomLabel();
    private CustomLabel durationLabel = new CustomLabel();

    private MusicPlayer player;
    private static ImageIcon musicIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon musicMvIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.musicMvItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon programIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.programItem"), ImageConstants.SMALL_WIDTH));
    private static ImageIcon playingIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playingItem"), ImageConstants.SMALL_WIDTH));

    public MusicListRenderer(MusicPlayer player) {
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
        iconLabel.setIconTextGap(15);
        iconLabel.setHorizontalTextPosition(LEFT);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(artistLabel);
        outerPanel.add(albumNameLabel);
        outerPanel.add(durationLabel);

        outerPanel.setBluntDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof AudioFile) && !(value instanceof NetMusicInfo)) return this;

        AudioFile file = null;
        NetMusicInfo musicInfo = null;
        boolean isFile = value instanceof AudioFile;
        if (isFile) file = (AudioFile) value;
        else musicInfo = (NetMusicInfo) value;

        if (isFile) {
            // 播放中的文件图标不同
            if (!player.loadedAudioFile(file)) iconLabel.setIcon(musicIcon);
            else iconLabel.setIcon(playingIcon);
        } else {
            // 播放中的文件图标不同
            if (!player.loadedNetMusic(musicInfo)) {
                if (musicInfo.hasMv()) iconLabel.setIcon(musicMvIcon);
                else if (musicInfo.isProgram()) iconLabel.setIcon(programIcon);
                else iconLabel.setIcon(musicIcon);
            } else iconLabel.setIcon(playingIcon);
        }

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

        int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
        String source = StringUtil.textToHtml(isFile ? "  " : NetMusicSource.NAMES[musicInfo.getSource()]
                + (musicInfo.hasQualityType() ? " " + AudioQuality.QT_NAMES[musicInfo.getQualityType()] : ""));
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(isFile ? file.hasSongName() ? file.getSongName() : file.toString() : musicInfo.getName(), RendererConstants.STRING_MAX_LENGTH),
                maxWidth));
        String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(isFile ? file.hasArtist() ? file.getArtist() : "" : musicInfo.hasArtist() ? musicInfo.getArtist() : "", RendererConstants.STRING_MAX_LENGTH),
                maxWidth));
        String albumName = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(isFile ? (file.hasAlbum() ? file.getAlbum() : "") : musicInfo.hasAlbumName() ? musicInfo.getAlbumName() : "", RendererConstants.STRING_MAX_LENGTH),
                maxWidth));
        String duration = StringUtil.textToHtml(isFile ? file.hasDuration() ? DurationUtil.format(file.getDuration()) : "--:--"
                : musicInfo.hasDuration() ? DurationUtil.format(musicInfo.getDuration()) : "--:--");

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

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
