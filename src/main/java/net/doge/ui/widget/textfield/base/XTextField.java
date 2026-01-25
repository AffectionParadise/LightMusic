//package net.doge.ui.widget.textfield.base;
//
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.text.*;
//import java.awt.*;
//import java.awt.event.*;
//
//public class XTextField extends JTextPane {
//    private int columns = 20;
//    private boolean horizontalScrolling = false;
//    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
//    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
//
//    // 对齐方式枚举
//    public enum VerticalAlignment {
//        TOP, CENTER, BOTTOM
//    }
//
//    public enum HorizontalAlignment {
//        LEFT, CENTER, RIGHT
//    }
//
//    public XTextField(int length) {
//        setColumns(length);
//        init();
//    }
//
//    private void init() {
//        // 设置单行行为
//        setSingleLineBehavior();
//
//        // 设置外观和大小
//        updateAppearance();
//        updatePreferredSize();
//
//        // 添加监听器
//        addListeners();
//    }
//
//    // 设置列数
//    public void setColumns(int columns) {
//        if (columns < 1) columns = 1;
//        this.columns = columns;
//        updatePreferredSize();
//        repaint();
//    }
//
//    public int getColumns() {
//        return columns;
//    }
//
//    // 设置垂直对齐方式
//    public void setVerticalAlignment(VerticalAlignment alignment) {
//        this.verticalAlignment = alignment;
//        updateVerticalAlignment();
//        repaint();
//    }
//
//    public VerticalAlignment getVerticalAlignment() {
//        return verticalAlignment;
//    }
//
//    // 设置水平对齐方式
//    public void setHorizontalAlignment(HorizontalAlignment alignment) {
//        this.horizontalAlignment = alignment;
//        updateHorizontalAlignment();
//        repaint();
//    }
//
//    public HorizontalAlignment getHorizontalAlignment() {
//        return horizontalAlignment;
//    }
//
//    // 设置水平滚动
//    public void setHorizontalScrolling(boolean enable) {
//        this.horizontalScrolling = enable;
//        updateHorizontalScrollPolicy();
//    }
//
//    public boolean isHorizontalScrolling() {
//        return horizontalScrolling;
//    }
//
//    // 重写setText方法，确保单行
//    @Override
//    public void setText(String text) {
//        if (text != null) {
//            text = text.replaceAll("[\n\r]", "");
//        }
//        super.setText(text);
//        updateVerticalAlignment();
//        updateHorizontalAlignment();
//    }
//
//    // 更新外观
//    private void updateAppearance() {
//        // 使用TextField的外观
//        setBorder(UIManager.getBorder("TextField.border"));
//        setBackground(UIManager.getColor("TextField.background"));
//        setForeground(UIManager.getColor("TextField.foreground"));
//        setSelectionColor(UIManager.getColor("TextField.selectionBackground"));
//        setSelectedTextColor(UIManager.getColor("TextField.selectionForeground"));
//        setCaretColor(UIManager.getColor("TextField.caretForeground"));
//
//        // 设置字体
//        Font defaultFont = UIManager.getFont("TextField.font");
//        if (defaultFont != null) {
//            setFont(defaultFont);
//        }
//
//        // 使用自定义EditorKit
//        setEditorKit(new SingleLineEditorKit());
//    }
//
//    // 更新首选大小
//    private void updatePreferredSize() {
//        FontMetrics fm = getFontMetrics(getFont());
//        int charWidth = fm.charWidth('m');
//        int charHeight = fm.getHeight();
//
//        int width = columns * charWidth + getInsets().left + getInsets().right + 8;
//        int height = charHeight + getInsets().top + getInsets().bottom + 6;
//
//        setPreferredSize(new Dimension(width, height));
//        setMinimumSize(new Dimension(charWidth * 5, height));
//    }
//
//    // 更新垂直对齐方式
//    private void updateVerticalAlignment() {
//        // 计算垂直偏移量
//        int height = getHeight();
//        if (height == 0) return;
//
//        FontMetrics fm = getFontMetrics(getFont());
//        int fontHeight = fm.getHeight();
//
//        // 获取文本行数
//        int lineCount = getLineCount();
//        int textHeight = lineCount * fontHeight;
//
//        // 计算垂直位置
//        int y = 0;
//        switch (verticalAlignment) {
//            case TOP:
//                y = getInsets().top + 2;
//                break;
//            case CENTER:
//                y = (height - textHeight) / 2 + fm.getAscent() - 2;
//                break;
//            case BOTTOM:
//                y = height - getInsets().bottom - (lineCount * fontHeight) + fm.getAscent() - 2;
//                break;
//        }
//
//        // 设置段落样式（通过设置上边距实现）
//        try {
//            StyledDocument doc = getStyledDocument();
//            Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
//            if (style == null) {
//                style = doc.addStyle(StyleContext.DEFAULT_STYLE, null);
//            }
//
//            // 计算上边距
//            int topMargin = Math.max(0, y - fm.getAscent() - getInsets().top);
//            StyleConstants.setSpaceAbove(style, topMargin);
//
//            // 应用样式到整个文档
//            doc.setParagraphAttributes(0, doc.getLength(), style, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 更新水平对齐方式
//    private void updateHorizontalAlignment() {
//        try {
//            StyledDocument doc = getStyledDocument();
//            Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
//            if (style == null) {
//                style = doc.addStyle(StyleContext.DEFAULT_STYLE, null);
//            }
//
//            // 设置水平对齐
//            switch (horizontalAlignment) {
//                case LEFT:
//                    StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
//                    break;
//                case CENTER:
//                    StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
//                    break;
//                case RIGHT:
//                    StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
//                    break;
//            }
//
//            // 应用样式到整个文档
//            doc.setParagraphAttributes(0, doc.getLength(), style, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 获取文本行数
//    private int getLineCount() {
//        String text = getText();
//        if (text == null || text.isEmpty()) return 1;
//        return 1; // 单行始终为1
//    }
//
//    // 更新水平滚动策略
//    private void updateHorizontalScrollPolicy() {
/// /        if (horizontalScrolling) {
/// /            setAutoscrolls(true);
/// /        } else {
/// /            setAutoscrolls(false);
/// /        }
//    }
//
//    // 设置单行行为
//    private void setSingleLineBehavior() {
//        // 禁用自动换行
//        putClientProperty(JEditorPane.W3C_LENGTH_UNITS, Boolean.FALSE);
//        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
//
//        // 横向滚动
//        setAutoscrolls(true);
//
//        // 添加按键监听器，阻止换行
//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    e.consume();
//                    fireActionPerformed();
//                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
//                    if (e.isShiftDown()) {
//                        transferFocusBackward();
//                    } else {
//                        transferFocus();
//                    }
//                    e.consume();
//                }
//            }
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//                char c = e.getKeyChar();
//                if (c == '\n' || c == '\r') {
//                    e.consume();
//                }
//            }
//        });
//
//        // 添加文档监听器，移除换行符
//        getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    removeNewlines();
//                });
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                // Nothing to do
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                // Nothing to do
//            }
//        });
//    }
//
//    // 移除换行符
//    private void removeNewlines() {
//        try {
//            String text = getText();
//            if (text.contains("\n") || text.contains("\r")) {
//                String cleaned = text.replaceAll("[\n\r]", "");
//                setText(cleaned);
//            }
//        } catch (Exception e) {
//            // Ignore
//        }
//    }
//
//    // 添加监听器
//    private void addListeners() {
//        // 监听组件大小变化
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                updateVerticalAlignment();
//            }
//        });
//
//        // 监听字体变化
//        addPropertyChangeListener("font", evt -> {
//            updatePreferredSize();
//            updateVerticalAlignment();
//            revalidate();
//        });
//    }
//
//    // 触发Action事件
//    private void fireActionPerformed() {
//        ActionListener[] listeners = getListeners(ActionListener.class);
//        if (listeners.length > 0) {
//            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText());
//            for (ActionListener listener : listeners) {
//                listener.actionPerformed(event);
//            }
//        }
//    }
//
//    // 添加ActionListener
//    public void addActionListener(ActionListener l) {
//        listenerList.add(ActionListener.class, l);
//    }
//
//    public void removeActionListener(ActionListener l) {
//        listenerList.remove(ActionListener.class, l);
//    }
//
//    // 自定义EditorKit
//    private class SingleLineEditorKit extends StyledEditorKit {
//        @Override
//        public ViewFactory getViewFactory() {
//            return new SingleLineViewFactory();
//        }
//    }
//
//    // 自定义ViewFactory
//    private class SingleLineViewFactory extends StyledViewFactory {
//        @Override
//        public View create(Element elem) {
//            String kind = elem.getName();
//
//            if (kind != null) {
//                if (kind.equals(AbstractDocument.ContentElementName)) {
//                    return new SingleLineLabelView(elem);
//                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
//                    return new SingleLineParagraphView(elem);
//                } else if (kind.equals(AbstractDocument.SectionElementName)) {
//                    return new BoxView(elem, View.Y_AXIS) {
//                        @Override
//                        public float getMinimumSpan(int axis) {
//                            if (axis == View.Y_AXIS) {
//                                return super.getPreferredSpan(axis);
//                            }
//                            return super.getMinimumSpan(axis);
//                        }
//                    };
//                }
//            }
//
//            return super.create(elem);
//        }
//    }
//
//    // 自定义StyledViewFactory
//    private class StyledViewFactory implements ViewFactory {
//        @Override
//        public View create(Element elem) {
//            String kind = elem.getName();
//
//            if (kind != null) {
//                if (kind.equals(AbstractDocument.ContentElementName)) {
//                    return new LabelView(elem);
//                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
//                    return new ParagraphView(elem);
//                } else if (kind.equals(AbstractDocument.SectionElementName)) {
//                    return new BoxView(elem, View.Y_AXIS);
//                } else if (kind.equals(StyleConstants.ComponentElementName)) {
//                    return new ComponentView(elem);
//                } else if (kind.equals(StyleConstants.IconElementName)) {
//                    return new IconView(elem);
//                }
//            }
//
//            return new LabelView(elem);
//        }
//    }
//
//    // 单行标签视图
//    private class SingleLineLabelView extends LabelView {
//        public SingleLineLabelView(Element elem) {
//            super(elem);
//        }
//
//        @Override
//        public float getMinimumSpan(int axis) {
//            if (axis == View.X_AXIS) {
//                return 0;
//            }
//            return super.getMinimumSpan(axis);
//        }
//    }
//
//    // 单行段落视图
//    private class SingleLineParagraphView extends ParagraphView {
//        public SingleLineParagraphView(Element elem) {
//            super(elem);
//        }
//
//        @Override
//        public void layout(int width, int height) {
//            super.layout(width, height);
//        }
//
//        @Override
//        public float getMinimumSpan(int axis) {
//            if (axis == View.Y_AXIS) {
//                return super.getPreferredSpan(axis);
//            }
//            return super.getMinimumSpan(axis);
//        }
//
//        @Override
//        public float getPreferredSpan(int axis) {
//            if (axis == View.Y_AXIS) {
//                // 限制垂直方向高度
//                FontMetrics fm = getContainer().getFontMetrics(getContainer().getFont());
//                return fm.getHeight() + 4;
//            }
//            return super.getPreferredSpan(axis);
//        }
//    }
//}
