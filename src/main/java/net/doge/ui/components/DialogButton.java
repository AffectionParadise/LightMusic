package net.doge.ui.components;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @Author yzx
 * @Description 对话框中的按钮自定义 UI
 * @Date 2020/12/13
 */
public class DialogButton extends CustomButton implements MouseListener {
    private Color foreColor;
    private Color foreColorBk;
    private float alpha = 0.2f;

    public DialogButton() {
        foreColor = Colors.WHITE;
        addMouseListener(this);
        setOpaque(false);
    }

    public DialogButton(String text) {
        super(text);
        foreColor = Colors.WHITE;
        addMouseListener(this);
        setOpaque(false);
    }

    public DialogButton(String text, Color foreColor) {
        super(text);
        this.foreColor = foreColor;
        addMouseListener(this);
        setOpaque(false);
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Rectangle rect = getVisibleRect();
        Graphics2D g2d = (Graphics2D) g;
        // 画背景
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(foreColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);

        // 画文字
        String text = getText();
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int stringHeight = fontMetrics.getHeight();
        g2d.setColor(foreColor);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

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
                    g2d.setFont(Fonts.TYPES[j]);
                    g2d.drawString(ch + "", (rect.width - stringWidth) / 2 + widthDrawn, (rect.height - stringHeight) / 2 + 16);
                    widthDrawn += metrics[j].stringWidth(ch + "");
                    break;
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        foreColorBk = foreColor;
        foreColor = foreColor.darker();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        foreColor = foreColorBk;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alpha = 0.4f;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        alpha = 0.2f;
    }
}
