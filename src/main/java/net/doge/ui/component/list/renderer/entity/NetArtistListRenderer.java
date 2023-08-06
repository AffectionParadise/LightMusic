package net.doge.ui.component.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetArtistInfo;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
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
public class NetArtistListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon artistIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.artistItem"), ImageConstants.MEDIUM_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        artistIcon = ImageUtil.dye(artistIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetArtistInfo artistInfo = (NetArtistInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel songNumLabel = new CustomLabel();
        CustomLabel albumNumLabel = new CustomLabel();
        CustomLabel mvNumLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(artistInfo.hasCoverImgThumb() ? new ImageIcon(artistInfo.getCoverImgThumb()) : artistIcon);

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

        final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(artistInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String songNum = artistInfo.hasSongNum() ? StringUtil.textToHtml(artistInfo.fromME() ? artistInfo.getSongNum() + " 电台" : artistInfo.getSongNum() + " 歌曲") : "";
        String albumNum = artistInfo.hasAlbumNum() ? StringUtil.textToHtml(artistInfo.getAlbumNum() + " 专辑") : "";
        String mvNum = artistInfo.hasMvNum() ? StringUtil.textToHtml(artistInfo.getMvNum() + " MV") : "";

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