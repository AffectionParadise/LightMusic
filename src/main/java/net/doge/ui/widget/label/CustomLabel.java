package net.doge.ui.widget.label;

import lombok.Getter;
import lombok.Setter;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.ui.widget.base.ExtendedOpacitySupported;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.ui.GraphicsUtil;
import net.doge.util.ui.ScaleUtil;
import net.doge.util.ui.SwingUtil;

import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel implements ExtendedOpacitySupported {
    @Setter
    private boolean drawBg;
    @Setter
    private Color bgColor;
    @Setter
    @Getter
    protected float opacity = 1f;
    private float destOpacity = 1f;
    private Timer opacityTimer;
    @Getter
    private float extendedOpacity = 1f;

    public CustomLabel() {
        this(null, null);
    }

    public CustomLabel(String text) {
        this(text, null);
    }

    public CustomLabel(Icon icon) {
        this(null, icon);
    }

    public CustomLabel(String text, Icon icon) {
        super(text, icon, CENTER);
        init();
    }

    private void init() {
        setFont(Fonts.NORMAL);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        opacityTimer = new Timer(0, e -> {
            if (opacity < destOpacity) opacity = Math.min(destOpacity, opacity + 0.005f);
            else if (opacity > destOpacity) opacity = Math.max(destOpacity, opacity - 0.005f);
            else opacityTimer.stop();
            repaint();
        });
    }

    public void transitionOpacity(float opacity) {
        this.destOpacity = opacity;
        if (opacityTimer.isRunning()) return;
        opacityTimer.start();
    }

    public void setOpacity(float opacity) {
        // 设置透明度时打断透明度动画
        if (opacityTimer.isRunning()) opacityTimer.stop();
        this.opacity = opacity;
    }

    // 根据主题色更新图标
    public void updateIconStyle() {
        Icon icon = getIcon();
        if (icon == null) return;
        setIcon(ImageUtil.dye((ImageIcon) icon, UIStyleStorage.currUIStyle.getIconColor()));
    }

    // 是否为空文本
    public boolean isTextEmpty() {
        return StringUtil.isEmpty(getText());
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
        // 画背景
        if (drawBg) {
            Graphics2D g2d = GraphicsUtil.setup(g);
            g2d.setColor(bgColor);
            GraphicsUtil.srcOver(g2d, extendedOpacity * 0.1f);
            int arc = ScaleUtil.scale(10);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }
        GraphicsUtil.srcOver(g, extendedOpacity * opacity);
        super.paintComponent(g);
    }
}
