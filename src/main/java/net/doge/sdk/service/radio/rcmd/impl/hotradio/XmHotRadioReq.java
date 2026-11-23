package net.doge.sdk.service.radio.rcmd.impl.hotradio;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagModule;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetRadioInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class XmHotRadioReq {
    private static XmHotRadioReq instance;

    private XmHotRadioReq() {
    }

    public static XmHotRadioReq getInstance() {
        if (instance == null) instance = new XmHotRadioReq();
        return instance;
    }

    // 分类电台 API (喜马拉雅)
    private final String CAT_RADIO_XM_API
            = "https://www.ximalaya.com/revision/category/queryCategoryPageAlbums";
    // 频道电台 API (喜马拉雅)
    private final String CHANNEL_RADIO_XM_API = "https://www.ximalaya.com/revision/metadata/v2/channel/albums";
    // 分类电台榜 API (喜马拉雅)
    private final String CAT_RADIO_RANK_XM_API = "https://www.ximalaya.com/revision/rank/v3/element";

    /**
     * 分类电台
     */
    public CommonResult<NetRadioInfo> getCatRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagModule.CAT_RADIO_XM];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ", -1);
            Map<String, Object> params = new HashMap<>();
            params.put("category", sp[0]);
            params.put("subcategory", sp[1]);
            params.put("meta", "");
            params.put("sort", 0);
            params.put("page", page);
            params.put("perPage", limit);
            params.put("useCache", false);
            String radioInfoBody = HttpRequest.get(CAT_RADIO_XM_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray radioArray = data.getJSONArray("albums");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("albumId");
                String radioName = radioJson.getString("title");
                String dj = radioJson.getString("anchorName");
                String djId = radioJson.getString("uid");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("trackCount");
                String category = tag;
                String coverImgThumbUrl = "https:" + radioJson.getString("coverPath");
                coverImgThumbUrl = coverImgThumbUrl.substring(0, coverImgThumbUrl.lastIndexOf('!'));

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);

                final String finalCoverImgThumbUrl = coverImgThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(finalCoverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 频道电台
     */
    public CommonResult<NetRadioInfo> getChannelRadios(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagModule.CHANNEL_RADIO_XM];
        if (StringUtil.notEmpty(param)) {
            Map<String, Object> params = new HashMap<>();
            params.put("groupId", param);
            params.put("pageNum", page);
            params.put("pageSize", limit);
            params.put("sort", 1);
            params.put("metadata", "");
            String radioInfoBody = HttpRequest.get(CHANNEL_RADIO_XM_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray radioArray = data.getJSONArray("albums");
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("albumId");
                String radioName = radioJson.getString("albumTitle");
                String dj = radioJson.getString("albumUserNickName");
                Long playCount = radioJson.getLong("albumPlayCount");
                Integer trackCount = radioJson.getIntValue("albumTrackCount");
                String category = tag;
                String coverImgThumbUrl = "https://imagev2.xmcdn.com/" + radioJson.getString("albumCoverPath");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 排行榜
     */
    public CommonResult<NetRadioInfo> getCatRadioRank(String tag, int page, int limit) {
        List<NetRadioInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.radioTags.get(tag);

        String param = s[TagModule.CAT_RADIO_XM];
        if (StringUtil.notEmpty(param)) {
            String[] sp = param.split(" ");
            Map<String, Object> params = new HashMap<>();
            params.put("typeId", sp[0]);
            params.put("clusterId", sp[1]);
            String radioInfoBody = HttpRequest.get(CAT_RADIO_RANK_XM_API)
                    .queryParams(params)
                    .executeAsStr();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray radioArray = radioInfoJson.getJSONObject("data").getJSONArray("rankList").getJSONObject(0).getJSONArray("albums");
            t = radioArray.size();
            for (int i = (page - 1) * limit, len = Math.min(radioArray.size(), page * limit); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("albumTitle");
                String dj = radioJson.getString("anchorName");
                String djId = radioJson.getString("anchorUrl").replace("/zhubo/", "");
                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("trackCount");
                String category = radioJson.getString("categoryTitle");
                String coverImgThumbUrl = "https://imagev2.xmcdn.com/" + radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetResourceSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(radioInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
