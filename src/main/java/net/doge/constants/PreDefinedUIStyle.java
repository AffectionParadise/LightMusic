package net.doge.constants;

import net.doge.models.UIStyle;

/**
 * @Author yzx
 * @Description 预设样式
 * @Date 2020/12/12
 */
public class PreDefinedUIStyle {
    public static final UIStyle[] styles = {
            // 默认
            new UIStyle(UIStyleConstants.DEFAULT, "默认", false, SimplePath.STYLE_IMG_PATH + "default.jpg",
                    Colors.LIGHT_GRAY, Colors.GRAY, Colors.WHITE, Colors.DEEP_SKY_BLUE_1, Colors.WHITE,
                    Colors.LIGHT_GRAY, Colors.LIGHT_GRAY, Colors.LIGHT_GRAY, Colors.LIGHT_GRAY, Colors.LIGHT_GRAY,
                    Colors.LIGHT_GRAY),
            // 夜晚
            new UIStyle(UIStyleConstants.NIGHT, "夜晚", false, SimplePath.STYLE_IMG_PATH + "night.jpg",
                    Colors.LEMON_CHIFFON, Colors.GOLD, Colors.LEMON_CHIFFON, Colors.GOLD3, Colors.LEMON_CHIFFON,
                    Colors.LEMON_CHIFFON, Colors.LEMON_CHIFFON, Colors.LEMON_CHIFFON, Colors.LEMON_CHIFFON, Colors.LEMON_CHIFFON,
                    Colors.LEMON_CHIFFON),
            // 粉色回忆
            new UIStyle(UIStyleConstants.PINK, "粉色回忆", false, SimplePath.STYLE_IMG_PATH + "pink.jpg",
                    Colors.PINK, Colors.HOT_PINK, Colors.PINK, Colors.DEEP_PINK, Colors.PINK,
                    Colors.PINK, Colors.PINK, Colors.PINK, Colors.PINK, Colors.PINK,
                    Colors.PINK),
            // 她
            new UIStyle(UIStyleConstants.SHE, "她", false, SimplePath.STYLE_IMG_PATH + "she.jpg",
                    Colors.BISQUE_1, Colors.BISQUE_3, Colors.BISQUE_1, Colors.CYAN_3, Colors.BISQUE_1,
                    Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1,
                    Colors.BISQUE_1),
            // 鹿鸣
            new UIStyle(UIStyleConstants.LUMING, "鹿鸣", false, SimplePath.STYLE_IMG_PATH + "luming.jpg",
                    Colors.AQUAMARINE_1, Colors.AQUAMARINE_3, Colors.AQUAMARINE_1, Colors.SPRING_GREEN, Colors.AQUAMARINE_1,
                    Colors.AQUAMARINE_1, Colors.AQUAMARINE_1, Colors.AQUAMARINE_1, Colors.AQUAMARINE_1, Colors.AQUAMARINE_1,
                    Colors.AQUAMARINE_1),
            // 你的名字
            new UIStyle(UIStyleConstants.YOUR_NAME, "你的名字", false, SimplePath.STYLE_IMG_PATH + "yourName.jpg",
                    Colors.THISTLE_1, Colors.ORCHID_2, Colors.THISTLE_1, Colors.ORCHID_2, Colors.THISTLE_1,
                    Colors.THISTLE_1, Colors.THISTLE_1, Colors.THISTLE_1, Colors.THISTLE_1, Colors.THISTLE_1,
                    Colors.THISTLE_1),
            // 深海
            new UIStyle(UIStyleConstants.DEEP_SEA, "深海", false, SimplePath.STYLE_IMG_PATH + "deepSea.jpg",
                    Colors.LIGHT_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_1, Colors.LIGHT_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_3, Colors.LIGHT_SKY_BLUE_1,
                    Colors.LIGHT_SKY_BLUE_1, Colors.LIGHT_SKY_BLUE_1, Colors.LIGHT_SKY_BLUE_1, Colors.LIGHT_SKY_BLUE_1, Colors.LIGHT_SKY_BLUE_1,
                    Colors.LIGHT_SKY_BLUE_1),
            // 林间小溪
            new UIStyle(UIStyleConstants.BROOK, "林间小溪", false, SimplePath.STYLE_IMG_PATH + "brook.jpg",
                    Colors.KHAKI_1, Colors.ORANGE_1, Colors.KHAKI_1, Colors.ORANGE_1, Colors.KHAKI_1,
                    Colors.KHAKI_1, Colors.KHAKI_1, Colors.KHAKI_1, Colors.KHAKI_1, Colors.KHAKI_1,
                    Colors.KHAKI_1),
            // 新年快乐
            new UIStyle(UIStyleConstants.HAPPY_NEW_YEAR, "新年快乐", false, SimplePath.STYLE_IMG_PATH + "happyNewYear.jpg",
                    Colors.MISTY_ROSE, Colors.CHINESE_RED_2, Colors.MISTY_ROSE, Colors.CHINESE_RED_2, Colors.MISTY_ROSE,
                    Colors.MISTY_ROSE, Colors.MISTY_ROSE, Colors.MISTY_ROSE, Colors.MISTY_ROSE, Colors.MISTY_ROSE,
                    Colors.MISTY_ROSE),
            // 犬塚信乃戍孝
            new UIStyle(UIStyleConstants.MORITAKA, "犬塚信乃戍孝", false, SimplePath.STYLE_IMG_PATH + "moritaka.jpg",
                    Colors.LIGHT_BLUE_1, Colors.ORANGE_1, Colors.LIGHT_BLUE_1, Colors.ORANGE_1, Colors.LIGHT_BLUE_1,
                    Colors.LIGHT_BLUE_1, Colors.LIGHT_BLUE_1, Colors.LIGHT_BLUE_1, Colors.LIGHT_BLUE_1, Colors.LIGHT_BLUE_1,
                    Colors.LIGHT_BLUE_1),
            // 戍孝与雨
            new UIStyle(UIStyleConstants.MORITAKA_AND_RAIN, "戍孝与雨", false, SimplePath.STYLE_IMG_PATH + "moritakaAndRain.jpg",
                    Colors.WHITE_SMOKE, Colors.BISQUE_2, Colors.WHITE_SMOKE, Colors.BISQUE_3, Colors.WHITE_SMOKE,
                    Colors.WHITE_SMOKE, Colors.WHITE_SMOKE, Colors.WHITE_SMOKE, Colors.WHITE_SMOKE, Colors.WHITE_SMOKE,
                    Colors.WHITE_SMOKE)
    };
}
