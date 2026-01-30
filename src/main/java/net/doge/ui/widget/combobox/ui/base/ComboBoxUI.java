package net.doge.ui.widget.combobox.ui.base;

import lombok.Getter;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.button.listener.ButtonMouseListener;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.CustomComboPopup;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 下拉框元素标签自定义 UI
 * @Date 2020/12/13
 */
public class ComboBoxUI extends BasicComboBoxUI {
    protected CustomComboBox comboBox;
    @Getter
    protected CustomComboPopup popup;
    @Getter
    protected CustomButton arrowButton;
    protected MainFrame f;
    protected Color textColor;
    protected Color iconColor;

    protected ImageIcon arrowIcon = LMIconManager.getIcon("toolbar.arrow");

    public ComboBoxUI(CustomComboBox<?> comboBox, MainFrame f) {
        this.comboBox = comboBox;
        this.f = f;

        textColor = f.currUIStyle.getTextColor();
        iconColor = f.currUIStyle.getIconColor();

        arrowIcon = ImageUtil.dye(arrowIcon, iconColor);

        final int width = 170;
        if (comboBox.getPreferredSize().width < width) comboBox.setPreferredSize(new Dimension(width, 30));

        comboBox.setForeground(textColor);
    }

    public ComboBoxUI(CustomComboBox<?> comboBox, MainFrame f, int width) {
        this(comboBox, f);
        comboBox.setPreferredSize(new Dimension(width, 30));
    }

//    @Override
//    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
//        // 画文字
//        String text = (String) comboBox.getSelectedItem();
//        if (text != null) {
//            Graphics2D g2d = GraphicsUtil.setup(g);
//            FontMetrics metrics = comboBox.getFontMetrics(font);
//            int sw = metrics.stringWidth(text);
//            int sh = metrics.getHeight();
//            g2d.setColor(foreColor);
//            g2d.drawString(text, (bounds.width - sw) / 2, (bounds.height - sh) / 2 + 16);
//        }
//    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        g2d.setColor(f.currUIStyle.getTextColor());
        GraphicsUtil.srcOver(g2d, comboBox.getAlpha());
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        GraphicsUtil.srcOver(g2d);
    }

    @Override
    protected JButton createArrowButton() {
        arrowButton = new CustomButton();
        arrowButton.setIcon(arrowIcon);
        arrowButton.addMouseListener(new ButtonMouseListener(arrowButton, f));
        arrowButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comboBox.setEntered(true);
                comboBox.showPopup();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (comboBox.getBounds().contains(e.getPoint())) return;
                comboBox.setEntered(false);
            }
        });
        return arrowButton;
    }

    @Override
    protected ComboPopup createPopup() {
        popup = new CustomComboPopup(comboBox, f);
        popup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                validateHiding(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                validateHiding(e);
            }

            private void validateHiding(MouseEvent e) {
                if (popup.getBounds().contains(e.getPoint())) return;
                comboBox.hidePopup();
            }
        });
        return popup;
    }
}
