package net.doge.ui.components.dialog;

import net.doge.constants.Colors;
import net.doge.constants.Fonts;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.ui.components.CustomSlider;
import net.doge.ui.components.dialog.factory.AbstractShadowDialog;
import net.doge.ui.componentui.VSliderUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author yzx
 * @Description 播放速率对话框
 * @Date 2020/12/15
 */
public class RateDialog extends AbstractShadowDialog {
    // 最大/小值
    private final int MIN_VAL = 1;
    private final int MAX_VAL = 80;

    private CustomPanel centerPanel = new CustomPanel();
    private final CustomLabel valLabel = new CustomLabel();
    private final CustomSlider slider = new CustomSlider();

    private VideoDialog d;
    private JComponent comp;

    public RateDialog(PlayerFrame f, VideoDialog d, JComponent comp) {
        super(d == null ? f : d, false);
        this.f = f;
        this.d = d;
        this.comp = comp;
    }

    public void close() {
        f.currDialogs.remove(this);
        dispose();
    }

    public void showDialog() {
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                close();
            }
        });
        setResizable(false);
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();

        // 调整位置使之在按钮上方
        Point p = comp.getLocation();
        Dimension s = getSize();
        SwingUtilities.convertPointToScreen(p, comp.getParent());
        setLocation(p.x - s.width / 2 + comp.getWidth() / 2, p.y - s.height - 5);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Color textColor = f.currUIStyle.getTextColor();
        Color sliderColor = f.currUIStyle.getSliderColor();

        // 标签
        valLabel.setForeground(textColor);
        centerPanel.add(valLabel, BorderLayout.NORTH);

        // 滑动条
        slider.setFont(Fonts.NORMAL);
        slider.setForeground(textColor);
        slider.setUI(new VSliderUI(slider, sliderColor, sliderColor));
        slider.setPreferredSize(new Dimension(35, 500));
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        slider.setMinimum(MIN_VAL);
        slider.setMaximum(MAX_VAL);
        slider.setOrientation(SwingConstants.VERTICAL);
        double rate = d == null ? f.currRate : f.currVideoRate;
        int val = (int) (rate * 10);
        valLabel.setText(String.format("%.1fx", rate).replace(".0", ""));
        slider.setValue(val);

        slider.addChangeListener(e -> {
            // 更新值
            float newVal = (float) slider.getValue() / 10;
            String txt = String.format("%.1fx", newVal).replace(".0", "");
            valLabel.setText(txt);
            if (d == null) f.player.setRate(f.currRate = newVal);
            else d.mp.setRate(f.currVideoRate = newVal);
        });

        centerPanel.add(slider, BorderLayout.CENTER);
    }
}
