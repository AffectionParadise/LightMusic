package net.doge.ui.component.combobox.ui;

import net.doge.ui.MainFrame;
import net.doge.ui.component.button.CustomButton;
import net.doge.ui.component.button.listener.ButtonMouseListener;
import net.doge.ui.component.combobox.CustomComboBox;
import net.doge.ui.component.combobox.CustomComboPopup;
import net.doge.ui.component.list.renderer.system.ComboBoxRenderer;
import net.doge.util.system.LMIconManager;
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
    private CustomComboBox comboBox;
    private CustomComboPopup popup;
    private CustomButton arrowButton;
    private MainFrame f;
    private Color textColor;
    private Color iconColor;

    private ImageIcon arrowIcon = LMIconManager.getIcon("toolbar.arrow");

    public ComboBoxUI(CustomComboBox comboBox, MainFrame f) {
        this.comboBox = comboBox;
        this.f = f;

        textColor = f.currUIStyle.getTextColor();
        iconColor = f.currUIStyle.getIconColor();

        arrowIcon = ImageUtil.dye(arrowIcon, iconColor);

        // 下拉列表渲染
        comboBox.setRenderer(new ComboBoxRenderer(f));
        final int width = 170;
        if (comboBox.getPreferredSize().width < width) comboBox.setPreferredSize(new Dimension(width, 30));

        comboBox.setForeground(textColor);
    }

    public ComboBoxUI(CustomComboBox comboBox, MainFrame f, int width) {
        this(comboBox, f);
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
        g2d.setColor(f.currUIStyle.getTextColor());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, comboBox.getAlpha()));
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 10, 10);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
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
                if(comboBox.getBounds().contains(e.getPoint())) return;
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
                if(popup.getBounds().contains(e.getPoint())) return;
                comboBox.hidePopup();
            }
        });
        return popup;
    }

    public CustomButton getArrowButton() {
        return arrowButton;
    }

    public CustomComboPopup getPopup() {
        return popup;
    }
}
