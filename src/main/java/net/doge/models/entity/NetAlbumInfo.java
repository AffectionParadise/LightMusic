package net.doge.models.entity;

import net.doge.constants.NetMusicSource;
import lombok.Data;
import net.doge.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 专辑
 * @Date 2020/12/7
 */
@Data
public class NetAlbumInfo {
    // 专辑来源
    private int source = NetMusicSource.NET_CLOUD;
    // 专辑 id
    private String id;
    // 专辑名称
    private String name;
    // 艺术家
    private String artist;
    // 艺术家 id
    private String artistId;
    // 封面图片
    private BufferedImage coverImg;
    // 封面图片 url
    private String coverImgUrl;
    // 封面图片缩略图
    private BufferedImage coverImgThumb;
    // 封面图片缩略图 url
    private String coverImgThumbUrl;
    // 描述
    private String description;
    // 发行日期
    private String publishTime;
    // 歌曲数量
    private Integer songNum;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean isPhoto() {
        return source == NetMusicSource.DT;
    }

    public boolean hasCoverImg() {
        return coverImg != null;
    }

    public boolean hasName() {
        return StringUtils.isNotEmpty(name);
    }

    public boolean hasArtist() {
        return StringUtils.isNotEmpty(artist);
    }

    public boolean hasArtistId() {
        return StringUtils.isNotEmpty(artistId);
    }

    public boolean hasPublishTime() {
        return StringUtils.isNotEmpty(publishTime);
    }

    public boolean hasSongNum() {
        return songNum != null && songNum >= 0;
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
     * 判断专辑信息是否完整
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
        if (o instanceof NetAlbumInfo) {
            NetAlbumInfo netAlbumInfo = (NetAlbumInfo) o;
            return hashCode() == netAlbumInfo.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, id);
    }

    public String toString() {
        return NetMusicSource.names[source] + " - " + toSimpleString();
    }

    public String toSimpleString() {
        return name
                + (StringUtils.isEmpty(artist) ? "" : " - " + artist);
    }

//    public String toString() {
//        return name + " - " + id;
//    }
}
