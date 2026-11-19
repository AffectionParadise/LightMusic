//package net.doge.ui.widget.list.renderer.core;
//
//import lombok.Data;
//import net.doge.constant.core.ui.Fonts;
//import net.doge.constant.core.ui.lyric.LyricAlignment;
//import net.doge.entity.core.lyric.Statement;
//import net.doge.ui.widget.label.ui.CustomLabelUI;
//import net.doge.ui.widget.list.renderer.base.CustomListCellRenderer;
//import net.doge.ui.widget.list.ui.CustomListUI;
//import net.doge.ui.widget.lyric.HighlightLyric;
//import net.doge.util.core.text.HtmlUtil;
//import net.doge.util.ui.ScaleUtil;
//
//import javax.swing.*;
//import javax.swing.border.Border;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//
/// **
// * @author Doge
// * @description
// * @date 2020/12/7
// */
//@Data
//public class LyricListRendererBackup extends CustomListCellRenderer {
//    private final Font defaultFont = Fonts.NORMAL_SUPER;
//    private Font shrinkFont = defaultFont;
//    private Font highlightFont = Fonts.NORMAL_BIG;
//    // 字体大小动画数组
//    private Font[] fontAnimation;
//    private int hIndex;
//    private int sIndex;
//    // 走过的歌词颜色
//    private Color highlightColor;
//    // 未走的歌词颜色
//    private Color bgColor;
//    // 高亮文字
//    private HighlightLyric hl;
//    // 比例
//    private double ratio;
//    // 高亮行
//    private int row = -1;
//    // 上一个高亮行
//    private int lRow = -1;
//
////    private CustomPanel outerPanel = new CustomPanel();
////    private CustomLabel lyricLabel = new CustomLabel();
//
//    // 高亮最大透明度
//    private final float highlightMaxAlpha = 1f;
//    // 普通歌词最大最小透明度
//    public final float normalMaxAlpha = 0.4f;
//    public final float normalMinAlpha = 0.05f;
//    private CustomLabelUI labelUI = new CustomLabelUI(normalMaxAlpha);
//    private Timer fontTimer;
//
//    private final int HORIZONTAL_INSET = ScaleUtil.scale(90);
//    private final int VERTICAL_INSET = ScaleUtil.scale(28);
//    private final Border BORDER = new EmptyBorder(VERTICAL_INSET, 0, VERTICAL_INSET, HORIZONTAL_INSET);
//
//    public LyricListRendererBackup() {
//        createFontAnimation();
//        fontTimer = new Timer(10, e -> {
//            // 高亮行字体增大
//            highlightFont = fontAnimation[hIndex++];
//            // 经过行字体减小
//            shrinkFont = fontAnimation[sIndex--];
//            if (sIndex < 0) fontTimer.stop();
//        });
////        init();
//    }
//
//    private void createFontAnimation() {
//        int hs = highlightFont.getSize(), ss = shrinkFont.getSize(), l = hs - ss + 1;
//        fontAnimation = new Font[l];
//        for (int i = 0; i < l; i++) fontAnimation[i] = shrinkFont.deriveFont((float) (ss++));
//    }
//
//    public void setRow(int row) {
//        this.ratio = 0;
//        lRow = this.row;
//        this.row = row;
//
//        highlightFont = fontAnimation[hIndex = 0];
//        shrinkFont = fontAnimation[sIndex = fontAnimation.length - 1];
//        if (!fontTimer.isRunning()) fontTimer.start();
//    }
//
////    private void init() {
////        outerPanel.setLayout(new GridLayout(1, 1));
////
////        outerPanel.add(lyricLabel);
////    }
//
//    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        JLabel label = (JLabel) component;
//
//        int maxWidth = list.getVisibleRect().width - HORIZONTAL_INSET;
//        if (maxWidth <= 0) return label;
//        list.setFixedCellWidth(maxWidth);
//
//        Statement statement = (Statement) value;
//        double time = statement.getTime();
//        String plainLyric = statement.getPlainLyric();
//
//        // 标签
//        label.setOpaque(false);
//        label.setBorder(BORDER);
//        label.setHorizontalAlignment(LyricAlignment.VALUES[LyricAlignment.lyricAlignmentIndex]);
//        label.setForeground(bgColor);
//        label.setUI(labelUI);
//        if (index == row) labelUI.setOpacity(highlightMaxAlpha);
//        else if (index < row) labelUI.setOpacity(normalMinAlpha);
//        else labelUI.setOpacity(normalMaxAlpha);
//
//        // 高亮的行的样式
//        if (index == row) {
//            label.setFont(highlightFont);
//            if (hl == null
//                    || hl.getWidthThreshold() != maxWidth
//                    || !hl.getLabelFont().equals(highlightFont)
//                    || hl.getTime() != time
//                    || !hl.getPlainLyric().equals(plainLyric)
//                    || !hl.getC1().equals(highlightColor)
//                    || !hl.getC2().equals(bgColor))
//                hl = new HighlightLyric(label, statement, highlightColor, bgColor, ratio, false, maxWidth, !fontTimer.isRunning());
//            else hl.setRatio(ratio);
//            label.setIcon(hl.getImgIcon());
//            label.setText("");
//        }
//        // 其他行的样式
//        else {
//            label.setFont(index == lRow ? shrinkFont : defaultFont);
//            // 对长歌词手动添加 <br> 换行，由于排版问题取消自动换行
//            label.setText(HtmlUtil.textToHtmlNoWrap(HtmlUtil.wrapLineByWidth(plainLyric, maxWidth, defaultFont.getSize())));
//            label.setIcon(null);
//        }
//        // 设置 list 对应行的高度
//        ((CustomListUI) list.getUI()).setCellHeight(index, getPreferredSize().height);
//
//        return label;
//    }
//
//    @Override
//    public Component getRootComponent() {
//        return null;
//    }
//}
