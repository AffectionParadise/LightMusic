package net.doge.ui.widget.combobox.popup;

import lombok.Getter;
import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.scrollbar.ui.CustomScrollBarUI;
import net.doge.util.ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class CustomComboPopup extends BasicComboPopup implements ExtendedOpacitySupported {
    @Getter
    private float extendedOpacity = 1f;

    private Color scrollBarColor;
    private MainFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = Math.min(100, ScaleUtil.scale(30));
    // 阴影大小像素
    private final int pixels = ScaleUtil.scale(10);

    public CustomComboPopup(CustomComboBox comboBox, MainFrame f) {
        super(comboBox);

        this.f = f;
        this.scrollBarColor = f.currUIStyle.getScrollBarColor();

        setLightWeightPopupEnabled(false);

        // 阴影边框
        setBorder(new EmptyBorder(pixels, pixels, pixels, pixels));
    }

    @Override
    protected JList createList() {
        JList list = super.createList();
        list.setOpaque(false);
        return list;
    }

    @Override
    protected JScrollPane createScroller() {
        CustomScrollPane sp = new CustomScrollPane(list);
        sp.setHBarUI(new CustomScrollBarUI(scrollBarColor));
        sp.setVBarUI(new CustomScrollBarUI(scrollBarColor));
        return sp;
    }

    @Override
    public void setExtendedOpacity(float extendedOpacity) {
        this.extendedOpacity = extendedOpacity;
        repaint();
    }

    @Override
    public void setTreeExtendedOpacity(float extendedOpacity) {
        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);

        g2d.setColor(ImageUtil.getAvgColor(f.globalPanel.getBgImg()));
        int arc = ScaleUtil.scale(8);
        g2d.fillRoundRect(pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, arc, arc);

        // 画边框阴影
        int step = TOP_OPACITY / pixels;
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(ColorUtil.deriveAlpha(Colors.BLACK, step * i));
            g2d.drawRoundRect(i, i, getWidth() - (i * 2 + 1), getHeight() - (i * 2 + 1), arc, arc);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
//        super.paintBorder(g);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // 使 JPopupMenu 对应的 Window 透明！
            Window w = SwingUtilities.getWindowAncestor(this);
            w.setVisible(false);
            w.setBackground(Colors.BLACK);
            w.setBackground(Colors.TRANSPARENT);
            w.setVisible(true);
        }
        f.currPopup = b ? this : null;
    }
}
