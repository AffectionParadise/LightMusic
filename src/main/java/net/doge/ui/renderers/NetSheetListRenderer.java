package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.entity.NetSheetInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.utils.ImageUtil;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetSheetListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private ImageIcon sheetIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "sheetItem.png"), ImageConstants.profileWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        sheetIcon = ImageUtil.dye(sheetIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetSheetInfo netSheetInfo = (NetSheetInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel difficultyLabel = new CustomLabel();
        CustomLabel musicKeyLabel = new CustomLabel();
        CustomLabel playVersionLabel = new CustomLabel();
        CustomLabel chordNameLabel = new CustomLabel();
        CustomLabel bpmLabel = new CustomLabel();
        CustomLabel pageSizeLabel = new CustomLabel();

        iconLabel.setHorizontalTextPosition(LEFT);
        iconLabel.setIconTextGap(15);
        iconLabel.setIcon(netSheetInfo.hasCoverImg() ? new ImageIcon(netSheetInfo.getCoverImg()) : sheetIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        difficultyLabel.setForeground(textColor);
        musicKeyLabel.setForeground(textColor);
        playVersionLabel.setForeground(textColor);
        chordNameLabel.setForeground(textColor);
        bpmLabel.setForeground(textColor);
        pageSizeLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        difficultyLabel.setFont(customFont);
        musicKeyLabel.setFont(customFont);
        playVersionLabel.setFont(customFont);
        chordNameLabel.setFont(customFont);
        bpmLabel.setFont(customFont);
        pageSizeLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(difficultyLabel);
        outerPanel.add(musicKeyLabel);
        outerPanel.add(playVersionLabel);
        outerPanel.add(chordNameLabel);
        outerPanel.add(bpmLabel);
        outerPanel.add(pageSizeLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String source = StringUtil.textToHtml(NetMusicSource.names[netSheetInfo.getSource()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(netSheetInfo.getName(), maxWidth));
        String difficulty = netSheetInfo.hasDifficulty() ? StringUtil.textToHtml(netSheetInfo.getDifficulty() + "难度") : "";
        String musicKey = netSheetInfo.hasMusicKey() ? StringUtil.textToHtml(netSheetInfo.getMusicKey() + "调") : "";
        String playVersion = netSheetInfo.hasPlayVersion() ? StringUtil.textToHtml(netSheetInfo.getPlayVersion()) : "";
        String chordName = netSheetInfo.hasChordName() ? StringUtil.textToHtml(netSheetInfo.getChordName()) : "";
        String bpm = netSheetInfo.hasBpm() ? StringUtil.textToHtml(netSheetInfo.getBpm() + " 拍/分钟") : "";
        String pageSize = netSheetInfo.hasPageSize() ? StringUtil.textToHtml(netSheetInfo.getPageSize() + " 页") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        difficultyLabel.setText(difficulty);
        musicKeyLabel.setText(musicKey);
        playVersionLabel.setText(playVersion);
        chordNameLabel.setText(chordName);
        bpmLabel.setText(bpm);
        pageSizeLabel.setText(pageSize);

        Dimension ps = iconLabel.getPreferredSize();
        Dimension ps2 = nameLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 12, 46));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}
