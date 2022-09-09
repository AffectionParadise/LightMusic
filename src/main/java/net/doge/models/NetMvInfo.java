package net.doge.models;

import net.doge.constants.MvInfoType;
import net.doge.constants.NetMusicSource;
import lombok.Data;
import net.doge.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Objects;

/**
 * @Author yzx
 * @Description MV
 * @Date 2020/12/7
 */
@Data
public class NetMvInfo {
    // MV 来源
    private int source = NetMusicSource.NET_CLOUD;
    // 类型 (网易云分成 MV 视频 Mlog)
    private int type;
    // MV id
    private String id;
    // MV 名称
    private String name;
    // 艺术家
    private String artist;
    // 封面图片 url
    private String coverImgUrl;
    // 封面图片缩略图
    private BufferedImage coverImgThumb;
    // url
    private String url;
    // 播放量
    private Long playCount;
    // 时长
    private Double duration;
    // 发布时间
    private String pubTime;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean hasDuration() {
        return duration != null && !Double.isNaN(duration) && !Double.isInfinite(duration) && duration.intValue() != 0;
    }

    public boolean hasPubTime() {
        return StringUtils.isNotEmpty(pubTime);
    }

    public boolean hasPlayCount() {
        return playCount != null && playCount >= 0;
    }

    public void setCoverImgThumb(BufferedImage coverImgThumb) {
        this.coverImgThumb = coverImgThumb;
        callback();
    }

    private void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    /**
     * 判断 MV 信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return url != null;
    }

    public boolean isVideo() {
        return type == MvInfoType.VIDEO;
    }

    public boolean isMlog() {
        return type == MvInfoType.MLOG;
    }

    public boolean hasCoverImgThumb() {
        return coverImgThumb != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetMvInfo) {
            NetMvInfo netMvInfo = (NetMvInfo) o;
            return hashCode() == netMvInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toFileName() {
        return String.format("%s - %s - %s.mp4", name, artist, id);
    }

    public String toSimpleFileName() {
        return String.format("%s - %s.mp4", name, artist);
    }

    public String toString() {
        return NetMusicSource.names[source] + " - " + toSimpleString()
                + (playCount == null ? "" : "\n\n" + StringUtils.formatNumber(playCount));
    }

    public String toSimpleString() {
        return name + " - " + artist;
    }

//    public String toString() {
//        return name + " - " + id;
//    }
}
