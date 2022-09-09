package net.doge.ui.componentui;

import net.doge.constants.Colors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 为 ChangePaneButton 设定的自定义 UI，图标添加遮罩
 * @Date 2020/12/13
 */
public class ChangePaneButtonUI extends BasicButtonUI {
    private boolean drawMask = false;

    public void setDrawMask(boolean drawMask) {
        this.drawMask = drawMask;
    }

    @Override
    protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
        super.paintIcon(g, c, iconRect);

        if(drawMask) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            Color OriginColor = g2d.getColor();
            g2d.setColor(Colors.BLACK);
            g2d.fillRoundRect(iconRect.x, iconRect.y, iconRect.width, iconRect.height, 10, 10);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setColor(OriginColor);
        }
    }
}
