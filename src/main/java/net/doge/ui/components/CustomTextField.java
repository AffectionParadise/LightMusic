package net.doge.ui.components;

import lombok.Data;
import net.doge.constants.Fonts;
import net.doge.ui.listeners.JTextFieldHintListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @Author yzx
 * @Description 列表元素标签自定义 UI
 * @Date 2020/12/13
 */
@Data
public class CustomTextField extends JTextField {
    private Color backgroundColor;
    private Color foregroundColor;

    private boolean drawBg;

    public CustomTextField(int length) {
        super(length);
        setHorizontalAlignment(CENTER);
        setMaximumSize(new Dimension(3000, 30));
        drawBg = true;
        init();
    }

    public CustomTextField(int length, boolean drawBg) {
        super(length);
        this.drawBg = drawBg;
        init();
    }

    // 解决设置文本后不刷新的问题
    void init() {
        setFocusable(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setFocusable(true);
                requestFocus();
                getCaret().setVisible(true);
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setFocusable(false);
                getCaret().setVisible(false);
            }
        });
    }

    // 判断是否需要刷新
    private boolean needRefresh() {
        FocusListener[] fls = getFocusListeners();
        for (FocusListener fl : fls) {
            if (fl instanceof JTextFieldHintListener) return false;
        }
        return true;
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        // 解决设置文本后不刷新的问题
        if(!needRefresh()) return;
        setVisible(false);
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        Rectangle rect = getVisibleRect();
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 画背景
        if (drawBg) {
            g2d.setColor(foregroundColor);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 25, 25);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // 画文字
        String text = getText();
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int stringHeight = fontMetrics.getHeight();
        g2d.setColor(foregroundColor);

        FontMetrics[] metrics = new FontMetrics[Fonts.TYPES.length];
        for (int i = 0, len = metrics.length; i < len; i++) {
            metrics[i] = getFontMetrics(Fonts.TYPES[i]);
        }

        // 计算宽度
        int stringWidth = 0;
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            for (int j = 0, l = metrics.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(ch)) {
                    stringWidth += metrics[j].stringWidth(ch + "");
                    break;
                }
            }
        }

        int widthDrawn = 0;
        for (int i = 0, len = text.length(); i < len; i++) {
            char ch = text.charAt(i);
            for (int j = 0, l = metrics.length; j < l; j++) {
                if (Fonts.TYPES[j].canDisplay(ch)) {
                    // 只画普通字体显示不出来的文字
                    if (j != 0) {
                        g2d.setFont(Fonts.TYPES[j]);
                        g2d.drawString(ch + "", (rect.width - stringWidth) / 2 + widthDrawn, (rect.height - stringHeight) / 2 + 16);
                    }
                    widthDrawn += metrics[j].stringWidth(ch + "");
                    break;
                }
            }
        }
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
