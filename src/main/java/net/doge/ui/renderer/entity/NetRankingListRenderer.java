package net.doge.ui.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.system.SimplePath;
import net.doge.model.entity.NetRankingInfo;
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
public class NetRankingListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private ImageIcon rankingIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "rankingItem.png"), ImageConstants.mediumWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        rankingIcon = ImageUtil.dye(rankingIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetRankingInfo netRankingInfo = (NetRankingInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel updateFreLabel = new CustomLabel();
        CustomLabel updateTimeLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(netRankingInfo.hasCoverImgThumb() ? new ImageIcon(netRankingInfo.getCoverImgThumb()) : rankingIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        updateFreLabel.setForeground(textColor);
        updateTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        playCountLabel.setFont(tinyFont);
        updateFreLabel.setFont(tinyFont);
        updateTimeLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        playCountLabel.setBluntAlpha(alpha);
        updateFreLabel.setBluntAlpha(alpha);
        updateTimeLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(playCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(updateFreLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(updateTimeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = 200, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netRankingInfo.getName(), tw));
        String playCount = netRankingInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(netRankingInfo.getPlayCount())) : "";
        String updateFre = netRankingInfo.hasUpdateFre() ? StringUtil.textToHtml(netRankingInfo.getUpdateFre()) : "";
        String updateTime = netRankingInfo.hasUpdateTime() ? StringUtil.textToHtml(netRankingInfo.getUpdateTime() + " 更新") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        playCountLabel.setText(playCount);
        updateFreLabel.setText(updateFre);
        updateTimeLabel.setText(updateTime);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
