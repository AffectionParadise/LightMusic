package net.doge.util.ui.helper;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * 在 Windows 上为 AWT 或 Swing 顶层窗口启用鼠标点击穿透
 */
public final class WindowClickThroughHelper {
    private static final int GWL_EXSTYLE = -20;
    private static final int WS_EX_TRANSPARENT = 0x00000020;
    private static final int WS_EX_LAYERED = 0x00080000;
    private static final int LWA_ALPHA = 0x00000002;
    private static final int CLICK_THROUGH_STYLES = WS_EX_LAYERED | WS_EX_TRANSPARENT;

    // 对窗口及其原生样式的访问/修改必须在 EDT（事件分发线程）上进行
    private static final Map<Window, StyleChange> CHANGES = new WeakHashMap<>();

    /**
     * 启用点击穿透，或恢复此方法所添加的样式。请在通过 {@code pack()} 或
     * {@code setVisible(true)} 创建原生对等体（native peer）之后，在 EDT 上调用
     * 启用点击穿透前，请先配置窗口的不透明度和背景
     */
    public static void setEnabled(Window window, boolean enabled) {
        Objects.requireNonNull(window, "window");
        if (!Platform.isWindows()) {
            throw new UnsupportedOperationException("Window click-through is available on Windows");
        }
        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Change window click-through on the AWT event dispatch thread");
        }
        if (!window.isDisplayable()) {
            CHANGES.remove(window);
            if (enabled) {
                throw new IllegalStateException("Call pack() or setVisible(true) before enabling click-through");
            }
            return;
        }

        Pointer handle = Native.getWindowPointer(window);
        StyleChange previous = CHANGES.get(window);
        if (previous != null && !previous.handle.equals(handle)) {
            // 已 dispose 的窗口可通过新的 native peer 再次显示
            CHANGES.remove(window);
            previous = null;
        }

        if (enabled) {
            enable(window, handle, previous);
        } else if (previous != null) {
            int style = User32.INSTANCE.GetWindowLongW(handle, GWL_EXSTYLE);
            User32.INSTANCE.SetWindowLongW(handle, GWL_EXSTYLE, style & ~previous.addedStyles);
            CHANGES.remove(window);
            window.repaint();
        }
    }

    private static void enable(Window window, Pointer handle, StyleChange previous) {
        User32 user = User32.INSTANCE;
        int style = user.GetWindowLongW(handle, GWL_EXSTYLE);
        int addedStyles = CLICK_THROUGH_STYLES & ~style;
        if (addedStyles == 0) return;

        int ownedStyles = previous == null ? addedStyles : previous.addedStyles | addedStyles;
        StyleChange change = new StyleChange(handle, ownedStyles);
        user.SetWindowLongW(handle, GWL_EXSTYLE, style | addedStyles);
        // 若 Swing 已拥有分层窗口，则保持其 alpha 设置不变
        if ((addedStyles & WS_EX_LAYERED) != 0 && !user.SetLayeredWindowAttributes(handle, 0, (byte) 255, LWA_ALPHA)) {
            LastErrorException failure = new LastErrorException(Native.getLastError());
            try {
                user.SetWindowLongW(handle, GWL_EXSTYLE, style);
            } catch (LastErrorException rollbackFailure) {
                CHANGES.put(window, change);
                failure.addSuppressed(rollbackFailure);
            }
            throw failure;
        }

        CHANGES.put(window, change);
        window.repaint();
    }

    private static final class StyleChange {
        private final Pointer handle;
        private final int addedStyles;

        private StyleChange(Pointer handle, int addedStyles) {
            this.handle = handle;
            this.addedStyles = addedStyles;
        }
    }

    private interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        int GetWindowLongW(Pointer window, int index) throws LastErrorException;

        int SetWindowLongW(Pointer window, int index, int value) throws LastErrorException;

        boolean SetLayeredWindowAttributes(Pointer window, int colorKey, byte alpha, int flags);
    }
}
