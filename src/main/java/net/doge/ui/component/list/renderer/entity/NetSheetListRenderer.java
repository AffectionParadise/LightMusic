package net.doge.ui.component.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constant.ui.Fonts;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.ui.RendererConstants;
import net.doge.model.entity.NetSheetInfo;
import net.doge.ui.component.label.CustomLabel;
import net.doge.ui.component.panel.CustomPanel;
import net.doge.util.common.StringUtil;
import net.doge.util.lmdata.LMIconManager;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author Doge
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetSheetListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon sheetIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.sheetItem"), ImageConstants.MEDIUM_WIDTH));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        sheetIcon = ImageUtil.dye(sheetIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetSheetInfo sheetInfo = (NetSheetInfo) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel();
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel difficultyLabel = new CustomLabel();
        CustomLabel musicKeyLabel = new CustomLabel();
        CustomLabel playVersionLabel = new CustomLabel();
        CustomLabel chordNameLabel = new CustomLabel();
        CustomLabel bpmLabel = new CustomLabel();
        CustomLabel pageSizeLabel = new CustomLabel();

        iconLabel.setIconTextGap(0);
        iconLabel.setIcon(sheetInfo.hasCoverImg() ? new ImageIcon(sheetInfo.getCoverImg()) : sheetIcon);

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
        difficultyLabel.setFont(tinyFont);
        musicKeyLabel.setFont(tinyFont);
        playVersionLabel.setFont(tinyFont);
        chordNameLabel.setFont(tinyFont);
        bpmLabel.setFont(tinyFont);
        pageSizeLabel.setFont(tinyFont);

        final float alpha = 0.5f;
        difficultyLabel.setBluntAlpha(alpha);
        musicKeyLabel.setBluntAlpha(alpha);
        playVersionLabel.setBluntAlpha(alpha);
        chordNameLabel.setBluntAlpha(alpha);
        bpmLabel.setBluntAlpha(alpha);
        pageSizeLabel.setBluntAlpha(alpha);

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        final int sh = 10;
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(Box.createVerticalGlue());
        outerPanel.add(difficultyLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(musicKeyLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(playVersionLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(chordNameLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(bpmLabel);
        outerPanel.add(Box.createVerticalStrut(sh));
        outerPanel.add(pageSizeLabel);
        outerPanel.add(Box.createVerticalStrut(sh));

        final int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(StringUtil.shorten(sheetInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String difficulty = sheetInfo.hasDifficulty() ? StringUtil.textToHtml(sheetInfo.getDifficulty() + "难度") : "";
        String musicKey = sheetInfo.hasMusicKey() ? StringUtil.textToHtml(sheetInfo.getMusicKey() + "调") : "";
        String playVersion = sheetInfo.hasPlayVersion() ? StringUtil.textToHtml(sheetInfo.getPlayVersion()) : "";
        String chordName = sheetInfo.hasChordName() ? StringUtil.textToHtml(sheetInfo.getChordName()) : "";
        String bpm = sheetInfo.hasBpm() ? StringUtil.textToHtml(sheetInfo.getBpm() + " 拍/分钟") : "";
        String pageSize = sheetInfo.hasPageSize() ? StringUtil.textToHtml(sheetInfo.getPageSize() + " 页") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        difficultyLabel.setText(difficulty);
        musicKeyLabel.setText(musicKey);
        playVersionLabel.setText(playVersion);
        chordNameLabel.setText(chordName);
        bpmLabel.setText(bpm);
        pageSizeLabel.setText(pageSize);

        list.setFixedCellWidth(pw);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}