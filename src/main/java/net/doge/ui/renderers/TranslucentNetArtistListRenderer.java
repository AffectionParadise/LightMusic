package net.doge.ui.renderers;

import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetAlbumInfo;
import net.doge.models.NetArtistInfo;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.FontUtils;
import net.doge.utils.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtils;

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
public class TranslucentNetArtistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon artistIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "artistItem.png"), ImageConstants.profileWidth));
    private ImageIcon artistSIcon;

    public TranslucentNetArtistListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        artistIcon = ImageUtils.dye(artistIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        artistSIcon = ImageUtils.dye(artistIcon, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        JLabel label = (JLabel) component;
//        label.setForeground(isSelected ? selectedColor : foreColor);
//        setDrawBg(isSelected);
//
        NetArtistInfo netArtistInfo = (NetArtistInfo) value;
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setFont(customFont);
//        setIcon(artistIcon);
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
//        setIcon(netArtistInfo.hasCoverImgThumb() ? new ImageIcon(netArtistInfo.getCoverImgThumb()) : artistIcon);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

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
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netArtistInfo.getName(), maxWidth));
        String songNum = netArtistInfo.hasSongNum() ? netArtistInfo.fromME() ? netArtistInfo.getSongNum() + " 电台" : netArtistInfo.getSongNum() + " 歌曲" : "";
        String albumNum = netArtistInfo.hasAlbumNum() ? netArtistInfo.getAlbumNum() + " 专辑" : "";
        String mvNum = netArtistInfo.hasMvNum() ? netArtistInfo.getMvNum() + " MV" : "";

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
