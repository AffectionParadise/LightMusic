package net.doge.util.ui.helper.platform;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 适用于 X11(Linux) 的窗口穿透
 */
public class X11WindowClickThroughHelper {
    private static final int SHAPE_INPUT = 2;
    private static final int SHAPE_SET = 0;
    private static final int UNSORTED = 0;
    private static final int RECTANGLE_BYTES = 4 * Short.BYTES;
    private static final NativeLong NONE = new NativeLong(0);
    private static final Map<Window, ShapeChange> CHANGES = new WeakHashMap<>();

    public static void setEnabled(Window window, boolean enabled) {
        ShapeChange previous = CHANGES.get(window);
        if (!window.isDisplayable()) {
            CHANGES.remove(window);
            return;
        }
        if (!enabled && previous == null) return;

        NativeLong handle = new NativeLong(Native.getWindowID(window));
        if (previous != null && !previous.handle.equals(handle)) {
            CHANGES.remove(window);
            previous = null;
        }
        if (enabled == (previous != null)) return;

        Pointer display = Xlib.INSTANCE.XOpenDisplay(null);
        if (display == null) {
            throw new IllegalStateException("Cannot open the X11 display");
        }
        try {
            if (enabled) {
                checkInputShapeSupport(display);
                byte[] previousShape = getInputShape(display, handle);
                setInputRectangles(display, handle, null, 0);
                CHANGES.put(window, new ShapeChange(handle, previousShape));
            } else {
                restoreInputShape(display, previous);
                CHANGES.remove(window);
            }
        } finally {
            Xlib.INSTANCE.XCloseDisplay(display);
        }
    }

    private static void checkInputShapeSupport(Pointer display) {
        IntByReference major = new IntByReference();
        IntByReference minor = new IntByReference();
        if (Shape.INSTANCE.XShapeQueryVersion(display, major, minor) == 0
                || major.getValue() < 1 || (major.getValue() == 1 && minor.getValue() < 1)) {
            throw new UnsupportedOperationException("X11 mouse click-through requires the Shape extension 1.1");
        }
    }

    private static byte[] getInputShape(Pointer display, NativeLong handle) {
        IntByReference count = new IntByReference();
        Pointer rectangles = Shape.INSTANCE.XShapeGetRectangles(display, handle, SHAPE_INPUT, count, new IntByReference());
        if (rectangles == null) return new byte[0];
        try {
            if (count.getValue() == 1 && coversDefaultRegion(display, handle, rectangles)) {
                // 默认输入区域应随窗口尺寸变化
                return null;
            }
            return rectangles.getByteArray(0, count.getValue() * RECTANGLE_BYTES);
        } finally {
            Xlib.INSTANCE.XFree(rectangles);
        }
    }

    private static boolean coversDefaultRegion(Pointer display, NativeLong handle, Pointer rectangle) {
        IntByReference width = new IntByReference();
        IntByReference height = new IntByReference();
        IntByReference border = new IntByReference();
        if (Xlib.INSTANCE.XGetGeometry(display, handle, new NativeLongByReference(), new IntByReference(),
                new IntByReference(), width, height, border, new IntByReference()) == 0) {
            throw new IllegalStateException("Cannot read X11 window geometry");
        }
        int borderWidth = border.getValue();
        return rectangle.getShort(0) == -borderWidth && rectangle.getShort(2) == -borderWidth
                && Short.toUnsignedInt(rectangle.getShort(4)) == width.getValue() + 2 * borderWidth
                && Short.toUnsignedInt(rectangle.getShort(6)) == height.getValue() + 2 * borderWidth;
    }

    private static void restoreInputShape(Pointer display, ShapeChange previous) {
        byte[] rectangles = previous.rectangles;
        if (rectangles == null) {
            Shape.INSTANCE.XShapeCombineMask(display, previous.handle, SHAPE_INPUT, 0, 0, NONE, SHAPE_SET);
        } else if (rectangles.length == 0) {
            setInputRectangles(display, previous.handle, null, 0);
        } else {
            try (Memory data = new Memory(rectangles.length)) {
                data.write(0, rectangles, 0, rectangles.length);
                setInputRectangles(display, previous.handle, data, rectangles.length / RECTANGLE_BYTES);
            }
        }
    }

    private static void setInputRectangles(Pointer display, NativeLong handle, Pointer rectangles, int count) {
        Shape.INSTANCE.XShapeCombineRectangles(display, handle, SHAPE_INPUT, 0, 0, rectangles, count, SHAPE_SET, UNSORTED);
    }

    private static final class ShapeChange {
        private final NativeLong handle;
        private final byte[] rectangles;

        private ShapeChange(NativeLong handle, byte[] rectangles) {
            this.handle = handle;
            this.rectangles = rectangles;
        }
    }

    private interface Xlib extends Library {
        Xlib INSTANCE = Native.load("X11", Xlib.class);

        Pointer XOpenDisplay(String name);

        int XCloseDisplay(Pointer display);

        int XFree(Pointer data);

        int XGetGeometry(Pointer display, NativeLong window, NativeLongByReference root, IntByReference x,
                         IntByReference y, IntByReference width, IntByReference height, IntByReference border, IntByReference depth);
    }

    private interface Shape extends Library {
        Shape INSTANCE = Native.load("Xext", Shape.class);

        int XShapeQueryVersion(Pointer display, IntByReference major, IntByReference minor);

        Pointer XShapeGetRectangles(Pointer display, NativeLong window, int kind, IntByReference count,
                                    IntByReference ordering);

        void XShapeCombineRectangles(Pointer display, NativeLong window, int kind, int x, int y,
                                     Pointer rectangles, int count, int operation, int ordering);

        void XShapeCombineMask(Pointer display, NativeLong window, int kind, int x, int y, NativeLong mask, int operation);
    }
}
