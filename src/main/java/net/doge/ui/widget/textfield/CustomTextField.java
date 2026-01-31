package net.doge.ui.widget.textfield;

import lombok.Data;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.core.inset.HDInsets;
import net.doge.ui.widget.textfield.listener.TextFieldHintListener;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@Data
public class CustomTextField extends JTextField {
    private boolean occupied;

    public CustomTextField(int length) {
        super(length);
        init();
    }

    // 解决设置文本后不刷新的问题
    private void init() {
        setOpaque(false);
        setBackground(Colors.TRANSPARENT);
        setFocusable(false);
        setHorizontalAlignment(CENTER);
        setFont(Fonts.NORMAL);
        setMaximumSize(new HDDimension(3000, 30));
        setMargin(new HDInsets(4, 6, 4, 6));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                getCaret().setVisible(true);
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setFocusable(false);
                getCaret().setVisible(false);
                repaint();
            }
        });
    }

    // 需要焦点时，先设置可聚焦的
    @Override
    public void requestFocus() {
        setFocusable(true);
        setOccupied(true);
        super.requestFocus();
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    // 判断是否需要刷新
    private boolean needRefresh() {
        FocusListener[] fls = getFocusListeners();
        for (FocusListener fl : fls) {
            if (fl instanceof TextFieldHintListener) return false;
        }
        return true;
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        // 解决设置文本后不刷新的问题
        if (!needRefresh()) return;
        revalidate();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        int w = getWidth(), h = getHeight();

        // 画背景
        g2d.setColor(getForeground());
        GraphicsUtil.srcOver(g2d, 0.2f);
        int arc = ScaleUtil.scale(25);
        g2d.fillRoundRect(0, 0, w, h, arc, arc);
        GraphicsUtil.srcOver(g2d);

        super.paintComponent(g);

        // 画文字(由于 JTextField 只支持纯文本，对于特殊字符不支持显示，因此以下代码用于画出特殊字符，此方法无法准确推测特殊字符的位置，弃用)
//        String text = getText();
//        FontMetrics fontMetrics = getFontMetrics(getFont());
//        int stringHeight = fontMetrics.getHeight();
//        g2d.setColor(foreColor);
//
//        FontMetrics[] metrics = new FontMetrics[Fonts.TYPES.size()];
//        for (int i = 0, len = metrics.length; i < len; i++) {
//            metrics[i] = getFontMetrics(Fonts.TYPES.get(i));
//        }
//
//        // 计算宽度
//        int stringWidth = 0;
//        for (int i = 0, len = text.length(); i < len; i++) {
//            int codePoint = text.codePointAt(i);
//            char[] chars = Character.toChars(codePoint);
//            String str = new String(chars);
//            for (int j = 0, l = metrics.length; j < l; j++) {
//                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
//                stringWidth += metrics[j].stringWidth(str);
//                i += chars.length - 1;
//                break;
//            }
//        }
//
//        int widthDrawn = 0;
//        for (int i = 0, len = text.length(); i < len; i++) {
//            int codePoint = text.codePointAt(i);
//            char[] chars = Character.toChars(codePoint);
//            String str = new String(chars);
//            for (int j = 0, l = metrics.length; j < l; j++) {
//                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
//                // 只画普通字体显示不出来的文字
//                if (j != 0) {
//                    g2d.setFont(Fonts.TYPES.get(j));
//                    g2d.drawString(str, (w - stringWidth) / 2 + widthDrawn, (h - stringHeight) / 2 + 16);
//                }
//                widthDrawn += metrics[j].stringWidth(str);
//                i += chars.length - 1;
//                break;
//            }
//        }
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
