package net.doge.util.ui.helper.platform;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongConsumer;

/**
 * 适用于 MacOS 的窗口穿透
 */
public class MacWindowClickThroughHelper {
    private static final Map<Window, MouseState> CHANGES = new WeakHashMap<>();

    public static void setEnabled(Window window, boolean enabled) {
        MouseState previous = CHANGES.get(window);
        if (!window.isDisplayable()) {
            CHANGES.remove(window);
            return;
        }
        if (!enabled && previous == null) return;

        Object platformWindow = getPlatformWindow(window);
        MouseState saved = previous != null && previous.peer.get() == platformWindow ? previous : null;
        if (!enabled && saved == null) {
            CHANGES.remove(window);
            return;
        }

        AtomicBoolean original = new AtomicBoolean();
        AppKit.run(() -> withHandle(platformWindow, value -> {
            Pointer handle = new Pointer(value);
            AppKit.requireWindow(handle);
            if (enabled && saved == null) {
                original.set(AppKit.sendBoolean(handle, AppKit.GET_IGNORES_MOUSE));
            }
            AppKit.setIgnoresMouse(handle, enabled || saved.ignoresMouse);
        }));

        if (!enabled) {
            CHANGES.remove(window);
        } else if (saved == null) {
            CHANGES.put(window, new MouseState(platformWindow, original.get()));
        }
    }

    private static Object getPlatformWindow(Window window) {
        try {
            Class<?> accessorClass = Class.forName("sun.awt.AWTAccessor");
            Object accessor = accessorClass.getMethod("getComponentAccessor").invoke(null);
            Class<?> componentAccessor = Class.forName("sun.awt.AWTAccessor$ComponentAccessor");
            Object peer = componentAccessor.getMethod("getPeer", Component.class).invoke(accessor, window);
            Class<?> windowPeer = Class.forName("sun.lwawt.LWWindowPeer");
            if (!windowPeer.isInstance(peer)) {
                throw new UnsupportedOperationException("The window does not have an OpenJDK Cocoa peer");
            }
            return windowPeer.getMethod("getPlatformWindow").invoke(peer);
        } catch (ReflectiveOperationException failure) {
            throw new UnsupportedOperationException("Cannot access the macOS AWT peer. See the macOS JVM options in README.",
                    failure);
        }
    }

    private static void withHandle(Object platformWindow, LongConsumer operation) {
        try {
            Class<?> resource = Class.forName("sun.lwawt.macosx.CFRetainedResource");
            Class<?> action = Class.forName("sun.lwawt.macosx.CFRetainedResource$CFNativeAction");
            AtomicBoolean called = new AtomicBoolean();
            Object callback = Proxy.newProxyInstance(action.getClassLoader(), new Class<?>[]{action}, (proxy, method, args) -> {
                operation.accept((Long) args[0]);
                called.set(true);
                return null;
            });
            resource.getMethod("execute", action).invoke(platformWindow, callback);
            if (!called.get()) {
                throw new IllegalStateException("The Cocoa window no longer has a native handle");
            }
        } catch (ReflectiveOperationException failure) {
            throw new UnsupportedOperationException("Cannot access the Cocoa window. See the macOS JVM options in README.",
                    failure);
        }
    }

    private static final class MouseState {
        private final WeakReference<Object> peer;
        private final boolean ignoresMouse;

        private MouseState(Object peer, boolean ignoresMouse) {
            this.peer = new WeakReference<>(peer);
            this.ignoresMouse = ignoresMouse;
        }
    }

    private static final class AppKit {
        private static final NativeLibrary SYSTEM = NativeLibrary.getInstance("System");
        private static final NativeLibrary OBJC = NativeLibrary.getInstance("objc");
        private static final Function SEND_MESSAGE = OBJC.getFunction("objc_msgSend");
        private static final Function REGISTER_SELECTOR = OBJC.getFunction("sel_registerName");
        private static final Pointer NS_WINDOW = OBJC.getFunction("objc_getClass").invokePointer(new Object[]{"NSWindow"});
        private static final Pointer IS_KIND_OF_CLASS = REGISTER_SELECTOR.invokePointer(new Object[]{"isKindOfClass:"});
        private static final Pointer GET_IGNORES_MOUSE = REGISTER_SELECTOR.invokePointer(new Object[]{"ignoresMouseEvents"});
        private static final Pointer SET_IGNORES_MOUSE = REGISTER_SELECTOR.invokePointer(new Object[]{"setIgnoresMouseEvents:"});

        private static void requireWindow(Pointer handle) {
            if (NS_WINDOW == null || !sendBoolean(handle, IS_KIND_OF_CLASS, NS_WINDOW)) {
                throw new UnsupportedOperationException("The AWT peer does not own an NSWindow");
            }
        }

        private static boolean sendBoolean(Pointer handle, Pointer selector) {
            return (Byte) SEND_MESSAGE.invoke(Byte.TYPE, new Object[]{handle, selector}) != 0;
        }

        private static boolean sendBoolean(Pointer handle, Pointer selector, Pointer argument) {
            return (Byte) SEND_MESSAGE.invoke(Byte.TYPE, new Object[]{handle, selector, argument}) != 0;
        }

        private static void setIgnoresMouse(Pointer handle, boolean ignoresMouse) {
            SEND_MESSAGE.invokeVoid(new Object[]{handle, SET_IGNORES_MOUSE, (byte) (ignoresMouse ? 1 : 0)});
        }

        private static void run(Runnable action) {
            if (SYSTEM.getFunction("pthread_main_np").invokeInt(new Object[0]) != 0) {
                action.run();
                return;
            }
            AtomicReference<Throwable> failure = new AtomicReference<>();
            DispatchCallback callback = context -> {
                try {
                    action.run();
                } catch (RuntimeException | Error error) {
                    failure.set(error);
                }
            };
            // dispatch_get_main_queue() 是内联函数，返回这个主队列对象的地址
            Pointer queue = SYSTEM.getGlobalVariableAddress("_dispatch_main_q");
            SYSTEM.getFunction("dispatch_sync_f").invokeVoid(new Object[]{queue, null, callback});
            Throwable error = failure.get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            }
            if (error instanceof Error) {
                throw (Error) error;
            }
        }
    }

    private interface DispatchCallback extends Callback {
        void run(Pointer context);
    }
}
