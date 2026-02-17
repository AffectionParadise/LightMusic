package net.doge.sdk.service.music.rcmd.impl.program;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.data.Tags;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.media.DurationUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class MeRecommendProgramReq {
    private static MeRecommendProgramReq instance;

    private MeRecommendProgramReq() {
    }

    public static MeRecommendProgramReq getInstance() {
        if (instance == null) instance = new MeRecommendProgramReq();
        return instance;
    }

    // 推荐节目 API (猫耳)
    private final String REC_PROGRAM_ME_API = "https://www.missevan.com/site/homepage";
    // 探索节目 API (猫耳)
    private final String EXP_PROGRAM_ME_API = "https://www.missevan.com/explore/%s?p=%s&pagesize=%s";
    // 首页分类节目 API (猫耳)
    private final String INDEX_CAT_PROGRAM_ME_API = "https://www.missevan.com/sound/m?order=1&id=%s&p=%s&pagesize=%s";
    // 首页分类节目 API (最新)(猫耳)
    private final String INDEX_CAT_NEW_PROGRAM_ME_API = "https://www.missevan.com/sound/m?order=0&id=%s&p=%s&pagesize=%s";

    /**
     * 推荐节目
     */
    public CommonResult<NetMusicInfo> getRecPrograms(int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t;

        String programInfoBody = HttpRequest.get(REC_PROGRAM_ME_API)
                .executeAsStr();
        JSONObject programInfoJson = JSONObject.parseObject(programInfoBody);
        JSONObject info = programInfoJson.getJSONObject("info");
        // 轮播图
        JSONArray programArray = info.getJSONArray("links");
        t = programArray.size();
        for (int i = (page - 1) * limit, len = Math.min(programArray.size(), page * limit); i < len; i++) {
            JSONObject programJson = programArray.getJSONObject(i);

            String id = RegexUtil.getGroup1("/sound/(\\d+)", programJson.getString("url"));
            String name = programJson.getString("title");

            NetMusicInfo musicInfo = new NetMusicInfo();
            musicInfo.setSource(NetMusicSource.ME);
            musicInfo.setId(id);
            musicInfo.setName(name);

            r.add(musicInfo);
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

            r.add(musicInfo);
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 探索节目(从网页解析，每页不超过 20 条)
     */
    public CommonResult<NetMusicInfo> getExpPrograms(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.programTag.get(tag);

        if (StringUtil.notEmpty(s[0])) {
            String programInfoBody = HttpRequest.get(String.format(EXP_PROGRAM_ME_API, s[0], page, Math.min(limit, 20)))
                    .executeAsStr();
            Document doc = Jsoup.parse(programInfoBody);
            String ts = RegexUtil.getGroup1("p=(\\d+)", doc.select("li.last a").attr("href"));
            t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
            Elements boxes = doc.select(".video-box");
            for (int i = 0, size = boxes.size(); i < size; i++) {
                Element box = boxes.get(i);
                Elements a = box.select(".video-auther a");

                String id = box.attr("data-id");
                String name = box.select(".video-title").text();
                String artist = a.text();
                String artistId = a.attr("href").replaceFirst("/", "");
                Double duration = DurationUtil.chineseToSeconds(box.select("span.video-duration").get(1).text());

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 首页分类节目(从网页解析，每页不超过 20 条)
     */
    public CommonResult<NetMusicInfo> getIndexCatPrograms(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.programTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_PROGRAM_ME_API, s[1], page, Math.min(limit, 20)))
                    .executeAsStr();
            Document doc = Jsoup.parse(programInfoBody);
            String ts = RegexUtil.getGroup1("p=(\\d+)", doc.select("li.last a").attr("href"));
            t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
            Elements boxes = doc.select(".vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
            for (int i = 0, size = boxes.size(); i < size; i++) {
                Element box = boxes.get(i);

                String id = box.attr("href").replaceFirst("/sound/", "");
                String name = box.attr("title");
                Double duration = DurationUtil.toSeconds(box.select(".vw-frontsound-time.fc-hoverheight").first().text().trim());

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 首页分类节目(最新)(从网页解析，每页不超过 20 条)
     */
    public CommonResult<NetMusicInfo> getIndexCatNewPrograms(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.programTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_NEW_PROGRAM_ME_API, s[1], page, Math.min(limit, 20)))
                    .executeAsStr();
            Document doc = Jsoup.parse(programInfoBody);
            String ts = RegexUtil.getGroup1("p=(\\d+)", doc.select("li.last a").attr("href"));
            t = StringUtil.isEmpty(ts) ? limit : Integer.parseInt(ts) * limit;
            Elements boxes = doc.select(".vw-subcatalog-contant.fc-leftcontent-block.floatleft a[target=_player]");
            for (int i = 0, size = boxes.size(); i < size; i++) {
                Element box = boxes.get(i);

                String id = box.attr("href").replaceFirst("/sound/", "");
                String name = box.attr("title");
                Double duration = DurationUtil.toSeconds(box.select(".vw-frontsound-time.fc-hoverheight").first().text().trim());

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(id);
                musicInfo.setName(name);
                musicInfo.setDuration(duration);

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }

    /**
     * 首页分类节目侧边榜单
     */
    public CommonResult<NetMusicInfo> getIndexCatProgramsRanking(String tag, int page, int limit) {
        List<NetMusicInfo> r = new LinkedList<>();
        int t = 0;
        String[] s = Tags.programTag.get(tag);

        if (StringUtil.notEmpty(s[1])) {
            String programInfoBody = HttpRequest.get(String.format(INDEX_CAT_PROGRAM_ME_API, s[1], page, limit))
                    .executeAsStr();
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

                r.add(musicInfo);
            }
        }
        return new CommonResult<>(r, t);
    }
}
