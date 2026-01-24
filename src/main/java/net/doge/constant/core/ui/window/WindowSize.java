package net.doge.constant.core.ui.window;

import net.doge.constant.core.lang.I18n;

public class WindowSize {
    public static final int[][] DIMENSIONS = {
            {1182, 748},
            {1245, 788},
            {1370, 867},
            {1494, 946},
            {1619, 1024},
            {1960, 1182},
    };
    public static final int[][] VIDEO_DIMENSIONS = {
            {1140, 641},
            {1200, 675},
            {1320, 743},
            {1440, 810},
            {1560, 878},
            {1800, 1013},
    };

    public static final int MIDDLE = 1;

    public static final String[] NAMES = {
            I18n.getText("small") + " (95%)",
            I18n.getText("medium") + " (100%)",
            I18n.getText("large") + " (110%)",
            I18n.getText("larger") + " (120%)",
            I18n.getText("sLarge") + " (130%)",
            I18n.getText("huge") + " (150%)"
    };
}
