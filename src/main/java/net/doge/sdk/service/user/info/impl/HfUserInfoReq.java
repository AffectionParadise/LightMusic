package net.doge.sdk.service.user.info.impl;

import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class HfUserInfoReq {
    private static HfUserInfoReq instance;

    private HfUserInfoReq() {
    }

    public static HfUserInfoReq getInstance() {
        if (instance == null) instance = new HfUserInfoReq();
        return instance;
    }

    // 用户信息 API (音乐磁场)
    private final String USER_DETAIL_HF_API = "https://www.hifiti.com/user-%s.htm";
    // 用户节目 API (音乐磁场)
    private final String USER_PROGRAMS_HF_API = "https://www.hifiti.com/user-thread-%s-%s.htm";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        userInfo.setSign("");
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(userInfo.getAvatarUrl())));
        GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl("")));
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(NetUserInfo userInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_HF_API, id, page))
                .cookie(SdkCommon.HF_COOKIE)
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);
        Elements songs = doc.select(".media.thread.tap");
        Elements ap = doc.select("a.page-link");
        String ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 1).text());
        if (StringUtil.isEmpty(ts))
            ts = RegexUtil.getGroup1("(\\d+)", ap.isEmpty() ? "" : ap.get(ap.size() - 2).text());
        boolean hasTs = StringUtil.notEmpty(ts);
        if (hasTs) total = Integer.parseInt(ts) * limit;
        else total = limit;
        for (int i = 0, len = songs.size(); i < len; i++) {
            Element song = songs.get(i);

            Elements a = song.select(".subject.break-all a");
            // 用户没有帖子直接跳过
            if (a.isEmpty()) continue;
            Element span = song.select(".haya-post-info-username .username").first();

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

            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }

    /**
     * 获取用户实体 (通过用户 id)
     *
     * @return
     */
    public CommonResult<NetUserInfo> getUserInfo(String id) {
        List<NetUserInfo> res = new LinkedList<>();
        Integer t = 1;

        String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_HF_API, id))
                .cookie(SdkCommon.HF_COOKIE)
                .executeAsStr();
        Document doc = Jsoup.parse(userInfoBody);

        Elements tc = doc.select(".col-md-2.col-sm-12.text-center");
        Elements b = tc.select("b");
        Elements sm = doc.select(".col-4.col-sm-5");

        String userId = id;
        String userName = b.text().trim();
        String gender = "保密";
        String avatarThumbUrl = "https://www.hifiti.com/" + tc.select("img").attr("src");
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

        return new CommonResult<>(res, t);
    }
}
