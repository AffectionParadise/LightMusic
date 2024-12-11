package net.doge.model.task;

import lombok.Data;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.system.Format;
import net.doge.constant.system.SimplePath;
import net.doge.constant.task.TaskStatus;
import net.doge.constant.task.TaskType;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetMvInfo;
import net.doge.model.entity.base.Downloadable;
import net.doge.sdk.system.listener.DownloadListener;
import net.doge.sdk.util.MusicServerUtil;
import net.doge.util.system.FileUtil;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @Author Doge
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
    // 资源信息
    private Downloadable resource;
    // url
    private String url;
    // 文件路径
    private String dest;
    // 格式
    private String format;
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

    public Task(JList downloadList, int type, Downloadable resource) {
        this.downloadList = downloadList;
        this.type = type;
        this.resource = resource;
        this.name = type == TaskType.MUSIC ? ((NetMusicInfo) resource).toSimpleString() : ((NetMvInfo) resource).toSimpleString();
        this.dest = type == TaskType.MUSIC ? SimplePath.DOWNLOAD_MUSIC_PATH + ((NetMusicInfo) resource).toSimpleFileName()
                : SimplePath.DOWNLOAD_MV_PATH + ((NetMvInfo) resource).toSimpleFileName();
        this.format = type == TaskType.MUSIC ? ((NetMusicInfo) resource).getFormat() : ((NetMvInfo) resource).getFormat();
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

    private Map<String, String> getHeaders() {
        Map<String, String> headers = null;
        if (isMv() && ((NetMvInfo) resource).getSource() == NetMusicSource.BI || isMusic() && ((NetMusicInfo) resource).getSource() == NetMusicSource.BI) {
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
                MusicServerUtil.download(url, dest, getHeaders(), new DownloadListener() {
                    @Override
                    public void totalSizeInitialized(long totalSize) {
                        setTotal(totalSize);
                    }

                    @Override
                    public void progress(long finishedSize, long totalSize) {
                        setPercent((double) finishedSize / totalSize * 100);
                        setFinished(finishedSize);
                    }

                    @Override
                    public boolean canInterrupt() {
                        // 中断任务后跳出
                        return isInterrupted();
                    }
                });
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

    public boolean isMp3() {
        return Format.MP3.equalsIgnoreCase(format);
    }

    public boolean isFlac() {
        return Format.FLAC.equalsIgnoreCase(format);
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
            NetMusicInfo musicInfo = (NetMusicInfo) resource;
            // 先补全音乐信息、url
            MusicServerUtil.fillMusicInfo(musicInfo);
            MusicServerUtil.fillMusicUrl(musicInfo);
            MusicServerUtil.fillLrc(musicInfo);
            url = musicInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MUSIC_PATH + musicInfo.toSimpleFileName();
        } else if (type == TaskType.MV) {
            NetMvInfo mvInfo = (NetMvInfo) resource;
            MusicServerUtil.fillMvInfo(mvInfo);
            url = mvInfo.getUrl();
            dest = SimplePath.DOWNLOAD_MV_PATH + mvInfo.toSimpleFileName();
        }
    }

    private void dirCheck() {
        FileUtil.mkDir(SimplePath.DOWNLOAD_MUSIC_PATH);
        FileUtil.mkDir(SimplePath.DOWNLOAD_MV_PATH);
    }

    @Override
    public String toString() {
        return TaskStatus.NAMES[status] + SEPARATOR + (isProcessing() ? String.format("%.1f%%", percent).replace(".0", "") + SEPARATOR : "")
                + TaskType.NAMES[type] + SEPARATOR + name;
    }
}
