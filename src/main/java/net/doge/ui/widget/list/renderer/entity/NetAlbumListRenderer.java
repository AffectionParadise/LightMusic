package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetAlbumInfo;
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
public class NetAlbumListRenderer extends DefaultListCellRenderer {
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
    private CustomLabel artistLabel = new CustomLabel();
    private CustomLabel songNumLabel = new CustomLabel();
    private CustomLabel publishTimeLabel = new CustomLabel();

    private static ImageIcon albumIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.albumItem"), ImageConstants.MEDIUM_WIDTH));

    public NetAlbumListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        albumIcon = ImageUtil.dye(albumIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        float alpha = 0.5f;
        artistLabel.setBluntAlpha(alpha);
        songNumLabel.setBluntAlpha(alpha);
        publishTimeLabel.setBluntAlpha(alpha);

        int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(artistLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(songNumLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(publishTimeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        outerPanel.setBluntDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetAlbumInfo albumInfo = (NetAlbumInfo) value;

        iconLabel.setIcon(albumInfo.hasCoverImgThumb() ? new ImageIcon(albumInfo.getCoverImgThumb()) : albumIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        songNumLabel.setForeground(textColor);
        publishTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        artistLabel.setFont(tinyFont);
        songNumLabel.setFont(tinyFont);
        publishTimeLabel.setFont(tinyFont);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(albumInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String artist = albumInfo.hasArtist() ? StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                StringUtil.shorten(albumInfo.getArtist(), RendererConstants.STRING_MAX_LENGTH), tw)) : "";
        String songNum = albumInfo.hasSongNum() ? StringUtil.textToHtml(albumInfo.isPhoto() ? albumInfo.getSongNum() + " 图片" : albumInfo.getSongNum() + " 歌曲") : "";
        String publishTime = albumInfo.hasPublishTime() ? StringUtil.textToHtml(albumInfo.getPublishTime() + " 发行") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        songNumLabel.setText(songNum);
        publishTimeLabel.setText(publishTime);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
