package net.doge.constant.core.ui.style;

import net.doge.entity.core.ui.UIStyle;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Doge
 * @Description 主题变量存储
 * @Date 2020/12/12
 */
public class UIStyleStorage {
    // 当前主题
    public static UIStyle currUIStyle;
    // 所有界面主题
    public static List<UIStyle> styles = new LinkedList<>();

    // 添加界面主题
    static {
        styles.addAll(Arrays.asList(PreDefinedUIStyle.STYLES));
        currUIStyle = styles.get(0);
    }
}
