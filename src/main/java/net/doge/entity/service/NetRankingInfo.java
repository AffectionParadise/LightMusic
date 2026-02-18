package net.doge.entity.service;

import lombok.Data;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.base.NetResource;
import net.doge.util.core.StringUtil;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @author Doge
 * @description 榜单
 * @date 2020/12/7
 */
@Data
public class NetRankingInfo implements NetResource {
    // 榜单来源
    private int source = NetMusicSource.NC;
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
        return StringUtil.notEmpty(updateFre);
    }

    public boolean hasUpdateTime() {
        return StringUtil.notEmpty(updateTime);
    }

    public boolean hasCoverImgUrl() {
        return StringUtil.notEmpty(coverImgUrl);
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
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    /**
     * 判断榜单信息是否完整
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
        if (o instanceof NetRankingInfo) {
            NetRankingInfo rankingInfo = (NetRankingInfo) o;
            return hashCode() == rankingInfo.hashCode();
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
        return name;
    }
}
