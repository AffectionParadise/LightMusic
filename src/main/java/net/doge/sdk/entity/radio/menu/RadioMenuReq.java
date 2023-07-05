package net.doge.sdk.entity.radio.menu;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.constant.model.RadioType;
import net.doge.model.entity.NetArtistInfo;
import net.doge.model.entity.NetRadioInfo;
import net.doge.model.entity.NetUserInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class RadioMenuReq {
    // 电台订阅者 API
    private final String RADIO_SUBSCRIBERS_API = SdkCommon.prefix + "/dj/subscriber?id=%s";
    
    // 电台 CV API (猫耳)
    private final String RADIO_CVS_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 电台演职员 API (豆瓣)
    private final String RADIO_ARTISTS_DB_API = "https://movie.douban.com/subject/%s/celebrities";
    
    // 相似电台 API (猫耳)
    private final String SIMILAR_RADIO_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 相似电台 API (豆瓣)
    private final String SIMILAR_RADIO_DB_API = "https://movie.douban.com/subject/%s/";
    // 相似图书电台 API (豆瓣)
    private final String SIMILAR_BOOK_RADIO_DB_API = "https://book.douban.com/subject/%s/";
    // 相似游戏电台 API (豆瓣)
    private final String SIMILAR_GAME_RADIO_DB_API = "https://www.douban.com/game/%s/";

    /**
     * 获取电台订阅者
     *
     * @return
     */
    public CommonResult<NetUserInfo> getRadioSubscribers(NetRadioInfo netRadioInfo, int limit, int page) {
        int source = netRadioInfo.getSource();
        String id = netRadioInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(RADIO_SUBSCRIBERS_API, id))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("subscribers");
            t = userArray.size();
            for (int i = (page - 1) * limit, len = Math.min(userArray.size(), page * limit); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getInt("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                String sign = userJson.getString("signature");
                String avatarThumbUrl = userJson.getString("avatarUrl");
//                Integer follow = userJson.getInt("follows");
//                Integer followed = userJson.getInt("followeds");
//                Integer playlistCount = userJson.getInt("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                userInfo.setSign(sign);
//                userInfo.setFollow(follow);
//                userInfo.setFollowed(followed);
//                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取相似电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getSimilarRadios(NetRadioInfo radioInfo) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;

        // 豆瓣
        if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(isBook ? SIMILAR_BOOK_RADIO_DB_API
                            : isGame ? SIMILAR_GAME_RADIO_DB_API : SIMILAR_RADIO_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements rs = doc.select(isBook ? "div#db-rec-section dl:not(.clear)"
                    : isGame ? "div.list.fav-list li" : "div.recommendations-bd dl");
            t = rs.size();
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select(isGame ? "div.text a" : "dd a").first();
                Element img = radio.select("img").first();

                String radioId = ReUtil.get(isGame ? "game/(\\d+)/" : "subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text().trim();
                String coverImgThumbUrl = img.attr("src");

                NetRadioInfo ri = new NetRadioInfo();
                ri.setType(isBook ? RadioType.BOOK : isGame ? RadioType.GAME : RadioType.RADIO);
                ri.setSource(NetMusicSource.DB);
                ri.setId(radioId);
                ri.setName(radioName);
                ri.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    ri.setCoverImgThumb(coverImgThumb);
                });
                res.add(ri);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(SIMILAR_RADIO_ME_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("info");
            JSONArray radioArray = data.getJSONArray("recommend");
            t = radioArray.size();
            for (int i = 0, len = radioArray.size(); i < len; i++) {
                JSONObject radioJson = radioArray.getJSONObject(i);

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String coverImgThumbUrl = radioJson.getString("front_cover");
                Long playCount = radioJson.getLong("view_count");

                NetRadioInfo ri = new NetRadioInfo();
                ri.setSource(NetMusicSource.ME);
                ri.setId(radioId);
                ri.setName(radioName);
                ri.setPlayCount(playCount);
                ri.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    ri.setCoverImgThumb(coverImgThumb);
                });

                res.add(ri);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取电台演职员
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getRadioArtists(NetRadioInfo netRadioInfo) {
        int source = netRadioInfo.getSource();
        String id = netRadioInfo.getId();
        boolean isBook = netRadioInfo.isBook();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 猫耳
        if (source == NetMusicSource.ME) {
            String artistInfoBody = HttpRequest.get(String.format(RADIO_CVS_ME_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("info");
            JSONArray artistArray = data.getJSONArray("cvs");
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i).getJSONObject("cv_info");

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String avatarThumbUrl = artistJson.getString("icon");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(avatarThumbUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    artistInfo.setCoverImgThumb(avatarThumb);
                });

                res.add(artistInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB && !isBook) {
            String artistInfoBody = HttpRequest.get(String.format(RADIO_ARTISTS_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select("li.celebrity");
            t = cs.size();
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select("span.name a").first();
                Element img = artist.select("div.avatar").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = ReUtil.get("url\\((.*?)\\)", img.attr("style"), 1);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.DB);
                artistInfo.setId(artistId);
                artistInfo.setName(artistName);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(artistInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
