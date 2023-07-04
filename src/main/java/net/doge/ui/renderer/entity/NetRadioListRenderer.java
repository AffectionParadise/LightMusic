package net.doge.ui.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.NetRadioInfo;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.ImageUtil;
import net.doge.util.StringUtil;

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