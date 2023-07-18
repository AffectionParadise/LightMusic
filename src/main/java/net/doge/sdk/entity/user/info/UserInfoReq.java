package net.doge.sdk.entity.user.info;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.system.NetMusicSource;
import net.doge.model.entity.NetMusicInfo;
import net.doge.model.entity.NetUserInfo;
import net.doge.sdk.common.CommonResult;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.util.AreaUtil;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.common.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class UserInfoReq {
    // 用户信息 API
    private final String USER_DETAIL_API = SdkCommon.prefix + "/user/detail?uid=%s";
    // 用户歌曲 API
    private final String USER_SONGS_API = SdkCommon.prefix + "/user/record?type=%s&uid=%s";
    // 用户信息 API (喜马拉雅)
    private final String USER_DETAIL_XM_API = "https://www.ximalaya.com/revision/user/basic?uid=%s";
    // 用户节目 API (喜马拉雅)
    private final String USER_PROGRAMS_XM_API = "https://www.ximalaya.com/revision/user/track?uid=%s&orderType=%s&page=%s&pageSize=%s&keyWord=";
    // 用户信息 API (音乐磁场)
    private final String USER_DETAIL_HF_API = "https://www.hifini.com/user-%s.htm";
    // 用户节目 API (音乐磁场)
    private final String USER_PROGRAMS_HF_API = "https://www.hifini.com/user-thread-%s-%s.htm";
    // 用户信息 API (咕咕咕音乐)
    private final String USER_DETAIL_GG_API = "http://www.gggmusic.com/user-%s.htm";
    // 用户节目 API (咕咕咕音乐)
    private final String USER_PROGRAMS_GG_API = "http://www.gggmusic.com/user-thread-%s-%s.htm";
    // 用户信息 API (猫耳)
    private final String USER_DETAIL_ME_API = "https://www.missevan.com/%s/";
    // 用户节目 API (猫耳)
    private final String USER_PROGRAMS_ME_API = "https://www.missevan.com/person/getusersound?order=%s&user_id=%s&p=%s&page_size=%s";
    // 用户信息 API (好看)
    private final String USER_DETAIL_HK_API = "https://haokan.baidu.com/author/%s";
    // 用户信息 API (豆瓣)
    private final String USER_DETAIL_DB_API = "https://www.douban.com/people/%s/";
    // 用户信息 API (堆糖)
    private final String USER_DETAIL_DT_API = "https://www.duitang.com/people/?id=%s";
    // 用户信息 API (哔哩哔哩)
    private final String USER_DETAIL_BI_API = "https://api.bilibili.com/x/web-interface/card?mid=%s&photo=true";
    // 用户音频 API (猫耳)
    private final String USER_AUDIO_BI_API = "https://api.bilibili.com/audio/music-service/web/song/upper?order=%s&uid=%s&pn=%s&ps=%s";
    // 用户信息 API (5sing)
    private final String USER_DETAIL_FS_API = "http://5sing.kugou.com/%s/default.html";
    // 用户歌曲(原唱) API (5sing)
    private final String USER_YC_SONGS_FS_API = "http://5sing.kugou.com/%s/yc/%s.html";
    // 用户歌曲(翻唱) API (5sing)
    private final String USER_FC_SONGS_FS_API = "http://5sing.kugou.com/%s/fc/%s.html";
    // 用户歌曲(伴奏) API (5sing)
    private final String USER_BZ_SONGS_FS_API = "http://5sing.kugou.com/%s/bz/%s.html";

    /**
     * 根据用户 id 预加载用户信息
     */
    public void preloadUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;

        GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatarThumb(SdkUtil.extractCover(userInfo.getAvatarThumbUrl())));
    }

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        // 信息完整直接跳过
        if (userInfo.isIntegrated()) return;

        int source = userInfo.getSource();
        String uid = userInfo.getId();

        // 网易云
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_API, uid))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject profileJson = userInfoJson.getJSONObject("profile");
            if (!userInfo.hasLevel()) userInfo.setLevel(userInfoJson.getIntValue("level"));
            if (!userInfo.hasAccAge()) userInfo.setAccAge(TimeUtil.getAccAge(profileJson.getLong("createTime")));
            if (!userInfo.hasBirthday()) userInfo.setBirthday(TimeUtil.msToDate(profileJson.getLong("birthday")));
            if (!userInfo.hasArea())
                userInfo.setArea(AreaUtil.getArea(profileJson.getIntValue("province"), profileJson.getIntValue("city")));
            if (!userInfo.hasSign()) userInfo.setSign(profileJson.getString("signature"));
            if (!userInfo.hasFollow()) userInfo.setFollow(profileJson.getIntValue("follows"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(profileJson.getIntValue("followeds"));
            if (!userInfo.hasPlaylistCount()) userInfo.setPlaylistCount(profileJson.getIntValue("playlistCount"));

            String avatarUrl = profileJson.getString("avatarUrl");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

            String bgImgUrl = profileJson.getString("backgroundUrl");
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            userInfo.setSign("");
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(userInfo.getAvatarUrl())));
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, uid))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            if (!userInfo.hasLevel()) userInfo.setLevel(data.getIntValue("anchorGrade"));
            if (!userInfo.hasGender()) {
                Integer gen = data.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                userInfo.setGender(gender);
            }
            if (!userInfo.hasBirthday())
                userInfo.setBirthday(data.getIntValue("birthMonth") <= 0 ? null : data.getString("birthMonth") + "-" + data.getString("birthDay"));
            if (!userInfo.hasArea()) {
                String area = (data.containsKey("province") ? data.getString("province") : "") + (data.containsKey("city") ? " - " + data.getString("city") : "");
                userInfo.setArea(StringUtil.isEmpty(area) ? "未知" : area);
            }
            if (!userInfo.hasSign()) userInfo.setSign(data.getString("personalSignature"));
            if (!userInfo.hasFollow()) userInfo.setFollow(data.getIntValue("followingCount"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(data.getIntValue("fansCount"));
            if (!userInfo.hasRadioCount()) userInfo.setRadioCount(data.getIntValue("albumsCount"));
            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(data.getIntValue("tracksCount"));

            String avatarUrl = "https:" + data.getString("cover");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

            String bgImgUrl = "https:" + data.getString("background");
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            userInfo.setSign("");
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(userInfo.getAvatarUrl())));
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            userInfo.setSign("");
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(userInfo.getAvatarUrl())));
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            Runnable getProgramCount = () -> {
                // 用户节目数
                if (!userInfo.hasProgramCount()) {
                    GlobalExecutors.requestExecutor.submit(() -> {
                        String programCountBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, 0, uid, 1, 1))
                                .execute()
                                .body();
                        Integer programCount = JSONObject.parseObject(programCountBody).getJSONObject("info").getJSONObject("pagination").getIntValue("count");
                        userInfo.setProgramCount(programCount);
                    });
                }
            };
            Runnable fillUserInfo = () -> {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, uid))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                if (!userInfo.hasLevel())
                    userInfo.setLevel(Integer.parseInt(doc.select("span.level").first().text().replace("LV", "")));
                if (!userInfo.hasGender()) userInfo.setGender("保密");
                if (!userInfo.hasSign())
                    userInfo.setSign(doc.select("#t_u_n_a").first().text());
                if (!userInfo.hasFollow())
                    userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
                if (!userInfo.hasFollowed())
                    userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));

                String avatarUrl = "https:" + doc.select("div#topusermainicon img").attr("src");
                if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
                GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

                String bgUrl = RegexUtil.getGroup1("background-image: url\\([\"'](.*?)[\"']\\)", userInfoBody);
                String bgImgUrl = (bgUrl.startsWith("//static") ? "https:" : "https://www.missevan.com") + bgUrl;
                if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
                GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
            };

            List<Future<?>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(fillUserInfo));
            taskList.add(GlobalExecutors.requestExecutor.submit(getProgramCount));

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

        // 5sing
        else if (source == NetMusicSource.FS) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_FS_API, uid))
                    .setFollowRedirects(true)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);

            // 个人信息面板 ui 不同，需要分情况
            // 签名
            Elements intro = doc.select(".home_fullTxt");
            if (intro.isEmpty()) intro = doc.select(".rig.intro p");
            if (intro.isEmpty()) intro = doc.select(".simpleTxt");
            if (intro.isEmpty()) intro = doc.select(".resurm");
            if (intro.isEmpty()) intro = doc.select(".intr_box");
            // 头像
            Elements img = doc.select(".m_about.lt dt img");
            if (img.isEmpty()) img = doc.select(".user_pic img");
            if (img.isEmpty()) img = doc.select(".photo img");
            if (img.isEmpty()) img = doc.select(".b_con.c_wap dt img");
            if (img.isEmpty()) img = doc.select(".my_pic img");
            if (img.isEmpty()) img = doc.select(".p_abs img");
            // 关注数
            Elements followElem = doc.select("#totalfriend a");
            if (followElem.isEmpty()) followElem = doc.select(".lt.w_20 a");
            // 粉丝数
            Elements followedElem = doc.select("#totalfans a");
            // 背景图
            Elements bgImgElem = doc.select("html > body");

            String sign = intro.first().ownText();
            String avatarUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
            Integer follow = Integer.parseInt(followElem.text());
            Integer followed = Integer.parseInt(followedElem.text());

            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
            userInfo.setSign(sign);
            if (!userInfo.hasFollow()) userInfo.setFollow(follow);
            if (!userInfo.hasFollowed()) userInfo.setFollowed(followed);

            String bgImgUrl = RegexUtil.getGroup1("background-image:url\\((.*?)\\)", bgImgElem.attr("style"));
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        }

        // 好看
        else if (source == NetMusicSource.HK) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HK_API, uid))
                    .cookie(SdkCommon.HK_COOKIE)
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(RegexUtil.getGroup1("\"author_info\":(\\{.*?\\})", userInfoBody));

            if (!userInfo.hasSign()) userInfo.setSign(userInfoJson.getString("wishes"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(userInfoJson.getIntValue("fansCnt"));
            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(userInfoJson.getIntValue("videoCnt"));

            String avatarUrl = userInfoJson.getString("avatar");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
        }

        // 豆瓣
        else if (source == NetMusicSource.DB) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, uid))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);

            if (!userInfo.hasAccAge()) {
                String dt = RegexUtil.getGroup1("(\\d+\\-\\d+\\-\\d+)加入", doc.select("div.pl").text());
                userInfo.setAccAge(TimeUtil.getAccAge(TimeUtil.dateToMs(dt)));
            }
            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasArea()) userInfo.setArea(doc.select("div.user-info a").text());
            if (!userInfo.hasSign())
                userInfo.setSign(StringUtil.getPrettyText(doc.select("span#intro_display").first()));
//            if (!userInfo.hasFollow())
//                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
//            if (!userInfo.hasFollowed())
//                userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));

            String avatarUrl = doc.select("div.basic-info img").attr("src");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));
        }

        // 堆糖
        else if (source == NetMusicSource.DT) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DT_API, uid))
                    .setFollowRedirects(true)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            Elements a = doc.select("div.people-funs a");

            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasSign())
                userInfo.setSign(doc.select("div.people-desc").text().trim());
            if (!userInfo.hasProgramCount()) {
                String s = doc.select("ul.people-nav li a").get(1).select("u").text().trim();
                if (StringUtil.notEmpty(s)) userInfo.setProgramCount(Integer.parseInt(s));
            }
            if (!userInfo.hasFollow())
                userInfo.setFollow(Integer.parseInt(RegexUtil.getGroup1("(\\d+)", a.first().text())));
            if (!userInfo.hasFollowed())
                userInfo.setFollowed(Integer.parseInt(RegexUtil.getGroup1("(\\d+)", a.last().text())));

            String avatarUrl = doc.select("a.people-avatar img").attr("src").replaceFirst("\\.thumb\\.\\d+_\\d+_\\w+", "");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

            String bgImgUrl = doc.select("img.header-bg").attr("src");
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_BI_API, uid))
                    .cookie(SdkCommon.BI_COOKIE)
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONObject card = data.getJSONObject("card");

            if (!userInfo.hasLevel()) userInfo.setLevel(card.getJSONObject("level_info").getIntValue("current_level"));
            if (!userInfo.hasGender()) userInfo.setGender(card.getString("sex"));
            if (!userInfo.hasBirthday()) userInfo.setBirthday(card.getString("birthday"));
            if (!userInfo.hasSign()) userInfo.setSign(card.getString("sign"));
            if (!userInfo.hasFollow()) userInfo.setFollow(card.getIntValue("attention"));
            if (!userInfo.hasFollowed()) userInfo.setFollowed(card.getIntValue("fans"));
            if (!userInfo.hasProgramCount()) userInfo.setProgramCount(data.getIntValue("archive_count"));

            String avatarUrl = card.getString("face");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

            String bgImgUrl = data.getJSONObject("space").getString("s_img");
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.submit(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        }
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int limit, int page) {
        AtomicInteger total = new AtomicInteger();
        List<NetMusicInfo> musicInfos = new LinkedList<>();
        boolean isAll = recordType == 1;

        int source = userInfo.getSource();
        String userId = userInfo.getId();

        // 网易云(程序分页)
        if (source == NetMusicSource.NET_CLOUD) {
            String userInfoBody = HttpRequest.get(String.format(USER_SONGS_API, recordType ^ 1, userId))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONArray songArray = userInfoJson.getJSONArray(isAll ? "allData" : "weekData");
            if (songArray != null) {
                total.set(songArray.size());
                for (int i = (page - 1) * limit, len = Math.min(songArray.size(), page * limit); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i).getJSONObject("song");

                    String songId = songJson.getString("id");
                    String name = songJson.getString("name").trim();
                    String artist = SdkUtil.parseArtists(songJson, NetMusicSource.NET_CLOUD);
                    String artistId = songJson.getJSONArray("ar").getJSONObject(0).getString("id");
                    String albumName = songJson.getJSONObject("al").getString("name");
                    String albumId = songJson.getJSONObject("al").getString("id");
                    String albumImgUrl = songJson.getJSONObject("al").getString("picUrl");
                    Double duration = songJson.getDouble("dt") / 1000;

                    NetMusicInfo musicInfo = new NetMusicInfo();
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

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_XM_API, userId, recordType + 1, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            JSONArray songArray = data.getJSONArray("trackList");
            total.set(data.getIntValue("totalCount"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("trackId");
                String name = songJson.getString("title");
                String artist = songJson.getString("nickname");
                String artistId = songJson.getString("anchorUid");
                String albumName = songJson.getString("albumTitle");
                String albumId = songJson.getString("albumId");
                Double duration = songJson.getDouble("length");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.XM);
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

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_HF_API, userId, page))
                    .cookie(SdkCommon.HF_COOKIE)
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            Elements songs = doc.select(".media.thread.tap");
            Elements ap = doc.select("a.page-link");
            String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
            if (StringUtil.isEmpty(ts))
                ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
            boolean hasTs = StringUtil.notEmpty(ts);
            if (hasTs) total.set(Integer.parseInt(ts) * limit);
            else total.set(songs.size());
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);

                Elements a = song.select(".subject.break-all a");
                // 用户没有帖子直接跳过
                if (a.isEmpty()) continue;
                Element span = song.select(".username.text-grey.mr-1").first();

                String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
                String songName = a.text();
                String artist = span.text();
                String artistId = span.attr("uid");

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.HF);
                musicInfo.setId(songId);
                musicInfo.setName(songName);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);

                musicInfos.add(musicInfo);
            }
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_GG_API, userId, page))
                    .execute()
                    .body();
            Document doc = Jsoup.parse(userInfoBody);
            Elements songs = doc.select(".media.thread.tap");
            Elements ap = doc.select("a.page-link");
            String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
            if (StringUtil.isEmpty(ts))
                ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
            boolean hasTs = StringUtil.notEmpty(ts);
            if (hasTs) total.set(Integer.parseInt(ts) * limit);
            else total.set(songs.size());
            for (int i = 0, len = songs.size(); i < len; i++) {
                Element song = songs.get(i);

                Elements a = song.select(".subject.break-all a");
                // 用户没有帖子直接跳过
                if (a.isEmpty()) continue;

                String songId = RegexUtil.getGroup1("thread-(.*?)\\.htm", a.attr("href"));
                String songName = a.text();

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.GG);
                musicInfo.setId(songId);
                musicInfo.setName(songName);

                musicInfos.add(musicInfo);
            }
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, recordType, userId, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("info");
            JSONArray songArray = data.getJSONArray("Datas");
            total.set(data.getJSONObject("pagination").getIntValue("count"));
            for (int i = 0, len = songArray.size(); i < len; i++) {
                JSONObject songJson = songArray.getJSONObject(i);

                String songId = songJson.getString("id");
                String name = songJson.getString("soundstr");
                String artist = userInfo.getName();
                String artistId = userInfo.getId();
                Double duration = songJson.getDouble("duration") / 1000;

                NetMusicInfo musicInfo = new NetMusicInfo();
                musicInfo.setSource(NetMusicSource.ME);
                musicInfo.setId(songId);
                musicInfo.setName(name);
                musicInfo.setArtist(artist);
                musicInfo.setArtistId(artistId);
                musicInfo.setDuration(duration);
                musicInfos.add(musicInfo);
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            // 原唱
            Callable<CommonResult<NetMusicInfo>> getYc = () -> {
                LinkedList<NetMusicInfo> res = new LinkedList<>();
                Integer t = 0;

                String userInfoBody = HttpRequest.get(String.format(USER_YC_SONGS_FS_API, userId, page))
                        .setFollowRedirects(true)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                // 判断主页 ui 类型
                int pageType = 1;
                if (!doc.select(".mainnav").isEmpty()) pageType = 2;
                else if (!doc.select(".right h1").isEmpty()) pageType = 3;
                else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
                else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
                else if (!doc.select(".user_name").isEmpty()) pageType = 6;

                switch (pageType) {
                    case 1:
                        Elements pageElem = doc.select(".page_number em");
                        String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                        t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                        break;
                    case 2:
                        pageElem = doc.select(".page_list span");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 3:
                        pageElem = doc.select(".page_message a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 4:
                    case 6:
                        pageElem = doc.select("span.page_list a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 5:
                        pageElem = doc.select(".page span");
                        pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                }

                // 主页 ui 不同
                Elements songArray;
                if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
                else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
                else if (pageType == 3) songArray = doc.select(".song_tb_a a");
                else if (pageType == 4) songArray = doc.select(".lt.list_name a");
                else if (pageType == 5) songArray = doc.select(".per_td a");
                else songArray = doc.select(".s_title.list_name.lt a");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    Element song = songArray.get(i);

                    String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                    String name = song.text();
                    String artist = userInfo.getName();
                    String artistId = userId;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    res.add(musicInfo);
                }

                return new CommonResult<>(res, t);
            };
            // 翻唱
            Callable<CommonResult<NetMusicInfo>> getFc = () -> {
                LinkedList<NetMusicInfo> res = new LinkedList<>();
                Integer t = 0;

                String userInfoBody = HttpRequest.get(String.format(USER_FC_SONGS_FS_API, userId, page))
                        .setFollowRedirects(true)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                // 判断主页 ui 类型
                int pageType = 1;
                if (!doc.select(".mainnav").isEmpty()) pageType = 2;
                else if (!doc.select(".right h1").isEmpty()) pageType = 3;
                else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
                else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
                else if (!doc.select(".user_name").isEmpty()) pageType = 6;

                switch (pageType) {
                    case 1:
                        Elements pageElem = doc.select(".page_number em");
                        String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                        t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                        break;
                    case 2:
                        pageElem = doc.select(".page_list span");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 3:
                        pageElem = doc.select(".page_message a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 4:
                    case 6:
                        pageElem = doc.select("span.page_list a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 5:
                        pageElem = doc.select(".page span");
                        pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                }

                // 主页 ui 不同
                Elements songArray;
                if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
                else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
                else if (pageType == 3) songArray = doc.select(".song_tb_a a");
                else if (pageType == 4) songArray = doc.select(".lt.list_name a");
                else if (pageType == 5) songArray = doc.select(".per_td a");
                else songArray = doc.select(".s_title.list_name.lt a");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    Element song = songArray.get(i);

                    String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                    String name = song.text();
                    String artist = userInfo.getName();
                    String artistId = userId;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    res.add(musicInfo);
                }

                return new CommonResult<>(res, t);
            };
            // 伴奏
            Callable<CommonResult<NetMusicInfo>> getBz = () -> {
                LinkedList<NetMusicInfo> res = new LinkedList<>();
                Integer t = 0;

                String userInfoBody = HttpRequest.get(String.format(USER_BZ_SONGS_FS_API, userId, page))
                        .setFollowRedirects(true)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                // 判断主页 ui 类型
                int pageType = 1;
                if (!doc.select(".mainnav").isEmpty()) pageType = 2;
                else if (!doc.select(".right h1").isEmpty()) pageType = 3;
                else if (!doc.select(".rank_ry.c_wap").isEmpty()) pageType = 4;
                else if (!doc.select(".rank_ry h3").isEmpty()) pageType = 5;
                else if (!doc.select(".user_name").isEmpty()) pageType = 6;

                switch (pageType) {
                    case 1:
                        Elements pageElem = doc.select(".page_number em");
                        String pageText = pageElem.isEmpty() ? "" : pageElem.text();
                        t = StringUtil.notEmpty(pageText) ? (Integer.parseInt(pageText) / 20 + 1) * limit + 1 : limit;
                        break;
                    case 2:
                        pageElem = doc.select(".page_list span");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.last().text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 3:
                        pageElem = doc.select(".page_message a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.last().attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 4:
                    case 6:
                        pageElem = doc.select("span.page_list a");
                        pageText = pageElem.isEmpty() ? "" : RegexUtil.getGroup1("/(\\d+)\\.html", pageElem.get(pageElem.size() - 1).attr("href"));
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                    case 5:
                        pageElem = doc.select(".page span");
                        pageText = pageElem.size() <= 1 ? "" : RegexUtil.getGroup1("第\\d+/(\\d+)页", pageElem.get(pageElem.size() - 3).text());
                        t = StringUtil.notEmpty(pageText) ? Integer.parseInt(pageText) * limit : limit;
                        break;
                }

                // 主页 ui 不同
                Elements songArray;
                if (pageType == 1) songArray = doc.select(".song_list li .song_name a");
                else if (pageType == 2) songArray = doc.select(".song tr td:nth-child(2) a");
                else if (pageType == 3) songArray = doc.select(".song_tb_a a");
                else if (pageType == 4) songArray = doc.select(".lt.list_name a");
                else if (pageType == 5) songArray = doc.select(".per_td a");
                else songArray = doc.select(".s_title.list_name.lt a");
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    Element song = songArray.get(i);

                    String songId = RegexUtil.getGroup1("http://5sing.kugou.com/(.*?)\\.html", song.attr("href")).replaceFirst("/", "_");
                    String name = song.text();
                    String artist = userInfo.getName();
                    String artistId = userId;

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.FS);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);

                    res.add(musicInfo);
                }

                return new CommonResult<>(res, t);
            };

            List<Future<CommonResult<NetMusicInfo>>> taskList = new LinkedList<>();

            taskList.add(GlobalExecutors.requestExecutor.submit(getYc));
            taskList.add(GlobalExecutors.requestExecutor.submit(getFc));
            taskList.add(GlobalExecutors.requestExecutor.submit(getBz));

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
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String userInfoBody = HttpRequest.get(String.format(USER_AUDIO_BI_API, recordType + 1, userId, page, limit))
                    .execute()
                    .body();
            JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
            JSONObject data = userInfoJson.getJSONObject("data");
            total.set(data.getIntValue("totalSize"));
            JSONArray songArray = data.getJSONArray("data");
            if (songArray != null) {
                for (int i = 0, len = songArray.size(); i < len; i++) {
                    JSONObject songJson = songArray.getJSONObject(i);

                    String songId = songJson.getString("id");
                    String name = songJson.getString("title");
                    String artist = songJson.getString("uname");
                    String artistId = songJson.getString("uid");
                    Double duration = songJson.getDouble("duration");

                    NetMusicInfo musicInfo = new NetMusicInfo();
                    musicInfo.setSource(NetMusicSource.BI);
                    musicInfo.setId(songId);
                    musicInfo.setName(name);
                    musicInfo.setArtist(artist);
                    musicInfo.setArtistId(artistId);
                    musicInfo.setDuration(duration);
                    musicInfos.add(musicInfo);
                }
            }
        }

        return new CommonResult<>(musicInfos, total.get());
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id, int source) {
        LinkedList<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        if (!"0".equals(id) && StringUtil.notEmpty(id)) {
            // 网易云
            if (source == NetMusicSource.NET_CLOUD) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_API, id))
                        .execute()
                        .body();
                JSONObject userJson = JSONObject.parseObject(userInfoBody).getJSONObject("profile");

                String userId = userJson.getString("userId");
                String userName = userJson.getString("nickname");
                Integer gen = userJson.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String accAge = TimeUtil.getAccAge(userJson.getLong("createTime"));
                String avatarThumbUrl = userJson.getString("avatarUrl");
                Integer follow = userJson.getIntValue("follows");
                Integer followed = userJson.getIntValue("followeds");
                Integer playlistCount = userJson.getIntValue("playlistCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAccAge(accAge);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setPlaylistCount(playlistCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 喜马拉雅
            else if (source == NetMusicSource.XM) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_XM_API, id))
                        .execute()
                        .body();
                JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
                JSONObject data = userInfoJson.getJSONObject("data");

                String userId = data.getString("uid");
                String userName = data.getString("nickName");
                Integer gen = data.getIntValue("gender");
                String gender = gen == 0 ? "保密" : gen == 1 ? "♂ 男" : "♀ 女";
                String avatarThumbUrl = "https:" + data.getString("cover");
                Integer follow = data.getIntValue("followingCount");
                Integer followed = data.getIntValue("fansCount");
                Integer radioCount = data.getIntValue("albumsCount");
                Integer programCount = data.getIntValue("tracksCount");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.XM);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setRadioCount(radioCount);
                userInfo.setProgramCount(programCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 音乐磁场
            else if (source == NetMusicSource.HF) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HF_API, id))
                        .cookie(SdkCommon.HF_COOKIE)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                Elements tc = doc.select(".col-md-2.col-sm-12.text-center");
                Elements b = tc.select("b");
                Elements sm = doc.select(".col-4.col-sm-5");

                String userId = id;
                String userName = b.text().trim();
                String gender = "保密";
                String avatarThumbUrl = "https://www.hifini.com/" + tc.select("img").attr("src");
                String avatarUrl = avatarThumbUrl;
                Integer programCount = Integer.parseInt(RegexUtil.getGroup1("主题数：(\\d+)", sm.text()));

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.HF);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setProgramCount(programCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 咕咕咕音乐
            else if (source == NetMusicSource.GG) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_GG_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                Elements tc = doc.select(".col-md-2.col-sm-12.text-center");
                Elements b = tc.select("b");
                Elements sm = doc.select(".col-4.col-sm-5");

                String userId = id;
                String userName = b.text().trim();
                String gender = "保密";
                String avatarThumbUrl = "http://www.gggmusic.com/" + tc.select("img").attr("src");
                String avatarUrl = avatarThumbUrl;
                Integer programCount = Integer.parseInt(RegexUtil.getGroup1("主题数：(\\d+)", sm.text()));

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.GG);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setAvatarUrl(avatarUrl);
                userInfo.setProgramCount(programCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 猫耳
            else if (source == NetMusicSource.ME) {
                NetUserInfo userInfo = new NetUserInfo();

                Runnable getProgramCount = () -> {
                    // 用户节目数
                    GlobalExecutors.requestExecutor.submit(() -> {
                        String programCountBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, 0, id, 1, 1))
                                .execute()
                                .body();
                        Integer programCount = JSONObject.parseObject(programCountBody).getJSONObject("info").getJSONObject("pagination").getIntValue("count");
                        userInfo.setProgramCount(programCount);
                    });
                };
                Runnable fillUserInfo = () -> {
                    String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, id))
                            .execute()
                            .body();
                    Document doc = Jsoup.parse(userInfoBody);

                    userInfo.setSource(NetMusicSource.ME);
                    userInfo.setId(id);
                    Element tun = doc.getElementById("t_u_n");
                    // 判断账号是否已注销
                    if (tun != null) {
                        userInfo.setName(tun.getElementsByTag("a").first().text());
                        String avaUrl = "https:" + doc.getElementById("topusermainicon").getElementsByTag("img").first().attr("src");
                        userInfo.setAvatarThumbUrl(avaUrl);
                        userInfo.setGender("保密");
                        userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").first().text()));
                        userInfo.setFollowed(Integer.parseInt(doc.select(".home-fans span").first().text()));

                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage avatarThumb = SdkUtil.extractCover(avaUrl);
                            userInfo.setAvatarThumb(avatarThumb);
                        });

                        res.add(userInfo);
                    }
                };

                List<Future<?>> taskList = new LinkedList<>();

                taskList.add(GlobalExecutors.requestExecutor.submit(fillUserInfo));
                taskList.add(GlobalExecutors.requestExecutor.submit(getProgramCount));

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

            // 5sing
            else if (source == NetMusicSource.FS) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_FS_API, id))
                        .setFollowRedirects(true)
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                // 个人信息面板 ui 不同，需要分情况
                // 用户名
                Elements nameElem = doc.select("h1.lt");
                if (nameElem.isEmpty()) nameElem = doc.select(".user_info h2");
                if (nameElem.isEmpty()) nameElem = doc.select(".right h1");
                if (nameElem.isEmpty()) nameElem = doc.select(".rank_ry.c_wap h1");
                if (nameElem.isEmpty()) nameElem = doc.select(".per_info h1");
                if (nameElem.isEmpty()) nameElem = doc.select(".user_name h3");
                // 头像
                Elements img = doc.select(".m_about.lt dt img");
                if (img.isEmpty()) img = doc.select(".user_pic img");
                if (img.isEmpty()) img = doc.select(".photo img");
                if (img.isEmpty()) img = doc.select(".b_con.c_wap dt img");
                if (img.isEmpty()) img = doc.select(".my_pic img");
                if (img.isEmpty()) img = doc.select(".p_abs img");
                // 关注数
                Elements followElem = doc.select("#totalfriend a");
                if (followElem.isEmpty()) followElem = doc.select(".lt.w_20 a");
                // 粉丝数
                Elements followedElem = doc.select("#totalfans a");

                String name = nameElem.text().replaceFirst("音乐人：", "");
                String avatarThumbUrl = img.attr("src").replaceFirst("_\\d+x\\d+\\.\\w+", "");
                Integer follow = Integer.parseInt(followElem.text());
                Integer followed = Integer.parseInt(followedElem.text());

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.FS);
                userInfo.setId(id);
                userInfo.setName(name);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });

                res.add(userInfo);
            }

            // 好看
            else if (source == NetMusicSource.HK) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HK_API, id))
                        .cookie(SdkCommon.HK_COOKIE)
                        .execute()
                        .body();
                JSONObject data = JSONObject.parseObject(RegexUtil.getGroup1("\"author_info\":(\\{.*?\\})", userInfoBody));

                String userId = data.getString("id");
                String userName = data.getString("name");
                String gender = "保密";
                String avatarThumbUrl = data.getString("avatar");
                Integer followed = data.getIntValue("fansCnt");
                Integer programCount = data.getIntValue("videoCnt");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.HK);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }

            // 豆瓣
            else if (source == NetMusicSource.DB) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_DB_API, id))
                        .execute()
                        .body();
                Document doc = Jsoup.parse(userInfoBody);

                Element h1 = doc.select("div.info > h1").first();
                Elements img = doc.select("div.basic-info img");

                if (h1 != null) {
                    String userName = h1.ownText();
                    if (StringUtil.notEmpty(userName)) {
                        String gender = "保密";
                        String avatarThumbUrl = img.attr("src");

                        NetUserInfo userInfo = new NetUserInfo();
                        userInfo.setSource(NetMusicSource.DB);
                        userInfo.setId(id);
                        userInfo.setName(userName);
                        userInfo.setGender(gender);
                        userInfo.setAvatarThumbUrl(avatarThumbUrl);
                        GlobalExecutors.imageExecutor.execute(() -> {
                            BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                            userInfo.setAvatarThumb(coverImgThumb);
                        });

                        res.add(userInfo);
                    }
                }
            }

            // 哔哩哔哩
            else if (source == NetMusicSource.BI) {
                String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_BI_API, id))
                        .cookie(SdkCommon.BI_COOKIE)
                        .execute()
                        .body();
                JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
                JSONObject data = userInfoJson.getJSONObject("data");
                JSONObject card = data.getJSONObject("card");

                String userId = card.getString("mid");
                String userName = card.getString("name");
                String gender = card.getString("sex");
                gender = "男".equals(gender) ? "♂ " + gender : "女".equals(gender) ? "♀ " + gender : "保密";
                String avatarThumbUrl = card.getString("face");
                Integer follow = card.getIntValue("attention");
                Integer followed = card.getIntValue("fans");
                Integer programCount = data.getIntValue("archive_count");

                NetUserInfo userInfo = new NetUserInfo();
                userInfo.setSource(NetMusicSource.BI);
                userInfo.setId(userId);
                userInfo.setName(userName);
                userInfo.setGender(gender);
                userInfo.setAvatarThumbUrl(avatarThumbUrl);
                userInfo.setFollow(follow);
                userInfo.setFollowed(followed);
                userInfo.setProgramCount(programCount);
                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage coverImgThumb = SdkUtil.extractCover(avatarThumbUrl);
                    userInfo.setAvatarThumb(coverImgThumb);
                });

                res.add(userInfo);
            }
        }

        return new CommonResult<>(res, t);
    }
}
