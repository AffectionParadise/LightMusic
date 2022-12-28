package net.doge.ui.components.dialog;

import net.doge.constants.Colors;
import net.doge.constants.EqualizerData;
import net.doge.ui.PlayerFrame;
import net.doge.ui.components.*;
import net.doge.ui.components.dialog.factory.AbstractShadowDialog;
import net.doge.ui.componentui.ComboBoxUI;
import net.doge.ui.componentui.VSliderUI;
import net.doge.ui.listeners.ButtonMouseListener;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @Author yzx
 * @Description 音效对话框
 * @Date 2020/12/15
 */
public class SoundEffectDialog extends AbstractShadowDialog {
    private final String TITLE = "音效";

    private CustomPanel centerPanel = new CustomPanel();

    private CustomPanel topPanel = new CustomPanel();
    private CustomLabel titleLabel = new CustomLabel();
    private CustomPanel windowCtrlPanel = new CustomPanel();
    private CustomButton closeButton = new CustomButton();

    private CustomPanel soundEffectPanel = new CustomPanel();
    private CustomPanel sliderPanel = new CustomPanel();

    private CustomLabel soundEffectLabel = new CustomLabel("音效：");
    private CustomComboBox<String> comboBox = new CustomComboBox<>();
    private final CustomPanel[] panels = {
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel(),
            new CustomPanel()
    };
    private final CustomLabel[] vals = {
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel()
    };
    private final CustomSlider[] sliders = {
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider(),
            new CustomSlider()
    };
    private final CustomLabel[] hzs = {
            new CustomLabel("31"),
            new CustomLabel("62"),
            new CustomLabel("125"),
            new CustomLabel("250"),
            new CustomLabel("500"),
            new CustomLabel("1k"),
            new CustomLabel("2k"),
            new CustomLabel("4k"),
            new CustomLabel("8k"),
            new CustomLabel("16k")
    };
    private boolean fitting;

    public SoundEffectDialog(PlayerFrame f) {
        super(f);

        for (String se : EqualizerData.names) comboBox.addItem(se);
        comboBox.addItem("自定义");
    }

    public void showDialog() {
        // 解决 setUndecorated(true) 后窗口不能拖动的问题
        Point origin = new Point();
        topPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // mouseDragged 不能正确返回 button 值，需要借助此方法
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });

        setTitle(TITLE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocation(400, 200);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        add(globalPanel, BorderLayout.CENTER);

        setUndecorated(true);
        setBackground(Colors.TRANSLUCENT);
        pack();
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    // 初始化标题栏
    private void initTitleBar() {
        titleLabel.setForeground(f.currUIStyle.getTextColor());
        titleLabel.setText(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        closeButton.setIcon(ImageUtils.dye(f.closeWindowIcon, f.currUIStyle.getIconColor()));
        closeButton.setPreferredSize(new Dimension(f.closeWindowIcon.getIconWidth() + 2, f.closeWindowIcon.getIconHeight()));
        // 关闭窗口
        closeButton.addActionListener(e -> {
            f.currDialogs.remove(this);
            dispose();
        });
        // 鼠标事件
        closeButton.addMouseListener(new ButtonMouseListener(closeButton, f));
        FlowLayout fl = new FlowLayout(FlowLayout.RIGHT);
        windowCtrlPanel.setLayout(fl);
        windowCtrlPanel.setMinimumSize(new Dimension(40, 30));
        windowCtrlPanel.add(closeButton);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(windowCtrlPanel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        globalPanel.add(topPanel, BorderLayout.NORTH);
    }

    private void initView() {
        centerPanel.setLayout(new BorderLayout());

        Color textColor = f.currUIStyle.getTextColor();
        Color sliderColor = f.currUIStyle.getSliderColor();

        // 音效选择面板
        soundEffectLabel.setForeground(textColor);
        comboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            int index = comboBox.getSelectedIndex();
            // 记录当前音效
            f.currSoundEffect = index;
            double[][] eds = EqualizerData.data;
            if (index >= eds.length) return;

            double[] newEd = eds[index];
            // 记录当前均衡
            f.ed = newEd;
            f.player.adjustEqualizerBands(newEd);
            fitData(newEd);
        });
        // 下拉框 UI
        comboBox.setUI(new ComboBoxUI(comboBox, f, 220));

        soundEffectPanel.add(soundEffectLabel);
        soundEffectPanel.add(comboBox);
        centerPanel.add(soundEffectPanel, BorderLayout.NORTH);

        // 滑动条面板
        sliderPanel.setLayout(new GridLayout(1, 10));
        for (int i = 0, len = panels.length; i < len; i++) {
            CustomPanel p = panels[i];
            CustomSlider s = sliders[i];
            CustomLabel val = vals[i];
            CustomLabel hz = hzs[i];

            // 滑动条
            s.setUI(new VSliderUI(s, sliderColor, sliderColor));
            s.setPreferredSize(new Dimension(30, 300));
            s.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            s.setMinimum(EqualizerData.MIN_GAIN);
            s.setMaximum(EqualizerData.MAX_GAIN);
            s.setOrientation(SwingConstants.VERTICAL);
//            s.setPaintTicks(true);
//            s.setPaintLabels(true);
//            s.setMajorTickSpacing(4);
//            s.setMinorTickSpacing(1);
//            s.setSnapToTicks(true);
            s.addChangeListener(e -> {
                // 更新值
                updateVals();
                if (fitting) return;
                comboBox.setSelectedItem("自定义");
                // 调整并记录当前均衡
                f.player.adjustEqualizerBands(f.ed = getData());
            });

            // 值
            val.setForeground(textColor);
            // 频率
            hz.setForeground(textColor);

            p.setLayout(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            p.add(val, BorderLayout.NORTH);
            p.add(s, BorderLayout.CENTER);
            p.add(hz, BorderLayout.SOUTH);

            sliderPanel.add(p);
        }

        // 加载当前音效
        comboBox.setSelectedIndex(f.currSoundEffect);
        // 加载当前均衡
        fitData(f.ed);

        centerPanel.add(sliderPanel, BorderLayout.CENTER);
    }

    // 根据滑动条的值获取均衡数据
    private double[] getData() {
        double[] data = new double[EqualizerData.BAND_NUM];
        int i = 0;
        for (CustomSlider slider : sliders) data[i++] = slider.getValue();
        return data;
    }

    // 根据均衡数据调整滑动条
    private void fitData(double[] data) {
        fitting = true;
        for (int i = 0, len = data.length; i < len; i++) sliders[i].setValue((int) data[i]);
        fitting = false;
    }

    // 更新值显示
    private void updateVals() {
        for (int i = 0, len = sliders.length; i < len; i++) {
            int val = sliders[i].getValue();
            String s = String.valueOf(val > 0 ? "+" + val : val);
            vals[i].setText(s);
        }
    }
}
