package net.doge.sdk.util;

import cn.hutool.http.HttpRequest;
import net.doge.constant.ui.ImageConstants;
import net.doge.constant.system.NetMusicSource;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.ui.ImageUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;

public class SdkUtil {
    /**
     * 解析歌曲艺术家
     */
    public static String parseArtists(JSONObject json, int source) {
        JSONArray artistsArray;
        if (source == NetMusicSource.QQ) {
            artistsArray = json.optJSONArray("singer");
            if (artistsArray == null) artistsArray = json.optJSONArray("singer_list");
            if (artistsArray == null) artistsArray = json.optJSONArray("singers");
            if (artistsArray == null) artistsArray = json.optJSONArray("ar");
        } else if (source == NetMusicSource.KG) {
            artistsArray = json.optJSONArray("authors");
        } else {
            artistsArray = json.optJSONArray("artists");
            if (artistsArray == null) artistsArray = json.optJSONArray("ar");
            if (artistsArray == null) artistsArray = json.optJSONArray("artist");
            if (artistsArray == null) artistsArray = json.optJSONArray("actors");
        }
        if (artistsArray == null) return "";

        StringJoiner sj = new StringJoiner("、");
        for (int i = 0, len = artistsArray.size(); i < len; i++) {
            JSONObject artistJson = artistsArray.getJSONObject(i);
            String name = artistJson.optString("name", null);
            if (name == null) name = artistJson.optString("singer_name", null);
            if (name == null) name = artistJson.getString("author_name");
            sj.add(name);
        }
        return sj.toString();
    }

    /**
     * 根据链接获取图片
     */
    public static BufferedImage getImageFromUrl(String url) {
        return ImageUtil.read(HttpRequest.get(url).setFollowRedirects(true).execute().bodyStream());
    }

    /**
     * 提取头像
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractProfile(String imgUrl) {
        return ImageUtil.setRadius(ImageUtil.width(imgUrl, ImageConstants.profileWidth), 0.1);
    }

    /**
     * 提取封面
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractCover(String imgUrl) {
        BufferedImage img = ImageUtil.width(imgUrl, ImageConstants.mediumWidth);
        if (img == null) return null;
        // 控制封面高度不超过阈值
        if (img.getHeight() > ImageConstants.mvCoverMaxHeight)
            img = ImageUtil.height(img, ImageConstants.mvCoverMaxHeight);
        return ImageUtil.setRadius(img, 0.1);
    }

    /**
     * 提取 MV 封面
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractMvCover(String imgUrl) {
        BufferedImage img = ImageUtil.width(imgUrl, ImageConstants.mvCoverWidth);
        if (img == null) return null;
        // 控制 MV 封面高度不超过阈值
        if (img.getHeight() > ImageConstants.mvCoverMaxHeight)
            img = ImageUtil.height(img, ImageConstants.mvCoverMaxHeight);
        return ImageUtil.setRadius(img, 0.1);
    }

    /**
     * 解析视频作者
     */
    public static String parseCreators(JSONObject json) {
        JSONArray artistsArray = json.getJSONArray("creator");
        StringJoiner sj = new StringJoiner("、");
        for (int i = 0, len = artistsArray.size(); i < len; i++) {
            sj.add(artistsArray.getJSONObject(i).getString("userName"));
        }
        return sj.toString();
    }

    /**
     * 连接 Json 数组中的所有字符串
     */
    public static String joinStrings(JSONArray array, int limit) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = array.size(); i < len; i++) {
            sb.append(array.getString(i));
            if (i != len - 1) sb.append("、");
            if (limit > 0 && i >= limit) {
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 解析歌单标签
     */
    public static String parseTags(JSONObject json, int source) {
        JSONArray tagArray;
        StringJoiner sj = new StringJoiner("、");
        if (source == NetMusicSource.NET_CLOUD) {
            tagArray = json.getJSONArray("tags");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                sj.add(tagArray.getString(i));
            }
        } else if (source == NetMusicSource.QQ) {
            tagArray = json.getJSONArray("tags");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sj.add(tagJson.getString("name"));
            }
        } else if (source == NetMusicSource.MG) {
            tagArray = json.getJSONArray("tags");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sj.add(tagJson.getString("tagName"));
            }
        } else if (source == NetMusicSource.XM) {
            tagArray = json.getJSONArray("tags");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                sj.add(tagArray.getString(i));
            }
        } else if (source == NetMusicSource.QI) {
            tagArray = json.getJSONArray("tagList");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                sj.add(tagArray.getString(i));
            }
        } else if (source == NetMusicSource.ME) {
            tagArray = json.getJSONArray("tags");
            for (int i = 0, len = tagArray.size(); i < len; i++) {
                JSONObject tagJson = tagArray.getJSONObject(i);
                sj.add(tagJson.getString("name"));
            }
        }
        return sj.toString();
    }

    /**
     * 获取重定向之后的 url
     *
     * @param url
     * @return
     */
    public static String getRedirectUrl(String url) {
        try {
            // 获取重定向之后的 url
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(SdkCommon.TIME_OUT);
            return conn.getHeaderField("Location");
        } catch (IOException e) {
            return "";
        }
    }
}
