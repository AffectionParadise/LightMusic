package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetPlaylistInfo;
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
@NoArgsConstructor
public class NetPlaylistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon playlistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playlistItem"), ImageConstants.MEDIUM_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        playlistIcon = ImageUtil.dye(playlistIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetPlaylistInfo playlistInfo = (NetPlaylistInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel creatorLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel trackCountLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(playlistInfo.hasCoverImgThumb() ? new ImageIcon(playlistInfo.getCoverImgThumb()) : playlistIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        creatorLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        creatorLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        creatorLabel.setBluntAlpha(alpha);
        playCountLabel.setBluntAlpha(alpha);
        trackCountLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(creatorLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(trackCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(playlistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String creator = playlistInfo.hasCreator() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(playlistInfo.getCreator(), RendererConstants.STRING_MAX_LENGTH), tw)) : "";
        String playCount = playlistInfo.hasPlayCount() ? StringUtil.textToHtml(StringUtil.formatNumber(playlistInfo.getPlayCount())) : "";
        String trackCount = playlistInfo.hasTrackCount() ? StringUtil.textToHtml(playlistInfo.getTrackCount() + " 歌曲") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        creatorLabel.setText(creator);
        playCountLabel.setText(playCount);
        trackCountLabel.setText(trackCount);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}