package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetMvInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

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
public class TranslucentNetMvListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon mvIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "mvItem.png"), ImageConstants.profileWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        mvIcon = ImageUtils.dye(mvIcon, iconColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetMvInfo netMvInfo = (NetMvInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel artistLabel = new CustomLabel();
        CustomLabel durationLabel = new CustomLabel();
        CustomLabel playCountLabel = new CustomLabel();
        CustomLabel pubTimeLabel = new CustomLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIcon(netMvInfo.hasCoverImgThumb() ? new ImageIcon(netMvInfo.getCoverImgThumb())  : mvIcon);
        iconLabel.setIconTextGap(10);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        artistLabel.setForeground(textColor);
        durationLabel.setForeground(textColor);
        playCountLabel.setForeground(textColor);
        pubTimeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        artistLabel.setFont(customFont);
        durationLabel.setFont(customFont);
        playCountLabel.setFont(customFont);
        pubTimeLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(artistLabel);
        outerPanel.add(durationLabel);
        outerPanel.add(playCountLabel);
        outerPanel.add(pubTimeLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtils.textToHtml(NetMusicSource.names[netMvInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netMvInfo.getName(), maxWidth));
        String artist = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netMvInfo.getArtist(), maxWidth));
        String duration = netMvInfo.hasDuration() ? TimeUtils.format(netMvInfo.getDuration()) : "--:--";
        String playCount = netMvInfo.hasPlayCount() ? StringUtils.formatNumber(netMvInfo.getPlayCount()) : "";
        String pubTime = netMvInfo.hasPubTime() ? netMvInfo.getPubTime() : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        artistLabel.setText(artist);
        durationLabel.setText(duration);
        playCountLabel.setText(playCount);
        pubTimeLabel.setText(pubTime);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        Dimension ps3 = artistLabel.getPreferredSize();
        int ph = Math.max(ps.height, Math.max(ps2.height, ps3.height));
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
