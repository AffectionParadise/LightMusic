package net.doge.ui.widget.dialog;

import com.mpatric.mp3agic.ID3v1Genres;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.doge.constant.lang.I18n;
import net.doge.constant.system.Format;
import net.doge.constant.ui.Colors;
import net.doge.model.entity.AudioFile;
import net.doge.model.entity.MediaInfo;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.StringComboBoxUI;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.common.DurationUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import net.doge.util.media.MediaUtil;
import net.doge.util.system.FileUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @Author Doge
 * @Description 编辑歌曲信息的对话框
 * @Date 2020/12/15
 */
public class EditInfoDialog extends AbstractTitledDialog {
    private final int imgWidth = 120;
    private final int imgHeight = 120;

    // 文件正在使用提示
    private final String FILE_USED_MSG = I18n.getText("fileUsedMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomScrollPane centerScrollPane = new CustomScrollPane(centerPanel);
    private CustomPanel buttonPanel = new CustomPanel();

    private final CustomLabel[] labels = {
            new CustomLabel(I18n.getText("fileName")),
            new CustomLabel(I18n.getText("fileSize")),
            new CustomLabel(I18n.getText("creationTime")),
            new CustomLabel(I18n.getText("modificationTime")),
            new CustomLabel(I18n.getText("accessTime")),
            new CustomLabel(I18n.getText("duration")),
            new CustomLabel(I18n.getText("fileTitle")),
            new CustomLabel(I18n.getText("fileArtist")),
            new CustomLabel(I18n.getText("fileAlbum")),
            new CustomLabel(I18n.getText("genre")),
            new CustomLabel(I18n.getText("fileComment")),
            new CustomLabel(I18n.getText("copyright")),
            new CustomLabel(I18n.getText("coverImg"))
    };

    private CustomComboBox<String> comboBox = new CustomComboBox<>();
    private final int columns = 20;
    private final Component[] components = {
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            comboBox,
            new CustomTextField(columns),
            new CustomTextField(columns),
            new DialogButton(I18n.getText("browseImg"))
    };

    private DialogButton okButton;
    private DialogButton cancelButton;

    // 面板展示的文件
    private AudioFile file;

    private Object[] results = new Object[components.length];

    // 父窗口，传入要展示的文件
    public EditInfoDialog(MainFrame f, AudioFile file) {
        super(f, I18n.getText("editInfoTitle"));
        this.file = file;

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);

        comboBox.addItem("");
        for (String genre : ID3v1Genres.GENRES) comboBox.addItem(genre);
    }

    public void showDialog() {
        setResizable(false);
        setSize(960, 750);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();

        globalPanel.add(centerScrollPane, BorderLayout.CENTER);
        okButton.addActionListener(e -> {
            if (f.player.loadedAudioFile(file)) {
                new TipDialog(f, FILE_USED_MSG, true).showDialog();
                return;
            } else {
                for (int i = 0, size = labels.length; i < size; i++) {
                    if (components[i] instanceof CustomTextField) {
                        results[i] = ((CustomTextField) components[i]).getText();
                    }
                }
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setTitle((String) results[6]);
                mediaInfo.setArtist((String) results[7]);
                mediaInfo.setAlbum((String) results[8]);
                mediaInfo.setGenre((String) results[9]);
                mediaInfo.setComment((String) results[10]);
                mediaInfo.setCopyright((String) results[11]);
                mediaInfo.setAlbumImage((BufferedImage) results[12]);
                mediaInfo.setFormat(file.getFormat());
                MediaUtil.writeAudioFileInfo(file.getAbsolutePath(), mediaInfo);
                // 歌曲信息更改后重新填充
                MediaUtil.fillAudioFileInfo(file);
            }
            close();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
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
        centerPanel.setLayout(new GridLayout(7, 2));

        // 获得传入的歌曲信息
        String fileName = StringUtil.wrapLineByWidth(file.getName(), 300);
        String fileSize = FileUtil.getUnitString(FileUtil.size(file));
        String creationTime = TimeUtil.msToDatetime(FileUtil.getCreationTime(file));
        String lastModifiedTime = TimeUtil.msToDatetime(file.lastModified());
        String lastAccessTime = TimeUtil.msToDatetime(FileUtil.getAccessTime(file));
        String duration = DurationUtil.format(file.getDuration());
        String title = file.getSongName();
        String artist = file.getArtist();
        String album = file.getAlbum();
        String genre = MediaUtil.getGenre(file);
        String comment = MediaUtil.getComment(file);
        String copyright = MediaUtil.getCopyright(file);
        BufferedImage albumImage = MediaUtil.getAlbumImage(file);

        results[0] = fileName;
        results[1] = fileSize;
        results[2] = creationTime;
        results[3] = lastModifiedTime;
        results[4] = lastAccessTime;
        results[5] = duration;
        results[6] = title;
        results[7] = artist;
        results[8] = album;
        results[9] = genre;
        results[10] = comment;
        results[11] = copyright;
        results[12] = albumImage;

        Border b = BorderFactory.createEmptyBorder(0, 20, 0, 20);

        Color textColor = f.currUIStyle.getTextColor();
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            CustomPanel panel = new CustomPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(b);
            // 添加标签
            labels[i].setForeground(textColor);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof CustomLabel) {
                CustomLabel component = (CustomLabel) components[i];
                component.setForeground(textColor);
                component.setText(StringUtil.textToHtml((String) results[i]));
            } else if (components[i] instanceof CustomTextField) {
                CustomTextField component = (CustomTextField) components[i];
                component.setForeground(textColor);
                component.setCaretColor(textColor);
                component.setSelectedTextColor(textColor);
                component.setSelectionColor(darkerTextAlphaColor);
                component.setText((String) results[i]);
            } else if (components[i] instanceof CustomComboBox) {
                CustomComboBox component = (CustomComboBox) components[i];
                // 下拉框 UI
                component.setUI(new StringComboBoxUI(component, f));

                int finalI = i;
                component.addItemListener(e -> {
                    results[finalI] = e.getItem().toString();
                });
                component.setSelectedItem(results[i]);
            } else if (components[i] instanceof DialogButton) {
                DialogButton component = (DialogButton) components[i];
                component.setForeColor(textColor);
                labels[i].setHorizontalTextPosition(SwingConstants.LEFT);
                // 加载封面图片(显示一个缩略图)
                if (results[i] != null) {
                    BufferedImage image = (BufferedImage) results[i];
                    if (image.getWidth() >= image.getHeight())
                        labels[i].setIcon(new ImageIcon(ImageUtil.width(image, imgWidth)));
                    else labels[i].setIcon(new ImageIcon(ImageUtil.height(image, imgHeight)));
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
                        if (file != null) {
                            BufferedImage image = ImageUtil.read(file);
                            results[finalI] = image;
                            if (image.getWidth() >= image.getHeight())
                                labels[finalI].setIcon(new ImageIcon(ImageUtil.width(image, imgWidth)));
                            else labels[finalI].setIcon(new ImageIcon(ImageUtil.height(image, imgHeight)));
                            setLocationRelativeTo(null);
                        }
                    });
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

        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        centerScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        centerScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }
}
