package net.doge.ui.renderers;

import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetMvInfo;
import net.doge.models.NetRadioInfo;
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
public class TranslucentNetMvListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon mvIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.profileWidth));
    private ImageIcon mvSIcon;

    public TranslucentNetMvListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        mvIcon = ImageUtils.dye(mvIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        mvSIcon = ImageUtils.dye(mvIcon, selectedColor);
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
        NetMvInfo netMvInfo = (NetMvInfo) value;
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setFont(customFont);
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
//        setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : mvIcon);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

        CustomPanel outerPanel = new CustomPanel();
        JLabel iconLabel = new JLabel();
        JLabel nameLabel = new JLabel();
        JLabel artistLabel = new JLabel();
        JLabel durationLabel = new JLabel();
        JLabel playCountLabel = new JLabel();
        JLabel pubTimeLabel = new JLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : isSelected ? mvSIcon : mvIcon);
        iconLabel.setIconTextGap(10);

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
