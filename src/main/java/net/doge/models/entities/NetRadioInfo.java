package net.doge.models.entities;

import lombok.Data;
import net.doge.constants.NetMusicSource;
import net.doge.constants.RadioType;
import net.doge.utils.StringUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 电台
 * @Date 2020/12/7
 */
@Data
public class NetRadioInfo {
    // 电台来源
    private int source = NetMusicSource.NET_CLOUD;
    // 类型 (豆瓣)
    private int type;
    // 电台 id
    private String id;
    // 电台名称
    private String name;
    // 电台 DJ
    private String dj;
    // 电台 DJ id
    private String djId;
    // 封面图片
    private BufferedImage coverImg;
    // 封面图片 url (QQ / 喜马拉雅 需要)
    private String coverImgUrl;
    // 封面图片缩略图
    private BufferedImage coverImgThumb;
    // 封面图片缩略图 url
    private String coverImgThumbUrl;
    // 标签
    private String tag;
    // 描述
    private String description;
    // 播放量
    private Long playCount;
    // 节目数
    private Integer trackCount;
    // 类别
    private String category;
//    // 创建时间
//    private String createTime;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean fromME() {
        return source == NetMusicSource.ME;
    }

    public boolean fromXM() {
        return source == NetMusicSource.XM;
    }

    public boolean isBook() {
        return type == RadioType.BOOK;
    }

    public boolean isGame() {
        return type == RadioType.GAME;
    }

    public boolean hasCoverImgUrl() {
        return StringUtil.isNotEmpty(coverImgUrl);
    }

    public boolean hasCoverImg() {
        return coverImg != null;
    }

    public boolean hasTrackCount() {
        return trackCount != null && trackCount >= 0;
    }

    public boolean hasPlayCount() {
        return playCount != null && playCount >= 0;
    }

    public boolean hasName() {
        return StringUtil.isNotEmpty(name);
    }

    public boolean hasTag() {
        return StringUtil.isNotEmpty(tag);
    }

    public boolean hasDescription() {
        return StringUtil.isNotEmpty(description);
    }

    public boolean hasDj() {
        return StringUtil.isNotEmpty(dj);
    }

    public boolean hasDjId() {
        return StringUtil.isNotEmpty(djId);
    }

    public boolean hasCategory() {
        return StringUtil.isNotEmpty(category);
    }

//    public boolean hasCreateTime() {
//        return StringUtils.isNotEmpty(createTime);
//    }

    public void setCoverImgThumb(BufferedImage coverImgThumb) {
        this.coverImgThumb = coverImgThumb;
        callback();
    }

    public void setCoverImg(BufferedImage coverImg) {
        this.coverImg = coverImg;
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
     * 判断电台信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return coverImg != null;
    }

    public boolean hasCoverImgThumb() {
        return coverImgThumb != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetRadioInfo) {
            NetRadioInfo netRadioInfo = (NetRadioInfo) o;
            return hashCode() == netRadioInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toString() {
        return NetMusicSource.names[source] + " - " + toSimpleString();
    }

    public String toSimpleString() {
        return name
                + (StringUtil.isEmpty(dj) ? "" : " - " + dj);
    }

//    public String toString() {
//        return name + " - " + id;
//    }
}
