package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetUserInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

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
public class TranslucentNetUserListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon userIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.profileWidth));
    private ImageIcon userSIcon;

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        userIcon = ImageUtils.dye(userIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        userSIcon = ImageUtils.dye(userIcon, selectedColor);
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
        avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : isSelected ? userSIcon : userIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        avatarLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        genderLabel.setForeground(isSelected ? selectedColor : foreColor);
//        birthdayLabel.setForeground(isSelected ? selectedColor : foreColor);
//        areaLabel.setForeground(isSelected ? selectedColor : foreColor);
        followLabel.setForeground(isSelected ? selectedColor : foreColor);
        followedLabel.setForeground(isSelected ? selectedColor : foreColor);
        playlistCountLabel.setForeground(isSelected ? selectedColor : foreColor);

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
        String source = StringUtils.textToHtml(NetMusicSource.names[netUserInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netUserInfo.getName(), maxWidth));
        String gender = netUserInfo.hasGender() ? netUserInfo.getGender() : "";
//        String birthday = netUserInfo.hasBirthday() ? netUserInfo.getBirthday() : "";
//        String area = netUserInfo.hasArea() ? netUserInfo.getArea() : "";
        boolean hasRadioCount = netUserInfo.hasRadioCount(), hasProgramCount = netUserInfo.hasProgramCount();
        String playlistCount = netUserInfo.hasPlaylistCount() ? netUserInfo.getPlaylistCount() + " 歌单"
                : hasRadioCount && hasProgramCount ? netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目"
                : hasRadioCount ? netUserInfo.getRadioCount() + " 电台"
                : hasProgramCount ? netUserInfo.getProgramCount() + (netUserInfo.fromDt() ? " 专辑" : netUserInfo.fromBI() ? " 视频" : " 节目")
                : "";
        String follow = netUserInfo.hasFollow() ? StringUtils.formatNumberWithoutSuffix(netUserInfo.getFollow()) + " 关注" : "";
        String followed = netUserInfo.hasFollowed() ? StringUtils.formatNumberWithoutSuffix(netUserInfo.getFollowed()) + " 粉丝" : "";

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

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            // 注意这里不能用 getVisibleRect ！！！
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
