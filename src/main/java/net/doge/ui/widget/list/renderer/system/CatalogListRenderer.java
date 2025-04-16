package net.doge.ui.widget.list.renderer.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.ui.Fonts;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @Author Doge
 * @Description 默认的主题列表显示渲染器
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
public class CatalogListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel nameLabel = new CustomLabel();

    public CatalogListRenderer() {
        init();
    }

    private void init() {
        GridLayout layout = new GridLayout(1, 1);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(nameLabel);

        outerPanel.setBluntDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        File file = (File) value;

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(textColor);

        nameLabel.setFont(customFont);

        int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(file.getAbsolutePath(), maxWidth));

        nameLabel.setText(name);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension d = new Dimension(lw, Math.max(ps.height + 16, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(lw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
