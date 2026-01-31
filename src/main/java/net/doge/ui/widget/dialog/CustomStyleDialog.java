package net.doge.ui.widget.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import lombok.Getter;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.media.Format;
import net.doge.constant.core.os.SimplePath;
import net.doge.constant.core.ui.core.Colors;
import net.doge.entity.core.ui.UIStyle;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMStyleManager;
import net.doge.util.os.FileUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * @Author Doge
 * @Description 自定义样式的对话框
 * @Date 2020/12/15
 */
public class CustomStyleDialog extends AbstractTitledDialog implements DocumentListener {
    private final int WIDTH = ScaleUtil.scale(960);
    private final int HEIGHT = ScaleUtil.scale(750);
    private final int imgWidth = ScaleUtil.scale(150);
    private final int imgHeight = ScaleUtil.scale(120);
    private final int rectWidth = ScaleUtil.scale(170);
    private final int rectHeight = ScaleUtil.scale(30);
    private final String STYLE_NAME_NOT_NULL_MSG = I18n.getText("styleNameNotNullMsg");
    private final String STYLE_NAME_DUPLICATE_MSG = I18n.getText("styleNameDuplicateMsg");
    private final String IMG_FILE_NOT_EXIST_MSG = I18n.getText("imgFileNotExistMsg");
    private final String IMG_NOT_VALID_MSG = I18n.getText("imgNotValidMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomScrollPane centerScrollPane = new CustomScrollPane(centerPanel);
    private CustomPanel buttonPanel = new CustomPanel();

    private final CustomLabel[] labels = {
            new CustomLabel(I18n.getText("styleName")),
            new CustomLabel(I18n.getText("bgImg")),
            new CustomLabel(I18n.getText("foreColor")),
            new CustomLabel(I18n.getText("selectedColor")),
            new CustomLabel(I18n.getText("lyricTextColor")),
            new CustomLabel(I18n.getText("lyricHighlightColor")),
            new CustomLabel(I18n.getText("uiTextColor")),
            new CustomLabel(I18n.getText("timeBarColor")),
            new CustomLabel(I18n.getText("iconColor")),
            new CustomLabel(I18n.getText("scrollBarColor")),
            new CustomLabel(I18n.getText("volumeBarColor")),
            new CustomLabel(I18n.getText("spectrumColor"))
    };

    private final Component[] components = {
            new CustomTextField(15),
            new DialogButton(I18n.getText("browseImg")),
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
    private DialogButton pureColor;

    private DialogButton okButton;
    private DialogButton cancelButton;

    // 面板展示的样式
    private UIStyle showedStyle;

    @Getter
    private boolean confirmed = false;
    @Getter
    private Object[] results = new Object[components.length];

    // 父窗口，传入 OK 按钮文字，要展示的样式(添加则用当前样式，编辑则用选中样式)
    public CustomStyleDialog(MainFrame f, String okButtonText, UIStyle showedStyle) {
        super(f, I18n.getText("customStyleTitle"));
        this.showedStyle = showedStyle;

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton(okButtonText, textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);
        pureColor = new DialogButton(I18n.getText("solidColor"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(WIDTH, HEIGHT);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerScrollPane, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            // 主题名称不为空
            if ("".equals(results[0])) {
                new TipDialog(f, STYLE_NAME_NOT_NULL_MSG, true).showDialog();
                return;
            }
            // 主题名称不重复
            List<UIStyle> styles = f.styles;
            boolean isAdd = okButton.getPlainText().contains(I18n.getText("addStyle"));
            for (UIStyle style : styles) {
                // 添加时，名称一定不相等；编辑时，只允许同一样式名称相等
                if (style.getName().equals(results[0])
                        && (style != showedStyle || isAdd)) {
                    new TipDialog(f, STYLE_NAME_DUPLICATE_MSG, true).showDialog();
                    return;
                }
            }
            // 文件夹不存在就创建
            File dir = new File(SimplePath.CUSTOM_STYLE_PATH);
            FileUtil.mkDir(dir);
            // 图片路径
            if (results[1] instanceof String) {
                String str = (String) results[1];
                // 图片 key
                if (LMStyleManager.contains(str)) {
                    BufferedImage img = LMStyleManager.getImage(str);
                    String dest = SimplePath.CUSTOM_STYLE_PATH + System.currentTimeMillis() + "." + Format.JPG;
                    ImageUtil.toFile(img, dest);
                    // 设置路径
                    results[1] = dest;
                }
                // 图片路径
                else {
                    File imgFile = new File(str);
                    if (imgFile.exists()) {
                        String newPath = SimplePath.CUSTOM_STYLE_PATH + System.currentTimeMillis() + "." + FileUtil.getSuffix(imgFile);
                        FileUtil.copy(imgFile.getPath(), newPath);
                        // 设置新的路径
                        results[1] = newPath;
                        // 更新时删除原来的图片
                        String ik = showedStyle.getImgKey();
                        FileUtil.delete(ik);
                    } else {
                        new TipDialog(f, IMG_FILE_NOT_EXIST_MSG, true).showDialog();
                        return;
                    }
                }
            }
            confirmed = true;
            f.currDialogs.remove(this);
            dispose();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(globalPanel);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new GridLayout(6, 2));

        // 获得传入的界面样式
        results[0] = showedStyle.getName();
        String imgKey = showedStyle.getImgKey();
        results[1] = StringUtil.isEmpty(imgKey) ? showedStyle.getBgColor() : imgKey;
        results[2] = showedStyle.getForeColor();
        results[3] = showedStyle.getSelectedColor();
        results[4] = showedStyle.getLrcColor();
        results[5] = showedStyle.getHighlightColor();
        results[6] = showedStyle.getTextColor();
        results[7] = showedStyle.getTimeBarColor();
        results[8] = showedStyle.getIconColor();
        results[9] = showedStyle.getScrollBarColor();
        results[10] = showedStyle.getSliderColor();
        results[11] = showedStyle.getSpectrumColor();

        Border eb = new HDEmptyBorder(0, 20, 0, 20);

        Color textColor = f.currUIStyle.getTextColor();
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            CustomPanel panel = new CustomPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(eb);
            // 添加标签
            labels[i].setForeground(textColor);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof CustomTextField) {
                CustomTextField component = (CustomTextField) components[i];
                component.setForeground(textColor);
                component.setCaretColor(textColor);
                component.setSelectedTextColor(textColor);
                component.setSelectionColor(darkerTextAlphaColor);
                // 加载主题名称
                component.setText((String) results[i]);
                Document document = component.getDocument();
                // 添加文本改变监听器
                document.addDocumentListener(this);
            } else if (components[i] instanceof DialogButton) {
                DialogButton component = (DialogButton) components[i];
                component.setForeColor(textColor);
                labels[i].setHorizontalTextPosition(SwingConstants.LEFT);
                // 加载当前样式背景图(显示一个缩略图)
                if (results[i] != null) {
                    if (results[i] instanceof String) {
                        String ik = (String) results[i];
                        BufferedImage img = showedStyle.isPreDefined() ? LMStyleManager.getImage(ik) : ImageUtil.read(ik);
                        if (img != null) {
                            if (img.getWidth() >= img.getHeight())
                                labels[i].setIcon(new ImageIcon(ImageUtil.width(img, imgWidth)));
                            else labels[i].setIcon(new ImageIcon(ImageUtil.height(img, imgHeight)));
                        }
                    } else {
                        labels[i].setIcon(new ImageIcon(ImageUtil.width(ImageUtil.dyeRect(2, 1, (Color) results[i]), imgWidth)));
                    }
                }
                int finalI = i;
                // 图片文件选择
                component.addActionListener(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle(I18n.getText("chooseImg"));
                    ObservableList<FileChooser.ExtensionFilter> filters = fileChooser.getExtensionFilters();
                    // 添加可读取的图片格式
                    String allSuffix = "";
                    for (String suffix : Format.READ_IMAGE_TYPE_SUPPORTED) {
                        filters.add(new FileChooser.ExtensionFilter(suffix.toUpperCase(), "*." + suffix));
                        allSuffix += "*." + suffix + ";";
                    }
                    filters.add(0, new FileChooser.ExtensionFilter(I18n.getText("imgFile"), allSuffix));
                    Platform.runLater(() -> {
                        File file = fileChooser.showOpenDialog(null);
                        if (file == null) return;
                        String path = file.getPath();
                        results[finalI] = path;
                        BufferedImage img = ImageUtil.read(path);
                        if (img == null) {
                            new TipDialog(f, IMG_NOT_VALID_MSG, true).showDialog();
                            return;
                        }
                        if (img.getWidth() >= img.getHeight())
                            labels[finalI].setIcon(new ImageIcon(ImageUtil.width(img, imgWidth)));
                        else labels[finalI].setIcon(new ImageIcon(ImageUtil.height(img, imgHeight)));
                        setLocationRelativeTo(null);
                    });
                });
            } else if (components[i] instanceof CustomLabel) {
                CustomLabel component = (CustomLabel) components[i];
                // 鼠标光标
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // 获取主题颜色并显示成小方格
                component.setIcon(ImageUtil.dyeRoundRect(rectWidth, rectHeight, ((Color) results[i])));
                int finalI = i;
                // 颜色选择
                component.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            ColorChooserDialog d = new ColorChooserDialog(f, (Color) results[finalI]);
                            d.showDialog();
                            if (!d.isConfirmed()) return;
                            Color color = d.getResult();
                            // 更改方框内颜色并保存
                            component.setIcon(ImageUtil.dyeRoundRect(rectWidth, rectHeight, color));
                            results[finalI] = color;
                        }
                    }
                });
            }
            panel.add(components[i]);

            CustomPanel outer = new CustomPanel();
            outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
            outer.add(Box.createVerticalGlue());
            outer.add(panel);
            outer.add(Box.createVerticalGlue());

            centerPanel.add(outer);
        }

        // 纯色按钮
        pureColor.addActionListener(e -> {
            ColorChooserDialog d = new ColorChooserDialog(f, results[1] instanceof Color ? (Color) results[1] : Colors.THEME);
            d.showDialog();
            if (!d.isConfirmed()) return;
            Color color = d.getResult();
            // 更改方框内颜色并保存
            labels[1].setIcon(new ImageIcon(ImageUtil.width(ImageUtil.dyeRect(2, 1, color), imgWidth)));
            results[1] = color;
            setLocationRelativeTo(null);
        });
        ((CustomPanel) ((CustomPanel) centerPanel.getComponent(1)).getComponent(1)).add(pureColor);

        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        centerScrollPane.setHBarUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setVBarUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setBorder(new HDEmptyBorder(10, 0, 10, 0));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        results[0] = ((CustomTextField) components[0]).getText();
    }
}
