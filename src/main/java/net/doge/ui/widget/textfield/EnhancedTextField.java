//package net.doge.ui.widget.textfield;
//
//import lombok.Getter;
//import net.doge.ui.widget.base.ExtendedOpacitySupported;
//import net.doge.ui.widget.button.CustomButton;
//import net.doge.ui.widget.panel.CustomPanel;
//import net.doge.ui.widget.textfield.base.CustomTextField;
//import net.doge.util.lmdata.manager.LMIconManager;
//import net.doge.util.ui.GraphicsUtil;
//import net.doge.util.core.img.ImageUtil;
//import net.doge.util.ui.ScaleUtil;
//import net.doge.util.ui.SwingUtil;
//
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.text.Document;
//import java.awt.*;
//import java.awt.event.FocusListener;
//
//public class EnhancedTextField extends CustomPanel implements ExtendedOpacitySupported {
//    @Getter
//    private float extendedOpacity = 1f;
//
//    private CustomTextField textField;
//    private CustomButton clearButton;
//    private ImageIcon clearIcon = LMIconManager.getIcon("toolbar.clearInput");
//
//    public EnhancedTextField(int length) {
//        textField = new CustomTextField(length);
//        init();
//    }
//
//    // 解决设置文本后不刷新的问题
//    private void init() {
//        // 清除按钮
//        clearButton = new CustomButton(clearIcon);
//        clearButton.addActionListener(e -> {
//            setText("");
//            clearButton.setVisible(false);
//        });
//        // 监听文本变化
//        textField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                if (isOccupied()) clearButton.setVisible(true);
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                if (textField.getText().isEmpty()) clearButton.setVisible(false);
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//
//            }
//        });
//
//        setLayout(new BorderLayout());
//        add(textField, BorderLayout.CENTER);
//        add(clearButton, BorderLayout.EAST);
//    }
//
//    @Override
//    public void setForeground(Color fg) {
//        super.setForeground(fg);
//        if (textField != null) textField.setForeground(fg);
//    }
//
//    public void setIconColor(Color iconColor) {
//        clearButton.setIcon(ImageUtil.dye(clearIcon, iconColor));
//    }
//
//    public boolean isOccupied() {
//        return textField.isOccupied();
//    }
//
//    public void setOccupied(boolean occupied) {
//        textField.setOccupied(occupied);
//    }
//
//    @Override
//    public void requestFocus() {
//        super.requestFocus();
//        textField.requestFocus();
//    }
//
//    public void setText(String t) {
//        textField.setText(t);
//    }
//
//    public String getText() {
//        return textField.getText();
//    }
//
//    public void setCaretColor(Color c) {
//        textField.setCaretColor(c);
//    }
//
//    public void setSelectedTextColor(Color c) {
//        textField.setSelectedTextColor(c);
//    }
//
//    public void setSelectionColor(Color c) {
//        textField.setSelectionColor(c);
//    }
//
//    public void setDocument(Document doc) {
//        textField.setDocument(doc);
//    }
//
//    public Document getDocument() {
//        return textField.getDocument();
//    }
//
//    public void setEditable(boolean editable) {
//        textField.setEditable(editable);
//    }
//
//    @Override
//    public synchronized void addFocusListener(FocusListener l) {
//        textField.addFocusListener(l);
//    }
//
//    @Override
//    public synchronized FocusListener[] getFocusListeners() {
//        return textField.getFocusListeners();
//    }
//
//    @Override
//    public void setExtendedOpacity(float extendedOpacity) {
//        this.extendedOpacity = extendedOpacity;
//        repaint();
//    }
//
//    @Override
//    public void setTreeExtendedOpacity(float extendedOpacity) {
//        SwingUtil.setTreeExtendedOpacity(this, extendedOpacity);
//    }
//
//    @Override
//    public void paintComponent(Graphics g) {
//        Graphics2D g2d = GraphicsUtil.setup(g);
//        int w = getWidth(), h = getHeight();
//
//        // 画背景
//        g2d.setColor(getForeground());
//        GraphicsUtil.srcOver(g2d, extendedOpacity * 0.2f);
//        int arc = ScaleUtil.scale(25);
//        g2d.fillRoundRect(0, 0, w, h, arc, arc);
//        GraphicsUtil.srcOver(g2d, extendedOpacity);
//
//        super.paintComponent(g);
//
//        // 画文字(由于 JTextField 只支持纯文本，对于特殊字符不支持显示，因此以下代码用于画出特殊字符，此方法无法准确推测特殊字符的位置，弃用)
/// /        String text = getText();
/// /        FontMetrics fontMetrics = getFontMetrics(getFont());
/// /        int stringHeight = fontMetrics.getHeight();
/// /        g2d.setColor(foreColor);
/// /
/// /        FontMetrics[] metrics = new FontMetrics[net.doge.constant.core.ui.core.Fonts.TYPES.size()];
/// /        for (int i = 0, len = metrics.length; i < len; i++) {
/// /            metrics[i] = getFontMetrics(Fonts.TYPES.get(i));
/// /        }
/// /
/// /        // 计算宽度
/// /        int stringWidth = 0;
/// /        for (int i = 0, len = text.length(); i < len; i++) {
/// /            int codePoint = text.codePointAt(i);
/// /            char[] chars = Character.toChars(codePoint);
/// /            String str = new String(chars);
/// /            for (int j = 0, l = metrics.length; j < l; j++) {
/// /                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
/// /                stringWidth += metrics[j].stringWidth(str);
/// /                i += chars.length - 1;
/// /                break;
/// /            }
/// /        }
/// /
/// /        int widthDrawn = 0;
/// /        for (int i = 0, len = text.length(); i < len; i++) {
/// /            int codePoint = text.codePointAt(i);
/// /            char[] chars = Character.toChars(codePoint);
/// /            String str = new String(chars);
/// /            for (int j = 0, l = metrics.length; j < l; j++) {
/// /                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
/// /                // 只画普通字体显示不出来的文字
/// /                if (j != 0) {
/// /                    g2d.setFont(Fonts.TYPES.get(j));
/// /                    g2d.drawString(str, (w - stringWidth) / 2 + widthDrawn, (h - stringHeight) / 2 + 16);
/// /                }
/// /                widthDrawn += metrics[j].stringWidth(str);
/// /                i += chars.length - 1;
/// /                break;
/// /            }
/// /        }
//    }
//}
