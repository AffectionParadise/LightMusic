package net.doge.ui.renderers;

import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetPlaylistInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.FontUtils;
import net.doge.utils.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentNetPlaylistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon playlistIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "playlistItem.png"), ImageConstants.profileWidth));
    private ImageIcon playlistSIcon;

    public TranslucentNetPlaylistListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        playlistIcon = ImageUtils.dye(playlistIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        playlistSIcon = ImageUtils.dye(playlistIcon, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetPlaylistInfo netPlaylistInfo = (NetPlaylistInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel creatorLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel trackCountLabel = new CustomLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(40);
        iconLabel.setIcon(netPlaylistInfo.hasCoverImgThumb() ? new ImageIcon(netPlaylistInfo.getCoverImgThumb()) : isSelected ? playlistSIcon : playlistIcon);

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
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netPlaylistInfo.getName(), maxWidth));
        String creator = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netPlaylistInfo.getCreator(), maxWidth));
        String playCount = netPlaylistInfo.hasPlayCount() ? StringUtils.formatNumber(netPlaylistInfo.getPlayCount()) : "";
        String trackCount = netPlaylistInfo.hasTrackCount() ? netPlaylistInfo.getTrackCount() + " 歌曲" : "";

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
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

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
