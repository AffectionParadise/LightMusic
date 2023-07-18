package net.doge.sdk.entity.artist.info;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetAlbumInfo;
import net.doge.model.entity.NetArtistInfo;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetMvInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class ArtistInfoReq {
    // 歌手信息 API
    private final String ARTIST_DETAIL_API = SdkCommon.prefix + "/artists?id=%s";
    // 歌手信息 API (酷狗)
    private final String ARTIST_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/info?singerid=%s";
    // 歌手信息 API (QQ)
    private final String ARTIST_DETAIL_QQ_API = SdkCommon.prefixQQ33 + "/singer/desc?singermid=%s";
    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // 歌手信息 API (酷我)
    private final String ARTIST_DETAIL_KW_API = "https://kuwo.cn/api/www/artist/artist?artistid=%s&httpsStatus=1";
    // 歌手信息 API (咪咕)
    private final String ARTIST_DETAIL_MG_API = SdkCommon.prefixMg + "/singer/desc?id=%s";
    // 歌手信息 API (千千)
    private final String ARTIST_DETAIL_QI_API = "https://music.91q.com/v1/artist/info?appid=16073360&artistCode=%s&timestamp=%s";
    // 歌手信息 API (豆瓣)
    private final String ARTIST_DETAIL_DB_API = "https://movie.douban.com/celebrity/%s/";

    // 歌手歌曲 API
    private final String ARTIST_SONGS_API = SdkCommon.prefix + "/artist/songs?id=%s&offset=%s&limit=%s";
    // 歌手专辑 API
    private final String ARTIST_ALBUMS_API = SdkCommon.prefix + "/artist/album?id=%s&offset=%s&limit=%s";
    // 歌手 MV API
    private final String ARTIST_MVS_API = SdkCommon.prefix + "/artist/mv?id=%s&offset=%s&limit=%s";
    // 歌手视频 API
//    private final String ARTIST_VIDEOS_API = SdkCommon.prefix + "/artist/video?id=%s&cursor=%s&size=%s";
    // 歌手歌曲 API (酷狗)
    private final String ARTIST_SONGS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/song?&singerid=%s&page=%s&pagesize=%s";
    // 歌手专辑 API (酷狗)
    private final String ARTIST_ALBUMS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/album?&singerid=%s&page=%s&pagesize=%s";
    // 歌手 MV API (酷狗)
    private final String ARTIST_MVS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/mv?&singerid=%s&page=%s&pagesize=%s";
    // 歌手歌曲 API (QQ)
    private final String ARTIST_SONGS_QQ_API = SdkCommon.prefixQQ33 + "/singer/songs?singermid=%s&page=%s&num=%s";
    // 歌手专辑 API (QQ)
    private final String ARTIST_ALBUMS_QQ_API = SdkCommon.prefixQQ33 + "/singer/album?singermid=%s&pageNo=%s&pageSize=%s";
    // 歌手 MV API (QQ)
    private final String ARTIST_MVS_QQ_API = SdkCommon.prefixQQ33 + "/singer/mv?singermid=%s&pageNo=%s&pageSize=%s";
    // 歌手歌曲 API (酷我)
    private final String ARTIST_SONGS_KW_API = "http://www.kuwo.cn/api/www/artist/artistMusic?artistid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 歌手专辑 API (酷我)
    private final String ARTIST_ALBUMS_KW_API = "http://www.kuwo.cn/api/www/artist/artistAlbum?artistid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 歌手 MV API (酷我)
    private final String ARTIST_MVS_KW_API = "http://www.kuwo.cn/api/www/artist/artistMv?artistid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 歌手歌曲 API (咪咕)
    private final String ARTIST_SONGS_MG_API = SdkCommon.prefixMg + "/singer/songs?id=%s&pageNo=%s";
    // 歌手专辑 API (咪咕)
    private final String ARTIST_ALBUMS_MG_API = SdkCommon.prefixMg + "/singer/albums?id=%s&pageNo=%s";
    // 歌手歌曲 API (千千)
    private final String ARTIST_SONGS_QI_API = "https://music.91q.com/v1/artist/song?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";
    // 歌手专辑 API (千千)
    private final String ARTIST_ALBUMS_QI_API = "https://music.91q.com/v1/artist/album?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    // 社团信息 API (猫耳)
    private final String ORGANIZATION_DETAIL_ME_API = "https://www.missevan.com/organization/profile?organization_id=%s";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";
    // 声优节目 API (猫耳)
    private final String CV_PROGRAMS_ME_API = "https://www.missevan.com/seiy/%s";

    // 歌曲封面信息 API (QQ)
    private final String SINGLE_SONG_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T002R500x500M000%s.jpg";

    // 获取歌手照片 API (豆瓣)
    private final String GET_ARTISTS_IMG_DB_API
            = "https://movie.douban.com/celebrity/%s/photos/?type=C&start=%s&sortby=like&size=a&subtype=a";

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public void preloadArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;

        int source = artistInfo.getSource();

        // 酷我
        if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl());
                if (coverImgThumb == null)
                    coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgUrl().replaceFirst("/300/", "/0/"));
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
        } else {
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImgThumb(SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id, int source) {
        LinkedList<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"0".equals(id) && StringUtil.notEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NET_CLOUD) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("artist");

                String artistId = artistJson.getString("id");
                String name = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("img1v1Url");
                Integer songNum = artistJson.getIntValue("musicSize");
                Integer albumNum = artistJson.getIntValue("albumSize");
                Integer mvNum = artistJson.getIntValue("mvSize");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setId(artistId);
                artistInfo.setName(name);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // 酷狗
            else if (source == NetMusicSource.KG) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data");

                String artistId = artistJson.getString("singerid");
                String name = artistJson.getString("singername");
                String coverImgThumbUrl = artistJson.getString("imgurl").replace("{size}", "240");
                Integer songNum = artistJson.getIntValue("songcount");
                Integer albumNum = artistJson.getIntValue("albumcount");
                Integer mvNum = artistJson.getIntValue("mvcount");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.KG);
                artistInfo.setId(artistId);
                artistInfo.setName(name);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // QQ
            else if (source == NetMusicSource.QQ) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data");

                String name = artistJson.getString("singername");
                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, id);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QQ);
                artistInfo.setId(id);
                artistInfo.setName(name);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // 酷我
            else if (source == NetMusicSource.KW) {
                String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data");

                String artistId = artistJson.getString("id");
                String name = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("pic300");
                Integer songNum = artistJson.getIntValue("musicNum");
                Integer albumNum = artistJson.getIntValue("albumNum");
                Integer mvNum = artistJson.getIntValue("mvNum");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.KW);
                artistInfo.setId(artistId);
                artistInfo.setName(name);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // 咪咕
            else if (source == NetMusicSource.MG) {
                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data");

                String artistId = artistJson.getString("id");
                String name = artistJson.getString("name");
                String coverImgThumbUrl = "https:" + artistJson.getString("picUrl");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(artistId);
                artistInfo.setName(name);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // 千千
            else if (source == NetMusicSource.QI) {
                String artistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis())))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data");

                String artistId = artistJson.getString("artistCode");
                String name = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("pic");
                Integer songNum = artistJson.getIntValue("trackTotal");
                Integer albumNum = artistJson.getIntValue("albumTotal");
                Integer mvNum = artistJson.getIntValue("videoTotal");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QI);
                artistInfo.setId(artistId);
                artistInfo.setName(name);
                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                artistInfo.setSongNum(songNum);
                artistInfo.setAlbumNum(albumNum);
                artistInfo.setMvNum(mvNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    artistInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(artistInfo);
            }

            // 豆瓣
//            else if (source == NetMusicSource.DB) {
//                String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, id))
//                        .execute()
//                        .body();
//                Document doc = Jsoup.parse(artistInfoBody);
//                Element h1 = doc.select("#content > h1").first();
//                Element img = doc.select(".nbg img").first();
//
//                String name = h1.text();
//                String coverImgThumbUrl = img.attr("src");
//
//                NetArtistInfo artistInfo = new NetArtistInfo();
//                artistInfo.setSource(NetMusicSource.DB);
//                artistInfo.setId(id);
//                artistInfo.setName(name);
//                artistInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                GlobalExecutors.imageExecutor.execute(() -> {
//                    BufferedImage coverImgThumb = SdkUtil.SdkUtil.extractCover(coverImgThumbUrl);
//                    artistInfo.setCoverImgThumb(coverImgThumb);
//                });
//
//                res.add(artistInfo);
//            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;

        int source = artistInfo.getSource();
        String id = artistInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject artistJson = artistInfoJson.getJSONObject("artist");

            String coverImgUrl = artistJson.getString("img1v1Url");
            String description = artistJson.getString("briefDesc");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistJson.getIntValue("musicSize"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(artistJson.getIntValue("mvSize"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("intro");
            String coverImgUrl = data.getString("imgurl").replace("{size}", "240");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("songcount"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumcount"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String coverImgUrl = String.format(ARTIST_IMG_QQ_API, id);
            String description = data.getString("desc");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = StringUtil.removeHTMLLabel(data.getString("info"));
            String coverImgUrl = data.getString("pic300");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("musicNum"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumNum"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvNum"));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String coverImgUrl = "https:" + data.getString("picUrl");
            String description = data.getString("desc");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("introduce");
            String coverImgUrl = data.getString("pic");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("trackTotal"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumTotal"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("videoTotal"));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            if (artistInfo.isOrganization()) {
                String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_DETAIL_ME_API, id))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("info").getJSONObject("organization");

                String coverImgUrl = data.getString("avatar");
                String intro = StringUtil.removeHTMLLabel(data.getString("intro"));
                String announcement = StringUtil.removeHTMLLabel(data.getString("announcement"));

                if (!artistInfo.hasDescription()) artistInfo.setDescription(intro + "\n\n" + announcement);

                if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            } else {
                String artistInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, 1, 1))
                        .execute()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("info");
                JSONObject cv = data.getJSONObject("cv");
                JSONObject dramas = data.getJSONObject("dramas");

                if (!artistInfo.hasGender()) {
                    Integer gen = cv.getIntValue("gender");
                    String gender = gen == 1 ? "♂ 男" : gen == 2 ? "♀ 女" : "保密";
                    artistInfo.setGender(gender);
                }
                if (!artistInfo.hasCareer()) {
                    Integer ca = cv.getIntValue("career");
                    String career = ca == 1 ? "中文 CV" : ca == 0 ? "日文 CV" : "";
                    artistInfo.setCareer(career);
                }
                if (!artistInfo.hasBloodType()) {
                    Integer bt = cv.getIntValue("bloodtype");
                    String bloodType = bt == 1 ? "A" : bt == 2 ? "B" : bt == 3 ? "O" : "保密";
                    artistInfo.setBloodType(bloodType);
                }
                if (!artistInfo.hasAlias()) artistInfo.setAlias(cv.getString("seiyalias"));
                if (!artistInfo.hasGroup()) artistInfo.setGroup(cv.getString("group"));
                if (!artistInfo.hasBirthday()) {
                    int year = cv.getIntValue("birthyear"), month = cv.getIntValue("birthmonth"), day = cv.getIntValue("birthday");
                    artistInfo.setBirthday(year <= 0 ? month <= 0 ? null : month + "-" + day : year + "-" + month + "-" + day);
                }
                if (!artistInfo.hasDescription()) artistInfo.setDescription(cv.getString("profile"));
                if (!artistInfo.hasSongNum())
                    artistInfo.setSongNum(dramas.getJSONObject("pagination").getIntValue("count"));

                String coverImgUrl = cv.getString("icon");
                if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            String info = StringUtil.getPrettyText(doc.select("#headline div.info").first()) + "\n";
            Element bd = doc.select("#intro div.bd").first();
            Elements span = bd.select("span");
            String desc = StringUtil.getPrettyText(span.isEmpty() ? bd : span.last());
            String coverImgUrl = doc.select("div.nbg img").attr("src");

            artistInfo.setDescription(info + desc);
            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        }
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int limit, int page) {
        int total = 0;
        List<NetMusicInfo> netMusicInfos = new LinkedList<>();

        int source = artistInfo.getSource();
        String id = artistInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_API, id, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            total = artistInfoJson.getIntValue("total");
            JSONArray songArray = artistInfoJson.getJSONArray("songs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("name").trim();
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD);
                String artistId = songJson.getJSONArray("ar").getJSONObject(0).getString("id");
                String albumName = songJson.getJSONObject("al").getString("name");
                String albumId = songJson.getJSONObject("al").getString("id");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(albumId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_KG_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("info");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String hash = songJson.getString("hash");
                String songId = songJson.getString("album_audio_id");
                String[] s = songJson.getString("filename").split(" - ");
                String name = s[1];
                String artist = s[0];
                String albumName = songJson.getString("album_name");
                String albumId = songJson.getString("album_id");
                Double duration = songJson.getDouble("duration");
                JSONArray mvdata = songJson.getJSONArray("mvdata");
                String mvId = mvdata == null ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KG);
                netMusicInfo.setHash(hash);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artist);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(albumId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_QQ_API, id, page, limit))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("mid");
                String name = songJson.getString("name");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.QQ);
                JSONArray singerArray = songJson.getJSONArray("singer");
                String artistId = singerArray.isEmpty() ? "" : singerArray.getJSONObject(0).getString("mid");
                String albumName = songJson.getJSONObject("album").getString("name");
                String albumId = songJson.getJSONObject("album").getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QQ);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(albumId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_SONGS_KW_API, id, page, limit))
                    .header(Header.REFERER, "http://www.kuwo.cn/singer_detail/" + StringUtil.urlEncode(id))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("list");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("rid");
                // 酷我歌名中可能含有 HTML 标签，先去除
                String name = StringUtil.removeHTMLLabel(songJson.getString("name"));
                String artist = songJson.getString("artist").replace("&", "、");
                String artistId = songJson.getString("artistid");
                String albumName = songJson.getString("album");
                String albumId = songJson.getString("albumid");
                Double duration = songJson.getDouble("duration");
                String mvId = songJson.getIntValue("hasmv") == 0 ? "" : songId;

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.KW);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setArtist(artist);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(albumId);
                netMusicInfo.setDuration(duration);
                netMusicInfo.setMvId(mvId);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_MG_API, id, page))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            // 咪咕可能接口异常，需要判空！
            if (data != null) {
                total = data.getIntValue("totalPage") * limit;
                JSONArray songArray = data.getJSONArray("list");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("cid");
                    String name = songJson.getString("name");
                    String artists = SdkUtil.parseArtists(songJson, NetMusicSource.MG);
                    String artistId = songJson.getJSONArray("artists").getJSONObject(0).getString("id");
                    String albumName = songJson.getJSONObject("album").getString("name");
                    String albumId = songJson.getJSONObject("album").getString("id");
                    // 咪咕音乐可能没有 MV 字段！
                    String mvId = songJson.getString("mvId");

                    NetMusicInfo netMusicInfo = new NetMusicInfo();
                    netMusicInfo.setSource(NetMusicSource.MG);
                    netMusicInfo.setId(songId);
                    netMusicInfo.setName(name);
                    netMusicInfo.setArtist(artists);
                    netMusicInfo.setArtistId(artistId);
                    netMusicInfo.setAlbumName(albumName);
                    netMusicInfo.setAlbumId(albumId);
                    netMusicInfo.setMvId(mvId);

                    netMusicInfos.add(netMusicInfo);
                }
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ARTIST_SONGS_QI_API, id, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artists = SdkUtil.parseArtists(songJson, NetMusicSource.QI);
                JSONArray artistArray = songJson.getJSONArray("artist");
                String artistId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo netMusicInfo = new NetMusicInfo();
                netMusicInfo.setSource(NetMusicSource.QI);
                netMusicInfo.setId(songId);
                netMusicInfo.setName(name);
                netMusicInfo.setArtist(artists);
                netMusicInfo.setArtistId(artistId);
                netMusicInfo.setAlbumName(albumName);
                netMusicInfo.setAlbumId(albumId);
                netMusicInfo.setDuration(duration);

                netMusicInfos.add(netMusicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String artistInfoBody = HttpRequest.get(String.format(CV_PROGRAMS_ME_API, id))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements programs = doc.select(".pld-sound-title.cv-title a");
            total = programs.size();
//            Elements a = doc.select("a.share-personage-name.show_album_ower_name");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, programs.size()); i < len; i++) {
                Element program = programs.get(i);

                String songId = ReUtil.get("id=(\\d+)", program.attr("href"), 1);
                String name = program.text().trim();
                // 部分音频的艺术家不一致，干脆先不记录！
//                String artist = a.text();
//                String artistId = a.attr("href").replaceFirst("/", "");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
                netMusicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(netMusicInfos, total);
    }

    /**
     * 根据歌手 id 获取里面专辑的粗略信息，分页，返回 NetAlbumInfo
     */
    public CommonResult<NetAlbumInfo> getAlbumInfoInArtist(NetArtistInfo netArtistInfo, int limit, int page) {
        int total = 0;
        List<NetAlbumInfo> albumInfos = new LinkedList<>();

        String artistId = netArtistInfo.getId();
        int source = netArtistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_API, artistId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            total = albumInfoJson.getJSONObject("artist").getIntValue("albumSize");
            JSONArray albumArray = albumInfoJson.getJSONArray("hotAlbums");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String id = albumJson.getString("id");
                String name = albumJson.getString("name");
                String artists = SdkUtil.parseArtists(albumJson, NetMusicSource.NET_CLOUD);
                String arId = albumJson.getJSONArray("artists").getJSONObject(0).getString("id");
                String publishTime = TimeUtil.msToDate(albumJson.getLong("publishTime"));
                Integer songNum = albumJson.getIntValue("size");
                String coverImgThumbUrl = albumJson.getString("picUrl");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setId(id);
                albumInfo.setName(name);
                albumInfo.setArtist(artists);
                albumInfo.setArtistId(arId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_KG_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("info");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumid");
                String albumName = albumJson.getString("albumname");
                String artist = albumJson.getString("singername");
                String arId = albumJson.getString("singerid");
                String coverImgThumbUrl = albumJson.getString("imgurl").replace("/{size}", "");
                String description = albumJson.getString("intro");
                String publishTime = albumJson.getString("publishtime").replace(" 00:00:00", "");
                Integer songNum = albumJson.getIntValue("songcount");

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.KG);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(arId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setDescription(description);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_QQ_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = Math.max(total, data.getIntValue("total"));
            JSONArray albumArray = data.getJSONArray("list");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("album_mid");
                String albumName = albumJson.getString("album_name");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QQ);
                String arId = albumJson.getJSONArray("singers").getJSONObject(0).getString("singer_mid");
                String publishTime = albumJson.getString("pub_time");
                Integer songNum = albumJson.getJSONObject("latest_song").getIntValue("song_count");
                String coverImgThumbUrl = String.format(SINGLE_SONG_IMG_QQ_API, albumId);

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QQ);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(arId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_ALBUMS_KW_API, artistId, page, limit))
                    .header(Header.REFERER, "http://www.kuwo.cn/singer_detail/" + StringUtil.urlEncode(artistId) + "/album")
                    .execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String albumInfoBody = resp.body();
                JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
                JSONObject data = albumInfoJson.getJSONObject("data");
                total = data.getIntValue("total");
                JSONArray albumArray = data.getJSONArray("albumList");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("albumid");
                    String albumName = StringUtil.removeHTMLLabel(albumJson.getString("album"));
                    String artist = StringUtil.removeHTMLLabel(albumJson.getString("artist"));
                    String arId = albumJson.getString("artistid");
                    String publishTime = albumJson.getString("releaseDate");
                    String coverImgThumbUrl = albumJson.getString("pic");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.KW);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setArtistId(arId);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                    albumInfo.setPublishTime(publishTime);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    albumInfos.add(albumInfo);
                }
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String albumInfoBody = HttpRequest.get(String.format(ARTIST_ALBUMS_MG_API, artistId, page))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            // 咪咕可能接口异常，需要判空！
            JSONObject data = albumInfoJson.getJSONObject("data");
            if (data != null) {
                total = data.getIntValue("total");
                JSONArray albumArray = data.getJSONArray("list");
                for (int i = 0, len = albumArray.size(); i < len; i++) {
                    JSONObject albumJson = albumArray.getJSONObject(i);

                    String albumId = albumJson.getString("id");
                    String albumName = albumJson.getString("name");
                    String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.MG);
                    String arId = albumJson.getJSONArray("artists").getJSONObject(0).getString("id");
                    String coverImgThumbUrl = "http:" + albumJson.getString("picUrl");
//                    String publishTime = albumJson.getString("publishTime");
//                    Integer songNum = albumJson.getIntValue("songCount");

                    NetAlbumInfo albumInfo = new NetAlbumInfo();
                    albumInfo.setSource(NetMusicSource.MG);
                    albumInfo.setId(albumId);
                    albumInfo.setName(albumName);
                    albumInfo.setArtist(artist);
                    albumInfo.setArtistId(arId);
                    albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                    albumInfo.setPublishTime(publishTime);
//                    albumInfo.setSongNum(songNum);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                        albumInfo.setCoverImgThumb(coverImgThumb);
                    });
                    albumInfos.add(albumInfo);
                }
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String albumInfoBody = HttpRequest.get(SdkCommon.buildQianUrl(String.format(ARTIST_ALBUMS_QI_API, artistId, page, limit, System.currentTimeMillis())))
                    .execute()
                    .body();
            JSONObject albumInfoJson = JSONObject.parseObject(albumInfoBody);
            JSONObject data = albumInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray albumArray = data.getJSONArray("result");
            for (int i = 0, len = albumArray.size(); i < len; i++) {
                JSONObject albumJson = albumArray.getJSONObject(i);

                String albumId = albumJson.getString("albumAssetCode");
                String albumName = albumJson.getString("title");
                String artist = SdkUtil.parseArtists(albumJson, NetMusicSource.QI);
                JSONArray artistArray = albumJson.getJSONArray("artist");
                String arId = artistArray != null && !artistArray.isEmpty() ? artistArray.getJSONObject(0).getString("artistCode") : "";
                String coverImgThumbUrl = albumJson.getString("pic");
                String publishTime = albumJson.getString("releaseDate").split("T")[0];
                Integer songNum = albumJson.getJSONArray("trackList").size();

                NetAlbumInfo albumInfo = new NetAlbumInfo();
                albumInfo.setSource(NetMusicSource.QI);
                albumInfo.setId(albumId);
                albumInfo.setName(albumName);
                albumInfo.setArtist(artist);
                albumInfo.setArtistId(arId);
                albumInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                albumInfo.setPublishTime(publishTime);
                albumInfo.setSongNum(songNum);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    albumInfo.setCoverImgThumb(coverImgThumb);
                });
                albumInfos.add(albumInfo);
            }
        }

        return new CommonResult<>(albumInfos, total);
    }

    /**
     * 根据歌手 id 获取里面 MV 的粗略信息，分页，返回 NetMvInfo
     */
    public CommonResult<NetMvInfo> getMvInfoInArtist(NetArtistInfo netArtistInfo, int limit, int page) {
        int total = 0;
        List<NetMvInfo> mvInfos = new LinkedList<>();

        String artistId = netArtistInfo.getId();
        int source = netArtistInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            // 歌手 MV
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_API, artistId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONArray mvArray = mvInfoJson.getJSONArray("mvs");
            total = netArtistInfo.getMvNum();
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("id");
                String mvName = mvJson.getString("name").trim();
                String artistName = mvJson.getString("artistName");
                String creatorId = mvJson.getJSONObject("artist").getString("id");
                Long playCount = mvJson.getLong("playCount");
                Double duration = mvJson.getDouble("duration") / 1000;
                String coverImgUrl = mvJson.getString("imgurl");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
            // 歌手视频
//            Callable<CommonResult<NetMvInfo>> getArtistVideo = ()->{
//                List<NetMvInfo> res = new LinkedList<>();
//                int t = 0;
//
//                String mvInfoBody = HttpRequest.get(String.format(ARTIST_VIDEOS_API, artistId, (page - 1) * limit, limit))
//                        .execute()
//                        .body();
//                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
//                JSONArray mvArray = mvInfoJson.getJSONObject("data").getJSONArray("records");
//                t = netArtistInfo.getMvNum();
//                for (int i = 0, len = mvArray.size(); i < len; i++) {
//                    JSONObject mvJson = mvArray.getJSONObject(i);
//
//                    String mvId = mvJson.getString("id");
//                    String mvName = mvJson.getString("name");
//                    String artistName = mvJson.getString("artistName");
//                    Long playCount = mvJson.getLong("playCount");
//                    Double duration = mvJson.getDouble("duration") / 1000;
//                    String coverImgUrl = mvJson.getString("imgurl");
//
//                    NetMvInfo mvInfo = new NetMvInfo();
//                    mvInfo.setId(mvId);
//                    mvInfo.setName(mvName.trim());
//                    mvInfo.setArtist(artistName);
//                    mvInfo.setCoverImgUrl(coverImgUrl);
//                    mvInfo.setPlayCount(playCount);
//                    mvInfo.setDuration(duration);
//                    GlobalExecutors.imageExecutor.execute(() -> {
//                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
//                        mvInfo.setCoverImgThumb(coverImgThumb);
//                    });
//
//                    res.add(mvInfo);
//                }
//
//                return new CommonResult<>(res, t);
//            };
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_KG_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("hash");
                // 酷狗返回的名称含有 HTML 标签，需要去除
                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("filename"));
                String artistName = StringUtil.removeHTMLLabel(mvJson.getString("singername"));
                String coverImgUrl = mvJson.getString("imgurl");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String mvInfoBody = HttpRequest.get(String.format(ARTIST_MVS_QQ_API, artistId, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title").trim();
                String artistName = mvJson.getString("singer_name");
                String creatorId = mvJson.getString("singer_id");
                String coverImgUrl = mvJson.getString("pic");
                Long playCount = mvJson.getLong("listenCount");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setCoverImgUrl(coverImgUrl);
                mvInfo.setPlayCount(playCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                mvInfos.add(mvInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            HttpResponse resp = SdkCommon.kwRequest(String.format(ARTIST_MVS_KW_API, artistId, page, limit))
                    .header(Header.REFERER, "http://www.kuwo.cn/singer_detail/" + StringUtil.urlEncode(artistId) + "/mv")
                    .execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.parseObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                total = data.getIntValue("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = mvJson.getString("artist").replace("&", "、");
                    String creatorId = mvJson.getString("artistid");
                    String coverImgUrl = mvJson.getString("pic");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    mvInfos.add(mvInfo);
                }
            }
        }

        return new CommonResult<>(mvInfos, total);
    }

    /**
     * 获取歌手照片链接
     */
    public CommonResult<String> getArtistImgUrls(NetArtistInfo artistInfo, int page) {
        int source = artistInfo.getSource();
        String id = artistInfo.getId();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB) {
            String imgInfoBody = HttpRequest.get(String.format(GET_ARTISTS_IMG_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
            String t = ReUtil.get("共(\\d+)张", doc.select("span.count").text(), 1);
            total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
            for (int i = 0, len = imgs.size(); i < len; i++) {
                Element img = imgs.get(i);
                String url = img.attr("src").replaceFirst("/m/", "/l/");
                imgUrls.add(url);
            }
        }

        return new CommonResult<>(imgUrls, total);
    }
}
