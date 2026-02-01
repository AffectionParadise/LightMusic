package net.doge.ui.widget.list.renderer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetRadioInfo;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.LangUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
public class NetRadioListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel dCustomLabel = new CustomLabel();
    private CustomLabel categoryLabel = new CustomLabel();
    private CustomLabel trackCountLabel = new CustomLabel();
    private CustomLabel playCountLabel = new CustomLabel();
//    private CustomLabel createTimeLabel = new CustomLabel();

    private static ImageIcon radioIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.radioItem"), ImageConstants.MEDIUM_WIDTH));

    public NetRadioListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        radioIcon = ImageUtil.dye(radioIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        dCustomLabel.setFont(tinyFont);
        categoryLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
//        createTimeLabel.setFont(tinyFont);

        float alpha = 0.5f;
        dCustomLabel.setOpacity(alpha);
        categoryLabel.setOpacity(alpha);
        trackCountLabel.setOpacity(alpha);
        playCountLabel.setOpacity(alpha);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(dCustomLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(categoryLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(trackCountLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(playCountLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetRadioInfo radioInfo = (NetRadioInfo) value;

        iconLabel.setIcon(radioInfo.hasCoverImgThumb() ? new ImageIcon(radioInfo.getCoverImgThumb()) : radioIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        dCustomLabel.setForeground(textColor);
        categoryLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
//        createTimeLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(radioInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String dj = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(
                StringUtil.shorten(radioInfo.hasDj() ? radioInfo.getDj() : "", RendererConstants.STRING_MAX_LENGTH), tw));
        String category = radioInfo.hasCategory() ? HtmlUtil.textToHtml(radioInfo.getCategory()) : "";
        String trackCount = radioInfo.hasTrackCount() ? HtmlUtil.textToHtml(radioInfo.getTrackCount() + " 节目") : "";
        String playCount = radioInfo.hasPlayCount() ? HtmlUtil.textToHtml(LangUtil.formatNumber(radioInfo.getPlayCount())) : "";
//        String createTime = radioInfo.hasCreateTime() ? radioInfo.getCreateTime() : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        dCustomLabel.setText(dj);
        categoryLabel.setText(category);
        trackCountLabel.setText(trackCount);
        playCountLabel.setText(playCount);
//        createTimeLabel.setText(createTime);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}