package net.doge.ui.widget.dialog.base;

import net.doge.constant.core.ui.core.Colors;

import javax.swing.*;
import java.awt.*;

/**
 * 所有自定义 Dialog 的基类
 */
public class BaseDialog extends JDialog {
    public BaseDialog(Window owner) {
        this(owner, false);
    }

    public BaseDialog(Window owner, boolean modal) {
        super(owner);
        setModal(modal);
        init();
    }

    private void init() {
        // Dialog 背景透明
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setResizable(false);
    }
}
