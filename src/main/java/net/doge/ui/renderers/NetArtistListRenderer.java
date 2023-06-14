package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.SimplePath;
import net.doge.models.entities.NetArtistInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetArtistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon artistIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "artistItem.png"), ImageConstants.mediumWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        artistIcon = ImageUtil.dye(artistIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetArtistInfo netArtistInfo = (NetArtistInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel songNumLabel = new CustomLabel();
        CustomLabel albumNumLabel = new CustomLabel();
        CustomLabel mvNumLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(netArtistInfo.hasCoverImgThumb() ? new ImageIcon(netArtistInfo.getCoverImgThumb()) : artistIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        songNumLabel.setForeground(textColor);
        albumNumLabel.setForeground(textColor);
        mvNumLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        songNumLabel.setFont(tinyFont);
        albumNumLabel.setFont(tinyFont);
        mvNumLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        songNumLabel.setBluntAlpha(alpha);
        albumNumLabel.setBluntAlpha(alpha);
        mvNumLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
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

        final int pw = 200, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netArtistInfo.getName(), tw));
        String songNum = netArtistInfo.hasSongNum() ? StringUtil.textToHtml(netArtistInfo.fromME() ? netArtistInfo.getSongNum() + " 电台" : netArtistInfo.getSongNum() + " 歌曲") : "";
        String albumNum = netArtistInfo.hasAlbumNum() ? StringUtil.textToHtml(netArtistInfo.getAlbumNum() + " 专辑") : "";
        String mvNum = netArtistInfo.hasMvNum() ? StringUtil.textToHtml(netArtistInfo.getMvNum() + " MV") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        songNumLabel.setText(songNum);
        albumNumLabel.setText(albumNum);
        mvNumLabel.setText(mvNum);
        
        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}