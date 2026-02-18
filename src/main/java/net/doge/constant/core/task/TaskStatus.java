package net.doge.constant.core.task;

import net.doge.constant.core.lang.I18n;

/**
 * @author Doge
 * @description 任务状态
 * @date 2020/12/7
 */
public class TaskStatus {
    public static final String[] NAMES = new String[]{
            I18n.getText("downloading"),
            I18n.getText("completed"),
            I18n.getText("interrupted"),
            I18n.getText("failed"),
            I18n.getText("waiting")
    };

    public static final int RUNNING = 0;
    public static final int FINISHED = 1;
    public static final int INTERRUPTED = 2;
    public static final int FAILED = 3;
    public static final int WAITING = 4;
}
