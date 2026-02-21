package net.doge.ui.widget.dialog;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import net.doge.constant.core.lang.I18n;
import net.doge.constant.core.os.Format;
import net.doge.constant.core.ui.style.UIStyleStorage;
import net.doge.entity.core.ui.UIStyle;
import net.doge.entity.service.AudioFile;
import net.doge.entity.service.MediaInfo;
import net.doge.ui.MainFrame;
import net.doge.ui.core.layout.HDFlowLayout;
import net.doge.ui.widget.border.HDEmptyBorder;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.dialog.base.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.textarea.CustomTextArea;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.io.FileUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.core.time.TimeUtil;
import net.doge.util.media.DurationUtil;
import net.doge.util.media.MediaUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Doge
 * @description 编辑歌曲信息的对话框
 * @date 2020/12/15
 */
public class EditInfoDialog extends AbstractTitledDialog {
    private final int WIDTH = ScaleUtil.scale(960);
    private final int HEIGHT = ScaleUtil.scale(750);
    private final int imgWidth = ScaleUtil.scale(120);
    private final int imgHeight = ScaleUtil.scale(120);

    // 文件正在使用提示
    private final String FILE_USED_MSG = I18n.getText("fileUsedMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomScrollPane centerScrollPane = new CustomScrollPane(centerPanel);
    private CustomPanel buttonPanel = new CustomPanel();

    private final CustomLabel[] labels = {
            new CustomLabel(I18n.getText("fileName")),
            new CustomLabel(I18n.getText("filePath")),
            new CustomLabel(I18n.getText("fileSize")),
            new CustomLabel(I18n.getText("creationTime")),
            new CustomLabel(I18n.getText("modificationTime")),
            new CustomLabel(I18n.getText("accessTime")),
            new CustomLabel(I18n.getText("fileDuration")),
            new CustomLabel(I18n.getText("fileTitle")),
            new CustomLabel(I18n.getText("fileArtist")),
            new CustomLabel(I18n.getText("fileAlbum")),
            new CustomLabel(I18n.getText("coverImg")),
            new CustomLabel(I18n.getText("fileGenre")),
            new CustomLabel(I18n.getText("fileLyrics")),
            new CustomLabel(I18n.getText("fileLyricist")),
            new CustomLabel(I18n.getText("fileYear")),
            new CustomLabel(I18n.getText("fileRating")),
            new CustomLabel(I18n.getText("fileBpm")),
            new CustomLabel(I18n.getText("fileKey")),
            new CustomLabel(I18n.getText("fileComment")),
            new CustomLabel(I18n.getText("fileRecordLabel")),
            new CustomLabel(I18n.getText("fileMood")),
            new CustomLabel(I18n.getText("fileOccasion")),
            new CustomLabel(I18n.getText("fileLanguage")),
            new CustomLabel(I18n.getText("fileCountry")),
            new CustomLabel(I18n.getText("fileVersion")),
            new CustomLabel(I18n.getText("fileCopyright")),
    };

    //    private CustomComboBox<String> comboBox = new CustomComboBox<>();
    private final int rows = 5, columns = 20;
    private final Component[] components = {
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomLabel(),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new DialogButton(I18n.getText("browseImg"), UIStyleStorage.currUIStyle.getTextColor()),
//            comboBox,
            new CustomTextField(columns),
            new CustomScrollPane(new CustomTextArea(rows, columns)),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns),
            new CustomTextField(columns)
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

        Color textColor = UIStyleStorage.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);

//        comboBox.addItem("");
//        for (String genre : ID3v1Genres.GENRES) comboBox.addItem(genre);
    }

    public void showDialog() {
        setSize(WIDTH, HEIGHT);

        initTitleBar();
        initView();

        setLocationRelativeTo(null);
        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        centerPanel.setLayout(new GridLayout(13, 2));

        // 获得传入的歌曲信息
        String fileName = HtmlUtil.wrapLineByWidth(file.getName(), ScaleUtil.scale(300));
        String filePath = HtmlUtil.wrapLineByWidth(file.getParent(), ScaleUtil.scale(300));
        String fileSize = FileUtil.getUnitString(FileUtil.size(file));
        String creationTime = TimeUtil.msToDatetime(FileUtil.getCreationTime(file));
        String lastModifiedTime = TimeUtil.msToDatetime(file.lastModified());
        String lastAccessTime = TimeUtil.msToDatetime(FileUtil.getAccessTime(file));
        String duration = DurationUtil.format(file.getDuration());
        String title = file.getSongName();
        String artist = file.getArtist();
        String album = file.getAlbum();
        BufferedImage albumImage = MediaUtil.getAlbumImage(file);
        MediaInfo extraMediaInfo = MediaUtil.getExtraMediaInfo(file);
        String genre = extraMediaInfo.getGenre();
        String lyrics = extraMediaInfo.getLyrics();
        String lyricist = extraMediaInfo.getLyricist();
        String year = extraMediaInfo.getYear();
        String rating = extraMediaInfo.getRating();
        String bpm = extraMediaInfo.getBpm();
        String key = extraMediaInfo.getKey();
        String comment = extraMediaInfo.getComment();
        String recordLabel = extraMediaInfo.getRecordLabel();
        String mood = extraMediaInfo.getMood();
        String occasion = extraMediaInfo.getOccasion();
        String language = extraMediaInfo.getLanguage();
        String country = extraMediaInfo.getCountry();
        String version = extraMediaInfo.getVersion();
        String copyright = extraMediaInfo.getCopyright();

        results[0] = fileName;
        results[1] = filePath;
        results[2] = fileSize;
        results[3] = creationTime;
        results[4] = lastModifiedTime;
        results[5] = lastAccessTime;
        results[6] = duration;
        results[7] = title;
        results[8] = artist;
        results[9] = album;
        results[10] = albumImage;
        results[11] = genre;
        results[12] = lyrics;
        results[13] = lyricist;
        results[14] = year;
        results[15] = rating;
        results[16] = bpm;
        results[17] = key;
        results[18] = comment;
        results[19] = recordLabel;
        results[20] = mood;
        results[21] = occasion;
        results[22] = language;
        results[23] = country;
        results[24] = version;
        results[25] = copyright;

        Border b = new HDEmptyBorder(0, 20, 0, 20);

        UIStyle style = UIStyleStorage.currUIStyle;
        Color textColor = style.getTextColor();
        Color darkerTextAlphaColor = ColorUtil.deriveAlpha(ColorUtil.darker(textColor), 0.5f);
        for (int i = 0, size = labels.length; i < size; i++) {
            // 左对齐容器
            CustomPanel panel = new CustomPanel(new HDFlowLayout(HDFlowLayout.LEFT));
            panel.setBorder(b);
            // 添加标签
            labels[i].setForeground(textColor);
            panel.add(labels[i]);
            // 组件配置
            if (components[i] instanceof CustomLabel) {
                CustomLabel label = (CustomLabel) components[i];
                label.setForeground(textColor);
                label.setText(HtmlUtil.textToHtml((String) results[i]));
            } else if (components[i] instanceof CustomTextField) {
                CustomTextField textField = (CustomTextField) components[i];
                textField.setForeground(textColor);
                textField.setCaretColor(textColor);
                textField.setSelectedTextColor(textColor);
                textField.setSelectionColor(darkerTextAlphaColor);
                textField.setText((String) results[i]);
            } else if (components[i] instanceof CustomScrollPane) {
                CustomScrollPane sp = (CustomScrollPane) components[i];
                sp.setBorder(new HDEmptyBorder(10, 0, 10, 0));

                CustomTextArea textArea = (CustomTextArea) sp.getViewportView();
                textArea.setForeground(textColor);
                textArea.setCaretColor(textColor);
                textArea.setSelectedTextColor(textColor);
                textArea.setSelectionColor(darkerTextAlphaColor);
                textArea.setText((String) results[i]);
            }
//            else if (components[i] instanceof CustomComboBox) {
//                CustomComboBox comboBox = (CustomComboBox) components[i];
//                // 下拉框 UI
//                comboBox.setUI(new StringComboBoxUI(comboBox, f));
//
//                int finalI = i;
//                comboBox.addItemListener(e -> {
//                    results[finalI] = e.getItem().toString();
//                });
//                comboBox.setSelectedItem(results[i]);
//            }
            else if (components[i] instanceof DialogButton) {
                CustomLabel label = labels[i];
                label.setHorizontalTextPosition(SwingConstants.LEFT);
                boolean hasAlbumImg = results[i] != null;
                int finalI = i;
                // 右键清除封面图片
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (!hasAlbumImg) return;
                            results[finalI] = null;
                            label.setIcon(null);
                        }
                    }
                });
                // 加载封面图片(显示一个缩略图)
                if (hasAlbumImg) {
                    BufferedImage image = (BufferedImage) results[i];
                    if (image.getWidth() >= image.getHeight())
                        label.setIcon(new ImageIcon(ImageUtil.width(image, imgWidth)));
                    else label.setIcon(new ImageIcon(ImageUtil.height(image, imgHeight)));
                }
                DialogButton dialogButton = (DialogButton) components[i];
                // 图片文件选择
                dialogButton.addActionListener(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle(I18n.getText("chooseImg"));
                    ObservableList<FileChooser.ExtensionFilter> filters = fileChooser.getExtensionFilters();
                    // 添加可读取的图片格式
                    StringBuilder allSuffix = new StringBuilder();
                    for (String suffix : Format.READ_IMAGE_TYPE_SUPPORTED) {
                        filters.add(new FileChooser.ExtensionFilter(suffix.toUpperCase(), "*." + suffix));
                        allSuffix.append("*.").append(suffix).append(";");
                    }
                    filters.add(0, new FileChooser.ExtensionFilter(I18n.getText("imgFile"), allSuffix.toString()));
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
            outer.add(CustomBox.createVerticalGlue());
            outer.add(panel);
            outer.add(CustomBox.createVerticalGlue());

            centerPanel.add(outer);
        }
        centerScrollPane.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        globalPanel.add(centerScrollPane, BorderLayout.CENTER);

        okButton.addActionListener(e -> {
            if (f.player.loadedAudioFile(file)) {
                new TipDialog(f, FILE_USED_MSG, true).showDialog();
                return;
            } else {
                for (int i = 0, size = labels.length; i < size; i++) {
                    if (components[i] instanceof CustomTextField) {
                        results[i] = ((CustomTextField) components[i]).getText();
                    } else if (components[i] instanceof CustomScrollPane) {
                        CustomScrollPane sp = (CustomScrollPane) components[i];
                        CustomTextArea textArea = (CustomTextArea) sp.getViewportView();
                        results[i] = textArea.getText();
                    }
                }
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setTitle((String) results[7]);
                mediaInfo.setArtist((String) results[8]);
                mediaInfo.setAlbum((String) results[9]);
                mediaInfo.setAlbumImage((BufferedImage) results[10]);
                mediaInfo.setGenre((String) results[11]);
                mediaInfo.setLyrics((String) results[12]);
                mediaInfo.setLyricist((String) results[13]);
                mediaInfo.setYear((String) results[14]);
                mediaInfo.setRating((String) results[15]);
                mediaInfo.setBpm((String) results[16]);
                mediaInfo.setKey((String) results[17]);
                mediaInfo.setComment((String) results[18]);
                mediaInfo.setRecordLabel((String) results[19]);
                mediaInfo.setMood((String) results[20]);
                mediaInfo.setOccasion((String) results[21]);
                mediaInfo.setLanguage((String) results[22]);
                mediaInfo.setCountry((String) results[23]);
                mediaInfo.setVersion((String) results[24]);
                mediaInfo.setCopyright((String) results[25]);
                mediaInfo.setFormat(file.getFormat());
                MediaUtil.writeAudioFileInfo(file, mediaInfo);
                // 歌曲信息更改后重新填充
                MediaUtil.fillAudioFileInfo(file);
            }
            close();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(new HDEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
}
