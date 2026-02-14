package net.doge.sdk.service.music.info.lyrichero.mg;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.entity.service.NetMusicInfo;
import net.doge.sdk.service.music.info.lyrichero.mg.decoder.MrcDecoder;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.core.DurationUtil;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.http.HttpRequest;

import java.util.List;

public class MgLyricHero {
    private static MgLyricHero instance;

    private MgLyricHero() {
    }

    public static MgLyricHero getInstance() {
        if (instance == null) instance = new MgLyricHero();
        return instance;
    }

    // 歌词 API (咪咕)
    private final String LYRIC_MG_API = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?copyrightId=%s&resourceType=2";
//    private final String LYRIC_MG_API = "https://music.migu.cn/v3/api/music/audioPlayer/getLyric?copyrightId=%s";

    public void fillLyric(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        String songBody = HttpRequest.get(String.format(LYRIC_MG_API, id))
                .executeAsStr();
        JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
        String mrcUrl = data.getString("mrcUrl");
        // mrc 优先
        if (StringUtil.notEmpty(mrcUrl)) {
            String mrcStr = HttpRequest.get(mrcUrl).executeAsStr();
            mrcStr = MrcDecoder.getInstance().decode(mrcStr);
            String[] lsp = mrcStr.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String l : lsp) {
                if (RegexUtil.contains("\\[\\d+,\\d+\\]", l)) {
                    // 行起始时间
                    String lineStartStr = RegexUtil.getGroup1("\\[(\\d+),\\d+\\]", l);
                    int lineStart = Integer.parseInt(lineStartStr);
                    String lyricTime = DurationUtil.formatToLyricTime((double) lineStart / 1000);
                    sb.append(lyricTime);

                    List<String> wordStartList = RegexUtil.findAllGroup1("\\((\\d+),\\d+\\)", l);
                    List<String> wordDurationList = RegexUtil.findAllGroup1("\\(\\d+,(\\d+)\\)", l);
                    // mrc 逐字时间轴在后
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
            musicInfo.setLyric(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }
        // lrc
        else {
            String lyricUrl = data.getString("lrcUrl");
            String lyricStr = HttpRequest.get(lyricUrl).executeAsStr();
            musicInfo.setLyric(lyricStr);
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // lrc
//            String lyricBody = HttpRequest.get(String.format(LYRIC_MG_API, id))
//                    .header(Header.REFERER, "https://music.migu.cn/v3/music/player/audio?from=migu")
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(lyricBody);
//            String lyricStr = data.getString("lyric").replace("\r\n", "\n");
//            musicInfo.setLyric(lyricStr);
//            musicInfo.setTrans("");
//            musicInfo.setRoma("");
    }
}
