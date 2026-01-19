package net.doge.util.common;

import net.doge.constant.ui.Fonts;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Doge
 * @Description HTML 工具类
 * @Date 2020/12/15
 */
public class HtmlUtil {
    private static final Map<Character, String> cMap = new HashMap<>();

    static {
        cMap.put(' ', "&nbsp;");
        cMap.put('<', "&lt;");
        cMap.put('>', "&gt;");
        cMap.put('\n', "<br>");
    }

    /**
     * 从元素提取文本，替换 <br><li><p><dd> 为 \n
     *
     * @param parentElement
     * @return
     */
    public static String getPrettyText(Element parentElement) {
        if (parentElement == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Node child : parentElement.childNodes()) {
            if (child instanceof TextNode) {
                sb.append(((TextNode) child).text());
            } else if (child instanceof Element) {
                Element childElement = (Element) child;
                sb.append(getPrettyText(childElement));
                String s = childElement.tag().getName().toLowerCase();
                if ("br".equals(s) || "li".equals(s) || "p".equals(s) || "dd".equals(s)) sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtmlWithSpace(String text) {
        return textToHtml(text, true, false);
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtmlNoWrap(String text) {
        return textToHtml(text, false, true);
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text) {
        return textToHtml(text, false, false);
    }

    /**
     * 将无法显示的字符通过 HTML 换种字体显示，不替换空格
     *
     * @param text
     * @return
     */
    public static String textToHtml(String text, boolean withSpace, boolean noWrap) {
        if (text == null || text.startsWith("<html>") || text.trim().isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<html><div style=\"%sfont-family:%s\">", noWrap ? "white-space:nowrap;" : "", Fonts.NORMAL.getFontName(Locale.ENGLISH)));
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);
            if ((withSpace || chars[0] != ' ') && cMap.containsKey(chars[0])) {
                sb.append(cMap.get(chars[0]));
                continue;
            }
            for (int j = 0, l = Fonts.TYPES.size(); j < l; j++) {
                if (!Fonts.TYPES.get(j).canDisplay(codePoint)) continue;
                // 中文
                if (j == 0) sb.append(chars[0]);
                else
                    sb.append(String.format("<span style=\"font-family:%s\">%s</span>", Fonts.TYPES.get(j).getFontName(), str));
                i += chars.length - 1;
                break;
            }
        }
        sb.append("</div></html>");
        return sb.toString();
    }

    /**
     * 去掉字符串中所有 HTML 标签，并将转义后的符号还原
     *
     * @param s
     * @return
     */
    public static String removeHtmlLabel(String s) {
        if (s == null) return s;
        s = s.replaceAll("<br ?/?>", "\n");
        Pattern pattern = Pattern.compile("<[^>]+>");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&apos;", "'");
    }

    public static String wrapLineByWidth(String text, int thresholdWidth) {
        return wrapLineByWidth(text, thresholdWidth, Fonts.FONT_SIZE);
    }

    /**
     * 字符串宽度 thresholdWidth 加 <br/> 换行
     *
     * @param text
     * @param thresholdWidth
     * @return
     */
    public static String wrapLineByWidth(String text, int thresholdWidth, int fontSize) {
        if (thresholdWidth < 0) return text;
        StringBuilder sb = new StringBuilder();
        int sw = 0;
        JLabel label = new JLabel();
        for (int i = 0, len = text.length(); i < len; i++) {
            int codePoint = text.codePointAt(i);
            char[] chars = Character.toChars(codePoint);
            String str = new String(chars);

            for (int j = 0, l = Fonts.TYPES.size(); j < l; j++) {
                Font font = Fonts.TYPES.get(j);
                // 不同字体大小对应的字符宽度不同
                if (font.getSize() != fontSize) font = font.deriveFont((float) fontSize);
                if (!font.canDisplay(codePoint)) continue;
                if (chars[0] != '\n') {
                    int tw = label.getFontMetrics(font).stringWidth(str);
                    sw += tw;
                    if (sw >= thresholdWidth) {
                        sb.append('\n');
                        sw = tw;
                    }
                } else sw = 0;
                sb.append(str);
                i += chars.length - 1;
                break;
            }
        }
        return sb.toString();
    }
}
