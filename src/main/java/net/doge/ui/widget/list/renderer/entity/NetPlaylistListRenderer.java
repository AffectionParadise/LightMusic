package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
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
public class NetPlaylistListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel creatorLabel = new CustomLabel();
    private CustomLabel playCountLabel = new CustomLabel();
    private CustomLabel trackCountLabel = new CustomLabel();

    private static ImageIcon playlistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.playlistItem"), ImageConstants.MEDIUM_WIDTH));

    public NetPlaylistListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        playlistIcon = ImageUtil.dye(playlistIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        creatorLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);

        float alpha = 0.5f;
        creatorLabel.setInstantAlpha(alpha);
        playCountLabel.setInstantAlpha(alpha);
        trackCountLabel.setInstantAlpha(alpha);

        int sh = 10;
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

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetPlaylistInfo playlistInfo = (NetPlaylistInfo) value;

        iconLabel.setIcon(playlistInfo.hasCoverImgThumb() ? new ImageIcon(playlistInfo.getCoverImgThumb()) : playlistIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);

        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        creatorLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        trackCountLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
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

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}