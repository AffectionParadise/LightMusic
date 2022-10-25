package net.doge.ui.renderers;

import net.doge.constants.*;
import net.doge.models.Task;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.componentui.MuteSliderUI;
import net.doge.utils.FileUtils;
import net.doge.utils.ImageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentDownloadListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon taskIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "taskItem.png"), ImageConstants.smallWidth));
    private ImageIcon taskSIcon;

    public TranslucentDownloadListRenderer(Font customFont) {
        this.customFont = customFont;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        taskIcon = ImageUtils.dye(taskIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        taskSIcon = ImageUtils.dye(taskIcon, selectedColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        JLabel label = (JLabel) component;
//        label.setForeground(isSelected ? selectedColor : foreColor);
//        setDrawBg(isSelected);
//        setIconTextGap(10);
//        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
//
//        setText(StringUtils.textToHtmlWithSpace(getText()));
//        setIcon(taskIcon);
//        setFont(customFont);
//
//        // 所有标签透明
//        label.setOpaque(false);
//        return this;

        Task task = (Task) value;

        CustomPanel outerPanel = new CustomPanel();
        JLabel iconLabel = new JLabel(isSelected ? taskSIcon : taskIcon);
        JLabel nameLabel = new JLabel();
        JLabel typeLabel = new JLabel();
        JLabel sizeLabel = new JLabel();
        JSlider progressSlider = new JSlider();
        JLabel percentLabel = new JLabel();
        JLabel statusLabel = new JLabel();

        iconLabel.setIconTextGap(15);

        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        progressSlider.setUI(new MuteSliderUI(progressSlider, isSelected ? selectedColor : foreColor));

        iconLabel.setHorizontalAlignment(CENTER);
        nameLabel.setHorizontalAlignment(CENTER);
        typeLabel.setHorizontalAlignment(CENTER);
        sizeLabel.setHorizontalAlignment(CENTER);
        percentLabel.setHorizontalAlignment(CENTER);
        statusLabel.setHorizontalAlignment(CENTER);

        outerPanel.setOpaque(false);
        iconLabel.setOpaque(false);
        nameLabel.setOpaque(false);
        typeLabel.setOpaque(false);
        sizeLabel.setOpaque(false);
        progressSlider.setOpaque(false);
        percentLabel.setOpaque(false);
        statusLabel.setOpaque(false);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        typeLabel.setForeground(isSelected ? selectedColor : foreColor);
        sizeLabel.setForeground(isSelected ? selectedColor : foreColor);
        percentLabel.setForeground(isSelected ? selectedColor : foreColor);
        statusLabel.setForeground(isSelected ? selectedColor : foreColor);

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

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(task.getName(), maxWidth));
        String type = StringUtils.textToHtml(TaskType.s[task.getType()]);
        double percent = task.isRunning() ? task.getPercent() : task.isFinished() ? 100 : 0;
        String percentStr = StringUtils.textToHtml(String.format("%.2f %%", percent));
        String size = StringUtils.textToHtml(StringUtils.wrapLineByWidth(
                String.format("%s / %s", FileUtils.getUnitString(task.getFinished()), FileUtils.getUnitString(task.getTotal())), maxWidth));
        String status = StringUtils.textToHtml(TaskStatus.s[task.getStatus()]);

        nameLabel.setText(name);
        typeLabel.setText(type);
        sizeLabel.setText(size);
        progressSlider.setValue((int) percent);
        percentLabel.setText(percentStr);
        statusLabel.setText(status);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = sizeLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 10, 50));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || index == hoverIndex);

        return outerPanel;
    }

    @Override
    public void paint(Graphics g) {
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

        super.paint(g);
    }
}
