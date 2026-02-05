package net.doge.ui.widget.list.renderer.service;

import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
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
public class NetPlaylistListRenderer extends CustomListCellRenderer {
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
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        creatorLabel.setFont(tinyFont);
        playCountLabel.setFont(tinyFont);
        trackCountLabel.setFont(tinyFont);

        float opacity = 0.5f;
        creatorLabel.setOpacity(opacity);
        playCountLabel.setOpacity(opacity);
        trackCountLabel.setOpacity(opacity);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(creatorLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(trackCountLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(playCountLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
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

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(playlistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String creator = playlistInfo.hasCreator() ? HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(
                StringUtil.shorten(playlistInfo.getCreator(), RendererConstants.STRING_MAX_LENGTH), tw)) : "";
        String playCount = playlistInfo.hasPlayCount() ? HtmlUtil.textToHtml(LangUtil.formatNumber(playlistInfo.getPlayCount())) : "";
        String trackCount = playlistInfo.hasTrackCount() ? HtmlUtil.textToHtml(playlistInfo.getTrackCount() + " 歌曲") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        creatorLabel.setText(creator);
        playCountLabel.setText(playCount);
        trackCountLabel.setText(trackCount);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}