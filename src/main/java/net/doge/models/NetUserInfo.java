package net.doge.models;

import lombok.Data;
import net.doge.constants.NetMusicSource;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * @Author yzx
 * @Description 用户
 * @Date 2020/12/7
 */
@Data
public class NetUserInfo {
    // 用户来源
    private int source = NetMusicSource.NET_CLOUD;
    // 用户 id
    private String id;
    // 用户名称
    private String name;
    // 性别
    private String gender;
    // 生日
    private String birthday;
    // 地区
    private String area;
    // 等级
    private Integer level;
    // 号龄
    private String accAge;
    // 头像
    private BufferedImage avatar;
    // 头像 url
    private String avatarUrl;
    // 头像缩略图
    private BufferedImage avatarThumb;
    // 头像缩略图 url
    private String avatarThumbUrl;
    // 背景图
    private BufferedImage bgImg;
    // 背景图 url (猫耳)
//    private String bgImgUrl;
    // 签名
    private String sign;
    // 关注数
    private Integer follow;
    // 粉丝数
    private Integer followed;
    // 歌单数
    private Integer playlistCount;
    // 电台数
    private Integer radioCount;
    // 节目数
    private Integer programCount;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;
    private Runnable invokeLater2;

    public String getTag() {
        boolean hasBirth = StringUtils.isNotEmpty(birthday);
        String[] s = null;
        boolean isShort = false;
        if (hasBirth) {
            s = birthday.split("-");
            isShort = s.length < 3;
        }
        return (hasLevel() ? "等级：Lv. " + level + "\n" : "")
                + (hasAccAge() ? "号龄：" + accAge + "\n" : "")
                + (hasGender() ? "性别：" + gender + "\n" : "")
                + (hasBirth ? "生日：" + birthday + (isShort ? "" : TimeUtils.yearToAge(Integer.parseInt(s[0]))) + "\n"
                + "星座：" + (isShort ? TimeUtils.getConstellation(Integer.parseInt(s[0]), Integer.parseInt(s[1]))
                : TimeUtils.getConstellation(Integer.parseInt(s[1]), Integer.parseInt(s[2]))) + "\n" : "")
                + (hasArea() ? "地区：" + area : "");
    }

    public boolean hasAvatar() {
        return avatar != null;
    }

    public boolean hasBgImg() {
        return bgImg != null;
    }

    public boolean hasName() {
        return StringUtils.isNotEmpty(name);
    }

    public boolean hasGender() {
        return StringUtils.isNotEmpty(gender);
    }

    public boolean hasBirthday() {
        return StringUtils.isNotEmpty(birthday);
    }

    public boolean hasAccAge() {
        return StringUtils.isNotEmpty(accAge);
    }

    public boolean hasArea() {
        return StringUtils.isNotEmpty(area);
    }

    public boolean hasLevel() {
        return level != null && level >= 0;
    }

    public boolean hasFollow() {
        return follow != null && follow >= 0;
    }

    public boolean hasFollowed() {
        return followed != null && followed >= 0;
    }

    public boolean hasPlaylistCount() {
        return playlistCount != null && playlistCount >= 0;
    }

    public boolean hasRadioCount() {
        return radioCount != null && radioCount >= 0;
    }

    public boolean hasProgramCount() {
        return programCount != null && programCount >= 0;
    }

    public boolean hasSign() {
        return StringUtils.isNotEmpty(sign);
    }

    public void setAvatarThumb(BufferedImage avatarThumb) {
        this.avatarThumb = avatarThumb;
        callback();
    }

    public void setAvatar(BufferedImage avatar) {
        this.avatar = avatar;
        callback();
    }

    public void setBgImg(BufferedImage bgImg) {
        this.bgImg = bgImg;
        callback2();
    }

    private void callback() {
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    private void callback2() {
        if (invokeLater2 != null) {
            invokeLater2.run();
            // 调用后丢弃
            invokeLater2 = null;
        }
    }

    /**
     * 判断用户信息是否完整
     *
     * @return
     */
    public boolean isIntegrated() {
        return avatar != null;
    }

    public boolean hasAvatarThumb() {
        return avatarThumb != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetUserInfo) {
            NetUserInfo netUserInfo = (NetUserInfo) o;
            return hashCode() == netUserInfo.hashCode();
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
