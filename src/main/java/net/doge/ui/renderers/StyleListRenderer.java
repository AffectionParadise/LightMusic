package net.doge.ui.renderers;

import lombok.Data;
import net.doge.constants.Fonts;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author yzx
 * @Description 默认的风格列表显示渲染器
 * @Date 2020/12/7
 */
@Data
public class StyleListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private int hoverIndex = -1;

    private PlayerFrame f;

    public StyleListRenderer(PlayerFrame f) {
        this.f = f;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        UIStyle style = (UIStyle) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel typeLabel = new CustomLabel();
        CustomLabel inUseLabel = new CustomLabel();

        BufferedImage img = style.getImgThumb();
        if (img != null) iconLabel.setIcon(new ImageIcon(img));
        iconLabel.setIconTextGap(0);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        inUseLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(tinyFont);
        inUseLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        typeLabel.setBluntAlpha(alpha);
//        inUseLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(typeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(inUseLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = 200, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(style.getStyleName(), tw));
        String type = StringUtil.textToHtml(style.isCustom() ? "自定义" : "预设");
        String inUse = StringUtil.textToHtml(f.currUIStyle == style ? "使用中" : "");

        iconLabel.setText(source);
        nameLabel.setText(name);
        typeLabel.setText(type);
        inUseLabel.setText(inUse);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
