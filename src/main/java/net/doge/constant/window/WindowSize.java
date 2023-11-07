package net.doge.constant.window;

import net.doge.constant.system.I18n;

public class WindowSize {
    public static final int[][] DIMENSIONS = {
            {1182, 748},
            {1245, 788},
            {1307, 827},
            {1369, 866},
            {1619, 1024},
            {1960, 1182},
    };
    public static final int[][] VIDEO_DIMENSIONS = {
            {1140, 641},
            {1200, 675},
            {1260, 709},
            {1320, 743},
            {1560, 878},
            {1800, 1013},
    };

    public static final int MIDDLE = 1;

    public static final String[] NAMES = {
            I18n.getText("small") + " (95%)",
            I18n.getText("medium") + " (100%)",
            I18n.getText("large") + " (105%)",
            I18n.getText("larger") + " (110%)",
            I18n.getText("sLarge") + " (130%)",
            I18n.getText("huge") + " (150%)"
    };
}
