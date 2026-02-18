package net.doge.ui.widget.list.renderer.service;

import lombok.Data;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.entity.service.NetCommentInfo;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Doge
 * @description
 * @date 2020/12/7
 */
@Data
public class NetCommentListRenderer extends CustomListCellRenderer {
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel textLabel = new CustomLabel();

//    private CustomPanel outerPanel = new CustomPanel();
//    private CustomPanel westPanel = new CustomPanel();
//    private CustomLabel iconLabel = new CustomLabel();
//    private CustomPanel centerPanel = new CustomPanel();
//    private CustomLabel nameLabel = new CustomLabel();
//    private CustomLabel timeLabel = new CustomLabel();
//    private CustomLabel contentLabel = new CustomLabel();
//    private CustomLabel likeLabel = new CustomLabel();

    private static ImageIcon defaultProfile = new ImageIcon(ImageUtil.radius(ImageUtil.width(LMIconManager.getImage("list.profile"), ImageConstants.PROFILE_WIDTH), ScaleUtil.scale(0.1)));

    public NetCommentListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        defaultProfile = ImageUtil.dye(defaultProfile, iconColor);
    }

    private void init() {
        outerPanel.setLayout(new BorderLayout(ScaleUtil.scale(10), ScaleUtil.scale(0)));
        outerPanel.add(iconLabel, BorderLayout.WEST);
        outerPanel.add(textLabel, BorderLayout.CENTER);

        // 使图标靠上
        iconLabel.setVerticalAlignment(TOP);
        // 文字靠左上
        textLabel.setHorizontalAlignment(LEFT);
        textLabel.setVerticalAlignment(TOP);
        textLabel.setIconTextGap(ScaleUtil.scale(15));
        textLabel.setOpacity(0.8f);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        textLabel.setForeground(textColor);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);

        NetCommentInfo commentInfo = (NetCommentInfo) value;
        boolean sub = commentInfo.isSub();
        BufferedImage profile = commentInfo.getProfile();

        int lw = list.getVisibleRect().width - ScaleUtil.scale(10);

        iconLabel.setIcon(profile != null ? new ImageIcon(profile) : defaultProfile);
        textLabel.setText(HtmlUtil.textToHtmlWithSpace(HtmlUtil.wrapLineByWidth(commentInfo.toString(), lw - ScaleUtil.scale(sub ? 235 : 160))));

        // 缩进
        outerPanel.setBorder(new HDEmptyBorder(6, sub ? 120 : 45, 0, 0));

        Dimension ips = iconLabel.getPreferredSize();
        Dimension tps = textLabel.getPreferredSize();
        outerPanel.setPreferredSize(new Dimension(lw, Math.max(ips.height, tps.height) + ScaleUtil.scale(12)));
        list.setFixedCellWidth(lw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }

//    private void init() {
//        outerPanel.setLayout(new BorderLayout());
//        outerPanel.add(westPanel, BorderLayout.WEST);
//        outerPanel.add(centerPanel, BorderLayout.CENTER);
//
//        westPanel.add(iconLabel);
//
//        // 字体
//        nameLabel.setFont(customFont);
//        timeLabel.setFont(customFont);
//        contentLabel.setFont(customFont);
//        likeLabel.setFont(customFont);
//        // 对齐方式
//        nameLabel.setHorizontalAlignment(LEFT);
//        timeLabel.setHorizontalAlignment(LEFT);
//        contentLabel.setHorizontalAlignment(LEFT);
//        likeLabel.setHorizontalAlignment(LEFT);
//
//        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
//        Component vs = CustomBox.createVerticalStrut(3);
//        centerPanel.add(nameLabel);
//        centerPanel.add(vs);
//        centerPanel.add(timeLabel);
//        centerPanel.add(vs);
//        centerPanel.add(contentLabel);
//        centerPanel.add(vs);
//        centerPanel.add(likeLabel);
//
//        // 使图标靠上
////        label.setVerticalTextPosition(TOP);
////        label.setHorizontalAlignment(LEFT);
////        label.setIconTextGap(ScaleUtil.scale(15));
//
//        outerPanel.setInstantDrawBg(true);
//    }
//
//    @Override
//    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        nameLabel.setForeground(textColor);
//        timeLabel.setForeground(textColor);
//        contentLabel.setForeground(textColor);
//        likeLabel.setForeground(textColor);
//
//        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
//
//        NetCommentInfo commentInfo = (NetCommentInfo) value;
//        boolean sub = commentInfo.isSub();
//        BufferedImage profile = commentInfo.getProfile();
//
//        int lw = list.getVisibleRect().width - 10;
//
//        String name = StringUtil.textToHtml(commentInfo.getUsername());
//        String time = commentInfo.getTime();
//        String content = StringUtil.textToHtml(commentInfo.getContent());
//        String likedCount = String.valueOf(commentInfo.getLikedCount());
//
////        label.setText(StringUtil.textToHtmlWithSpace(StringUtil.wrapLineByWidth(commentInfo.toString(), lw - (sub ? 235 : 160))));
////        label.setBorder(new HDEmptyBorder(0, sub ? 120 : 45, 0, 0));
////        label.setFont(customFont);
////        label.setIcon(profile != null ? new ImageIcon(profile) : defaultProfile);
//
//        outerPanel.setBorder(new HDEmptyBorder(0, sub ? 120 : 45, 0, 0));
//
//        iconLabel.setIcon(profile != null ? new ImageIcon(profile) : defaultProfile);
//        nameLabel.setText(name);
//        timeLabel.setText(time);
//        contentLabel.setText(content);
//        likeLabel.setText(likedCount);
//
//        centerPanel.revalidate();
//        Dimension ps = centerPanel.getPreferredSize();
//        outerPanel.setPreferredSize(new HDDimension(ps.width, ps.height + 12));
//        list.setFixedCellWidth(lw);
//
//        outerPanel.setDrawBg(isSelected || hoverIndex == index);
//
//        return outerPanel;
//    }
}
