package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetUserInfo;
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
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon userIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "userItem.png"), ImageConstants.profileWidth));
    private ImageIcon userSIcon;

    public TranslucentNetUserListRenderer(Font font) {
        this.customFont = font;
    }

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
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        JLabel label = (JLabel) component;
//        label.setForeground(isSelected ? selectedColor : foreColor);
//        setDrawBg(isSelected);
//
        NetUserInfo netUserInfo = (NetUserInfo) value;
//        setIconTextGap(15);
//        setText(StringUtils.textToHtml(getText()));
//        setFont(customFont);
//        setIcon(radioIcon);
//        setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
//        setIcon(netUserInfo.hasCoverImgThumb() ? new ImageIcon(netUserInfo.getCoverImgThumb()) : radioIcon);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

        CustomPanel outerPanel = new CustomPanel();
        JLabel avatarLabel = new JLabel();
        JLabel nameLabel = new JLabel();
        JLabel genderLabel = new JLabel();
//        JLabel birthdayLabel = new JLabel();
//        JLabel areaLabel = new JLabel();
        JLabel followLabel = new JLabel();
        JLabel followedLabel = new JLabel();
        JLabel playlistCountLabel = new JLabel();

        avatarLabel.setHorizontalTextPosition(LEFT);
        avatarLabel.setIconTextGap(25);
        avatarLabel.setIcon(netUserInfo.hasAvatarThumb() ? new ImageIcon(netUserInfo.getAvatarThumb()) : isSelected ? userSIcon : userIcon);

        avatarLabel.setHorizontalAlignment(CENTER);
        nameLabel.setHorizontalAlignment(CENTER);
        genderLabel.setHorizontalAlignment(CENTER);
//        birthdayLabel.setHorizontalAlignment(CENTER);
//        areaLabel.setHorizontalAlignment(CENTER);
        followLabel.setHorizontalAlignment(CENTER);
        followedLabel.setHorizontalAlignment(CENTER);
        playlistCountLabel.setHorizontalAlignment(CENTER);

        avatarLabel.setVerticalAlignment(CENTER);
        nameLabel.setVerticalAlignment(CENTER);
        genderLabel.setVerticalAlignment(CENTER);
//        birthdayLabel.setVerticalAlignment(CENTER);
//        areaLabel.setVerticalAlignment(CENTER);
        followLabel.setVerticalAlignment(CENTER);
        followedLabel.setVerticalAlignment(CENTER);
        playlistCountLabel.setVerticalAlignment(CENTER);

        outerPanel.setOpaque(false);
        avatarLabel.setOpaque(false);
        nameLabel.setOpaque(false);
        genderLabel.setOpaque(false);
//        birthdayLabel.setOpaque(false);
//        areaLabel.setOpaque(false);
        followLabel.setOpaque(false);
        followedLabel.setOpaque(false);
        playlistCountLabel.setOpaque(false);

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
        String playlistCount = netUserInfo.hasPlaylistCount() ? netUserInfo.getPlaylistCount() + " 歌单"
                : netUserInfo.hasRadioCount() && netUserInfo.hasProgramCount() ? netUserInfo.getRadioCount() + " 电台，" + netUserInfo.getProgramCount() + " 节目"
                : netUserInfo.hasRadioCount() ? netUserInfo.getRadioCount() + " 电台"
                : netUserInfo.hasProgramCount() ? netUserInfo.getProgramCount() + " 节目"
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
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public void paint(Graphics g) {
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

        super.paint(g);
    }
}
