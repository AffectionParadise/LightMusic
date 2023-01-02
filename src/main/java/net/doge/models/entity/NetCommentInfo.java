package net.doge.models.entity;

import lombok.Data;
import net.doge.utils.StringUtils;

import java.awt.image.BufferedImage;

/**
 * @Author yzx
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
    // 点赞数
    private Integer likedCount;
    // 评分
    private Integer score;

    // 缩略图加载后的回调函数
    private Runnable invokeLater;

    public boolean hasTime() {
        return StringUtils.isNotEmpty(time);
    }

    public boolean hasProfileUrl() {
        return StringUtils.isNotEmpty(profileUrl);
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
        if (invokeLater != null) {
            invokeLater.run();
            // 调用后丢弃
            invokeLater = null;
        }
    }

    public String toString() {
        return username
                + (hasTime() ? "    " + time : "")
                + (hasScore() ? "    " + StringUtils.genStar(score) + " " + score + " 分" : "") + "\n"
                + content + "\n"
                + (hasLikedCount() ? "❤ " + StringUtils.formatNumberWithoutSuffix(likedCount) : "");
    }

    public String toSimpleString() {
        return username + "：" + content;
    }
}
