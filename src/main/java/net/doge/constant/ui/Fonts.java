package net.doge.constant.ui;

import net.doge.constant.config.ConfigConstants;
import net.doge.util.lmdata.LMFontManager;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @Author Doge
 * @Description 字体
 * @Date 2021/1/3
 */
public class Fonts {
    private static final int FONT_SIZE = 17;
    private static final int TINY_SIZE = FONT_SIZE - 2;
    private static final int MEDIUM_SIZE = FONT_SIZE + 3;
    private static final int BIG_SIZE = FONT_SIZE + 10;
    public static final int HUGE_SIZE = BIG_SIZE + 20;

    public static int currFont = 0;
    public static final String[] NAMES = {"鸿蒙", "小米", "OPPO", "vivo"};
    private static final String[] FONT_NAMES = {"HarmonyOS Sans", "MiSans", "OPPOSans", "vivoSans"};

    private static final String NORMAL_NAME = FONT_NAMES[currFont = ConfigConstants.CONFIG_DATA.getIntValue(ConfigConstants.UI_FONT, currFont)];

    // 中英文
    public static final Font NORMAL_TINY = LMFontManager.getFont(NORMAL_NAME, TINY_SIZE);
    public static final Font NORMAL = LMFontManager.getFont(NORMAL_NAME, FONT_SIZE);
    public static final Font NORMAL_MEDIUM = LMFontManager.getFont(NORMAL_NAME, MEDIUM_SIZE);
    public static final Font NORMAL_BIG = LMFontManager.getFont(NORMAL_NAME, BIG_SIZE);
    public static final Font NORMAL_HUGE = LMFontManager.getFont(NORMAL_NAME, HUGE_SIZE);

//    public static final Font NORMAL = new Font("微软雅黑", Font.PLAIN, FONT_SIZE);
//    public static final Font NORMAL_MEDIUM = new Font("微软雅黑", Font.PLAIN, MEDIUM_SIZE);
//    public static final Font NORMAL_BIG = new Font("微软雅黑", Font.PLAIN, BIG_SIZE);
//    public static final Font NORMAL_HUGE = new Font("微软雅黑", Font.PLAIN, HUGE_SIZE);

    // emoji
    public static final Font EMOJI = new Font("Segoe UI Emoji", Font.BOLD, FONT_SIZE);
    public static final Font EMOJI_BIG = new Font("Segoe UI Emoji", Font.BOLD, BIG_SIZE);
    public static final Font EMOJI_HUGE = new Font("Segoe UI Emoji", Font.BOLD, HUGE_SIZE);

    public static final List<Font> TYPES = new LinkedList<>();
    public static final List<Font> TYPES_BIG = new LinkedList<>();
    public static final List<Font> TYPES_HUGE = new LinkedList<>();

    static {
        TYPES.add(NORMAL);
        TYPES_BIG.add(NORMAL_BIG);
        TYPES_HUGE.add(NORMAL_HUGE);
        TYPES.add(EMOJI);
        TYPES_BIG.add(EMOJI_BIG);
        TYPES_HUGE.add(EMOJI_HUGE);
        // 加载所有安装的字体
        String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH);
        for (String name : names) {
            TYPES.add(new Font(name, Font.BOLD, FONT_SIZE));
            TYPES_BIG.add(new Font(name, Font.BOLD, BIG_SIZE));
            TYPES_HUGE.add(new Font(name, Font.BOLD, HUGE_SIZE));
        }
    }
}
