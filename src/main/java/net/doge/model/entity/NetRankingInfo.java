package net.doge.model.entity;

import lombok.Data;
import net.doge.constant.system.NetMusicSource;
import net.doge.util.StringUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 榜单
 * @Date 2020/12/7
 */
@Data
public class NetRankingInfo {
    // 榜单来源
    private int source = NetMusicSource.NET_CLOUD;
    // 榜单 id
    private String id;
    // 榜单名称
    private String name;
    // 封面图片
    private BufferedImage coverImg;
    // 封面图片 url
    private String coverImgUrl;
    // 封面图片缩略图
    private BufferedImage coverImgThumb;
    // 描述
    private String description;
    // 播放量
    private Long playCount;
    // 更新频率
    private String updateFre;
    // 更新时间
    private String updateTime;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean hasPlayCount() {
        return playCount != null && playCount >= 0;
    }

    public boolean hasUpdateFre() {
        return StringUtil.isNotEmpty(updateFre);
    }

    public boolean hasUpdateTime() {
        return StringUtil.isNotEmpty(updateTime);
    }

    public boolean hasCoverImgUrl() {
        return StringUtil.isNotEmpty(coverImgUrl);
    }

    public boolean hasCoverImg() {
        return coverImg != null;
    }

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
     * 判断榜单信息是否完整
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
        if (o instanceof NetRankingInfo) {
            NetRankingInfo netRankingInfo = (NetRankingInfo) o;
            return hashCode() == netRankingInfo.hashCode();
        }
        return false;
    }

    // 必须重写 hashCode 和 equals 方法才能在 Set 判断是否重复！
    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toString() {
        return NetMusicSource.names[source] + " - " + toSimpleString()
                + (playCount == null ? "" : "\n\n" + StringUtil.formatNumber(playCount));
    }

    public String toSimpleString() {
        return name;
    }

//    public String toString() {
//        return name + " - " + id;
//    }
}
