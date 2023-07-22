package net.doge.ui.component.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.system.SimplePath;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetUserInfo;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.common.StringUtil;
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
public class NetUserListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon userIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.MEDIUM_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        userIcon = ImageUtil.dye(userIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetUserInfo netUserInfo = (NetUserInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel avatarLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel genderLabel = new CustomLabel();
//        CustomLabel birthdayLabel = new CustomLabel();
//        CustomLabel areaLabel = new CustomLabel();
        CustomLabel followLabel = new CustomLabel();
        CustomLabel followedLabel = new CustomLabel();
        CustomLabel playlistCountLabel = new CustomLabel();

        avatarLabel.setIconTextGap(0);
        avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : userIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        avatarLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        genderLabel.setForeground(textColor);
//        birthdayLabel.setForeground(textColor);
//        areaLabel.setForeground(textColor);
        followLabel.setForeground(textColor);
        followedLabel.setForeground(textColor);
        playlistCountLabel.setForeground(textColor);

        avatarLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        genderLabel.setFont(tinyFont);
//        birthdayLabel.setFont(tinyFont);
//        areaLabel.setFont(tinyFont);
        followLabel.setFont(tinyFont);
        followedLabel.setFont(tinyFont);
        playlistCountLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        genderLabel.setBluntAlpha(alpha);
        followLabel.setBluntAlpha(alpha);
        followedLabel.setBluntAlpha(alpha);
        playlistCountLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(avatarLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(genderLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(followLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(followedLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playlistCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(netUserInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String gender = netUserInfo.hasGender() ? StringUtil.textToHtml(netUserInfo.getGender()) : "";
        boolean hasRadioCount = netUserInfo.hasRadioCount(), hasProgramCount = netUserInfo.hasProgramCount();
        String playlistCount = netUserInfo.hasPlaylistCount() ? StringUtil.textToHtml(netUserInfo.getPlaylistCount() + " 歌单")
                : hasRadioCount && hasProgramCount ? StringUtil.textToHtml(netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目")
                : hasRadioCount ? StringUtil.textToHtml(netUserInfo.getRadioCount() + " 电台")
                : hasProgramCount ? StringUtil.textToHtml(netUserInfo.getProgramCount() + (netUserInfo.fromDt() ? " 专辑" : netUserInfo.fromBI() ? " 视频" : " 节目"))
                : "";
        String follow = netUserInfo.hasFollow() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注") : "";
        String followed = netUserInfo.hasFollowed() ? StringUtil.textToHtml(StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝") : "";

        avatarLabel.setText(source);
        nameLabel.setText(name);
        genderLabel.setText(gender);
        playlistCountLabel.setText(playlistCount);
        followLabel.setText(follow);
        followedLabel.setText(followed);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}