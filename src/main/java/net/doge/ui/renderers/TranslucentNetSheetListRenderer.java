package net.doge.ui.renderers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.constants.Fonts;
import net.doge.constants.ImageConstants;
import net.doge.constants.NetMusicSource;
import net.doge.constants.SimplePath;
import net.doge.models.NetSheetInfo;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.CustomPanel;
import net.doge.utils.ImageUtils;
import net.doge.utils.StringUtils;

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
public class TranslucentNetSheetListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;
    private boolean drawBg;
    private int hoverIndex = -1;

    private ImageIcon sheetIcon = new ImageIcon(ImageUtils.width(ImageUtils.read(SimplePath.ICON_PATH + "sheetItem.png"), ImageConstants.profileWidth));
    private ImageIcon sheetSIcon;

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
        sheetIcon = ImageUtils.dye(sheetIcon, foreColor);
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        sheetSIcon = ImageUtils.dye(sheetIcon, selectedColor);
    }

    public void setDrawBg(boolean drawBg) {
        this.drawBg = drawBg;
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
        iconLabel.setIcon(netSheetInfo.hasCoverImg() ? new ImageIcon(netSheetInfo.getCoverImg()) : isSelected ? sheetSIcon : sheetIcon);

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(isSelected ? selectedColor : foreColor);
        nameLabel.setForeground(isSelected ? selectedColor : foreColor);
        difficultyLabel.setForeground(isSelected ? selectedColor : foreColor);
        musicKeyLabel.setForeground(isSelected ? selectedColor : foreColor);
        playVersionLabel.setForeground(isSelected ? selectedColor : foreColor);
        chordNameLabel.setForeground(isSelected ? selectedColor : foreColor);
        bpmLabel.setForeground(isSelected ? selectedColor : foreColor);
        pageSizeLabel.setForeground(isSelected ? selectedColor : foreColor);

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
        String source = StringUtils.textToHtml(NetMusicSource.names[netSheetInfo.getSource()]);
        String name = StringUtils.textToHtml(StringUtils.wrapLineByWidth(netSheetInfo.getName(), maxWidth));
        String difficulty = netSheetInfo.hasDifficulty() ? StringUtils.textToHtml(netSheetInfo.getDifficulty()) : "";
        String musicKey = netSheetInfo.hasMusicKey() ? StringUtils.textToHtml(netSheetInfo.getMusicKey()) : "";
        String playVersion = netSheetInfo.hasPlayVersion() ? StringUtils.textToHtml(netSheetInfo.getPlayVersion()) : "";
        String chordName = netSheetInfo.hasChordName() ? StringUtils.textToHtml(netSheetInfo.getChordName()) : "";
        String bpm = netSheetInfo.hasBpm() ? StringUtils.textToHtml(netSheetInfo.getBpm() + " 拍/分钟") : "";
        String pageSize = netSheetInfo.hasPageSize() ? StringUtils.textToHtml(netSheetInfo.getPageSize() + " 页") : "";

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
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 16, 50));
        outerPanel.setPreferredSize(d);
        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setDrawBg(isSelected || hoverIndex == index);

        return outerPanel;
    }

    @Override
    public void paintComponent(Graphics g) {
        // 画背景
        if (drawBg) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getForeground());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            // 注意这里不能用 getVisibleRect ！！！
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        super.paintComponent(g);
    }
}
