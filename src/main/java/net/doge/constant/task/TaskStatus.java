package net.doge.constant.task;

/**
 * @Author yzx
 * @Description 任务状态
 * @Date 2020/12/7
 */
public class TaskStatus {
    public static final String s[] = new String[]{"下载中", "已完成", "已中断", "失败", "等待中"};

    public static final int RUNNING = 0;
    public static final int FINISHED = 1;
    public static final int INTERRUPTED = 2;
    public static final int FAILED = 3;
    public static final int WAITING = 4;
}
