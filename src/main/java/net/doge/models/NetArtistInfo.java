package net.doge.models;

import net.doge.constants.NetMusicSource;
import lombok.Data;
import net.doge.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 歌手
 * @Date 2020/12/7
 */
@Data
public class NetArtistInfo {
    // 歌手来源
    private int source = NetMusicSource.NET_CLOUD;
    // 歌手 id
    private String id;
    // 歌手名称
    private String name;
    // 封面图片
    private BufferedImage coverImg;
    // 封面图片 url (酷我/QQ 音乐需要)
    private String coverImgUrl;
    // 封面图片缩略图
    private BufferedImage coverImgThumb;
    // 封面图片缩略图 url
    private String coverImgThumbUrl;
    // 描述
    private String description;
    // 歌曲数量
    private Integer songNum;
    // 专辑数量
    private Integer albumNum;
    // MV 数量
    private Integer mvNum;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean hasCoverImg() {
        return coverImg != null;
    }

    public boolean hasName() {
        return StringUtils.isNotEmpty(name);
    }

    public boolean hasSongNum() {
        return songNum != null && songNum >= 0;
    }

    public boolean hasAlbumNum() {
        return albumNum != null && albumNum >= 0;
    }

    public boolean hasMvNum() {
        return mvNum != null && mvNum >= 0;
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
     * 判断歌手信息是否完整
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
        if (o instanceof NetArtistInfo) {
            NetArtistInfo netArtistInfo = (NetArtistInfo) o;
            return hashCode() == netArtistInfo.hashCode();
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
        return name;
    }

//    public String toString() {
//        return name + " - " + id;
//    }
}
