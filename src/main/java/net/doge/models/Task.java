package net.doge.models;

import net.doge.constants.*;
import net.doge.utils.MusicServerUtils;
import lombok.Data;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * @Author yzx
 * @Description 下载任务
 * @Date 2022/1/21
 */
@Data
public class Task {
    private static final String SEPARATOR = "   |   ";

    // 下载列表
    private JList downloadList;
    // 下载类型
    private int type;
    // 音乐信息(类型为 MUSIC 时)
    private NetMusicInfo netMusicInfo;
    // MV 信息(类型为 MV 时)
    private NetMvInfo netMvInfo;
    // url
    private String url;
    // 文件路径
    private String dest;
    // 名称
    private String name;
    // 状态
    private int status;
    // 百分比
    private double percent;
    // 已下载大小
    private long finished;
    // 总大小
    private long total;
    // 任务对应的 Future 对象
    private Future future;
    // 完成后调用
    private Runnable invokeLater;

    public Task(JList downloadList, int type, NetMusicInfo netMusicInfo, NetMvInfo netMvInfo) {
        this.downloadList = downloadList;
        this.type = type;
        this.netMusicInfo = netMusicInfo;
        this.netMvInfo = netMvInfo;
        this.name = type == TaskType.MUSIC ? netMusicInfo.toSimpleString() : netMvInfo.toSimpleString();
        this.dest = type == TaskType.MUSIC ? SimplePath.DOWNLOAD_MUSIC_PATH + netMusicInfo.toSimpleFileName() : SimplePath.DOWNLOAD_MV_PATH + netMvInfo.toSimpleFileName();
    }

    public void setPercent(double percent) {
        this.percent = percent;
        downloadList.repaint();
    }

    public void setFinished(long finished) {
        this.finished = finished;
        downloadList.repaint();
    }

    public void setTotal(long total) {
        this.total = total;
        downloadList.repaint();
    }

    public void setStatus(int status) {
        this.status = status;
        downloadList.repaint();
    }

    public void start() {
        percent = 0;
        future = GlobalExecutors.downloadExecutor.submit(() -> {
            try {
                dirCheck();
                prepareInfo();
                MusicServerUtils.download(this);
                if(isInterrupted()) return;
                if (invokeLater != null) invokeLater.run();
                setStatus(TaskStatus.FINISHED);
            } catch (Exception e) {
                setStatus(TaskStatus.FAILED);
            }
        });
        setStatus(TaskStatus.RUNNING);
    }

    public void stop() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
            setStatus(TaskStatus.INTERRUPTED);
        }
    }

    public boolean isRunning() {
        return status == TaskStatus.RUNNING;
    }

    public boolean isFinished() {
        return status == TaskStatus.FINISHED;
    }

    public boolean isInterrupted() {
        return status == TaskStatus.INTERRUPTED;
    }

    public boolean isMusic() {
        return type == TaskType.MUSIC;
    }

    // 任务开始之前先请求所需信息
    private void prepareInfo() throws IOException {
        if (type == TaskType.MUSIC) {
            // 先补全音乐信息
            MusicServerUtils.fillNetMusicInfo(netMusicInfo);
            url = netMusicInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MUSIC_PATH + netMusicInfo.toSimpleFileName();
        } else if (type == TaskType.MV) {
            MusicServerUtils.fillMvInfo(netMvInfo);
            url = netMvInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MV_PATH + netMvInfo.toSimpleFileName();
        }
    }

    private void dirCheck() {
        File dir = new File(SimplePath.DOWNLOAD_MUSIC_PATH);
        if (!dir.exists()) dir.mkdirs();
        dir = new File(SimplePath.DOWNLOAD_MV_PATH);
        if (!dir.exists()) dir.mkdirs();
    }

    @Override
    public String toString() {
        return TaskStatus.s[status] + SEPARATOR + (isRunning() ? String.format("%.2f %%", percent) + SEPARATOR : "")
                + TaskType.s[type] + SEPARATOR + name;
    }
}
