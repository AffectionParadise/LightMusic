package net.doge.ui.components;

import net.doge.utils.StringUtils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class SafeDocument extends PlainDocument {
    // 数值范围限制
    private int min;
    private int max;
    private boolean numberRequired;
    private boolean ranged;

    // 长度限制
    private int lengthLimit;
    private boolean lengthLimited;

    public SafeDocument(int lengthLimit) {
        this.lengthLimit = lengthLimit;
        lengthLimited = true;
    }

    public SafeDocument(int min, int max) {
        this(StringUtils.bit(max));
        this.min = min;
        this.max = max;
        numberRequired = ranged = true;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (StringUtils.isEmpty(str)) return;

        String text = getText(0, getLength());
        String ns = StringUtils.insert(text, offs, str);

        // 数字
        if (numberRequired && !StringUtils.isNumber(ns)) return;
        // 数值范围
        if (ranged) {
            int number = StringUtils.toNumber(ns);
            if (min > number || max < number) return;
        }
        // 长度
        if (lengthLimited && ns.length() > lengthLimit) return;

        super.insertString(offs, str, a);
    }
}
