package net.doge.ui.widget.button;

import net.doge.ui.widget.button.base.BaseButton;
import net.doge.ui.widget.button.listener.ChangePaneButtonMouseListener;
import net.doge.ui.widget.button.ui.ChangePaneButtonUI;

public class ChangePaneButton extends BaseButton {
    public ChangePaneButton() {
        init();
    }

    private void init() {
        addMouseListener(new ChangePaneButtonMouseListener(this));
    }

    public void transitionDrawMask(boolean drawMaskIncreasing) {
        ((ChangePaneButtonUI) getUI()).transitionDrawMask(drawMaskIncreasing);
    }
}
