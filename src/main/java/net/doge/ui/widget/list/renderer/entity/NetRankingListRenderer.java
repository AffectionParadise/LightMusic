package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetRankingInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.LangUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
public class NetRankingListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel playCountLabel = new CustomLabel();
    private CustomLabel updateFreLabel = new CustomLabel();
    private CustomLabel updateTimeLabel = new CustomLabel();

    private ImageIcon rankingIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.rankingItem"), ImageConstants.MEDIUM_WIDTH));

    public NetRankingListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        rankingIcon = ImageUtil.dye(rankingIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        playCountLabel.setFont(tinyFont);
        updateFreLabel.setFont(tinyFont);
        updateTimeLabel.setFont(tinyFont);

        float alpha = 0.5f;
        playCountLabel.setInstantAlpha(alpha);
        updateFreLabel.setInstantAlpha(alpha);
        updateTimeLabel.setInstantAlpha(alpha);

        int sh = 10;
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

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetRankingInfo rankingInfo = (NetRankingInfo) value;

        iconLabel.setIcon(rankingInfo.hasCoverImgThumb() ? new ImageIcon(rankingInfo.getCoverImgThumb()) : rankingIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        updateFreLabel.setForeground(textColor);
        updateTimeLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(rankingInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String playCount = rankingInfo.hasPlayCount() ? HtmlUtil.textToHtml(LangUtil.formatNumber(rankingInfo.getPlayCount())) : "";
        String updateFre = rankingInfo.hasUpdateFre() ? HtmlUtil.textToHtml(rankingInfo.getUpdateFre()) : "";
        String updateTime = rankingInfo.hasUpdateTime() ? HtmlUtil.textToHtml(rankingInfo.getUpdateTime() + " 更新") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        playCountLabel.setText(playCount);
        updateFreLabel.setText(updateFre);
        updateTimeLabel.setText(updateTime);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
