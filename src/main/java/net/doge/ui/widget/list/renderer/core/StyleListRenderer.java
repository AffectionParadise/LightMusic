package net.doge.ui.widget.list.renderer.core;

import lombok.Data;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 默认的主题列表显示渲染器
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

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel typeLabel = new CustomLabel();
    private CustomLabel inUseLabel = new CustomLabel();

    private final String CUSTOM = I18n.getText("custom");
    private final String PRESET = I18n.getText("preset");
    private final String IN_USE = I18n.getText("inUse");

    private MainFrame f;

    public StyleListRenderer(MainFrame f) {
        this.f = f;
        init();
    }

    private void init() {
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        float alpha = 0.5f;
        typeLabel.setOpacity(alpha);
//        inUseLabel.setInstantAlpha(alpha);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(typeLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(inUseLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        UIStyle style = (UIStyle) value;

        BufferedImage img = style.getImgThumb();
        if (img != null) iconLabel.setIcon(new ImageIcon(img));

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        inUseLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(tinyFont);
        inUseLabel.setFont(tinyFont);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(style.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String type = HtmlUtil.textToHtml(style.isCustom() ? CUSTOM : PRESET);
        String inUse = HtmlUtil.textToHtml(f.currUIStyle == style ? IN_USE : "");

        iconLabel.setText(source);
        nameLabel.setText(name);
        typeLabel.setText(type);
        inUseLabel.setText(inUse);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
