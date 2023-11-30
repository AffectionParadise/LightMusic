package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.model.entity.NetCommentInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetCommentListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon defaultProfile = new ImageIcon(ImageUtil.setRadius(ImageUtil.width(LMIconManager.getImage("list.profile"), ImageConstants.PROFILE_WIDTH), 0.1));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        defaultProfile = ImageUtil.dye(defaultProfile, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        CustomPanel outerPanel = new CustomPanel();
        CustomLabel label = new CustomLabel();

        label.setForeground(textColor);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        outerPanel.setLayout(new GridLayout(1, 1));
        outerPanel.add(label);

        NetCommentInfo commentInfo = (NetCommentInfo) value;
        boolean sub = commentInfo.isSub();
        BufferedImage profile = commentInfo.getProfile();

        int lw = list.getVisibleRect().width - 10;

        // 使图标靠上
        label.setVerticalTextPosition(TOP);
        label.setHorizontalAlignment(LEFT);
        label.setText(StringUtil.textToHtmlWithSpace(StringUtil.wrapLineByWidth(commentInfo.toString(), lw - (sub ? 235 : 160))));
        label.setIconTextGap(15);
        label.setBorder(BorderFactory.createEmptyBorder(0, sub ? 120 : 45, 0, 0));
        label.setFont(customFont);
        label.setIcon(profile != null ? new ImageIcon(profile) : defaultProfile);

        Dimension ps = label.getPreferredSize();
        outerPanel.setPreferredSize(new Dimension(ps.width, ps.height + 12));
        list.setFixedCellWidth(lw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
