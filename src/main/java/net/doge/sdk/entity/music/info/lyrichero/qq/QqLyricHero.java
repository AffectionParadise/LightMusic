package net.doge.sdk.entity.music.info.lyrichero.qq;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.lyric.LyricPattern;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.entity.music.info.lyrichero.qq.decoder.QrcDecoder;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.DurationUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class QqLyricHero {
    private static QqLyricHero instance;

    private QqLyricHero() {
    }

    public static QqLyricHero getInstance() {
        if (instance == null) instance = new QqLyricHero();
        return instance;
    }

    // 歌词 API (QQ)
    private final String LYRIC_QQ_API = "https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg?version=15&miniversion=82&lrctype=4&musicid=%s";
    private final String LYRIC_QQ_API_2 = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0";
//    private final String SEARCH_QRC_QQ_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_search_pc_lrc.fcg?SONGNAME=%s&SINGERNAME=%s&TYPE=2&RANGE_MIN=1&RANGE_MAX=20";

    public void fillLrc(NetMusicInfo musicInfo) {
        String mid = musicInfo.getId();
//        String name = musicInfo.getName();
//        String artist = musicInfo.getArtist();

//        String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
//                .header(Header.REFERER, "https://y.qq.com/portal/player.html")
//                .executeAsync()
//                .body();
//        JSONObject lrcJson = JSONObject.parseObject(lrcBody);
//        String lyric = lrcJson.getString("lyric");
//        String trans = lrcJson.getString("trans");
//        musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
//        musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));

//        // 搜索 qrc
//        String lrcSearchBody = HttpRequest.get(String.format(SEARCH_QRC_QQ_API, StringUtil.urlEncodeAll(name), StringUtil.urlEncodeAll(artist)))
//                .executeAsync()
//                .body();
//        Document doc = Jsoup.parse(lrcSearchBody);
//        Elements songInfos = doc.select("songinfo");
//        // 没有 qrc 用 lrc 代替
//        if (songInfos.isEmpty()) {
//            String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
//                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
//                    .executeAsync()
//                    .body();
//            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
//            String lyric = lrcJson.getString("lyric");
//            String trans = lrcJson.getString("trans");
//            musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
//            musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));
//            return;
//        }
        //        Element songInfo = songInfos.get(0);
//        String songId = songInfo.attr("id");
//        String lrcBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
//                .body(String.format("{\"comm\":{\"ct\":\"19\",\"cv\":\"1859\",\"uin\":\"0\"},\"req\":{\"method\":\"GetPlayLyricInfo\"," +
//                        "\"module\":\"music.musichallSong.PlayLyricInfo\",\"param\":{\"format\":\"json\",\"crypt\":1,\"ct\":19," +
//                        "\"cv\":1873,\"interval\":0,\"lrc_t\":0,\"qrc\":1,\"qrc_t\":0,\"roma\":1,\"roma_t\":0,\"songID\":%s," +
//                        "\"trans\":1,\"trans_t\":0,\"type\":-1}}}", songId))
//                .header(Header.REFERER, "https://y.qq.com")
//                .executeAsync()
//                .body();
//        JSONObject data = JSONObject.parseObject(lrcBody).getJSONObject("req").getJSONObject("data");
//        String qrcStr = QrcParser.getInstance().parse(data.getString("lyric"));

        // 先根据 mid 获取 id
        String musicInfoBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
                .body(String.format("{\"songinfo\":{\"method\":\"get_song_detail_yqq\",\"module\":\"music.pf_song_detail_svr\",\"param\":{\"song_mid\":\"%s\"}}}", mid))
                .executeAsync()
                .body();
        JSONObject musicInfoJson = JSONObject.parseObject(musicInfoBody);
        String id = musicInfoJson.getJSONObject("songinfo").getJSONObject("data").getJSONObject("track_info").getString("id");
        // 获取歌词
        String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
                .executeAsync()
                .body();
        Document doc = Jsoup.parse(lrcBody.replaceAll("(<!--)|(-->)", ""));
        String lrcHex = doc.select("content").text();
        String transHex = doc.select("contentts").text();
        String romaHex = doc.select("contentroma").text();
        QrcDecoder qrcDecoder = QrcDecoder.getInstance();
        String qrcXml = qrcDecoder.decode(lrcHex);
        // 没有 qrc 用 lrc 代替
        if (StringUtil.isEmpty(qrcXml) && StringUtil.notEmpty(lrcHex)) {
            lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API_2, mid))
                    .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                    .executeAsync()
                    .body();
            JSONObject lrcJson = JSONObject.parseObject(lrcBody);
            String lyric = lrcJson.getString("lyric");
            String trans = lrcJson.getString("trans");
            musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
            musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));
            musicInfo.setRoma("");
        }
        // qrc
        else {
            String lrc = parseQrcXml(qrcXml);
            musicInfo.setLrc(lrc);
            // 翻译
            if (StringUtil.notEmpty(transHex)) {
                String trans = qrcDecoder.decode(transHex);
                musicInfo.setTrans(trans);
            }
            // 罗马音
            if (StringUtil.notEmpty(romaHex)) {
                String romaXml = qrcDecoder.decode(romaHex);
                String roma = parseQrcXml(romaXml);
                musicInfo.setRoma(roma);
            }
        }
    }

    // 从 qrc 的 xml 格式中解析歌词
    private String parseQrcXml(String xmlStr) {
        Document doc = Jsoup.parse(xmlStr);
        String lyric = doc.select("Lyric_1").attr("LyricContent");
        String[] lsp = lyric.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String l : lsp) {
            if (RegexUtil.contains("\\[\\d+,\\d+\\]", l)) {
                // 行起始时间
                String lineStartStr = RegexUtil.getGroup1("\\[(\\d+),\\d+\\]", l);
                int lineStart = Integer.parseInt(lineStartStr);
                String lrcTime = DurationUtil.formatToLrcTime((double) lineStart / 1000);
                sb.append(lrcTime);

                List<String> wordStartList = RegexUtil.findAllGroup1("\\((\\d+),\\d+\\)", l);
                List<String> wordDurationList = RegexUtil.findAllGroup1("\\(\\d+,(\\d+)\\)", l);
                // qrc 逐字时间轴在后
                String[] sp = ArrayUtil.removeLastEmpty(l.replaceFirst("\\[\\d+,\\d+\\]", "").split("\\(\\d+,\\d+\\)", -1));
                for (int i = 0, s = wordStartList.size(); i < s; i++) {
                    String wordStart = wordStartList.get(i);
                    int wsi = Integer.parseInt(wordStart);
                    sb.append(String.format(LyricPattern.PAIR_FMT, wsi - lineStart, wordDurationList.get(i)));
                    sb.append(sp[i]);
                }
            } else sb.append(l);
            sb.append("\n");
        }
        return sb.toString();
    }
}
