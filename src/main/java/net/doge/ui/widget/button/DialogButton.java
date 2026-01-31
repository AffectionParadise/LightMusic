package net.doge.ui.widget.button;

import net.doge.constant.core.ui.core.Colors;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.tooltip.CustomToolTip;
import net.doge.util.core.HtmlUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @Author Doge
 * @Description 对话框中的按钮自定义 UI
 * @Date 2020/12/13
 */
public class DialogButton extends BaseButton implements MouseListener {
    private Color foreColor;
    private Color foreColorBk;
    private Timer drawBgTimer;
    private boolean entered;
    private final float startAlpha = 0.2f;
    private final float destAlpha = 0.4f;
    private float alpha = startAlpha;
    private static final Border BORDER = new HDEmptyBorder(4, 16, 4, 16);

    public DialogButton() {
        this(null);
    }

    public DialogButton(String text) {
        this(text, Colors.WHITE);
    }

    public DialogButton(String text, Color foreColor) {
        this(text, foreColor, false);
    }

    // 关键词按钮，需显示多种字符
    public DialogButton(String text, boolean htmlMode) {
        this(text, Colors.WHITE, htmlMode);
    }

    // 常规按钮
    public DialogButton(String text, Color foreColor, boolean htmlMode) {
        super(htmlMode ? HtmlUtil.textToHtml(text) : text);
        setForeColor(foreColor);
        init();
    }

    private void init() {
        addMouseListener(this);
        setBorder(BORDER);

        drawBgTimer = new Timer(2, e -> {
            if (entered) alpha = Math.min(destAlpha, alpha + 0.005f);
            else alpha = Math.max(startAlpha, alpha - 0.005f);
            if (alpha <= startAlpha || alpha >= destAlpha) drawBgTimer.stop();
            repaint();
        });
    }

    public void setForeColor(Color foreColor) {
        setForeground(foreColor);
        this.foreColor = foreColor;
        this.foreColorBk = foreColor;
        repaint();
    }

    public String getPlainText() {
        return HtmlUtil.removeHtmlLabel(getText());
    }

    @Override
    public JToolTip createToolTip() {
        CustomToolTip tooltip = new CustomToolTip(this);
        tooltip.setVisible(false);
        return tooltip;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtil.setup(g);
        // 画背景
        g2d.setColor(foreColor);
        GraphicsUtil.srcOver(g2d, alpha);
        int arc = ScaleUtil.scale(8);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        GraphicsUtil.srcOver(g2d);

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

    public void setEntered(boolean entered) {
        if (this.entered == entered) return;
        this.entered = entered;
        if (drawBgTimer.isRunning()) return;
        drawBgTimer.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        foreColor = ColorUtil.darker(foreColor);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        foreColor = foreColorBk;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setEntered(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setEntered(false);
    }
}
