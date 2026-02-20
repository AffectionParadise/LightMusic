package net.doge.sdk.service.artist.rcmd.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.constant.service.tag.TagType;
import net.doge.constant.service.tag.Tags;
import net.doge.entity.service.NetArtistInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.kg.KugouReqOptEnum;
import net.doge.sdk.common.opt.kg.KugouReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KgArtistListReq {
    private static KgArtistListReq instance;

    private KgArtistListReq() {
    }

    public static KgArtistListReq getInstance() {
        if (instance == null) instance = new KgArtistListReq();
        return instance;
    }

    // 热门歌手推荐 API (酷狗)
    private final String HOT_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=1&page=%s&pagesize=%s";
    // 飙升歌手推荐 API (酷狗)
    private final String UP_ARTIST_LIST_KG_API = "http://mobilecdnbj.kugou.com/api/v5/singer/list?sextype=%s&type=%s&sort=2&page=%s&pagesize=%s";
    // 编辑精选歌手 API (酷狗)
    private final String IP_ARTIST_KG_API = "/openapi/v1/ip/author_list";

    /**
     * 热门歌手
     */
    public CommonResult<NetArtistInfo> getHotArtist(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.HOT_ARTIST_LIST_KG];
        if (StringUtil.notEmpty(param)) {
            String[] split = param.split(" ");
            String artistInfoBody = HttpRequest.get(String.format(HOT_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("info");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("singerid");
                String artistName = artistJson.getString("singername");
//            Integer songNum = artistJson.getIntValue("songcount");
//            Integer albumNum = artistJson.getIntValue("albumcount");
//            Integer mvNum = artistJson.getIntValue("mvcount");
                String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
//            artistInfo.setSongNum(songNum);
//            artistInfo.setAlbumNum(albumNum);
//            artistInfo.setMvNum(mvNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 飙升歌手
     */
    public CommonResult<NetArtistInfo> getUpArtist(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.HOT_ARTIST_LIST_KG];
        if (StringUtil.notEmpty(param)) {
            String[] split = param.split(" ");
            String artistInfoBody = HttpRequest.get(String.format(UP_ARTIST_LIST_KG_API, split[0], split[1], page, limit))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            t = data.getIntValue("total");
            JSONArray artistArray = data.getJSONArray("info");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("singerid");
                String artistName = artistJson.getString("singername");
//                    Integer songNum = artistJson.getIntValue("songcount");
//                    Integer albumNum = artistJson.getIntValue("albumcount");
//                    Integer mvNum = artistJson.getIntValue("mvcount");
                String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
//                    artistInfo.setSongNum(songNum);
//                    artistInfo.setAlbumNum(albumNum);
//                    artistInfo.setMvNum(mvNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 编辑精选歌手
     */
    public CommonResult<NetArtistInfo> getIpArtist(String tag, int page, int limit) {
        List<NetArtistInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.artistTags.get(tag);

        String param = s[TagType.IP_ARTIST_KG];
        if (StringUtil.notEmpty(param)) {
            Map<KugouReqOptEnum, Object> options = KugouReqOptsBuilder.androidPost(IP_ARTIST_KG_API);
            String dat = String.format("{\"is_publish\":1,\"ip_id\":\"%s\",\"sort\":3,\"page\":%s,\"pagesize\":%s,\"query\":1}", param, page, limit);
            String artistInfoBody = SdkCommon.kgRequest(null, dat, options)
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            t = artistInfoJson.getIntValue("total");
            JSONArray artistArray = artistInfoJson.getJSONArray("data");
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);
                JSONObject base = artistJson.getJSONObject("base");

                String artistId = base.getString("author_id");
                String artistName = base.getString("author_name");
                String coverImgThumbUrl = base.getString("avatar").replace("{size}", "240");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetResourceSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                r.add(artistInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
