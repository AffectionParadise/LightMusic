package net.doge.ui.widget.list.renderer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetArtistInfo;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
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
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        songNumLabel.setFont(tinyFont);
        albumNumLabel.setFont(tinyFont);
        mvNumLabel.setFont(tinyFont);

        float alpha = 0.5f;
        songNumLabel.setOpacity(alpha);
        albumNumLabel.setOpacity(alpha);
        mvNumLabel.setOpacity(alpha);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(songNumLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(albumNumLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(mvNumLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
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

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
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