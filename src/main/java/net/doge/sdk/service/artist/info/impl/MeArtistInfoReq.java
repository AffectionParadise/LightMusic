package net.doge.sdk.service.artist.info.impl;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.async.GlobalExecutors;
import net.doge.constant.service.source.NetResourceSource;
import net.doge.entity.service.NetArtistInfo;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.core.text.HtmlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class MeArtistInfoReq {
    private static MeArtistInfoReq instance;

    private MeArtistInfoReq() {
    }

    public static MeArtistInfoReq getInstance() {
        if (instance == null) instance = new MeArtistInfoReq();
        return instance;
    }

    // 社团信息 API (猫耳)
    private final String ORGANIZATION_DETAIL_ME_API = "https://www.missevan.com/organization/profile?organization_id=%s";
    // CV 信息 API (猫耳)
    private final String CV_DETAIL_ME_API = "https://www.missevan.com/dramaapi/cvinfo?cv_id=%s&page=%s&page_size=%s";
    // 声优节目 API (猫耳)
    private final String CV_PROGRAMS_ME_API = "https://www.missevan.com/seiy/%s";

    /**
     * 根据歌手 id 补全歌手信息(包括封面图、描述)
     */
    public void fillArtistInfo(NetArtistInfo artistInfo) {
        String id = artistInfo.getId();
        if (artistInfo.isOrganization()) {
            String artistInfoBody = HttpRequest.get(String.format(ORGANIZATION_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject artistInfoJson = JSONObject.parseObject(artistInfoBody);
            JSONObject data = artistInfoJson.getJSONObject("info").getJSONObject("organization");

            String coverImgUrl = data.getString("avatar");
            String intro = HtmlUtil.removeHtmlLabel(data.getString("intro"));
            String announcement = HtmlUtil.removeHtmlLabel(data.getString("announcement"));

            if (!artistInfo.hasDescription()) artistInfo.setDescription(intro + "\n\n" + announcement);

            if (!artistInfo.hasCoverImgUrl()) artistInfo.setCoverImgUrl(coverImgUrl);
            GlobalExecutors.imageExecutor.execute(() -> artistInfo.setCoverImg(SdkUtil.getImageFromUrl(coverImgUrl)));
        } else {
            String artistInfoBody = HttpRequest.get(String.format(CV_DETAIL_ME_API, id, 1, 1))
                    .executeAsStr();
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

    /**
     * 根据歌手 id 获取里面歌曲的粗略信息，分页，返回 NetMusicInfo
     */
    public CommonResult<NetMusicInfo> getMusicInfoInArtist(NetArtistInfo artistInfo, int page, int limit) {
        List<NetMusicInfo> res = new LinkedList<>();
        int total;

        String id = artistInfo.getId();
        String artistInfoBody = HttpRequest.get(String.format(CV_PROGRAMS_ME_API, id))
                .executeAsStr();
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
            musicInfo.setSource(NetResourceSource.ME);
            musicInfo.setId(songId);
            musicInfo.setName(name);
//                musicInfo.setArtist(artist);
//                musicInfo.setArtistId(artistId);
            res.add(musicInfo);
        }

        return new CommonResult<>(res, total);
    }
}
