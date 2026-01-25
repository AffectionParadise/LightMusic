package net.doge.entity.service;

import lombok.Data;
import net.doge.util.core.LangUtil;
import net.doge.util.core.StringUtil;

import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 评论
 * @Date 2020/12/7
 */
@Data
public class NetCommentInfo {
    // 来源
    private int source;
    // 是否是子评论
    private boolean sub;
    // 用户 id
    private String userId;
    // 用户名
    private String username;
    // 头像
    private BufferedImage profile;
    // 头像 url
    private String profileUrl;
    // 内容
    private String content;
    // 时间
    private String time;
    // IP 属地
    private String location;
    // 点赞数
    private Integer likedCount;
    // 评分
    private Integer score;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean hasTime() {
        return StringUtil.notEmpty(time);
    }

    public boolean hasLocation() {
        return StringUtil.notEmpty(location);
    }

    public boolean hasProfileUrl() {
        return StringUtil.notEmpty(profileUrl);
    }

    public boolean hasLikedCount() {
        return likedCount != null && likedCount >= 0;
    }

    public boolean hasScore() {
        return score != null && score >= 0;
    }

    public void setProfile(BufferedImage profile) {
        this.profile = profile;
        callback();
    }

    private void callback() {
        if (invokeLater == null) return;
        invokeLater.run();
        // 调用后丢弃
        invokeLater = null;
    }

    public String toString() {
        return username
                + (hasTime() ? "    " + time : "")
                + (hasLocation() ? "    " + location : "")
                + (hasScore() ? "    " + LangUtil.genStar(score) + " " + score + " 分" : "") + "\n"
                + content + "\n"
                + (hasLikedCount() ? "❤ " + LangUtil.formatNumberWithoutSuffix(likedCount) : "");
    }

    public String toSimpleString() {
        return username + "：" + content;
    }
}
