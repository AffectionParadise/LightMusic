package net.doge.sdk.util;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.ui.ImageConstants;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.ui.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.StringJoiner;

public class SdkUtil {
    /**
     * 获取艺术家数组
     *
     * @param json
     * @return
     */
    private static JSONArray getArtistArray(JSONObject json) {
        String[] artistKeys = {"artists", "artist", "ar", "authors", "singers", "Singers", "singer_list", "singerList", "singer", "singerinfo", "creator", "actors"};
        JSONArray artistArray = null;
        for (String key : artistKeys) {
            if (!json.containsKey(key)) continue;
            artistArray = json.getJSONArray(key);
            break;
        }
        return artistArray;
    }

    /**
     * 解析歌曲艺术家名称
     *
     * @param json
     * @return
     */
    public static String parseArtist(JSONObject json) {
        JSONArray artistArray = getArtistArray(json);
        if (JsonUtil.isEmpty(artistArray)) return "";

        // 获取艺术家名称 key
        JSONObject first = artistArray.getJSONObject(0);
        String[] nameKeys = {"name", "singer_name", "author_name", "singername", "userName"};
        String nameKey = null;
        for (String key : nameKeys) {
            if (!first.containsKey(key)) continue;
            nameKey = key;
            break;
        }

        StringJoiner sj = new StringJoiner("、");
        for (int i = 0, len = artistArray.size(); i < len; i++) {
            String name = artistArray.getJSONObject(i).getString(nameKey);
            sj.add(name);
        }
        return sj.toString();
    }

    /**
     * 解析歌曲艺术家 id
     *
     * @param json
     * @return
     */
    public static String parseArtistId(JSONObject json) {
        JSONArray artistArray = getArtistArray(json);
        if (JsonUtil.isEmpty(artistArray)) return "";

        // 获取艺术家 id
        JSONObject first = artistArray.getJSONObject(0);
        String[] idKeys = {"singermid", "singer_mid", "mid", "singerid", "artistCode", "artistId", "author_id", "userId", "id"};
        for (String key : idKeys) {
            if (!first.containsKey(key)) continue;
            return first.getString(key);
        }

        return "";
    }

    /**
     * 根据链接获取图片
     */
    public static BufferedImage getImageFromUrl(String url) {
        BufferedImage img = ImageUtil.readByUrl(url);
        return img == null ? ImageConstants.DEFAULT_IMG : img;
    }

    /**
     * 提取头像
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractProfile(String imgUrl) {
        BufferedImage img = ImageUtil.width(imgUrl, ImageConstants.PROFILE_WIDTH);
        if (img == null) img = ImageUtil.width(ImageConstants.DEFAULT_IMG, ImageConstants.PROFILE_WIDTH);
        return ImageUtil.setRadius(img, 0.1);
    }

    /**
     * 提取封面
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractCover(String imgUrl) {
        BufferedImage img = ImageUtil.width(imgUrl, ImageConstants.MEDIUM_WIDTH);
        if (img == null) img = ImageUtil.width(ImageConstants.DEFAULT_IMG, ImageConstants.MEDIUM_WIDTH);
        // 控制封面高度不超过阈值
        if (img.getHeight() > ImageConstants.MV_COVER_MAX_HEIGHT)
            img = ImageUtil.height(img, ImageConstants.MV_COVER_MAX_HEIGHT);
        return ImageUtil.setRadius(img, 0.1);
    }

    /**
     * 提取 MV 封面
     *
     * @param imgUrl
     * @return
     */
    public static BufferedImage extractMvCover(String imgUrl) {
        BufferedImage img = ImageUtil.width(imgUrl, ImageConstants.MV_COVER_WIDTH);
        if (img == null) img = ImageUtil.width(ImageConstants.DEFAULT_IMG, ImageConstants.MV_COVER_WIDTH);
        // 控制 MV 封面高度不超过阈值
        if (img.getHeight() > ImageConstants.MV_COVER_MAX_HEIGHT)
            img = ImageUtil.height(img, ImageConstants.MV_COVER_MAX_HEIGHT);
        return ImageUtil.setRadius(img, 0.1);
    }

    /**
     * 连接 Json 数组中的所有字符串
     */
    public static String joinString(JSONArray array) {
        return joinString(array, "、");
    }

    public static String joinString(JSONArray array, String delimiter) {
        StringJoiner sj = new StringJoiner(delimiter);
        for (int i = 0, len = array.size(); i < len; i++) sj.add(array.getString(i));
        return sj.toString();
    }

    /**
     * 解析歌单标签
     */
    public static String parseTag(JSONObject json) {
        // 获取标签数组
        String[] tagKeys = {"tags", "tagList"};
        JSONArray tagArray = null;
        for (String key : tagKeys) {
            if (!json.containsKey(key)) continue;
            tagArray = json.getJSONArray(key);
            break;
        }
        if (JsonUtil.isEmpty(tagArray)) return "";

        StringJoiner sj = new StringJoiner("、");
        for (int i = 0, len = tagArray.size(); i < len; i++) {
            Object obj = tagArray.get(i);
            if (obj instanceof String) sj.add(tagArray.getString(i));
            else if (obj instanceof JSONObject) {
                JSONObject tagJson = (JSONObject) obj;
                String name = tagJson.getString("name");
                if (StringUtil.isEmpty(name)) name = tagJson.getString("tagName");
                if (StringUtil.isEmpty(name)) name = tagJson.getString("tagname");
                if (StringUtil.notEmpty(name)) sj.add(name);
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
            HttpResponse resp = HttpRequest.get(url)
                    .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                    .executeAsync();
            return resp.header("Location");
        } catch (Exception e) {
            return "";
        }
    }
}
