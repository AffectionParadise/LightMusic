package net.doge.sdk.service.music.search.impl.musicsearch;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.net.UrlUtil;
import net.doge.util.core.text.HtmlUtil;

import java.util.LinkedList;
import java.util.List;

public class MeMusicSearchReq {
    private static MeMusicSearchReq instance;

    private MeMusicSearchReq() {
    }

    public static MeMusicSearchReq getInstance() {
        if (instance == null) instance = new MeMusicSearchReq();
        return instance;
    }

    // 关键词搜索节目 API (猫耳)
    private final String SEARCH_PROGRAM_ME_API = "https://www.missevan.com/sound/getsearch?cid=%s&s=%s&p=%s&type=3&page_size=%s";

    /**
     * 根据关键词获取节目
     */
    public CommonResult<NetMusicInfo> searchProgram(String subType, String keyword, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.programSearchTags.get(subType);

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);
        if (StringUtil.notEmpty(s[0])) {
            String musicInfoBody = HttpRequest.get(String.format(SEARCH_PROGRAM_ME_API, s[0].trim(), encodedKeyword, page, limit))
                    .executeAsStr();
            JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
            JSONObject data = musicInfoJson.getJSONObject("info");
            t = data.getJSONObject("pagination").getIntValue("count");
            JSONArray songArray = data.getJSONArray("Datas");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = HtmlUtil.removeHtmlLabel(songJson.getString("soundstr"));
                String artist = songJson.getString("username");
                String artistId = songJson.getString("user_id");
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
