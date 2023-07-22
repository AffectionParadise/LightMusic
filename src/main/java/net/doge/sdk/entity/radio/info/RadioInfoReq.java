package net.doge.sdk.entity.radio.info;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.model.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetRadioInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
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
import java.util.concurrent.atomic.AtomicInteger;

public class RadioInfoReq {
    // 电台信息 API
    private final String RADIO_DETAIL_API = SdkCommon.PREFIX + "/dj/detail?rid=%s";
    // 电台节目信息 API
    private final String RADIO_PROGRAM_DETAIL_API = SdkCommon.PREFIX + "/dj/program?rid=%s&offset=%s&limit=%s";
    // 电台信息 API (喜马拉雅)
    private final String RADIO_DETAIL_XM_API = "https://www.ximalaya.com/revision/album/v1/simple?albumId=%s";
    // 简短电台信息 API (喜马拉雅)
    private final String BRIEF_RADIO_DETAIL_XM_API = "https://www.ximalaya.com/tdk-web/seo/search/albumInfo?albumId=%s";
    // 电台节目 API (喜马拉雅)
    private final String RADIO_PROGRAM_XM_API = "http://www.ximalaya.com/revision/album/v1/getTracksList?albumId=%s&sort=%s&&pageNum=%s&pageSize=%s";
    // 电台信息 API (猫耳)
    private final String RADIO_DETAIL_ME_API = "https://www.missevan.com/dramaapi/getdrama?drama_id=%s";
    // 电台节目 API (猫耳)
//    private final String RADIO_PROGRAM_ME_API = "https://www.missevan.com/dramaapi/getdramaepisodedetails?drama_id=%s&p=%s&page_size=%s";
    // 电台信息 API (豆瓣)
    private final String RADIO_DETAIL_DB_API = "https://movie.douban.com/subject/%s/";
    // 图书电台信息 API (豆瓣)
    private final String BOOK_RADIO_DETAIL_DB_API = "https://book.douban.com/subject/%s/";
    // 游戏电台信息 API (豆瓣)
    private final String GAME_RADIO_DETAIL_DB_API = "https://www.douban.com/game/%s/";

    // 获取电台照片 API (豆瓣)
    private final String GET_RADIO_IMG_DB_API
            = "https://movie.douban.com/subject/%s/photos?type=S&start=%s&sortby=like&size=a&subtype=a";
    // 获取电台海报 API (豆瓣)
    private final String GET_RADIO_POSTER_DB_API
            = "https://movie.douban.com/subject/%s/photos?type=R&start=%s&sortby=like&size=a&subtype=a";
    // 获取游戏电台照片 API (豆瓣)
    private final String GET_GAME_RADIO_IMG_DB_API
            = "https://www.douban.com/game/%s/photos/?type=all&start=%s&sortby=hot";

    /**
     * 根据电台 id 预加载电台信息
     */
    public void preloadRadioInfo(NetRadioInfo radioInfo) {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImgThumb(SdkUtil.extractCover(radioInfo.getCoverImgThumbUrl())));
    }

    /**
     * 根据电台 id 获取电台
     */
    public CommonResult<NetRadioInfo> getRadioInfo(String id, int source) {
        LinkedList<NetRadioInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"0".equals(id) && StringUtil.notEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NET_CLOUD) {
                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, id))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject radioJson = radioInfoJson.getJSONObject("data");

                String radioId = radioJson.getString("id");
                String radioName = radioJson.getString("name");
                String dj = radioJson.getJSONObject("dj").getString("nickname");
                String djId = radioJson.getJSONObject("dj").getString("userId");
//                Long playCount = radioJson.getLong("playCount");
                Integer trackCount = radioJson.getIntValue("programCount");
                String category = radioJson.getString("category");
                if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
                String coverImgThumbUrl = radioJson.getString("picUrl");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
//                radioInfo.setPlayCount(playCount);
                radioInfo.setTrackCount(trackCount);
                radioInfo.setCategory(category);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }

            // 喜马拉雅
            else if (source == NetMusicSource.XM) {
                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_XM_API, id))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject data = radioInfoJson.getJSONObject("data");
                JSONObject radioJson = data.getJSONObject("albumPageMainInfo");

                String radioId = data.getString("albumId");
                String radioName = radioJson.getString("albumTitle");
//                String dj = radioJson.getString("nickname");
                String djId = radioJson.getString("anchorUid");
                Long playCount = radioJson.getLong("playCount");
//                Integer trackCount = radioJson.getIntValue("programCount");
                String coverImgThumbUrl = "https:" + radioJson.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.XM);
                radioInfo.setId(radioId);
                radioInfo.setName(radioName);
//                radioInfo.setDj(dj);
                radioInfo.setDjId(djId);
                radioInfo.setCoverImgThumbUrl(coverImgThumbUrl);
                radioInfo.setPlayCount(playCount);
//                radioInfo.setTrackCount(trackCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(coverImgThumbUrl);
                    radioInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(radioInfo);
            }

            // 猫耳
            else if (source == NetMusicSource.ME) {
                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                        .execute()
                        .body();
                JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                JSONObject info = radioInfoJson.getJSONObject("info");
                JSONObject drama = info.getJSONObject("drama");
                JSONObject episodes = info.getJSONObject("episodes");

                String radioId = drama.getString("id");
                String radioName = drama.getString("name");
                String dj = drama.getString("author");
                String djId = drama.getString("user_id");
                Long playCount = drama.getLong("view_count");
                // 猫耳的电台可能有多种类型！
                int episodeSize = episodes.getJSONArray("episode").size();
                int ftSize = episodes.getJSONArray("ft").size();
                int musicSize = episodes.getJSONArray("music").size();
                Integer trackCount = episodeSize + ftSize + musicSize;
                String category = drama.getString("catalog_name");
                String coverImgThumbUrl = drama.getString("cover");

                NetRadioInfo radioInfo = new NetRadioInfo();
                radioInfo.setSource(NetMusicSource.ME);
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

                res.add(radioInfo);
            }
        }

        return new CommonResult<>(res, t);
    }

    /**
     * 根据电台 id 补全电台信息(包括封面图、描述)
     */
    public void fillRadioInfo(NetRadioInfo radioInfo) {
        // 信息完整直接跳过
        if (radioInfo.isIntegrated()) return;

        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject radioJson = radioInfoJson.getJSONObject("data");

            String coverImgUrl = radioJson.getString("picUrl");
            String description = radioJson.getString("desc");

            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            radioInfo.setDescription(description);
            if (!radioInfo.hasDj()) radioInfo.setDj(radioJson.getJSONObject("dj").getString("nickname"));
            if (!radioInfo.hasDjId()) radioInfo.setDjId(radioJson.getJSONObject("dj").getString("userId"));
            String category = radioJson.getString("category");
            if (StringUtil.notEmpty(category)) category += "、" + radioJson.getString("secondCategory");
            if (!radioInfo.hasCategory()) radioInfo.setCategory(category);
            if (!radioInfo.hasTag()) radioInfo.setTag(category);
            if (!radioInfo.hasTrackCount()) radioInfo.setTrackCount(radioJson.getIntValue("programCount"));
            if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(radioJson.getLong("playCount"));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(radioInfo.getCoverImgUrl())));
            radioInfo.setTag("");
            radioInfo.setDescription("");
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            if (!radioInfo.hasTrackCount()) {
                GlobalExecutors.requestExecutor.submit(() -> {
                    String radioInfoBody = HttpRequest.get(String.format(BRIEF_RADIO_DETAIL_XM_API, id))
                            .execute()
                            .body();
                    JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
                    JSONObject radioJson = radioInfoJson.getJSONObject("data");

                    radioInfo.setTrackCount(radioJson.getIntValue("trackCount"));
                });
            }
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_XM_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject radioJson = radioInfoJson.getJSONObject("data").getJSONObject("albumPageMainInfo");

            String coverImgUrl = "https:" + radioJson.getString("cover");
            String tag = SdkUtil.parseTag(radioJson, NetMusicSource.XM);
            String description = radioJson.getString("shortIntro");

            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            if (!radioInfo.hasTag()) radioInfo.setTag(tag);
            if (!radioInfo.hasDescription()) radioInfo.setDescription(description);
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, id))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject info = radioInfoJson.getJSONObject("info");
            JSONObject drama = info.getJSONObject("drama");
            JSONObject episodes = info.getJSONObject("episodes");

            String coverImgUrl = drama.getString("cover");
            String description = drama.getString("abstract");

            if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            if (!radioInfo.hasTag()) radioInfo.setTag(SdkUtil.parseTag(drama, NetMusicSource.ME));
            if (!radioInfo.hasDescription()) radioInfo.setDescription(StringUtil.removeHTMLLabel(description));
            if (!radioInfo.hasDj()) radioInfo.setDj(drama.getString("author"));
            if (!radioInfo.hasDjId()) radioInfo.setDjId(drama.getString("user_id"));
            if (!radioInfo.hasCategory()) radioInfo.setCategory(drama.getString("catalog_name"));
            if (!radioInfo.hasTrackCount()) {
                // 猫耳的电台可能有多种类型！
                int episodeSize = episodes.getJSONArray("episode").size();
                int ftSize = episodes.getJSONArray("ft").size();
                int musicSize = episodes.getJSONArray("music").size();
                Integer trackCount = episodeSize + ftSize + musicSize;
                radioInfo.setTrackCount(trackCount);
            }
            if (!radioInfo.hasPlayCount()) radioInfo.setPlayCount(drama.getLong("view_count"));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            if (isBook) {
                String radioInfoBody = HttpRequest.get(String.format(BOOK_RADIO_DETAIL_DB_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                String info = StringUtil.getPrettyText(doc.select("div#info").first()) + "\n";
                Elements re = doc.select("div#link-report");
                Elements span = re.select("span");
                Element intro = doc.select("div.intro").last();
                Element cata = doc.select(String.format("div#dir_%s_full", id)).first();
                Element tr = doc.select("div.subject_show.block5:not(#rec-ebook-section) div").first();

                String desc = StringUtil.getPrettyText(span.isEmpty() ? re.first() : span.last()) + "\n";
                String authorIntro = StringUtil.getPrettyText(intro) + "\n";
                String catalog = StringUtil.getPrettyText(cata) + "\n\n";
                String trace = StringUtil.getPrettyText(tr);
                String coverImgUrl = doc.select("div#mainpic img").attr("src");

                radioInfo.setDescription(info + desc + "作者简介：\n" + authorIntro + "目录：\n" + catalog + "丛书信息：\n" + trace);
                if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            } else if (isGame) {
                String radioInfoBody = HttpRequest.get(String.format(GAME_RADIO_DETAIL_DB_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                String info = StringUtil.getPrettyText(doc.select("dl.game-attr").first()) + "\n";
                Element p = doc.select("div#link-report p").first();

                String desc = StringUtil.getPrettyText(p) + "\n";
                String coverImgUrl = doc.select("div.pic img").attr("src");

                radioInfo.setDescription(info + desc);
                if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            } else {
                String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_DB_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(radioInfoBody);
                String info = StringUtil.getPrettyText(doc.select("div#info").first()) + "\n";
                Elements re = doc.select("div#link-report");
                Elements span = re.select("span");

                String desc = StringUtil.getPrettyText(span.isEmpty() ? re.first() : span.last()) + "\n";
                String coverImgUrl = doc.select("div#mainpic img").attr("src");

                radioInfo.setDescription(info + desc);
                if (!radioInfo.hasCoverImgUrl()) radioInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> radioInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
            }
        }
    }

    /**
     * 根据电台 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInRadio(NetRadioInfo radioInfo, int sortType, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        int source = radioInfo.getSource();
        String radioId = radioInfo.getId();

        // 网易云(接口分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_PROGRAM_DETAIL_API, radioId, (page - 1) * limit, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            total.set(radioInfoJson.getIntValue("count"));
            JSONArray songArray = radioInfoJson.getJSONArray("programs");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject programJson = songArray.getJSONObject(i);
                JSONObject mainSongJson = programJson.getJSONObject("mainSong");
                JSONObject djJson = programJson.getJSONObject("dj");
                JSONObject radioJson = programJson.getJSONObject("radio");

                String programId = programJson.getString("id");
                String songId = mainSongJson.getString("id");
                String name = mainSongJson.getString("name");
                String artist = djJson.getString("nickname");
                String artistId = djJson.getString("userId");
                String albumName = radioJson.getString("name");
                String albumId = radioJson.getString("id");
                Double duration = mainSongJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setProgramId(programId);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);
                musicInfos.add(musicInfo);
            }
        }

        // QQ(程序分页)
        else if (source == NetMusicSource.QQ) {
            String radioInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format("{\"songlist\":{\"module\":\"mb_track_radio_svr\",\"method\":\"get_radio_track\"," +
                            "\"param\":{\"id\":%s,\"firstplay\":1,\"num\":15}},\"radiolist\":{\"module\":\"pf.radiosvr\"," +
                            "\"method\":\"GetRadiolist\",\"param\":{\"ct\":\"24\"}},\"comm\":{\"ct\":24,\"cv\":0}}", radioId))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONArray songArray = radioInfoJson.getJSONObject("songlist").getJSONObject("data").getJSONArray("tracks");
            if (JsonUtil.notEmpty(songArray)) {
                total.set(songArray.size());
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("mid");
                    String name = songJson.getString("name");
                    String artist = SdkUtil.parseArtist(songJson, NetMusicSource.QQ);
                    JSONArray singerArray = songJson.getJSONArray("singer");
                    String artistId = JsonUtil.isEmpty(singerArray) ? "" : singerArray.getJSONObject(0).getString("mid");
                    String albumName = songJson.getJSONObject("album").getString("name");
                    String albumId = songJson.getJSONObject("album").getString("mid");
                    Double duration = songJson.getDouble("interval");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.QQ);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setAlbumName(albumName);
                    musicInfo.setAlbumId(albumId);
                    musicInfo.setDuration(duration);
                    musicInfos.add(musicInfo);
                }
            }
        }

        // 喜马拉雅(接口分页)
        else if (source == NetMusicSource.XM) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_PROGRAM_XM_API, radioId, sortType, page, limit))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject data = radioInfoJson.getJSONObject("data");
            total.set(data.getIntValue("trackTotalCount"));
            JSONArray songArray = data.getJSONArray("tracks");
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String id = songJson.getString("trackId");
                String name = songJson.getString("title");
                String artist = songJson.getString("anchorName");
                String artistId = songJson.getString("anchorId");
                Double duration = songJson.getDouble("duration");
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumId");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfos.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String radioInfoBody = HttpRequest.get(String.format(RADIO_DETAIL_ME_API, radioId))
                    .execute()
                    .body();
            JSONObject radioInfoJson = JSONObject.parseObject(radioInfoBody);
            JSONObject info = radioInfoJson.getJSONObject("info");
            // 猫耳的电台可能有多种类型！
            JSONObject episodes = info.getJSONObject("episodes");
            JSONArray songArray = episodes.getJSONArray("episode");
            songArray.addAll(episodes.getJSONArray("ft"));
            songArray.addAll(episodes.getJSONArray("music"));
            total.set(songArray.size());
            for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = songArray.getJSONObject(i);

                String songId = programJson.getString("sound_id");
                String name = programJson.getString("name");
                // 艺术家与电台作者不一致！
//                String artist = radioInfo.getDj();
//                String artistId = radioInfo.getDjId();
                String albumName = radioInfo.getName();
                String albumId = radioInfo.getId();
                Double duration = programJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
                musicInfo.setAlbumName(albumName);
                musicInfo.setAlbumId(albumId);
                musicInfo.setDuration(duration);

                musicInfos.add(musicInfo);
            }
        }

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取电台照片链接
     */
    public CommonResult<String> getRadioImgUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        boolean isGame = radioInfo.isGame();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = isGame ? 24 : 30;

        if (source == NetMusicSource.DB) {
            if (isGame) {
                String imgInfoBody = HttpRequest.get(String.format(GET_GAME_RADIO_IMG_DB_API, id, (page - 1) * limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(imgInfoBody);
                Elements imgs = doc.select("div.pholist ul img");
                String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
                total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
                for (int i = 0, len = imgs.size(); i < len; i++) {
                    Element img = imgs.get(i);
                    String url = img.attr("src").replaceFirst("/thumb/", "/photo/");
                    imgUrls.add(url);
                }
            } else if (!isBook) {
                String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_IMG_DB_API, id, (page - 1) * limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(imgInfoBody);
                Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
                String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
                total = StringUtil.isEmpty(t) ? imgs.size() : Integer.parseInt(t);
                for (int i = 0, len = imgs.size(); i < len; i++) {
                    Element img = imgs.get(i);
                    String url = img.attr("src").replaceFirst("/m/", "/l/");
                    imgUrls.add(url);
                }
            }
        }

        return new CommonResult<>(imgUrls, total);
    }

    /**
     * 获取电台海报链接
     */
    public CommonResult<String> getRadioPosterUrls(NetRadioInfo radioInfo, int page) {
        int source = radioInfo.getSource();
        String id = radioInfo.getId();
        boolean isBook = radioInfo.isBook();
        LinkedList<String> imgUrls = new LinkedList<>();
        Integer total = 0;
        final int limit = 30;

        if (source == NetMusicSource.DB && !isBook) {
            String imgInfoBody = HttpRequest.get(String.format(GET_RADIO_POSTER_DB_API, id, (page - 1) * limit))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(imgInfoBody);
            Elements imgs = doc.select("ul.poster-col3.clearfix div.cover img");
            String t = RegexUtil.getGroup1("共(\\d+)张", doc.select("span.count").text());
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
