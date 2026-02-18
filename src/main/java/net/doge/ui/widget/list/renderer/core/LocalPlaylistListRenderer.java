package net.doge.ui.widget.list.renderer.core;

import lombok.Data;
import net.doge.entity.service.LocalPlaylist;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.entity.ChoosableListItem;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description 默认的主题列表显示渲染器
 * @date 2020/12/7
 */
@Data
public class LocalPlaylistListRenderer extends CustomListCellRenderer {
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel nameLabel = new CustomLabel();

    public LocalPlaylistListRenderer() {
        init();
    }

    private void init() {
        GridLayout layout = new GridLayout(1, 1);
        layout.setHgap(ScaleUtil.scale(15));
        outerPanel.setLayout(layout);

        outerPanel.add(nameLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        ChoosableListItem<LocalPlaylist> item = (ChoosableListItem<LocalPlaylist>) value;
        LocalPlaylist playlist = item.getItem();

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(textColor);
        nameLabel.setOpacity(item.isSelected() ? 0.5f : 1f);

        int lw = list.getVisibleRect().width - ScaleUtil.scale(10), maxWidth = (lw - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(playlist.getName(), maxWidth));

        nameLabel.setText(name);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension d = new Dimension(lw, Math.max(ps.height + ScaleUtil.scale(16), ScaleUtil.scale(46)));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(lw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
