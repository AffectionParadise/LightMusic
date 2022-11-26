package net.doge.ui.componentui;

import net.doge.constants.SimplePath;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.ui.components.CustomComboBox;
import net.doge.ui.components.CustomComboPopup;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.ui.renderers.ComboBoxRenderer;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 下拉框元素标签自定义 UI
 * @Date 2020/12/13
 */
public class ComboBoxUI extends BasicComboBoxUI {
    private CustomComboBox comboBox;
    private PlayerFrame f;
    private Color foreColor;

    private ImageIcon arrowIcon = new ImageIcon(SimplePath.ICON_PATH + "arrow.png");

    public ComboBoxUI(CustomComboBox comboBox, PlayerFrame f, Color foreColor) {
        this.comboBox = comboBox;
        this.f = f;
        this.foreColor = foreColor;

        arrowIcon = ImageUtils.dye(arrowIcon, foreColor);

        // 下拉列表渲染
        comboBox.setRenderer(new ComboBoxRenderer(foreColor));
        // 设置最大显示项目数量
        comboBox.setMaximumRowCount(15);
        final int width = 170;
        if (comboBox.getPreferredSize().width < width) comboBox.setPreferredSize(new Dimension(width, 30));

        comboBox.setForeground(foreColor);
    }

    public ComboBoxUI(CustomComboBox comboBox, PlayerFrame f, Color foreColor, int width) {
        this(comboBox, f, foreColor);
        comboBox.setPreferredSize(new Dimension(width, 30));
    }

//    @Override
//    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
//        // 画文字
//        String text = (String) comboBox.getSelectedItem();
//        if (text != null) {
//            Graphics2D g2d = (Graphics2D) g;
//            FontMetrics metrics = comboBox.getFontMetrics(font);
//            int sw = metrics.stringWidth(text);
//            int sh = metrics.getHeight();
//            g2d.setColor(foreColor);
//            g2d.drawString(text, (bounds.width - sw) / 2, (bounds.height - sh) / 2 + 16);
//        }
//    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(foreColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, comboBox.isEntered() ? 0.3f : 0.15f));
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    protected JButton createArrowButton() {
        CustomButton btn = new CustomButton();
        btn.setIcon(arrowIcon);
        btn.addMouseListener(new ButtonMouseListener(btn, f));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comboBox.setEntered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!comboBox.getBounds().contains(e.getPoint())) comboBox.setEntered(false);
            }
        });
        return btn;
    }

    @Override
    protected ComboPopup createPopup() {
        return new CustomComboPopup(comboBox, f, foreColor);
    }
}
