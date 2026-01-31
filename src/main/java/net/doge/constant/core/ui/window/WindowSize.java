package net.doge.constant.core.ui.window;

import net.doge.constant.core.lang.I18n;
import net.doge.util.ui.ScaleUtil;

public class WindowSize {
    public static final int[][] DIMENSIONS = {
            {ScaleUtil.scale(1182), ScaleUtil.scale(748)},
            {ScaleUtil.scale(1245), ScaleUtil.scale(788)},
            {ScaleUtil.scale(1370), ScaleUtil.scale(867)},
            {ScaleUtil.scale(1494), ScaleUtil.scale(946)},
            {ScaleUtil.scale(1619), ScaleUtil.scale(1024)},
            {ScaleUtil.scale(1960), ScaleUtil.scale(1182)},
    };
    public static final int[][] VIDEO_DIMENSIONS = {
            {ScaleUtil.scale(1140), ScaleUtil.scale(641)},
            {ScaleUtil.scale(1200), ScaleUtil.scale(675)},
            {ScaleUtil.scale(1320), ScaleUtil.scale(743)},
            {ScaleUtil.scale(1440), ScaleUtil.scale(810)},
            {ScaleUtil.scale(1560), ScaleUtil.scale(878)},
            {ScaleUtil.scale(1800), ScaleUtil.scale(1013)},
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
