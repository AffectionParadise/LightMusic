package net.doge.sdk.entity.music.info.lyrichero.mg;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import net.doge.model.entity.NetMusicInfo;
import net.doge.sdk.entity.music.info.lyrichero.mg.decoder.MrcDecoder;
import net.doge.util.collection.ArrayUtil;
import net.doge.util.common.DurationUtil;
import net.doge.util.common.RegexUtil;
import net.doge.util.common.StringUtil;

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

    public void fillLrc(NetMusicInfo musicInfo) {
        String id = musicInfo.getId();

        String songBody = HttpRequest.get(String.format(LYRIC_MG_API, id))
                .executeAsync()
                .body();
        JSONObject data = JSONObject.parseObject(songBody).getJSONArray("resource").getJSONObject(0);
        String mrcUrl = data.getString("mrcUrl");
        // mrc 优先
        if (StringUtil.notEmpty(mrcUrl)) {
            String mrcStr = HttpRequest.get(mrcUrl).executeAsync().body();
            mrcStr = MrcDecoder.getInstance().decode(mrcStr);
            String[] lsp = mrcStr.split("\n");
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
                    // mrc 逐字时间轴在后
                    String[] sp = ArrayUtil.removeLastEmpty(l.replaceFirst("\\[\\d+,\\d+\\]", "").split("\\(\\d+,\\d+\\)", -1));
                    for (int i = 0, s = wordStartList.size(); i < s; i++) {
                        String wordStart = wordStartList.get(i);
                        int wsi = Integer.parseInt(wordStart);
                        sb.append("<")
                                .append(wsi - lineStart)
                                .append(",")
                                .append(wordDurationList.get(i))
                                .append(">")
                                .append(sp[i]);
                    }
                } else sb.append(l);
                sb.append("\n");
            }
            musicInfo.setLrc(sb.toString());
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }
        // lrc
        else {
            String lrcUrl = data.getString("lrcUrl");
            String lrcStr = HttpRequest.get(lrcUrl).executeAsync().body();
            musicInfo.setLrc(lrcStr);
            musicInfo.setTrans("");
            musicInfo.setRoma("");
        }

        // lrc
//            String lrcBody = HttpRequest.get(String.format(LYRIC_MG_API, id))
//                    .header(Header.REFERER, "https://music.migu.cn/v3/music/player/audio?from=migu")
//                    .executeAsync()
//                    .body();
//            JSONObject data = JSONObject.parseObject(lrcBody);
//            String lrcStr = data.getString("lyric").replace("\r\n", "\n");
//            musicInfo.setLrc(lrcStr);
//            musicInfo.setTrans("");
//            musicInfo.setRoma("");
    }
}
