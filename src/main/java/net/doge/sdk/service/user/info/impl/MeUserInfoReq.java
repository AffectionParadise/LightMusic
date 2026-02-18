package net.doge.sdk.service.user.info.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.entity.service.NetUserInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class MeUserInfoReq {
    private static MeUserInfoReq instance;

    private MeUserInfoReq() {
    }

    public static MeUserInfoReq getInstance() {
        if (instance == null) instance = new MeUserInfoReq();
        return instance;
    }

    // 用户信息 API (猫耳)
    private final String USER_DETAIL_ME_API = "https://www.missevan.com/%s/";
    // 用户节目 API (猫耳)
    private final String USER_PROGRAMS_ME_API = "https://www.missevan.com/person/getusersound?order=%s&user_id=%s&p=%s&page_size=%s";

    /**
     * 根据用户 id 补全用户信息(包括封面图、描述)
     */
    public void fillUserInfo(NetUserInfo userInfo) {
        String id = userInfo.getId();
        Runnable getProgramCount = () -> {
            // 用户节目数
            if (!userInfo.hasProgramCount()) {
                GlobalExecutors.requestExecutor.execute(() -> {
                    String programCountBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, 0, id, 1, 1))
                            .executeAsStr();
                    Integer programCount = JSONObject.parseObject(programCountBody).getJSONObject("info").getJSONObject("pagination").getIntValue("count");
                    userInfo.setProgramCount(programCount);
                });
            }
        };
        Runnable fillUserInfo = () -> {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(userInfoBody);
            Elements level = doc.select("span.level");
            if (level.isEmpty()) return;
            Elements tuna = doc.select("#t_u_n_a");
            Elements follow = doc.select(".home-follow span");
            Elements fans = doc.select(".home-fans span");

            if (!userInfo.hasLevel()) userInfo.setLevel(Integer.parseInt(level.text().replace("LV", "")));
            if (!userInfo.hasGender()) userInfo.setGender("保密");
            if (!userInfo.hasSign()) userInfo.setSign(tuna.text());
            if (!userInfo.hasFollow()) userInfo.setFollow(Integer.parseInt(follow.text()));
            if (!userInfo.hasFan()) userInfo.setFan(Integer.parseInt(fans.text()));

            String avatarUrl = "https:" + doc.select("#topusermainicon img").attr("src");
            if (!userInfo.hasAvatarUrl()) userInfo.setAvatarUrl(avatarUrl);
            GlobalExecutors.imageExecutor.execute(() -> userInfo.setAvatar(SdkUtil.getImageFromUrl(avatarUrl)));

            String bgUrl = RegexUtil.getGroup1("background-image: url\\([\"'](.*?)[\"']\\)", userInfoBody);
            String bgImgUrl = bgUrl.startsWith("http") ? bgUrl : "https:" + bgUrl;
            if (!userInfo.hasBgImgUrl()) userInfo.setBgImgUrl(bgImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> userInfo.setBgImg(SdkUtil.getImageFromUrl(bgImgUrl)));
        };

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(fillUserInfo);
        executor.submit(getProgramCount);
        executor.await();
    }

    /**
     * 根据用户 id 获取里面歌曲的 id 并获取每首歌曲粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInUser(int recordType, NetUserInfo userInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = userInfo.getId();
        String userInfoBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, recordType, id, page, limit))
                .executeAsStr();
        JSONObject userInfoJson = JSONObject.parseObject(userInfoBody);
        JSONObject data = userInfoJson.getJSONObject("info");
        JSONArray songArray = data.getJSONArray("Datas");
        total = data.getJSONObject("pagination").getIntValue("count");
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

        NetUserInfo userInfo = new NetUserInfo();
        Runnable getProgramCount = () -> {
            // 用户节目数
            GlobalExecutors.requestExecutor.execute(() -> {
                String programCountBody = HttpRequest.get(String.format(USER_PROGRAMS_ME_API, 0, id, 1, 1))
                        .executeAsStr();
                Integer programCount = JSONObject.parseObject(programCountBody).getJSONObject("info").getJSONObject("pagination").getIntValue("count");
                userInfo.setProgramCount(programCount);
            });
        };
        Runnable fillUserInfo = () -> {
            String userInfoBody = HttpRequest.get(String.format(USER_DETAIL_ME_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(userInfoBody);

            userInfo.setSource(NetMusicSource.ME);
            userInfo.setId(id);
            Element tun = doc.getElementById("t_u_n");
            // 判断账号是否已注销
            if (tun != null) {
                userInfo.setName(tun.select("a").text());
                String avaUrl = "https:" + doc.select("#topusermainicon img").attr("src");
                userInfo.setAvatarThumbUrl(avaUrl);
                userInfo.setGender("保密");
                userInfo.setFollow(Integer.parseInt(doc.select(".home-follow span").text()));
                userInfo.setFan(Integer.parseInt(doc.select(".home-fans span").text()));

                GlobalExecutors.imageExecutor.execute(() -> {
                    BufferedImage avatarThumb = SdkUtil.extractCover(avaUrl);
                    userInfo.setAvatarThumb(avatarThumb);
                });

                res.add(userInfo);
            }
        };

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(fillUserInfo);
        executor.submit(getProgramCount);
        executor.await();

        return new CommonResult<>(res, t);
    }
}
