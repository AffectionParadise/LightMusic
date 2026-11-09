package net.doge.sdk.service.playlist.rcmd.impl.highqualityplaylist;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetPlaylistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QiHighQualityPlaylistReq {
    private static QiHighQualityPlaylistReq instance;

    private QiHighQualityPlaylistReq() {
    }

    public static QiHighQualityPlaylistReq getInstance() {
        if (instance == null) instance = new QiHighQualityPlaylistReq();
        return instance;
    }

    // 分类歌单 API (千千)
    private final String CAT_PLAYLIST_QI_API = "https://music.91q.com/v1/tracklist/list";

    /**
     * 分类歌单
     */
    public CommonResult<NetPlaylistInfo> getCatPlaylists(String tag, int page, int limit) {
        List<NetPlaylistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.hotPlaylistTags.get(tag);

        String param = s[TagType.CAT_PLAYLIST_QI];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new TreeMap<>();
            params.put("pageNo", page);
            params.put("pageSize", limit);
            params.put("subCateId", param.trim());
            String playlistInfoBody = SdkCommon.qiRequest(CAT_PLAYLIST_QI_API, params)
                    .executeAsStr();
            JSONObject playlistInfoJson = JSONObject.parseObject(playlistInfoBody);
            JSONObject data = playlistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray playlistArray = data.getJSONArray("result");
            for (int i = 0, len = playlistArray.size(); i < len; i++) {
                JSONObject playlistJson = playlistArray.getJSONObject(i);

                String playlistId = playlistJson.getString("id");
                String playlistName = playlistJson.getString("title");
                Integer trackCount = playlistJson.getIntValue("trackCount");
                String coverImgThumbUrl = playlistJson.getString("pic");

                NetPlaylistInfo playlistInfo = new NetPlaylistInfo();
                playlistInfo.setSource(NetResourceSource.QI);
                playlistInfo.setId(playlistId);
                playlistInfo.setName(playlistName);
                playlistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                playlistInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    playlistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(playlistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
