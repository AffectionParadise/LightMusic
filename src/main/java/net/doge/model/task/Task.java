package net.doge.model.task;

import lombok.Data;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.constant.system.SimplePath;
import net.doge.constant.task.TaskStatus;
import net.doge.constant.task.TaskType;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetMvInfo;
import net.doge.util.system.FileUtil;
import net.doge.sdk.util.MusicServerUtil;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
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

    private Map<String, Object> getHeaders() {
        Map<String, Object> headers = null;
        if (isMv() && netMvInfo.getSource() == NetMusicSource.BI || isMusic() && netMusicInfo.getSource() == NetMusicSource.BI) {
            headers = new HashMap<>();
            headers.put("referer", "https://www.bilibili.com/");
        }
        return headers;
    }

    public void start() {
        percent = 0;
        setStatus(TaskStatus.WAITING);
        future = GlobalExecutors.downloadExecutor.submit(() -> {
            try {
                dirCheck();
                prepareInfo();
                setStatus(TaskStatus.RUNNING);
                MusicServerUtil.download(this, getHeaders());
                if (isInterrupted()) return;
                if (invokeLater != null) invokeLater.run();
                setStatus(TaskStatus.FINISHED);
            } catch (Exception e) {
                setStatus(TaskStatus.FAILED);
            }
        });
    }

    public void stop() {
        if (future != null && !future.isDone() && !future.isCancelled()) {
            future.cancel(true);
            setStatus(TaskStatus.INTERRUPTED);
        }
    }

    public boolean isProcessing() {
        return isRunning() || isWaiting();
    }

    public boolean isWaiting() {
        return status == TaskStatus.WAITING;
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

    public boolean isMv() {
        return type == TaskType.MV;
    }

    // 任务开始之前先请求所需信息
    private void prepareInfo() {
        if (type == TaskType.MUSIC) {
            // 先补全音乐信息、url
            MusicServerUtil.fillMusicInfo(netMusicInfo);
            MusicServerUtil.fillMusicUrl(netMusicInfo);
            MusicServerUtil.fillLrc(netMusicInfo);
            url = netMusicInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MUSIC_PATH + netMusicInfo.toSimpleFileName();
        } else if (type == TaskType.MV) {
            MusicServerUtil.fillMvInfo(netMvInfo);
            url = netMvInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MV_PATH + netMvInfo.toSimpleFileName();
        }
    }

    private void dirCheck() {
        FileUtil.makeSureDir(SimplePath.DOWNLOAD_MUSIC_PATH);
        FileUtil.makeSureDir(SimplePath.DOWNLOAD_MV_PATH);
    }

    @Override
    public String toString() {
        return TaskStatus.s[status] + SEPARATOR + (isProcessing() ? String.format("%.2f %%", percent) + SEPARATOR : "")
                + TaskType.s[type] + SEPARATOR + name;
    }
}
