package net.doge.ui.widget.list.renderer.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.task.TaskStatus;
import net.doge.constant.task.TaskType;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.task.Task;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.MuteSliderUI;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.system.FileUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
public class DownloadListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel typeLabel = new CustomLabel();
    private CustomLabel sizeLabel = new CustomLabel();
    //    private CustomSlider progressSlider = new CustomSlider();
    private CustomLabel percentLabel = new CustomLabel();
    private CustomLabel statusLabel = new CustomLabel();

    private static ImageIcon taskIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.taskItem"), ImageConstants.SMALL_WIDTH));

    private final float alpha = 0.5f;

    public DownloadListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        taskIcon = ImageUtil.dye(taskIcon, iconColor);

        iconLabel.setIcon(taskIcon);
    }

    private void init() {
        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        iconLabel.setInstantAlpha(alpha);
        typeLabel.setInstantAlpha(alpha);
        sizeLabel.setInstantAlpha(alpha);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(typeLabel);
        outerPanel.add(sizeLabel);
//        outerPanel.add(progressSlider);
        outerPanel.add(percentLabel);
        outerPanel.add(statusLabel);

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Task task = (Task) value;

        int sliderIndex = 4;
        if (outerPanel.getComponent(sliderIndex) instanceof CustomSlider) outerPanel.remove(sliderIndex);
        // 多个任务同时进行时，需要使用不同的进度条，避免线程安全问题
        CustomSlider progressSlider = new CustomSlider();
        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        MuteSliderUI sliderUI = new MuteSliderUI(progressSlider, textColor);
        progressSlider.setUI(sliderUI);
        outerPanel.add(progressSlider, sliderIndex);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        sizeLabel.setForeground(textColor);
        percentLabel.setForeground(textColor);
        statusLabel.setForeground(textColor);

        // 已完成的任务透明显示
        if (task.isFinished()) {
            percentLabel.setInstantAlpha(alpha);
            statusLabel.setInstantAlpha(alpha);
            sliderUI.setRest(true);
        } else {
            percentLabel.setInstantAlpha(1f);
            statusLabel.setInstantAlpha(1f);
            sliderUI.setRest(false);
        }

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(customFont);
        sizeLabel.setFont(customFont);
        percentLabel.setFont(customFont);
        statusLabel.setFont(customFont);

        int lw = list.getVisibleRect().width - 10, maxWidth = (lw - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
        String type = StringUtil.textToHtml(TaskType.NAMES[task.getType()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(task.getName(), RendererConstants.STRING_MAX_LENGTH), maxWidth));
        double percent = task.isProcessing() ? task.getPercent() : task.isFinished() ? 100 : 0;
        String percentStr = StringUtil.textToHtml(String.format("%.1f%%", percent).replace(".0", ""));
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

        outerPanel.setDrawBg(isSelected || index == hoverIndex);

        return outerPanel;
    }
}
