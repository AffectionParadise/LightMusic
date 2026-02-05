package net.doge.ui.widget.list.renderer.core;

import lombok.Data;
import net.doge.constant.core.task.TaskStatus;
import net.doge.constant.core.task.TaskType;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.core.task.Task;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.MuteSliderUI;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.os.FileUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
public class DownloadListRenderer extends CustomListCellRenderer {
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

    private final float opacity = 0.5f;

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
        layout.setHgap(ScaleUtil.scale(15));
        outerPanel.setLayout(layout);

        iconLabel.setOpacity(opacity);
        typeLabel.setOpacity(opacity);
        sizeLabel.setOpacity(opacity);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(typeLabel);
        outerPanel.add(sizeLabel);
//        outerPanel.add(progressSlider);
        outerPanel.add(percentLabel);
        outerPanel.add(statusLabel);
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
            percentLabel.setOpacity(opacity);
            statusLabel.setOpacity(opacity);
            sliderUI.setRest(true);
        } else {
            percentLabel.setOpacity(1f);
            statusLabel.setOpacity(1f);
            sliderUI.setRest(false);
        }

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(customFont);
        sizeLabel.setFont(customFont);
        percentLabel.setFont(customFont);
        statusLabel.setFont(customFont);

        int lw = list.getVisibleRect().width - ScaleUtil.scale(10), maxWidth = (lw - (outerPanel.getComponentCount() - 1) * ((GridLayout) outerPanel.getLayout()).getHgap()) / outerPanel.getComponentCount();
        String type = HtmlUtil.textToHtml(TaskType.NAMES[task.getType()]);
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(task.getName(), RendererConstants.STRING_MAX_LENGTH), maxWidth));
        double percent = task.isProcessing() ? task.getPercent() : task.isFinished() ? 100 : 0;
        String percentStr = HtmlUtil.textToHtml(String.format("%.1f%%", percent).replace(".0", ""));
        String size = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(
                String.format("%s / %s", FileUtil.getUnitString(task.getFinished()), FileUtil.getUnitString(task.getTotal())), maxWidth));
        String status = HtmlUtil.textToHtml(TaskStatus.NAMES[task.getStatus()]);

        nameLabel.setText(name);
        typeLabel.setText(type);
        sizeLabel.setText(size);
        progressSlider.setValue((int) percent);
        percentLabel.setText(percentStr);
        statusLabel.setText(status);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = sizeLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(lw, Math.max(ph + ScaleUtil.scale(10), ScaleUtil.scale(46)));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(lw);

        outerPanel.setDrawBg(isSelected || index == hoverIndex);

        return outerPanel;
    }

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}
