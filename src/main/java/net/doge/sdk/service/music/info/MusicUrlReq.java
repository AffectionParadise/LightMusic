package net.doge.sdk.service.music.info;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.media.AudioQuality;
import net.doge.constant.core.os.Format;
import net.doge.constant.service.NetMusicSource;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.common.entity.CommonResult;
import net.doge.sdk.common.opt.fs.FiveSingReqOptEnum;
import net.doge.sdk.common.opt.fs.FiveSingReqOptsBuilder;
import net.doge.sdk.service.music.info.entity.MusicCandidate;
import net.doge.sdk.service.music.info.trackhero.kg.KgTrackHeroV2;
import net.doge.sdk.service.music.info.trackhero.kw.KwTrackHeroV3;
import net.doge.sdk.service.music.info.trackhero.mg.MgTrackHero;
import net.doge.sdk.service.music.info.trackhero.nc.CunYuNcTrackHero;
import net.doge.sdk.service.music.info.trackhero.nc.CyruiNcTrackHero;
import net.doge.sdk.service.music.info.trackhero.nc.NcTrackHero;
import net.doge.sdk.service.music.info.trackhero.nc.TmetuNcTrackHero;
import net.doge.sdk.service.music.info.trackhero.qq.LittleYouziQqTrackHero;
import net.doge.sdk.service.music.info.trackhero.qq.QqTrackHeroV2;
import net.doge.sdk.service.music.info.trackhero.qq.VkeysQqTrackHero;
import net.doge.sdk.service.music.search.MusicSearchReq;
import net.doge.sdk.util.SdkUtil;
import net.doge.util.core.CryptoUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.UrlUtil;
import net.doge.util.http.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MusicUrlReq {
    private static MusicUrlReq instance;

    private MusicUrlReq() {
    }

    public static MusicUrlReq getInstance() {
        if (instance == null) instance = new MusicUrlReq();
        return instance;
    }

    // 歌曲 URL 获取 API (咪咕)
//    private final String SONG_URL_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";
    // 歌曲 URL 获取 API (千千)
    private final String SONG_URL_QI_API = "https://music.91q.com/v1/song/tracklink?TSID=%s&appid=16073360&timestamp=%s";
    // 歌曲 URL 获取 API (喜马拉雅)
    private final String SONG_URL_XM_API = "https://www.ximalaya.com/revision/play/v1/audio?id=%s&ptype=1";
    // 歌曲 URL 获取 API (哔哩哔哩)
    private final String SONG_URL_BI_API = "https://www.bilibili.com/audio/music-service-c/web/url?sid=%s";
    // 歌曲 URL 获取 API (5sing)
    private final String SONG_URL_FS_API = "http://service.5sing.kugou.com/song/getsongurl";
    // 歌曲 URL 获取 API (发姐)
    private final String SONG_URL_FA_API = "https://www.chatcyf.com/wp-admin/admin-ajax.php?action=hermit&musicset=%s&_nonce=%s";
    // 歌曲 URL 获取 API (李志)
    private final String SONG_URL_LZ_API = "https://www.lizhinb.com/?audioigniter_playlist_id=%s";

    // 歌曲信息 API (酷狗)
//    private final String SINGLE_SONG_DETAIL_KG_API = "https://www.kugou.com/yy/index.php?r=play/getdata&album_audio_id=%s";
    // 歌曲信息 API (音乐磁场)
    private final String SINGLE_SONG_DETAIL_HF_API = "https://www.hifiti.com/thread-%s.htm";
    // 歌曲信息 API (咕咕咕音乐)
    private final String SINGLE_SONG_DETAIL_GG_API = "http://www.gggmusic.com/thread-%s.htm";
    // 歌曲信息 API (猫耳)
    private final String SINGLE_SONG_DETAIL_ME_API = "https://www.missevan.com/sound/getsound?soundid=%s";

    /**
     * 补充 NetMusicInfo 的 url
     */
    public void fillMusicUrl(NetMusicInfo musicInfo) {
        // 歌曲信息是完整的且音质与设置的音质相同
        if (musicInfo.isIntegrated() && musicInfo.isQualityMatch()) return;

        // 无链接，直接换源
        String url = fetchMusicUrl(musicInfo);
        if (StringUtil.notEmpty(url)) musicInfo.setUrl(url);
        else fillAvailableMusicUrl(musicInfo);

        String realUrl = musicInfo.getUrl();
        if (realUrl.contains(".mp3") || realUrl.contains(".wav")) musicInfo.setFormat(Format.MP3);
        else if (realUrl.contains(".flac")) musicInfo.setFormat(Format.FLAC);
        else if (realUrl.contains(".m4a")) musicInfo.setFormat(Format.M4A);

        // 更新音质
        musicInfo.setQuality(AudioQuality.quality);
    }

    /**
     * 根据歌曲 id 获取歌曲地址
     */
    public String fetchMusicUrl(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();
        String hash = musicInfo.getHash();
        int source = musicInfo.getSource();

        // 网易云
        if (source == NetMusicSource.NC) {
            String quality;
            switch (AudioQuality.quality) {
                case AudioQuality.MASTER:
                    quality = AudioQuality.KEYS[AudioQuality.MASTER];
                    break;
                case AudioQuality.ATMOSPHERE:
                    quality = AudioQuality.KEYS[AudioQuality.ATMOSPHERE];
                    break;
                case AudioQuality.HI_RES:
                    quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                    break;
                case AudioQuality.LOSSLESS:
                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                    break;
                case AudioQuality.SUPER:
                case AudioQuality.HIGH:
                    quality = AudioQuality.KEYS[AudioQuality.HIGH];
                    break;
                default:
                    quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                    break;
            }
            String trackUrl = TmetuNcTrackHero.getInstance().getTrackUrl(id, quality);
            if (StringUtil.isEmpty(trackUrl)) trackUrl = CunYuNcTrackHero.getInstance().getTrackUrl(id, quality);
            if (StringUtil.isEmpty(trackUrl)) trackUrl = CyruiNcTrackHero.getInstance().getTrackUrl(id, quality);
            if (StringUtil.isEmpty(trackUrl)) trackUrl = NcTrackHero.getInstance().getTrackUrl(id, quality);
            return trackUrl;
        }

        // 酷狗
        else if (source == NetMusicSource.KG) {
            String quality;
            switch (AudioQuality.quality) {
                case AudioQuality.MASTER:
                case AudioQuality.ATMOSPHERE:
                    quality = AudioQuality.KEYS[AudioQuality.ATMOSPHERE];
                    break;
                case AudioQuality.HI_RES:
                    quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                    break;
                case AudioQuality.LOSSLESS:
                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                    break;
                case AudioQuality.SUPER:
                case AudioQuality.HIGH:
                    quality = AudioQuality.KEYS[AudioQuality.HIGH];
                    break;
                default:
                    quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                    break;
            }
            return KgTrackHeroV2.getInstance().getTrackUrl(hash, quality);
        }

        // QQ
        else if (source == NetMusicSource.QQ) {
            String quality;
            switch (AudioQuality.quality) {
                case AudioQuality.MASTER:
                    quality = AudioQuality.KEYS[AudioQuality.MASTER];
                    break;
                case AudioQuality.ATMOSPHERE:
                    quality = AudioQuality.KEYS[AudioQuality.ATMOSPHERE];
                    break;
                case AudioQuality.HI_RES:
                    quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                    break;
                case AudioQuality.LOSSLESS:
                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                    break;
                case AudioQuality.SUPER:
                    quality = AudioQuality.KEYS[AudioQuality.SUPER];
                    break;
                case AudioQuality.HIGH:
                    quality = AudioQuality.KEYS[AudioQuality.HIGH];
                    break;
                default:
                    quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                    break;
            }
            String trackUrl = VkeysQqTrackHero.getInstance().getTrackUrl(id, quality);
            if (StringUtil.isEmpty(trackUrl)) trackUrl = LittleYouziQqTrackHero.getInstance().getTrackUrl(id, quality);
            if (StringUtil.isEmpty(trackUrl)) trackUrl = QqTrackHeroV2.getInstance().getTrackUrl(id, quality);
            return trackUrl;
        }

        // 酷我
        else if (source == NetMusicSource.KW) {
            String quality;
            switch (AudioQuality.quality) {
                case AudioQuality.MASTER:
                case AudioQuality.ATMOSPHERE:
                case AudioQuality.HI_RES:
                case AudioQuality.LOSSLESS:
                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                    break;
                case AudioQuality.SUPER:
                case AudioQuality.HIGH:
                    quality = AudioQuality.KEYS[AudioQuality.HIGH];
                    break;
                default:
                    quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                    break;
            }
            return KwTrackHeroV3.getInstance().getTrackUrl(id, quality);
        }

        // 咪咕
        else if (source == NetMusicSource.MG) {
            String quality;
            switch (AudioQuality.quality) {
                case AudioQuality.MASTER:
                case AudioQuality.ATMOSPHERE:
                case AudioQuality.HI_RES:
                    quality = AudioQuality.KEYS[AudioQuality.HI_RES];
                    break;
                case AudioQuality.LOSSLESS:
                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
                    break;
                case AudioQuality.SUPER:
                case AudioQuality.HIGH:
                    quality = AudioQuality.KEYS[AudioQuality.HIGH];
                    break;
                default:
                    quality = AudioQuality.KEYS[AudioQuality.STANDARD];
                    break;
            }
            return MgTrackHero.getInstance().getTrackUrl(id, quality);
//            String songBody = HttpRequest.get(String.format(SONG_URL_MG_API, id))
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
//            JSONArray rateFormats = data.getJSONArray("rateFormats");
//            rateFormats.addAll(data.getJSONArray("newRateFormats"));
//            String quality, urlKey;
//            String[] qs = {"SQ", "HQ", "PQ", "LQ"};
//            switch (AudioQuality.quality) {
//                case AudioQuality.HI_RES:
//                case AudioQuality.LOSSLESS:
//                    quality = "SQ";
//                    urlKey = "androidUrl";
//                    break;
//                case AudioQuality.SUPER:
//                    quality = "HQ";
//                    urlKey = "url";
//                    break;
//                case AudioQuality.HIGH:
//                    quality = "PQ";
//                    urlKey = "url";
//                    break;
//                default:
//                    quality = "LQ";
//                    urlKey = "url";
//                    break;
//            }
//            for (int i = rateFormats.size() - 1; i >= 0; i--) {
//                JSONObject urlJson = rateFormats.getJSONObject(i);
//                if (ArrayUtil.indexOf(qs, quality) > ArrayUtil.indexOf(qs, urlJson.getString("formatType"))) continue;
//                String ftp = urlJson.getString(urlKey);
//                if (StringUtil.isEmpty(ftp)) continue;
//                String url = ftp.replaceFirst("ftp://[^/]+", "https://freetyst.nf.migu.cn");
//                return StringUtil.urlEncodeBlank(url);
//            }
        }

        // 千千
        else if (source == NetMusicSource.QI) {
            String playUrlBody = SdkCommon.qiRequest(String.format(SONG_URL_QI_API, id, System.currentTimeMillis()))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(playUrlBody).getJSONObject("data");
            // 排除试听部分，直接换源
            if (data.getIntValue("isVip") == 0) {
                String url = data.getString("path");
                if (StringUtil.isEmpty(url)) url = data.getJSONObject("trail_audio_info").getString("path");
                return url;
            }
        }

        // 音乐磁场
        else if (source == NetMusicSource.HF) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_HF_API, id))
                    .cookie(SdkCommon.HF_COOKIE)
                    .executeAsStr();
            Document doc = Jsoup.parse(songBody);
            String dataStr = RegexUtil.getGroup1("audio:\\[.*?(\\{.*?\\}).*?\\]", doc.html());
            if (StringUtil.notEmpty(dataStr)) {
                // json 字段带引号
                JSONObject data = JSONObject.parseObject(dataStr.replaceAll("(\\w+):'(.*?)'", "'$1':'$2'"));
                String url = UrlUtil.encodeBlank(data.getString("url"));
                if (url.startsWith("http")) return url;
                return SdkUtil.getRedirectUrl("https://www.hifiti.com/" + url);
            }
        }

        // 咕咕咕音乐
        else if (source == NetMusicSource.GG) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_GG_API, id))
                    .executeAsStr();
            Document doc = Jsoup.parse(songBody);
            String dataStr = RegexUtil.getGroup1("(?:audio|music): \\[.*?(\\{.*?\\}).*?\\]", doc.html());
            if (StringUtil.notEmpty(dataStr)) {
                String base64Pattern = "base64_decode\\(\"(.*?)\"\\)";
                String base64Str = RegexUtil.getGroup1(base64Pattern, dataStr);
                if (StringUtil.notEmpty(base64Str))
                    dataStr = dataStr.replaceFirst(base64Pattern, String.format("\"%s\"", CryptoUtil.base64Decode(base64Str)));

                // json 字段带引号
                JSONObject data = JSONObject.parseObject(dataStr.replaceAll(" (\\w+):", "'$1':"));
                String url = UrlUtil.encodeBlank(data.getString("url"));
                if (url.startsWith("http")) return url;
                else {
                    // 获取重定向之后的 url
                    String startUrl = "http://www.gggmusic.com" + url;
                    String newUrl = SdkUtil.getRedirectUrl(startUrl);
                    return StringUtil.isEmpty(newUrl) ? startUrl : newUrl;
                }
            }
        }

        // 5sing
        else if (source == NetMusicSource.FS) {
            String[] sp = id.split("_");
            Map<String, Object> params = new TreeMap<>();
            params.put("songtype", sp[0]);
            params.put("songid", sp[1]);
            params.put("version", "6.6.72");
            Map<FiveSingReqOptEnum, Object> options = FiveSingReqOptsBuilder.get(SONG_URL_FS_API);
            String songBody = SdkCommon.fsRequest(params, null, options)
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("data");
            String url = AudioQuality.quality <= AudioQuality.SUPER ? data.getString("squrl") : "";
            if (StringUtil.isEmpty(url)) url = AudioQuality.quality <= AudioQuality.HIGH ? data.getString("hqurl") : "";
            if (StringUtil.isEmpty(url)) url = data.getString("lqurl");
            return url;
        }

        // 喜马拉雅
        else if (source == NetMusicSource.XM) {
            String playUrlBody = HttpRequest.get(String.format(SONG_URL_XM_API, id))
                    .executeAsStr();

            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            return urlJson.getJSONObject("data").getString("src");
        }

        // 猫耳
        else if (source == NetMusicSource.ME) {
            String songBody = HttpRequest.get(String.format(SINGLE_SONG_DETAIL_ME_API, id))
                    .executeAsStr();
            JSONObject data = JSONObject.parseObject(songBody).getJSONObject("info").getJSONObject("sound");
            return data.getString(AudioQuality.quality == AudioQuality.STANDARD ? "soundurl_128" : "soundurl");
        }

        // 哔哩哔哩
        else if (source == NetMusicSource.BI) {
            String playUrlBody = HttpRequest.get(String.format(SONG_URL_BI_API, id))
                    .cookie(SdkCommon.BI_COOKIE)
                    .executeAsStr();
            JSONObject urlJson = JSONObject.parseObject(playUrlBody);
            return urlJson.getJSONObject("data").getJSONArray("cdns").getString(0);
        }

        // 发姐
        else if (source == NetMusicSource.FA) {
            // 获取发姐请求参数
            String body = HttpRequest.get(SdkCommon.FA_RADIO_API)
                    .executeAsStr();
            Document doc = Jsoup.parse(body);
            Elements ap = doc.select("#aplayer1");
            String musicSet = UrlUtil.encodeAll(ap.attr("data-songs"));
            String _nonce = ap.attr("data-_nonce");

            String songInfoBody = HttpRequest.get(String.format(SONG_URL_FA_API, musicSet, _nonce))
                    .executeAsStr();
            JSONObject songInfoJson = JSONObject.parseObject(songInfoBody);
            JSONObject data = songInfoJson.getJSONObject("msg");
            JSONArray songArray = data.getJSONArray("songs");
            for (int i = 0, s = songArray.size(); i < s; i++) {
                JSONObject songJson = songArray.getJSONObject(i);
                if (!id.equals(songJson.getString("id"))) continue;
                return UrlUtil.encodeBlank(songJson.getString("url"));
            }
        }

        // 李志
        else if (source == NetMusicSource.LZ) {
            String[] sp = id.split("_");
            String albumSongBody = HttpRequest.get(String.format(SONG_URL_LZ_API, sp[0]))
                    .executeAsStr();
            JSONArray songArray = JSONArray.parseArray(albumSongBody);
            JSONObject urlJson = songArray.getJSONObject(Integer.parseInt(sp[1]));
            return UrlUtil.encodeBlank(urlJson.getString("audio"));
        }

        // 果核
//        else if (source == NetMusicSource.GH) {
//            String quality;
//            switch (AudioQuality.quality) {
//                case AudioQuality.HI_RES:
//                case AudioQuality.LOSSLESS:
//                    quality = AudioQuality.KEYS[AudioQuality.LOSSLESS];
//                    break;
//                case AudioQuality.SUPER:
//                case AudioQuality.HIGH:
//                    quality = "320";
//                    break;
//                default:
//                    quality = "128";
//                    break;
//            }
//            String urlBody = HttpRequest.post(SdkCommon.GH_MAIN_API)
//                    .cookie(SdkCommon.GH_COOKIE)
//                    .form("action", "gh_music_ajax")
//                    .form("type", "getMusicUrl")
//                    .form("music_type", "qq")
//                    .form("music_size", quality)
//                    .form("songid", id)
//                    .executeAsync()
//                    .body();
//            JSONObject urlJson = JSONObject.parseObject(urlBody);
//            return urlJson.getString("url");
//        }

        return "";
    }

    /**
     * 歌曲换源
     *
     * @param musicInfo
     * @return
     */
    public void fillAvailableMusicUrl(NetMusicInfo musicInfo) {
        CommonResult<NetMusicInfo> result = MusicSearchReq.getInstance().searchMusic(NetMusicSource.ALL, 0, "默认", musicInfo.toKeywords(), 1, 20);
        List<NetMusicInfo> data = result.data;
        List<MusicCandidate> candidates = new LinkedList<>();
        MusicInfoReq musicInfoReq = MusicInfoReq.getInstance();
        for (NetMusicInfo info : data) {
            // 部分歌曲没有时长，先填充时长，准备判断
            if (!info.hasDuration()) musicInfoReq.fillDuration(info);
            double nameSimi = StringUtil.similar(info.getName(), musicInfo.getName());
            double artistSimi = StringUtil.similar(info.getArtist(), musicInfo.getArtist());
            double albumSimi = StringUtil.similar(info.getAlbumName(), musicInfo.getAlbumName());
            // 匹配依据：歌名、歌手相似度，时长之差绝对值。如果合适，纳入候选者
            if (info.equals(musicInfo)
                    || nameSimi == 0
                    || artistSimi == 0
                    || info.hasDuration() && musicInfo.hasDuration() && Math.abs(info.getDuration() - musicInfo.getDuration()) > 3)
                continue;
            double weight = nameSimi * 2 + artistSimi + albumSimi * 2;
            candidates.add(new MusicCandidate(info, weight));
        }
        // 将所有候选的匹配按照相关度排序
        candidates.sort((c1, c2) -> Double.compare(c2.weight, c1.weight));
        for (MusicCandidate candidate : candidates) {
            NetMusicInfo info = candidate.musicInfo;
            String url = fetchMusicUrl(info);
            if (StringUtil.isEmpty(url)) continue;
            musicInfo.setUrl(url);
            if (!musicInfo.hasDuration()) musicInfo.setDuration(info.getDuration());
            return;
        }
    }
}
