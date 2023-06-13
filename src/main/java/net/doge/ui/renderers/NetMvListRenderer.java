package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.SimplePath;
import net.doge.models.entities.NetMvInfo;
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
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class NetMvListRenderer extends DefaultListCellRenderer {
//    // 属性不能用 font，不然重复！
//    private Font customFont = Fonts.NORMAL;
//    private Color foreColor;
//    private Color selectedColor;
//    private Color textColor;
//    private Color iconColor;
//    private int hoverIndex = -1;
//
//    private static ImageIcon mvIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.mediumWidth));
//
//    public void setIconColor(Color iconColor) {
//        this.iconColor = iconColor;
//        mvIcon = ImageUtil.dye(mvIcon, iconColor);
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        NetMvInfo netMvInfo = (NetMvInfo) value;
//
//        CustomPanel outerPanel = new CustomPanel();
//        CustomLabel iconLabel = new CustomLabel();
//        CustomLabel nameLabel = new CustomLabel();
//        CustomLabel artistLabel = new CustomLabel();
//        CustomLabel durationLabel = new CustomLabel();
//        CustomLabel playCountLabel = new CustomLabel();
//        CustomLabel pubTimeLabel = new CustomLabel();
//
//        iconLabel.setHorizontalTextPosition(LEFT);
//        iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : mvIcon);
//        iconLabel.setIconTextGap(10);
//
//        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//        iconLabel.setForeground(textColor);
//        nameLabel.setForeground(textColor);
//        artistLabel.setForeground(textColor);
//        durationLabel.setForeground(textColor);
//        playCountLabel.setForeground(textColor);
//        pubTimeLabel.setForeground(textColor);
//
//        iconLabel.setFont(customFont);
//        nameLabel.setFont(customFont);
//        artistLabel.setFont(customFont);
//        durationLabel.setFont(customFont);
//        playCountLabel.setFont(customFont);
//        pubTimeLabel.setFont(customFont);
//
//        GridLayout layout = new GridLayout(1, 5);
//        layout.setHgap(15);
//        outerPanel.setLayout(layout);
//
//        outerPanel.add(iconLabel);
//        outerPanel.add(nameLabel);
//        outerPanel.add(artistLabel);
//        outerPanel.add(durationLabel);
//        outerPanel.add(playCountLabel);
//        outerPanel.add(pubTimeLabel);
//
//        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//        String source = StringUtil.textToHtml(NetMusicSource.names[netMvInfo.getSource()]);
//        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getName(), maxWidth));
//        String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getArtist(), maxWidth));
//        String duration = netMvInfo.hasDuration() ? TimeUtil.format(netMvInfo.getDuration()) : "--:--";
//        String playCount = netMvInfo.hasPlayCount() ? StringUtil.formatNumber(netMvInfo.getPlayCount()) : "";
//        String pubTime = netMvInfo.hasPubTime() ? netMvInfo.getPubTime() : "";
//
//        iconLabel.setText(source);
//        nameLabel.setText(name);
//        artistLabel.setText(artist);
//        durationLabel.setText(duration);
//        playCountLabel.setText(playCount);
//        pubTimeLabel.setText(pubTime);
//
//        Dimension ps = iconLabel.getPreferredSize();
//        Dimension ps2 = nameLabel.getPreferredSize();
//        Dimension ps3 = artistLabel.getPreferredSize();
//        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
//        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
//        outerPanel.setPreferredSize(d);
//        list.setFixedCellWidth(list.getVisibleRect().width - 10);
//
//        outerPanel.setBluntDrawBg(true);
//        outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//        return outerPanel;
//    }
//}
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetMvListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon mvIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.mediumWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        mvIcon = ImageUtil.dye(mvIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetMvInfo netMvInfo = (NetMvInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel artistLabel = new CustomLabel();
        CustomLabel durationLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel pubTimeLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb()) : mvIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        pubTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        artistLabel.setFont(tinyFont);
        durationLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
        pubTimeLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        artistLabel.setBluntAlpha(alpha);
        durationLabel.setBluntAlpha(alpha);
        playCountLabel.setBluntAlpha(alpha);
        pubTimeLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(artistLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(durationLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(pubTimeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = 200, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getName(), tw));
        String artist = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netMvInfo.getArtist(), tw));
        String duration = StringUtil.textToHtml(netMvInfo.hasDuration() ? TimeUtil.format(netMvInfo.getDuration()) : "--:--");
        String playCount = netMvInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netMvInfo.getPlayCount())) : "";
        String pubTime = netMvInfo.hasPubTime() ? StringUtil.textToHtml(netMvInfo.getPubTime()) : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        durationLabel.setText(duration);
        playCountLabel.setText(playCount);
        pubTimeLabel.setText(pubTime);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}