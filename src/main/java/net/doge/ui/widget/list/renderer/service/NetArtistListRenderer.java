package net.doge.ui.widget.list.renderer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetArtistInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
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
public class NetArtistListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel songNumLabel = new CustomLabel();
    private CustomLabel albumNumLabel = new CustomLabel();
    private CustomLabel mvNumLabel = new CustomLabel();

    private static ImageIcon artistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.artistItem"), ImageConstants.MEDIUM_WIDTH));

    public NetArtistListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        artistIcon = ImageUtil.dye(artistIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        songNumLabel.setFont(tinyFont);
        albumNumLabel.setFont(tinyFont);
        mvNumLabel.setFont(tinyFont);

        float alpha = 0.5f;
        songNumLabel.setInstantAlpha(alpha);
        albumNumLabel.setInstantAlpha(alpha);
        mvNumLabel.setInstantAlpha(alpha);

        int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(songNumLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(albumNumLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(mvNumLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetArtistInfo artistInfo = (NetArtistInfo) value;

        iconLabel.setIcon(artistInfo.hasCoverImgThumb() ? new ImageIcon(artistInfo.getCoverImgThumb()) : artistIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        songNumLabel.setForeground(textColor);
        albumNumLabel.setForeground(textColor);
        mvNumLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(artistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String songNum = artistInfo.hasSongNum() ? HtmlUtil.textToHtml(artistInfo.fromME() ? artistInfo.getSongNum() + " 电台" : artistInfo.getSongNum() + " 歌曲") : "";
        String albumNum = artistInfo.hasAlbumNum() ? HtmlUtil.textToHtml(artistInfo.getAlbumNum() + " 专辑") : "";
        String mvNum = artistInfo.hasMvNum() ? HtmlUtil.textToHtml(artistInfo.getMvNum() + " MV") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        songNumLabel.setText(songNum);
        albumNumLabel.setText(albumNum);
        mvNumLabel.setText(mvNum);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}