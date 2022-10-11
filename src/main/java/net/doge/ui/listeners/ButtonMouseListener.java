package net.doge.ui.listeners;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomButton;
import net.doge.utils.ColorUtils;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author yzx
 * @Description 改变按钮样式的监听器
 * @Date 2021/1/10
 */
public class ButtonMouseListener extends MouseAdapter {
    private JButton b;
    private PlayerFrame f;

    public ButtonMouseListener(JButton b, PlayerFrame f) {
        this.b = b;
        this.f = f;

        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        Color brighterColor = ColorUtils.brighter(buttonColor);
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), brighterColor));
        b.setForeground(brighterColor);

//        // 画出提示文字
//        String toolTipText = b.getToolTipText();
//        if (toolTipText != null) {
//            FontMetrics metrics = f.getFontMetrics(Fonts.NORMAL_TINY);
//            int sw = metrics.stringWidth(toolTipText), sh = metrics.getHeight();
//            Rectangle r = b.getVisibleRect();
//            Point p = SwingUtilities.convertPoint(b, r.x, r.y, f);
//            Graphics2D g2d = (Graphics2D) f.getGraphics();
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setColor(Colors.DODGER_BLUE_4);
//            int x = p.x + (r.width - (sw + 20)) / 2;
//            int y = p.y + r.height + sh + 11 >= f.getHeight() ? p.y - 25 : p.y + r.height + 5;
//            g2d.fillRoundRect(x, y, sw + 20, sh + 6, 5, 5);
//            g2d.setColor(Colors.WHITE);
//            g2d.setFont(Fonts.NORMAL_TINY);
//            g2d.drawString(toolTipText, x + 10, y + 16);
//        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), buttonColor));
        b.setForeground(buttonColor);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Color buttonColor = f.getCurrUIStyle().getButtonColor();
            Color darkerColor = ColorUtils.darker(buttonColor);
            if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), darkerColor));
            b.setForeground(darkerColor);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Color buttonColor = f.getCurrUIStyle().getButtonColor();
        Color brighterColor = ColorUtils.brighter(buttonColor);
        boolean c = b.getVisibleRect().contains(e.getPoint());
        if (b.getIcon() != null) b.setIcon(ImageUtils.dye((ImageIcon) b.getIcon(), brighterColor));
        b.setForeground(c ? brighterColor : buttonColor);
    }
}
