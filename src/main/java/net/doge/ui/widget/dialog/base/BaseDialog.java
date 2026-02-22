package net.doge.ui.widget.dialog.base;

import net.doge.constant.core.ui.core.Colors;

import javax.swing.*;
import java.awt.*;

/**
 * 所有自定义 Dialog 的基类
 */
public class BaseDialog extends JDialog {
    public BaseDialog() {
        // 编译器默认选择最具体的类型，因此此处调用 Frame 的构造器
        // 桌面歌词虽然没有父窗口，但不能直接调用 Window 的构造器，应当使用 Frame 的！
        this(null);
    }

    public BaseDialog(Window owner, boolean modal) {
        super(owner);
        setModal(modal);
        init();
    }

    public BaseDialog(Frame owner) {
        this(owner, false);
    }

    public BaseDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }

    private void init() {
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setResizable(false);
    }
}
