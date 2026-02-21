package net.doge.ui.widget.textfield.filter;

import net.doge.util.core.StringUtil;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 * 文本筛选器
 */
public class LimitedDocumentFilter extends DocumentFilter {
    // 数值范围限制
    private int min;
    private int max;
    private boolean numberRequired;
    private boolean ranged;
    // 长度限制
    private int lengthLimit;
    private boolean lengthLimited;

    public LimitedDocumentFilter(int lengthLimit) {
        this.lengthLimit = lengthLimit;
        lengthLimited = true;
    }

    public LimitedDocumentFilter(int min, int max) {
        this(Math.max(String.valueOf(min).length(), String.valueOf(max).length()));
        this.min = min;
        this.max = max;
        numberRequired = ranged = true;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (StringUtil.isEmpty(string)) return;
        Document doc = fb.getDocument();
        String text = doc.getText(0, doc.getLength());
        String ns = StringUtil.insert(text, offset, string);
        if (!isValidText(ns)) return;
        super.insertString(fb, offset, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (StringUtil.isEmpty(text)) return;
        Document doc = fb.getDocument();
        String ot = doc.getText(0, doc.getLength());
        String ns = StringUtil.replace(ot, offset, length, text);
        if (!isValidText(ns)) return;
        super.replace(fb, offset, length, text, attrs);
    }

    // 验证新文本
    private boolean isValidText(String text) {
        // 数字
        if (numberRequired && !StringUtil.isNumber(text)) return false;
        // 数值范围
        if (ranged) {
            int number = StringUtil.toNumber(text);
            if (min > number || max < number) return false;
        }
        // 长度
        if (lengthLimited && text.length() > lengthLimit) return false;
        return true;
    }
}
