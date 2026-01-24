package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetMvInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.DurationUtil;
import net.doge.util.common.HtmlUtil;
import net.doge.util.common.LangUtil;
import net.doge.util.common.StringUtil;
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
public class NetMvListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel artistLabel = new CustomLabel();
    private CustomLabel durationLabel = new CustomLabel();
    private CustomLabel playCountLabel = new CustomLabel();
    private CustomLabel pubTimeLabel = new CustomLabel();

    private static ImageIcon mvIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.mvItem"), ImageConstants.MEDIUM_WIDTH));

    public NetMvListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        mvIcon = ImageUtil.dye(mvIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        artistLabel.setFont(tinyFont);
        durationLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
        pubTimeLabel.setFont(tinyFont);

        float alpha = 0.5f;
        artistLabel.setInstantAlpha(alpha);
        durationLabel.setInstantAlpha(alpha);
        playCountLabel.setInstantAlpha(alpha);
        pubTimeLabel.setInstantAlpha(alpha);

        int sh = 10;
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

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetMvInfo mvInfo = (NetMvInfo) value;

        iconLabel.setIcon(mvInfo.hasCoverImgThumb() ? new ImageIcon(mvInfo.getCoverImgThumb()) : mvIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        pubTimeLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(mvInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String artist = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(mvInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), tw));
        String duration = HtmlUtil.textToHtml(mvInfo.hasDuration() ? DurationUtil.format(mvInfo.getDuration()) : "--:--");
        String playCount = mvInfo.hasPlayCount() ? HtmlUtil.textToHtml(LangUtil.formatNumber(mvInfo.getPlayCount())) : "";
        String pubTime = mvInfo.hasPubTime() ? HtmlUtil.textToHtml(mvInfo.getPubTime()) : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        durationLabel.setText(duration);
        playCountLabel.setText(playCount);
        pubTimeLabel.setText(pubTime);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}