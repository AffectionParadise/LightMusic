package net.doge.ui.widget.dialog;

import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.ui.core.Colors;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.core.dimension.HDDimension;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractShadowDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.VerticalSliderUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Doge
 * @description 播放速率对话框
 * @date 2020/12/15
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

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        reset = new DialogButton(I18n.getText("reset"), textColor);
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
        centerPanel.setBorder(new HDEmptyBorder(10, 15, 10, 15));

        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color sliderColor = style.getSliderColor();

        // 标签
        valLabel.setForeground(textColor);
        centerPanel.add(valLabel, BorderLayout.NORTH);

        // 滑动条
        slider.setFont(Fonts.NORMAL);
        slider.setForeground(textColor);
        slider.setUI(new VerticalSliderUI(slider, sliderColor, sliderColor));
        slider.setPreferredSize(new HDDimension(35, 500));
        slider.setBorder(new HDEmptyBorder(0, 0, 4, 0));
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
