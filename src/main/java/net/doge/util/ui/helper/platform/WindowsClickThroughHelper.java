package net.doge.util.ui.helper.platform;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 适用于 Windows 的窗口穿透
 */
public class WindowsClickThroughHelper {
    private static final int GWL_EXSTYLE = -20;
    private static final int WS_EX_TRANSPARENT = 0x00000020;
    private static final int WS_EX_LAYERED = 0x00080000;
    private static final int LWA_ALPHA = 0x00000002;
    private static final int CLICK_THROUGH_STYLES = WS_EX_LAYERED | WS_EX_TRANSPARENT;

    // 在事件分发线程上访问窗口及其原生样式记录
    private static final Map<Window, StyleChange> CHANGES = new WeakHashMap<>();

    public static void setEnabled(Window window, boolean enabled) {
        if (!window.isDisplayable()) {
            CHANGES.remove(window);
            return;
        }

        Pointer handle = Native.getWindowPointer(window);
        StyleChange previous = CHANGES.get(window);
        if (previous != null && !previous.handle.equals(handle)) {
            // 已释放的窗口重新显示时可能会创建新的原生窗口
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
        // 逐像素透明窗口由 AWT 在显示时初始化分层样式
        int requestedStyles = window.isOpaque() ? CLICK_THROUGH_STYLES : WS_EX_TRANSPARENT;
        int addedStyles = requestedStyles & ~style;
        if (addedStyles == 0) return;

        int ownedStyles = previous == null ? addedStyles : previous.addedStyles | addedStyles;
        StyleChange change = new StyleChange(handle, ownedStyles);
        user.SetWindowLongW(handle, GWL_EXSTYLE, style | addedStyles);
        // 已由 Swing 管理的分层窗口沿用其不透明度设置
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
