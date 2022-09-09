package net.doge.ui.components;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;

import javax.swing.*;
import java.awt.*;

public class CustomToolTip extends JToolTip {

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle rect = getVisibleRect();
        Graphics2D g2d = (Graphics2D) g;
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Colors.DODGER_BLUE_4);
        g2d.fillRoundRect(rect.x, rect.y - 20, rect.width, rect.height, 10, 10);
        g2d.setFont(Fonts.NORMAL);
        g2d.setColor(Colors.WHITE);
        g2d.drawString(getTipText(), 0, 0);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }
}
