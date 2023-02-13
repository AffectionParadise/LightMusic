package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.MusicPlayer;
import net.doge.models.entity.NetMusicInfo;
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
public class NetMusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private MusicPlayer player;
    private ImageIcon musicIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "musicItem.png"), ImageConstants.smallWidth));
    private ImageIcon musicMvIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "musicMvItem.png"), ImageConstants.smallWidth));
    private ImageIcon programIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "programItem.png"), ImageConstants.smallWidth));
    private ImageIcon playingIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "playingItem.png"), ImageConstants.smallWidth));

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

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
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

        final int maxWidth = (list.getVisibleRect().width - 10 - (innerPanel.getComponentCount() - 1) * layout.getHgap()) / innerPanel.getComponentCount();
        String source = StringUtil.textToHtml(NetMusicSource.names[musicInfo.getSource()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(musicInfo.getName(), maxWidth));
        String artist = musicInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(musicInfo.getArtist(), maxWidth)) : "";
        String albumName = musicInfo.hasAlbumName() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(musicInfo.getAlbumName(), maxWidth)) : "";
        String duration = StringUtil.textToHtml(musicInfo.hasDuration() ? TimeUtil.format(musicInfo.getDuration()) : "--:--");

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        albumNameLabel.setText(albumName);
        durationLabel.setText(duration);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = artistLabel.getPreferredSize();
        Dimension ps3 = albumNameLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
        int lw = list.getVisibleRect().width - 10;
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

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        // 画背景
//        if (drawBg) {
//            Graphics2D g2d = (Graphics2D) g;
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setColor(getForeground());
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
//            // 注意这里不能用 getVisibleRect ！！！
//            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        }
//
//        super.paintComponent(g);
//    }
}
