package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.SimplePath;
import net.doge.models.entities.NetRadioInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;

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
//public class NetRadioListRenderer extends DefaultListCellRenderer {
//    // 属性不能用 font，不然重复！
//    private Font customFont = Fonts.NORMAL;
//    private Color foreColor;
//    private Color selectedColor;
//    private Color textColor;
//    private Color iconColor;
//    private int hoverIndex = -1;
//
//    private static ImageIcon radioIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.mediumWidth));
//
//    public void setIconColor(Color iconColor) {
//        this.iconColor = iconColor;
//        radioIcon = ImageUtil.dye(radioIcon, iconColor);
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        NetRadioInfo netRadioInfo = (NetRadioInfo) value;
//
//        CustomPanel outerPanel = new CustomPanel();
//        CustomLabel iconLabel = new CustomLabel();
//        CustomLabel nameLabel = new CustomLabel();
//        CustomLabel dCustomLabel = new CustomLabel();
//        CustomLabel categoryLabel = new CustomLabel();
//        CustomLabel trackCountLabel = new CustomLabel();
//        CustomLabel playCountLabel = new CustomLabel();
////        CustomLabel createTimeLabel = new CustomLabel();
//
//        iconLabel.setHorizontalTextPosition(LEFT);
//        iconLabel.setIconTextGap(25);
//        iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);
//
//        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//        iconLabel.setForeground(textColor);
//        nameLabel.setForeground(textColor);
//        dCustomLabel.setForeground(textColor);
//        categoryLabel.setForeground(textColor);
//        trackCountLabel.setForeground(textColor);
//        playCountLabel.setForeground(textColor);
////        createTimeLabel.setForeground(textColor);
//
//        iconLabel.setFont(customFont);
//        nameLabel.setFont(customFont);
//        dCustomLabel.setFont(customFont);
//        categoryLabel.setFont(customFont);
//        trackCountLabel.setFont(customFont);
//        playCountLabel.setFont(customFont);
////        createTimeLabel.setFont(customFont);
//
//        GridLayout layout = new GridLayout(1, 5);
//        layout.setHgap(15);
//        outerPanel.setLayout(layout);
//
//        outerPanel.add(iconLabel);
//        outerPanel.add(nameLabel);
//        outerPanel.add(dCustomLabel);
//        outerPanel.add(categoryLabel);
//        outerPanel.add(trackCountLabel);
//        outerPanel.add(playCountLabel);
////        outerPanel.add(createTimeLabel);
//
//        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
//        String source = StringUtil.textToHtml(NetMusicSource.names[netRadioInfo.getSource()]);
//        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.getName(), maxWidth));
//        String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", maxWidth));
//        String category = netRadioInfo.hasCategory() ? netRadioInfo.getCategory() : "";
//        String trackCount = netRadioInfo.hasTrackCount() ? netRadioInfo.getTrackCount() + " 节目" : "";
//        String playCount = netRadioInfo.hasPlayCount() ? StringUtil.formatNumber(netRadioInfo.getPlayCount()) : "";
////        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";
//
//        iconLabel.setText(source);
//        nameLabel.setText(name);
//        dCustomLabel.setText(dj);
//        categoryLabel.setText(category);
//        trackCountLabel.setText(trackCount);
//        playCountLabel.setText(playCount);
////        createTimeLabel.setText(createTime);
//
//        Dimension ps = iconLabel.getPreferredSize();
//        Dimension ps2 = nameLabel.getPreferredSize();
//        Dimension ps3 = dCustomLabel.getPreferredSize();
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
public class NetRadioListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon radioIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "radioItem.png"), ImageConstants.mediumWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        radioIcon = ImageUtil.dye(radioIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetRadioInfo netRadioInfo = (NetRadioInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel dCustomLabel = new CustomLabel();
        CustomLabel categoryLabel = new CustomLabel();
        CustomLabel trackCountLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
//        CustomLabel createTimeLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(netRadioInfo.hasCoverImgThumb() ? new ImageIcon(netRadioInfo.getCoverImgThumb()) : radioIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        dCustomLabel.setForeground(textColor);
        categoryLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
//        createTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        dCustomLabel.setFont(tinyFont);
        categoryLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
//        createTimeLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        dCustomLabel.setBluntAlpha(alpha);
        categoryLabel.setBluntAlpha(alpha);
        trackCountLabel.setBluntAlpha(alpha);
        playCountLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(dCustomLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(categoryLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(trackCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = 200, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.getName(), tw));
        String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRadioInfo.hasDj() ? netRadioInfo.getDj() : "", tw));
        String category = netRadioInfo.hasCategory() ? StringUtil.textToHtml(netRadioInfo.getCategory()) : "";
        String trackCount = netRadioInfo.hasTrackCount() ? StringUtil.textToHtml(netRadioInfo.getTrackCount() + " 节目") : "";
        String playCount = netRadioInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netRadioInfo.getPlayCount())) : "";
//        String createTime = netRadioInfo.hasCreateTime() ? netRadioInfo.getCreateTime() : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        dCustomLabel.setText(dj);
        categoryLabel.setText(category);
        trackCountLabel.setText(trackCount);
        playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}