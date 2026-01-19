package net.doge.model.entity;

import lombok.Data;
import net.doge.constant.core.Format;
import net.doge.constant.core.VideoQuality;
import net.doge.constant.model.MvInfoType;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.base.Downloadable;
import net.doge.model.entity.base.NetResource;
import net.doge.util.common.StringUtil;
import net.doge.util.os.FileUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author Doge
 * @Description MV
 * @Date 2020/12/7
 */
@Data
public class NetMvInfo implements NetResource, Downloadable {
    // MV 来源
    private int source = NetMusicSource.NC;
    // 类型 (网易云分成 MV 视频 Mlog)
    private int type;
    // 格式
    private String format = Format.MP4;
    // 画质
    private int quality;
    // MV id
    private String id;
    // MV bvid (哔哩哔哩)
    private String bvid;
    // MV 名称
    private String name;
    // 创建者 id
    private String creatorId;
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

    public boolean isRealMV() {
        return type == MvInfoType.MV && source != NetMusicSource.HK && source != NetMusicSource.BI;
    }

    public boolean hasDuration() {
        return duration != null && !Double.isNaN(duration) && !Double.isInfinite(duration) && duration != 0;
    }

    public boolean hasCreatorId() {
        return StringUtil.notEmpty(creatorId);
    }

    public boolean hasPubTime() {
        return StringUtil.notEmpty(pubTime);
    }

    public boolean hasPlayCount() {
        return playCount != null && playCount >= 0;
    }

    public void setCoverImgThumb(BufferedImage coverImgThumb) {
        this.coverImgThumb = coverImgThumb;
        callback();
    }

    private void callback() {
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    // 判断 MV 信息是否完整
    public boolean isIntegrated() {
        return hasUrl();
    }

    // 判断当前画质是否与设置的画质匹配
    public boolean isQualityMatch() {
        return quality == VideoQuality.quality;
    }

    public void setFormat(String format) {
        this.format = StringUtil.notEmpty(format) ? format : Format.MP4;
    }

    public boolean isFlv() {
        return Format.FLV.equalsIgnoreCase(format);
    }

    public boolean isMp4() {
        return Format.MP4.equalsIgnoreCase(format);
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

    public boolean hasUrl() {
        return StringUtil.notEmpty(url);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetMvInfo) {
            NetMvInfo mvInfo = (NetMvInfo) o;
            return hashCode() == mvInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id, bvid);
    }

    public String toFileName() {
        return FileUtil.filterFileName(String.format("%s - %s.%s", toSimpleString(), id, format));
    }

    public String toSimpleFileName() {
        return FileUtil.filterFileName(String.format("%s.%s", toSimpleString(), format));
    }

    public String toString() {
        return NetMusicSource.NAMES[source] + " - " + toSimpleString();
    }

    public String toSimpleString() {
        return StringUtil.shorten(name + " - " + artist, 230);
    }
}
