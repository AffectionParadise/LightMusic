package net.doge.sdk.entity.music.rcmd;

import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.sdk.common.Tags;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RecommendProgramReq {
    // 推荐节目 API
    private final String RECOMMEND_PROGRAM_API = SdkCommon.prefix + "/program/recommend";
    // 推荐个性节目 API
    private final String PERSONALIZED_PROGRAM_API = SdkCommon.prefix + "/personalized/djprogram";
    // 24 小时节目榜 API
    private final String PROGRAM_24_HOURS_TOPLIST_API = SdkCommon.prefix + "/dj/program/toplist/hours";
    // 节目榜 API
    private final String PROGRAM_TOPLIST_API = SdkCommon.prefix + "/dj/program/toplist?limit=200";
    // 推荐节目 API (猫耳)
    private final String REC_PROGRAM_ME_API = "https://www.missevan.com/site/homepage";
    // 探索节目 API (猫耳)
    private final String EXP_PROGRAM_ME_API = "https://www.missevan.com/explore/%s?p=%s&pagesize=%s";
    // 首页分类节目 API (猫耳)
    private final String INDEX_CAT_PROGRAM_ME_API = "https://www.missevan.com/sound/m?order=1&id=%s&p=%s&pagesize=%s";
    // 首页分类节目 API (最新)(猫耳)
    private final String INDEX_CAT_NEW_PROGRAM_ME_API = "https://www.missevan.com/sound/m?order=0&id=%s&p=%s&pagesize=%s";
    
    /**
     * 获取推荐节目
     */
    public CommonResult<NetMusicInfo> getRecommendPrograms(int src, String tag, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();

        final String defaultTag = "默认";
        String[] s = Tags.programTag.get(tag);

        // 网易云(程序分页)
        // 推荐节目
        Callable<CommonResult<NetMusicInfo>> getRecommendPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(RECOMMEND_PROGRAM_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("programs");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i);
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

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 推荐个性节目
        Callable<CommonResult<NetMusicInfo>> getPersonalizedPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PERSONALIZED_PROGRAM_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("result");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 24 小时节目榜
        Callable<CommonResult<NetMusicInfo>> get24HoursPrograms = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PROGRAM_24_HOURS_TOPLIST_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONObject("data").getJSONArray("list");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 节目榜
        Callable<CommonResult<NetMusicInfo>> getProgramsRanking = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(PROGRAM_TOPLIST_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
            JSONArray programArray = programInfoJson.getJSONArray("toplist");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i).getJSONObject("program");
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

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 猫耳
        // 推荐节目
        Callable<CommonResult<NetMusicInfo>> getRecProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            String programInfoBody = HttpRequest.get(String.format(REC_PROGRAM_ME_API))
                    .execute()
                    .body();
            JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
            JSONObject info = programInfoJson.getJSONObject("info");
            // 轮播图
            JSONArray programArray = info.getJSONArray("links");
            t = programArray.size();
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i);

                String id = ReUtil.get("/sound/(\\d+)", programJson.getString("url"), 1);
                String name = programJson.getString("title");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(id);
                musicInfo.setName(name);

                res.add(musicInfo);
            }
            // 右侧节目
            programArray = info.getJSONObject("sounds").getJSONArray("day3");
            t = Math.max(t, programArray.size());
            for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
                JSONObject programJson = programArray.getJSONObject(i);

                String id = programJson.getString("id");
                String name = programJson.getString("soundstr");
                Double duration = programJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setDuration(duration);

                res.add(musicInfo);
            }
            return new CommonResult<>(res, t);
        };
        // 探索节目(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getExpProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[0])) {
                String programInfoBody = HttpRequest.get(String.format(EXP_PROGRAM_ME_API, s[0], page, Math.min(limit, 20)))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select(".video-box");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);
                    Elements a = box.select(".video-auther a");

                    String id = box.attr("data-id");
                    String name = box.select(".video-title").text();
                    String artist = a.text();
                    String artistId = a.attr("href").replaceFirst("/", "");
                    Double duration = TimeUtil.chineseToSeconds(box.select("span.video-duration").get(1).text());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 首页分类节目(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getIndexCatProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_PROGRAM_ME_API, s[1], page, Math.min(limit, 20)))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select("div.vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("href").replaceFirst("/sound/", "");
                    String name = box.attr("title");
                    Double duration = TimeUtil.toSeconds(box.select("div.vw-frontsound-time.fc-hoverheight").first().text().trim());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 首页分类节目(最新)(从网页解析，每页不超过 20 条)
        Callable<CommonResult<NetMusicInfo>> getIndexCatNewProgramsMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_NEW_PROGRAM_ME_API, s[1], page, Math.min(limit, 20)))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                String ts = ReUtil.get("p=(\\d+)", doc.select("li.last a").attr("href"), 1);
                t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
                Elements boxes = doc.select("div.vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
                for (int i = 0, size = boxes.size(); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("href").replaceFirst("/sound/", "");
                    String name = box.attr("title");
                    Double duration = TimeUtil.toSeconds(box.select("div.vw-frontsound-time.fc-hoverheight").first().text().trim());

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setId(id);
                    musicInfo.setName(name);
                    musicInfo.setDuration(duration);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 首页分类节目侧边榜单
        Callable<CommonResult<NetMusicInfo>> getIndexCatProgramsRankingMe = () -> {
            LinkedList<NetMusicInfo> res = new LinkedList<>();
            Integer t = 0;

            if (StringUtil.notEmpty(s[1])) {
                String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_PROGRAM_ME_API, s[1], page, limit))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(programInfoBody);
                Elements boxes = doc.select(".vw-weibo-title.floatleft a");
                boxes.addAll(doc.select(".vw-right-content.floatleft a"));
                t = boxes.size();
                for (int i = (page - 1) * limit, size = Math.min(page * limit, boxes.size()); i < size; i++) {
                    Element box = boxes.get(i);

                    String id = box.attr("href").replaceFirst("/sound/", "");
                    String name = box.childNodeSize() > 1 ? box.select(".vw-rank-title.floatleft").text().trim() : box.text().trim();

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.ME);
                    musicInfo.setId(id);
                    musicInfo.setName(name);

                    res.add(musicInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

        boolean dt = tag.equals(defaultTag);

        if (dt) {
            if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getRecommendPrograms));
                taskList.add(GlobalExecutors.requestExecutor.submit(getPersonalizedPrograms));
                taskList.add(GlobalExecutors.requestExecutor.submit(get24HoursPrograms));
                taskList.add(GlobalExecutors.requestExecutor.submit(getProgramsRanking));
            }

            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getRecProgramsMe));
            }
        } else {
            if (src == NetMusicSource.ME || src == NetMusicSource.ALL) {
                taskList.add(GlobalExecutors.requestExecutor.submit(getExpProgramsMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getIndexCatProgramsMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getIndexCatNewProgramsMe));
                taskList.add(GlobalExecutors.requestExecutor.submit(getIndexCatProgramsRankingMe));
            }
        }

        List<List<NetMusicInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMusicInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        musicInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(musicInfos, total.get());
    }
}
