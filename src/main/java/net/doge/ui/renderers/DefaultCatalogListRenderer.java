package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @Author yzx
 * @Description 默认的风格列表显示渲染器
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultCatalogListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        File file = (File) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel nameLabel = new CustomLabel();

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);

        nameLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 1);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(nameLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(file.getAbsolutePath(), maxWidth));

        nameLabel.setText(name);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ps.height + 16, 50));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public void paintComponent(Graphics g) {
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

        super.paintComponent(g);
    }
}
