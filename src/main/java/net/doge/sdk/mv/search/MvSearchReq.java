package net.doge.sdk.mv.search;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import net.doge.constants.GlobalExecutors;
import net.doge.constants.MvInfoType;
import net.doge.constants.NetMusicSource;
import net.doge.models.entities.NetMvInfo;
import net.doge.models.server.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.SdkUtil;
import net.doge.utils.ListUtil;
import net.doge.utils.StringUtil;
import net.doge.utils.TimeUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class MvSearchReq {
    // 关键词搜索 MV API
    private final String SEARCH_MV_API = SdkCommon.prefix + "/cloudsearch?type=1004&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索视频 API
    private final String SEARCH_VIDEO_API = SdkCommon.prefix + "/cloudsearch?type=1014&keywords=%s&limit=%s&offset=%s";
    // 关键词搜索 MV API (酷狗)
    private final String SEARCH_MV_KG_API
            = "http://msearch.kugou.com/api/v3/search/mv?version=9108&keyword=%s&page=%s&pagesize=%s&sver=2";
    // 关键词搜索 MV API (酷我)
    private final String SEARCH_MV_KW_API = "http://www.kuwo.cn/api/www/search/searchMvBykeyWord?key=%s&pn=%s&rn=%s&httpsStatus=1";
    // 关键词搜索 MV API (好看)
    private final String SEARCH_MV_HK_API = "https://haokan.baidu.com/haokan/ui-search/pc/search/video?query=%s&pn=%s&rn=%s&type=video";
    // 关键词搜索 MV API (哔哩哔哩)
    private final String SEARCH_MV_BI_API = "https://api.bilibili.com/x/web-interface/search/type?search_type=video&keyword=%s&page=%s";
    
    /**
     * 根据关键词获取 MV
     */
    public CommonResult<NetMvInfo> searchMvs(int src, String keyword, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMvInfo> mvInfos = new LinkedList<>();
//        Set<NetMvInfo> set = Collections.synchronizedSet(new HashSet<>());

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = StringUtil.encode(keyword);

        // 网易云
        // MV
        Callable<CommonResult<NetMvInfo>> searchMvs = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("mvCount");
                JSONArray mvArray = result.getJSONArray("mvs");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.NET_CLOUD);
                    String creatorId = mvJson.getJSONArray("artists").getJSONObject(0).getString("id");
                    Long playCount = mvJson.getLong("playCount");
                    Double duration = mvJson.getDouble("duration") / 1000;
                    String coverImgUrl = mvJson.getString("cover");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };
        // 视频
        Callable<CommonResult<NetMvInfo>> searchVideos = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_VIDEO_API, encodedKeyword, limit, (page - 1) * limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject result = mvInfoJson.getJSONObject("result");
            if (!result.isEmpty()) {
                t = result.getInt("videoCount");
                JSONArray mvArray = result.getJSONArray("videos");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    Integer type = mvJson.getInt("type");
                    String mvId = mvJson.getString("vid");
                    String mvName = mvJson.getString("title");
                    String creators = SdkUtil.parseCreators(mvJson);
                    String creatorId = mvJson.getJSONArray("creator").getJSONObject(0).getString("userId");
                    Long playCount = mvJson.getLong("playTime");
                    Double duration = mvJson.getDouble("durationms") / 1000;
                    String coverImgUrl = mvJson.getString("coverUrl");

                    NetMvInfo mvInfo = new NetMvInfo();
                    // 网易云视频和 MV 分开了
                    mvInfo.setType(type == 1 ? MvInfoType.VIDEO : MvInfoType.MV);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(creators);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });
                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 酷狗
        Callable<CommonResult<NetMvInfo>> searchMvsKg = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.get(String.format(SEARCH_MV_KG_API, encodedKeyword, page, limit))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("total");
            JSONArray mvArray = data.getJSONArray("info");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("hash");
                // 酷狗返回的名称含有 HTML 标签，需要去除
                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("filename"));
                String artistName = StringUtil.removeHTMLLabel(mvJson.getString("singername"));
                String creatorId = mvJson.getString("userid");
                Long playCount = mvJson.getLong("historyheat");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publishdate").split(" ")[0];
                String coverImgUrl = mvJson.getString("imgurl").replace("/{size}", "");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.KG);
                // 酷狗搜索 MV 只给了用户 id，默认全是视频
                mvInfo.setType(MvInfoType.VIDEO);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // QQ
        Callable<CommonResult<NetMvInfo>> searchMvsQq = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            String mvInfoBody = HttpRequest.post(String.format(SdkCommon.qqSearchApi))
                    .body(String.format(SdkCommon.qqSearchJson, page, limit, keyword, 4))
                    .execute()
                    .body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getInt("sum");
            JSONArray mvArray = data.getJSONObject("body").getJSONObject("mv").getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("v_id");
                String mvName = mvJson.getString("mv_name").trim();
                String artistName = SdkUtil.parseArtists(mvJson, NetMusicSource.QQ);
                String creatorId = mvJson.getJSONArray("singer_list").getJSONObject(0).getString("mid");
                Long playCount = mvJson.getLong("play_count");
                Double duration = mvJson.getDouble("duration");
                String pubTime = mvJson.getString("publish_date");
                String coverImgUrl = mvJson.getString("mv_pic_url").replaceFirst("http:", "https:");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.QQ);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 酷我
        Callable<CommonResult<NetMvInfo>> searchMvsKw = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = SdkCommon.kwRequest(String.format(SEARCH_MV_KW_API, encodedKeyword, page, limit)).execute();
            if (resp.getStatus() == HttpStatus.HTTP_OK) {
                String mvInfoBody = resp.body();
                JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
                JSONObject data = mvInfoJson.getJSONObject("data");
                t = data.getInt("total");
                JSONArray mvArray = data.getJSONArray("mvlist");
                for (int i = 0, len = mvArray.size(); i < len; i++) {
                    JSONObject mvJson = mvArray.getJSONObject(i);

                    String mvId = mvJson.getString("id");
                    String mvName = mvJson.getString("name").trim();
                    String artistName = mvJson.getString("artist");
                    String creatorId = mvJson.getString("artistid");
                    Long playCount = mvJson.getLong("mvPlayCnt");
                    Double duration = mvJson.getDouble("duration");
                    String coverImgUrl = mvJson.getString("pic");

                    NetMvInfo mvInfo = new NetMvInfo();
                    mvInfo.setSource(NetMusicSource.KW);
                    mvInfo.setId(mvId);
                    mvInfo.setName(mvName);
                    mvInfo.setArtist(artistName);
                    mvInfo.setCreatorId(creatorId);
                    mvInfo.setPlayCount(playCount);
                    mvInfo.setDuration(duration);
                    mvInfo.setCoverImgUrl(coverImgUrl);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                        mvInfo.setCoverImgThumb(coverImgThumb);
                    });

                    res.add(mvInfo);
                }
            }
            return new CommonResult<>(res, t);
        };

        // 好看
        Callable<CommonResult<NetMvInfo>> searchMvsHk = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_HK_API, encodedKeyword, page, limit))
                    .header(Header.COOKIE, SdkCommon.HK_COOKIE)
                    .execute();
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = page * limit;
            if (data.getInt("has_more") == 1) t++;
            JSONArray mvArray = data.getJSONArray("list");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String mvId = mvJson.getString("vid");
                String mvName = mvJson.getString("title");
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("author_id");
                Long playCount = StringUtil.parseNumber(mvJson.getString("read_num").replaceFirst("次播放", ""));
                Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = mvJson.getString("publishTimeText");
                String coverImgUrl = mvJson.getString("cover_src");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.HK);
                mvInfo.setId(mvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        // 哔哩哔哩
        Callable<CommonResult<NetMvInfo>> searchMvsBi = () -> {
            LinkedList<NetMvInfo> res = new LinkedList<>();
            Integer t = 0;

            HttpResponse resp = HttpRequest.get(String.format(SEARCH_MV_BI_API, encodedKeyword, page))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute();
            String mvInfoBody = resp.body();
            JSONObject mvInfoJson = JSONObject.fromObject(mvInfoBody);
            JSONObject data = mvInfoJson.getJSONObject("data");
            t = data.getInt("numResults");
            JSONArray mvArray = data.getJSONArray("result");
            for (int i = 0, len = mvArray.size(); i < len; i++) {
                JSONObject mvJson = mvArray.getJSONObject(i);

                String bvId = mvJson.getString("bvid");
                String mvName = StringUtil.removeHTMLLabel(mvJson.getString("title"));
                String artistName = mvJson.getString("author");
                String creatorId = mvJson.getString("mid");
                Long playCount = mvJson.getLong("play");
                Double duration = TimeUtil.toSeconds(mvJson.getString("duration"));
                String pubTime = TimeUtil.msToDate(mvJson.getLong("pubdate") * 1000);
                String coverImgUrl = "https:" + mvJson.getString("pic");

                NetMvInfo mvInfo = new NetMvInfo();
                mvInfo.setSource(NetMusicSource.BI);
                mvInfo.setBvid(bvId);
                mvInfo.setName(mvName);
                mvInfo.setArtist(artistName);
                mvInfo.setCreatorId(creatorId);
                mvInfo.setPlayCount(playCount);
                mvInfo.setDuration(duration);
                mvInfo.setPubTime(pubTime);
                mvInfo.setCoverImgUrl(coverImgUrl);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractMvCover(coverImgUrl);
                    mvInfo.setCoverImgThumb(coverImgThumb);
                });

                res.add(mvInfo);
            }
            return new CommonResult<>(res, t);
        };

        List<Future<CommonResult<NetMvInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvs));
        if (src == NetMusicSource.NET_CLOUD || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchVideos));
        if (src == NetMusicSource.KG || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKg));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsQq));
        if (src == NetMusicSource.KW || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsKw));
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsHk));
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchMvsBi));

        List<List<NetMvInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetMvInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        mvInfos.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(mvInfos, total.get());
    }
}
