package net.doge.ui.widget.list.renderer.service;

import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetRankingInfo;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.core.text.LangUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description
 * @date 2020/12/7
 */
@Data
public class NetRankingListRenderer extends CustomListCellRenderer {
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
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        playCountLabel.setFont(tinyFont);
        updateFreLabel.setFont(tinyFont);
        updateTimeLabel.setFont(tinyFont);

        float opacity = 0.5f;
        playCountLabel.setOpacity(opacity);
        updateFreLabel.setOpacity(opacity);
        updateTimeLabel.setOpacity(opacity);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(playCountLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(updateFreLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(updateTimeLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
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

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
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

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
