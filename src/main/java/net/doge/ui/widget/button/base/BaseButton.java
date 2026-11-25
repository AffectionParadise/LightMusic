package net.doge.ui.widget.button.base;

import net.doge.constant.core.ui.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.tooltip.CustomToolTip;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BaseButton extends JButton {
    private static final HDEmptyBorder BORDER = new HDEmptyBorder(5, 5, 5, 5);
    private CustomToolTip toolTip;

    public BaseButton() {
        this(null, null);
    }

    public BaseButton(String text) {
        this(text, null);
    }

    public BaseButton(Icon icon) {
        this(null, icon);
    }

    public BaseButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(BORDER);
        setContentAreaFilled(false);
        setFocusable(false);
        setFocusPainted(false);
        setFont(Fonts.NORMAL);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // 根据主题色更新图标
    public void updateIconStyle() {
        Icon icon = getIcon();
        if (icon == null) return;
        setIcon(ImageUtil.dye((ImageIcon) icon, UIStyleStorage.currUIStyle.getIconColor()));
    }

    // 根据指定颜色更新图标
    public void updateIconColor(Color color) {
        Icon icon = getIcon();
        if (icon == null) return;
        setIcon(ImageUtil.dye((ImageIcon) icon, color));
    }

    // 应用主题色的图标
    public void setStyledIcon(ImageIcon icon) {
        setIcon(ImageUtil.dye(icon, UIStyleStorage.currUIStyle.getIconColor()));
    }

    @Override
    public JToolTip createToolTip() {
        if (toolTip == null) {
            toolTip = new CustomToolTip();
            toolTip.setComponent(this);
        }
        return toolTip;
    }

    @Override
    public Point getToolTipLocation(MouseEvent event) {
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        String text = getToolTipText(event);
        if (rootPane == null || text == null) return null;

        JToolTip tip = createToolTip();
        tip.setTipText(text);
        Dimension size = tip.getPreferredSize();
        Rectangle button = SwingUtilities.convertRectangle(this, getVisibleRect(), rootPane);
        Rectangle available = getToolTipBounds(rootPane, button);
        button = button.intersection(available);
        if (button.isEmpty()) return null;

        Point location = placeToolTip(button, available, size);

        // ToolTipManager 会在从右到左的布局中将提示框左移一个提示框宽度
        if (!getComponentOrientation().isLeftToRight()) location.x += size.width;
        return SwingUtilities.convertPoint(rootPane, location, this);
    }

    private Rectangle getToolTipBounds(JRootPane rootPane, Rectangle button) {
        Rectangle available = rootPane.getVisibleRect();
        if (!rootPane.isShowing()) return available;

        Point origin = rootPane.getLocationOnScreen();
        Point buttonCenter = new Point(origin.x + button.x + button.width / 2,
                origin.y + button.y + button.height / 2);
        GraphicsConfiguration screen = rootPane.getGraphicsConfiguration();
        if (screen == null || !screen.getBounds().contains(buttonCenter)) {
            for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
                GraphicsConfiguration candidate = device.getDefaultConfiguration();
                if (candidate.getBounds().contains(buttonCenter)) {
                    screen = candidate;
                    break;
                }
            }
        }
        if (screen == null) return available;

        Rectangle workArea = screen.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(screen);
        workArea.setBounds(workArea.x + insets.left - origin.x, workArea.y + insets.top - origin.y,
                workArea.width - insets.left - insets.right, workArea.height - insets.top - insets.bottom);
        // 从右到左布局用提示框右端定位，定位点也需要留在当前屏幕内
        if (!getComponentOrientation().isLeftToRight() && insets.right == 0) workArea.width--;
        return available.intersection(workArea);
    }

    private Point placeToolTip(Rectangle button, Rectangle available, Dimension size) {
        int gap = ScaleUtil.scale(3);
        int maxX = available.x + available.width - size.width;
        int maxY = available.y + available.height - size.height;
        int x = Math.max(available.x, Math.min(button.x + (button.width - size.width) / 2, maxX));
        int below = button.y + button.height + gap;
        int above = button.y - size.height - gap;
        if (below <= maxY) return new Point(x, below);
        if (above >= available.y) return new Point(x, above);

        // 上下空间不足时尝试放到按钮侧面，避免遮住按钮
        int y = Math.max(available.y, Math.min(button.y + (button.height - size.height) / 2, maxY));
        int right = button.x + button.width + gap;
        int left = button.x - size.width - gap;
        boolean rightFits = right <= maxX;
        boolean leftFits = left >= available.x;
        if (rightFits && (!leftFits || getComponentOrientation().isLeftToRight())) return new Point(right, y);
        if (leftFits) return new Point(left, y);

        int belowOverflow = below - maxY;
        int aboveOverflow = available.y - above;
        return new Point(x, belowOverflow <= aboveOverflow ? Math.max(available.y, maxY) : available.y);
    }
}
