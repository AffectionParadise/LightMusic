package net.doge.sdk.service.user.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.nc.NeteaseReqOptEnum;
import net.doge.sdk.common.opt.nc.NeteaseReqOptsBuilder;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.core.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class UserSearchReq {
    private static UserSearchReq instance;

    private UserSearchReq() {
    }

    public static UserSearchReq getInstance() {
        if (instance == null) instance = new UserSearchReq();
        return instance;
    }
    
    // 关键词搜索用户 API
    private final String CLOUD_SEARCH_API = "https://interface.music.163.com/eapi/cloudsearch/pc";
    // 关键词搜索用户 API (喜马拉雅)
    private final String SEARCH_USER_XM_API
            = "https://www.ximalaya.com/revision/search/main?kw=%s&page=%s&spellchecker=true&condition=relation&rows=%s&core=user&device=iPhone";
    // 关键词搜索用户 API (猫耳)
    private final String SEARCH_USER_ME_API = "https://www.missevan.com/sound/getsearch?s=%s&type=1&p=%s&page_size=%s";
    // 关键词搜索用户 API (5sing)
    private final String SEARCH_USER_FS_API = "http://search.5sing.kugou.com/home/json?keyword=%s&sort=1&page=%s&filter=1&type=2";
    // 关键词搜索用户 API (好看)
    private final String SEARCH_USER_HK_API
            = "https://haokan.baidu.com/haokan/ui-search/pc/search/author?pn=%s&query=%s&rn=%s&timestamp=1683718494563&type=author&version=1";
    // 关键词搜索用户 API (豆瓣)
    private final String SEARCH_USER_DB_API = "https://www.douban.com/j/search?q=%s&start=%s&cat=1005";
    // 关键词搜索用户 API (堆糖)
    private final String SEARCH_USER_DT_API = "https://www.duitang.com/napi/people/list/by_search/?kw=%s&start=%s&limit=%s&type=people&_type=&_=%s";
    // 关键词搜索用户 API (哔哩哔哩)
    private final String SEARCH_USER_BI_API = "https://api.bilibili.com/x/web-interface/search/type?search_type=bili_user&keyword=%s&page=%s";

    /**
     * 根据关键词获取用户
     */
    public CommonResult<NetUserInfo> searchUsers(int src, String keyword, int page, int limit) {
        AtomicInteger total = new AtomicInteger();
        List<NetUserInfo> res = new LinkedList<>();

        // 先对关键词编码，避免特殊符号的干扰
        String encodedKeyword = UrlUtil.encodeAll(keyword);

        // 网易云
        Callable<CommonResult<NetUserInfo>> searchUsers = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            Map<NeteaseReqOptEnum, String> options = NeteaseReqOptsBuilder.eapi("/api/cloudsearch/pc");
            String userInfoBody = SdkCommon.ncRequest(Method.POST, CLOUD_SEARCH_API,
                            String.format("{\"s\":\"%s\",\"type\":1002,\"offset\":%s,\"limit\":%s,\"total\":true}", keyword, (page - 1) * limit, limit), options)
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject result = userInfoJson.getJSONObject("result");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("userprofileCount");
                JSONArray userArray = result.getJSONArray("userprofiles");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("userId");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getIntValue("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                    String avatarThumbUrl = userJson.getString("avatarUrl");
                    Integer follow = userJson.getIntValue("follows");
                    Integer fan = userJson.getIntValue("followeds");
                    Integer playlistCount = userJson.getIntValue("playlistCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);
                    userInfo.setPlaylistCount(playlistCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // QQ
        Callable<CommonResult<NetUserInfo>> searchUsersQq = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                    .body(String.format(SdkCommon.QQ_SEARCH_JSON, page, limit, keyword, 8))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("music.search.SearchCgiService").getJSONObject("data");
            t = data.getJSONObject("meta").getIntValue("sum");
            JSONArray userArray = data.getJSONObject("body").getJSONObject("user").getJSONArray("list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("encrypt_uin");
                String userName = userJson.getString("title");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("pic");
                Integer fan = userJson.getIntValue("fans_num");
//                Integer playlistCount = userJson.getIntValue("diss_num");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.QQ);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarThumbUrl);
                userInfo.setFan(fan);
//                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                r.add(userInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 喜马拉雅
        Callable<CommonResult<NetUserInfo>> searchUsersXm = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_XM_API, encodedKeyword, page, limit))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject result = userInfoJson.getJSONObject("data").getJSONObject("user");
            if (JsonUtil.notEmpty(result)) {
                t = result.getIntValue("total");
                JSONArray userArray = result.getJSONArray("docs");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("uid");
                    String userName = userJson.getString("nickname");
                    Integer gen = userJson.getIntValue("gender");
                    String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                    String avatarThumbUrl = userJson.getString("logoPic").replaceFirst("http:", "https:");
                    Integer follow = userJson.getIntValue("followingsCount");
                    Integer fan = userJson.getIntValue("followersCount");
                    Integer radioCount = userJson.getIntValue("albumCount");
                    Integer programCount = userJson.getIntValue("tracksCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.XM);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);
                    userInfo.setRadioCount(radioCount);
                    userInfo.setProgramCount(programCount);

                    String finalAvatarThumbUrl = avatarThumbUrl;
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(finalAvatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 猫耳
        Callable<CommonResult<NetUserInfo>> searchUsersMe = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_ME_API, encodedKeyword, page, limit))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject info = userInfoJson.getJSONObject("info");
            t = info.getJSONObject("pagination").getIntValue("count");
            JSONArray userArray = info.getJSONArray("Datas");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("id");
                String userName = userJson.getString("username");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("avatar2");
//                String bgImgUrl = userJson.getString("coverurl2");
                Integer follow = userJson.getIntValue("follownum");
                Integer fan = userJson.getIntValue("fansnum");
//                Integer radioCount = userJson.getIntValue("albumnum");
                Integer programCount = userJson.getIntValue("soundnum");
//                String sign = userJson.getString("userintro");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.ME);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFan(fan);
//                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);
//                userInfo.setSign(sign);
//                userInfo.setBgImgUrl(bgImgUrl);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                r.add(userInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 5sing
        Callable<CommonResult<NetUserInfo>> searchUsersFs = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_FS_API, encodedKeyword, page))
                    .executeAsync()
                    .body();
            JSONObject data = JSONObject.parseObject(userInfoBody);
            t = data.getJSONObject("pageInfo").getIntValue("totalPages") * limit;
            JSONArray userArray = data.getJSONArray("list");
            if (JsonUtil.notEmpty(userArray)) {
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("id");
                    String name = HtmlUtil.removeHtmlLabel(userJson.getString("nickName"));
                    int sex = userJson.getIntValue("sex");
                    String gender = sex == 0 ? "♂ 男" : sex == 1 ? "♀ 女" : "保密";
                    String avatarThumbUrl = userJson.getString("pictureUrl");
                    Integer follow = userJson.getIntValue("follow");
                    Integer fan = userJson.getIntValue("fans");
                    Integer programCount = userJson.getIntValue("totalSong");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.FS);
                    userInfo.setId(userId);
                    userInfo.setName(name);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);
                    userInfo.setProgramCount(programCount);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(coverImgThumb);
                    });
                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 好看
        Callable<CommonResult<NetUserInfo>> searchUsersHk = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_HK_API, page, encodedKeyword, limit))
                    .cookie(SdkCommon.HK_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            t = data.getIntValue("has_more") == 1 ? (page + 1) * limit : page * limit;
            JSONArray userArray = data.getJSONArray("list");
            for (int i = 0, len = userArray.size(); i < len; i++) {
                JSONObject userJson = userArray.getJSONObject(i);

                String userId = userJson.getString("appid");
                String userName = userJson.getString("name");
                String gender = "保密";
                String avatarThumbUrl = userJson.getString("author_icon");
                Integer fan = userJson.getIntValue("fansCnt");
                Integer programCount = userJson.getIntValue("videoCnt");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.HK);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFan(fan);
                userInfo.setProgramCount(programCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                r.add(userInfo);
            }
            return new CommonResult<>(r, t);
        };

        // 豆瓣
        Callable<CommonResult<NetUserInfo>> searchUsersDb = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            final int lim = Math.min(20, limit);
            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_DB_API, encodedKeyword, (page - 1) * lim))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONArray userArray = userInfoJson.getJSONArray("items");
            if (JsonUtil.notEmpty(userArray)) {
                int to = userInfoJson.getIntValue("total");
                t = PageUtil.totalPage(to, lim) * limit;
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    Document doc = Jsoup.parse(userArray.getString(i));
                    Elements result = doc.select(".result");
                    Elements a = result.select("h3 a");
                    Elements info = result.select(".title .info");
                    Elements img = result.select(".pic img");

                    String userId = RegexUtil.getGroup1("sid: (\\d+)", a.attr("onclick"));
                    String userName = a.text().trim();
                    String gender = "保密";
                    String sr = img.attr("src");
                    String avatarThumbUrl = sr.contains("/user") ? sr.replaceFirst("normal", "large") : sr.replaceFirst("/up", "/ul");
                    Integer fan = Integer.parseInt(RegexUtil.getGroup1("(\\d+)人关注", info.text()));

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.DB);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFan(fan);
                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(coverImgThumb);
                    });

                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 堆糖
        Callable<CommonResult<NetUserInfo>> searchUsersDt = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_DT_API, encodedKeyword, (page - 1) * limit, limit, System.currentTimeMillis()))
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("object_list");
            if (JsonUtil.notEmpty(userArray)) {
                t = data.getIntValue("total");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("id");
                    String userName = userJson.getString("username");
                    String gender = "保密";
                    String avatarThumbUrl = userJson.getString("avatar");
                    Integer follow = userJson.getIntValue("followCount");
                    Integer fan = userJson.getIntValue("beFollowCount");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.DT);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setFollow(follow);
                    userInfo.setFan(fan);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        // 哔哩哔哩
        Callable<CommonResult<NetUserInfo>> searchUsersBi = () -> {
            List<NetUserInfo> r = new LinkedList<>();
            Integer t = 0;

            String userInfoBody = HttpRequest.get(String.format(SEARCH_USER_BI_API, encodedKeyword, page))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsync()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray userArray = data.getJSONArray("result");
            if (JsonUtil.notEmpty(userArray)) {
                t = data.getIntValue("numResults");
                for (int i = 0, len = userArray.size(); i < len; i++) {
                    JSONObject userJson = userArray.getJSONObject(i);

                    String userId = userJson.getString("mid");
                    String userName = userJson.getString("uname");
                    int gen = userJson.getIntValue("gender");
                    String gender = gen == 1 ? "♂ 男" : gen == 2 ? "♀ 女" : "保密";
                    String avatarThumbUrl = "https:" + userJson.getString("upic");
                    Integer programCount = userJson.getIntValue("videos");
                    Integer fan = userJson.getIntValue("fans");

                    NetUserInfo userInfo = new NetUserInfo();
                    userInfo.setSource(NetMusicSource.BI);
                    userInfo.setId(userId);
                    userInfo.setName(userName);
                    userInfo.setGender(gender);
                    userInfo.setAvatarThumbUrl(avatarThumbUrl);
                    userInfo.setProgramCount(programCount);
                    userInfo.setFan(fan);

                    GlobalExecutors.imageExecutor.execute(() -> {
                        BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                        userInfo.setAvatarThumb(avatarThumb);
                    });

                    r.add(userInfo);
                }
            }
            return new CommonResult<>(r, t);
        };

        List<Future<CommonResult<NetUserInfo>>> taskList = new LinkedList<>();

        if (src == NetMusicSource.NC || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsers));
        if (src == NetMusicSource.QQ || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersQq));
        if (src == NetMusicSource.XM || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersXm));
        if (src == NetMusicSource.ME || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersMe));
        if (src == NetMusicSource.FS || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersFs));
        if (src == NetMusicSource.HK || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersHk));
        if (src == NetMusicSource.DB || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersDb));
        if (src == NetMusicSource.DT || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersDt));
        if (src == NetMusicSource.BI || src == NetMusicSource.ALL)
            taskList.add(GlobalExecutors.requestExecutor.submit(searchUsersBi));

        List<List<NetUserInfo>> rl = new LinkedList<>();
        taskList.forEach(task -> {
            try {
                CommonResult<NetUserInfo> result = task.get();
                rl.add(result.data);
                total.set(Math.max(total.get(), result.total));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        res.addAll(ListUtil.joinAll(rl));

        return new CommonResult<>(res, total.get());
    }
}
