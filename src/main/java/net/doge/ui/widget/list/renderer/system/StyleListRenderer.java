package net.doge.ui.widget.list.renderer.system;

import lombok.Data;
import net.doge.constant.lang.I18n;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.StringUtil;

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
        iconLabel.setIconTextGap(0);

        float alpha = 0.5f;
        typeLabel.setInstantAlpha(alpha);
//        inUseLabel.setInstantAlpha(alpha);

        int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(typeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(inUseLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        outerPanel.setInstantDrawBg(true);
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

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(style.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String type = StringUtil.textToHtml(style.isCustom() ? CUSTOM : PRESET);
        String inUse = StringUtil.textToHtml(f.currUIStyle == style ? IN_USE : "");

        iconLabel.setText(source);
        nameLabel.setText(name);
        typeLabel.setText(type);
        inUseLabel.setText(inUse);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
