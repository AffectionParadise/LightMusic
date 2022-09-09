package net.doge.ui.componentui;

import lombok.Data;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 多字体标签 UI
 * @Date 2021/1/3
 */
@Data
public class MultiFontLabelUI extends BasicLabelUI {
    // 中文/英文/日文字体
    private Font cnFont;
    // 韩文字体
    private Font knFont;

    public MultiFontLabelUI(Font cnFont, Font knFont) {
        this.cnFont = cnFont;
        this.knFont = knFont;
    }

    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        textY += 1;
        String str;
        int w;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (isKnChar(s.charAt(i))) g.setFont(knFont);
            else g.setFont(cnFont);
            str = s.charAt(i) + "";
            super.paintEnabledText(l, g, str, textX, textY);
            w = g.getFontMetrics().stringWidth(str);
            textX += w;
        }
    }

    @Override
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
//        super.paintEnabledText(l, g, s, textX, textY);
        textY += 6;
        String str;
        int w;
        for (int i = 0, len = s.length(); i < len; i++) {
            if (isKnChar(s.charAt(i))) g.setFont(knFont);
            else g.setFont(cnFont);
            str = s.charAt(i) + "";
            super.paintDisabledText(l, g, str, textX, textY);
            w = g.getFontMetrics().stringWidth(str);
            textX += w;
        }
    }

    // 判断是否为韩文字符
    private static boolean isKnChar(int u) {
        return u >= 0xAC00 && u <= 0xD7AF
                || u >= 0x1100 && u <= 0x11FF || u >= 0x3130 && u <= 0x318F;
    }
}
