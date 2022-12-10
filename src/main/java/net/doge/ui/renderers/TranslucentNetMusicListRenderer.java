package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.MusicPlayer;
import net.doge.models.NetMusicInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

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
public class TranslucentNetMusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private MusicPlayer player;
    private ImageIcon musicIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "musicItem.png"), ImageConstants.smallWidth));
    private ImageIcon musicMvIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "musicMvItem.png"), ImageConstants.smallWidth));
    private ImageIcon programIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "programItem.png"), ImageConstants.smallWidth));
    private ImageIcon playingIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "playingItem.png"), ImageConstants.smallWidth));

    private ImageIcon musicSIcon;
    private ImageIcon musicMvSIcon;
    private ImageIcon programSIcon;
    private ImageIcon playingSIcon;

    public TranslucentNetMusicListRenderer(MusicPlayer player) {
        this.player = player;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        musicIcon = ImageUtils.dye(musicIcon, foreColor);
        musicMvIcon = ImageUtils.dye(musicMvIcon, foreColor);
        programIcon = ImageUtils.dye(programIcon, foreColor);
        playingIcon = ImageUtils.dye(playingIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        musicSIcon = ImageUtils.dye(musicIcon, selectedColor);
        musicMvSIcon = ImageUtils.dye(musicMvIcon, selectedColor);
        programSIcon = ImageUtils.dye(programIcon, selectedColor);
        playingSIcon = ImageUtils.dye(playingIcon, selectedColor);
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
        if (!player.isPlayingNetMusic(musicInfo)) {
            if (musicInfo.hasMv()) iconLabel.setIcon(isSelected ? musicMvSIcon : musicMvIcon);
            else if (musicInfo.isProgram()) iconLabel.setIcon(isSelected ? programSIcon : programIcon);
            else iconLabel.setIcon(isSelected ? musicSIcon : musicIcon);
        } else iconLabel.setIcon(isSelected ? playingSIcon : playingIcon);

        iconLabel.setIconTextGap(15);
        iconLabel.setHorizontalTextPosition(LEFT);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        artistLabel.setForeground(isSelected ? selectedColor : foreColor);
        albumNameLabel.setForeground(isSelected ? selectedColor : foreColor);
        durationLabel.setForeground(isSelected ? selectedColor : foreColor);

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
        String source = StringUtils.textToHtml(NetMusicSource.names[musicInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(musicInfo.getName(), maxWidth));
        String artist = musicInfo.hasArtist() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(musicInfo.getArtist(), maxWidth)) : "";
        String albumName = musicInfo.hasAlbumName() ? StringUtils.textToHtml(StringUtils.wrapLineByWidth(musicInfo.getAlbumName(), maxWidth)) : "";
        String duration = StringUtils.textToHtml(musicInfo.hasDuration() ? TimeUtils.format(musicInfo.getDuration()) : "--:--");

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
            String lrcMatch = StringUtils.textToHtml(StringUtils.wrapLineByWidth("词： " + musicInfo.getLrcMatch(), lw));
            CustomLabel lrcMatchLabel = new CustomLabel(lrcMatch);
            lrcMatchLabel.setForeground(isSelected ? selectedColor : foreColor);
            lrcMatchLabel.setFont(customFont);
            Dimension p = lrcMatchLabel.getPreferredSize();
            outerPanel.add(lrcMatchLabel, BorderLayout.SOUTH);
            outerPanel.setPreferredSize(new Dimension(d.width, d.height + p.height + 20));
        }

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public void paintComponent(Graphics g) {
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

        super.paintComponent(g);
    }
}
