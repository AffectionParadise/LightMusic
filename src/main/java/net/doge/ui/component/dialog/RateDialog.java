package net.doge.ui.component.dialog;

import net.doge.constant.system.I18n;
import net.doge.constant.ui.Colors;
import net.doge.constant.ui.Fonts;
import net.doge.ui.MainFrame;
import net.doge.ui.component.button.DialogButton;
import net.doge.ui.component.dialog.factory.AbstractShadowDialog;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.ui.component.slider.CustomSlider;
import net.doge.ui.component.slider.ui.VSliderUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author Doge
 * @Description 播放速率对话框
 * @Date 2020/12/15
 */
public class RateDialog extends AbstractShadowDialog {
    // 最大/小值
    private final int MIN_VAL = 1;
    private final int MAX_VAL = 80;

    private CustomPanel centerPanel = new CustomPanel();
    private CustomLabel valLabel = new CustomLabel();
    private CustomSlider slider = new CustomSlider();
    private DialogButton reset;

    private VideoDialog d;
    private JComponent comp;

    public RateDialog(MainFrame f, VideoDialog d, JComponent comp) {
        super(d == null ? f : d, false);
        this.f = f;
        this.d = d;
        this.comp = comp;

        Color textColor = f.currUIStyle.getTextColor();
        reset = new DialogButton(I18n.getText("reset"), textColor);
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
        setContentPane(globalPanel);

        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
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

        // 重置按钮
        reset.addActionListener(e -> slider.setValue(10));

        centerPanel.add(slider, BorderLayout.CENTER);
        centerPanel.add(reset, BorderLayout.SOUTH);
    }
}
