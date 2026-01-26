package net.doge.ui.widget.list.renderer.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetUserInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.LangUtil;
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
public class NetUserListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel avatarLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel genderLabel = new CustomLabel();
    //    private CustomLabel birthdayLabel = new CustomLabel();
//    private CustomLabel areaLabel = new CustomLabel();
    private CustomLabel followLabel = new CustomLabel();
    private CustomLabel fanLabel = new CustomLabel();
    private CustomLabel playlistCountLabel = new CustomLabel();

    private static ImageIcon userIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.userItem"), ImageConstants.MEDIUM_WIDTH));

    public NetUserListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        userIcon = ImageUtil.dye(userIcon, iconColor);
    }

    private void init() {
        avatarLabel.setIconTextGap(0);

        genderLabel.setFont(tinyFont);
//        birthdayLabel.setFont(tinyFont);
//        areaLabel.setFont(tinyFont);
        followLabel.setFont(tinyFont);
        fanLabel.setFont(tinyFont);
        playlistCountLabel.setFont(tinyFont);

        float alpha = 0.5f;
        genderLabel.setInstantAlpha(alpha);
        followLabel.setInstantAlpha(alpha);
        fanLabel.setInstantAlpha(alpha);
        playlistCountLabel.setInstantAlpha(alpha);

        int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(avatarLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(genderLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(followLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(fanLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playlistCountLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetUserInfo userInfo = (NetUserInfo) value;

        avatarLabel.setIcon(userInfo.hasAvatarThumb() ? new ImageIcon(userInfo.getAvatarThumb()) : userIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        avatarLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        genderLabel.setForeground(textColor);
//        birthdayLabel.setForeground(textColor);
//        areaLabel.setForeground(textColor);
        followLabel.setForeground(textColor);
        fanLabel.setForeground(textColor);
        playlistCountLabel.setForeground(textColor);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(userInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String gender = userInfo.hasGender() ? HtmlUtil.textToHtml(userInfo.getGender()) : "";
        boolean hasRadioCount = userInfo.hasRadioCount(), hasProgramCount = userInfo.hasProgramCount();
        String playlistCount = userInfo.hasPlaylistCount() ? HtmlUtil.textToHtml(userInfo.getPlaylistCount() + " 歌单")
                : hasRadioCount && hasProgramCount ? HtmlUtil.textToHtml(userInfo.getRadioCount() + " 电台，" + userInfo.getProgramCount() + " 节目")
                : hasRadioCount ? HtmlUtil.textToHtml(userInfo.getRadioCount() + " 电台")
                : hasProgramCount ? HtmlUtil.textToHtml(userInfo.getProgramCount() + (userInfo.fromDt() ? " 专辑" : userInfo.fromBI() ? " 视频" : " 节目"))
                : "";
        String follow = userInfo.hasFollow() ? HtmlUtil.textToHtml(LangUtil.formatNumberWithoutSuffix(userInfo.getFollow()) + " 关注") : "";
        String fan = userInfo.hasFan() ? HtmlUtil.textToHtml(LangUtil.formatNumberWithoutSuffix(userInfo.getFan()) + " 粉丝") : "";

        avatarLabel.setText(source);
        nameLabel.setText(name);
        genderLabel.setText(gender);
        playlistCountLabel.setText(playlistCount);
        followLabel.setText(follow);
        fanLabel.setText(fan);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}