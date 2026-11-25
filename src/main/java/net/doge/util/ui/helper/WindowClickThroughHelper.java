package net.doge.util.ui.helper;

import com.sun.jna.Platform;
import net.doge.util.ui.helper.platform.MacWindowClickThroughHelper;
import net.doge.util.ui.helper.platform.WindowsClickThroughHelper;
import net.doge.util.ui.helper.platform.X11WindowClickThroughHelper;

import java.awt.*;
import java.util.Objects;

/**
 * 为 Windows、MacOS 或 X11 上的 AWT 或 Swing 窗口提供鼠标穿透功能。
 */
public final class WindowClickThroughHelper {
    /**
     * 根据当前平台选择实现，开启或关闭鼠标穿透。
     * 通过 {@code pack()} 或 {@code setVisible(true)} 创建原生窗口后，在事件分发线程上调用。
     * 如需设置窗口的不透明度和背景，请在开启穿透前完成。
     */
    public static void setEnabled(Window window, boolean enabled) {
        Objects.requireNonNull(window, "window");
        if (enabled && !window.isDisplayable()) {
            throw new IllegalStateException("Call pack() or setVisible(true) before enabling click-through");
        }

        // Windows
        if (Platform.isWindows()) {
            WindowsClickThroughHelper.setEnabled(window, enabled);
        }
        // MacOS
        else if (Platform.isMac()) {
            MacWindowClickThroughHelper.setEnabled(window, enabled);
        }
        // X11
        else {
            String toolkit = Toolkit.getDefaultToolkit().getClass().getName();
            if ("sun.awt.X11.XToolkit".equals(toolkit)) {
                X11WindowClickThroughHelper.setEnabled(window, enabled);
            } else if (toolkit.endsWith(".WLToolkit")) {
                throw new UnsupportedOperationException("Run AWT with XToolkit through XWayland to use click-through");
            } else {
                throw new UnsupportedOperationException("Window click-through is unavailable for " + toolkit);
            }
        }
    }
}
