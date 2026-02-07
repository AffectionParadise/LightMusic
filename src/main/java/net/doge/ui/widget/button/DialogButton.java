package net.doge.ui.widget.button;

import lombok.Getter;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.listener.DialogButtonMouseAdapter;
import net.doge.util.core.HtmlUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description 对话框中的按钮自定义 UI
 * @Date 2020/12/13
 */
public class DialogButton extends BaseButton implements ExtendedOpacitySupported {
    private static final HDEmptyBorder BORDER = new HDEmptyBorder(4, 15, 4, 15);

    private boolean highlightBgIncreasing;
    private Timer highlightBgTimer;
    private final float startBgAlpha = 0.2f;
    private final float destBgAlpha = 0.4f;
    private float bgAlpha = startBgAlpha;
    @Getter
    private float extendedOpacity = 1f;

    public DialogButton() {
        this(null, null);
    }

    public DialogButton(String text, Color foreground) {
        this(text, foreground, false);
    }

    // 常规按钮
    public DialogButton(String text, Color foreground, boolean htmlMode) {
        super(htmlMode ? HtmlUtil.textToHtml(text) : text);
        setForeground(foreground);
        init();
    }

    private void init() {
        addMouseListener(new DialogButtonMouseAdapter(this));
        setBorder(BORDER);

        highlightBgTimer = new Timer(2, e -> {
            if (highlightBgIncreasing) bgAlpha = Math.min(destBgAlpha, bgAlpha + 0.005f);
            else bgAlpha = Math.max(startBgAlpha, bgAlpha - 0.005f);
            if (bgAlpha <= startBgAlpha || bgAlpha >= destBgAlpha) highlightBgTimer.stop();
            repaint();
        });
    }

    public void transitionHighlightBg(boolean drawBgIncreasing) {
        this.highlightBgIncreasing = drawBgIncreasing;
        if (highlightBgTimer.isRunning()) return;
        highlightBgTimer.start();
    }

    public void setHighlightBg(boolean highlightBg) {
        bgAlpha = highlightBg ? destBgAlpha : startBgAlpha;
    }

    public String getPlainText() {
        return HtmlUtil.removeHtmlLabel(getText());
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
        // 画背景
        g2d.setColor(getForeground());
        GraphicsUtil.srcOver(g2d, extendedOpacity * bgAlpha);
        int arc = ScaleUtil.scale(8);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        GraphicsUtil.srcOver(g2d, extendedOpacity);

        super.paintComponent(g);

//        // 画文字
//        String text = getText();
//        FontMetrics fontMetrics = getFontMetrics(getFont());
//        int stringHeight = fontMetrics.getHeight();
//        g2d.setColor(foreColor);
//        GraphicsUtil.srcOver(g2d);
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
//                    stringWidth += metrics[j].stringWidth(str);
//                    i += chars.length - 1;
//                    break;
//            }
//        }
//
//        int widthDrawn = 0, width = getWidth(), height = getHeight();
//        for (int i = 0, len = text.length(); i < len; i++) {
//            int codePoint = text.codePointAt(i);
//            char[] chars = Character.toChars(codePoint);
//            String str = new String(chars);
//            for (int j = 0, l = metrics.length; j < l; j++) {
//                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
//                    // 只画显示不出的文字
//                    if (j == 0) continue;
//                    g2d.setFont(Fonts.TYPES.get(j));
//                    g2d.drawString(str, (width - stringWidth) / 2 + widthDrawn, (height - stringHeight) / 2 + 16);
//                    widthDrawn += metrics[j].stringWidth(str);
//                    i += chars.length - 1;
//                    break;
//            }
//        }
    }
}
