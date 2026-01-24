package net.doge.constant.core.task;

import net.doge.constant.core.lang.I18n;

/**
 * @Author Doge
 * @Description 任务类型
 * @Date 2020/12/7
 */
public class TaskType {
    public static final String[] NAMES = new String[]{
            I18n.getText("track"),
            I18n.getText("mv")
    };

    public static final int MUSIC = 0;
    public static final int MV = 1;
}
