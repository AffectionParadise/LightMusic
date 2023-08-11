package net.doge.sdk.entity.artist.info;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetArtistInfo;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.opt.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ArtistInfoReq {
    // 歌手信息 API
    private final String ARTIST_DETAIL_API = "https://music.163.com/api/artist/head/info/get";
    // 歌手信息 API (酷狗)
    private final String ARTIST_DETAIL_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/info?singerid=%s";
    // 歌手信息 API (QQ)
    private final String ARTIST_DETAIL_QQ_API = "https://y.qq.com/n/ryqq/singer/%s";
    // 歌手图片 API (QQ)
    private final String ARTIST_IMG_QQ_API = "https://y.gtimg.cn/music/photo_new/T001R500x500M000%s.jpg";
    // 歌手信息 API (酷我)
    private final String ARTIST_DETAIL_KW_API = "https://kuwo.cn/api/www/artist/artist?artistid=%s&httpsStatus=1";
    // 歌手信息 API (咪咕)
    private final String ARTIST_DETAIL_MG_API = "http://music.migu.cn/v3/music/artist/%s";
    // 歌手信息 API (千千)
    private final String ARTIST_DETAIL_QI_API = "https://music.91q.com/v1/artist/info?appid=16073360&artistCode=%s&timestamp=%s";
    // 歌手信息 API (豆瓣)
    private final String ARTIST_DETAIL_DB_API = "https://movie.douban.com/celebrity/%s/";

    // 歌手歌曲 API
    private final String ARTIST_SONGS_API = "https://music.163.com/api/v1/artist/songs";
    // 歌手歌曲 API (酷狗)
    private final String ARTIST_SONGS_KG_API = "http://mobilecdnbj.kugou.com/api/v3/singer/song?&singerid=%s&page=%s&pagesize=%s";
    // 歌手歌曲 API (酷我)
    private final String ARTIST_SONGS_KW_API = "http://www.kuwo.cn/api/www/artist/artistMusic?artistid=%s&pn=%s&rn=%s&httpsStatus=1";
    // 歌手歌曲 API (咪咕)
    private final String ARTIST_SONGS_MG_API = "http://music.migu.cn/v3/music/artist/%s/song?page=%s";
    // 歌手歌曲 API (千千)
    private final String ARTIST_SONGS_QI_API = "https://music.91q.com/v1/artist/song?appid=16073360&artistCode=%s&pageNo=%s&pageSize=%s&timestamp=%s";

    // 社团信息 API (猫耳)
    private final String ORGANIZATION_DETAIL_ME_API = "https://www.missevan.com/organization/profile?organization_id=%s";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";
    // 声优节目 API (猫耳)
    private final String CV_PROGRAMS_ME_API = "https://www.missevan.com/seiy/%s";

    /**
     * 根据歌手 id 预加载歌手信息
     */
    public void preloadArtistInfo(NetArtistInfo artistInfo) {
        // 信息完整直接跳过
        if (artistInfo.isIntegrated()) return;

        int source = artistInfo.getSource();

        // 酷我
        if (source == NetMusicSource.KW) {
            GlobalExecutors.imageExecutor.execute(() -> {
                BufferedImage coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl());
                if (coverImgThumb == null)
                    coverImgThumb = SdkUtil.extractCover(artistInfo.getCoverImgUrl().replaceFirst("/300/", "/0/"));
                artistInfo.setCoverImgThumb(coverImgThumb);
            });
        } else {
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImgThumb(SdkUtil.extractCover(artistInfo.getCoverImgThumbUrl())));
        }
    }

    /**
     * 根据歌手 id 获取歌手
     */
    public CommonResult<NetArtistInfo> getArtistInfo(String id, int source) {
        List<NetArtistInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"0".equals(id) && StringUtil.notEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NC) {
                Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
                String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_DETAIL_API, String.format("{\"id\":\"%s\"}", id), options)
                        .executeAsync()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject artistJson = artistInfoJson.getJSONObject("data").getJSONObject("artist");

                String artistId = artistJson.getString("id");
                String name = artistJson.getString("name");
                String coverImgThumbUrl = artistJson.getString("avatar");
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
                        .executeAsync()
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
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(artistInfoBody);

                Elements sn = doc.select(".data_statistic__number");

                String name = doc.select("h1.data__name_txt").text();
                Integer songNum = !sn.isEmpty() ? Integer.parseInt(sn.get(0).text()) : 0;
                Integer albumNum = sn.size() > 1 ? Integer.parseInt(sn.get(1).text()) : 0;
                Integer mvNum = sn.size() > 2 ? Integer.parseInt(sn.get(2).text()) : 0;
                String coverImgThumbUrl = String.format(ARTIST_IMG_QQ_API, id);

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.QQ);
                artistInfo.setId(id);
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

            // 酷我
            else if (source == NetMusicSource.KW) {
                String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                        .executeAsync()
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
                        .setFollowRedirects(true)
                        .executeAsync()
                        .body();
                Document doc = Jsoup.parse(artistInfoBody);

                String name = doc.select(".artist-info .artist-name a").text();
                String txt = doc.select(".artist-section-title").text();
                String songNumText = RegexUtil.getGroup1("全部(\\d+)首", txt);
                Integer songNum = StringUtil.isEmpty(songNumText) ? 0 : Integer.parseInt(songNumText);
                String albumNumText = RegexUtil.getGroup1("全部(\\d+)张", txt);
                Integer albumNum = StringUtil.isEmpty(albumNumText) ? 0 : Integer.parseInt(albumNumText);
                String mvNumText = RegexUtil.getGroup1("全部(\\d+)支", txt);
                Integer mvNum = StringUtil.isEmpty(mvNumText) ? 0 : Integer.parseInt(mvNumText);
                String coverImgThumbUrl = "https:" + doc.select(".artist-info .artist-avatar img").attr("src");

                NetArtistInfo artistInfo = new NetArtistInfo();
                artistInfo.setSource(NetMusicSource.MG);
                artistInfo.setId(id);
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

            // 千千
            else if (source == NetMusicSource.QI) {
                String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis()))
                        .executeAsync()
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
//                        .executeAsync()
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
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_DETAIL_API, String.format("{\"id\":\"%s\"}", id), options)
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject artistJson = artistInfoJson.getJSONObject("data").getJSONObject("artist");

            String coverImgUrl = artistJson.getString("avatar");
            String description = artistJson.getString("briefDesc");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(artistJson.getIntValue("musicSize"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setSongNum(artistJson.getIntValue("albumSize"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(artistJson.getIntValue("mvSize"));
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_KG_API, id))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("intro");
            String coverImgUrl = data.getString("imgurl").replace("{size}", "240");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("songcount"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumcount"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvcount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_QQ_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);

            Elements sn = doc.select(".data_statistic__number");
            Elements ps = doc.select("#popup_data_detail .popup_data_detail__cont p");

            String coverImgUrl = String.format(ARTIST_IMG_QQ_API, id);
            StringJoiner sj = new StringJoiner("\n");
            ps.forEach(p -> sj.add(p.text()));
            String description = sj.toString();

            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(!sn.isEmpty() ? Integer.parseInt(sn.get(0).text()) : 0);
            if (!artistInfo.hasAlbumNum())
                artistInfo.setSongNum(sn.size() > 1 ? Integer.parseInt(sn.get(1).text()) : 0);
            if (!artistInfo.hasMvNum()) artistInfo.setSongNum(sn.size() > 2 ? Integer.parseInt(sn.get(2).text()) : 0);
            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_DETAIL_KW_API, id))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = StringUtil.removeHTMLLabel(data.getString("info"));
            String coverImgUrl = data.getString("pic300");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("musicNum"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumNum"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("mvNum"));
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_MG_API, id))
                    .setFollowRedirects(true)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);

            String txt = doc.select(".artist-section-title").text();
            String coverImgUrl = "https:" + doc.select(".artist-info .artist-avatar img").attr("src");
            String description = doc.select("#J_ArtistIntro .content").text();

            if (!artistInfo.hasSongNum()) {
                String songNumText = RegexUtil.getGroup1("全部(\\d+)首", txt);
                Integer songNum = StringUtil.isEmpty(songNumText) ? 0 : Integer.parseInt(songNumText);
                artistInfo.setSongNum(songNum);
            }
            if (!artistInfo.hasAlbumNum()) {
                String albumNumText = RegexUtil.getGroup1("全部(\\d+)张", txt);
                Integer albumNum = StringUtil.isEmpty(albumNumText) ? 0 : Integer.parseInt(albumNumText);
                artistInfo.setAlbumNum(albumNum);
            }
            if (!artistInfo.hasMvNum()) {
                String mvNumText = RegexUtil.getGroup1("全部(\\d+)支", txt);
                Integer mvNum = StringUtil.isEmpty(mvNumText) ? 0 : Integer.parseInt(mvNumText);
                artistInfo.setMvNum(mvNum);
            }
            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_DETAIL_QI_API, id, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");

            String description = data.getString("introduce");
            String coverImgUrl = data.getString("pic");

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            artistInfo.setDescription(description);
            if (!artistInfo.hasSongNum()) artistInfo.setSongNum(data.getIntValue("trackTotal"));
            if (!artistInfo.hasAlbumNum()) artistInfo.setAlbumNum(data.getIntValue("albumTotal"));
            if (!artistInfo.hasMvNum()) artistInfo.setMvNum(data.getIntValue("videoTotal"));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            if (artistInfo.isOrganization()) {
                String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_DETAIL_ME_API, id))
                        .executeAsync()
                        .body();
                JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
                JSONObject data = artistInfoJson.getJSONObject("info").getJSONObject("organization");

                String coverImgUrl = data.getString("avatar");
                String intro = StringUtil.removeHTMLLabel(data.getString("intro"));
                String announcement = StringUtil.removeHTMLLabel(data.getString("announcement"));

                if (!artistInfo.hasDescription()) artistInfo.setDescription(intro + "\n\n" + announcement);

                if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            } else {
                String artistInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, 1, 1))
                        .executeAsync()
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
                GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            }
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_DETAIL_DB_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            String info = StringUtil.getPrettyText(doc.select("#headline div.info").first()) + "\n";
            Element bd = doc.select("#intro div.bd").first();
            Elements span = bd.select("span");
            String desc = StringUtil.getPrettyText(span.isEmpty() ? bd : span.last());
            String coverImgUrl = doc.select("div.nbg img").attr("src");

            artistInfo.setDescription(info + desc);
            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        }
    }

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int limit, int page) {
        int total = 0;
        List<NetMusicInfo> res = new LinkedList<>();

        int source = artistInfo.getSource();
        String id = artistInfo.getId();

        // 网易云
        if (source == NetMusicSource.NC) {
            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.weApi();
            String artistInfoBody = SdkCommon.ncRequest(Method.POST, ARTIST_SONGS_API,
                            String.format("{\"id\":\"%s\",\"private_cloud\":true,\"work_type\":1,\"order\":\"hot\",\"offset\":%s,\"limit\":%s}", id, (page - 1) * limit, limit),
                            options)
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            total = artistInfoJson.getIntValue("total");
            JSONArray songArray = artistInfoJson.getJSONArray("songs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("al");

                String songId = songJson.getString("id");
                String name = songJson.getString("name").trim();
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("name");
                String albumId = albumJson.getString("id");
                Double duration = songJson.getDouble("dt") / 1000;
                String mvId = songJson.getString("mv");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_KG_API, id, page, limit))
                    .executeAsync()
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
                String mvId = JsonUtil.isEmpty(mvdata) ? songJson.getString("mvhash") : mvdata.getJSONObject(0).getString("hash");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KG);
                musicInfo.setHash(hash);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String artistInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"comm\":{\"ct\":24,\"cv\":0},\"singer\":{\"method\":\"get_singer_detail_info\",\"param\":" +
                            "{\"sort\":5,\"singermid\":\"%s\",\"sin\":%s,\"num\":%s},\"module\":\"music.web_singer_info_svr\"}}", id, (page - 1) * limit, limit))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("singer").getJSONObject("data");
            total = data.getIntValue("total_song");
            JSONArray songArray = data.getJSONArray("songlist");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                JSONObject albumJson = songJson.getJSONObject("album");

                String songId = songJson.getString("mid");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = albumJson.getString("title");
                String albumId = albumJson.getString("mid");
                Double duration = songJson.getDouble("interval");
                String mvId = songJson.getJSONObject("mv").getString("vid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QQ);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String artistInfoBody = SdkCommon.kwRequest(String.format(ARTIST_SONGS_KW_API, id, page, limit))
                    .header(Header.REFERER, "http://www.kuwo.cn/singer_detail/" + StringUtil.urlEncodeAll(id))
                    .executeAsync()
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

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.KW);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtistId(artistId);
                musicInfo.setArtist(artist);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String artistInfoBody = HttpRequest.get(String.format(ARTIST_SONGS_MG_API, id, page))
                    .setFollowRedirects(true)
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements pageElem = doc.select(".views-pagination .pagination-item");
            total = !pageElem.isEmpty() ? Integer.parseInt(pageElem.get(pageElem.size() - 1).text()) * limit : limit;
            Elements songArray = doc.select(".row.J_CopySong");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                Element song = songArray.get(i);
                Elements a = song.select("a.song-name-txt");
                Elements aa = song.select(".J_SongSingers a");
                Elements ba = song.select(".song-belongs a");
                Elements fa = song.select("a.flag.flag-mv");

                String songId = RegexUtil.getGroup1("/v3/music/song/(.*)", a.attr("href"));
                String name = a.text();
                StringJoiner sj = new StringJoiner("、");
                aa.forEach(aElem -> sj.add(aElem.text()));
                String artist = sj.toString();
                String artistId = aa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/music/artist/(\\d+)", aa.get(0).attr("href"));
                String albumName = ba.text();
                String albumId = RegexUtil.getGroup1("/v3/music/album/(\\d+)", ba.attr("href"));
                String mvId = fa.isEmpty() ? "" : RegexUtil.getGroup1("/v3/video/mv/(.*)", fa.attr("href"));

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.MG);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setMvId(mvId);

                res.add(musicInfo);
            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String artistInfoBody = SdkCommon.qiRequest(String.format(ARTIST_SONGS_QI_API, id, page, limit, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("data");
            total = data.getIntValue("total");
            JSONArray songArray = data.getJSONArray("result");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("TSID");
                String name = songJson.getString("title");
                String artist = SdkUtil.parseArtist(songJson);
                String artistId = SdkUtil.parseArtistId(songJson);
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumAssetCode");
                Double duration = songJson.getDouble("duration");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.QI);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String artistInfoBody = HttpRequest.get(String.format(CV_PROGRAMS_ME_API, id))
                    .executeAsync()
                    .body();
            Document doc = Jsoup.parse(artistInfoBody);
            Elements programs = doc.select(".pld-sound-title.cv-title a");
            total = programs.size();
//            Elements a = doc.select("a.share-personage-name.show_album_ower_name");
            for (int i = (page - 1) * limit, len = Math.min(page * limit, programs.size()); i < len; i++) {
                Element program = programs.get(i);

                String songId = RegexUtil.getGroup1("id=(\\d+)", program.attr("href"));
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
                res.add(musicInfo);
            }
        }

        return new CommonResult<>(res, total);
    }
}
