package net.doge.ui.widget.list.renderer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetSheetInfo;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.HtmlUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.lmdata.manager.LMIconManager;
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
public class NetSheetListRenderer extends DefaultListCellRenderer {
    private final Font tinyFont = Fonts.NORMAL_TINY;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private CustomPanel outerPanel = new CustomPanel();
    private CustomLabel iconLabel = new CustomLabel();
    private CustomLabel nameLabel = new CustomLabel();
    private CustomLabel difficultyLabel = new CustomLabel();
    private CustomLabel musicKeyLabel = new CustomLabel();
    private CustomLabel playVersionLabel = new CustomLabel();
    private CustomLabel chordNameLabel = new CustomLabel();
    private CustomLabel bpmLabel = new CustomLabel();
    private CustomLabel pageSizeLabel = new CustomLabel();

    private static ImageIcon sheetIcon = new ImageIcon(ImageUtil.width(LMIconManager.getImage("list.sheetItem"), ImageConstants.MEDIUM_WIDTH));

    public NetSheetListRenderer() {
        init();
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        sheetIcon = ImageUtil.dye(sheetIcon, iconColor);
    }

    private void init() {
        iconLabel.setIconTextGap(0);

        difficultyLabel.setFont(tinyFont);
        musicKeyLabel.setFont(tinyFont);
        playVersionLabel.setFont(tinyFont);
        chordNameLabel.setFont(tinyFont);
        bpmLabel.setFont(tinyFont);
        pageSizeLabel.setFont(tinyFont);

        float alpha = 0.5f;
        difficultyLabel.setInstantAlpha(alpha);
        musicKeyLabel.setInstantAlpha(alpha);
        playVersionLabel.setInstantAlpha(alpha);
        chordNameLabel.setInstantAlpha(alpha);
        bpmLabel.setInstantAlpha(alpha);
        pageSizeLabel.setInstantAlpha(alpha);

        int sh = 10;
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

        outerPanel.setInstantDrawBg(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NetSheetInfo sheetInfo = (NetSheetInfo) value;

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

        BoxLayout layout = new BoxLayout(outerPanel, BoxLayout.Y_AXIS);
        outerPanel.setLayout(layout);

        int pw = RendererConstants.CELL_WIDTH, tw = pw - 20;
        String source = "<html></html>";
        String name = HtmlUtil.textToHtml(HtmlUtil.wrapLineByWidth(StringUtil.shorten(sheetInfo.getName(), RendererConstants.STRING_MAX_LENGTH), tw));
        String difficulty = sheetInfo.hasDifficulty() ? HtmlUtil.textToHtml(sheetInfo.getDifficulty() + "难度") : "";
        String musicKey = sheetInfo.hasMusicKey() ? HtmlUtil.textToHtml(sheetInfo.getMusicKey() + "调") : "";
        String playVersion = sheetInfo.hasPlayVersion() ? HtmlUtil.textToHtml(sheetInfo.getPlayVersion()) : "";
        String chordName = sheetInfo.hasChordName() ? HtmlUtil.textToHtml(sheetInfo.getChordName()) : "";
        String bpm = sheetInfo.hasBpm() ? HtmlUtil.textToHtml(sheetInfo.getBpm() + " 拍/分钟") : "";
        String pageSize = sheetInfo.hasPageSize() ? HtmlUtil.textToHtml(sheetInfo.getPageSize() + " 页") : "";

        iconLabel.setText(source);
        nameLabel.setText(name);
        difficultyLabel.setText(difficulty);
        musicKeyLabel.setText(musicKey);
        playVersionLabel.setText(playVersion);
        chordNameLabel.setText(chordName);
        bpmLabel.setText(bpm);
        pageSizeLabel.setText(pageSize);

        list.setFixedCellWidth(pw);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }
}