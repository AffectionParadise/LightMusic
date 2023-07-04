package net.doge.model.entity;

import net.doge.constant.system.NetMusicSource;
import lombok.Data;
import net.doge.util.StringUtil;
import net.doge.util.TimeUtil;

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
    // 是否是社团(猫耳)
    private boolean isOrganization;
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

    // 性别
    private String gender;
    // 生日
    private String birthday;
    // 职业
    private String career;
    // 血型
    private String bloodType;
    // 别名
    private String alias;
    // 社团
    private String group;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public String getTag() {
        boolean hasBirth = hasBirthday();
        String[] s = null;
        boolean isShort = false;
        if (hasBirth) {
            s = birthday.split("-");
            isShort = s.length < 3;
        }
        return (hasGender() ? "性别：" + gender + "\n" : "")
                + (hasBirth ? "生日：" + birthday + (isShort ? "" : TimeUtil.yearToAge(Integer.parseInt(s[0]))) + "\n"
                + "星座：" + (isShort ? TimeUtil.getConstellation(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                : TimeUtil.getConstellation(Integer.parseInt(s[1]), Integer.parseInt(s[2]))) + "\n" : "")
                + (hasCareer() ? "职业：" + career + "\n" : "")
                + (hasBloodType() ? "血型：" + bloodType + "\n" : "")
                + (hasAlias() ? "别名：" + alias + "\n" : "")
                + (hasGroup() ? "社团：" + group + "\n" : "");
    }

    public boolean fromME() {
        return source == NetMusicSource.ME;
    }

    public boolean isCV() {
        return fromME() && !isOrganization;
    }

    public boolean isOrganization() {
        return isOrganization;
    }

    public boolean hasCoverImgUrl() {
        return StringUtil.isNotEmpty(coverImgUrl);
    }

    public boolean hasCoverImg() {
        return coverImg != null;
    }

    public boolean hasName() {
        return StringUtil.isNotEmpty(name);
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

    public boolean hasGender() {
        return StringUtil.isNotEmpty(gender);
    }

    public boolean hasBirthday() {
        return StringUtil.isNotEmpty(birthday);
    }

    public boolean hasCareer() {
        return StringUtil.isNotEmpty(career);
    }

    public boolean hasBloodType() {
        return StringUtil.isNotEmpty(bloodType);
    }

    public boolean hasAlias() {
        return StringUtil.isNotEmpty(alias);
    }

    public boolean hasGroup() {
        return StringUtil.isNotEmpty(group);
    }

    public boolean hasDescription() {
        return StringUtil.isNotEmpty(description);
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
