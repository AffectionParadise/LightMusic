package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetRadioInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMIconManager;
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
public class NetRadioListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
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
        iconLabel.setIconTextGap(0);

        float alpha = 0.5f;
        dCustomLabel.setBluntAlpha(alpha);
        categoryLabel.setBluntAlpha(alpha);
        trackCountLabel.setBluntAlpha(alpha);
        playCountLabel.setBluntAlpha(alpha);

        int sh = 10;
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

        outerPanel.setBluntDrawBg(true);
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

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        dCustomLabel.setFont(tinyFont);
        categoryLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
//        createTimeLabel.setFont(tinyFont);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(radioInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String dj = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(radioInfo.hasDj() ? radioInfo.getDj() : "", RendererConstants.STRING_MAX_LENGTH), tw));
        String category = radioInfo.hasCategory() ? StringUtil.textToHtml(radioInfo.getCategory()) : "";
        String trackCount = radioInfo.hasTrackCount() ? StringUtil.textToHtml(radioInfo.getTrackCount() + " 节目") : "";
        String playCount = radioInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(radioInfo.getPlayCount())) : "";
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