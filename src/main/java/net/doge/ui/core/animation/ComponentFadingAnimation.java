package net.doge.ui.core.animation;

import lombok.Data;
import net.doge.ui.core.animation.handler.ComponentChangeHandler;
import net.doge.ui.widget.base.ExtendedOpacitySupported;

import javax.swing.*;
import java.awt.*;

/**
 * 处理组件淡入淡出的动画类
 */
@Data
public class ComponentFadingAnimation {
    // 淡入淡出状态
    private boolean changePaneFadeOut;
    // 组件
    private Component srcFadingComp;
    private Component targetFadingComp;
    // 动画 Timer
    private Timer changePaneFadingTimer;
    // 事件处理器
    private ComponentChangeHandler onFadingOutStopped;
    private ComponentChangeHandler onFadingInStopped;

    public ComponentFadingAnimation(Component srcFadingComp, Component targetFadingComp, ComponentChangeHandler onFadingOutStopped) {
        this(srcFadingComp, targetFadingComp, onFadingOutStopped, null);
    }

    public ComponentFadingAnimation(Component srcFadingComp, Component targetFadingComp, ComponentChangeHandler onFadingOutStopped, ComponentChangeHandler onFadingInStopped) {
        this.srcFadingComp = srcFadingComp;
        this.targetFadingComp = targetFadingComp;
        this.onFadingOutStopped = onFadingOutStopped;
        this.onFadingInStopped = onFadingInStopped;

        changePaneFadingTimer = new Timer(10, e -> {
            // 淡出
            if (changePaneFadeOut) {
                ExtendedOpacitySupported src = (ExtendedOpacitySupported) srcFadingComp;
                float opacity = Math.max(0f, src.getExtendedOpacity() - 0.05f);
                src.setTreeExtendedOpacity(opacity);
                if (opacity <= 0f) {
                    // 淡出动画完成后恢复透明度
                    src.setTreeExtendedOpacity(1f);
                    changePaneFadeOut = false;
                    if (onFadingOutStopped != null) onFadingOutStopped.handle(srcFadingComp, targetFadingComp);
                }
            }
            // 淡入
            else {
                ExtendedOpacitySupported target = (ExtendedOpacitySupported) targetFadingComp;
                float opacity = Math.min(1f, target.getExtendedOpacity() + 0.05f);
                target.setTreeExtendedOpacity(opacity);
                if (opacity >= 1f) {
                    // 淡入动画完成后恢复透明度
                    target.setTreeExtendedOpacity(1f);
                    changePaneFadingTimer.stop();
                    if (onFadingInStopped != null) onFadingInStopped.handle(srcFadingComp, targetFadingComp);
                }
            }
        });
    }

    // 执行淡入淡出切换组件动画
    public void transition() {
        if (changePaneFadingTimer.isRunning()) return;
        if (srcFadingComp == targetFadingComp) {
            if (onFadingOutStopped != null) onFadingOutStopped.handle(srcFadingComp, targetFadingComp);
            if (onFadingInStopped != null) onFadingInStopped.handle(srcFadingComp, targetFadingComp);
        } else {
            ((ExtendedOpacitySupported) srcFadingComp).setTreeExtendedOpacity(1f);
            ((ExtendedOpacitySupported) targetFadingComp).setTreeExtendedOpacity(0f);
            changePaneFadeOut = true;
            changePaneFadingTimer.start();
        }
    }

    // 是否正在执行动画
    public boolean isRunning() {
        return changePaneFadingTimer.isRunning();
    }
}
