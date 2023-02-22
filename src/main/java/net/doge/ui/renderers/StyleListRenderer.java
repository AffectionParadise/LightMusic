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

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        inUseLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(customFont);
        inUseLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 2);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(typeLabel);
        outerPanel.add(inUseLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(style.getStyleName(), maxWidth));
        String type = StringUtil.textToHtml(style.isCustom() ? "自定义" : "预设");
        String inUse = StringUtil.textToHtml(f.currUIStyle == style ? "使用中" : "");

        nameLabel.setText(name);
        typeLabel.setText(type);
        inUseLabel.setText(inUse);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
