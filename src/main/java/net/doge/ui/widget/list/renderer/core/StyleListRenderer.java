package net.doge.ui.widget.list.renderer.core;

import lombok.Data;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.StringUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Doge
 * @description 默认的主题列表显示渲染器
 * @date 2020/12/7
 */
@Data
public class StyleListRenderer extends CustomListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
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

        float opacity = 0.5f;
        typeLabel.setOpacity(opacity);

        typeLabel.setFont(tinyFont);
        inUseLabel.setFont(tinyFont);

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

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(style.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String type = HtmlUtil.textToHtml(style.isCustom() ? CUSTOM : PRESET);
        String inUse = HtmlUtil.textToHtml(UIStyleStorage.currUIStyle == style ? IN_USE : "");

        iconLabel.setText(source);
        nameLabel.setText(name);
        typeLabel.setText(type);
        inUseLabel.setText(inUse);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
