package net.doge.ui.widget.combobox.ui.base;

import lombok.Getter;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.button.CustomButton;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.popup.CustomComboPopup;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Doge
 * @description 下拉框元素标签自定义 UI
 * @date 2020/12/13
 */
public class CustomComboBoxUI extends BasicComboBoxUI {
    // 此处不要用父类的变量，因为需要初始化，避免 null 异常
    protected CustomComboBox<?> comboBox;
    @Getter
    protected CustomComboPopup popup;
    @Getter
    protected CustomButton arrowButton;
    protected MainFrame f;
    protected ImageIcon arrowIcon = LMIconManager.getIcon("toolbar.arrow");

    public CustomComboBoxUI(CustomComboBox<?> comboBox, MainFrame f) {
        this(comboBox, f, 170);
    }

    public CustomComboBoxUI(CustomComboBox<?> comboBox, MainFrame f, int width) {
        this.comboBox = comboBox;
        this.f = f;

        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color iconColor = style.getIconColor();

        comboBox.setForeground(textColor);
        arrowIcon = ImageUtil.dye(arrowIcon, iconColor);
        comboBox.setPreferredSize(new HDDimension(width, 30));
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // 画背景
        Graphics2D g2d = GraphicsUtil.setup(g);
        g2d.setColor(UIStyleStorage.currUIStyle.getTextColor());
        float extendedOpacity = comboBox.getExtendedOpacity(), bgAlpha = comboBox.getBgAlpha();
        GraphicsUtil.srcOver(g2d, extendedOpacity * bgAlpha);
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, arc, arc);
        GraphicsUtil.srcOver(g2d, extendedOpacity);
    }

    @Override
    protected JButton createArrowButton() {
        arrowButton = new CustomButton(arrowIcon);
        arrowButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comboBox.transitionHighlightBg(true);
                comboBox.showPopup();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 将侧边箭头按钮的相对坐标转为 Combobox 整体坐标
                Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), comboBox);
                if (comboBox.contains(p)) return;
                comboBox.transitionHighlightBg(false);
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
                if (popup.contains(e.getPoint())) return;
                comboBox.hidePopup();
            }
        });
        return popup;
    }
}
