package net.doge.sdk.service.music.info.impl.musicinfo.lyric.qs;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.core.lyric.LyricPattern;
import net.doge.entity.service.NetMusicInfo;
import net.doge.util.core.RegexUtil;
import net.doge.util.core.StringUtil;
import net.doge.util.core.array.ArrayUtil;
import net.doge.util.core.http.HttpRequest;
import net.doge.util.media.DurationUtil;

import java.util.List;

public class QsLyricReq {
    private static QsLyricReq instance;

    private QsLyricReq() {
    }

    public static QsLyricReq getInstance() {
        if (instance == null) instance = new QsLyricReq();
        return instance;
    }

    // 歌词 API (汽水)
    private final String LYRIC_QS_API = "https://api.qishui.com/luna/h5/track?track_id=%s";

    public void fillLyric(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        String lyricBody = HttpRequest.get(String.format(LYRIC_QS_API, id))
                .executeAsStr();
        JSONObject lyricJson = JSONObject.parseObject(lyricBody).getJSONObject("lyric");
        // 逐字歌词
        String lyric = lyricJson.getString("content");
        if (StringUtil.isEmpty(lyric)) musicInfo.setLyric("");
        else {
            String[] lsp = lyric.split("\n");
            StringBuilder sb = new StringBuilder();
            for (String l : lsp) {
                // 行起始时间
                String lineStartStr = RegexUtil.getGroup1("\\[(\\d+),\\d+\\]", l);
                if (StringUtil.notEmpty(lineStartStr)) {
                    int lineStart = Integer.parseInt(lineStartStr);
                    String lyricTime = DurationUtil.formatToLyricTime((double) lineStart / 1000);
                    sb.append(lyricTime);

                    List<String> wordStartList = RegexUtil.findAllGroup1("<(\\d+),\\d+,\\d+>", l);
                    List<String> wordDurationList = RegexUtil.findAllGroup1("<\\d+,(\\d+),\\d+>", l);
                    String[] sp = ArrayUtil.removeFirstEmpty(l.replaceFirst("\\[\\d+,\\d+\\]", "").split("<\\d+,\\d+,\\d+>", -1));
                    for (int i = 0, s = wordStartList.size(); i < s; i++) {
                        String wordStart = wordStartList.get(i);
                        int wsi = Integer.parseInt(wordStart);
                        sb.append(String.format(LyricPattern.PAIR_FMT, wsi, wordDurationList.get(i)));
                        sb.append(sp[i]);
                    }
                } else sb.append(l);
                sb.append("\n");
            }
            musicInfo.setLyric(sb.toString());
        }
        musicInfo.setTrans("");
        musicInfo.setRoma("");
    }
}
