package net.doge.constants;

import net.doge.utils.FontUtils;

import java.awt.*;

/**
 * @Author yzx
 * @Description 字体
 * @Date 2021/1/3
 */
public class Fonts {
    private static final int FONT_SIZE = 15;
    private static final int MEDIUM_SIZE = 18;
    private static final int BIG_SIZE = FONT_SIZE + 10;
    public static final int HUGE_SIZE = BIG_SIZE + 20;

    private static final String NORMAL_NAME = "normal.ttf";
    public static final String NORMAL_BOLD_NAME = "normal.ttf";

    // 中英文
    public static final Font NORMAL = FontUtils.loadFont(SimplePath.FONT_PATH + NORMAL_NAME, FONT_SIZE);
    public static final Font NORMAL_MEDIUM = FontUtils.loadFont(SimplePath.FONT_PATH + NORMAL_NAME, MEDIUM_SIZE);
    public static final Font NORMAL_BIG = FontUtils.loadFont(SimplePath.FONT_PATH + NORMAL_NAME, BIG_SIZE);
    public static final Font NORMAL_HUGE = FontUtils.loadFont(SimplePath.FONT_PATH + NORMAL_BOLD_NAME, HUGE_SIZE);

    // 韩语 俄语
    public static final Font MALGUN_GOTHIC = new Font("Malgun Gothic", Font.PLAIN, FONT_SIZE);
    public static final Font MALGUN_GOTHIC_BIG = new Font("Malgun Gothic", Font.PLAIN, BIG_SIZE);
    public static final Font MALGUN_GOTHIC_HUGE = new Font("Malgun Gothic", Font.BOLD, HUGE_SIZE);
    // 越南语
    public static final Font CALIBRI = new Font("Calibri", Font.PLAIN, FONT_SIZE);
    public static final Font CALIBRI_BIG = new Font("Calibri", Font.PLAIN, BIG_SIZE);
    public static final Font CALIBRI_HUGE = new Font("Calibri", Font.BOLD, HUGE_SIZE);
    // 藏语
    public static final Font HIMALAYA = new Font("Microsoft Himalaya", Font.PLAIN, FONT_SIZE);
    public static final Font HIMALAYA_BIG = new Font("Microsoft Himalaya", Font.PLAIN, BIG_SIZE);
    public static final Font HIMALAYA_HUGE = new Font("Microsoft Himalaya", Font.BOLD, HUGE_SIZE);
    // 罗马
    public static final Font TIMES_NEW_ROMAN = new Font("Times New Roman", Font.PLAIN, FONT_SIZE);
    public static final Font TIMES_NEW_ROMAN_BIG = new Font("Times New Roman", Font.PLAIN, BIG_SIZE);
    public static final Font TIMES_NEW_ROMAN_HUGE = new Font("Times New Roman", Font.BOLD, HUGE_SIZE);
    // 特殊符号
//    public static final Font MS_GOTHIC = new Font("MS Gothic", Font.PLAIN, FONT_SIZE);
//    public static final Font MS_GOTHIC_BIG = new Font("MS Gothic", Font.PLAIN, BIG_SIZE);
//    public static final Font MS_GOTHIC_HUGE = new Font("MS Gothic", Font.BOLD, HUGE_SIZE);
    // emoji
    public static final Font EMOJI = new Font("Segoe UI Emoji", Font.PLAIN, FONT_SIZE);
    public static final Font EMOJI_BIG = new Font("Segoe UI Emoji", Font.PLAIN, BIG_SIZE);
    public static final Font EMOJI_HUGE = new Font("Segoe UI Emoji", Font.BOLD, HUGE_SIZE);
//    public static final Font EMOJI = FontUtils.loadFont(SimplePath.FONT_PATH+"emoji.ttf", FONT_SIZE);
//    public static final Font EMOJI_BIG = FontUtils.loadFont(SimplePath.FONT_PATH+"emoji.ttf", BIG_SIZE);
//    public static final Font EMOJI_HUGE = FontUtils.loadFont(SimplePath.FONT_PATH+"emoji.ttf", HUGE_SIZE);
    // sans-serif
    public static final Font SANS_SERIF = new Font("sans-serif", Font.PLAIN, FONT_SIZE);
    public static final Font SANS_SERIF_BIG = new Font("sans-serif", Font.PLAIN, BIG_SIZE);
    public static final Font SANS_SERIF_HUGE = new Font("sans-serif", Font.BOLD, HUGE_SIZE);
    // 藏语
//    public static final Font LEELAWADEE = new Font("Leelawadee UI", Font.PLAIN, FONT_SIZE);
//    public static final Font LEELAWADEE_BIG = new Font("Leelawadee UI", Font.PLAIN, BIG_SIZE);
//    public static final Font LEELAWADEE_HUGE = new Font("Leelawadee UI", Font.BOLD, HUGE_SIZE);

    public static final Font[] TYPES = {NORMAL, MALGUN_GOTHIC, CALIBRI, HIMALAYA, TIMES_NEW_ROMAN, EMOJI, SANS_SERIF};
    public static final Font[] TYPES_BIG = {NORMAL_BIG, MALGUN_GOTHIC_BIG, CALIBRI_BIG, HIMALAYA_BIG, TIMES_NEW_ROMAN_BIG, EMOJI_BIG, SANS_SERIF_BIG};
    public static final Font[] TYPES_HUGE = {NORMAL_HUGE, MALGUN_GOTHIC_HUGE, CALIBRI_HUGE, HIMALAYA_HUGE, TIMES_NEW_ROMAN_HUGE, EMOJI_HUGE, SANS_SERIF_HUGE};
}
