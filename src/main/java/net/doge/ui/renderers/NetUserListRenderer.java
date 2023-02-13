package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.entity.NetUserInfo;
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
public class NetUserListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon userIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.profileWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        userIcon = ImageUtil.dye(userIcon, iconColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
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

        avatarLabel.setHorizontalTextPosition(LEFT);
        avatarLabel.setIconTextGap(25);
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
        genderLabel.setFont(customFont);
//        birthdayLabel.setFont(customFont);
//        areaLabel.setFont(customFont);
        followLabel.setFont(customFont);
        followedLabel.setFont(customFont);
        playlistCountLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(avatarLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(genderLabel);
//        outerPanel.add(birthdayLabel);
//        outerPanel.add(areaLabel);
        outerPanel.add(playlistCountLabel);
        outerPanel.add(followLabel);
        outerPanel.add(followedLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtil.textToHtml(NetMusicSource.names[netUserInfo.getSource()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netUserInfo.getName(), maxWidth));
        String gender = netUserInfo.hasGender() ? netUserInfo.getGender() : "";
//        String birthday = netUserInfo.hasBirthday() ? netUserInfo.getBirthday() : "";
//        String area = netUserInfo.hasArea() ? netUserInfo.getArea() : "";
        boolean hasRadioCount = netUserInfo.hasRadioCount(), hasProgramCount = netUserInfo.hasProgramCount();
        String playlistCount = netUserInfo.hasPlaylistCount() ? netUserInfo.getPlaylistCount() + " 歌单"
                : hasRadioCount && hasProgramCount ? netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目"
                : hasRadioCount ? netUserInfo.getRadioCount() + " 电台"
                : hasProgramCount ? netUserInfo.getProgramCount() + (netUserInfo.fromDt() ? " 专辑" : netUserInfo.fromBI() ? " 视频" : " 节目")
                : "";
        String follow = netUserInfo.hasFollow() ? StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注" : "";
        String followed = netUserInfo.hasFollowed() ? StringUtil.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝" : "";

        avatarLabel.setText(source);
        nameLabel.setText(name);
        genderLabel.setText(gender);
//        birthdayLabel.setText(birthday);
//        areaLabel.setText(area);
        playlistCountLabel.setText(playlistCount);
        followLabel.setText(follow);
        followedLabel.setText(followed);

        Dimension ps = avatarLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        // 画背景
//        if (drawBg) {
//            Graphics2D g2d = (Graphics2D) g;
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setColor(getForeground());
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
//            // 注意这里不能用 getVisibleRect ！！！
//            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        }
//
//        super.paintComponent(g);
//    }
}
