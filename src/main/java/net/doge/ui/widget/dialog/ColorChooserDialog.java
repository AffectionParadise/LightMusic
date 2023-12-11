package net.doge.ui.widget.dialog;

import net.doge.constant.lang.I18n;
import net.doge.constant.ui.Colors;
import net.doge.model.color.HSL;
import net.doge.model.color.HSV;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.ComboBoxUI;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.slider.CustomSlider;
import net.doge.ui.widget.slider.ui.ColorSliderUI;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.ui.widget.textfield.SafeDocument;
import net.doge.util.common.StringUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Author Doge
 * @Description 颜色选择对话框
 * @Date 2020/12/15
 */
public class ColorChooserDialog extends AbstractTitledDialog implements DocumentListener {
    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel cPanel = new CustomPanel();
    private CustomPanel leftPanel = new CustomPanel();
    private CustomPanel rightPanel = new CustomPanel();
    private CustomPanel tfPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    // 预定义颜色面板
    private Box preBox = Box.createVerticalBox();
    private CustomPanel prePanel = new CustomPanel();
    // 预设标签
    private CustomPanel pPanel = new CustomPanel();
    private CustomLabel preLabel = new CustomLabel(I18n.getText("presets"));

    private Box customBox = Box.createVerticalBox();
    // 自定义标签
    private CustomPanel customPanel = new CustomPanel();
    private CustomLabel customLabel = new CustomLabel(I18n.getText("custom"));
    // r
    private CustomLabel rlb = new CustomLabel("R");
    public CustomSlider rSlider = new CustomSlider();
    // g
    private CustomLabel glb = new CustomLabel("G");
    public CustomSlider gSlider = new CustomSlider();
    // b
    private CustomLabel blb = new CustomLabel("B");
    public CustomSlider bSlider = new CustomSlider();
    // 显示
    private CustomLabel view = new CustomLabel();

    private CustomComboBox<String> modelComboBox = new CustomComboBox<>();
    // 文本框
    private CustomLabel rLabel = new CustomLabel("R：");
    private CustomTextField rTextField = new CustomTextField(3);
    private CustomLabel gLabel = new CustomLabel("G：");
    private CustomTextField gTextField = new CustomTextField(3);
    private CustomLabel bLabel = new CustomLabel("B：");
    private CustomTextField bTextField = new CustomTextField(3);
    private CustomLabel hexLabel = new CustomLabel("Hex：");
    private CustomTextField hexTextField = new CustomTextField(7);

    private DialogButton ok;
    private DialogButton cancel;
    private DialogButton reset;

    private final Color[] preColors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PINK, Color.MAGENTA,
            Colors.BRICK_RED, Colors.DEEP_ORANGE, Colors.GOLD3, Colors.SPRING_GREEN, Colors.SKY, Colors.DEEP_BLUE, Colors.PINK3, Colors.ORCHID_3,
            Colors.DEEP_RED, Colors.BROWN, Colors.CARTON, Colors.TEA, Colors.CYAN_4, Colors.DODGER, Colors.PINK4, Colors.ORCHID_4};
    private boolean confirmed;
    private boolean updating;
    public int r;
    public int g;
    public int b;
    public float h;
    public float s;
    public float v;
    public float nh;
    public float ns;
    public float nl;
    public int max1;
    public int max2;
    public int max3;
    private Color source;
    private Color result;

    // 父窗口是否是模态
    public ColorChooserDialog(MainFrame f, Color color) {
        super(f, I18n.getText("chooseColor"));
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        HSV hsv = ColorUtil.colorToHsv(color);
        this.h = hsv.h;
        this.s = hsv.s;
        this.v = hsv.v;
        HSL hsl = ColorUtil.colorToHsl(color);
        this.nh = hsl.h;
        this.ns = hsl.s;
        this.nl = hsl.l;
        this.source = color;

        Color textColor = f.currUIStyle.getTextColor();
        ok = new DialogButton(I18n.getText("ok"), textColor);
        cancel = new DialogButton(I18n.getText("cancel"), textColor);
        reset = new DialogButton(I18n.getText("reset"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(600, 580);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(globalPanel);

        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setLocationRelativeTo(null);
        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Color getResult() {
        return result;
    }

    private void initView() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        Dimension d = new Dimension(1, 10);
        Box rBox = Box.createHorizontalBox();
        rBox.add(rlb);
        rBox.add(rSlider);
        leftPanel.add(rBox);
        leftPanel.add(Box.createRigidArea(d));
        Box gBox = Box.createHorizontalBox();
        gBox.add(glb);
        gBox.add(gSlider);
        leftPanel.add(gBox);
        leftPanel.add(Box.createRigidArea(d));
        Box bBox = Box.createHorizontalBox();
        bBox.add(blb);
        bBox.add(bSlider);
        leftPanel.add(bBox);

        rightPanel.add(view);

        d = new Dimension(10, 1);
        tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.X_AXIS));
        tfPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tfPanel.add(modelComboBox);
        tfPanel.add(rLabel);
        tfPanel.add(rTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(gLabel);
        tfPanel.add(gTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(bLabel);
        tfPanel.add(bTextField);
        tfPanel.add(Box.createRigidArea(d));
        tfPanel.add(hexLabel);
        tfPanel.add(hexTextField);

        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        buttonPanel.add(reset);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        pPanel.add(preLabel);
        preBox.add(pPanel);
        preBox.add(prePanel);

        customPanel.add(customLabel);
        customBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        customBox.add(customPanel);
        customBox.add(cPanel);

        cPanel.setLayout(new BorderLayout());
        cPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cPanel.add(leftPanel, BorderLayout.CENTER);
        cPanel.add(rightPanel, BorderLayout.EAST);
        cPanel.add(tfPanel, BorderLayout.SOUTH);

        centerPanel.add(preBox, BorderLayout.NORTH);
        centerPanel.add(customBox, BorderLayout.CENTER);

        globalPanel.add(centerPanel, BorderLayout.CENTER);
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        Color textColor = f.currUIStyle.getTextColor();
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);

        // 预设
        preLabel.setForeground(textColor);
        GridLayout gl = new GridLayout(3, 8);
        gl.setHgap(15);
        gl.setVgap(15);
        prePanel.setLayout(gl);
        prePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (Color c : preColors) {
            CustomLabel l = new CustomLabel();
            l.setIcon(ImageUtil.dyeCircle(50, c));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            l.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    updateColor(c);
                }
            });
            prePanel.add(l);
        }
        // 自定义
        customLabel.setForeground(textColor);

        rlb.setForeground(textColor);
        rSlider.setUI(new ColorSliderUI(rSlider, this));
        d = new Dimension(400, 12);
        rSlider.setPreferredSize(d);
        rSlider.setMinimum(0);
        rSlider.addChangeListener(e -> {
            if (updating) return;
            int val = rSlider.getValue();
            boolean isRGB = isRGB(), isHSV = isHSV();
            if (isRGB) r = val;
            else if (isHSV) h = val;
            else nh = val;
            updateColor(makeColor(), true);
        });
        glb.setForeground(textColor);
        gSlider.setUI(new ColorSliderUI(gSlider, this));
        gSlider.setPreferredSize(d);
        gSlider.setMinimum(0);
        gSlider.addChangeListener(e -> {
            if (updating) return;
            int val = gSlider.getValue();
            boolean isRGB = isRGB(), isHSV = isHSV();
            if (isRGB) g = val;
            else if (isHSV) s = val;
            else ns = val;
            updateColor(makeColor(), true);
        });
        blb.setForeground(textColor);
        bSlider.setUI(new ColorSliderUI(bSlider, this));
        bSlider.setPreferredSize(d);
        bSlider.setMinimum(0);
        bSlider.addChangeListener(e -> {
            if (updating) return;
            int val = bSlider.getValue();
            boolean isRGB = isRGB(), isHSV = isHSV();
            if (isRGB) b = val;
            else if (isHSV) v = val;
            else nl = val;
            updateColor(makeColor(), true);
        });

        // 按钮
        ok.addActionListener(e -> {
            confirmed = true;
            result = makeColor();
            close();
        });
        cancel.addActionListener(e -> close());
        reset.addActionListener(e -> updateColor(source));

        // 下拉框
        modelComboBox.setUI(new ComboBoxUI(modelComboBox, f, 80));
        modelComboBox.addItem("RGB");
        modelComboBox.addItem("HSV");
        modelComboBox.addItem("HSL");
        modelComboBox.addItemListener(e -> {
            // 避免事件被处理 2 次！
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            updateColorModel();
        });
        // 文本框
        rLabel.setForeground(textColor);
        rTextField.setForeground(textColor);
        rTextField.setCaretColor(textColor);
        rTextField.setSelectedTextColor(textColor);
        rTextField.setSelectionColor(darkerTextAlphaColor);

        gLabel.setForeground(textColor);
        gTextField.setForeground(textColor);
        gTextField.setCaretColor(textColor);
        gTextField.setSelectedTextColor(textColor);
        gTextField.setSelectionColor(darkerTextAlphaColor);

        bLabel.setForeground(textColor);
        bTextField.setForeground(textColor);
        bTextField.setCaretColor(textColor);
        bTextField.setSelectedTextColor(textColor);
        bTextField.setSelectionColor(darkerTextAlphaColor);

        hexLabel.setForeground(textColor);
        hexTextField.setForeground(textColor);
        hexTextField.setCaretColor(textColor);
        hexTextField.setSelectedTextColor(textColor);
        hexTextField.setSelectionColor(darkerTextAlphaColor);
        SafeDocument doc = new SafeDocument(7);
        doc.addDocumentListener(this);
        hexTextField.setDocument(doc);

        updateColorModel();
    }

    public boolean isRGB() {
        return modelComboBox.getSelectedIndex() == 0;
    }

    public boolean isHSV() {
        return modelComboBox.getSelectedIndex() == 1;
    }

    public boolean isHSL() {
        return modelComboBox.getSelectedIndex() == 2;
    }

    public Color makeColorFromRgb(int r, int g, int b) {
        return new Color(r, g, b);
    }

    public Color makeColorFromHsv(float h, float s, float v) {
        return ColorUtil.hsvValToColor(h, s, v);
    }

    public Color makeColorFromHsl(float h, float s, float l) {
        return ColorUtil.hslValToColor(h, s, l);
    }

    public Color makeColor() {
        return isRGB() ? makeColorFromRgb(r, g, b) : isHSV() ? makeColorFromHsv(h, s, v) : makeColorFromHsl(nh, ns, nl);
    }

    // 改变颜色模型
    private void updateColorModel() {
        updating = true;
        if (isRGB()) {
            max1 = max2 = max3 = 255;
            rlb.setText("R");
            glb.setText("G");
            blb.setText("B");
            rLabel.setText("R：");
            gLabel.setText("G：");
            bLabel.setText("B：");
        } else if (isHSV()) {
            max1 = 359;
            max2 = max3 = 100;
            rlb.setText("H");
            glb.setText("S");
            blb.setText("V");
            rLabel.setText("H：");
            gLabel.setText("S：");
            bLabel.setText("V：");
        } else {
            max1 = 359;
            max2 = max3 = 100;
            rlb.setText("H");
            glb.setText("S");
            blb.setText("L");
            rLabel.setText("H：");
            gLabel.setText("S：");
            bLabel.setText("L：");
        }

        rSlider.setMaximum(max1);
        gSlider.setMaximum(max2);
        bSlider.setMaximum(max3);

        SafeDocument doc = new SafeDocument(0, max1);
        doc.addDocumentListener(this);
        rTextField.setDocument(doc);
        doc = new SafeDocument(0, max2);
        doc.addDocumentListener(this);
        gTextField.setDocument(doc);
        doc = new SafeDocument(0, max3);
        doc.addDocumentListener(this);
        bTextField.setDocument(doc);

        updateColor(makeColor());
        updating = false;
    }

    // 颜色更新时更新界面
    private void updateUI() {
        rSlider.repaint();
        gSlider.repaint();
        bSlider.repaint();
        view.setIcon(ImageUtil.dyeCircle(80, makeColor()));
        try {
            boolean isRGB = isRGB(), isHSV = isHSV();
            rTextField.setText(String.valueOf(isRGB ? r : isHSV ? (int) h : (int) nh));
            gTextField.setText(String.valueOf(isRGB ? g : isHSV ? (int) s : (int) ns));
            bTextField.setText(String.valueOf(isRGB ? b : isHSV ? (int) v : (int) nl));
            hexTextField.setText(ColorUtil.colorToHex(makeColor()));
        } catch (Exception e) {

        }
    }

    private void updateColor(Color color) {
        updateColor(color, false);
    }

    // 更新 RGB 颜色
    private void updateColor(Color color, boolean sliderRequest) {
        updating = true;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        boolean isRGB = isRGB(), isHSV = isHSV(),isHSL=isHSL();
        // 针对 HSV 防止因转换误差带来的滑动条调整
        if (!isHSV || !sliderRequest) {
            HSV hsv = ColorUtil.colorToHsv(color);
            h = hsv.h;
            s = hsv.s;
            v = hsv.v;
        }
        if (!isHSL || !sliderRequest) {
            HSL hsl = ColorUtil.colorToHsl(color);
            nh = hsl.h;
            ns = hsl.s;
            nl = hsl.l;
        }
        if (isRGB) {
            rSlider.setValue(r);
            gSlider.setValue(g);
            bSlider.setValue(b);
        } else if (isHSV) {
            rSlider.setValue((int) h);
            gSlider.setValue((int) s);
            bSlider.setValue((int) v);
        } else {
            rSlider.setValue((int) nh);
            gSlider.setValue((int) ns);
            bSlider.setValue((int) nl);
        }
        updateUI();
        updating = false;
    }

    private void checkDocument(DocumentEvent e) {
        Document doc = e.getDocument();
        JTextField tf = null;
        boolean d1 = doc == rTextField.getDocument(), d2 = doc == gTextField.getDocument(),
                d3 = doc == bTextField.getDocument(), d4 = doc == hexTextField.getDocument();
        if (d1) tf = rTextField;
        else if (d2) tf = gTextField;
        else if (d3) tf = bTextField;
        else if (d4) {
            tf = hexTextField;
            String text = tf.getText();
            Color color = ColorUtil.hexToColor(text);
            if (color != null) updateColor(color);
            return;
        }

        String text = tf.getText();
        if (StringUtil.isEmpty(text)) return;
        int i = Integer.parseInt(text);
        boolean isRGB = isRGB(), isHSV = isHSV();
        if (d1) rSlider.setValue(isRGB ? (r = i) : isHSV ? (int) (h = i) : (int) (nh = i));
        else if (d2) gSlider.setValue(isRGB ? (g = i) : isHSV ? (int) (s = i) : (int) (ns = i));
        else if (d3) bSlider.setValue(isRGB ? (b = i) : isHSV ? (int) (v = i) : (int) (nl = i));

        if (!d4) hexTextField.setText(ColorUtil.colorToHex(makeColorFromRgb(r, g, b)));
        else updateUI();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!updating) checkDocument(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (!updating) checkDocument(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
