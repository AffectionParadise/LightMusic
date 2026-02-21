package net.doge.ui.widget.textfield;

import lombok.Getter;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.textfield.listener.TextFieldHintListener;
import net.doge.util.core.StringUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomTextField extends JTextField implements ExtendedOpacitySupported {
    private static final HDEmptyBorder BORDER = new HDEmptyBorder(5, 10, 5, 10);

    // 是否在占位状态
    @Getter
    private boolean hintHolding;
    private TextFieldHintListener hintListener;
    @Getter
    private float extendedOpacity = 1f;

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
        setBorder(BORDER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                setCaretVisible(true);
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                setFocusable(false);
                setCaretVisible(false);
                repaint();
            }
        });
    }

    public void updateStyle() {
        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        if (hintHolding) {
            Color darkerTextColor = ColorUtil.darker(textColor);
            setForeground(darkerTextColor);
        } else setForeground(textColor);
    }

    // 需要焦点时，先设置可聚焦的
    @Override
    public void requestFocus() {
        setFocusable(true);
        super.requestFocus();
    }

    // 设置占位文本，如果不为空，默认开启占位监听器
    public void setHintText(String hintText) {
        removeFocusListener(hintListener);
        if (StringUtil.notEmpty(hintText)) {
            addFocusListener(hintListener = new TextFieldHintListener(this, hintText));
            hintListener.toHintHoldingStatus();
        } else hintListener = null;
    }

    public void setHintHolding(boolean hintHolding) {
        this.hintHolding = hintHolding;
        updateStyle();
    }

    @Override
    public void setText(String t) {
        setText(t, true);
    }

    // 设置文本后决定是否中断占位属性
    public void setText(String t, boolean breakHint) {
        super.setText(t);
        if (breakHint && hintHolding) setHintHolding(false);
        // 解决设置文本后不刷新的问题
        if (hintListener != null) return;
        revalidate();
    }

    // 是否为空文本
    public boolean isTextEmpty() {
        return StringUtil.isEmpty(getText());
    }

    public void setCaretVisible(boolean visible) {
        getCaret().setVisible(visible);
    }

    // 设置文本筛选器
    public void setDocumentFilter(DocumentFilter documentFilter) {
        ((AbstractDocument) getDocument()).setDocumentFilter(documentFilter);
    }

    public void addDocumentListener(DocumentListener documentListener) {
        getDocument().addDocumentListener(documentListener);
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
    public void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        int w = getWidth(), h = getHeight();

        // 画背景
        g2d.setColor(getForeground());
        GraphicsUtil.srcOver(g2d, extendedOpacity * 0.2f);
        int arc = ScaleUtil.scale(10);
        g2d.fillRoundRect(0, 0, w, h, arc, arc);
        GraphicsUtil.srcOver(g2d, extendedOpacity);

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
}
