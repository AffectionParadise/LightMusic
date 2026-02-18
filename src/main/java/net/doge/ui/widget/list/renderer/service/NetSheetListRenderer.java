package net.doge.ui.widget.list.renderer.service;

import lombok.Data;
import net.doge.constant.core.ui.core.Fonts;
import net.doge.constant.core.ui.image.ImageConstants;
import net.doge.constant.core.ui.list.RendererConstants;
import net.doge.entity.service.NetSheetInfo;
import net.doge.ui.widget.box.CustomBox;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.util.core.StringUtil;
import net.doge.util.core.img.ImageUtil;
import net.doge.util.core.text.HtmlUtil;
import net.doge.util.lmdata.manager.LMIconManager;
import net.doge.util.ui.ScaleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Doge
 * @description
 * @date 2020/12/7
 */
@Data
public class NetSheetListRenderer extends CustomListCellRenderer {
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
        iconLabel.setIconTextGap(ScaleUtil.scale(0));

        difficultyLabel.setFont(tinyFont);
        musicKeyLabel.setFont(tinyFont);
        playVersionLabel.setFont(tinyFont);
        chordNameLabel.setFont(tinyFont);
        bpmLabel.setFont(tinyFont);
        pageSizeLabel.setFont(tinyFont);

        float opacity = 0.5f;
        difficultyLabel.setOpacity(opacity);
        musicKeyLabel.setOpacity(opacity);
        playVersionLabel.setOpacity(opacity);
        chordNameLabel.setOpacity(opacity);
        bpmLabel.setOpacity(opacity);
        pageSizeLabel.setOpacity(opacity);

        int sh = ScaleUtil.scale(10);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(iconLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(nameLabel);
        outerPanel.add(CustomBox.createVerticalGlue());
        outerPanel.add(difficultyLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(musicKeyLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(playVersionLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(chordNameLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(bpmLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
        outerPanel.add(pageSizeLabel);
        outerPanel.add(CustomBox.createVerticalStrut(sh));
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

        int pw = RendererConstants.CELL_WIDTH, tw = RendererConstants.TEXT_WIDTH;
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

    @Override
    public Component getRootComponent() {
        return outerPanel;
    }
}