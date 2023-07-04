package net.doge.sdk.entity.artist.menu;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetArtistInfo;
import net.doge.model.entity.NetRadioInfo;
import net.doge.model.entity.NetUserInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class ArtistMenuReq {
    // 相似歌手 API
    private final String SIMILAR_ARTIST_API = SdkCommon.prefix + "/simi/artist?id=%s";
    // 相似歌手 API (酷狗)(POST)
    private final String SIMILAR_ARTIST_KG_API = "http://kmr.service.kugou.com/v1/author/similar";
    // 相似歌手 API (QQ)
    private final String SIMILAR_ARTIST_QQ_API = SdkCommon.prefixQQ33 + "/singer/sim?singermid=%s";

    // 歌手粉丝 API
    private final String ARTIST_FANS_API = SdkCommon.prefix + "/artist/fans?id=%s&offset=%s&limit=%s";
    // 歌手粉丝总数 API
    private final String ARTIST_FANS_TOTAL_API = SdkCommon.prefix + "/artist/follow/count?id=%s";
    // 社团职员 API (猫耳)
    private final String ORGANIZATION_STAFFS_ME_API = "https://www.missevan.com/organization/staff?organization_id=%s&page=%s";
    // 歌手粉丝 API (豆瓣)
    private final String ARTIST_FANS_DB_API = "https://movie.douban.com/celebrity/%s/fans?start=%s";
    // 社团声优 API (猫耳)
    private final String ORGANIZATION_CVS_ME_API = "https://www.missevan.com/organization/cast?organization_id=%s&page=%s";
    // 歌手合作人 API (豆瓣)
    private final String ARTIST_BUDDY_DB_API = "https://movie.douban.com/celebrity/%s/partners?start=%s";

    // 歌手电台 API (豆瓣)
    private final String ARTIST_RADIO_DB_API = "https://movie.douban.com/celebrity/%s/movies?start=%s&format=pic&sortby=time";
    // 社团电台 API (猫耳)
    private final String ORGANIZATION_RADIOS_ME_API = "https://www.missevan.com/organization/drama?organization_id=%s&page=%s";

    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";

    /**
     * 获取相似歌手 (通过歌手)
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getSimilarArtists(NetArtistInfo netArtistInfo, int page) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.optJSONArray("artists");
            if (artistArray != null) {
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("id");
                    String artistName = artistJson.getString("name");
                    Integer songNum = artistJson.getInt("musicSize");
                    Integer albumNum = artistJson.getInt("albumSize");
//                Integer mvNum = artistJson.optInt("mvSize");
                    String coverImgThumbUrl = artistJson.getString("img1v1Url");

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setId(artistId);
                    artistInfo.setName(artistName);
                    artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    artistInfo.setSongNum(songNum);
                    artistInfo.setAlbumNum(albumNum);
//                artistInfo.setMvNum(mvNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        artistInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(artistInfo);
                }
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.post(String.format(SIMILAR_ARTIST_KG_API))
                    .body("{\"clientver\":\"9108\",\"mid\":\"286974383886022203545511837994020015101\"," +
                            "\"clienttime\":\"1545746019\",\"key\":\"4c8b684568f03eeef985ae271561bcd8\"," +
                            "\"appid\":\"1005\",\"data\":[{\"author_id\":" + id + "}]}")
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONArray("data").getJSONArray(0);
            t = artistArray.size();
            for (int i = 0, len = artistArray.size(); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("author_id");
                String artistName = artistJson.getString("author_name");
                String coverImgThumbUrl = artistJson.getString("sizable_avatar").replace("{size}", "240");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.KG);
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

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(SIMILAR_ARTIST_QQ_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONArray artistArray = artistInfoJson.getJSONObject("data").optJSONArray("list");
            if (artistArray != null) {
                t = artistArray.size();
                for (int i = 0, len = artistArray.size(); i < len; i++) {
                    JSONObject artistJson = artistArray.getJSONObject(i);

                    String artistId = artistJson.getString("mid");
                    String artistName = artistJson.getString("name");
                    String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, artistId);

                    NetArtistInfo artistInfo = new NetArtistInfo();
                    artistInfo.setSource(NetMusicSource.QQ);
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
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 获取歌手粉丝
     *
     * @return
     */
    public CommonResult<NetUserInfo> getArtistFans(NetArtistInfo netArtistInfo, int limit, int page) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetUserInfo> res = new LinkedList<>();
        AtomicReference<Integer> t = new AtomicReference<>(0);

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            Runnable getFans = () -> {
                String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_API, id, (page - 1) * limit, limit))
                        .execute()
                        .body();
                JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
                JSONArray userArray = userInfoJson.getJSONArray("data");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i).getJSONObject("userProfile");

                    String userId = userJson.getString("userId");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getInt("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
//                    String sign = userJson.getString("signature");
                    String avatarThumbUrl = userJson.getString("avatarUrl");
//                    Integer follow = userJson.getInt("follows");
//                    Integer followed = userJson.getInt("followeds");
//                    Integer playlistCount = userJson.getInt("playlistCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
//                    userInfo.setSign(sign);
//                    userInfo.setFollow(follow);
//                    userInfo.setFollowed(followed);
//                    userInfo.setPlaylistCount(playlistCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    res.add(userInfo);
                }
            };

            Runnable getFansCnt = () -> {
                String tBody = HttpRequest.get(String.format(ARTIST_FANS_TOTAL_API, id))
                        .execute()
                        .body();
                t.set(JSONObject.fromObject(tBody).getJSONObject("data").getInt("fansCnt"));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getFans));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFansCnt));

            taskList.forEach(task -> {
                try {
                    task.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(ORGANIZATION_STAFFS_ME_API, id, page))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.fromObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray userArray = data.getJSONArray("staff");
            t.set(data.getJSONObject("pagination").getInt("count"));
            for (int i = (page - 1) * limit, len = Math.min(page * limit, userArray.size()); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("name");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);

                String finalAvatarThumbUrl = avatarThumbUrl;
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            final int rn = 35;
            String userInfoBody = HttpRequest.get(String.format(ARTIST_FANS_DB_API, id, (page - 1) * rn))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            String ts = ReUtil.get("（(\\d+)）", doc.select("div#content > h1").text(), 1);
            int tn = Integer.parseInt(ts);
            t.set(tn -= tn / rn * 15);
            Elements us = doc.select("dl.obu");
            for (int i = 0, len = us.size(); i < len; i++) {
                Element user = us.get(i);
                Elements a = user.select("dd a");
                Elements img = user.select("img");

                String userId = ReUtil.get("/people/(.*?)/", a.attr("href"), 1);
                String userName = a.text();
                String gender = "保密";
                String src = img.attr("src");
                String avatarThumbUrl = src.contains("/user") ? src.replaceFirst("normal", "large") : src.replaceFirst("/u", "/ul");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.DB);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t.get());
    }

    /**
     * 获取歌手合作人
     *
     * @return
     */
    public CommonResult<NetArtistInfo> getArtistBuddies(NetArtistInfo netArtistInfo, int page, int limit) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 0;
        final int dbLimit = 10;

        // 猫耳
        if (source == NetMusicSource.ME) {
            String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_CVS_ME_API, id, page))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.fromObject(artistInfoBody);
            JSONObject info = artistInfoJson.optJSONObject("info");
            t = info.getJSONObject("pagination").getInt("count");
            JSONArray artistArray = info.getJSONArray("cast");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, artistArray.size()); i < len; i++) {
                JSONObject artistJson = artistArray.getJSONObject(i);

                String artistId = artistJson.getString("id");
                String artistName = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("avatar");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.ME);
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

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_BUDDY_DB_API, id, (page - 1) * dbLimit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements cs = doc.select("div.partners.item");
            String ts = ReUtil.get("共(\\d+)条", doc.select("span.count").text(), 1);
            t = StringUtil.isEmpty(ts) ? cs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = cs.size(); i < len; i++) {
                Element artist = cs.get(i);
                Element a = artist.select("div.info a").first();
                Element img = artist.select("div.pic img").first();

                String artistId = ReUtil.get("celebrity/(\\d+)/", a.attr("href"), 1);
                String artistName = a.text();
                String coverImgThumbUrl = img.attr("src");

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

    /**
     * 获取歌手电台
     *
     * @return
     */
    public CommonResult<NetRadioInfo> getArtistRadios(NetArtistInfo netArtistInfo, int page, int limit) {
        int source = netArtistInfo.getSource();
        String id = netArtistInfo.getId();

        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 0;
        final int dbLimit = 10;

        // 猫耳
        if (source == NetMusicSource.ME) {
            if (netArtistInfo.isOrganization()) {
                String radioInfoBody = HttpRequest.get(String.format(ORGANIZATION_RADIOS_ME_API, id, page))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info");
                t = data.getJSONObject("pagination").getInt("count");
                JSONArray radioArray = data.getJSONArray("drama");
                for (int i = (page - 1) * limit, len = Math.min(page * limit, radioArray.size()); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i);

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String dj = netArtistInfo.getName();
                    String coverImgThumbUrl = "https:" + radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            } else {
                String radioInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, page, limit))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.fromObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("info").getJSONObject("dramas");
                JSONArray radioArray = data.getJSONArray("Datas");
                t = data.getJSONObject("pagination").getInt("count");
                for (int i = 0, len = radioArray.size(); i < len; i++) {
                    JSONObject radioJson = radioArray.getJSONObject(i).getJSONObject("drama");

                    String radioId = radioJson.getString("id");
                    String radioName = radioJson.getString("name");
                    String category = radioJson.getString("catalog_name");
                    String dj = netArtistInfo.getName();
                    Long playCount = radioJson.getLong("view_count");
                    String coverImgThumbUrl = radioJson.getString("cover");

                    NetRadioInfo radioInfo = new NetRadioInfo();
                    radioInfo.setSource(NetMusicSource.ME);
                    radioInfo.setId(radioId);
                    radioInfo.setName(radioName);
                    radioInfo.setDj(dj);
                    radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    radioInfo.setPlayCount(playCount);
                    radioInfo.setCategory(category);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        radioInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(radioInfo);
                }
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_RADIO_DB_API, id, (page - 1) * dbLimit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements rs = doc.select("div.grid_view > ul > li > dl");
            String ts = ReUtil.get("共(\\d+)条", doc.select("span.count").text(), 1);
            t = StringUtil.isEmpty(ts) ? rs.size() : Integer.parseInt(ts);
            t += t / limit * 10;
            for (int i = 0, len = rs.size(); i < len; i++) {
                Element radio = rs.get(i);
                Element a = radio.select("h6 a").first();
                Element span = radio.select("h6 span").first();
                Element img = radio.select("img").first();
                Elements dl = radio.select("dl > dd > dl");

                String radioId = ReUtil.get("subject/(\\d+)/", a.attr("href"), 1);
                String radioName = a.text();
                String dj = dl.text().trim();
                String coverImgThumbUrl = img.attr("src");
                String category = ReUtil.get("(\\d+)", span.text(), 1);

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.DB);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setCategory(category);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });
                res.add(radioInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
