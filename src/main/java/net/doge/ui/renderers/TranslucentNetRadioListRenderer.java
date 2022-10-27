package net.doge.ui.renderers;

import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetArtistInfo;
import net.doge.models.NetRadioInfo;
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
public class TranslucentNetRadioListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon radioIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.profileWidth));
    private ImageIcon radioSIcon;

    public TranslucentNetRadioListRenderer(Font font) {
        this.customFont = font;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        radioIcon = ImageUtils.dye(radioIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        radioSIcon = ImageUtils.dye(radioIcon, selectedColor);
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
        NetRadioInfo netRadioInfo = (NetRadioInfo) value;
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setFont(customFont);
//        setIcon(radioIcon);
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
//        setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

        CustomPanel outerPanel = new CustomPanel();
        JLabel iconLabel = new JLabel();
        JLabel nameLabel = new JLabel();
        JLabel djLabel = new JLabel();
        JLabel categoryLabel = new JLabel();
        JLabel trackCountLabel = new JLabel();
        JLabel playCountLabel = new JLabel();
//        JLabel createTimeLabel = new JLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(25);
        iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : isSelected ? radioSIcon : radioIcon);

        iconLabel.setHorizontalAlignment(CENTER);
        nameLabel.setHorizontalAlignment(CENTER);
        djLabel.setHorizontalAlignment(CENTER);
        categoryLabel.setHorizontalAlignment(CENTER);
        trackCountLabel.setHorizontalAlignment(CENTER);
        playCountLabel.setHorizontalAlignment(CENTER);
//        createTimeLabel.setHorizontalAlignment(CENTER);

        iconLabel.setVerticalAlignment(CENTER);
        nameLabel.setVerticalAlignment(CENTER);
        djLabel.setVerticalAlignment(CENTER);
        categoryLabel.setVerticalAlignment(CENTER);
        trackCountLabel.setVerticalAlignment(CENTER);
        playCountLabel.setVerticalAlignment(CENTER);
//        createTimeLabel.setVerticalAlignment(CENTER);

        outerPanel.setOpaque(false);
        iconLabel.setOpaque(false);
        nameLabel.setOpaque(false);
        djLabel.setOpaque(false);
        categoryLabel.setOpaque(false);
        trackCountLabel.setOpaque(false);
        playCountLabel.setOpaque(false);
//        createTimeLabel.setOpaque(false);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        djLabel.setForeground(isSelected ? selectedColor : foreColor);
        categoryLabel.setForeground(isSelected ? selectedColor : foreColor);
        trackCountLabel.setForeground(isSelected ? selectedColor : foreColor);
        playCountLabel.setForeground(isSelected ? selectedColor : foreColor);
//        createTimeLabel.setForeground(isSelected ? selectedColor : foreColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        djLabel.setFont(customFont);
        categoryLabel.setFont(customFont);
        trackCountLabel.setFont(customFont);
        playCountLabel.setFont(customFont);
//        createTimeLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(djLabel);
        outerPanel.add(categoryLabel);
        outerPanel.add(trackCountLabel);
        outerPanel.add(playCountLabel);
//        outerPanel.add(createTimeLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtils.textToHtml(NetMusicSource.names[netRadioInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.getName(), maxWidth));
        String dj = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", maxWidth));
        String category = netRadioInfo.hasCategory() ? netRadioInfo.getCategory() : "";
        String trackCount = netRadioInfo.hasTrackCount() ? netRadioInfo.getTrackCount() + " 节目" : "";
        String playCount = netRadioInfo.hasPlayCount() ? StringUtils.formatNumber(netRadioInfo.getPlayCount()) : "";
//        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        djLabel.setText(dj);
        categoryLabel.setText(category);
        trackCountLabel.setText(trackCount);
        playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        Dimension ps3 = djLabel.getPreferredSize();
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
