package net.doge.sdk.entity.music.info.lyrichero;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.model.entity.NetMusicInfo;
import net.doge.util.common.CryptoUtil;
import net.doge.util.common.StringUtil;

public class QqLyricHero {
    private static QqLyricHero instance;
    // 歌词 API (QQ)
    private final String LYRIC_QQ_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=%s&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8¬ice=0&platform=yqq&needNewCode=0";
    //    private final String LYRIC_QQ_XML_API = "https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg?version=15&miniversion=82&lrctype=4&musicid=%s";
    private final String SEARCH_QRC_QQ_API = "https://c.y.qq.com/lyric/fcgi-bin/fcg_search_pc_lrc.fcg?SONGNAME=%s&SINGERNAME=%s&TYPE=2&RANGE_MIN=1&RANGE_MAX=20";

    private QqLyricHero() {
    }

    public static QqLyricHero getInstance() {
        if (instance == null) instance = new QqLyricHero();
        return instance;
    }

    public void fillLrc(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
                .header(Header.REFERER, "https://y.qq.com/portal/player.html")
                .executeAsync()
                .body();
        JSONObject lrcJson = JSONObject.parseObject(lrcBody);
        String lyric = lrcJson.getString("lyric");
        String trans = lrcJson.getString("trans");
        musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
        musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));
//            // 搜索 qrc
//            String lrcSearchBody = HttpRequest.get(String.format(SEARCH_QRC_QQ_API, StringUtil.urlEncodeAll(name), StringUtil.urlEncodeAll(artist)))
//                    .executeAsync()
//                    .body();
//            Document doc = Jsoup.parse(lrcSearchBody);
//            Elements songInfos = doc.select("songinfo");
//            // 没有 qrc 用 lrc 代替
//            if (songInfos.isEmpty()) {
//                String lrcBody = HttpRequest.get(String.format(LYRIC_QQ_API, id))
//                        .header(Header.REFERER, "https://y.qq.com/portal/player.html")
//                        .executeAsync()
//                        .body();
//                JSONObject lrcJson = JSONObject.parseObject(lrcBody);
//                String lyric = lrcJson.getString("lyric");
//                String trans = lrcJson.getString("trans");
//                musicInfo.setLrc(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(lyric)));
//                musicInfo.setTrans(StringUtil.removeHTMLLabel(CryptoUtil.base64Decode(trans)));
//                return;
//            }
//            Element songInfo = songInfos.get(0);
//            String songId = songInfo.attr("id");
////            String title = StringUtil.urlDecode(songInfo.select("name").text());
////            String singerName = StringUtil.urlDecode(songInfo.select("singername").text());
////            String albumName = StringUtil.urlDecode(songInfo.select("albumname").text());
//            String lrcBody = HttpRequest.post(SdkCommon.QQ_MAIN_API)
//                    .body(String.format("{\"comm\":{\"ct\":\"19\",\"cv\":\"1859\",\"uin\":\"0\"},\"req\":{\"method\":\"GetPlayLyricInfo\"," +
//                                    "\"module\":\"music.musichallSong.PlayLyricInfo\",\"param\":{\"format\":\"json\",\"crypt\":1,\"ct\":19," +
//                                    "\"cv\":1873,\"interval\":0,\"lrc_t\":0,\"qrc\":1,\"qrc_t\":0,\"roma\":1,\"roma_t\":0,\"songID\":%s," +
//                                    "\"trans\":1,\"trans_t\":0,\"type\":-1}}}", songId))
//                    .header(Header.REFERER, "https://y.qq.com")
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(lrcBody).getJSONObject("req").getJSONObject("data");
//            String lyric = QrcParser.parse(data.getString("lyric")).replaceAll("\\((\\d+),(\\d+)\\)", "<$1,$2>");
//            musicInfo.setLrc(lyric);
//            String trans = data.getString("trans");
//            String roma = data.getString("roma");
//            System.out.println(data);
    }
}
