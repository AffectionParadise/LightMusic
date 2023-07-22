package net.doge.ui.component.list.renderer.system;

import net.doge.constant.system.SimplePath;
import net.doge.constant.task.TaskStatus;
import net.doge.constant.task.TaskType;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.task.Task;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.slider.CustomSlider;
import net.doge.ui.component.slider.ui.MuteSliderUI;
import net.doge.util.system.FileUtil;
import net.doge.util.ui.ImageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.util.common.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon taskIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "taskItem.png"), ImageConstants.SMALL_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        taskIcon = ImageUtil.dye(taskIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Task task = (Task) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel(taskIcon);
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel typeLabel = new CustomLabel();
        CustomLabel sizeLabel = new CustomLabel();
        CustomSlider progressSlider = new CustomSlider();
        CustomLabel percentLabel = new CustomLabel();
        CustomLabel statusLabel = new CustomLabel();

        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        progressSlider.setUI(new MuteSliderUI(progressSlider, textColor));

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        sizeLabel.setForeground(textColor);
        percentLabel.setForeground(textColor);
        statusLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(customFont);
        sizeLabel.setFont(customFont);
        percentLabel.setFont(customFont);
        statusLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(typeLabel);
        outerPanel.add(sizeLabel);
        outerPanel.add(progressSlider);
        outerPanel.add(percentLabel);
        outerPanel.add(statusLabel);

        final int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String type = StringUtil.textToHtml(TaskType.NAMES[task.getType()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(task.getName(), RendererConstants.STRING_MAX_LENGTH), maxWidth));
        double percent = task.isProcessing() ? task.getPercent() : task.isFinished() ? 100 : 0;
        String percentStr = StringUtil.textToHtml(String.format("%.2f %%", percent));
        String size = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                String.format("%s / %s", FileUtil.getUnitString(task.getFinished()), FileUtil.getUnitString(task.getTotal())), maxWidth));
        String status = StringUtil.textToHtml(TaskStatus.NAMES[task.getStatus()]);

        nameLabel.setText(name);
        typeLabel.setText(type);
        sizeLabel.setText(size);
        progressSlider.setValue((int) percent);
        percentLabel.setText(percentStr);
        statusLabel.setText(status);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = sizeLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(lw, Math.max(ph + 10, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(lw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || index == hoverIndex);

        return outerPanel;
    }
}
