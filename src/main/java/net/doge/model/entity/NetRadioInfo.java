package net.doge.model.entity;

import lombok.Data;
import net.doge.constant.model.NetMusicSource;
import net.doge.constant.model.RadioType;
import net.doge.model.entity.base.NetResource;
import net.doge.util.common.StringUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author Doge
 * @Description 电台
 * @Date 2020/12/7
 */
@Data
public class NetRadioInfo implements NetResource {
    // 电台来源
    private int source = NetMusicSource.NC;
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
        return StringUtil.notEmpty(coverImgUrl);
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
        return StringUtil.notEmpty(name);
    }

    public boolean hasTag() {
        return StringUtil.notEmpty(tag);
    }

    public boolean hasDescription() {
        return StringUtil.notEmpty(description);
    }

    public boolean hasDj() {
        return StringUtil.notEmpty(dj);
    }

    public boolean hasDjId() {
        return StringUtil.notEmpty(djId);
    }

    public boolean hasCategory() {
        return StringUtil.notEmpty(category);
    }

//    public boolean hasCreateTime() {
//        return StringUtils.notEmpty(createTime);
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
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    /**
     * 判断电台信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return hasCoverImg();
    }

    public boolean hasCoverImgThumb() {
        return coverImgThumb != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetRadioInfo) {
            NetRadioInfo radioInfo = (NetRadioInfo) o;
            return hashCode() == radioInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toString() {
        return NetMusicSource.NAMES[source] + " - " + toSimpleString();
    }

    public String toSimpleString() {
        return name
                + (StringUtil.isEmpty(dj) ? "" : " - " + dj);
    }
}
